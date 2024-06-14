FROM python:3-slim

WORKDIR /app

RUN pip install pika redis

COPY worker.py .

# Turns off buffering for stdout, so container logging is visible
ENV PYTHONUNBUFFERED=1

ENTRYPOINT [ "python", "./worker.py" ]
