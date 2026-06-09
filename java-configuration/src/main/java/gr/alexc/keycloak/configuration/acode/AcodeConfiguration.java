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
    static final String RECREATE_REALM_PROPERTY = "KEYCLOAK_RECREATE_REALM";

    private final Keycloak keycloak;
    private final KeycloakConfigurationProperties configuration;

    /**
     * Configures the acode realm.
     * <p>
     * When the realm already exists, configuration is skipped unless the
     * {@value #RECREATE_REALM_PROPERTY} property is set to {@code true}, in which case the existing
     * realm is deleted and recreated from scratch.
     */
    public void configure() {
        log.info("-----------------------------------------------");
        log.infof("Starting configuration of realm '%s'.", REALM_NAME);
        log.info("-----------------------------------------------");

        RealmConfiguration realmConfiguration = new RealmConfiguration(keycloak.realms());
        boolean realmExists = realmConfiguration.realmExists(REALM_NAME);
        boolean recreateRealm = configuration.getBooleanOrDefault(RECREATE_REALM_PROPERTY, false);

        if (realmExists && !recreateRealm) {
            log.infof("Realm '%s' already exists and '%s' is false; skipping configuration.",
                    REALM_NAME, RECREATE_REALM_PROPERTY);
            return;
        }

        if (realmExists) {
            log.infof("Realm '%s' already exists and '%s' is true; deleting it before recreating.",
                    REALM_NAME, RECREATE_REALM_PROPERTY);
            realmConfiguration.deleteRealm(REALM_NAME);
        }

        realmConfiguration.configure(REALM_NAME, REALM_DISPLAY_NAME);
        new UiClientConfiguration(keycloak.realm(REALM_NAME).clients(), configuration).configure();

        if (configuration.getBoolean("KEYCLOAK_CREATE_TEST_USERS")) {
            new TestUsersConfiguration(keycloak.realm(REALM_NAME), configuration).configure();
        }

        log.info("-----------------------------------------------");
        log.infof("Finished configuration of realm '%s'.%n", REALM_NAME);
        log.info("-----------------------------------------------");
    }

}
