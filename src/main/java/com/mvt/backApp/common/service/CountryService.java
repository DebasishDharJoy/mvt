package com.mvt.backApp.common.service;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.mvt.backApp.common.dto.CountryDto;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CountryService {
    private final PhoneNumberUtil phones = PhoneNumberUtil.getInstance();

    public List<CountryDto> all() {
        return phones.getSupportedRegions().stream().map(code -> new CountryDto(code, new Locale("", code).getDisplayCountry(Locale.ENGLISH), "+" + phones.getCountryCodeForRegion(code))).sorted(Comparator.comparing(CountryDto::name)).toList();
    }
}
