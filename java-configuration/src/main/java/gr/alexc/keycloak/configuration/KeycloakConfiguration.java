package gr.alexc.keycloak.configuration;

import gr.alexc.keycloak.configuration.acode.AcodeConfiguration;
import lombok.AllArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.admin.client.Keycloak;

/**
 * Class to configure Keycloak.
 */
@JBossLog
@AllArgsConstructor
public class KeycloakConfiguration {
    private final Keycloak keycloak;
    private final KeycloakConfigurationProperties configuration;

    /**
     * Starts configuration of Keycloak.
     */
    public void configure() {
        log.info("-----------------------------------------------");
        log.info("Starting Java configuration");
        log.info("-----------------------------------------------");

        new AcodeConfiguration(keycloak, configuration).configure();

        log.info("-----------------------------------------------");
        log.infof("Finished Java configuration without errors.");
        log.info("-----------------------------------------------");
    }
}
