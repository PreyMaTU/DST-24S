## Building

Adds the tag `dst/ass3-worker` to the image.

```bash
docker build -t dst/ass3-worker .
```

## Running

Runs the `dst/ass3-worker` container within the `dst` network and sends
the remaining trailing arguments to the python script. `--rm` removes
the container when we are done.

```bash
docker run --rm --network=dst dst/ass3-worker at_vienna
```
