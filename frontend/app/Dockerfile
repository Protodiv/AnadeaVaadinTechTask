# Allows you to run this app easily as a docker container.
# Uses Docker Multi-stage builds: https://docs.docker.com/build/building/multi-stage/

# The "Build" stage.
FROM eclipse-temurin:21 AS builder
WORKDIR /app

COPY . .

RUN --mount=type=cache,target=/root/.gradle,sharing=locked \
    --mount=type=cache,target=/root/.vaadin,sharing=locked \
    ./gradlew :frontend:app:assemble -Pvaadin.productionMode --no-daemon --no-watch-fs

WORKDIR /app/frontend/app/build/distributions/
RUN tar xvf app.tar

# The "Run" stage.
FROM eclipse-temurin:21
COPY --from=builder /app/frontend/app/build/distributions/app /app/
WORKDIR /app/bin
EXPOSE 8080
ENTRYPOINT ["./app"]
