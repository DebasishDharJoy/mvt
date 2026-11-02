package com.mvt.backApp.validation;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class MessageBundleParityTest {
    private Properties load(String name) throws Exception {
        Properties p = new Properties();
        try (Reader r = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/" + name)), StandardCharsets.UTF_8)) {
            p.load(r);
        }
        return p;
    }

    @Test
    void englishAndFrenchContainSameKeys() throws Exception {
        Properties english = load("messages.properties");
        Properties french = load("messages_fr.properties");
        assertThat(french.stringPropertyNames()).containsExactlyInAnyOrderElementsOf(english.stringPropertyNames());
        assertThat(french.getProperty("validation.firstName.required")).isNotEqualTo(english.getProperty("validation.firstName.required"));
    }
}
