import json
import sys
import signal
import time
import random
from math import radians, sin, cos, asin, sqrt

import pika
from pika.adapters.blocking_connection import BlockingChannel
from redis import Redis

RMQ_HOST = 'rabbit'
RMQ_PORT = 5672
RMQ_VHOST = '/'
RMQ_USER = 'dst'
RMQ_PWD = 'dst'

RMQ_EXCHANGE = 'dst.workers'

REDIS_HOST = 'redis'

region: str | None = None
channel: BlockingChannel | None = None
redis: Redis | None = None


def simulate_work():
    sleeping_time_ranges = {
        "at_linz": (1, 2),
        "at_vienna": (3, 5),
        "de_berlin": (8, 11),
    }

    global region
    lower, upper = sleeping_time_ranges[region]
    time.sleep(random.randint(lower, upper))


# Get all available drivers in the region from Redis
def fetch_drivers() -> list[tuple[str, float, float]]:
    entries = redis.hgetall(f'drivers:{region}')
    drivers = []
    for k, v in entries.items():
        driver_id, location = k.decode(), v.decode()
        latitude, longitude = location.split(' ')
        drivers.append((driver_id, float(latitude), float(longitude)))

    return drivers


# Mark the driver as unavailable by removing them from Redis
def remove_driver(driver_id: str) -> int:
    global region, redis
    return redis.hdel(f'drivers:{region}', str(driver_id))


def publish_result(request_id: str, driver_id: str, processing_time: int):
    global region, channel
    result_message = json.dumps({
        'requestId': request_id,
        'driverId': driver_id,
        'processingTime': processing_time,
    })
    channel.basic_publish(RMQ_EXCHANGE, f'requests.{region}', result_message.encode())


def haversine(start: tuple[float, float], end: tuple[float, float]) -> float:
    lat1, lon1 = radians(start[0]), radians(start[1])
    lat2, lon2 = radians(end[0]), radians(end[1])

    dlon = lon2 - lon1
    dlat = lat2 - lat1

    a = sin(dlat / 2) ** 2 + cos(lat1) * cos(lat2) * sin(dlon / 2) ** 2
    c = 2 * asin(sqrt(a))

    earth_radius = 6370.0
    return earth_radius * c


# Find the closest driver from the list by taking the one with the
# smallest haversine distance to the location
def match_driver(
        drivers: list[tuple[str, float, float]],
        location: tuple[float, float]
) -> str:
    driver = min(drivers, key=lambda d: haversine((d[1], d[2]), location))
    return driver[0]


# Try to get an available closest driver and return its id. Gets all
# available drivers in the region, selects the nearest one and tries
# to remove it when it is still available. If no driver is available
# an empty string is returned, if the driver was not available
# anymore None is returned.
def try_acquire_driver(location: tuple[float, float]) -> str | None:
    drivers = fetch_drivers()
    if not drivers:
        print('No drivers available for matching')
        return ''

    print(f'Got {len(drivers)} available drivers for matching')
    matched_driver = match_driver(drivers, location)

    simulate_work()

    print(f'Matched driver with id {matched_driver}')
    num_removed = remove_driver(matched_driver)
    if num_removed == 0:
        return None

    return matched_driver


# Handles an incoming driver request, by trying to select the
# closest driver in the region. On success a message is published
# to RabbitMQ, otherwise the process is retried.
def handle_message(
        channel: BlockingChannel,
        method: pika.spec.Basic.Deliver,
        properties: pika.spec.BasicProperties,
        body: bytes
):
    global region
    start_time = time.time_ns()

    message = json.loads(body)
    request_id = message['id']
    location = (
        float(message['pickup']['latitude']),
        float(message['pickup']['longitude'])
    )

    print(f'Start matching for {request_id}')

    while True:
        driver_id = try_acquire_driver(location)
        if driver_id is not None:
            if driver_id == '':
                publish_result(request_id, '', 0)
            else:
                end_time = time.time_ns()
                duration_ms = int((end_time - start_time) / 1e6)
                publish_result(request_id, driver_id, duration_ms)
            print('Published result')
            return

        print('Driver no longer available, retrying...')


# Connect to RabbitMQ and opens the queue on the channel. Does not
# assert the channel, and hence throws if it does not exist.
def open_rmq_channel(queue_name: str):
    connection = pika.BlockingConnection(pika.ConnectionParameters(
        RMQ_HOST,
        RMQ_PORT,
        RMQ_VHOST,
        pika.PlainCredentials(RMQ_USER, RMQ_PWD)
    ))
    channel = connection.channel()
    channel.queue_declare(queue_name, durable=False)

    return channel


def signal_term_handler(_signo, _stack_frame):
    print("Terminated with SIGTERM")
    sys.exit(0)


def main():
    if len(sys.argv) != 2:
        print("Usage: worker.py <region>")
        exit(1)

    global region
    region = sys.argv[1]

    signal.signal(signal.SIGTERM, signal_term_handler)

    print('Connecting to Redis')

    global redis
    redis = Redis(host=REDIS_HOST)

    print('Connecting to RabbitMQ')

    global channel
    queue_name = "dst." + region
    channel = open_rmq_channel(queue_name)

    channel.basic_consume(
        queue=queue_name,
        on_message_callback=handle_message,
        auto_ack=True
    )

    print("Waiting for messages")
    channel.start_consuming()


if __name__ == "__main__":
    main()
