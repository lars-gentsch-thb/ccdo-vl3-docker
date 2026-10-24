# ccdo-vl3-docker

Demo-Repo zur Vorlesung 3 von **Cloud Computing: Entwicklung und Betrieb (CCDO)** an der TH Brandenburg (Thema Docker).
Grundlage ist die Spring-Boot-App aus [spring-boot-webapp-uebung](https://github.com/lars-gentsch-thb/spring-boot-webapp-uebung), hier mit Java 21, Spring Boot 4 und Actuator.

## Lokal bauen und starten

```bash
docker build -t ccdo-vl3 .
docker run -p 8080:8080 ccdo-vl3
curl localhost:8080/actuator/health     # {"status":"UP"}
```

Mit Podman genauso: `podman build -t ccdo-vl3 .` und `podman run -p 8080:8080 ccdo-vl3`.

Ohne Container: `./mvnw verify` (Tests) und `./mvnw spring-boot:run`.

## App und Datenbank mit Compose

```bash
docker compose up --build
curl localhost:8080/besuche             # zählt Aufrufe in Postgres
curl localhost:8080/actuator/health     # enthält jetzt auch "db"
docker compose down                     # mit -v auch das Volume löschen
```

`compose.yaml` startet die App mit `postgres:17` und setzt das Spring-Profil `db` sowie die Variablen `SPRING_DATASOURCE_*`.
Ohne dieses Profil ist die Datenbank-Autokonfiguration abgeschaltet (`application.properties`), deshalb startet `docker run` auch ohne Postgres.

## CI und Image in GHCR

- `.github/workflows/ci.yml`: `./mvnw verify` mit Java 21.
- `.github/workflows/docker.yml`: baut das Image, startet es zum Smoke-Test (`/actuator/health`) und pusht es nach GHCR.

Image: `ghcr.io/lars-gentsch-thb/ccdo-vl3-docker:latest` (zusätzlich mit dem Commit-SHA als Tag).

```bash
docker pull ghcr.io/lars-gentsch-thb/ccdo-vl3-docker:latest
docker run -p 8080:8080 ghcr.io/lars-gentsch-thb/ccdo-vl3-docker:latest
```
