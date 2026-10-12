package com.mvt.backApp.config;

import com.mvt.backApp.user.service.BootstrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.bootstrap.enabled", havingValue = "true")
public class DevelopmentBootstrapRunner implements ApplicationRunner {
    private final BootstrapService bootstrapService;

    @Override
    public void run(ApplicationArguments args) {
        bootstrapService.initialize();
    }
}
