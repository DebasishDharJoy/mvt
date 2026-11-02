package com.mvt.backApp.common.controller;

import com.mvt.backApp.common.dto.*;
import com.mvt.backApp.common.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reference")
@RequiredArgsConstructor
public class ReferenceDataController {
    private final CountryService countries;
    private final ApiResponseFactory responses;

    @GetMapping("/countries")
    public ResponseEntity<ApiResponse<List<CountryDto>>> all() {
        return ResponseEntity.ok(responses.success("success.countries.fetched", 200, countries.all()));
    }
}
