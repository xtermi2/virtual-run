# virtual-run-backend project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

## 

## Requirement

-   Java 11
-   Docker

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

## Packaging the application

**Build a container:**
```bash
./mvnw clean package \
    -Dquarkus.container-image.build=true \
    -Dquarkus.container-image.group=afrika-run \
    -Dquarkus.container-image.registry=eu.gcr.io
```

**Build a native container:**

This will build a native image use GraalVM. 
GraalVM must not be installed, it's build inside a GraalVM aware Docker image.
```bash
./mvnw clean verify -Pnative \
    -Dquarkus.native.container-build=true \
    -Dquarkus.container-image.build=true \
    -Dquarkus.container-image.group=afrika-run \
    -Dquarkus.container-image.registry=eu.gcr.io
```

## push the image to the docker registry

**build and push a container:**
```bash
./mvnw clean package \
    -Dquarkus.container-image.build=true \
    -Dquarkus.container-image.push=true \
    -Dquarkus.container-image.group=afrika-run \
    -Dquarkus.container-image.registry=eu.gcr.io \
    -Dquarkus.container-image.username=... \
    -Dquarkus.container-image.password=... 
```

**build and push a native container:**
```bash
./mvnw clean verify -Pnative \
    -Dquarkus.native.container-build=true \
    -Dquarkus.container-image.build=true \
    -Dquarkus.container-image.push=true \
    -Dquarkus.container-image.group=afrika-run \
    -Dquarkus.container-image.registry=eu.gcr.io \
    -Dquarkus.container-image.username=... \
    -Dquarkus.container-image.password=...
```

### References

-   Unofficial Google Cloud Run FAQ: https://github.com/ahmetb/cloud-run-faq
-   QUARKUS - SIMPLIFIED MONGODB WITH PANACHE: https://quarkus.io/guides/mongodb-panache
-   QUARKUS - USING SECURITY WITH .PROPERTIES FILE: https://quarkus.io/guides/security-properties
-   QUARKUS - CONFIGURING YOUR APPLICATION: https://quarkus.io/guides/config
-   QUARKUS - USING KOTLIN: https://quarkus.io/guides/kotlin
-   QUARKUS - VALIDATION WITH HIBERNATE VALIDATOR: https://quarkus.io/guides/validation