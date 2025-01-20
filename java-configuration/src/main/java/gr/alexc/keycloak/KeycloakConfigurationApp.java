package gr.alexc.keycloak;

import gr.alexc.keycloak.configuration.KeycloakClientBuilder;
import gr.alexc.keycloak.configuration.KeycloakConfiguration;
import gr.alexc.keycloak.configuration.KeycloakConfigurationProperties;
import org.jboss.logging.Logger;
import org.keycloak.admin.client.Keycloak;

/**
 * Hello world!
 */
public class KeycloakConfigurationApp {
    public static void main(String[] args) {
        try {
            KeycloakConfigurationProperties configuration = KeycloakConfigurationProperties.fromEnv();

            KeycloakClientBuilder keycloakClientBuilder = KeycloakClientBuilder.create(
                    configuration.get("KEYCLOAK_SERVER"),
                    configuration.get("KEYCLOAK_USER"),
                    configuration.get("KEYCLOAK_PASSWORD"),
                    configuration.get("KEYCLOAK_REALM"));
            Keycloak keycloak = keycloakClientBuilder.getClient();

            KeycloakConfiguration keycloakConfig = new KeycloakConfiguration(keycloak);
            keycloakConfig.configure();
        } catch (Exception all) {
            Logger.getLogger(KeycloakConfigurationApp.class).error("Exception occurred.", all);
            throw all;
        }
    }
}
