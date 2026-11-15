# ccdo-vl3-docker

Demo-Repo zur Vorlesung 3 von **Cloud Computing: Entwicklung und Betrieb (CCDO)** an der TH Brandenburg (Thema Docker).
Grundlage ist die Spring-Boot-App aus [spring-boot-webapp-uebung](https://github.com/lars-gentsch-thb/spring-boot-webapp-uebung), hier mit Java 21, Spring Boot 4 und Actuator.

## Lokal bauen und starten

Erst das JAR mit Maven bauen (inkl. Tests), dann das Image:

```bash
./mvnw package                          # erzeugt target/demo-0.0.1-SNAPSHOT.jar
docker build -t ccdo-vl3 .
docker run -p 8080:8080 ccdo-vl3
curl localhost:8080/actuator/health     # {"status":"UP"}
```

Mit Podman genauso: `./mvnw package`, dann `podman build -t ccdo-vl3 .` und `podman run -p 8080:8080 ccdo-vl3`.

Ohne Container: `./mvnw verify` (Tests) und `./mvnw spring-boot:run`.

## Warum kein Multi-Stage-Build?

Das `Dockerfile` ist einstufig und kopiert nur das fertige JAR aus `target/` in ein JRE-Image („Build your binaries only once“):

- Gebaut wird genau einmal, mit Maven, und dabei auch getestet.
- Der Docker-Build ist schnell, weil im Container nichts kompiliert wird.
- Ins Image kommt genau das JAR, das vorher die Tests bestanden hat.

`.dockerignore` schließt alles außer dem JAR aus, der Build-Kontext bleibt dadurch klein.

Alternative: Ein Multi-Stage-Build kompiliert die App in einer ersten Stufe mit JDK im Container (z. B. `FROM eclipse-temurin:21-jdk AS build`) und kopiert das Ergebnis in ein JRE-Image; dann braucht man lokal kein Java, baut aber ein zweites Mal außerhalb der getesteten Pipeline.

## App und Datenbank mit Compose

```bash
./mvnw package                          # Compose baut das Image aus target/*.jar
docker compose up --build
curl localhost:8080/besuche             # zählt Aufrufe in Postgres
curl localhost:8080/actuator/health     # enthält jetzt auch "db"
docker compose down                     # mit -v auch das Volume löschen
```

`compose.yaml` startet die App mit `postgres:17` und setzt das Spring-Profil `db` sowie die Variablen `SPRING_DATASOURCE_*`.
Ohne dieses Profil ist die Datenbank-Autokonfiguration abgeschaltet (`application.properties`), deshalb startet `docker run` auch ohne Postgres.

## CI und Image in GHCR

- `.github/workflows/ci.yml`: `./mvnw verify` mit Java 21.
- `.github/workflows/docker.yml`: baut und testet das JAR mit `./mvnw -B verify`, baut ein Image für den Smoke-Test (`/actuator/health`) und pusht danach ein Multi-Platform-Image (amd64, arm64) nach GHCR.

Das Image wird für `linux/amd64` und `linux/arm64` gebaut (Multi-Platform) und läuft damit auf Intel-/AMD-Rechnern und Apple Silicon ohne Emulation.

Image: `ghcr.io/lars-gentsch-thb/ccdo-vl3-docker:latest` (zusätzlich mit dem Commit-SHA als Tag).

```bash
docker pull ghcr.io/lars-gentsch-thb/ccdo-vl3-docker:latest
docker run -p 8080:8080 ghcr.io/lars-gentsch-thb/ccdo-vl3-docker:latest
```

Plattformen des Images prüfen:

```bash
docker buildx imagetools inspect ghcr.io/lars-gentsch-thb/ccdo-vl3-docker:latest
```
