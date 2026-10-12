CREATE TABLE permissions (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(150) NOT NULL UNIQUE,
    created_by varchar(255),
    updated_by varchar(255),
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false
);

CREATE TABLE roles (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(100) NOT NULL UNIQUE,
    created_by varchar(255),
    updated_by varchar(255),
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false
);

CREATE TABLE users (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    email varchar(320) NOT NULL UNIQUE,
    password_hash varchar(255) NOT NULL,
    title varchar(10),
    first_name varchar(100) NOT NULL,
    middle_name varchar(100),
    last_name varchar(100) NOT NULL,
    role_id uuid NOT NULL REFERENCES roles(id),
    created_by varchar(255),
    updated_by varchar(255),
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false
);

CREATE TABLE care_partner_profiles (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL UNIQUE REFERENCES users(id),
    partner_id varchar(32) NOT NULL UNIQUE,
    company_name varchar(200),
    country varchar(100) NOT NULL,
    trade_license varchar(150),
    gst_number varchar(100),
    passport_number varchar(100),
    verification_document_url varchar(1000) NOT NULL,
    approval_status varchar(20) NOT NULL,
    approved_by uuid REFERENCES users(id),
    approved_at timestamp,
    rejection_reason varchar(1000),
    created_by varchar(255),
    updated_by varchar(255),
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false,
    CONSTRAINT ck_care_partner_approval_status
        CHECK (approval_status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

CREATE TABLE patient_profiles (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL UNIQUE REFERENCES users(id),
    patient_id varchar(32) NOT NULL UNIQUE,
    country varchar(100) NOT NULL,
    referred_by_partner_id uuid REFERENCES care_partner_profiles(id),
    national_id varchar(100),
    passport_number varchar(100),
    created_by varchar(255),
    updated_by varchar(255),
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    is_active boolean NOT NULL DEFAULT true,
    is_deleted boolean NOT NULL DEFAULT false
);

CREATE TABLE role_permissions (
    role_id uuid NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id uuid NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE INDEX idx_users_role_id ON users(role_id);
CREATE INDEX idx_patient_profiles_referred_by ON patient_profiles(referred_by_partner_id);
CREATE INDEX idx_care_partner_status_created ON care_partner_profiles(approval_status, created_at DESC);
CREATE INDEX idx_care_partner_approved_by ON care_partner_profiles(approved_by);
