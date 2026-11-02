package com.mvt.backApp.common.service;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import java.util.Locale;
@Service @RequiredArgsConstructor
public class MessageService {
    public record ResolvedMessage(String messageKey, String defaultMessage,
                                  String localizedMessage, String requestedLanguage) {}
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    private final MessageSource messageSource;
    public ResolvedMessage resolve(String key, Object... arguments) {
        Locale requested = supported(LocaleContextHolder.getLocale());
        String english = messageSource.getMessage(key, arguments, key, DEFAULT_LOCALE);
        String localized = messageSource.getMessage(key, arguments, english, requested);
        return new ResolvedMessage(key, english, localized, requested.getLanguage());
    }
    public String getMessage(String key, Object... arguments) {
        return resolve(key, arguments).localizedMessage();
    }
    private Locale supported(Locale locale) {
        return locale != null && Locale.FRENCH.getLanguage().equals(locale.getLanguage())
                ? Locale.FRENCH : DEFAULT_LOCALE;
    }
}
