package com.mvt.backApp.common.id;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessIdServiceTest {
    @Mock
    BusinessIdSequenceRepository repository;
    @InjectMocks
    BusinessIdService service;

    @Test
    void createsId() {
        BusinessIdSequence s = new BusinessIdSequence();
        s.setKind(BusinessIdKind.PATIENT);
        s.setNextValue(1001);
        when(repository.findForUpdate(BusinessIdKind.PATIENT)).thenReturn(Optional.of(s));
        assertThat(service.next(BusinessIdKind.PATIENT, "in")).isEqualTo("PATIN1001");
        verify(repository).save(s);
    }
}
