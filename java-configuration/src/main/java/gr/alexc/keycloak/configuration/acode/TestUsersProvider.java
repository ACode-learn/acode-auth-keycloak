package gr.alexc.keycloak.configuration.acode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.alexc.keycloak.configuration.KeycloakConfigurationProperties;
import lombok.AllArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JBossLog
@AllArgsConstructor
public class TestUsersProvider {

    private final KeycloakConfigurationProperties configuration;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<TestUser> getTestUsers() {
        String usersFilePath = configuration.get("KEYCLOAK_TEST_USERS_FILE");
        if (usersFilePath != null) {
            File usersFile = new File(usersFilePath);
            if (usersFile.exists()) {
                try {
                    log.infof("Loading test users from file: %s", usersFilePath);
                    return objectMapper.readValue(usersFile, new TypeReference<>() {});
                } catch (IOException e) {
                    log.errorf(e, "Failed to load test users from file: %s", usersFilePath);
                }
            } else {
                log.warnf("Test users file not found: %s", usersFilePath);
            }
        }

        // Return defaults if no file provided or loading failed
        return getDefaultTestUsers();
    }

    private List<TestUser> getDefaultTestUsers() {
        log.info("Using default test users list.");
        List<TestUser> users = new ArrayList<>();
        
        users.add(TestUser.builder()
                .userId("c9dfb9e1-a612-4408-b830-8e4db1469b71")
                .username("test-admin")
                .password("admin")
                .email("admin@acode.gr")
                .firstName("Test")
                .lastName("Admin")
                .realmRoles(List.of("admin"))
                .build());

        users.add(TestUser.builder()
                .userId("ca77fd54-3cf1-4f51-967d-b4b2cb7ca7cb")
                .username("test-teacher")
                .password("teacher")
                .email("teacher@acode.gr")
                .firstName("Test")
                .lastName("Teacher")
                .realmRoles(List.of("teacher"))
                .build());

        users.add(TestUser.builder()
                .userId("1d6e54b6-afbb-47f6-b1bb-8ce445d159c5")
                .username("test-student")
                .password("student")
                .email("student@acode.gr")
                .firstName("Test")
                .lastName("Student")
                .realmRoles(List.of("student"))
                .build());

        return users;
    }
}
