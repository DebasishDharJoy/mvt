package com.mvt.backApp.user.service;

import com.mvt.backApp.agent.entity.ApprovalStatus;
import com.mvt.backApp.agent.entity.CarePartnerProfile;
import com.mvt.backApp.agent.repository.CarePartnerProfileRepository;
import com.mvt.backApp.common.dto.CarePartnerRegistrationDto;
import com.mvt.backApp.common.exception.ApiException;
import com.mvt.backApp.common.service.FileStorageService;
import com.mvt.backApp.user.entity.Role;
import com.mvt.backApp.user.entity.User;
import com.mvt.backApp.user.repository.RoleRepository;
import com.mvt.backApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    // ... repositories injected here ...
    private final FileStorageService fileStorageService; // A custom service you build to handle saving to OCI/Local disk

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final CarePartnerProfileRepository partnerRepository;

    @Transactional
    public String registerCarePartner(CarePartnerRegistrationDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email is already registered", HttpStatus.BAD_REQUEST.value(), "Duplicate Email");
        }

        // 1. Validate the File Type (PDF or JPG)
        MultipartFile file = request.getVerificationDocument();
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/pdf") && !contentType.equals("image/jpeg"))) {
            throw new ApiException("Invalid file type. Only PDF or JPG allowed.", HttpStatus.BAD_REQUEST.value(), "Invalid File");
        }

        // 2. Upload the file and get the URL
        String documentUrl;
        try {
            // e.g., creates a unique filename like "AGT-DOC-1234-uuid.pdf"
            String fileName = "AGT-DOC-" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            documentUrl = fileStorageService.uploadFile(file, fileName);
        } catch (IOException e) {
            throw new ApiException("Failed to upload document", HttpStatus.INTERNAL_SERVER_ERROR.value(), "Upload Error");
        }

        // 3. Create User with New Fields
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setTitle(request.getTitle());
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());

        Role partnerRole = roleRepository.findByName("CARE_PARTNER")
                .orElseThrow(() -> new ApiException("Role not found", HttpStatus.INTERNAL_SERVER_ERROR.value(), "System Error"));
        user.setRole(partnerRole);
        user = userRepository.save(user);

        // 4. Create Care Partner Profile
        CarePartnerProfile profile = new CarePartnerProfile();
        profile.setUser(user);
        profile.setCompanyName(request.getCompanyName());
        profile.setCountry(request.getCountry());
        profile.setTradeLicense(request.getTradeLicense());
        profile.setVerificationDocumentUrl(documentUrl); // Save the file path

        // Conditional ID checks based on country
        if ("India".equalsIgnoreCase(request.getCountry())) {
            if (request.getGstNumber() == null || request.getGstNumber().isEmpty()) {
                throw new ApiException("GST Number is required for Indian partners", HttpStatus.BAD_REQUEST.value(), "Validation Error");
            }
            profile.setGstNumber(request.getGstNumber());
        } else {
            if (request.getPassportNumber() == null || request.getPassportNumber().isEmpty()) {
                throw new ApiException("Passport is required for international partners", HttpStatus.BAD_REQUEST.value(), "Validation Error");
            }
            profile.setPassportNumber(request.getPassportNumber());
        }

        profile.setApprovalStatus(ApprovalStatus.PENDING);

        String partnerId = "AGT-" + (90000 + partnerRepository.count() + 1);
        profile.setPartnerId(partnerId);

        partnerRepository.save(profile);

        return partnerId;
    }
}