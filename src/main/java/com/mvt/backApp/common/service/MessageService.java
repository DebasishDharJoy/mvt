package com.mvt.backApp.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageSource messageSource;

    public String getMessage(String key, Object... arguments) {
        return messageSource.getMessage(
                key,
                arguments,
                key,
                LocaleContextHolder.getLocale()
        );
    }
}