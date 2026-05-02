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
                                           ('nadia.youssef@gmail.com',   'Nadia Youssef',      '01098765432'),
                                           ('khaled.ibrahim@gmail.com',  'Khaled Ibrahim',   '01187654321'),
                                           ('rana.mahmoud@gmail.com',    'Rana Mahmoud',    '01276543210'),
                                           ('moatzahmed010@gmail.com',     'Moatz Ahmed',     '01147796049'),
                                           ('hana.adel@gmail.com',       'Hana Adel',       '01454321098'),
                                           ('youssef.kamal@gmail.com',   'Youssef Kamal',   '01543210987'),
                                           ('laila.nasser@gmail.com',    'Laila Nasser',    '01632109876'),
                                           ('omar.fawzi@gmail.com',      'Omar Fawzi',      '01721098765'),
                                           ('mona.sayed@gmail.com',      'Mona Sayed',      '01810987654'),
                                           ('hassan.ali@gmail.com',      'Hassan Ali',      '01909876543'),
                                           ('dina.karim@gmail.com',      'Dina Karim',      '01098765431'),
                                           ('amr.lotfy@gmail.com',       'Amr Lotfy',       '01187654320');
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
                                           (3,  'ROLE_OWNER'),
                                           (4,  'ROLE_PHARMACIST'),
                                           (5,  'ROLE_PHARMACIST'),
                                           (6,  'ROLE_PHARMACIST'),
                                           (7,  'ROLE_CUSTOMER'),
                                           (8,  'ROLE_CUSTOMER'),
                                           (9,  'ROLE_CUSTOMER'),
                                           (10, 'ROLE_CUSTOMER'),
                                           (11, 'ROLE_CUSTOMER'),
                                           (12, 'ROLE_CUSTOMER'),
                                           (13, 'ROLE_CUSTOMER'),
                                           (14, 'ROLE_CUSTOMER'),
                                           (15, 'ROLE_CUSTOMER');


-- ────────────────────────────────────────────────────────────
-- 3. PROFILES
-- ────────────────────────────────────────────────────────────
INSERT INTO owner_profile (user_id) VALUES (1), (2), (3);

INSERT INTO pharmacist_profile (user_id) VALUES (4), (5), (6);

INSERT INTO customer_profile (user_id) VALUES
                                           (7), (8), (9), (10), (11), (12), (13), (14), (15);


-- ────────────────────────────────────────────────────────────
-- 4. USER_ADDRESS
-- ────────────────────────────────────────────────────────────
INSERT INTO user_address (user_id, street, city, country, postal_code, apartment_number) VALUES
                                                                                             (7,  '15 Tahrir Square',        'Cairo',       'Egypt', '11511', 'Apt 3'),
                                                                                             (8,  '42 Corniche El Nile',     'Cairo',       'Egypt', '11512', 'Apt 7'),
                                                                                             (9,  '8 El Geish Road',         'Alexandria',  'Egypt', '21511', 'Flat 2'),
                                                                                             (10, '20 Port Said Street',     'Giza',        'Egypt', '12511', 'Apt 5'),
                                                                                             (11, '33 Ramsis Street',        'Cairo',       'Egypt', '11711', 'Apt 1'),
                                                                                             (12, '5 El Haram Street',       'Giza',        'Egypt', '12512', NULL),
                                                                                             (13, '77 Maadi Corniche',       'Cairo',       'Egypt', '11431', 'Apt 9'),
                                                                                             (14, '12 Stanley Bay Road',     'Alexandria',  'Egypt', '21532', NULL),
                                                                                             (15, '3 Mohandiseen Square',    'Giza',        'Egypt', '12513', 'Apt 6');


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
                                                    ('Pain Relief',         'pain_relief.png'),
                                                    ('Antibiotics',         'antibiotics.png'),
                                                    ('Vitamins & Supplements', 'vitamins.png'),
                                                    ('Cardiovascular',      'cardiovascular.png'),
                                                    ('Diabetes',            'diabetes.png'),
                                                    ('Dermatology',         'dermatology.png'),
                                                    ('Respiratory',         'respiratory.png'),
                                                    ('Gastrointestinal',    'gastrointestinal.png'),
                                                    ('Mental Health',       'mental_health.png'),
                                                    ('Eye & Ear Care',      'eye_ear.png');
-- category_id sequence: 1–10


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


-- ────────────────────────────────────────────────────────────
-- 8. PHARMACIES
-- ────────────────────────────────────────────────────────────
INSERT INTO pharmacy (name, owner_user_id, average_rating, rating_count, review_count,
                      opening_time, closing_time, is_24_hours,
                      latitude, longitude, image_url, location) VALUES
                                                                    ('Al Shifa Pharmacy',   1, 4.50, 120, 45,
                                                                     '08:00', '22:00', false,
                                                                     30.0444, 31.2357, 'alshifa.png',
                                                                     ST_SetSRID(ST_MakePoint(31.2357, 30.0444), 4326)),

                                                                    ('Nile Care Pharmacy',  2, 4.20, 85,  30,
                                                                     '09:00', '23:00', false,
                                                                     30.0626, 31.2497, 'nilecare.png',
                                                                     ST_SetSRID(ST_MakePoint(31.2497, 30.0626), 4326)),

                                                                    ('Cairo Central Pharmacy', 3, 4.80, 200, 90,
                                                                     '00:00', '00:00', true,
                                                                     30.0561, 31.2272, 'cairocentral.png',
                                                                     ST_SetSRID(ST_MakePoint(31.2272, 30.0561), 4326));
-- pharmacy_id sequence: 1–3


-- ────────────────────────────────────────────────────────────
-- 9. PHARMACY_ADDRESS
-- ────────────────────────────────────────────────────────────
INSERT INTO pharmacy_address (pharmacy_id, street, city, country, postal_code, apartment_number) VALUES
                                                                                                     (1, '10 Kasr El Aini Street',   'Cairo',       'Egypt', '11511', NULL),
                                                                                                     (2, '5 Corniche El Nile',       'Cairo',       'Egypt', '11512', NULL),
                                                                                                     (3, '88 Ramsis Street',         'Cairo',       'Egypt', '11711', NULL);


-- ────────────────────────────────────────────────────────────
-- 10. INVENTORY  (one per pharmacy)
-- ────────────────────────────────────────────────────────────
INSERT INTO inventory (pharmacy_id) VALUES (1), (2), (3);
-- inventory_id sequence: 1, 2, 3


-- ────────────────────────────────────────────────────────────
-- 11. PHARMACY_STAFF
-- ────────────────────────────────────────────────────────────
INSERT INTO pharmacy_staff (pharmacy_id, user_id, staff_role, active, joined_at) VALUES
                                                                                     (1, 4, 'STAFF', true, '2023-01-15 09:00:00'),
                                                                                     (2, 5, 'STAFF', true, '2023-03-20 09:00:00'),
                                                                                     (3, 6, 'STAFF', false, '2023-06-01 09:00:00');


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
-- ────────────────────────────────────────────────────────────
-- 13. PHARMACY_RATINGS
-- ────────────────────────────────────────────────────────────
INSERT INTO pharmacy_rating (pharmacy_id, customer_id, rating) VALUES
                                                                   (1, 7,  5), (1, 8,  4), (1, 9,  5), (1, 10, 4), (1, 11, 5),
                                                                   (2, 7,  4), (2, 8,  4), (2, 9,  3), (2, 12, 5), (2, 13, 4),
                                                                   (3, 10, 5), (3, 11, 5), (3, 14, 5), (3, 15, 4), (3, 9,  5);


-- ────────────────────────────────────────────────────────────
-- 14. PHARMACY_REVIEWS
-- ────────────────────────────────────────────────────────────
INSERT INTO pharmacy_review (pharmacy_id, customer_id, comment) VALUES
                                                                    (1, 7,  'Great pharmacy, very professional staff and quick service.'),
                                                                    (1, 8,  'Good variety of medications, reasonable prices.'),
                                                                    (1, 9,  'Always has what I need. Highly recommended!'),
                                                                    (2, 10, 'Friendly pharmacists and clean environment.'),
                                                                    (2, 11, 'Good location near the Nile, convenient for me.'),
                                                                    (2, 12, 'Sometimes out of stock on common medications.'),
                                                                    (3, 13, 'Best pharmacy in Cairo! Open 24 hours, very helpful.'),
                                                                    (3, 14, 'Excellent service even late at night.'),
                                                                    (3, 15, 'Top-notch staff, they really know their medications.');


-- ────────────────────────────────────────────────────────────
-- 15. ORDERS  (source_cart_id must be unique)
-- ────────────────────────────────────────────────────────────
INSERT INTO orders (customer_id, pharmacy_id, source_cart_id, total_price, delivery_type, payment_method, status) VALUES
                                                                                                                      (7,  1, 1001, 85.50,  'DELIVERY', 'CARD', 'CONFIRMED'),
                                                                                                                      (8,  1, 1002, 120.00, 'PICKUP',   'CASH', 'CONFIRMED'),
                                                                                                                      (9,  2, 1003, 45.00,  'DELIVERY', 'CARD', 'PLACED'),
                                                                                                                      (10, 2, 1004, 200.00, 'PICKUP',   'CASH', 'CONFIRMED'),
                                                                                                                      (11, 3, 1005, 315.00, 'DELIVERY', 'CARD', 'CONFIRMED'),
                                                                                                                      (12, 3, 1006, 55.00,  'PICKUP',   'CASH', 'PLACED'),
                                                                                                                      (13, 1, 1007, 95.00,  'DELIVERY', 'CARD', 'PENDING_PAYMENT'),
                                                                                                                      (14, 2, 1008, 70.00,  'PICKUP',   'CASH', 'CANCELED'),
                                                                                                                      (15, 3, 1009, 160.00, 'DELIVERY', 'CARD', 'CONFIRMED'),
                                                                                                                      (7,  3, 1010, 92.00,  'PICKUP',   'CASH', 'CONFIRMED');
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
                                                                                      (4, 200.00,  'EGP', 'PENDING_CASH', 'pi_seed_004', 'idem_004', 'pi_seed_004_secret'),
                                                                                      (5, 315.00,  'EGP', 'SUCCEEDED',    'pi_seed_005', 'idem_005', 'pi_seed_005_secret'),
                                                                                      (9, 160.00,  'EGP', 'SUCCEEDED',    'pi_seed_009', 'idem_009', 'pi_seed_009_secret'),
                                                                                      (10, 92.00,  'EGP', 'PENDING_CASH', 'pi_seed_010', 'idem_010', 'pi_seed_010_secret');


-- ────────────────────────────────────────────────────────────
-- 19. PRESCRIPTIONS
-- ────────────────────────────────────────────────────────────
INSERT INTO prescription (customer_id, prescription_image_url, status, uploaded_at) VALUES
                                                                                        (7,  'https://prescriptions.pharma.com/rx_007_1.jpg', 'APPROVED',  '2024-01-10 10:00:00'),
                                                                                        (8,  'https://prescriptions.pharma.com/rx_008_1.jpg', 'APPROVED',  '2024-02-15 11:30:00'),
                                                                                        (9,  'https://prescriptions.pharma.com/rx_009_1.jpg', 'PENDING',   '2024-03-20 09:00:00'),
                                                                                        (10, 'https://prescriptions.pharma.com/rx_010_1.jpg', 'APPROVED',  '2024-04-05 14:00:00'),
                                                                                        (11, 'https://prescriptions.pharma.com/rx_011_1.jpg', 'APPROVED',  '2024-05-01 08:30:00'),
                                                                                        (12, 'https://prescriptions.pharma.com/rx_012_1.jpg', 'REJECTED',  '2024-05-18 16:00:00'),
                                                                                        (13, 'https://prescriptions.pharma.com/rx_013_1.jpg', 'PENDING',   '2024-06-01 10:00:00');


-- ────────────────────────────────────────────────────────────
-- 20. MEDICATION_REMINDERS
-- ────────────────────────────────────────────────────────────
INSERT INTO medication_reminder (customer_id, product_name, dosage_time, notify_before_minutes) VALUES
                                                                                                    (7,  'Concor 5mg',         '08:00', 15),
                                                                                                    (7,  'Glucophage 500mg',   '13:00', 10),
                                                                                                    (8,  'Amoxicillin 500mg',  '09:00', 20),
                                                                                                    (8,  'Amoxicillin 500mg',  '21:00', 20),
                                                                                                    (9,  'Ventolin Inhaler',   '07:30', 5),
                                                                                                    (10, 'Insulin Glargine',   '22:00', 30),
                                                                                                    (11, 'Zoloft 50mg',        '09:00', 10),
                                                                                                    (12, 'Prednisolone 5mg',   '08:00', 15),
                                                                                                    (13, 'Nexium 20mg',        '07:00', 10),
                                                                                                    (14, 'Aspirin 100mg',      '08:30', 10),
                                                                                                    (15, 'Vitamin C 1000mg',   '09:00', 5);


-- ────────────────────────────────────────────────────────────
-- 21. NOTIFICATIONS
-- ────────────────────────────────────────────────────────────
INSERT INTO notification (user_id, message, type, is_read) VALUES
                                                               (7,  'Your order #1 has been confirmed.',                0, true),
                                                               (8,  'Your order #2 is ready for pickup.',              0, true),
                                                               (9,  'Your order #3 has been placed successfully.',     0, false),
                                                               (10, 'Your order #4 is ready for pickup.',              0, true),
                                                               (11, 'Your order #5 has been confirmed.',               0, false),
                                                               (12, 'Your prescription was rejected. Please resubmit.', 1, false),
                                                               (13, 'Your medication reminder is set for 07:00.',      2, true),
                                                               (14, 'Your order #8 has been canceled.',                0, true),
                                                               (15, 'Your order #9 has been confirmed.',               0, false),
                                                               (7,  'Reminder: Time to take Glucophage 500mg.',        2, false);


-- ────────────────────────────────────────────────────────────
-- 22. P2P LISTINGS
-- ────────────────────────────────────────────────────────────
INSERT INTO p2p_listing (medicine_id, seller_id, product_name, price, quantity, expiry_date, image_url, status) VALUES
                                                                                                                    (1,  7,  'Panadol Extra 500mg',   20.00, 10, '2025-12-31', 'p2p_panadol.png',    'AVAILABLE'),
                                                                                                                    (5,  8,  'Vitamin C 1000mg',      25.00,  5, '2026-06-30', 'p2p_vitc.png',       'AVAILABLE'),
                                                                                                                    (6,  9,  'Omega-3 Fish Oil',      60.00,  3, '2025-09-30', 'p2p_omega3.png',     'AVAILABLE'),
                                                                                                                    (2,  10, 'Brufen 400mg',          30.00,  8, '2025-11-30', 'p2p_brufen.png',     'AVAILABLE'),
                                                                                                                    (11, 11, 'Nexium 20mg',           55.00,  4, '2026-01-31', 'p2p_nexium.png',     'AVAILABLE'),
                                                                                                                    (16, 12, 'Zinc 50mg',             18.00, 20, '2026-03-31', 'p2p_zinc.png',       'SOLD'),
                                                                                                                    (17, 13, 'Aspirin 100mg',         12.00, 15, '2025-10-31', 'p2p_aspirin.png',    'SOLD');
-- listing_id sequence: 1–7


-- ────────────────────────────────────────────────────────────
-- 23. P2P TRANSACTIONS  (listing_id must be unique)
-- ────────────────────────────────────────────────────────────
INSERT INTO p2p_transaction (listing_id, buyer_id, status) VALUES
    (3, 14, 'COMPLETED');


-- ────────────────────────────────────────────────────────────
-- 24. SELLER_RATINGS
-- ────────────────────────────────────────────────────────────
INSERT INTO seller_rating (seller_id, buyer_id, rating, comment) VALUES
                                                                     (9, 14, 5, 'Item was exactly as described. Fast response!'),
                                                                     (7, 8,  4, 'Good product, delivered on time.'),
                                                                     (10, 13, 5, 'Great seller, very helpful.');