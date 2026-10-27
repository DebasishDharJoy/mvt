package com.mvt.backApp.common.id;
import com.mvt.backApp.common.exception.ApiException;import lombok.RequiredArgsConstructor;import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;import java.util.*;
@Service @RequiredArgsConstructor public class BusinessIdService {
 private static final Set<String> ISO2=Set.of(Locale.getISOCountries());private final BusinessIdSequenceRepository repository;
 @Transactional public String next(BusinessIdKind kind,String raw){String code=raw==null?"":raw.trim().toUpperCase(Locale.ROOT);
  if(!ISO2.contains(code))throw new ApiException("COUNTRY_CODE_INVALID","validation.countryCode.invalid",400);
  BusinessIdSequence s=repository.findForUpdate(kind).orElseThrow(()->new ApiException("ID_SEQUENCE_MISSING","error.system.idSequenceMissing",500));
  long n=s.getNextValue();s.setNextValue(n+1);repository.save(s);return(kind==BusinessIdKind.PATIENT?"PAT":"AGT")+code+n; }
}
