# Pancord

## Description
HLS video streaming application with Minio object storage, Kafka, Debezium and FFmpeg.

## Tentative technologies and frameworks

- Java 21
- Spring boot 3.3.6-SNAPSHOT
- Docker
- Keycloak
- Kafka
- Minio
- Swagger-ui, Debezium, FFmpeg.

## Architecture
TO-DO architecture image will be here

## Get Started

1. Get the latest source code
2. In `pancord` directory copy and replace file name `env.example` to `.env`, change the value of key to your configuration 
3. Open terminal of your choice, go to `pancord` directory, run `docker compose up -d`, wait for all the containers up and running
4. `cd ../stream-service/src/main/resources` copy file `application-example.yml` to `application-[profile].yml` and update with your config, with `[profile]` is your `profiles.active: [profile]` in file `bootstrap.yml`
5. Config your IDE with java 21 and language level java 21 (preview)
6. Hit the Run/Debug waiting for spring boot application start
7. Go to [pgadmin](http://localhost:5050/) with your email config then add a servers to view DB, or you can choose any other tool
8. Open [Swagger-UI](http://localhost:8081/pancord/swagger-ui/index.html#) to view list of api

## Contributing
- Give us a star
- Reporting a bug
- Participate discussions
- Propose new features
- Submit pull requests. If you are new to GitHub, consider to [learn how to contribute to a project through forking](https://docs.github.com/en/get-started/quickstart/contributing-to-projects)

By contributing, you agree that your contributions will be licensed under MIT License. 
