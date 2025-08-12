FROM node:22.2.0 AS keycloakify_jar_builder
RUN apt-get update && \
    apt-get install -y maven;

# Install yarn globally if not already available
RUN corepack enable

COPY ./theme/ /opt/theme
WORKDIR /opt/theme
RUN yarn install --frozen-lockfile && npm run build-keycloak-theme

#FROM registry.access.redhat.com/ubi8-minimal:8.10 AS builder
#RUN microdnf update -y && \
#    microdnf install -y java-21-openjdk-headless && microdnf clean all && rm -rf /var/cache/yum/* && \
#    echo "keycloak:x:0:root" >> /etc/group && \
#    echo "keycloak:x:1000:0:keycloak user:/opt/keycloak:/sbin/nologin" >> /etc/passwd

FROM quay.io/keycloak/keycloak:26.3.2 as keycloak_builder
WORKDIR /opt/keycloak

# COPY --chown=keycloak:keycloak keycloak/target/keycloak-26.3.2  /opt/keycloak
COPY --from=keycloakify_jar_builder /opt/theme/dist_keycloak/keycloak-theme-for-kc-all-other-versions.jar /opt/keycloak/providers/

USER 1000

ENV KC_DB=postgres

RUN /opt/keycloak/bin/kc.sh build

FROM registry.access.redhat.com/ubi8-minimal:8.10

RUN microdnf update -y && \
    microdnf reinstall -y tzdata && \
    microdnf install -y java-21-openjdk-headless && \
    microdnf clean all && rm -rf /var/cache/yum/* && \
    echo "keycloak:x:0:root" >> /etc/group && \
    echo "keycloak:x:1000:0:keycloak user:/opt/keycloak:/sbin/nologin" >> /etc/passwd && \
    ln -sf /usr/share/zoneinfo/Europe/Athens /etc/localtime # set timezone

COPY --from=keycloak_builder --chown=1000:0 /opt/keycloak /opt/keycloak
RUN mkdir -p /opt/keycloak-config && chown 1000:0 /opt/keycloak-config
COPY --chown=1000:0 java-configuration/target/java-configuration.jar /opt/keycloak-config
COPY --chown=1000:0 java-configuration/target/classes/scripts/start-configuration.sh /opt/keycloak-config


USER 1000
WORKDIR /opt/keycloak-config

EXPOSE 8080
EXPOSE 8443

ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev"]
