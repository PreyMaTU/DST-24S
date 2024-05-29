#!/usr/bin/env bash
redis-cli hmset drivers:at_vienna "1234" "48.19819 16.37127" "5678" "50.19819 20.37127"

# Make sure that all required resources are created and correctly configured
# To test your implementation:
# Publish the following message via the RabbitMQ interface in the queue 'dst.at_vienna'
# {"id":"request1","region":"AT_VIENNA","pickup":{"longitude": 16.371334 ,"latitude": 48.198122}}
# To see if your implementation did remove the driver execute the following command:
# redis-cli hgetall drivers:at_vienna
# the output should not include driver with id 1234

