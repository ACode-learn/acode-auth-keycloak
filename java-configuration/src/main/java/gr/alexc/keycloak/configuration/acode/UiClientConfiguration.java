package gr.alexc.keycloak.configuration.acode;

import gr.alexc.keycloak.configuration.KeycloakConfigurationProperties;
import lombok.AllArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.representations.idm.ClientRepresentation;

import java.util.List;
import java.util.Map;

@JBossLog
@AllArgsConstructor
public class UiClientConfiguration {

    private static final String DEFAULT_CLIENT_ID = "acode-learn-ui";
    private static final String DEFAULT_CLIENT_NAME = "Acode Learn UI";
    private static final String DEFAULT_CLIENT_DESCRIPTION = "Client for the UI application";
    private static final String DEFAULT_UI_BASE_URL = "http://localhost:4200";
    private static final boolean DEFAULT_CLIENT_ENABLED = true;
    private static final boolean DEFAULT_FRONTCHANNEL_LOGOUT = true;

    private final ClientsResource clientsResource;
    private final KeycloakConfigurationProperties configuration;

    public void configure() {
        String clientId = getClientId();
        List<ClientRepresentation> clients = clientsResource.findByClientId(clientId);
        if (clients.isEmpty()) {
            log.infof("Creating client '%s'", clientId);
            createClient();
        } else {
            log.infof("Updating client '%s'", clientId);
            updateClient(clients.getFirst().getId());
        }
    }

    private void createClient() {
        ClientRepresentation client = getClientRepresentation();
        clientsResource.create(client);
    }

    private void updateClient(String id) {
        ClientRepresentation client = getClientRepresentation();
        clientsResource.get(id).update(client);
    }

    private String getClientId() {
        return configuration.getOrDefault("KEYCLOAK_UI_CLIENT_ID", DEFAULT_CLIENT_ID);
    }

    private String getBaseUrl() {
        return configuration.getOrDefault("KEYCLOAK_UI_CLIENT_BASE_URL", DEFAULT_UI_BASE_URL);
    }

    ClientRepresentation getClientRepresentation() {
        String baseUrl = getBaseUrl();
        ClientRepresentation client = new ClientRepresentation();
        client.setClientId(getClientId());
        client.setName(configuration.getOrDefault("KEYCLOAK_UI_CLIENT_NAME", DEFAULT_CLIENT_NAME));
        client.setDescription(configuration.getOrDefault("KEYCLOAK_UI_CLIENT_DESCRIPTION", DEFAULT_CLIENT_DESCRIPTION));
        client.setRootUrl(configuration.getOrDefault("KEYCLOAK_UI_CLIENT_ROOT_URL", baseUrl));
        client.setAdminUrl(configuration.getOrDefault("KEYCLOAK_UI_CLIENT_ADMIN_URL", baseUrl));
        client.setBaseUrl(baseUrl);
        client.setSurrogateAuthRequired(false);
        client.setEnabled(configuration.getBooleanOrDefault("KEYCLOAK_UI_CLIENT_ENABLED", DEFAULT_CLIENT_ENABLED));
        client.setAlwaysDisplayInConsole(false);
        client.setClientAuthenticatorType("client-secret");
        client.setRedirectUris(configuration.getListOrDefault(
                "KEYCLOAK_UI_CLIENT_REDIRECT_URIS",
                List.of(baseUrl + "/auth", baseUrl)
        ));
        client.setWebOrigins(configuration.getListOrDefault(
                "KEYCLOAK_UI_CLIENT_WEB_ORIGINS",
                List.of(baseUrl)
        ));
        client.setNotBefore(0);
        client.setBearerOnly(false);
        client.setConsentRequired(false);
        client.setStandardFlowEnabled(true);
        client.setImplicitFlowEnabled(false);
        client.setDirectAccessGrantsEnabled(false);
        client.setServiceAccountsEnabled(false);
        client.setPublicClient(true);
        client.setFrontchannelLogout(configuration.getBooleanOrDefault(
                "KEYCLOAK_UI_CLIENT_FRONTCHANNEL_LOGOUT",
                DEFAULT_FRONTCHANNEL_LOGOUT
        ));
        client.setProtocol("openid-connect");
        client.setAttributes(Map.of(
                "realm_client", "false",
                "oidc.ciba.grant.enabled", "false",
                "backchannel.logout.session.required", "true",
                "standard.token.exchange.enabled", "false",
                "frontchannel.logout.session.required", "true",
                "post.logout.redirect.uris", String.join("##", configuration.getListOrDefault(
                        "KEYCLOAK_UI_CLIENT_POST_LOGOUT_REDIRECT_URIS",
                        List.of(baseUrl + "/logout")
                )),
                "oauth2.device.authorization.grant.enabled", "false",
                "display.on.consent.screen", "false",
                "pkce.code.challenge.method", "S256",
                "backchannel.logout.revoke.offline.tokens", "false"
        ));
        client.setAuthenticationFlowBindingOverrides(Map.of());
        client.setFullScopeAllowed(true);
        client.setNodeReRegistrationTimeout(-1);
        client.setDefaultClientScopes(List.of(
                "web-origins",
                "acr",
                "profile",
                "roles",
                "basic",
                "email"
        ));
        client.setOptionalClientScopes(List.of(
                "address",
                "phone",
                "offline_access",
                "organization",
                "microprofile-jwt"
        ));
        client.setAccess(Map.of(
                "view", true,
                "configure", true,
                "manage", true
        ));
        return client;
    }
}
