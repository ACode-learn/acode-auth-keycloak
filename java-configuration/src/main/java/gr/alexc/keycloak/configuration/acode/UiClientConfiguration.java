package gr.alexc.keycloak.configuration.acode;

import lombok.AllArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.representations.idm.ClientRepresentation;

import java.util.List;
import java.util.Map;

@JBossLog
@AllArgsConstructor
public class UiClientConfiguration {

    private final ClientsResource clientsResource;

    public void configure() {
        String clientId = "acode-learn-ui";
        List<ClientRepresentation> clients = clientsResource.findByClientId(clientId);
        if (clients.isEmpty()) {
            log.infof("Creating client '%s'", clientId);
            createClient();
        } else {
            log.infof("Updating client '%s'", clientId);
            updateClient(clients.get(0).getId());
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

    private ClientRepresentation getClientRepresentation() {
        ClientRepresentation client = new ClientRepresentation();
        client.setClientId("acode-learn-ui");
        client.setName("Acode Learn UI");
        client.setDescription("Client for the UI application");
        client.setRootUrl("");
        client.setAdminUrl("");
        client.setBaseUrl("");
        client.setSurrogateAuthRequired(false);
        client.setEnabled(true);
        client.setAlwaysDisplayInConsole(false);
        client.setClientAuthenticatorType("client-secret");
        client.setRedirectUris(List.of("http://localhost:4200/auth"));
        client.setWebOrigins(List.of("http://localhost:4200"));
        client.setNotBefore(0);
        client.setBearerOnly(false);
        client.setConsentRequired(false);
        client.setStandardFlowEnabled(true);
        client.setImplicitFlowEnabled(false);
        client.setDirectAccessGrantsEnabled(false);
        client.setServiceAccountsEnabled(false);
        client.setPublicClient(true);
        client.setFrontchannelLogout(true);
        client.setProtocol("openid-connect");
        client.setAttributes(Map.of(
                "realm_client", "false",
                "oidc.ciba.grant.enabled", "false",
                "backchannel.logout.session.required", "true",
                "standard.token.exchange.enabled", "false",
                "frontchannel.logout.session.required", "true",
                "post.logout.redirect.uris", "http://localhost:4200/logout",
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
