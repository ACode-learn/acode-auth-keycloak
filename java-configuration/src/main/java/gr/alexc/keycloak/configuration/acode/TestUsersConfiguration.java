package gr.alexc.keycloak.configuration.acode;

import gr.alexc.keycloak.configuration.KeycloakConfigurationProperties;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration for test users in the ACode realm.
 */
@JBossLog
@AllArgsConstructor
public class TestUsersConfiguration {

    private final RealmResource realmResource;
    private final KeycloakConfigurationProperties configuration;

    public void configure() {
        log.info("Seeding test users...");

        // Ensure default roles exist
        ensureRoleExists("admin", "Administrator role");
        ensureRoleExists("teacher", "Teacher role");
        ensureRoleExists("student", "Student role");

        // Seed users
        TestUsersProvider usersProvider = new TestUsersProvider(configuration);
        for (TestUser user : usersProvider.getTestUsers()) {
            seedUser(user);
        }

        log.info("Finished seeding test users.");
    }

    private void ensureRoleExists(String roleName, String description) {
        RolesResource rolesResource = realmResource.roles();
        try {
            rolesResource.get(roleName).toRepresentation();
            log.debugf("Role '%s' already exists.", roleName);
        } catch (Exception e) {
            log.infof("Creating role '%s'.", roleName);
            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            role.setDescription(description);
            rolesResource.create(role);
        }
    }

    private void seedUser(TestUser testUser) {
        UsersResource usersResource = realmResource.users();
        List<UserRepresentation> existingUsers = usersResource.searchByUsername(testUser.getUsername(), true);

        if (existingUsers.isEmpty()) {
            log.infof("Creating test user '%s'.", testUser.getUsername());
            UserRepresentation user = new UserRepresentation();
            user.setUsername(testUser.getUsername());
            user.setEmail(testUser.getEmail());
            user.setFirstName(testUser.getFirstName());
            user.setLastName(testUser.getLastName());
            user.setEnabled(true);
            user.setEmailVerified(true);

            // Set user ID as an attribute
            if (testUser.getUserId() != null) {
                Map<String, List<String>> attributes = new HashMap<>();
                attributes.put("userId", List.of(testUser.getUserId()));
                user.setAttributes(attributes);
            }

            String userId;
            try (Response response = usersResource.create(user)) {
                if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                    userId = CreatedResponseUtil.getCreatedId(response);
                } else {
                    String errorMessage = response.readEntity(String.class);
                    log.errorf("Failed to create test user '%s'. Status: %s, Error: %s",
                            testUser.getUsername(), response.getStatus(), errorMessage);
                    return;
                }
            }

            if (userId != null) {
                // Set password
                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(testUser.getPassword());
                credential.setTemporary(false);
                usersResource.get(userId).resetPassword(credential);

                // Assign realm roles
                if (testUser.getRealmRoles() != null && !testUser.getRealmRoles().isEmpty()) {
                    List<RoleRepresentation> rolesToAdd = testUser.getRealmRoles().stream()
                            .map(roleName -> realmResource.roles().get(roleName).toRepresentation())
                            .toList();
                    usersResource.get(userId).roles().realmLevel().add(rolesToAdd);
                }
            }
        } else {
            log.debugf("Test user '%s' already exists.", testUser.getUsername());
        }
    }
}
