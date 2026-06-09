# ACode Auth Keycloak

## Run locally

Copy the sample environment file if needed:

```bash
cp .env.sample .env
```

Start the whole stack:

```bash
docker compose up --build
```

The Java Keycloak configuration app (`keycloak-java-configuration`) is built
automatically from source as part of the Docker Compose build (see
`java-configuration/Dockerfile`), so **no manual Maven `package` step is
required** anymore.

The configuration service starts only after Keycloak reports healthy and uses
the bundled `java-configuration.jar` from its own image. The `test-users.json`
file is mounted into the container so it is available when
`KEYCLOAK_CREATE_TEST_USERS` / `KEYCLOAK_TEST_USERS_FILE` are configured.

## Configuration

All configuration is provided through environment variables. See `.env.sample`
for a complete, ready-to-copy list of defaults.

### Database

The stack uses a bundled PostgreSQL 18 service. By default Keycloak connects to
it using the `postgres` Docker Compose service name:

| Variable | Default | Description |
| --- | --- | --- |
| `KC_DB` | `postgres` | Database vendor. |
| `KC_DB_SCHEMA` | `public` | Database schema. |
| `KC_DB_URL_HOST` | `postgres` | Database host (the internal Compose service). |
| `KC_DB_URL_PORT` | `5432` | Database port. |
| `KC_DB_URL_DATABASE` | `keycloak` | Database name. |
| `KC_DB_USERNAME` | `postgres` | Database user. |
| `KC_DB_PASSWORD` | `postgres` | Database password. |

### Realm provisioning

The `acode` realm is provisioned idempotently:

| Variable | Default | Description |
| --- | --- | --- |
| `KEYCLOAK_RECREATE_REALM` | `false` | When `false`, the realm is configured only if it does not already exist; existing realms are left untouched. When `true`, the realm is deleted and recreated from scratch on every run. |

### UI client

The `acode-learn-ui` OpenID Connect client is fully configurable through
environment variables. When a variable is omitted, the listed default is used:

| Variable | Default | Description |
| --- | --- | --- |
| `KEYCLOAK_UI_CLIENT_ID` | `acode-learn-ui` | Client ID. |
| `KEYCLOAK_UI_CLIENT_NAME` | `Acode Learn UI` | Display name. |
| `KEYCLOAK_UI_CLIENT_DESCRIPTION` | `Client for the UI application` | Client description. |
| `KEYCLOAK_UI_CLIENT_BASE_URL` | `http://localhost:4200` | Base URL (also used as the default for root/admin URLs). |
| `KEYCLOAK_UI_CLIENT_ROOT_URL` | base URL | Root URL. |
| `KEYCLOAK_UI_CLIENT_ADMIN_URL` | base URL | Admin URL. |
| `KEYCLOAK_UI_CLIENT_REDIRECT_URIS` | `<base>/auth`, `<base>` | Comma-separated list of valid redirect URIs. |
| `KEYCLOAK_UI_CLIENT_WEB_ORIGINS` | base URL | Comma-separated list of allowed web origins. |
| `KEYCLOAK_UI_CLIENT_POST_LOGOUT_REDIRECT_URIS` | `<base>/logout` | Comma-separated list of post-logout redirect URIs. |
| `KEYCLOAK_UI_CLIENT_ENABLED` | `true` | Whether the client is enabled. |
| `KEYCLOAK_UI_CLIENT_FRONTCHANNEL_LOGOUT` | `true` | Whether front-channel logout is enabled. |

List-type variables (redirect URIs, web origins, post-logout redirect URIs) are
comma-separated; surrounding whitespace and blank entries are ignored.
