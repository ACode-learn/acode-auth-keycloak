package gr.alexc.keycloak.configuration.acode;

import lombok.AllArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RealmsResource;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.userprofile.config.UPAttribute;
import org.keycloak.representations.userprofile.config.UPAttributePermissions;
import org.keycloak.representations.userprofile.config.UPConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JBossLog
@AllArgsConstructor
public class RealmConfiguration {

    private RealmsResource realmsResource;
//    private BrowserFlowConfiguration browserFlowConfiguration;

    /**
     * Configures the realm, first validates if the realm exists and if none exists, creates the realm.
     */
    public void configure(String realmName, String realmDisplayName) {
        List<RealmRepresentation> realms = realmsResource.findAll();
        if (realms.isEmpty() || realms.stream().noneMatch(realm -> realm.getId().equals(realmName))) {
            log.infof("Realm does not yet exist, creating for realm: %s", realmName);
            createRealm(realmName, realmDisplayName, realmsResource);
        }
        updateRealm(realmName);
    }

    private void createRealm(String realmName, String displayName, RealmsResource realmsResource) {
        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setDisplayName(displayName);
        realmRepresentation.setId(realmName);
        realmRepresentation.setRealm(realmName);
        realmRepresentation.setEnabled(false);

        realmsResource.create(realmRepresentation);
        log.infof("Created realm '%s'", realmName);
    }

    private void updateRealm(String realmName) {
        RealmResource realmResource = realmsResource.realm(realmName);

//        browserFlowConfiguration.setRealmResource(realmResource);
//        String browserFlowAlias = browserFlowConfiguration.createBrowserFlow();

        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setBruteForceProtected(true);
        realmRepresentation.setEnabled(true);
//        realmRepresentation.setBrowserFlow(browserFlowAlias);
        realmRepresentation.setLoginTheme("acode-auth-keycloak-theme");

        realmResource.update(realmRepresentation);
        configureUserProfile(realmResource);
        configureClientScopes(realmResource);
    }

    private void configureUserProfile(RealmResource realmResource) {
        UPConfig userProfile = realmResource.users().userProfile().getConfiguration();

        if (userProfile.getAttributes() == null) {
            userProfile.setAttributes(new ArrayList<>());
        }

        // Check if userId attribute already exists
        boolean exists = userProfile.getAttributes().stream()
                .anyMatch(attr -> "userId".equals(attr.getName()));

        if (!exists) {
            log.info("Adding 'userId' custom profile attribute to realm configuration.");
            UPAttribute userIdAttr = new UPAttribute();
            userIdAttr.setName("userId");
            userIdAttr.setDisplayName("User ID");

            UPAttributePermissions permissions = new UPAttributePermissions();
            permissions.setView(Set.of("admin", "user"));
            permissions.setEdit(Set.of("admin"));
            userIdAttr.setPermissions(permissions);

            userProfile.getAttributes().add(userIdAttr);

            realmResource.users().userProfile().update(userProfile);
        }
    }

    private void configureClientScopes(RealmResource realmResource) {
        realmResource.clientScopes().findAll().stream()
                .filter(scope -> "profile".equals(scope.getName()))
                .findFirst()
                .ifPresent(profileScope -> {
                    boolean mapperExists = profileScope.getProtocolMappers() != null &&
                            profileScope.getProtocolMappers().stream()
                                    .anyMatch(mapper -> "userId".equals(mapper.getName()));

                    if (!mapperExists) {
                        log.info("Adding 'userId' protocol mapper to 'profile' client scope.");
                        ProtocolMapperRepresentation userIdMapper = new ProtocolMapperRepresentation();
                        userIdMapper.setName("userId");
                        userIdMapper.setProtocol("openid-connect");
                        userIdMapper.setProtocolMapper("oidc-usermodel-attribute-mapper");
                        Map<String, String> config = new HashMap<>();
                        config.put("user.attribute", "userId");
                        config.put("claim.name", "user_id");
                        config.put("jsonType.label", "String");
                        config.put("id.token.claim", "true");
                        config.put("access.token.claim", "true");
                        config.put("userinfo.token.claim", "true");
                        userIdMapper.setConfig(config);

                        realmResource.clientScopes().get(profileScope.getId()).getProtocolMappers().createMapper(userIdMapper);
                    }
                });
    }
}
