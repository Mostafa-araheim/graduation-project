-- ============================================================
-- V2__init_schema.sql
-- الـ Migration التانية: إنشاء كل جداول الداتابيز
--
-- ⚠️ مهم جداً: ترتيب CREATE TABLE مهم بسبب الـ Foreign Keys
-- الجدول اللي بيتبع لجدول تاني لازم يتنشأ بعده.
--
-- الترتيب:
-- 1. users              (مفيش FK)
-- 2. user_roles         (FK → users)
-- 3. owner_profile      (FK → users)
-- 4. pharmacist_profile (FK → users)
-- 5. customer_profile   (FK → users)
-- 6. user_address       (FK → users)
-- 7. brand              (مفيش FK)
-- 8. category           (مفيش FK)
-- 9. product            (FK → brand, category)
-- 10. product_image     (FK → product)
-- 11. pharmacy          (FK → owner_profile)
-- 12. pharmacy_address  (FK → pharmacy)
-- 13. inventory         (FK → pharmacy)
-- 14. pharmacy_product  (FK → inventory, pharmacy, product)
-- 15. pharmacy_staff    (FK → pharmacy, users)
-- 16. pharmacy_hiring_request (FK → pharmacy, owner_profile, pharmacist_profile)
-- 17. orders            (FK → customer_profile, pharmacy, user_address)
-- 18. order_item        (FK → orders, product)
-- 19. payments          (FK → orders)
-- 20. invoice           (FK → orders)
-- 21. pharmacy_rating   (FK → pharmacy, customer_profile)
-- 22. pharmacy_review   (FK → pharmacy, customer_profile)
-- 23. p2p_listing       (FK → customer_profile, product)
-- 24. p2p_transaction   (FK → p2p_listing, customer_profile)
-- 25. product_reservation (FK → customer_profile, product)
-- 26. seller_rating     (FK → customer_profile)
-- 27. prescription      (FK → customer_profile)
-- 28. notification      (FK → users)
-- 29. medication_reminder (FK → customer_profile)
-- ============================================================


-- ─────────────────────────────────────────────
-- 1. USERS
-- ─────────────────────────────────────────────
-- الجدول الأساسي - كل حاجة بترجع له
CREATE TABLE users (
    user_id    BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    name       VARCHAR(255),
    phone      VARCHAR(255),
    image_url  VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────
-- 2. USER_ROLES
-- ─────────────────────────────────────────────
-- @ElementCollection في Java بيعمل جدول منفصل للـ Roles
-- FK → users(user_id)
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    role    VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role)
);

-- ─────────────────────────────────────────────
-- 3. OWNER_PROFILE
-- ─────────────────────────────────────────────
-- @MapsId بتاع Hibernate: الـ PK هو نفس الـ FK → users
CREATE TABLE owner_profile (
    user_id    BIGINT PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────
-- 4. PHARMACIST_PROFILE
-- ─────────────────────────────────────────────
CREATE TABLE pharmacist_profile (
    user_id    BIGINT PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────
-- 5. CUSTOMER_PROFILE
-- ─────────────────────────────────────────────
CREATE TABLE customer_profile (
    user_id    BIGINT PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────
-- 6. USER_ADDRESS
-- ─────────────────────────────────────────────
CREATE TABLE user_address (
    user_address_id       BIGSERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    street           VARCHAR(255),
    city             VARCHAR(255),
    country          VARCHAR(255),
    postal_code      VARCHAR(50),
    apartment_number VARCHAR(50)
);

-- ─────────────────────────────────────────────
-- 7. BRAND
-- ─────────────────────────────────────────────
CREATE TABLE brand (
    brand_id    BIGSERIAL PRIMARY KEY,
    brand_name  VARCHAR(255),
    brand_image VARCHAR(255)
);

-- ─────────────────────────────────────────────
-- 8. CATEGORY
-- ─────────────────────────────────────────────
CREATE TABLE category (
    category_id   BIGSERIAL PRIMARY KEY,
    category_name TEXT,
    image_url     VARCHAR(255)
);

-- ─────────────────────────────────────────────
-- 9. PRODUCT
-- ─────────────────────────────────────────────
-- @Enumerated(STRING) بيخزن الـ Enum كـ VARCHAR
-- UniqueConstraint على (name, strength, dosageForm) من الـ Entity
CREATE TABLE product (
    product_id             BIGSERIAL PRIMARY KEY,
    name                   VARCHAR(255) NOT NULL,
    description            TEXT,
    requires_prescription  BOOLEAN NOT NULL DEFAULT FALSE,
    -- dosage_form هو Enum في Java - بنخزنه كـ VARCHAR
    dosage_form            VARCHAR(50),
    strength               VARCHAR(255),
    manufacturer           VARCHAR(255),
    image_url              VARCHAR(255),
    category_id            BIGINT REFERENCES category(category_id),
    brand_id               BIGINT REFERENCES brand(brand_id),
    -- UniqueConstraint من @Table annotation في Entity
    CONSTRAINT uk_medicine_identity UNIQUE (name, strength, dosage_form)
);

-- ─────────────────────────────────────────────
-- 10. PRODUCT_IMAGE
-- ─────────────────────────────────────────────
CREATE TABLE product_image (
    image_id   BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(product_id) ON DELETE CASCADE,
    image_url  VARCHAR(255),
    sort_order BIGINT
);

-- ─────────────────────────────────────────────
-- 11. PHARMACY
-- ─────────────────────────────────────────────
-- location: نوع geometry من PostGIS - عشان كده عملنا V1 لتفعيل extension
CREATE TABLE pharmacy (
    pharmacy_id    BIGSERIAL PRIMARY KEY,
    owner_user_id  BIGINT REFERENCES owner_profile(user_id),
    name           VARCHAR(255),
    image_url      VARCHAR(255),
    latitude       DOUBLE PRECISION,
    longitude      DOUBLE PRECISION,
    -- geometry(Point, 4326): نقطة جغرافية بنظام إحداثيات WGS84
    location       geometry(Point, 4326),
    opening_time   TIME,
    closing_time   TIME,
    is_24_hours    BOOLEAN,
    average_rating NUMERIC(19, 2),
    rating_count   BIGINT,
    review_count   BIGINT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────
-- 12. PHARMACY_ADDRESS
-- ─────────────────────────────────────────────
CREATE TABLE pharmacy_address (
    address_id       BIGSERIAL PRIMARY KEY,
    pharmacy_id      BIGINT NOT NULL UNIQUE REFERENCES pharmacy(pharmacy_id) ON DELETE CASCADE,
    street           VARCHAR(255),
    city             VARCHAR(255),
    country          VARCHAR(255),
    postal_code      VARCHAR(50),
    apartment_number VARCHAR(50)
);

-- ─────────────────────────────────────────────
-- 13. INVENTORY
-- ─────────────────────────────────────────────
-- @MapsId: الـ PK هو نفسه الـ FK → pharmacy
-- معناها: كل pharmacy ليه inventory واحد بنفس الـ ID
CREATE TABLE inventory (
    pharmacy_id BIGINT PRIMARY KEY REFERENCES pharmacy(pharmacy_id) ON DELETE CASCADE
);

-- ─────────────────────────────────────────────
-- 14. PHARMACY_PRODUCT
-- ─────────────────────────────────────────────
-- UniqueConstraint على (inventory_pharmacy_id, product_id)
CREATE TABLE pharmacy_product (
    pharmacy_product_id    BIGSERIAL PRIMARY KEY,
    -- inventory_pharmacy_id بيرجع لـ pharmacy_id في inventory
    inventory_pharmacy_id  BIGINT NOT NULL REFERENCES inventory(pharmacy_id),
    pharmacy_id            BIGINT NOT NULL REFERENCES pharmacy(pharmacy_id),
    product_id             BIGINT NOT NULL REFERENCES product(product_id),
    price                  NUMERIC(19, 2),
    quantity               BIGINT,
    availability_status    VARCHAR(50),
    CONSTRAINT uk_inventory_product UNIQUE (inventory_pharmacy_id, product_id)
);

-- ─────────────────────────────────────────────
-- 15. PHARMACY_STAFF
-- ─────────────────────────────────────────────
CREATE TABLE pharmacy_staff (
    pharmacy_id BIGINT NOT NULL REFERENCES pharmacy(pharmacy_id) ON DELETE CASCADE,
    user_id     BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    staff_role  VARCHAR(50),
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    joined_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (pharmacy_id, user_id)
);

-- ─────────────────────────────────────────────
-- 16. PHARMACY_HIRING_REQUEST
-- ─────────────────────────────────────────────
CREATE TABLE pharmacy_hiring_request (
    request_id          BIGSERIAL PRIMARY KEY,
    pharmacy_id         BIGINT NOT NULL REFERENCES pharmacy(pharmacy_id),
    owner_user_id       BIGINT NOT NULL REFERENCES owner_profile(user_id),
    pharmacist_user_id  BIGINT NOT NULL REFERENCES pharmacist_profile(user_id),
    status              VARCHAR(50) NOT NULL,
    requested_role      VARCHAR(50) NOT NULL,
    message             VARCHAR(255)
);

-- ─────────────────────────────────────────────
-- 17. ORDERS
-- ─────────────────────────────────────────────
-- ملاحظة: الجدول اسمه "orders" مش "order" عشان
-- "ORDER" كلمة محجوزة في SQL!
CREATE TABLE orders (
    order_id           BIGSERIAL PRIMARY KEY,
    customer_id        BIGINT NOT NULL REFERENCES customer_profile(user_id),
    pharmacy_id        BIGINT REFERENCES pharmacy(pharmacy_id),
    total_price        NUMERIC(19, 2),
    delivery_type      VARCHAR(50),
    payment_method     VARCHAR(50),
    -- @OnDelete(SET_NULL): لو العنوان اتمسح، الـ FK يبقى NULL
    delivery_address_id BIGINT REFERENCES user_address(user_address_id) ON DELETE SET NULL,
    status             VARCHAR(50),
    -- source_cart_id: مرجع للكارت اللي منه جاء الأوردر (UNIQUE)
    source_cart_id     BIGINT NOT NULL UNIQUE,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────
-- 18. ORDER_ITEM
-- ─────────────────────────────────────────────
CREATE TABLE order_item (
    order_item_id      BIGSERIAL PRIMARY KEY,
    order_id           BIGINT NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
    product_id         BIGINT NOT NULL REFERENCES product(product_id),
    quantity           INTEGER,
    price_at_purchase  NUMERIC(19, 2),
    subtotal           NUMERIC(19, 2)
);

-- ─────────────────────────────────────────────
-- 19. PAYMENTS
-- ─────────────────────────────────────────────
CREATE TABLE payments (
    payment_id                   BIGSERIAL PRIMARY KEY,
    order_id                     BIGINT NOT NULL REFERENCES orders(order_id),
    provider_payment_intent_id   VARCHAR(255) UNIQUE,
    idempotency_key              VARCHAR(255) UNIQUE,
    client_secret                VARCHAR(255),
    amount                       NUMERIC(19, 2) NOT NULL,
    currency                     VARCHAR(10),
    status                       VARCHAR(50),
    failure_reason               VARCHAR(255),
    created_at                   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    paid_at                      TIMESTAMPTZ
);

-- ─────────────────────────────────────────────
-- 20. INVOICE
-- ─────────────────────────────────────────────
CREATE TABLE invoice (
    invoice_id  BIGSERIAL PRIMARY KEY,
    order_id    BIGINT NOT NULL UNIQUE REFERENCES orders(order_id),
    invoice_url VARCHAR(255),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────
-- 21. PHARMACY_RATING
-- ─────────────────────────────────────────────
CREATE TABLE pharmacy_rating (
    rating_id   BIGSERIAL PRIMARY KEY,
    pharmacy_id BIGINT NOT NULL REFERENCES pharmacy(pharmacy_id),
    customer_id BIGINT NOT NULL REFERENCES customer_profile(user_id),
    rating      INTEGER NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_pharmacy_customer_rating UNIQUE (pharmacy_id, customer_id)
);

-- ─────────────────────────────────────────────
-- 22. PHARMACY_REVIEW
-- ─────────────────────────────────────────────
CREATE TABLE pharmacy_review (
    review_id   BIGSERIAL PRIMARY KEY,
    pharmacy_id BIGINT NOT NULL REFERENCES pharmacy(pharmacy_id),
    customer_id BIGINT NOT NULL REFERENCES customer_profile(user_id),
    comment     VARCHAR(1000),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────
-- 23. P2P_LISTING
-- ─────────────────────────────────────────────
CREATE TABLE p2p_listing (
    listing_id    BIGSERIAL PRIMARY KEY,
    seller_id     BIGINT NOT NULL REFERENCES customer_profile(user_id),
    medicine_id   BIGINT NOT NULL REFERENCES product(product_id),
    product_name  VARCHAR(255),
    quantity      BIGINT,
    expiry_date   DATE,
    description   VARCHAR(255),
    price         REAL,
    image_url     VARCHAR(255),
    status        VARCHAR(50) DEFAULT 'AVAILABLE',
    condition     VARCHAR(50),
    city          VARCHAR(255),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────
-- 24. P2P_TRANSACTION
-- ─────────────────────────────────────────────
CREATE TABLE p2p_transaction (
    transaction_id BIGSERIAL PRIMARY KEY,
    -- UNIQUE عشان كل listing ليها transaction واحدة بس (@OneToOne)
    listing_id     BIGINT NOT NULL UNIQUE REFERENCES p2p_listing(listing_id),
    buyer_id       BIGINT NOT NULL REFERENCES customer_profile(user_id),
    status         VARCHAR(50),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────
-- 25. PRODUCT_RESERVATION
-- ─────────────────────────────────────────────
CREATE TABLE product_reservation (
    reservation_id BIGSERIAL PRIMARY KEY,
    user_id        BIGINT NOT NULL REFERENCES customer_profile(user_id),
    product_id     BIGINT NOT NULL REFERENCES product(product_id),
    product_name   VARCHAR(255),
    status         VARCHAR(50) DEFAULT 'PENDING',
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index من @Index annotation في Entity
CREATE INDEX idx_res_prod_status ON product_reservation (product_id, status);

-- ─────────────────────────────────────────────
-- 26. SELLER_RATING
-- ─────────────────────────────────────────────
CREATE TABLE seller_rating (
    rating_id  BIGSERIAL PRIMARY KEY,
    seller_id  BIGINT NOT NULL REFERENCES customer_profile(user_id),
    buyer_id   BIGINT NOT NULL REFERENCES customer_profile(user_id),
    rating     BIGINT,
    comment    VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────
-- 27. PRESCRIPTION
-- ─────────────────────────────────────────────
CREATE TABLE prescription (
    prescription_id       BIGSERIAL PRIMARY KEY,
    customer_id           BIGINT NOT NULL REFERENCES customer_profile(user_id),
    uploaded_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                VARCHAR(50),
    prescription_image_url VARCHAR(255)
);

-- ─────────────────────────────────────────────
-- 28. NOTIFICATION
-- ─────────────────────────────────────────────
CREATE TABLE notification (
    notification_id BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    type            VARCHAR(50),
    message         VARCHAR(255),
    is_read         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────
-- 29. MEDICATION_REMINDER
-- ─────────────────────────────────────────────
CREATE TABLE medication_reminder (
    reminder_id             BIGSERIAL PRIMARY KEY,
    customer_id             BIGINT NOT NULL REFERENCES customer_profile(user_id),
    -- ملاحظة: في Java الـ field اسمه ProductName (بحرف كبير) - Hibernate بيحوله لـ product_name
    product_name            VARCHAR(255),
    dosage_time             TIME,
    notify_before_minutes   BIGINT
);



CREATE TABLE refresh_tokens (
                                refresh_token_id BIGSERIAL PRIMARY KEY,
                                token            VARCHAR(500) NOT NULL UNIQUE,
                                user_id          BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                                expiry_date      TIMESTAMP NOT NULL,
                                revoked          BOOLEAN NOT NULL DEFAULT FALSE,
                                created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);