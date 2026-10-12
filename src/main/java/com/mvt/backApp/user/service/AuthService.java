package com.mvt.backApp.user.service;

import com.mvt.backApp.agent.dto.CarePartnerRegistrationDto;
import com.mvt.backApp.agent.dto.CarePartnerRegistrationResponseDto;
import com.mvt.backApp.agent.dto.CarePartnerRejectionRequestDto;
import com.mvt.backApp.agent.entity.ApprovalStatus;
import com.mvt.backApp.agent.entity.CarePartnerProfile;
import com.mvt.backApp.agent.repository.CarePartnerProfileRepository;
import com.mvt.backApp.common.dto.JwtResponseDto;
import com.mvt.backApp.common.dto.LoginRequestDto;
import com.mvt.backApp.common.exception.ApiException;
import com.mvt.backApp.common.security.RoleNames;
import com.mvt.backApp.common.service.FileStorageService;
import com.mvt.backApp.patient.dto.PatientRegistrationDto;
import com.mvt.backApp.patient.dto.PatientRegistrationResponseDto;
import com.mvt.backApp.patient.entity.PatientProfile;
import com.mvt.backApp.patient.repository.PatientProfileRepository;
import com.mvt.backApp.security.model.AuthenticatedUser;
import com.mvt.backApp.security.service.JwtService;
import com.mvt.backApp.user.entity.Role;
import com.mvt.backApp.user.entity.User;
import com.mvt.backApp.user.repository.RoleRepository;
import com.mvt.backApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CarePartnerProfileRepository partnerRepository;
    private final PatientProfileRepository patientRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public PatientRegistrationResponseDto registerPatient(PatientRegistrationDto request) {
        String email = normalizeEmail(request.getEmail());
        rejectDuplicateEmail(email);
        validatePatientIdentifiers(request);

        Role role = requiredRole(RoleNames.PATIENT, "PATIENT_ROLE_MISSING", "error.system.patientRoleMissing");
        User user = buildUser(
                email,
                request.getPassword(),
                request.getTitle(),
                request.getFirstName(),
                request.getMiddleName(),
                request.getLastName(),
                role
        );
        user = userRepository.save(user);

        PatientProfile profile = new PatientProfile();
        profile.setUser(user);
        profile.setPatientId(uniquePatientId());
        profile.setCountry(request.getCountry().trim());
        if (isIndia(request.getCountry())) {
            profile.setNationalId(request.getNationalId().trim());
            profile.setPassportNumber(null);
        } else {
            profile.setNationalId(null);
            profile.setPassportNumber(request.getPassportNumber().trim());
        }
        patientRepository.save(profile);

        // TODO before production: add an email-verification workflow.
        return PatientRegistrationResponseDto.builder()
                .patientId(profile.getPatientId())
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public CarePartnerRegistrationResponseDto registerCarePartner(CarePartnerRegistrationDto request) {
        String email = normalizeEmail(request.getEmail());
        rejectDuplicateEmail(email);
        validatePartnerIdentifiers(request);
        validateDocument(request.getVerificationDocument());

        Role role = requiredRole(
                RoleNames.CARE_PARTNER,
                "CARE_PARTNER_ROLE_MISSING",
                "error.system.carePartnerRoleMissing"
        );
        String documentUrl = storeVerificationDocument(request.getVerificationDocument());
        User user = buildUser(
                email,
                request.getPassword(),
                request.getTitle(),
                request.getFirstName(),
                request.getMiddleName(),
                request.getLastName(),
                role
        );
        user = userRepository.save(user);

        CarePartnerProfile profile = new CarePartnerProfile();
        profile.setUser(user);
        profile.setPartnerId(uniquePartnerId());
        profile.setCompanyName(trimToNull(request.getCompanyName()));
        profile.setCountry(request.getCountry().trim());
        profile.setTradeLicense(request.getTradeLicense().trim());
        profile.setGstNumber(isIndia(request.getCountry()) ? request.getGstNumber().trim() : null);
        profile.setPassportNumber(isIndia(request.getCountry()) ? null : request.getPassportNumber().trim());
        profile.setVerificationDocumentUrl(documentUrl);
        profile.setApprovalStatus(ApprovalStatus.PENDING);
        partnerRepository.save(profile);

        return CarePartnerRegistrationResponseDto.builder()
                .partnerId(profile.getPartnerId())
                .email(user.getEmail())
                .approvalStatus(profile.getApprovalStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public JwtResponseDto login(LoginRequestDto request) {
        String email = normalizeEmail(request.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        User user = userRepository.findByEmail(principal.getEmail())
                .orElseThrow(() -> new ApiException(
                        "INVALID_CREDENTIALS",
                        "error.auth.invalidCredentials",
                        HttpStatus.UNAUTHORIZED.value()
                ));
        enforceCarePartnerApproval(user);
        String token = jwtService.generateToken(principal);
        return JwtResponseDto.builder()
                .token(token)
                .userId(principal.getUserId())
                .email(principal.getEmail())
                .role(principal.getRole())
                .permissions(principal.getAuthorities().stream()
                        .map(authority -> authority.getAuthority())
                        .sorted()
                        .toList())
                .build();
    }

    private User buildUser(
            String email,
            String rawPassword,
            String title,
            String firstName,
            String middleName,
            String lastName,
            Role role
    ) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setTitle(trimToNull(title));
        user.setFirstName(firstName.trim());
        user.setMiddleName(trimToNull(middleName));
        user.setLastName(lastName.trim());
        user.setRole(role);
        user.setActive(true);
        user.setDeleted(false);
        return user;
    }

    private void rejectDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ApiException(
                    "DUPLICATE_EMAIL",
                    "error.registration.emailExists",
                    HttpStatus.CONFLICT.value()
            );
        }
    }

    private Role requiredRole(String name, String code, String messageKey) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ApiException(
                        code,
                        messageKey,
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }

    private void validatePatientIdentifiers(PatientRegistrationDto request) {
        if (isIndia(request.getCountry())) {
            if (isBlank(request.getNationalId())) {
                throw new ApiException(
                        "NATIONAL_ID_REQUIRED",
                        "error.registration.nationalIdRequired",
                        HttpStatus.BAD_REQUEST.value()
                );
            }
        } else if (isBlank(request.getPassportNumber())) {
            throw new ApiException(
                    "PASSPORT_REQUIRED",
                    "error.registration.passportRequired",
                    HttpStatus.BAD_REQUEST.value()
            );
        }
    }

    private void validatePartnerIdentifiers(CarePartnerRegistrationDto request) {
        if (isBlank(request.getTradeLicense())) {
            throw new ApiException(
                    "TRADE_LICENSE_REQUIRED",
                    "error.registration.tradeLicenseRequired",
                    HttpStatus.BAD_REQUEST.value()
            );
        }
        if (isIndia(request.getCountry()) && isBlank(request.getGstNumber())) {
            throw new ApiException(
                    "GST_NUMBER_REQUIRED",
                    "error.registration.gstRequired",
                    HttpStatus.BAD_REQUEST.value()
            );
        }
        if (!isIndia(request.getCountry()) && isBlank(request.getPassportNumber())) {
            throw new ApiException(
                    "PASSPORT_REQUIRED",
                    "error.registration.passportRequired",
                    HttpStatus.BAD_REQUEST.value()
            );
        }
    }

    private void validateDocument(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(
                    "VERIFICATION_DOCUMENT_REQUIRED",
                    "validation.verificationDocument.required",
                    HttpStatus.BAD_REQUEST.value()
            );
        }
        String contentType = file.getContentType();
        if (!("application/pdf".equals(contentType) || "image/jpeg".equals(contentType))) {
            throw new ApiException(
                    "INVALID_DOCUMENT_TYPE",
                    "error.registration.documentType",
                    HttpStatus.BAD_REQUEST.value()
            );
        }
    }

    private String storeVerificationDocument(MultipartFile file) {
        String extension = "application/pdf".equals(file.getContentType()) ? ".pdf" : ".jpg";
        String fileName = "AGT-DOC-" + UUID.randomUUID() + extension;
        try {
            return fileStorageService.uploadFile(file, fileName);
        } catch (IOException exception) {
            throw new ApiException(
                    "DOCUMENT_UPLOAD_FAILED",
                    "error.registration.documentUpload",
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
        }
    }

    private void enforceCarePartnerApproval(User user) {
        if (user.getRole() == null || !RoleNames.CARE_PARTNER.equals(user.getRole().getName())) {
            return;
        }
        CarePartnerProfile profile = partnerRepository.findByUser(user)
                .orElseThrow(() -> new ApiException(
                        "CARE_PARTNER_PROFILE_MISSING",
                        "error.auth.carePartnerProfileMissing",
                        HttpStatus.FORBIDDEN.value()
                ));
        if (profile.getApprovalStatus() != ApprovalStatus.APPROVED) {
            String code = profile.getApprovalStatus() == ApprovalStatus.REJECTED
                    ? "CARE_PARTNER_REJECTED" : "CARE_PARTNER_PENDING";
            String key = profile.getApprovalStatus() == ApprovalStatus.REJECTED
                    ? "error.auth.carePartnerRejected" : "error.auth.carePartnerPending";
            throw new ApiException(code, key, HttpStatus.FORBIDDEN.value());
        }
    }

    private String uniquePatientId() {
        String id;
        do {
            id = "PAT-" + compactUuid();
        } while (patientRepository.existsByPatientId(id));
        return id;
    }

    private String uniquePartnerId() {
        String id;
        do {
            id = "AGT-" + compactUuid();
        } while (partnerRepository.existsByPartnerId(id));
        return id;
    }

    private String compactUuid() {
        long value = Math.floorMod(UUID.randomUUID().getLeastSignificantBits(), 1_000_000_000_000L);
        return String.format(Locale.ROOT, "%012d", value);
    }

    private boolean isIndia(String country) {
        return country != null && "India".equalsIgnoreCase(country.trim());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String trimToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }
}
