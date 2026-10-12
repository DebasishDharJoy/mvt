package com.mvt.backApp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.bootstrap")
public class BootstrapProperties {
    private boolean enabled = false;
    private final Account superAdmin = new Account();
    private final DelegatedAdmin admin = new DelegatedAdmin();

    @Getter
    @Setter
    public static class Account {
        private String email;
        private String password;
        private String firstName;
        private String lastName;
    }

    @Getter
    @Setter
    public static class DelegatedAdmin extends Account {
        private boolean enabled = false;
        private Set<String> permissions = new LinkedHashSet<>();
    }
}
