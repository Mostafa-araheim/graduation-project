-- ============================================================
-- data.sql — Seed data matching schema exactly
-- Insert order respects all foreign key constraints
-- ============================================================

-- ────────────────────────────────────────────────────────────
-- 1. USERS  (users: user_id, email, name, phone, created_at)
-- ────────────────────────────────────────────────────────────
INSERT INTO users (email, name, phone) VALUES
                                           ('moatz.5668@gmail.com',    'Moatz Ahmed',       '01143323738'),
                                           ('mostafa.araheim@gmail.com',  'Mostafa Abdelrahim','01020520880'),
                                           ('mohamed.omar@gmail.com',    'Mohamed Omar',       '01234567890'),
                                           ('mohanadazazy@gmail.com',       'Mohanad Tarek',       '01187654320');
-- user_id sequence: 1–15
-- 1  = owner1, 2  = owner2, 3  = owner3
-- 4  = pharmacist1, 5  = pharmacist2, 6  = pharmacist3
-- 7–15 = customers


-- ────────────────────────────────────────────────────────────
-- 2. USER_ROLES
-- ────────────────────────────────────────────────────────────
INSERT INTO user_roles (user_id, role) VALUES
                                           (1,  'ROLE_OWNER'),
                                           (2,  'ROLE_OWNER'),
                                           (3, 'ROLE_CUSTOMER'),
                                           (4, 'ROLE_CUSTOMER');


-- ────────────────────────────────────────────────────────────
-- 3. PROFILES
-- ────────────────────────────────────────────────────────────
INSERT INTO owner_profile (user_id) VALUES (1), (2);


INSERT INTO customer_profile (user_id) VALUES
                                           (3), (4);


-- ────────────────────────────────────────────────────────────
-- 4. USER_ADDRESS
-- ────────────────────────────────────────────────────────────
INSERT INTO user_address (user_id, street, city, country, postal_code, apartment_number) VALUES
                                                                                             (3,  '15 Tahrir Square',        'Cairo',       'Egypt', '11511', 'Apt 3'),
                                                                                             (4,  '42 Corniche El Nile',     'Cairo',       'Egypt', '11512', 'Apt 7');


-- ────────────────────────────────────────────────────────────
-- 5. BRANDS
-- ────────────────────────────────────────────────────────────
INSERT INTO brand (brand_name, brand_image) VALUES
                                                ('Pfizer',              'pfizer.png'),
                                                ('Novartis',            'novartis.png'),
                                                ('GlaxoSmithKline',     'gsk.png'),
                                                ('Bayer',               'bayer.png'),
                                                ('Sanofi',              'sanofi.png'),
                                                ('Roche',               'roche.png'),
                                                ('AstraZeneca',         'astrazeneca.png'),
                                                ('Johnson & Johnson',   'jnj.png'),
                                                ('Merck',               'merck.png'),
                                                ('Abbott',              'abbott.png');
-- brand_id sequence: 1–10


-- ────────────────────────────────────────────────────────────
-- 6. CATEGORIES
-- ────────────────────────────────────────────────────────────
INSERT INTO category (category_name, image_url) VALUES
('Pain Relief', '/images/categories/knee-pad.png'),
('Antibiotics', '/images/categories/antibiotic.png'),
('Vitamins & Supplements', '/images/categories/supplement.png'),
('Cardiovascular', '/images/categories/cardio.png'),
('Diabetes', '/images/categories/blood-test.png'),
('Dermatology', '/images/categories/dermatology.png'),
('Respiratory', '/images/categories/respiratory-system.png'),
('Gastrointestinal', '/images/categories/intestine.png'),
('Mental Health', '/images/categories/mental-health.png'),
('Eye & Ear Care', '/images/categories/sensorineural.png');


-- ────────────────────────────────────────────────────────────
-- 7. PRODUCTS
-- ────────────────────────────────────────────────────────────
INSERT INTO product (name, description, dosage_form, strength, manufacturer, brand_id, category_id, requires_prescription) VALUES
                                                                                                                               ('Panadol Extra',        'Paracetamol + Caffeine for pain relief',    'Tablet',   '500mg/65mg',  'GSK Egypt',        3, 1,  false),
                                                                                                                               ('Brufen',               'Ibuprofen anti-inflammatory',               'Tablet',   '400mg',       'Abbott Egypt',     10, 1, false),
                                                                                                                               ('Amoxicillin',          'Broad-spectrum antibiotic',                 'Capsule',  '500mg',       'Pfizer Egypt',     1, 2,  true),
                                                                                                                               ('Augmentin',            'Amoxicillin + Clavulanate antibiotic',      'Tablet',   '875mg/125mg', 'GSK Egypt',        3, 2,  true),
                                                                                                                               ('Vitamin C',            'Ascorbic acid immune support',              'Tablet',   '1000mg',      'Bayer Egypt',      4, 3,  false),
                                                                                                                               ('Omega-3 Fish Oil',     'Essential fatty acids supplement',          'Capsule',  '1000mg',      'Novartis Egypt',   2, 3,  false),
                                                                                                                               ('Concor',               'Bisoprolol for hypertension',               'Tablet',   '5mg',         'Merck Egypt',      9, 4,  true),
                                                                                                                               ('Glucophage',           'Metformin for type 2 diabetes',             'Tablet',   '500mg',       'Merck Egypt',      9, 5,  true),
                                                                                                                               ('Betnovate',            'Betamethasone cream for skin conditions',   'Cream',    '0.1%',        'GSK Egypt',        3, 6,  true),
                                                                                                                               ('Ventolin',             'Salbutamol bronchodilator inhaler',         'Inhaler',  '100mcg',      'GSK Egypt',        3, 7,  true),
                                                                                                                               ('Nexium',               'Esomeprazole for acid reflux',              'Capsule',  '20mg',        'AstraZeneca',      7, 8,  false),
                                                                                                                               ('Zoloft',               'Sertraline antidepressant',                 'Tablet',   '50mg',        'Pfizer Egypt',     1, 9,  true),
                                                                                                                               ('Visine',               'Eye drops for redness relief',              'Drops',    '0.05%',       'JnJ Egypt',        8, 10, false),
                                                                                                                               ('Nurofen Syrup',        'Ibuprofen suspension for children',         'Syrup',    '100mg/5ml',   'Roche Egypt',      6, 1,  false),
                                                                                                                               ('Ciprobay',             'Ciprofloxacin antibiotic',                  'Tablet',   '500mg',       'Bayer Egypt',      4, 2,  true),
                                                                                                                               ('Zinc Supplement',      'Zinc for immune function',                  'Tablet',   '50mg',        'Sanofi Egypt',     5, 3,  false),
                                                                                                                               ('Aspirin',              'Acetylsalicylic acid blood thinner',        'Tablet',   '100mg',       'Bayer Egypt',      4, 4,  false),
                                                                                                                               ('Insulin Glargine',     'Long-acting insulin for diabetes',          'Injection','100 IU/ml',   'Sanofi Egypt',     5, 5,  true),
                                                                                                                               ('Clindamycin Gel',      'Topical antibiotic for acne',              'Ointment', '1%',          'Pfizer Egypt',     1, 6,  true),
                                                                                                                               ('Prednisolone',         'Corticosteroid for inflammation',           'Tablet',   '5mg',         'Novartis Egypt',   2, 7,  true);
-- product_id sequence: 1–20

INSERT INTO product_image (product_id, image_url, sort_order) VALUES
                                                                  ((SELECT product_id FROM product WHERE name = 'Panadol Extra'), '/images/panadol-extra-1.jpg', 1),
                                                                  ((SELECT product_id FROM product WHERE name = 'Panadol Extra'), '/images/panadol-extra-2.jpg', 2),

                                                                  ((SELECT product_id FROM product WHERE name = 'Brufen'), '/images/brufen-1.jpg', 1),
                                                                  ((SELECT product_id FROM product WHERE name = 'Brufen'), '/images/brufen-2.jpg', 2),

                                                                  ((SELECT product_id FROM product WHERE name = 'Amoxicillin'), '/images/amoxicillin-1.jpg', 1),
                                                                  ((SELECT product_id FROM product WHERE name = 'Augmentin'), '/images/augmentin-1.jpg', 1),

                                                                  ((SELECT product_id FROM product WHERE name = 'Vitamin C'), '/images/vitamin-c-1.jpg', 1),
                                                                  ((SELECT product_id FROM product WHERE name = 'Omega-3 Fish Oil'), '/images/omega-3-fish-oil-1.jpg', 1),

                                                                  ((SELECT product_id FROM product WHERE name = 'Concor'), '/images/concor-1.jpg', 1),
                                                                  ((SELECT product_id FROM product WHERE name = 'Glucophage'), '/images/glucophage-1.jpg', 1);
-- ────────────────────────────────────────────────────────────
-- 8. PHARMACIES
-- ────────────────────────────────────────────────────────────
INSERT INTO pharmacy (owner_user_id, name, image_url,
                      latitude, longitude, opening_time, closing_time,
                      is_24_hours, average_rating, rating_count, review_count,
                      location)
VALUES
-- Pharmacies owned by owner 1 (Ahmed Hassan)
(1, 'Al-Yasmin Pharmacy',
    '/images/pharmacies/pharma1.jpeg',
    30.025428476888607, 31.444307661557232,
    '08:00:00', '23:00:00', false,
    4.20, 35, 12,
    ST_SetSRID(ST_MakePoint(31.444307661557232, 30.025428476888607), 4326)),

(1, 'Air Force Specialized Hospital Pharmacy',
    '/images/pharmacies/pharma2.jpeg',
    30.01764808857284, 31.432789553182836,
    '07:00:00', '21:00:00', false,
    4.50, 42, 18,
    ST_SetSRID(ST_MakePoint(31.432789553182836, 30.01764808857284), 4326)),

-- Pharmacies owned by owner 2 (Mona Ibrahim)
(2, 'Ezbet El Waldaa Pharmacy',
    '/images/pharmacies/pharma3.jpeg',
    29.864707, 31.304053,
    '09:00:00', '22:00:00', false,
    3.80, 20, 7,
    ST_SetSRID(ST_MakePoint(31.304053, 29.864707), 4326)),

(2, 'El Maadi Charity Pharmacy',
    '/images/pharmacies/pharma4.jpeg',
    29.954417, 31.260575,
    '08:30:00', '20:30:00', false,
    4.10, 55, 22,
    ST_SetSRID(ST_MakePoint(31.260575, 29.954417), 4326)),

-- Pharmacies owned by owner 3 (Tarek Ali)
(2, 'Night Calm Pharmacy',
    '/images/pharmacies/pharma5.jpg',
    30.072625, 31.347434,
    '00:00:00', '00:00:00', true,
    4.70, 88, 40,
    ST_SetSRID(ST_MakePoint(31.347434, 30.072625), 4326)),

(2, 'Al Khalifa Pharmacy',
    '/images/pharmacies/pharma6.jpg',
    29.865418, 31.302829,
    '09:00:00', '22:00:00', false,
    3.90, 18, 6,
    ST_SetSRID(ST_MakePoint(31.302829, 29.865418), 4326)),

(2, 'Al Hekemdar Pharmacy',
    '/images/pharmacies/pharma7.jpg',
    29.861033, 31.306669,
    '08:00:00', '23:00:00', false,
    4.30, 27, 9,
    ST_SetSRID(ST_MakePoint(31.306669, 29.861033), 4326)),

-- Pharmacies owned by owner 4 (Heba Mahmoud)
(1, 'Ain Helwan Pharmacy',
    '/images/pharmacies/pharma8.jpg',
    29.859600, 31.320507,
    '00:00:00', '00:00:00', true,
    4.60, 70, 30,
    ST_SetSRID(ST_MakePoint(31.320507, 29.859600), 4326)),

(1, 'El Naseem Pharmacy',
    '/images/pharmacies/pharma9.jpg',
    29.859827, 31.308671,
    '09:00:00', '22:00:00', false,
    4.00, 15, 5,
    ST_SetSRID(ST_MakePoint(31.308671, 29.859827), 4326)),

(1, 'Al Hawamdeya General Pharmacy',
    '/images/pharmacies/pharma10.jpg',
    29.888915, 31.272435,
    '08:00:00', '21:00:00', false,
    3.70, 12, 3,
    ST_SetSRID(ST_MakePoint(31.272435, 29.888915), 4326)),

-- Pharmacies owned by owner 5 (Omar Sayed)
(2, 'Mar Girgis General Pharmacy',
    '/images/pharmacies/pharma11.jpg',
    29.893083, 31.303577,
    '08:00:00', '22:00:00', false,
    4.20, 22, 8,
    ST_SetSRID(ST_MakePoint(31.303577, 29.893083), 4326)),

(2, 'Mostawsaf Pharmacy',
    '/images/pharmacies/pharma12.jpeg',
    29.852241, 31.331241,
    '00:00:00', '00:00:00', true,
    4.80, 95, 45,
    ST_SetSRID(ST_MakePoint(31.331241, 29.852241), 4326)),

(2, 'Kher Pharmacy',
    '/images/pharmacies/pharma13.jpg',
    29.861427, 31.299040,
    '09:00:00', '23:00:00', false,
    4.10, 30, 11,
    ST_SetSRID(ST_MakePoint(31.299040, 29.861427), 4326)),

-- Unowned pharmacies (no owner_user_id)
(NULL, 'Heliopolis Pharmacy',
    '/images/pharmacies/pharma14.jpg',
    30.112210, 31.346008,
    '08:00:00', '23:00:00', false,
    4.40, 60, 25,
    ST_SetSRID(ST_MakePoint(31.346008, 30.112210), 4326)),

(NULL, 'Dusit Care Pharmacy',
    '/images/pharmacies/pharma15.jpg',
    30.025757, 31.458767,
    '00:00:00', '00:00:00', true,
    4.90, 110, 55,
    ST_SetSRID(ST_MakePoint(31.458767, 30.025757), 4326)),

(NULL, 'Dokki Ibn Sina Pharmacy',
    '/images/pharmacies/pharma16.webp',
    30.040366, 31.209487,
    '08:00:00', '22:00:00', false,
    4.30, 48, 20,
    ST_SetSRID(ST_MakePoint(31.209487, 30.040366), 4326)),

(NULL, 'Al Esaaf Pharmacy',
    '/images/pharmacies/pharma17.jpeg',
    30.053698, 31.238401,
    '00:00:00', '00:00:00', true,
    4.60, 85, 38,
    ST_SetSRID(ST_MakePoint(31.238401, 30.053698), 4326)),

(NULL, 'St. Teresa Pharmacy',
    '/images/pharmacies/pharma18.jpeg',
    30.088143, 31.245356,
    '08:00:00', '21:00:00', false,
    4.10, 32, 13,
    ST_SetSRID(ST_MakePoint(31.245356, 30.088143), 4326));


-- ────────────────────────────────────────────────────────────
-- 9. PHARMACY_ADDRESS
-- ────────────────────────────────────────────────────────────
INSERT INTO pharmacy_address (pharmacy_id, street, city, country, postal_code, apartment_number) VALUES
(1, '10 Kasr El Aini Street', 'Cairo', 'Egypt', '11511', NULL),
(2, '5 Corniche El Nile', 'Cairo', 'Egypt', '11512', NULL),
(3, '88 Ramsis Street', 'Cairo', 'Egypt', '11711', NULL),
(4, '15 Maadi Street', 'Cairo', 'Egypt', '11431', NULL),
(5, '12 Nasr City Road', 'Cairo', 'Egypt', '11765', NULL),
(6, '22 Khalifa Street', 'Cairo', 'Egypt', '11562', NULL),
(7, '9 Hekemdar Avenue', 'Cairo', 'Egypt', '11563', NULL),
(8, '45 Helwan Main Road', 'Cairo', 'Egypt', '11722', NULL),
(9, '33 Naseem Street', 'Cairo', 'Egypt', '11723', NULL),
(10, '19 Hawamdeya Square', 'Giza', 'Egypt', '12511', NULL),
(11, '21 Mar Girgis St', 'Cairo', 'Egypt', '11611', NULL),
(12, '10 Mostawsaf Alley', 'Cairo', 'Egypt', '11612', NULL),
(13, '5 Kher Road', 'Cairo', 'Egypt', '11613', NULL),
(14, '90 Heliopolis Ave', 'Cairo', 'Egypt', '11341', NULL),
(15, '15 Dusit Thani St', 'Cairo', 'Egypt', '11835', NULL),
(16, '24 Dokki Street', 'Giza', 'Egypt', '12311', NULL),
(17, '30 Ramsis Extension', 'Cairo', 'Egypt', '11522', NULL),
(18, '18 St. Teresa St', 'Cairo', 'Egypt', '11646', NULL);


-- ────────────────────────────────────────────────────────────
-- 10. INVENTORY  (one per pharmacy)
-- ────────────────────────────────────────────────────────────
INSERT INTO inventory (pharmacy_id) VALUES
    (1), (2), (3), (4), (5), (6), (7), (8), (9), (10),
    (11), (12), (13), (14), (15), (16), (17), (18);
-- inventory_id sequence: 1–18 (inventory_id matches pharmacy_id)


-- ────────────────────────────────────────────────────────────
-- 11. PHARMACY_STAFF
-- ────────────────────────────────────────────────────────────



-- ────────────────────────────────────────────────────────────
-- 12. PHARMACY_PRODUCT
-- ────────────────────────────────────────────────────────────

-- Pharmacy 1 (inventory_pharmacy_id=1, pharmacy_id=1)
INSERT INTO pharmacy_product (inventory_pharmacy_id, pharmacy_id, product_id, price, quantity, availability_status) VALUES
                                                                                                                        (1, 1, 1,  25.50,  200, 'Available'),
                                                                                                                        (1, 1, 2,  35.00,  150, 'Available'),
                                                                                                                        (1, 1, 3,  45.00,   80, 'Available'),
                                                                                                                        (1, 1, 5,  30.00,  300, 'Available'),
                                                                                                                        (1, 1, 7,  55.00,   60, 'Available'),
                                                                                                                        (1, 1, 8,  40.00,  120, 'Available'),
                                                                                                                        (1, 1, 10, 95.00,   40, 'LimitedSupply'),
                                                                                                                        (1, 1, 11, 65.00,   90, 'Available'),
                                                                                                                        (1, 1, 13, 20.00,  250, 'Available'),
                                                                                                                        (1, 1, 17, 15.00,  400, 'Available');

-- Pharmacy 2 (inventory_pharmacy_id=2, pharmacy_id=2)
INSERT INTO pharmacy_product (inventory_pharmacy_id, pharmacy_id, product_id, price, quantity, availability_status) VALUES
                                                                                                                        (2, 2, 1,  24.00,  180, 'Available'),
                                                                                                                        (2, 2, 4,  85.00,   50, 'Available'),
                                                                                                                        (2, 2, 6,  70.00,  100, 'Available'),
                                                                                                                        (2, 2, 9,  50.00,   70, 'Available'),
                                                                                                                        (2, 2, 12, 120.00,  30, 'LimitedSupply'),
                                                                                                                        (2, 2, 14, 28.00,  200, 'Available'),
                                                                                                                        (2, 2, 15, 60.00,   90, 'Available'),
                                                                                                                        (2, 2, 16, 22.00,  300, 'Available'),
                                                                                                                        (2, 2, 18, 250.00,  20, 'LimitedSupply'),
                                                                                                                        (2, 2, 20, 35.00,  110, 'Available');

-- Pharmacy 3 (inventory_pharmacy_id=3, pharmacy_id=3)
INSERT INTO pharmacy_product (inventory_pharmacy_id, pharmacy_id, product_id, price, quantity, availability_status) VALUES
                                                                                                                        (3, 3, 2,  34.00,  160, 'Available'),
                                                                                                                        (3, 3, 3,  44.00,  100, 'Available'),
                                                                                                                        (3, 3, 5,  29.00,  350, 'Available'),
                                                                                                                        (3, 3, 7,  54.00,   75, 'Available'),
                                                                                                                        (3, 3, 8,  39.00,  130, 'Available'),
                                                                                                                        (3, 3, 10, 92.00,   55, 'Available'),
                                                                                                                        (3, 3, 11, 63.00,   80, 'Available'),
                                                                                                                        (3, 3, 13, 19.00,  220, 'Available'),
                                                                                                                        (3, 3, 19, 45.00,   60, 'Available'),
                                                                                                                        (3, 3, 20, 34.00,  140, 'Available');

-- Pharmacy 4 (inventory_pharmacy_id=4, pharmacy_id=4)
INSERT INTO pharmacy_product (inventory_pharmacy_id, pharmacy_id, product_id, price, quantity, availability_status) VALUES
                                                                                                                        (4, 4, 1,  26.00,  170, 'Available'),
                                                                                                                        (4, 4, 3,  46.50,   60, 'Available'),
                                                                                                                        (4, 4, 4,  88.00,   45, 'Available'),
                                                                                                                        (4, 4, 6,  72.00,   85, 'Available'),
                                                                                                                        (4, 4, 9,  52.00,   55, 'Available'),
                                                                                                                        (4, 4, 11, 66.00,   70, 'Available'),
                                                                                                                        (4, 4, 14, 30.00,  180, 'Available'),
                                                                                                                        (4, 4, 16, 23.00,  260, 'Available'),
                                                                                                                        (4, 4, 18, 255.00,  15, 'LimitedSupply'),
                                                                                                                        (4, 4, 20, 36.00,  100, 'Available');

-- Pharmacy 5 (inventory_pharmacy_id=5, pharmacy_id=5)
INSERT INTO pharmacy_product (inventory_pharmacy_id, pharmacy_id, product_id, price, quantity, availability_status) VALUES
                                                                                                                        (5, 5, 1,  24.50,  220, 'Available'),
                                                                                                                        (5, 5, 2,  33.00,  180, 'Available'),
                                                                                                                        (5, 5, 5,  28.00,  400, 'Available'),
                                                                                                                        (5, 5, 7,  53.00,   90, 'Available'),
                                                                                                                        (5, 5, 10, 90.00,   35, 'LimitedSupply'),
                                                                                                                        (5, 5, 12, 115.00,  25, 'LimitedSupply'),
                                                                                                                        (5, 5, 15, 58.00,  100, 'Available'),
                                                                                                                        (5, 5, 17, 14.00,  500, 'Available'),
                                                                                                                        (5, 5, 19, 43.00,   70, 'Available');

-- Pharmacy 6 (inventory_pharmacy_id=6, pharmacy_id=6)
INSERT INTO pharmacy_product (inventory_pharmacy_id, pharmacy_id, product_id, price, quantity, availability_status) VALUES
                                                                                                                        (6, 6, 2,  36.00,  140, 'Available'),
                                                                                                                        (6, 6, 4,  84.00,   55, 'Available'),
                                                                                                                        (6, 6, 6,  68.00,  110, 'Available'),
                                                                                                                        (6, 6, 8,  41.00,  100, 'Available'),
                                                                                                                        (6, 6, 9,  48.00,   80, 'Available'),
                                                                                                                        (6, 6, 13, 21.00,  200, 'Available'),
                                                                                                                        (6, 6, 15, 62.00,   75, 'Available'),
                                                                                                                        (6, 6, 17, 16.00,  350, 'Available'),
                                                                                                                        (6, 6, 19, 47.00,   50, 'Available'),
                                                                                                                        (6, 6, 20, 33.00,  120, 'Available');

-- Pharmacy 7 (inventory_pharmacy_id=7, pharmacy_id=7)
INSERT INTO pharmacy_product (inventory_pharmacy_id, pharmacy_id, product_id, price, quantity, availability_status) VALUES
                                                                                                                        (7, 7, 1,  25.00,  190, 'Available'),
                                                                                                                        (7, 7, 3,  43.00,   90, 'Available'),
                                                                                                                        (7, 7, 5,  31.00,  280, 'Available'),
                                                                                                                        (7, 7, 7,  56.00,   50, 'Available'),
                                                                                                                        (7, 7, 10, 93.00,   30, 'LimitedSupply'),
                                                                                                                        (7, 7, 11, 64.00,   85, 'Available'),
                                                                                                                        (7, 7, 14, 27.00,  210, 'Available'),
                                                                                                                        (7, 7, 16, 21.00,  320, 'Available');

-- Pharmacy 8 (inventory_pharmacy_id=8, pharmacy_id=8)
INSERT INTO pharmacy_product (inventory_pharmacy_id, pharmacy_id, product_id, price, quantity, availability_status) VALUES
                                                                                                                        (8, 8, 2,  35.50,  155, 'Available'),
                                                                                                                        (8, 8, 4,  86.00,   40, 'Available'),
                                                                                                                        (8, 8, 6,  71.00,   95, 'Available'),
                                                                                                                        (8, 8, 8,  38.00,  140, 'Available'),
                                                                                                                        (8, 8, 9,  51.00,   65, 'Available'),
                                                                                                                        (8, 8, 12, 118.00,  28, 'LimitedSupply'),
                                                                                                                        (8, 8, 13, 20.50,  230, 'Available'),
                                                                                                                        (8, 8, 15, 59.00,   88, 'Available'),
                                                                                                                        (8, 8, 18, 248.00,  18, 'LimitedSupply'),
                                                                                                                        (8, 8, 20, 35.50,  105, 'Available');

-- Pharmacy 9 (inventory_pharmacy_id=9, pharmacy_id=9)
INSERT INTO pharmacy_product (inventory_pharmacy_id, pharmacy_id, product_id, price, quantity, availability_status) VALUES
                                                                                                                        (9, 9, 1,  23.50,  240, 'Available'),
                                                                                                                        (9, 9, 3,  44.50,   75, 'Available'),
                                                                                                                        (9, 9, 5,  27.50,  320, 'Available'),
                                                                                                                        (9, 9, 7,  54.50,   65, 'Available'),
                                                                                                                        (9, 9, 10, 91.00,   45, 'Available'),
                                                                                                                        (9, 9, 11, 62.00,   95, 'Available'),
                                                                                                                        (9, 9, 13, 18.50,  270, 'Available'),
                                                                                                                        (9, 9, 17, 14.50,  420, 'Available'),
                                                                                                                        (9, 9, 19, 44.00,   55, 'Available');

-- Pharmacy 10 (inventory_pharmacy_id=10, pharmacy_id=10)
INSERT INTO pharmacy_product (inventory_pharmacy_id, pharmacy_id, product_id, price, quantity, availability_status) VALUES
                                                                                                                        (10, 10, 2,  34.50,  165, 'Available'),
                                                                                                                        (10, 10, 4,  83.00,   60, 'Available'),
                                                                                                                        (10, 10, 6,  69.00,  105, 'Available'),
                                                                                                                        (10, 10, 8,  40.50,  115, 'Available'),
                                                                                                                        (10, 10, 9,  49.00,   75, 'Available'),
                                                                                                                        (10, 10, 12, 122.00,  22, 'LimitedSupply'),
                                                                                                                        (10, 10, 14, 29.00,  190, 'Available'),
                                                                                                                        (10, 10, 16, 22.50,  280, 'Available'),
                                                                                                                        (10, 10, 18, 252.00,  12, 'LimitedSupply'),
                                                                                                                        (10, 10, 20, 34.50,  125, 'Available');

-- Pharmacy 11 (inventory_pharmacy_id=11, pharmacy_id=11)
INSERT INTO pharmacy_product (inventory_pharmacy_id, pharmacy_id, product_id, price, quantity, availability_status) VALUES
                                                                                                                        (11, 11, 1,  25.00,  200, 'Available'),
                                                                                                                        (11, 11, 2,  34.00,  175, 'Available'),
                                                                                                                        (11, 11, 3,  45.50,   70, 'Available'),
                                                                                                                        (11, 11, 5,  29.50,  310, 'Available'),
                                                                                                                        (11, 11, 7,  55.50,   55, 'Available'),
                                                                                                                        (11, 11, 10, 94.00,   38, 'LimitedSupply'),
                                                                                                                        (11, 11, 11, 64.50,   82, 'Available'),
                                                                                                                        (11, 11, 15, 61.00,   78, 'Available'),
                                                                                                                        (11, 11, 19, 46.00,   48, 'Available');

-- Pharmacy 12 (inventory_pharmacy_id=12, pharmacy_id=12)
INSERT INTO pharmacy_product (inventory_pharmacy_id, pharmacy_id, product_id, price, quantity, availability_status) VALUES
                                                                                                                        (12, 12, 1,  24.00,  250, 'Available'),
                                                                                                                        (12, 12, 4,  87.00,   42, 'Available'),
                                                                                                                        (12, 12, 6,  73.00,   90, 'Available'),
                                                                                                                        (12, 12, 8,  39.50,  125, 'Available'),
                                                                                                                        (12, 12, 9,  50.50,   60, 'Available'),
                                                                                                                        (12, 12, 12, 119.00,  32, 'LimitedSupply'),
                                                                                                                        (12, 12, 13, 19.50,  240, 'Available'),
                                                                                                                        (12, 12, 16, 21.50,  290, 'Available'),
                                                                                                                        (12, 12, 17, 15.50,  380, 'Available'),
                                                                                                                        (12, 12, 20, 33.50,  135, 'Available');

-- Pharmacy 13 (inventory_pharmacy_id=13, pharmacy_id=13)
INSERT INTO pharmacy_product (inventory_pharmacy_id, pharmacy_id, product_id, price, quantity, availability_status) VALUES
                                                                                                                        (13, 13, 2,  35.50,  145, 'Available'),
                                                                                                                        (13, 13, 3,  43.50,   85, 'Available'),
                                                                                                                        (13, 13, 5,  30.50,  290, 'Available'),
                                                                                                                        (13, 13, 7,  56.50,   48, 'Available'),
                                                                                                                        (13, 13, 10, 93.50,   42, 'LimitedSupply'),
                                                                                                                        (13, 13, 14, 28.50,  195, 'Available'),
                                                                                                                        (13, 13, 15, 59.50,   85, 'Available'),
                                                                                                                        (13, 13, 18, 245.00,  16, 'LimitedSupply'),
                                                                                                                        (13, 13, 19, 44.50,   62, 'Available');

-- Pharmacy 14 (inventory_pharmacy_id=14, pharmacy_id=14)
INSERT INTO pharmacy_product (inventory_pharmacy_id, pharmacy_id, product_id, price, quantity, availability_status) VALUES
                                                                                                                        (14, 14, 1,  24.50,  210, 'Available'),
                                                                                                                        (14, 14, 2,  33.50,  170, 'Available'),
                                                                                                                        (14, 14, 4,  82.00,   58, 'Available'),
                                                                                                                        (14, 14, 6,  67.00,  115, 'Available'),
                                                                                                                        (14, 14, 8,  42.00,  110, 'Available'),
                                                                                                                        (14, 14, 11, 63.50,   88, 'Available'),
                                                                                                                        (14, 14, 13, 20.00,  235, 'Available'),
                                                                                                                        (14, 14, 16, 22.00,  310, 'Available'),
                                                                                                                        (14, 14, 17, 13.50,  450, 'Available'),
                                                                                                                        (14, 14, 20, 35.00,  115, 'Available');

-- Pharmacy 15 (inventory_pharmacy_id=15, pharmacy_id=15)
INSERT INTO pharmacy_product (inventory_pharmacy_id, pharmacy_id, product_id, price, quantity, availability_status) VALUES
                                                                                                                        (15, 15, 1,  23.00,  260, 'Available'),
                                                                                                                        (15, 15, 3,  42.00,  110, 'Available'),
                                                                                                                        (15, 15, 5,  27.00,  380, 'Available'),
                                                                                                                        (15, 15, 7,  52.00,   80, 'Available'),
                                                                                                                        (15, 15, 9,  47.00,   90, 'Available'),
                                                                                                                        (15, 15, 10, 89.00,   50, 'Available'),
                                                                                                                        (15, 15, 12, 116.00,  35, 'LimitedSupply'),
                                                                                                                        (15, 15, 14, 26.00,  220, 'Available'),
                                                                                                                        (15, 15, 18, 240.00,  22, 'LimitedSupply'),
                                                                                                                        (15, 15, 19, 42.00,   75, 'Available');

-- Pharmacy 16 (inventory_pharmacy_id=16, pharmacy_id=16)
INSERT INTO pharmacy_product (inventory_pharmacy_id, pharmacy_id, product_id, price, quantity, availability_status) VALUES
                                                                                                                        (16, 16, 2,  36.50,  130, 'Available'),
                                                                                                                        (16, 16, 4,  86.50,   38, 'Available'),
                                                                                                                        (16, 16, 6,  70.50,   98, 'Available'),
                                                                                                                        (16, 16, 8,  40.00,  135, 'Available'),
                                                                                                                        (16, 16, 11, 65.50,   72, 'Available'),
                                                                                                                        (16, 16, 13, 21.50,  210, 'Available'),
                                                                                                                        (16, 16, 15, 60.50,   82, 'Available'),
                                                                                                                        (16, 16, 17, 15.00,  370, 'Available'),
                                                                                                                        (16, 16, 19, 46.50,   45, 'Available'),
                                                                                                                        (16, 16, 20, 34.00,  130, 'Available');

-- Pharmacy 17 (inventory_pharmacy_id=17, pharmacy_id=17)
INSERT INTO pharmacy_product (inventory_pharmacy_id, pharmacy_id, product_id, price, quantity, availability_status) VALUES
                                                                                                                        (17, 17, 1,  25.50,  185, 'Available'),
                                                                                                                        (17, 17, 3,  45.00,   65, 'Available'),
                                                                                                                        (17, 17, 5,  30.00,  330, 'Available'),
                                                                                                                        (17, 17, 7,  55.00,   58, 'Available'),
                                                                                                                        (17, 17, 9,  50.00,   68, 'Available'),
                                                                                                                        (17, 17, 10, 94.50,   28, 'LimitedSupply'),
                                                                                                                        (17, 17, 12, 121.00,  20, 'LimitedSupply'),
                                                                                                                        (17, 17, 14, 29.50,  175, 'Available'),
                                                                                                                        (17, 17, 16, 23.50,  250, 'Available'),
                                                                                                                        (17, 17, 18, 253.00,  10, 'LimitedSupply');

-- Pharmacy 18 (inventory_pharmacy_id=18, pharmacy_id=18)
INSERT INTO pharmacy_product (inventory_pharmacy_id, pharmacy_id, product_id, price, quantity, availability_status) VALUES
                                                                                                                        (18, 18, 1,  24.00,  230, 'Available'),
                                                                                                                        (18, 18, 2,  34.50,  160, 'Available'),
                                                                                                                        (18, 18, 4,  84.50,   52, 'Available'),
                                                                                                                        (18, 18, 5,  28.50,  360, 'Available'),
                                                                                                                        (18, 18, 6,  69.50,  108, 'Available'),
                                                                                                                        (18, 18, 8,  38.50,  145, 'Available'),
                                                                                                                        (18, 18, 11, 62.50,   92, 'Available'),
                                                                                                                        (18, 18, 13, 18.00,  260, 'Available'),
                                                                                                                        (18, 18, 16, 20.50,  340, 'Available'),
                                                                                                                        (18, 18, 17, 14.00,  430, 'Available'),
                                                                                                                        (18, 18, 20, 33.00,  150, 'Available');

-- ────────────────────────────────────────────────────────────
-- 13. PHARMACY_RATINGS
-- ────────────────────────────────────────────────────────────
INSERT INTO pharmacy_rating (pharmacy_id, customer_id, rating) VALUES
                                                                   (1, 3, 5),
                                                                   (1, 4, 4),
                                                                   (2, 3, 4),
                                                                   (2, 4, 5),
                                                                   (3, 3, 5),
                                                                   (3, 4, 4),
                                                                   (4, 3, 4),
                                                                   (4, 4, 5),
                                                                   (5, 3, 5),
                                                                   (5, 4, 5),
                                                                   (6, 3, 3),
                                                                   (6, 4, 4),
                                                                   (7, 3, 4),
                                                                   (7, 4, 4),
                                                                   (8, 3, 5),
                                                                   (8, 4, 4),
                                                                   (9, 3, 4),
                                                                   (9, 4, 3),
                                                                   (10, 3, 4),
                                                                   (10, 4, 4),
                                                                   (11, 3, 5),
                                                                   (11, 4, 4),
                                                                   (12, 3, 5),
                                                                   (12, 4, 5),
                                                                   (13, 3, 4),
                                                                   (13, 4, 4),
                                                                   (14, 3, 4),
                                                                   (14, 4, 5),
                                                                   (15, 3, 5),
                                                                   (15, 4, 5),
                                                                   (16, 3, 4),
                                                                   (16, 4, 4),
                                                                   (17, 3, 5),
                                                                   (17, 4, 4),
                                                                   (18, 3, 4),
                                                                   (18, 4, 4);


-- ────────────────────────────────────────────────────────────
-- 14. PHARMACY_REVIEWS
-- ────────────────────────────────────────────────────────────
INSERT INTO pharmacy_review (pharmacy_id, customer_id, comment) VALUES
                                                                    (1, 3, 'Great pharmacy, very professional staff and quick service.'),
                                                                    (1, 4, 'Good variety of medications, reasonable prices.'),
                                                                    (2, 3, 'Friendly pharmacists and clean environment.'),
                                                                    (2, 4, 'Good location near the Nile, convenient for me.'),
                                                                    (3, 3, 'Best pharmacy in Cairo! Open 24 hours, very helpful.'),
                                                                    (3, 4, 'Excellent service even late at night.'),
                                                                    (4, 3, 'Very kind staff and good prices.'),
                                                                    (4, 4, 'They always have what I need, highly recommend.'),
                                                                    (5, 3, 'Lifesavers during the night! Very fast service.'),
                                                                    (5, 4, 'Excellent 24/7 pharmacy, very reliable.'),
                                                                    (6, 3, 'Decent pharmacy, but sometimes crowded.'),
                                                                    (6, 4, 'Good selection of cosmetics and medicines.'),
                                                                    (7, 3, 'Helpful pharmacists, answered all my questions.'),
                                                                    (7, 4, 'Clean and well-organized, easy to find items.'),
                                                                    (8, 3, 'My go-to pharmacy in Helwan, very convenient.'),
                                                                    (8, 4, 'Friendly and fast, will visit again.'),
                                                                    (9, 3, 'Okay pharmacy, but missing some rare meds.'),
                                                                    (9, 4, 'Good location but parking can be difficult.'),
                                                                    (10, 3, 'Standard pharmacy, gets the job done.'),
                                                                    (10, 4, 'Staff is polite and prices are standard.'),
                                                                    (11, 3, 'Great local pharmacy with good discounts on cosmetics.'),
                                                                    (11, 4, 'Always helpful and smiling, highly recommended.'),
                                                                    (12, 3, 'Incredible service and very fast delivery.'),
                                                                    (12, 4, 'Top tier pharmacy, very professional.'),
                                                                    (13, 3, 'Good neighborhood pharmacy, fair prices.'),
                                                                    (13, 4, 'They had a medication I could not find anywhere else.'),
                                                                    (14, 3, 'Large pharmacy with almost everything you need.'),
                                                                    (14, 4, 'Very modern and clean, professional staff.'),
                                                                    (15, 3, 'Luxury experience, amazing customer care.'),
                                                                    (15, 4, 'Premium products available here, very clean.'),
                                                                    (16, 3, 'Good reliable pharmacy in Dokki.'),
                                                                    (16, 4, 'Fast checkout and helpful advice from the pharmacist.'),
                                                                    (17, 3, 'Always open when you need them, great location.'),
                                                                    (17, 4, 'Very quick service despite being busy.'),
                                                                    (18, 3, 'Solid pharmacy, friendly staff.'),
                                                                    (18, 4, 'Well-stocked and neat, a reliable choice.');

-- ────────────────────────────────────────────────────────────
-- 15. ORDERS  (source_cart_id must be unique)
-- ────────────────────────────────────────────────────────────
INSERT INTO orders (customer_id, pharmacy_id, source_cart_id, total_price, delivery_type, payment_method, status) VALUES
                                                                                                                      (3,  1, 1001, 85.50,  'DELIVERY', 'CARD', 'CONFIRMED'),
                                                                                                                      (4,  2, 1002, 120.00, 'PICKUP',   'CASH', 'CONFIRMED'),
                                                                                                                      (3,  3, 1003, 45.00,  'DELIVERY', 'CARD', 'PLACED'),
                                                                                                                      (4,  4, 1004, 200.00, 'PICKUP',   'CASH', 'CONFIRMED'),
                                                                                                                      (3,  6, 1005, 315.00, 'DELIVERY', 'CARD', 'CONFIRMED'),
                                                                                                                      (4,  6, 1006, 55.00,  'PICKUP',   'CASH', 'PLACED'),
                                                                                                                      (3,  7, 1007, 95.00,  'DELIVERY', 'CARD', 'PENDING_PAYMENT'),
                                                                                                                      (4,  8, 1008, 70.00,  'PICKUP',   'CASH', 'CANCELED'),
                                                                                                                      (3,  6, 1009, 160.00, 'DELIVERY', 'CARD', 'CONFIRMED'),
                                                                                                                      (4,  10, 1010, 92.00,  'PICKUP',   'CASH', 'CONFIRMED');
-- order_id sequence: 1–10


-- ────────────────────────────────────────────────────────────
-- 16. ORDER_ITEMS
-- ────────────────────────────────────────────────────────────
INSERT INTO order_item (order_id, product_id, quantity, price_at_purchase, subtotal) VALUES
                                                                                         (1,  1, 2, 25.50,  51.00),
                                                                                         (1,  5, 1, 30.00,  30.00),
                                                                                         (2,  3, 1, 45.00,  45.00),
                                                                                         (2,  7, 1, 55.00,  55.00),
                                                                                         (3,  1, 1, 24.00,  24.00),
                                                                                         (3,  2, 1, 35.00,  35.00),
                                                                                         (4,  4, 1, 85.00,  85.00),
                                                                                         (4, 15, 1, 60.00,  60.00),
                                                                                         (5, 12, 1,120.00, 120.00),
                                                                                         (5, 18, 1,250.00, 250.00),
                                                                                         (6,  5, 1, 29.00,  29.00),
                                                                                         (6, 13, 1, 19.00,  19.00),
                                                                                         (7, 10, 1, 95.00,  95.00),
                                                                                         (9,  7, 1, 54.00,  54.00),
                                                                                         (9,  8, 1, 39.00,  39.00),
                                                                                         (9, 13, 1, 19.00,  19.00),
                                                                                         (10, 10, 1, 92.00, 92.00);


-- ────────────────────────────────────────────────────────────
-- 17. INVOICES  (order_id must be unique per invoice)
-- ────────────────────────────────────────────────────────────
INSERT INTO invoice (order_id, invoice_url) VALUES
                                                (1, 'https://invoices.pharma.com/inv_001.pdf'),
                                                (2, 'https://invoices.pharma.com/inv_002.pdf'),
                                                (4, 'https://invoices.pharma.com/inv_004.pdf'),
                                                (5, 'https://invoices.pharma.com/inv_005.pdf'),
                                                (9, 'https://invoices.pharma.com/inv_009.pdf'),
                                                (10,'https://invoices.pharma.com/inv_010.pdf');


-- ────────────────────────────────────────────────────────────
-- 18. PAYMENTS
-- ────────────────────────────────────────────────────────────
INSERT INTO payments (order_id, amount, currency, status,
                      provider_payment_intent_id, idempotency_key, client_secret) VALUES
                                                                                      (1,  85.50,  'EGP', 'SUCCEEDED',    'pi_seed_001', 'idem_001', 'pi_seed_001_secret'),
                                                                                      (2, 120.00,  'EGP', 'PENDING_CASH', 'pi_seed_002', 'idem_002', 'pi_seed_002_secret'),
                                                                                      (3,  45.00,  'EGP', 'INITIATED',    'pi_seed_003', 'idem_003', 'pi_seed_003_secret'),
                                                                                      (4, 200.00,  'EGP', 'PENDING_CASH', 'pi_seed_004', 'idem_004', 'pi_seed_004_secret'),
                                                                                      (5, 315.00,  'EGP', 'SUCCEEDED',    'pi_seed_005', 'idem_005', 'pi_seed_005_secret'),
                                                                                      (6,  55.00,  'EGP', 'PENDING_CASH', 'pi_seed_006', 'idem_006', 'pi_seed_006_secret'),
                                                                                      (7,  95.00,  'EGP', 'INITIATED',    'pi_seed_007', 'idem_007', 'pi_seed_007_secret'),
                                                                                      (8,  70.00,  'EGP', 'FAILED',       'pi_seed_008', 'idem_008', 'pi_seed_008_secret'),
                                                                                      (9, 160.00,  'EGP', 'SUCCEEDED',    'pi_seed_009', 'idem_009', 'pi_seed_009_secret'),
                                                                                      (10, 92.00,  'EGP', 'PENDING_CASH', 'pi_seed_010', 'idem_010', 'pi_seed_010_secret');
-- ────────────────────────────────────────────────────────────
-- 19. PRESCRIPTIONS
-- ────────────────────────────────────────────────────────────
INSERT INTO prescription (customer_id, prescription_image_url, status, uploaded_at) VALUES
                                                                                        (3,  'https://prescriptions.pharma.com/rx_007_1.jpg', 'APPROVED',  '2024-01-10 10:00:00'),
                                                                                        (4,  'https://prescriptions.pharma.com/rx_008_1.jpg', 'APPROVED',  '2024-02-15 11:30:00'),
                                                                                        (3,  'https://prescriptions.pharma.com/rx_009_1.jpg', 'PENDING',   '2024-03-20 09:00:00'),
                                                                                        (4,  'https://prescriptions.pharma.com/rx_010_1.jpg', 'APPROVED',  '2024-04-05 14:00:00'),
                                                                                        (3,  'https://prescriptions.pharma.com/rx_011_1.jpg', 'APPROVED',  '2024-05-01 08:30:00'),
                                                                                        (4,  'https://prescriptions.pharma.com/rx_012_1.jpg', 'REJECTED',  '2024-05-18 16:00:00'),
                                                                                        (3,  'https://prescriptions.pharma.com/rx_013_1.jpg', 'PENDING',   '2024-06-01 10:00:00');

-- ────────────────────────────────────────────────────────────
-- 20. MEDICATION_REMINDERS
-- ────────────────────────────────────────────────────────────
INSERT INTO medication_reminder (customer_id, product_name, dosage_time, notify_before_minutes) VALUES
                                                                                                    (3,  'Concor 5mg',         '08:00', 15),
                                                                                                    (3,  'Glucophage 500mg',   '13:00', 10),
                                                                                                    (3,  'Amoxicillin 500mg',  '09:00', 20),
                                                                                                    (3,  'Amoxicillin 500mg',  '21:00', 20),
                                                                                                    (3,  'Ventolin Inhaler',   '07:30', 5),
                                                                                                    (4, 'Insulin Glargine',   '22:00', 30),
                                                                                                    (4, 'Zoloft 50mg',        '09:00', 10),
                                                                                                    (4, 'Prednisolone 5mg',   '08:00', 15),
                                                                                                    (4, 'Nexium 20mg',        '07:00', 10),
                                                                                                    (4, 'Aspirin 100mg',      '08:30', 10),
                                                                                                    (3, 'Vitamin C 1000mg',   '09:00', 5);


-- ────────────────────────────────────────────────────────────
-- 21. NOTIFICATIONS
-- ────────────────────────────────────────────────────────────
INSERT INTO notification (user_id, message, type, is_read) VALUES
                                                               (3,  'Your order #1 has been confirmed.',                0, true),
                                                               (3,  'Your order #2 is ready for pickup.',              0, true),
                                                               (3,  'Your order #3 has been placed successfully.',     0, false),
                                                               (4, 'Your order #4 is ready for pickup.',              0, true),
                                                               (4, 'Your order #5 has been confirmed.',               0, false),
                                                               (4, 'Your prescription was rejected. Please resubmit.', 1, false),
                                                               (3, 'Your medication reminder is set for 07:00.',      2, true),
                                                               (3, 'Your order #8 has been canceled.',                0, true),
                                                               (3, 'Your order #9 has been confirmed.',               0, false),
                                                               (4,  'Reminder: Time to take Glucophage 500mg.',        2, false);


-- ────────────────────────────────────────────────────────────
-- 22. P2P LISTINGS
-- ────────────────────────────────────────────────────────────
INSERT INTO p2p_listing (medicine_id, seller_id, product_name, price, quantity, expiry_date, image_url, status) VALUES
                                                                                                                    (1,  3,  'Panadol Extra 500mg',   20.00, 10, '2025-12-31', 'p2p_panadol.png',    'AVAILABLE'),
                                                                                                                    (5,  4,  'Vitamin C 1000mg',      25.00,  5, '2026-06-30', 'p2p_vitc.png',       'AVAILABLE'),
                                                                                                                    (6,  4,  'Omega-3 Fish Oil',      60.00,  3, '2025-09-30', 'p2p_omega3.png',     'AVAILABLE'),
                                                                                                                    (2,  3,  'Brufen 400mg',          30.00,  8, '2025-11-30', 'p2p_brufen.png',     'AVAILABLE'),
                                                                                                                    (11, 4,  'Nexium 20mg',           55.00,  4, '2026-01-31', 'p2p_nexium.png',     'AVAILABLE'),
                                                                                                                    (16, 3,  'Zinc 50mg',             18.00, 20, '2026-03-31', 'p2p_zinc.png',       'SOLD'),
                                                                                                                    (17, 4,  'Aspirin 100mg',         12.00, 15, '2025-10-31', 'p2p_aspirin.png',    'SOLD');

-- ────────────────────────────────────────────────────────────
-- 23. P2P TRANSACTIONS  (listing_id must be unique)
-- ────────────────────────────────────────────────────────────
INSERT INTO p2p_transaction (listing_id, buyer_id, status) VALUES
    (3, 3, 'COMPLETED');


-- ────────────────────────────────────────────────────────────
-- 24. SELLER_RATINGS
-- ────────────────────────────────────────────────────────────
INSERT INTO seller_rating (seller_id, buyer_id, rating, comment) VALUES
                                                                     (3, 4, 5, 'Item was exactly as described. Fast response!'),
                                                                     (4, 3, 4, 'Good product, delivered on time.');