package gr.alexc.keycloak.configuration;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperty;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakConfigurationPropertiesTest {

    @Test
    @SetSystemProperty(key = "TEST_VALUE", value = "configured")
    void getOrDefaultReturnsConfiguredValue() {
        KeycloakConfigurationProperties configuration = KeycloakConfigurationProperties.fromEnv();

        assertThat(configuration.getOrDefault("TEST_VALUE", "fallback")).isEqualTo("configured");
    }

    @Test
    void getOrDefaultReturnsDefaultForMissingValue() {
        KeycloakConfigurationProperties configuration = KeycloakConfigurationProperties.fromEnv();

        assertThat(configuration.getOrDefault("MISSING_VALUE", "fallback")).isEqualTo("fallback");
    }

    @Test
    @SetSystemProperty(key = "BLANK_VALUE", value = "   ")
    void getOrDefaultReturnsDefaultForBlankValue() {
        KeycloakConfigurationProperties configuration = KeycloakConfigurationProperties.fromEnv();

        assertThat(configuration.getOrDefault("BLANK_VALUE", "fallback")).isEqualTo("fallback");
    }

    @Test
    void getBooleanOrDefaultReturnsDefaultWhenMissing() {
        KeycloakConfigurationProperties configuration = KeycloakConfigurationProperties.fromEnv();

        assertThat(configuration.getBooleanOrDefault("MISSING_BOOL", true)).isTrue();
        assertThat(configuration.getBooleanOrDefault("MISSING_BOOL", false)).isFalse();
    }

    @Test
    @SetSystemProperty(key = "BOOL_TRUE", value = "true")
    void getBooleanOrDefaultParsesTrue() {
        KeycloakConfigurationProperties configuration = KeycloakConfigurationProperties.fromEnv();

        assertThat(configuration.getBooleanOrDefault("BOOL_TRUE", false)).isTrue();
    }

    @Test
    @SetSystemProperty(key = "BOOL_FALSE", value = "false")
    void getBooleanOrDefaultParsesFalse() {
        KeycloakConfigurationProperties configuration = KeycloakConfigurationProperties.fromEnv();

        assertThat(configuration.getBooleanOrDefault("BOOL_FALSE", true)).isFalse();
    }

    @Test
    @SetSystemProperty(key = "LIST_VALUE", value = "http://localhost:4200/auth,http://localhost:4200")
    void getListOrDefaultParsesCommaSeparatedValues() {
        KeycloakConfigurationProperties configuration = KeycloakConfigurationProperties.fromEnv();

        assertThat(configuration.getListOrDefault("LIST_VALUE", List.of("default")))
                .containsExactly("http://localhost:4200/auth", "http://localhost:4200");
    }

    @Test
    @SetSystemProperty(key = "LIST_WHITESPACE", value = " a , b , c ")
    void getListOrDefaultTrimsWhitespace() {
        KeycloakConfigurationProperties configuration = KeycloakConfigurationProperties.fromEnv();

        assertThat(configuration.getListOrDefault("LIST_WHITESPACE", List.of("default")))
                .containsExactly("a", "b", "c");
    }

    @Test
    @SetSystemProperty(key = "LIST_BLANKS", value = "a,,  ,b")
    void getListOrDefaultRemovesBlankEntries() {
        KeycloakConfigurationProperties configuration = KeycloakConfigurationProperties.fromEnv();

        assertThat(configuration.getListOrDefault("LIST_BLANKS", List.of("default")))
                .containsExactly("a", "b");
    }

    @Test
    void getListOrDefaultReturnsDefaultWhenMissing() {
        KeycloakConfigurationProperties configuration = KeycloakConfigurationProperties.fromEnv();

        assertThat(configuration.getListOrDefault("MISSING_LIST", List.of("default")))
                .containsExactly("default");
    }

    @Test
    @SetSystemProperty(key = "LIST_ONLY_BLANKS", value = " , ,")
    void getListOrDefaultReturnsDefaultWhenOnlyBlanks() {
        KeycloakConfigurationProperties configuration = KeycloakConfigurationProperties.fromEnv();

        assertThat(configuration.getListOrDefault("LIST_ONLY_BLANKS", List.of("default")))
                .containsExactly("default");
    }
}
