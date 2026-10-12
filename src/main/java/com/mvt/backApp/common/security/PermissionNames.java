package com.mvt.backApp.common.security;

import java.util.Set;

public final class PermissionNames {
    public static final String CARE_PARTNER_APPLICATION_VIEW = "CARE_PARTNER_APPLICATION_VIEW";
    public static final String CARE_PARTNER_APPLICATION_APPROVE = "CARE_PARTNER_APPLICATION_APPROVE";
    public static final String CARE_PARTNER_APPLICATION_REJECT = "CARE_PARTNER_APPLICATION_REJECT";
    public static final String PATIENT_VIEW = "PATIENT_VIEW";
    public static final String ADMINISTRATOR_MANAGE = "ADMINISTRATOR_MANAGE";
    public static final String ROLE_MANAGE = "ROLE_MANAGE";
    public static final String PERMISSION_MANAGE = "PERMISSION_MANAGE";
    public static final String HOSPITAL_VIEW = "HOSPITAL_VIEW";
    public static final String HOSPITAL_CREATE = "HOSPITAL_CREATE";
    public static final String HOSPITAL_UPDATE = "HOSPITAL_UPDATE";
    public static final String HOSPITAL_DEACTIVATE = "HOSPITAL_DEACTIVATE";
    public static final String COUNTRY_MANAGE = "COUNTRY_MANAGE";
    public static final String STATE_MANAGE = "STATE_MANAGE";
    public static final String CITY_MANAGE = "CITY_MANAGE";
    public static final String DEPARTMENT_MANAGE = "DEPARTMENT_MANAGE";
    public static final String HOSPITAL_BRANCH_MANAGE = "HOSPITAL_BRANCH_MANAGE";
    public static final String BRANCH_DEPARTMENT_ASSIGN = "BRANCH_DEPARTMENT_ASSIGN";

    public static final Set<String> ALL = Set.of(
            CARE_PARTNER_APPLICATION_VIEW,
            CARE_PARTNER_APPLICATION_APPROVE,
            CARE_PARTNER_APPLICATION_REJECT,
            PATIENT_VIEW,
            ADMINISTRATOR_MANAGE,
            ROLE_MANAGE,
            PERMISSION_MANAGE,
            HOSPITAL_VIEW,
            HOSPITAL_CREATE,
            HOSPITAL_UPDATE,
            HOSPITAL_DEACTIVATE,
            COUNTRY_MANAGE,
            STATE_MANAGE,
            CITY_MANAGE,
            DEPARTMENT_MANAGE,
            HOSPITAL_BRANCH_MANAGE,
            BRANCH_DEPARTMENT_ASSIGN
    );

    public static final Set<String> DEFAULT_DELEGATED_ADMIN = Set.of(
            HOSPITAL_VIEW,
            HOSPITAL_CREATE,
            HOSPITAL_UPDATE,
            HOSPITAL_DEACTIVATE,
            COUNTRY_MANAGE,
            STATE_MANAGE,
            CITY_MANAGE,
            DEPARTMENT_MANAGE,
            HOSPITAL_BRANCH_MANAGE,
            BRANCH_DEPARTMENT_ASSIGN
    );

    private PermissionNames() {
    }
}
