SET DEFINE OFF;

-- Clean up existing objects from partial execution
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE evidence CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE puc_certificate CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE challan CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE police_officer CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE vehicle CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE owner CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE violation_master CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE users CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE users_seq';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE violation_master_seq';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE owner_seq';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE challan_seq';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE evidence_seq';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE puc_certificate_seq';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

-- Oracle Database Schema for Vehicle Authentication System
-- Converted from MySQL to Oracle SQL

-- =============== USERS TABLE ===============

CREATE TABLE users (
    id NUMBER(19) PRIMARY KEY,
    username VARCHAR2(50) NOT NULL UNIQUE,
    password VARCHAR2(255) NOT NULL,
    full_name VARCHAR2(100) NOT NULL,
    email VARCHAR2(100) NOT NULL UNIQUE,
    phone VARCHAR2(20),
    role VARCHAR2(50) NOT NULL CHECK (role IN ('POLICE', 'PUC_CENTER', 'INSURANCE_AGENCY')),
    department VARCHAR2(100),
    center_name VARCHAR2(100),
    agency_name VARCHAR2(100),
    license_number VARCHAR2(50),
    is_active NUMBER(1) DEFAULT 1 CHECK (is_active IN (0, 1)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_role ON users(role);

CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER users_trg
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT users_seq.NEXTVAL INTO :NEW.id FROM dual;
    END IF;
    :NEW.updated_at := SYSTIMESTAMP;
END;
/

INSERT INTO users (id, username, password, full_name, email, phone, role, department) VALUES
(1, 'police_admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Rajesh Kumar', 'rajesh.kumar@police.gov.in', '+91-98765-43210', 'POLICE', 'Traffic Police Department - Mumbai');

INSERT INTO users (id, username, password, full_name, email, phone, role, department) VALUES
(2, 'police_inspector', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Amit Sharma', 'amit.sharma@police.gov.in', '+91-98765-43211', 'POLICE', 'RTO Enforcement Wing - Delhi');

INSERT INTO users (id, username, password, full_name, email, phone, role, center_name, license_number) VALUES
(3, 'puc_mumbai_01', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Suresh Patel', 'suresh@mumbaipuc.com', '+91-98765-43220', 'PUC_CENTER', 'Mumbai PUC Testing Center - Andheri', 'PUC-MH-001245');

INSERT INTO users (id, username, password, full_name, email, phone, role, center_name, license_number) VALUES
(4, 'puc_delhi_01', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Vikram Singh', 'vikram@delhipuc.in', '+91-98765-43221', 'PUC_CENTER', 'Delhi Emission Testing Lab', 'PUC-DL-005678');

INSERT INTO users (id, username, password, full_name, email, phone, role, agency_name, license_number) VALUES
(5, 'insurance_lic_01', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Priya Nair', 'priya.nair@licindia.com', '+91-98765-43230', 'INSURANCE_AGENCY', 'Life Insurance Corporation of India', 'IRDA-LIC-1001');

INSERT INTO users (id, username, password, full_name, email, phone, role, agency_name, license_number) VALUES
(6, 'insurance_hdfc_01', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Karan Mehta', 'karan.mehta@hdfcergo.com', '+91-98765-43231', 'INSURANCE_AGENCY', 'HDFC ERGO General Insurance', 'IRDA-HDFC-2056');

-- =============== E-CHALLAN MODULE TABLES ===============

-- 1. VIOLATION MASTER (reference table)
CREATE TABLE violation_master (
    violation_id NUMBER(10) PRIMARY KEY,
    violation_name VARCHAR2(150) NOT NULL UNIQUE,
    mv_act_section VARCHAR2(20) NOT NULL,
    fine_amount NUMBER(10) DEFAULT 500 NOT NULL,
    description VARCHAR2(500),
    is_active NUMBER(1) DEFAULT 1 CHECK (is_active IN (0, 1)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vm_active ON violation_master(is_active);

CREATE SEQUENCE violation_master_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER violation_master_trg
BEFORE INSERT ON violation_master
FOR EACH ROW
BEGIN
    IF :NEW.violation_id IS NULL THEN
        SELECT violation_master_seq.NEXTVAL INTO :NEW.violation_id FROM dual;
    END IF;
    :NEW.updated_at := SYSTIMESTAMP;
END;
/

INSERT INTO violation_master (violation_id, violation_name, mv_act_section, fine_amount, description) VALUES
(1, 'Not Wearing Helmet', '194D', 500, 'Rider / pillion not wearing ISI standard helmet');

INSERT INTO violation_master (violation_id, violation_name, mv_act_section, fine_amount, description) VALUES
(2, 'Not Wearing Seatbelt', '177', 500, 'Driver / passenger not wearing seat belt in car');

INSERT INTO violation_master (violation_id, violation_name, mv_act_section, fine_amount, description) VALUES
(3, 'Triple Seat / Overloading', '182B', 1000, 'Extra pillion riders on two-wheeler or vehicle overload');

INSERT INTO violation_master (violation_id, violation_name, mv_act_section, fine_amount, description) VALUES
(4, 'Signal Jump / Red Light', '177', 1000, 'Violation of traffic signal / red light jumping');

INSERT INTO violation_master (violation_id, violation_name, mv_act_section, fine_amount, description) VALUES
(5, 'Wrong Parking', '177', 500, 'Vehicle parked in no-parking zone / obstruction');

INSERT INTO violation_master (violation_id, violation_name, mv_act_section, fine_amount, description) VALUES
(6, 'Mobile Phone While Driving', '177', 1000, 'Using hand-held mobile phone while driving');

INSERT INTO violation_master (violation_id, violation_name, mv_act_section, fine_amount, description) VALUES
(7, 'Over Speeding', '183(1)', 1000, 'Exceeding prescribed speed limit for vehicle class');

INSERT INTO violation_master (violation_id, violation_name, mv_act_section, fine_amount, description) VALUES
(8, 'Dangerous / Rash Driving', '184', 1000, 'Driving at a speed or manner dangerous to public');

INSERT INTO violation_master (violation_id, violation_name, mv_act_section, fine_amount, description) VALUES
(9, 'No Valid PUC Certificate', '175', 1000, 'Vehicle without valid Pollution Under Control cert');

INSERT INTO violation_master (violation_id, violation_name, mv_act_section, fine_amount, description) VALUES
(10, 'No Valid Insurance', '196', 2000, 'Vehicle without valid third-party insurance');

INSERT INTO violation_master (violation_id, violation_name, mv_act_section, fine_amount, description) VALUES
(11, 'Driving Without RC / Fitness', '192B', 2000, 'Vehicle without valid Registration Certificate');

INSERT INTO violation_master (violation_id, violation_name, mv_act_section, fine_amount, description) VALUES
(12, 'Driving Without Licence', '192A', 5000, 'Driving without a valid driving licence');

INSERT INTO violation_master (violation_id, violation_name, mv_act_section, fine_amount, description) VALUES
(13, 'Wrong Side Driving', '119', 500, 'Driving on the wrong side of the road');

INSERT INTO violation_master (violation_id, violation_name, mv_act_section, fine_amount, description) VALUES
(14, 'Drink and Drive / Alcohol', '185', 10000, 'Driving under influence of alcohol or drugs');

INSERT INTO violation_master (violation_id, violation_name, mv_act_section, fine_amount, description) VALUES
(15, 'Others / Miscellaneous', '177', 500, 'Any other violation not listed above');

-- 2. OWNER
CREATE TABLE owner (
    owner_id NUMBER(19) PRIMARY KEY,
    owner_name VARCHAR2(150) NOT NULL,
    mobile VARCHAR2(10),
    email VARCHAR2(150),
    address CLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_owner_mobile ON owner(mobile);
CREATE INDEX idx_owner_name ON owner(owner_name);

CREATE SEQUENCE owner_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER owner_trg
BEFORE INSERT ON owner
FOR EACH ROW
BEGIN
    IF :NEW.owner_id IS NULL THEN
        SELECT owner_seq.NEXTVAL INTO :NEW.owner_id FROM dual;
    END IF;
    :NEW.updated_at := SYSTIMESTAMP;
END;
/

INSERT INTO owner (owner_id, owner_name, mobile, email, address) VALUES
(1, 'Amit Sharma', '9820011234', 'amit.sharma@email.com', '12, MG Road, Andheri West, Mumbai - 400058');

INSERT INTO owner (owner_id, owner_name, mobile, email, address) VALUES
(2, 'Priya Verma', '9833312345', 'priya.verma@email.com', '45, Nehru Nagar, Bandra East, Mumbai - 400051');

INSERT INTO owner (owner_id, owner_name, mobile, email, address) VALUES
(3, 'Rajesh Patel', '9004454321', 'raj.patel@email.com', '7, LBS Marg, Ghatkopar, Mumbai - 400086');

INSERT INTO owner (owner_id, owner_name, mobile, email, address) VALUES
(4, 'Sneha Iyer', '9810087654', 'sneha.iyer@email.com', 'Flat 302, Green Park, Delhi - 110016');

INSERT INTO owner (owner_id, owner_name, mobile, email, address) VALUES
(5, 'Vikram Singh', '9999012345', 'vikram.singh@email.com', 'House 14, Sector 15, Gurgaon - 122001');

INSERT INTO owner (owner_id, owner_name, mobile, email, address) VALUES
(6, 'Anita Desai', '9821145678', 'anita.desai@email.com', '8, Residency Road, Bengaluru - 560025');

INSERT INTO owner (owner_id, owner_name, mobile, email, address) VALUES
(7, 'Karan Mehta', '9766677777', 'karan.mehta@email.com', 'Plot 22, Koregaon Park, Pune - 411001');

INSERT INTO owner (owner_id, owner_name, mobile, email, address) VALUES
(8, 'Sunita Rao', '9444488222', 'sunita.rao@email.com', '15, TTK Road, Alwarpet, Chennai - 600018');

INSERT INTO owner (owner_id, owner_name, mobile, email, address) VALUES
(9, 'Deepak Joshi', '9898976765', 'deepak.joshi@email.com', '20, C.G. Road, Navrangpura, Ahmedabad - 380009');

INSERT INTO owner (owner_id, owner_name, mobile, email, address) VALUES
(10, 'Rohit Yadav', '9911123222', 'rohit.yadav@email.com', '5, Hazratganj, Lucknow - 226001');

INSERT INTO owner (owner_id, owner_name, mobile, email, address) VALUES
(11, 'Meera Nair', '9847012321', 'meera.nair@email.com', '2, Kowdiar Gardens, Trivandrum - 695003');

INSERT INTO owner (owner_id, owner_name, mobile, email, address) VALUES
(12, 'Nikhil Jain', '9300055555', 'nikhil.jain@email.com', 'Shop 18, MI Road, Jaipur - 302001');

INSERT INTO owner (owner_id, owner_name, mobile, email, address) VALUES
(13, 'Aditya Das', '9088011222', 'aditya.das@email.com', '6, Ballygunge Place, Kolkata - 700019');

INSERT INTO owner (owner_id, owner_name, mobile, email, address) VALUES
(14, 'Sonal Gupta', '9988833444', 'sonal.gupta@email.com', 'Sector 21D, Chandigarh - 160021');

INSERT INTO owner (owner_id, owner_name, mobile, email, address) VALUES
(15, 'Amrita Sen', '7002200111', 'amrita.sen@email.com', '4, Garcha Road, Bhubaneswar - 751001');

-- 3. VEHICLE
CREATE TABLE vehicle (
    vehicle_no VARCHAR2(20) PRIMARY KEY,
    owner_id NUMBER(19) NOT NULL,
    vehicle_type VARCHAR2(40) NOT NULL,
    make VARCHAR2(100),
    model VARCHAR2(100),
    fuel_type VARCHAR2(20),
    registration_date DATE,
    registration_state VARCHAR2(50),
    insurance_status VARCHAR2(20) DEFAULT 'Valid',
    puc_status VARCHAR2(20) DEFAULT 'Valid',
    rc_status VARCHAR2(20) DEFAULT 'Active',
    engine_number VARCHAR2(50),
    chassis_number VARCHAR2(50),
    blacklisted NUMBER(1) DEFAULT 0 CHECK (blacklisted IN (0, 1)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicle_owner FOREIGN KEY (owner_id) REFERENCES owner(owner_id)
);

CREATE INDEX idx_vehicle_type ON vehicle(vehicle_type);
CREATE INDEX idx_vehicle_owner ON vehicle(owner_id);
CREATE INDEX idx_vehicle_black ON vehicle(blacklisted);

CREATE OR REPLACE TRIGGER vehicle_trg
BEFORE INSERT OR UPDATE ON vehicle
FOR EACH ROW
BEGIN
    :NEW.updated_at := SYSTIMESTAMP;
END;
/

INSERT INTO vehicle (vehicle_no, owner_id, vehicle_type, make, model, fuel_type, registration_date, registration_state, insurance_status, puc_status, rc_status, engine_number, chassis_number, blacklisted) VALUES
('MH-04 AB 1234', 1, 'Car / Jeep', 'Maruti Suzuki', 'Alto 800 LXi', 'Petrol', TO_DATE('2020-05-15', 'YYYY-MM-DD'), 'Maharashtra', 'Valid', 'Expired', 'Active', 'EN37FE881234', 'CH12345678MSA1', 0);

INSERT INTO vehicle (vehicle_no, owner_id, vehicle_type, make, model, fuel_type, registration_date, registration_state, insurance_status, puc_status, rc_status, engine_number, chassis_number, blacklisted) VALUES
('MH-12 CD 5678', 2, 'Two Wheeler', 'Honda', 'Activa 6G', 'Petrol', TO_DATE('2021-09-22', 'YYYY-MM-DD'), 'Maharashtra', 'Valid', 'Valid', 'Active', 'ENHW21K78901', 'CH78901234HW12', 0);

INSERT INTO vehicle (vehicle_no, owner_id, vehicle_type, make, model, fuel_type, registration_date, registration_state, insurance_status, puc_status, rc_status, engine_number, chassis_number, blacklisted) VALUES
('MH-03 ZA 1120', 3, 'HMV — Truck / Bus', 'Tata Motors', 'LPT 1109 CRX', 'Diesel', TO_DATE('2019-03-05', 'YYYY-MM-DD'), 'Maharashtra', 'Valid', 'Valid', 'Active', 'ENTM19D11220', 'CH11220033TM01', 0);

INSERT INTO vehicle (vehicle_no, owner_id, vehicle_type, make, model, fuel_type, registration_date, registration_state, insurance_status, puc_status, rc_status, engine_number, chassis_number, blacklisted) VALUES
('DL-01 BC 4455', 4, 'Two Wheeler', 'Bajaj Auto', 'Pulsar 150', 'Petrol', TO_DATE('2018-07-11', 'YYYY-MM-DD'), 'Delhi', 'Valid', 'Expired', 'Active', 'ENBA18P45000', 'CH45000011BA01', 1);

INSERT INTO vehicle (vehicle_no, owner_id, vehicle_type, make, model, fuel_type, registration_date, registration_state, insurance_status, puc_status, rc_status, engine_number, chassis_number, blacklisted) VALUES
('DL-10 XY 8832', 5, 'Car / Jeep', 'Hyundai', 'Creta 1.6 SX', 'Diesel', TO_DATE('2022-01-28', 'YYYY-MM-DD'), 'Delhi', 'Valid', 'Valid', 'Active', 'ENHM22D99800', 'CH99800765HM02', 0);

INSERT INTO vehicle (vehicle_no, owner_id, vehicle_type, make, model, fuel_type, registration_date, registration_state, insurance_status, puc_status, rc_status, engine_number, chassis_number, blacklisted) VALUES
('KA-05 DE 7788', 6, 'Car / Jeep', 'Tata Motors', 'Nexon EV', 'Electric', TO_DATE('2023-02-14', 'YYYY-MM-DD'), 'Karnataka', 'Valid', 'Valid', 'Active', 'EN-TM23E0012', 'CHTM001234EV03', 0);

INSERT INTO vehicle (vehicle_no, owner_id, vehicle_type, make, model, fuel_type, registration_date, registration_state, insurance_status, puc_status, rc_status, engine_number, chassis_number, blacklisted) VALUES
('GJ-06 HI 2211', 7, 'Two Wheeler', 'TVS Motor', 'Jupiter 125', 'Petrol', TO_DATE('2021-11-03', 'YYYY-MM-DD'), 'Gujarat', 'Expired', 'Valid', 'Active', 'ENTV21P88901', 'CH88901045TV04', 0);

INSERT INTO vehicle (vehicle_no, owner_id, vehicle_type, make, model, fuel_type, registration_date, registration_state, insurance_status, puc_status, rc_status, engine_number, chassis_number, blacklisted) VALUES
('TN-09 FG 9900', 8, 'HMV — Truck / Bus', 'Ashok Leyland', 'Ecomet 1214', 'Diesel', TO_DATE('2017-08-30', 'YYYY-MM-DD'), 'Tamil Nadu', 'Valid', 'Valid', 'Active', 'ENAL17D55330', 'CH55330009AL05', 0);

INSERT INTO vehicle (vehicle_no, owner_id, vehicle_type, make, model, fuel_type, registration_date, registration_state, insurance_status, puc_status, rc_status, engine_number, chassis_number, blacklisted) VALUES
('UP-32 JK 3344', 10, 'HMV — Truck / Bus', 'Volvo Trucks', 'FH 500', 'Diesel', TO_DATE('2020-10-08', 'YYYY-MM-DD'), 'Uttar Pradesh', 'Valid', 'Expired', 'Active', 'ENVT20D00451', 'CH00451908VT06', 0);

INSERT INTO vehicle (vehicle_no, owner_id, vehicle_type, make, model, fuel_type, registration_date, registration_state, insurance_status, puc_status, rc_status, engine_number, chassis_number, blacklisted) VALUES
('RJ-14 LM 8899', 12, 'Car / Jeep', 'Mahindra', 'Bolero B6', 'Diesel', TO_DATE('2019-06-20', 'YYYY-MM-DD'), 'Rajasthan', 'Valid', 'Valid', 'Active', 'ENMM19D22341', 'CH22341677MM07', 0);

INSERT INTO vehicle (vehicle_no, owner_id, vehicle_type, make, model, fuel_type, registration_date, registration_state, insurance_status, puc_status, rc_status, engine_number, chassis_number, blacklisted) VALUES
('MH-05 FG 9900', 3, 'Three Wheeler', 'Bajaj Auto', 'RE Compact 4S', 'CNG', TO_DATE('2022-03-17', 'YYYY-MM-DD'), 'Maharashtra', 'Valid', 'Valid', 'Active', 'ENBA22G77612', 'CH77612305BA08', 0);

INSERT INTO vehicle (vehicle_no, owner_id, vehicle_type, make, model, fuel_type, registration_date, registration_state, insurance_status, puc_status, rc_status, engine_number, chassis_number, blacklisted) VALUES
('PB-02 MN 1234', 14, 'Two Wheeler', 'Hero MotoCorp', 'Splendor Plus', 'Petrol', TO_DATE('2019-12-04', 'YYYY-MM-DD'), 'Punjab', 'Valid', 'Expired', 'Suspended', 'ENHM19P22890', 'CH22890011HM09', 0);

-- 4. POLICE OFFICER
CREATE TABLE police_officer (
    officer_id VARCHAR2(20) PRIMARY KEY,
    officer_name VARCHAR2(150) NOT NULL,
    badge_no VARCHAR2(20) NOT NULL UNIQUE,
    police_station VARCHAR2(200) NOT NULL,
    role VARCHAR2(50) DEFAULT 'POLICE' CHECK (role IN ('POLICE', 'RTO_ENFORCEMENT', 'INSPECTOR', 'SUPERINTENDENT')),
    user_id NUMBER(19) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_officer_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_officer_station ON police_officer(police_station);

CREATE OR REPLACE TRIGGER police_officer_trg
BEFORE INSERT OR UPDATE ON police_officer
FOR EACH ROW
BEGIN
    :NEW.updated_at := SYSTIMESTAMP;
END;
/

INSERT INTO police_officer (officer_id, officer_name, badge_no, police_station, role, user_id) VALUES
('PO43210', 'Rajesh Kumar', 'BADGE-43210', 'Mumbai Central Traffic Police Station', 'POLICE', 1);

INSERT INTO police_officer (officer_id, officer_name, badge_no, police_station, role, user_id) VALUES
('PO11223', 'Amit Sharma', 'BADGE-11223', 'Delhi RTO Enforcement Wing - Shahdara', 'RTO_ENFORCEMENT', 2);

INSERT INTO police_officer (officer_id, officer_name, badge_no, police_station, role, user_id) VALUES
('PO55440', 'Priya Nair', 'BADGE-55440', 'Andheri Traffic Division - Mumbai', 'INSPECTOR', NULL);

INSERT INTO police_officer (officer_id, officer_name, badge_no, police_station, role, user_id) VALUES
('PO88001', 'Karan Mehta', 'BADGE-88001', 'Thane Superintendent Traffic Office', 'SUPERINTENDENT', NULL);

INSERT INTO police_officer (officer_id, officer_name, badge_no, police_station, role, user_id) VALUES
('PO77123', 'Vikram Singh', 'BADGE-77123', 'Worli Traffic Outpost - Mumbai', 'POLICE', NULL);

INSERT INTO police_officer (officer_id, officer_name, badge_no, police_station, role, user_id) VALUES
('PO66987', 'Sneha Iyer', 'BADGE-66987', 'Pune Swargate Traffic Police Station', 'INSPECTOR', NULL);

-- 5. CHALLAN
CREATE TABLE challan (
    challan_id NUMBER(19) PRIMARY KEY,
    challan_number VARCHAR2(20) NOT NULL UNIQUE,
    vehicle_no VARCHAR2(20) NOT NULL,
    officer_id VARCHAR2(20) NOT NULL,
    violation_ids VARCHAR2(200) NOT NULL,
    total_fine_amount NUMBER(10) DEFAULT 0 NOT NULL,
    challan_date DATE NOT NULL,
    challan_time VARCHAR2(8) NOT NULL,
    location VARCHAR2(300) NOT NULL,
    gps_coords VARCHAR2(60),
    status VARCHAR2(50) DEFAULT 'PENDING_PAYMENT' CHECK (status IN ('PENDING_PAYMENT', 'PAID', 'CANCELLED', 'DISPUTED', 'EXPIRED')),
    remarks VARCHAR2(500),
    evidence_vehicle_photo CLOB,
    evidence_plate_photo CLOB,
    evidence_driver_photo CLOB,
    evidence_video CLOB,
    payment_status VARCHAR2(20) DEFAULT 'Unpaid',
    payment_date DATE,
    payment_reference VARCHAR2(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_challan_vehicle FOREIGN KEY (vehicle_no) REFERENCES vehicle(vehicle_no),
    CONSTRAINT fk_challan_officer FOREIGN KEY (officer_id) REFERENCES police_officer(officer_id)
);

CREATE INDEX idx_challan_vehicle ON challan(vehicle_no);
CREATE INDEX idx_challan_officer ON challan(officer_id);
CREATE INDEX idx_challan_status ON challan(status);
CREATE INDEX idx_challan_date ON challan(challan_date);

CREATE SEQUENCE challan_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER challan_trg
BEFORE INSERT ON challan
FOR EACH ROW
BEGIN
    IF :NEW.challan_id IS NULL THEN
        SELECT challan_seq.NEXTVAL INTO :NEW.challan_id FROM dual;
    END IF;
    :NEW.updated_at := SYSTIMESTAMP;
END;
/

INSERT INTO challan (challan_id, challan_number, vehicle_no, officer_id, violation_ids, total_fine_amount, challan_date, challan_time, location, gps_coords, status, remarks, evidence_vehicle_photo, evidence_plate_photo, payment_status) VALUES
(1, 'CH20260000123', 'MH-04 AB 1234', 'PO43210', '9', 1000, TO_DATE('2026-07-15', 'YYYY-MM-DD'), '09:14:00', 'Andheri Flyover, Western Express Hwy', '19.1197° N, 72.8469° E', 'PENDING_PAYMENT', 'PUC expired 2 months, notice sent.', NULL, NULL, 'Unpaid');

INSERT INTO challan (challan_id, challan_number, vehicle_no, officer_id, violation_ids, total_fine_amount, challan_date, challan_time, location, gps_coords, status, remarks, evidence_vehicle_photo, evidence_plate_photo, payment_status) VALUES
(2, 'CH20260000124', 'DL-01 BC 4455', 'PO11223', '1,7', 1500, TO_DATE('2026-07-18', 'YYYY-MM-DD'), '11:42:00', 'Shahdara Red Light Junction', '28.6747° N, 77.3005° E', 'PENDING_PAYMENT', 'No helmet + overspeeding through signal.', NULL, NULL, 'Unpaid');

INSERT INTO challan (challan_id, challan_number, vehicle_no, officer_id, violation_ids, total_fine_amount, challan_date, challan_time, location, gps_coords, status, remarks, evidence_vehicle_photo, evidence_plate_photo, payment_status) VALUES
(3, 'CH20260000122', 'GJ-06 HI 2211', 'PO77123', '10', 2000, TO_DATE('2026-07-09', 'YYYY-MM-DD'), '13:05:00', 'SG Highway Crossroads, Ahmedabad', '23.0394° N, 72.5112° E', 'PENDING_PAYMENT', 'Insurance policy not provided on demand.', NULL, NULL, 'Unpaid');

INSERT INTO challan (challan_id, challan_number, vehicle_no, officer_id, violation_ids, total_fine_amount, challan_date, challan_time, location, gps_coords, status, remarks, evidence_vehicle_photo, evidence_plate_photo, payment_status) VALUES
(4, 'CH20260000121', 'UP-32 JK 3344', 'PO77123', '9,8', 2000, TO_DATE('2026-07-02', 'YYYY-MM-DD'), '08:22:00', 'Agra-Ghaziabad Highway Plaza', '28.6685° N, 77.4520° E', 'PAID', 'Both PUC expired '||chr(38)||' dangerous driving. Paid via Paytm.', NULL, NULL, 'Paid');

INSERT INTO challan (challan_id, challan_number, vehicle_no, officer_id, violation_ids, total_fine_amount, challan_date, challan_time, location, gps_coords, status, remarks, evidence_vehicle_photo, evidence_plate_photo, payment_status) VALUES
(5, 'CH20260000120', 'MH-12 CD 5678', 'PO55440', '5', 500, TO_DATE('2026-06-28', 'YYYY-MM-DD'), '17:55:00', 'Bandra Link Road No-Parking Zone', '19.0596° N, 72.8295° E', 'PAID', 'Wrong parking cleared after fine.', NULL, NULL, 'Paid');

-- 6. EVIDENCE
CREATE TABLE evidence (
    evidence_id NUMBER(19) PRIMARY KEY,
    challan_id NUMBER(19) NOT NULL,
    vehicle_photo CLOB,
    plate_photo CLOB,
    driver_photo CLOB,
    video CLOB,
    gps_location VARCHAR2(60),
    captured_timestamp TIMESTAMP NOT NULL,
    officer_id VARCHAR2(20) NOT NULL,
    police_station VARCHAR2(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_evidence_challan FOREIGN KEY (challan_id) REFERENCES challan(challan_id) ON DELETE CASCADE
);

CREATE INDEX idx_evidence_challan ON evidence(challan_id);

CREATE SEQUENCE evidence_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER evidence_trg
BEFORE INSERT ON evidence
FOR EACH ROW
BEGIN
    IF :NEW.evidence_id IS NULL THEN
        SELECT evidence_seq.NEXTVAL INTO :NEW.evidence_id FROM dual;
    END IF;
END;
/

INSERT INTO evidence (evidence_id, challan_id, gps_location, captured_timestamp, officer_id, police_station) VALUES
(1, 1, '19.1197° N, 72.8469° E', TO_TIMESTAMP('2026-07-15 09:14:00', 'YYYY-MM-DD HH24:MI:SS'), 'PO43210', 'Mumbai Central Traffic Police Station');

INSERT INTO evidence (evidence_id, challan_id, gps_location, captured_timestamp, officer_id, police_station) VALUES
(2, 2, '28.6747° N, 77.3005° E', TO_TIMESTAMP('2026-07-18 11:42:00', 'YYYY-MM-DD HH24:MI:SS'), 'PO11223', 'Delhi RTO Enforcement Wing - Shahdara');

INSERT INTO evidence (evidence_id, challan_id, gps_location, captured_timestamp, officer_id, police_station) VALUES
(3, 3, '23.0394° N, 72.5112° E', TO_TIMESTAMP('2026-07-09 13:05:00', 'YYYY-MM-DD HH24:MI:SS'), 'PO77123', 'Worli Traffic Outpost - Mumbai');

INSERT INTO evidence (evidence_id, challan_id, gps_location, captured_timestamp, officer_id, police_station) VALUES
(4, 4, '28.6685° N, 77.4520° E', TO_TIMESTAMP('2026-07-02 08:22:00', 'YYYY-MM-DD HH24:MI:SS'), 'PO77123', 'Worli Traffic Outpost - Mumbai');

INSERT INTO evidence (evidence_id, challan_id, gps_location, captured_timestamp, officer_id, police_station) VALUES
(5, 5, '19.0596° N, 72.8295° E', TO_TIMESTAMP('2026-06-28 17:55:00', 'YYYY-MM-DD HH24:MI:SS'), 'PO55440', 'Andheri Traffic Division - Mumbai');

-- =============== PUC CERTIFICATE MODULE TABLES ===============

CREATE TABLE puc_certificate (
    puc_id NUMBER(19) PRIMARY KEY,
    certificate_number VARCHAR2(30) NOT NULL UNIQUE,
    vehicle_no VARCHAR2(20) NOT NULL,
    owner_name VARCHAR2(150),
    vehicle_type VARCHAR2(60),
    fuel_type VARCHAR2(30),
    registration_date DATE,
    engine_number VARCHAR2(50),
    chassis_number VARCHAR2(50),
    center_id VARCHAR2(60),
    center_name VARCHAR2(150),
    test_date_time TIMESTAMP NOT NULL,
    emission_norms VARCHAR2(30) DEFAULT 'BHARAT STAGE VI',
    odometer_km NUMBER(10, 1),
    co_percent NUMBER(5, 2),
    hc_ppm NUMBER(6, 2),
    co2_percent NUMBER(5, 2),
    o2_percent NUMBER(5, 2),
    lambda_value NUMBER(4, 2),
    smoke_density NUMBER(5, 2),
    rpm NUMBER(6, 1),
    temperature NUMBER(5, 1),
    pollution_under_control VARCHAR2(5) DEFAULT 'Yes' NOT NULL,
    valid_from DATE NOT NULL,
    valid_upto DATE NOT NULL,
    status VARCHAR2(20) DEFAULT 'Valid',
    remarks CLOB,
    issued_by_user_id NUMBER(19),
    issued_by_name VARCHAR2(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_puc_vehicle ON puc_certificate(vehicle_no);
CREATE INDEX idx_puc_center ON puc_certificate(center_id);
CREATE INDEX idx_puc_status ON puc_certificate(status);
CREATE INDEX idx_puc_valid_upto ON puc_certificate(valid_upto);

CREATE SEQUENCE puc_certificate_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER puc_certificate_trg
BEFORE INSERT ON puc_certificate
FOR EACH ROW
BEGIN
    IF :NEW.puc_id IS NULL THEN
        SELECT puc_certificate_seq.NEXTVAL INTO :NEW.puc_id FROM dual;
    END IF;
    :NEW.updated_at := SYSTIMESTAMP;
END;
/

INSERT INTO puc_certificate (puc_id, certificate_number, vehicle_no, owner_name, vehicle_type, fuel_type, registration_date, engine_number, chassis_number, center_id, center_name, test_date_time, emission_norms, odometer_km, co_percent, hc_ppm, co2_percent, lambda_value, pollution_under_control, valid_from, valid_upto, status, remarks, issued_by_user_id, issued_by_name) VALUES
(1, 'MH13150725P1002001', 'MH-04 AB 1234', 'Amit Sharma', 'Car / Jeep', 'Petrol', TO_DATE('2020-05-15', 'YYYY-MM-DD'), 'EN37FE881234', 'CH12345678MSA1', 'PUC10024', 'Mumbai PUC Testing Center - Andheri', TO_TIMESTAMP('2025-07-15 10:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'BHARAT STAGE VI', 42350.0, 0.22, 38, 12.4, 0.99, 'Yes', TO_DATE('2025-07-15', 'YYYY-MM-DD'), TO_DATE('2026-07-14', 'YYYY-MM-DD'), 'Expired', 'All emission parameters within limits.', 3, 'Suresh Patel');

INSERT INTO puc_certificate (puc_id, certificate_number, vehicle_no, owner_name, vehicle_type, fuel_type, registration_date, engine_number, chassis_number, center_id, center_name, test_date_time, emission_norms, odometer_km, co_percent, hc_ppm, co2_percent, lambda_value, pollution_under_control, valid_from, valid_upto, status, remarks, issued_by_user_id, issued_by_name) VALUES
(2, 'MH13020526P1002002', 'MH-12 CD 5678', 'Priya Verma', 'Two Wheeler', 'Petrol', TO_DATE('2021-09-22', 'YYYY-MM-DD'), 'ENHW21K78901', 'CH78901234HW12', 'PUC10024', 'Mumbai PUC Testing Center - Andheri', TO_TIMESTAMP('2026-05-02 14:12:00', 'YYYY-MM-DD HH24:MI:SS'), 'BHARAT STAGE VI', 18920.0, 0.18, 42, 11.8, 0.97, 'Yes', TO_DATE('2026-05-02', 'YYYY-MM-DD'), TO_DATE('2027-05-01', 'YYYY-MM-DD'), 'Valid', 'Excellent condition, well maintained.', 3, 'Suresh Patel');

INSERT INTO puc_certificate (puc_id, certificate_number, vehicle_no, owner_name, vehicle_type, fuel_type, registration_date, engine_number, chassis_number, center_id, center_name, test_date_time, emission_norms, odometer_km, co_percent, hc_ppm, co2_percent, lambda_value, pollution_under_control, valid_from, valid_upto, status, remarks, issued_by_user_id, issued_by_name) VALUES
(3, 'MH13140726P1002003', 'MH-03 ZA 1120', 'Rajesh Patel', 'HMV — Truck / Bus', 'Diesel', TO_DATE('2019-03-05', 'YYYY-MM-DD'), 'ENTM19D11220', 'CH11220033TM01', 'PUC10024', 'Mumbai PUC Testing Center - Andheri', TO_TIMESTAMP('2026-07-14 09:45:00', 'YYYY-MM-DD HH24:MI:SS'), 'BHARAT STAGE VI', 128400.0, 0.38, 210, 13.1, 1.02, 'Yes', TO_DATE('2026-07-14', 'YYYY-MM-DD'), TO_DATE('2027-07-13', 'YYYY-MM-DD'), 'Valid', 'Smoke opacity within permissible limits.', 3, 'Suresh Patel');

INSERT INTO puc_certificate (puc_id, certificate_number, vehicle_no, owner_name, vehicle_type, fuel_type, registration_date, engine_number, chassis_number, center_id, center_name, test_date_time, emission_norms, odometer_km, co_percent, hc_ppm, co2_percent, lambda_value, pollution_under_control, valid_from, valid_upto, status, remarks, issued_by_user_id, issued_by_name) VALUES
(4, 'DL10010826P5678001', 'DL-10 XY 8832', 'Vikram Singh', 'Car / Jeep', 'Diesel', TO_DATE('2022-01-28', 'YYYY-MM-DD'), 'ENHM22D99800', 'CH99800765HM02', 'PUC10023', 'Delhi Emission Testing Lab', TO_TIMESTAMP('2026-08-01 11:20:00', 'YYYY-MM-DD HH24:MI:SS'), 'BHARAT STAGE VI', 35670.0, 0.25, 78, 12.8, 1.00, 'Yes', TO_DATE('2026-08-01', 'YYYY-MM-DD'), TO_DATE('2027-07-31', 'YYYY-MM-DD'), 'Valid', 'All readings within BS VI limits.', 4, 'Vikram Singh');

INSERT INTO puc_certificate (puc_id, certificate_number, vehicle_no, owner_name, vehicle_type, fuel_type, registration_date, engine_number, chassis_number, center_id, center_name, test_date_time, emission_norms, odometer_km, co_percent, hc_ppm, co2_percent, lambda_value, pollution_under_control, valid_from, valid_upto, status, remarks, issued_by_user_id, issued_by_name) VALUES
(5, 'KA05010626P1002001', 'KA-05 DE 7788', 'Anita Desai', 'Car / Jeep', 'Electric', TO_DATE('2023-02-14', 'YYYY-MM-DD'), 'EN-TM23E0012', 'CHTM001234EV03', 'PUC10025', 'Solapur Center', TO_TIMESTAMP('2026-06-01 16:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'BHARAT STAGE VI', 9840.0, NULL, NULL, NULL, NULL, 'Yes', TO_DATE('2026-06-01', 'YYYY-MM-DD'), TO_DATE('2028-05-31', 'YYYY-MM-DD'), 'Valid', 'Electric Vehicle - Emission exempt, validity 2 years.', 3, 'Suresh Patel');

COMMIT;

-- =============== DIAGNOSTIC QUERIES ===============

-- Check all objects status
SELECT object_name, object_type, status
FROM user_objects
WHERE object_type IN ('TRIGGER','TABLE','SEQUENCE')
ORDER BY object_type, object_name;

-- Check trigger status specifically
SELECT trigger_name, status
FROM user_triggers
ORDER BY trigger_name;

-- Verify all tables were created
SELECT table_name
FROM user_tables
WHERE table_name IN ('USERS', 'VIOLATION_MASTER', 'OWNER', 'VEHICLE', 'POLICE_OFFICER', 'CHALLAN', 'EVIDENCE', 'PUC_CERTIFICATE')
ORDER BY table_name;
