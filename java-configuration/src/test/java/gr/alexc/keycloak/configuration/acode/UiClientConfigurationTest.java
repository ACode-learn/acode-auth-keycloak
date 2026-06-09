package gr.alexc.keycloak.configuration.acode;

import gr.alexc.keycloak.configuration.KeycloakConfigurationProperties;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.keycloak.representations.idm.ClientRepresentation;

import static org.assertj.core.api.Assertions.assertThat;

class UiClientConfigurationTest {

    private UiClientConfiguration newConfiguration() {
        KeycloakConfigurationProperties configuration = KeycloakConfigurationProperties.fromEnv();
        return new UiClientConfiguration(null, configuration);
    }

    @Test
    void usesDefaultsWhenNoEnvProvided() {
        ClientRepresentation client = newConfiguration().getClientRepresentation();

        assertThat(client.getClientId()).isEqualTo("acode-learn-ui");
        assertThat(client.getBaseUrl()).isEqualTo("http://localhost:4200");
        assertThat(client.getRedirectUris())
                .containsExactly("http://localhost:4200/auth", "http://localhost:4200");
        assertThat(client.getWebOrigins()).containsExactly("http://localhost:4200");
        assertThat(client.getAttributes().get("post.logout.redirect.uris"))
                .isEqualTo("http://localhost:4200/logout");
    }

    @Test
    @SetSystemProperty(key = "KEYCLOAK_UI_CLIENT_BASE_URL", value = "http://localhost:5173")
    @SetSystemProperty(key = "KEYCLOAK_UI_CLIENT_REDIRECT_URIS", value = "http://localhost:5173/auth,http://localhost:5173")
    @SetSystemProperty(key = "KEYCLOAK_UI_CLIENT_WEB_ORIGINS", value = "http://localhost:5173")
    @SetSystemProperty(key = "KEYCLOAK_UI_CLIENT_POST_LOGOUT_REDIRECT_URIS", value = "http://localhost:5173/logout")
    void configuredValuesOverrideDefaults() {
        ClientRepresentation client = newConfiguration().getClientRepresentation();

        assertThat(client.getBaseUrl()).isEqualTo("http://localhost:5173");
        assertThat(client.getRootUrl()).isEqualTo("http://localhost:5173");
        assertThat(client.getAdminUrl()).isEqualTo("http://localhost:5173");
        assertThat(client.getRedirectUris())
                .containsExactly("http://localhost:5173/auth", "http://localhost:5173");
        assertThat(client.getWebOrigins()).containsExactly("http://localhost:5173");
        assertThat(client.getAttributes().get("post.logout.redirect.uris"))
                .isEqualTo("http://localhost:5173/logout");
    }

    @Test
    void securityBehaviorRemainsStable() {
        ClientRepresentation client = newConfiguration().getClientRepresentation();

        assertThat(client.isPublicClient()).isTrue();
        assertThat(client.isStandardFlowEnabled()).isTrue();
        assertThat(client.isImplicitFlowEnabled()).isFalse();
        assertThat(client.isDirectAccessGrantsEnabled()).isFalse();
        assertThat(client.isServiceAccountsEnabled()).isFalse();
        assertThat(client.getProtocol()).isEqualTo("openid-connect");
        assertThat(client.getAttributes().get("pkce.code.challenge.method")).isEqualTo("S256");
    }
}
