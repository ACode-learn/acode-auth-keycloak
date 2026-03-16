package gr.alexc.keycloak.configuration.acode;

import gr.alexc.keycloak.configuration.KeycloakConfigurationProperties;
import lombok.AllArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.admin.client.Keycloak;

/**
 * Configuration for ACode realm.
 */
@JBossLog
@AllArgsConstructor
public class AcodeConfiguration {
    static final String REALM_NAME = "acode";
    static final String REALM_DISPLAY_NAME = "ACode Learn Realm";

    private final Keycloak keycloak;
    private final KeycloakConfigurationProperties configuration;

    /**
     * Configures the acode realm.
     */
    public void configure() {
        log.info("-----------------------------------------------");
        log.infof("Starting configuration of realm '%s'.", REALM_NAME);
        log.info("-----------------------------------------------");

        new RealmConfiguration(keycloak.realms()).configure(REALM_NAME, REALM_DISPLAY_NAME);
        new UiClientConfiguration(keycloak.realm(REALM_NAME).clients()).configure();

        if (configuration.getBoolean("KEYCLOAK_CREATE_TEST_USERS")) {
            new TestUsersConfiguration(keycloak.realm(REALM_NAME), configuration).configure();
        }

        log.info("-----------------------------------------------");
        log.infof("Finished configuration of realm '%s'.%n", REALM_NAME);
        log.info("-----------------------------------------------");
    }

}
