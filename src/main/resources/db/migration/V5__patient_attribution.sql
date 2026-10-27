ALTER TABLE patient_profiles ADD COLUMN IF NOT EXISTS referred_at timestamp;
CREATE INDEX IF NOT EXISTS idx_patient_profiles_referred_by_partner ON patient_profiles(referred_by_partner_id);
