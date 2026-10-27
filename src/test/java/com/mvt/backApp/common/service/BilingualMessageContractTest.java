package com.mvt.backApp.common.service;

import com.mvt.backApp.config.InternationalizationConfig;
import org.junit.jupiter.api.*;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class BilingualMessageContractTest {
    private MessageService service;

    @BeforeEach
    void setUp() {
        service = new MessageService(new InternationalizationConfig().messageSource());
    }

    @AfterEach
    void clear() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void frenchHasEnglishDefaultAndFrenchLocalized() {
        LocaleContextHolder.setLocale(Locale.FRENCH);
        var m = service.resolve("validation.firstName.required");
        assertThat(m.defaultMessage()).isEqualTo("First name is required.");
        assertThat(m.localizedMessage()).isEqualTo("Le prénom est obligatoire.");
        assertThat(m.requestedLanguage()).isEqualTo("fr");
    }
}
