package gr.alexc.keycloak.configuration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class with configuration properties for configuring Keycloak.
 * Configuration properties can be set using a combination of environment variables and Java system properties
 * (the latter has higher precedence) using {@link #fromEnv()}.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class KeycloakConfigurationProperties {

    private final Map<String, String> configuration;

    /**
     * Gets the value for given configuration property
     *
     * @param name Name of the configuration property to get a value for.
     * @return Value for the configuration property or null if the property doesn't exist.
     */
    public String get(String name) {
        return configuration.get(name);
    }

    /**
     * Gets the boolean value for given configuration property.
     *
     * @param name Name of the configuration property to get a value for.
     * @return Boolean value for the configuration property or false if the property doesn't exist or is not "true".
     */
    public boolean getBoolean(String name) {
        return Boolean.parseBoolean(get(name));
    }

    /**
     * Gets the value for given configuration property or a default value when the property is missing or blank.
     *
     * @param name         Name of the configuration property to get a value for.
     * @param defaultValue Default value to return when the property is missing or blank.
     * @return Configured value when present and not blank, otherwise the default value.
     */
    public String getOrDefault(String name, String defaultValue) {
        String value = get(name);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Gets the boolean value for given configuration property or a default value when the property is missing or blank.
     *
     * @param name         Name of the configuration property to get a value for.
     * @param defaultValue Default value to return when the property is missing or blank.
     * @return Parsed boolean value when present and not blank, otherwise the default value.
     */
    public boolean getBooleanOrDefault(String name, boolean defaultValue) {
        String value = get(name);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    /**
     * Gets a list of values for given configuration property from a comma-separated value.
     * Each item is trimmed and blank items are removed.
     *
     * @param name          Name of the configuration property to get values for.
     * @param defaultValues Default values to return when the property is missing, blank, or only contains blanks.
     * @return List of configured values, otherwise the default values.
     */
    public List<String> getListOrDefault(String name, List<String> defaultValues) {
        String value = get(name);
        if (value == null || value.isBlank()) {
            return defaultValues;
        }
        List<String> values = Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .collect(Collectors.toList());
        if (values.isEmpty()) {
            return defaultValues;
        }
        return values;
    }

    /**
     * Creates a new configuration based on System environment variables and Java system properties.
     * Java system properties have higher precedence than environment variables.
     *
     * @return New {@link KeycloakConfigurationProperties} instance.
     */
    public static KeycloakConfigurationProperties fromEnv() {
        Map<String, String> systemProperties = System.getProperties().entrySet().stream()
                .collect(Collectors.toMap(e -> convertKey((String) e.getKey()), e -> (String) e.getValue()));
        Map<String, String> configMap = new HashMap<>(System.getenv());
        configMap.putAll(systemProperties);

        return new KeycloakConfigurationProperties(configMap);
    }

    private static String convertKey(String key) {
        return key.replace('.', '_').toUpperCase(Locale.ROOT);
    }
}
