DROP DATABASE IF EXISTS phone_store;
CREATE DATABASE phone_store CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE phone_store;

SET NAMES utf8mb4;

CREATE USER IF NOT EXISTS 'phone_store_app'@'localhost' IDENTIFIED BY 'Lamtrithien2002';
ALTER USER 'phone_store_app'@'localhost' IDENTIFIED BY 'Lamtrithien2002';
GRANT ALL PRIVILEGES ON phone_store.* TO 'phone_store_app'@'localhost';
FLUSH PRIVILEGES;

CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    is_system BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    address VARCHAR(255),
    phone_number VARCHAR(20),
    avatar VARCHAR(255),
    gender VARCHAR(20),
    date_of_birth DATE,
    activation_token VARCHAR(255),
    activation_expires DATETIME,
    is_activated BOOLEAN NOT NULL DEFAULT FALSE,
    first_login BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE brands (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    brand_name VARCHAR(100) NOT NULL UNIQUE,
    brand_slug VARCHAR(120) NOT NULL UNIQUE,
    logo_url VARCHAR(255),
    description VARCHAR(255),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(150) NOT NULL UNIQUE,
    category_slug VARCHAR(180) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bar_code VARCHAR(50) NOT NULL UNIQUE,
    sku VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    product_slug VARCHAR(220) NOT NULL UNIQUE,
    brand_id BIGINT,
    category_id BIGINT NOT NULL,
    screen_size VARCHAR(50),
    ram VARCHAR(50),
    rom VARCHAR(50),
    chipset VARCHAR(100),
    battery_capacity VARCHAR(50),
    operating_system VARCHAR(100),
    color VARCHAR(50),
    warranty_months INT NOT NULL DEFAULT 12,
    import_price INT NOT NULL,
    price_sale INT NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    sale_number INT NOT NULL DEFAULT 0,
    description TEXT,
    image_link VARCHAR(255),
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_by BIGINT,
    updated_by BIGINT,
    created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_brand FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_product_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_product_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

CREATE TABLE product_images (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    image_link VARCHAR(255) NOT NULL,
    sort_order INT NOT NULL DEFAULT 1,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE customers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_code VARCHAR(30) NOT NULL UNIQUE,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150),
    address VARCHAR(255),
    ward VARCHAR(120),
    district VARCHAR(120),
    city VARCHAR(120),
    note VARCHAR(255),
    loyalty_points INT NOT NULL DEFAULT 0,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE staff_carts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sale_people_id BIGINT NOT NULL,
    cart_status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_staff_carts_user FOREIGN KEY (sale_people_id) REFERENCES users(id)
);

CREATE TABLE staff_cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    bar_code VARCHAR(50) NOT NULL,
    image_link VARCHAR(255),
    sale_price INT NOT NULL,
    quantity INT NOT NULL,
    total_money INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_staff_cart_items_cart FOREIGN KEY (cart_id) REFERENCES staff_carts(id),
    CONSTRAINT fk_staff_cart_items_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT uq_staff_cart_items UNIQUE (cart_id, product_id)
);

CREATE TABLE invoices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_code VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    cart_id BIGINT,
    sales_channel VARCHAR(20) NOT NULL DEFAULT 'POS',
    invoice_status VARCHAR(20) NOT NULL DEFAULT 'PAID',
    payment_method VARCHAR(20) NOT NULL DEFAULT 'CASH',
    receive_money INT NOT NULL,
    excess_money INT NOT NULL,
    subtotal_money INT NOT NULL,
    discount_money INT NOT NULL DEFAULT 0,
    total_money INT NOT NULL,
    voucher_id BIGINT NULL,
    voucher_code VARCHAR(50),
    voucher_name VARCHAR(150),
    voucher_type VARCHAR(30),
    voucher_value INT,
    voucher_min_order_value INT,
    quantity INT NOT NULL,
    note VARCHAR(255),
    pdf_link VARCHAR(255),
    created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_invoice_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_invoice_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_invoice_cart FOREIGN KEY (cart_id) REFERENCES staff_carts(id)
);

CREATE TABLE invoice_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    bar_code VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    unit_price INT NOT NULL,
    line_total INT NOT NULL,
    created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_invoice_item_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id),
    CONSTRAINT fk_invoice_item_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE customer_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_code VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    order_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    subtotal_money INT NOT NULL,
    shipping_fee INT NOT NULL DEFAULT 0,
    discount_money INT NOT NULL DEFAULT 0,
    total_money INT NOT NULL,
    recipient_name VARCHAR(150) NOT NULL,
    recipient_phone VARCHAR(20) NOT NULL,
    shipping_address VARCHAR(255) NOT NULL,
    voucher_id BIGINT,
    voucher_code VARCHAR(50),
    voucher_name VARCHAR(150),
    voucher_type VARCHAR(30),
    voucher_value INT,
    voucher_min_order_value INT,
    voucher_discount_released BOOLEAN NOT NULL DEFAULT FALSE,
    note VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer_orders_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE customer_order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    bar_code VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    unit_price INT NOT NULL,
    line_total INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer_order_items_order FOREIGN KEY (order_id) REFERENCES customer_orders(id),
    CONSTRAINT fk_customer_order_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE vouchers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    voucher_code VARCHAR(50) NOT NULL UNIQUE,
    voucher_name VARCHAR(150) NOT NULL,
    voucher_type VARCHAR(30) NOT NULL,
    discount_value INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    starts_at DATETIME,
    ends_at DATETIME,
    max_usage INT NOT NULL DEFAULT 0,
    used_count INT NOT NULL DEFAULT 0,
    min_order_value INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE customer_orders
    ADD CONSTRAINT fk_customer_orders_voucher FOREIGN KEY (voucher_id) REFERENCES vouchers(id);

CREATE TABLE voucher_usage (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    voucher_id BIGINT NOT NULL,
    order_id BIGINT,
    invoice_id BIGINT,
    voucher_code VARCHAR(50) NOT NULL,
    discount_money INT NOT NULL DEFAULT 0,
    usage_status VARCHAR(20) NOT NULL DEFAULT 'USED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_voucher_usage_voucher FOREIGN KEY (voucher_id) REFERENCES vouchers(id),
    CONSTRAINT fk_voucher_usage_order FOREIGN KEY (order_id) REFERENCES customer_orders(id),
    CONSTRAINT fk_voucher_usage_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id),
    CONSTRAINT uq_voucher_usage_order UNIQUE (order_id),
    CONSTRAINT uq_voucher_usage_invoice UNIQUE (invoice_id)
);

CREATE TABLE inventory_transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    quantity_change INT NOT NULL,
    quantity_after INT NOT NULL,
    reference_type VARCHAR(30),
    reference_id BIGINT,
    note VARCHAR(255),
    created_by BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inventory_transactions_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_inventory_transactions_user FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE chat_sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_code VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT,
    guest_name VARCHAR(150),
    channel_name VARCHAR(30) NOT NULL DEFAULT 'AGENT_WEB',
    session_status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat_sessions_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE chat_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    sender_type VARCHAR(20) NOT NULL,
    message_text TEXT NOT NULL,
    prompt_tokens INT NOT NULL DEFAULT 0,
    completion_tokens INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat_messages_session FOREIGN KEY (session_id) REFERENCES chat_sessions(id)
);

CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    actor_user_id BIGINT,
    module_name VARCHAR(50) NOT NULL,
    action_name VARCHAR(50) NOT NULL,
    entity_name VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    payload_json JSON,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (actor_user_id) REFERENCES users(id)
);

CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_brand ON products(brand_id);
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_sale_number ON products(sale_number);
CREATE INDEX idx_customers_phone ON customers(phone_number);
CREATE INDEX idx_invoices_created_date ON invoices(created_date);
CREATE INDEX idx_customer_orders_created_at ON customer_orders(created_at);
CREATE INDEX idx_chat_messages_session ON chat_messages(session_id, created_at);

INSERT INTO roles(role_code, role_name, description) VALUES
('ADMIN', 'Admin', 'Quan tri he thong va CMS'),
('STAFF', 'Staff', 'Nhan vien ban hang tai quay'),
('CUSTOMER', 'Customer', 'Tai khoan khach hang ecommerce');

INSERT INTO users(
    user_name, password_hash, full_name, email, address, phone_number, avatar,
    is_activated, first_login, is_deleted
) VALUES
('admin', 'admin123', 'System Administrator', 'admin@phonestore.local', 'Ho Chi Minh City', '0900000001', '/upload/avatar/admin.png', TRUE, FALSE, FALSE),
('staff1', 'staff123', 'Staff Nguyen', 'staff1@phonestore.local', 'Ho Chi Minh City', '0900000002', '/upload/avatar/staff1.png', TRUE, FALSE, FALSE);

INSERT INTO user_role(user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.role_code = 'ADMIN'
WHERE u.user_name = 'admin';

INSERT INTO user_role(user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.role_code = 'STAFF'
WHERE u.user_name = 'staff1';

INSERT INTO brands(brand_name, brand_slug, description) VALUES
('Apple', 'apple', 'Thuong hieu iPhone'),
('Samsung', 'samsung', 'Thuong hieu Galaxy'),
('Xiaomi', 'xiaomi', 'Thuong hieu Xiaomi');

INSERT INTO categories(category_name, category_slug, description) VALUES
('iPhone', 'iphone', 'Dien thoai Apple'),
('Android Flagship', 'android-flagship', 'Dien thoai Android cao cap'),
('Android Midrange', 'android-midrange', 'Dien thoai Android tam trung');

INSERT INTO products(
    bar_code, sku, name, product_slug, brand_id, category_id, screen_size, ram, rom,
    chipset, battery_capacity, operating_system, color, warranty_months, import_price,
    price_sale, stock_quantity, sale_number, description, image_link, is_featured,
    created_by, updated_by
) VALUES
('IP15PM256', 'APL-IP15PM-256', 'iPhone 15 Pro Max 256GB', 'iphone-15-pro-max-256gb', 1, 1, '6.7 inch', '8GB', '256GB', 'Apple A17 Pro', '4422mAh', 'iOS 17', 'Titan Tu Nhien', 12, 25000000, 29990000, 25, 10, 'Flagship Apple 2023', '/upload/images/iphone-15-pro-max-real.jpg', TRUE, 1, 1),
('SGS24U512', 'SMS-S24U-512', 'Samsung Galaxy S24 Ultra 512GB', 'samsung-galaxy-s24-ultra-512gb', 2, 2, '6.8 inch', '12GB', '512GB', 'Snapdragon 8 Gen 3', '5000mAh', 'Android 14', 'Titan Xam', 12, 23500000, 28990000, 20, 7, 'Flagship Samsung 2024', '/upload/images/s24-ultra-real.jpg', TRUE, 1, 1),
('X14T12-512', 'XMI-14T-512', 'Xiaomi 14T 512GB', 'xiaomi-14t-512gb', 3, 3, '6.67 inch', '12GB', '512GB', 'Dimensity 9300+', '5000mAh', 'Android 14', 'Den', 12, 12000000, 14990000, 18, 3, 'Dien thoai tam trung cao cap', '/upload/images/xiaomi-14t-real.png', FALSE, 1, 1);

INSERT INTO product_images(product_id, image_link, sort_order, is_primary) VALUES
(1, '/upload/images/iphone-15-pro-max-real.jpg', 1, TRUE),
(2, '/upload/images/s24-ultra-real.jpg', 1, TRUE),
(3, '/upload/images/xiaomi-14t-real.png', 1, TRUE);

INSERT INTO customers(customer_code, phone_number, full_name, email, address, ward, district, city, note, loyalty_points) VALUES
('CUS0001', '0901111111', 'Tran Van A', 'vana@example.com', '123 Nguyen Hue', 'Ben Nghe', 'Quan 1', 'Ho Chi Minh City', 'Khach quen tai cua hang', 120),
('CUS0002', '0902222222', 'Le Thi B', 'thib@example.com', '45 Phan Chau Trinh', 'Hai Chau', 'Hai Chau', 'Da Nang', 'Khach mua online', 40);

INSERT INTO inventory_transactions(product_id, transaction_type, quantity_change, quantity_after, reference_type, reference_id, note, created_by) VALUES
(1, 'INITIAL', 25, 25, 'SEED', 1, 'Seed du lieu ban dau', 1),
(2, 'INITIAL', 20, 20, 'SEED', 2, 'Seed du lieu ban dau', 1),
(3, 'INITIAL', 18, 18, 'SEED', 3, 'Seed du lieu ban dau', 1);

DELIMITER $$

DROP PROCEDURE IF EXISTS sp_auth_login $$
CREATE PROCEDURE sp_auth_login(IN p_user_name VARCHAR(100))
BEGIN
    SELECT u.id,
           u.user_name,
           u.password_hash,
           u.full_name,
           u.email,
           u.address,
           u.phone_number,
           u.avatar,
           u.activation_token,
           u.is_activated,
           u.first_login,
           u.is_deleted,
           r.role_code AS role_name
    FROM users u
    JOIN user_role ur ON ur.user_id = u.id
    JOIN roles r ON r.id = ur.role_id
    WHERE u.user_name = p_user_name
      AND u.is_deleted = FALSE
    ORDER BY r.id
    LIMIT 1;
END $$

DROP PROCEDURE IF EXISTS sp_auth_create_staff $$
CREATE PROCEDURE sp_auth_create_staff(
    IN p_user_name VARCHAR(100),
    IN p_password_hash VARCHAR(255),
    IN p_full_name VARCHAR(150),
    IN p_email VARCHAR(150),
    IN p_address VARCHAR(255),
    IN p_phone_number VARCHAR(20),
    IN p_activation_token VARCHAR(255),
    IN p_activation_expires DATETIME
)
BEGIN
    DECLARE v_user_id BIGINT;
    DECLARE v_role_id BIGINT;

    INSERT INTO users(
        user_name, password_hash, full_name, email, address, phone_number,
        activation_token, activation_expires, is_activated, first_login, is_deleted
    ) VALUES (
        p_user_name, p_password_hash, p_full_name, p_email, p_address, p_phone_number,
        p_activation_token, p_activation_expires, IF(p_activation_token IS NULL, TRUE, FALSE), TRUE, FALSE
    );

    SET v_user_id = LAST_INSERT_ID();
    SELECT id INTO v_role_id FROM roles WHERE role_code = 'STAFF' LIMIT 1;
    INSERT INTO user_role(user_id, role_id) VALUES (v_user_id, v_role_id);

    CALL sp_audit_log_add(v_user_id, 'AUTH', 'CREATE_STAFF', 'users', v_user_id, JSON_OBJECT('user_name', p_user_name));

    SELECT v_user_id AS user_id;
END $$

DROP PROCEDURE IF EXISTS sp_auth_activate_account $$
CREATE PROCEDURE sp_auth_activate_account(IN p_token VARCHAR(255))
BEGIN
    UPDATE users
    SET is_activated = TRUE,
        activation_token = NULL,
        activation_expires = NULL,
        updated_at = NOW()
    WHERE activation_token = p_token
      AND is_deleted = FALSE
      AND (activation_expires IS NULL OR activation_expires >= NOW());

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_auth_change_first_password $$
CREATE PROCEDURE sp_auth_change_first_password(
    IN p_user_id BIGINT,
    IN p_password_hash VARCHAR(255)
)
BEGIN
    UPDATE users
    SET password_hash = p_password_hash,
        first_login = FALSE,
        updated_at = NOW()
    WHERE id = p_user_id
      AND is_deleted = FALSE;

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_auth_create_reset_token $$
CREATE PROCEDURE sp_auth_create_reset_token(
    IN p_email VARCHAR(150),
    IN p_token VARCHAR(255),
    IN p_expires DATETIME
)
BEGIN
    UPDATE users
    SET activation_token = p_token,
        activation_expires = p_expires,
        updated_at = NOW()
    WHERE email = p_email
      AND is_deleted = FALSE;

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_auth_reset_password $$
CREATE PROCEDURE sp_auth_reset_password(
    IN p_token VARCHAR(255),
    IN p_password_hash VARCHAR(255)
)
BEGIN
    UPDATE users
    SET password_hash = p_password_hash,
        first_login = FALSE,
        activation_token = NULL,
        activation_expires = NULL,
        updated_at = NOW()
    WHERE activation_token = p_token
      AND activation_expires IS NOT NULL
      AND activation_expires >= NOW()
      AND is_deleted = FALSE;

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_user_get_all_staff $$
CREATE PROCEDURE sp_user_get_all_staff()
BEGIN
    SELECT u.id,
           u.user_name,
           u.full_name,
           u.email,
           u.address,
           u.phone_number,
           u.avatar,
           u.is_activated,
           u.is_deleted
    FROM users u
    JOIN user_role ur ON ur.user_id = u.id
    JOIN roles r ON r.id = ur.role_id
    WHERE r.role_code = 'STAFF'
    ORDER BY u.created_at DESC, u.id DESC;
END $$

DROP PROCEDURE IF EXISTS sp_user_get_profile $$
CREATE PROCEDURE sp_user_get_profile(IN p_user_id BIGINT)
BEGIN
    SELECT u.id,
           u.user_name,
           u.full_name,
           u.email,
           u.address,
           u.phone_number,
           u.avatar,
           u.first_login,
           u.is_activated,
           u.is_deleted,
           r.role_code AS role_name
    FROM users u
    JOIN user_role ur ON ur.user_id = u.id
    JOIN roles r ON r.id = ur.role_id
    WHERE u.id = p_user_id
    ORDER BY r.id
    LIMIT 1;
END $$

DROP PROCEDURE IF EXISTS sp_user_update_profile $$
CREATE PROCEDURE sp_user_update_profile(
    IN p_user_id BIGINT,
    IN p_full_name VARCHAR(150),
    IN p_email VARCHAR(150),
    IN p_address VARCHAR(255),
    IN p_phone_number VARCHAR(20),
    IN p_avatar VARCHAR(255)
)
BEGIN
    UPDATE users
    SET full_name = p_full_name,
        email = p_email,
        address = p_address,
        phone_number = p_phone_number,
        avatar = p_avatar,
        updated_at = NOW()
    WHERE id = p_user_id
      AND is_deleted = FALSE;

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_user_update_avatar $$
CREATE PROCEDURE sp_user_update_avatar(
    IN p_user_id BIGINT,
    IN p_avatar VARCHAR(255)
)
BEGIN
    UPDATE users
    SET avatar = p_avatar,
        updated_at = NOW()
    WHERE id = p_user_id
      AND is_deleted = FALSE;

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_user_change_password $$
CREATE PROCEDURE sp_user_change_password(
    IN p_user_id BIGINT,
    IN p_password_hash VARCHAR(255)
)
BEGIN
    UPDATE users
    SET password_hash = p_password_hash,
        updated_at = NOW()
    WHERE id = p_user_id
      AND is_deleted = FALSE;

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_user_toggle_lock $$
CREATE PROCEDURE sp_user_toggle_lock(IN p_user_id BIGINT)
BEGIN
    UPDATE users
    SET is_deleted = NOT is_deleted,
        updated_at = NOW()
    WHERE id = p_user_id;

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_user_mark_login $$
CREATE PROCEDURE sp_user_mark_login(IN p_user_id BIGINT)
BEGIN
    UPDATE users
    SET last_login_at = NOW(),
        updated_at = NOW()
    WHERE id = p_user_id
      AND is_deleted = FALSE;

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_category_get_all $$
CREATE PROCEDURE sp_category_get_all()
BEGIN
    SELECT id, category_name
    FROM categories
    WHERE is_deleted = FALSE
    ORDER BY category_name;
END $$

DROP PROCEDURE IF EXISTS sp_category_upsert $$
CREATE PROCEDURE sp_category_upsert(
    IN p_category_id BIGINT,
    IN p_category_name VARCHAR(150),
    IN p_category_slug VARCHAR(180),
    IN p_description VARCHAR(255)
)
BEGIN
    IF p_category_id IS NULL OR p_category_id = 0 THEN
        INSERT INTO categories(category_name, category_slug, description)
        VALUES (p_category_name, p_category_slug, p_description);
        SELECT LAST_INSERT_ID() AS category_id;
    ELSE
        UPDATE categories
        SET category_name = p_category_name,
            category_slug = p_category_slug,
            description = p_description,
            updated_at = NOW()
        WHERE id = p_category_id;
        SELECT p_category_id AS category_id;
    END IF;
END $$

DROP PROCEDURE IF EXISTS sp_brand_get_all $$
CREATE PROCEDURE sp_brand_get_all()
BEGIN
    SELECT id, brand_name, brand_slug, logo_url, description
    FROM brands
    WHERE is_deleted = FALSE
    ORDER BY brand_name;
END $$

DROP PROCEDURE IF EXISTS sp_product_get_all $$
CREATE PROCEDURE sp_product_get_all()
BEGIN
    SELECT p.id,
           p.bar_code,
           p.name,
           p.screen_size,
           p.ram,
           p.rom,
           p.import_price,
           p.price_sale,
           p.description,
           p.image_link,
           p.sale_number,
           p.created_date,
           p.updated_date,
           p.is_deleted,
           c.category_name
    FROM products p
    JOIN categories c ON c.id = p.category_id
    WHERE p.is_deleted = FALSE
    ORDER BY p.created_date DESC, p.id DESC;
END $$

DROP PROCEDURE IF EXISTS sp_product_get_public_catalog $$
CREATE PROCEDURE sp_product_get_public_catalog()
BEGIN
    SELECT p.id,
           p.bar_code,
           p.name,
           p.screen_size,
           p.ram,
           p.rom,
           p.import_price,
           p.price_sale,
           p.description,
           p.image_link,
           p.sale_number,
           p.created_date,
           p.updated_date,
           p.is_deleted,
           c.category_name
    FROM products p
    JOIN categories c ON c.id = p.category_id
    WHERE p.is_deleted = FALSE
      AND p.stock_quantity > 0
    ORDER BY p.is_featured DESC, p.sale_number DESC, p.created_date DESC;
END $$

DROP PROCEDURE IF EXISTS sp_product_get_by_barcode $$
CREATE PROCEDURE sp_product_get_by_barcode(IN p_bar_code VARCHAR(50))
BEGIN
    SELECT p.id,
           p.bar_code,
           p.name,
           p.screen_size,
           p.ram,
           p.rom,
           p.import_price,
           p.price_sale,
           p.description,
           p.image_link,
           p.sale_number,
           p.created_date,
           p.updated_date,
           p.is_deleted,
           c.category_name
    FROM products p
    JOIN categories c ON c.id = p.category_id
    WHERE p.bar_code = p_bar_code
    LIMIT 1;
END $$

DROP PROCEDURE IF EXISTS sp_product_search $$
CREATE PROCEDURE sp_product_search(IN p_keyword VARCHAR(150))
BEGIN
    SELECT p.id,
           p.bar_code,
           p.name,
           p.screen_size,
           p.ram,
           p.rom,
           p.import_price,
           p.price_sale,
           p.description,
           p.image_link,
           p.sale_number,
           p.created_date,
           p.updated_date,
           p.is_deleted,
           c.category_name
    FROM products p
    JOIN categories c ON c.id = p.category_id
    LEFT JOIN brands b ON b.id = p.brand_id
    WHERE p.is_deleted = FALSE
      AND (
          p.bar_code LIKE CONCAT('%', p_keyword, '%')
          OR p.sku LIKE CONCAT('%', p_keyword, '%')
          OR p.name LIKE CONCAT('%', p_keyword, '%')
          OR c.category_name LIKE CONCAT('%', p_keyword, '%')
          OR COALESCE(b.brand_name, '') LIKE CONCAT('%', p_keyword, '%')
      )
    ORDER BY p.sale_number DESC, p.created_date DESC;
END $$

DROP PROCEDURE IF EXISTS sp_product_create $$
CREATE PROCEDURE sp_product_create(
    IN p_bar_code VARCHAR(50),
    IN p_name VARCHAR(200),
    IN p_screen_size VARCHAR(50),
    IN p_ram VARCHAR(50),
    IN p_rom VARCHAR(50),
    IN p_import_price INT,
    IN p_price_sale INT,
    IN p_description TEXT,
    IN p_image_link VARCHAR(255),
    IN p_category_id BIGINT
)
BEGIN
    INSERT INTO products(
        bar_code, sku, name, product_slug, category_id, screen_size, ram, rom,
        import_price, price_sale, description, image_link, stock_quantity, sale_number,
        created_by, updated_by
    ) VALUES (
        p_bar_code,
        CONCAT('SKU-', p_bar_code),
        p_name,
        LOWER(REPLACE(REPLACE(CONCAT(p_name, '-', p_bar_code), ' ', '-'), '--', '-')),
        p_category_id,
        p_screen_size,
        p_ram,
        p_rom,
        p_import_price,
        p_price_sale,
        p_description,
        p_image_link,
        0,
        0,
        1,
        1
    );

    SELECT LAST_INSERT_ID() AS product_id;
END $$

DROP PROCEDURE IF EXISTS sp_product_update $$
CREATE PROCEDURE sp_product_update(
    IN p_bar_code VARCHAR(50),
    IN p_name VARCHAR(200),
    IN p_screen_size VARCHAR(50),
    IN p_import_price INT,
    IN p_price_sale INT,
    IN p_ram VARCHAR(50),
    IN p_rom VARCHAR(50),
    IN p_description TEXT,
    IN p_sale_number INT,
    IN p_image_link VARCHAR(255),
    IN p_category_id BIGINT
)
BEGIN
    UPDATE products
    SET name = p_name,
        screen_size = p_screen_size,
        import_price = p_import_price,
        price_sale = p_price_sale,
        ram = p_ram,
        rom = p_rom,
        description = p_description,
        sale_number = p_sale_number,
        image_link = COALESCE(p_image_link, image_link),
        category_id = p_category_id,
        updated_date = NOW()
    WHERE bar_code = p_bar_code
      AND is_deleted = FALSE;

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_product_soft_delete $$
CREATE PROCEDURE sp_product_soft_delete(IN p_bar_code VARCHAR(50))
BEGIN
    UPDATE products
    SET is_deleted = TRUE,
        updated_date = NOW()
    WHERE bar_code = p_bar_code;

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_product_add_image $$
CREATE PROCEDURE sp_product_add_image(
    IN p_product_id BIGINT,
    IN p_image_link VARCHAR(255),
    IN p_sort_order INT,
    IN p_is_primary BOOLEAN
)
BEGIN
    IF p_is_primary THEN
        UPDATE product_images
        SET is_primary = FALSE
        WHERE product_id = p_product_id;
    END IF;

    INSERT INTO product_images(product_id, image_link, sort_order, is_primary)
    VALUES (p_product_id, p_image_link, COALESCE(p_sort_order, 1), COALESCE(p_is_primary, FALSE));

    IF p_is_primary THEN
        UPDATE products
        SET image_link = p_image_link,
            updated_date = NOW()
        WHERE id = p_product_id;
    END IF;

    SELECT LAST_INSERT_ID() AS product_image_id;
END $$

DROP PROCEDURE IF EXISTS sp_product_adjust_stock $$
CREATE PROCEDURE sp_product_adjust_stock(
    IN p_product_id BIGINT,
    IN p_quantity_change INT,
    IN p_reference_type VARCHAR(30),
    IN p_reference_id BIGINT,
    IN p_note VARCHAR(255),
    IN p_created_by BIGINT
)
BEGIN
    DECLARE v_new_quantity INT;

    UPDATE products
    SET stock_quantity = stock_quantity + p_quantity_change,
        updated_date = NOW()
    WHERE id = p_product_id
      AND is_deleted = FALSE;

    SELECT stock_quantity INTO v_new_quantity
    FROM products
    WHERE id = p_product_id;

    INSERT INTO inventory_transactions(
        product_id, transaction_type, quantity_change, quantity_after,
        reference_type, reference_id, note, created_by
    ) VALUES (
        p_product_id,
        CASE
            WHEN p_quantity_change > 0 THEN 'ADJUST_IN'
            ELSE 'ADJUST_OUT'
        END,
        p_quantity_change,
        v_new_quantity,
        p_reference_type,
        p_reference_id,
        p_note,
        p_created_by
    );

    SELECT v_new_quantity AS stock_quantity;
END $$

DROP PROCEDURE IF EXISTS sp_product_increase_sale_number $$
CREATE PROCEDURE sp_product_increase_sale_number(
    IN p_product_id BIGINT,
    IN p_quantity INT
)
BEGIN
    DECLARE v_quantity_after INT;

    UPDATE products
    SET sale_number = sale_number + p_quantity,
        stock_quantity = stock_quantity - p_quantity,
        updated_date = NOW()
    WHERE id = p_product_id
      AND is_deleted = FALSE;

    SELECT stock_quantity INTO v_quantity_after
    FROM products
    WHERE id = p_product_id;

    INSERT INTO inventory_transactions(
        product_id, transaction_type, quantity_change, quantity_after,
        reference_type, reference_id, note, created_by
    ) VALUES (
        p_product_id,
        'SALE',
        -p_quantity,
        v_quantity_after,
        'PRODUCT',
        p_product_id,
        'Tang doanh so ban',
        NULL
    );

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_cart_ensure_open $$
CREATE PROCEDURE sp_cart_ensure_open(IN p_staff_id BIGINT)
BEGIN
    DECLARE v_cart_id BIGINT;

    SELECT id INTO v_cart_id
    FROM staff_carts
    WHERE sale_people_id = p_staff_id
      AND cart_status = 'OPEN'
    ORDER BY id DESC
    LIMIT 1;

    IF v_cart_id IS NULL THEN
        INSERT INTO staff_carts(sale_people_id, cart_status)
        VALUES (p_staff_id, 'OPEN');
        SET v_cart_id = LAST_INSERT_ID();
    END IF;

    SELECT v_cart_id AS cart_id;
END $$

DROP PROCEDURE IF EXISTS sp_cart_get_by_staff $$
CREATE PROCEDURE sp_cart_get_by_staff(IN p_staff_id BIGINT)
BEGIN
    SELECT sci.id,
           sc.sale_people_id,
           sci.product_id,
           sci.name,
           sci.image_link,
           sci.sale_price,
           sci.quantity,
           sci.total_money
    FROM staff_cart_items sci
    JOIN staff_carts sc ON sc.id = sci.cart_id
    WHERE sc.sale_people_id = p_staff_id
      AND sc.cart_status = 'OPEN'
    ORDER BY sci.id ASC;
END $$

DROP PROCEDURE IF EXISTS sp_cart_add_item $$
CREATE PROCEDURE sp_cart_add_item(
    IN p_staff_id BIGINT,
    IN p_product_id BIGINT,
    IN p_quantity INT
)
BEGIN
    DECLARE v_cart_id BIGINT;
    DECLARE v_item_id BIGINT;
    DECLARE v_name VARCHAR(200);
    DECLARE v_bar_code VARCHAR(50);
    DECLARE v_image_link VARCHAR(255);
    DECLARE v_price INT;

    CALL sp_cart_ensure_open(p_staff_id);

    SELECT id INTO v_cart_id
    FROM staff_carts
    WHERE sale_people_id = p_staff_id
      AND cart_status = 'OPEN'
    ORDER BY id DESC
    LIMIT 1;

    SELECT name, bar_code, image_link, price_sale
    INTO v_name, v_bar_code, v_image_link, v_price
    FROM products
    WHERE id = p_product_id
      AND is_deleted = FALSE;

    SELECT id INTO v_item_id
    FROM staff_cart_items
    WHERE cart_id = v_cart_id
      AND product_id = p_product_id
    LIMIT 1;

    IF v_item_id IS NULL THEN
        INSERT INTO staff_cart_items(
            cart_id, product_id, name, bar_code, image_link, sale_price, quantity, total_money
        ) VALUES (
            v_cart_id, p_product_id, v_name, v_bar_code, v_image_link, v_price, p_quantity, v_price * p_quantity
        );
    ELSE
        UPDATE staff_cart_items
        SET quantity = quantity + p_quantity,
            total_money = (quantity + p_quantity) * sale_price,
            updated_at = NOW()
        WHERE id = v_item_id;
    END IF;

    SELECT TRUE AS success;
END $$

DROP PROCEDURE IF EXISTS sp_cart_update_quantity $$
CREATE PROCEDURE sp_cart_update_quantity(
    IN p_cart_id BIGINT,
    IN p_quantity INT
)
BEGIN
    UPDATE staff_cart_items
    SET quantity = p_quantity,
        total_money = sale_price * p_quantity,
        updated_at = NOW()
    WHERE id = p_cart_id;

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_cart_delete_item $$
CREATE PROCEDURE sp_cart_delete_item(IN p_cart_id BIGINT)
BEGIN
    DELETE FROM staff_cart_items
    WHERE id = p_cart_id;

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_cart_clear_by_staff $$
CREATE PROCEDURE sp_cart_clear_by_staff(IN p_staff_id BIGINT)
BEGIN
    DELETE sci
    FROM staff_cart_items sci
    JOIN staff_carts sc ON sc.id = sci.cart_id
    WHERE sc.sale_people_id = p_staff_id
      AND sc.cart_status = 'OPEN';

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_customer_get_by_phone $$
CREATE PROCEDURE sp_customer_get_by_phone(IN p_phone_number VARCHAR(20))
BEGIN
    SELECT id,
           customer_code,
           phone_number,
           full_name,
           email,
           address,
           ward,
           district,
           city,
           note,
           loyalty_points,
           created_at
    FROM customers
    WHERE phone_number = p_phone_number
      AND is_deleted = FALSE
    LIMIT 1;
END $$

DROP PROCEDURE IF EXISTS sp_customer_search $$
CREATE PROCEDURE sp_customer_search(IN p_keyword VARCHAR(150))
BEGIN
    SELECT id,
           customer_code,
           phone_number,
           full_name,
           email,
           address,
           ward,
           district,
           city,
           note,
           loyalty_points,
           created_at
    FROM customers
    WHERE is_deleted = FALSE
      AND (
          phone_number LIKE CONCAT('%', p_keyword, '%')
          OR full_name LIKE CONCAT('%', p_keyword, '%')
          OR COALESCE(email, '') LIKE CONCAT('%', p_keyword, '%')
      )
    ORDER BY created_at DESC;
END $$

DROP PROCEDURE IF EXISTS sp_customer_create_if_not_exists $$
CREATE PROCEDURE sp_customer_create_if_not_exists(
    IN p_phone_number VARCHAR(20),
    IN p_full_name VARCHAR(150),
    IN p_address VARCHAR(255)
)
BEGIN
    DECLARE v_customer_id BIGINT;
    DECLARE v_next_customer_no BIGINT;

    SELECT id INTO v_customer_id
    FROM customers
    WHERE phone_number = p_phone_number
      AND is_deleted = FALSE
    LIMIT 1;

    IF v_customer_id IS NULL THEN
        SELECT COALESCE(MAX(id), 0) + 1 INTO v_next_customer_no
        FROM customers;

        INSERT INTO customers(customer_code, phone_number, full_name, address)
        VALUES (
            CONCAT('CUS', LPAD(v_next_customer_no, 4, '0')),
            p_phone_number,
            p_full_name,
            p_address
        );
        SET v_customer_id = LAST_INSERT_ID();
    ELSE
        UPDATE customers
        SET full_name = COALESCE(NULLIF(p_full_name, ''), full_name),
            address = COALESCE(NULLIF(p_address, ''), address),
            updated_at = NOW()
        WHERE id = v_customer_id;
    END IF;

    SELECT v_customer_id AS customer_id;
END $$

DROP PROCEDURE IF EXISTS sp_customer_purchase_history $$
CREATE PROCEDURE sp_customer_purchase_history(IN p_customer_id BIGINT)
BEGIN
    SELECT history.invoice_code,
           history.sale_people,
           history.customer_name,
           history.receive_money,
           history.excess_money,
           history.total_money,
           history.quantity,
           history.created_date
    FROM (
        SELECT i.invoice_code,
               u.full_name AS sale_people,
               c.full_name AS customer_name,
               i.receive_money,
               i.excess_money,
               i.total_money,
               i.quantity,
               i.created_date
        FROM invoices i
        JOIN customers c ON c.id = i.customer_id
        JOIN users u ON u.id = i.user_id
        WHERE c.id = p_customer_id

        UNION ALL

        SELECT o.order_code AS invoice_code,
               'Website' AS sale_people,
               c.full_name AS customer_name,
               o.total_money AS receive_money,
               0 AS excess_money,
               o.total_money,
               COALESCE(SUM(oi.quantity), 0) AS quantity,
               o.created_at AS created_date
        FROM customer_orders o
        JOIN customers c ON c.id = o.customer_id
        LEFT JOIN customer_order_items oi ON oi.order_id = o.id
        WHERE c.id = p_customer_id
        GROUP BY o.id, o.order_code, c.full_name, o.total_money, o.created_at
    ) history
    ORDER BY history.created_date DESC;
END $$

DROP PROCEDURE IF EXISTS sp_invoice_create $$
CREATE PROCEDURE sp_invoice_create(
    IN p_invoice_code VARCHAR(50),
    IN p_customer_id BIGINT,
    IN p_user_id BIGINT,
    IN p_receive_money INT,
    IN p_excess_money INT,
    IN p_subtotal_money INT,
    IN p_discount_money INT,
    IN p_total_money INT,
    IN p_quantity INT,
    IN p_voucher_id BIGINT,
    IN p_voucher_code VARCHAR(50),
    IN p_voucher_name VARCHAR(150),
    IN p_voucher_type VARCHAR(30),
    IN p_voucher_value INT,
    IN p_voucher_min_order_value INT
)
BEGIN
    DECLARE v_cart_id BIGINT;

    SELECT id INTO v_cart_id
    FROM staff_carts
    WHERE sale_people_id = p_user_id
      AND cart_status = 'OPEN'
    ORDER BY id DESC
    LIMIT 1;

    INSERT INTO invoices(
        invoice_code, customer_id, user_id, cart_id, sales_channel, invoice_status,
        payment_method, receive_money, excess_money, subtotal_money, discount_money, total_money,
        voucher_id, voucher_code, voucher_name, voucher_type, voucher_value, voucher_min_order_value, quantity
    ) VALUES (
        p_invoice_code, p_customer_id, p_user_id, v_cart_id, 'POS', 'PAID',
        'CASH', p_receive_money, p_excess_money, p_subtotal_money, p_discount_money, p_total_money,
        p_voucher_id, p_voucher_code, p_voucher_name, p_voucher_type, p_voucher_value, p_voucher_min_order_value, p_quantity
    );

    SELECT LAST_INSERT_ID() AS invoice_id;
END $$

DROP PROCEDURE IF EXISTS sp_invoice_item_create $$
CREATE PROCEDURE sp_invoice_item_create(
    IN p_invoice_id BIGINT,
    IN p_product_id BIGINT,
    IN p_quantity INT,
    IN p_unit_price INT
)
BEGIN
    DECLARE v_product_name VARCHAR(200);
    DECLARE v_bar_code VARCHAR(50);
    DECLARE v_quantity_after INT;

    SELECT name, bar_code
    INTO v_product_name, v_bar_code
    FROM products
    WHERE id = p_product_id;

    INSERT INTO invoice_items(
        invoice_id, product_id, product_name, bar_code, quantity, unit_price, line_total
    ) VALUES (
        p_invoice_id, p_product_id, v_product_name, v_bar_code, p_quantity, p_unit_price, p_quantity * p_unit_price
    );

    UPDATE products
    SET sale_number = sale_number + p_quantity,
        stock_quantity = stock_quantity - p_quantity,
        updated_date = NOW()
    WHERE id = p_product_id;

    SELECT stock_quantity INTO v_quantity_after
    FROM products
    WHERE id = p_product_id;

    INSERT INTO inventory_transactions(
        product_id, transaction_type, quantity_change, quantity_after,
        reference_type, reference_id, note, created_by
    ) VALUES (
        p_product_id,
        'SALE',
        -p_quantity,
        v_quantity_after,
        'INVOICE',
        p_invoice_id,
        'Ban hang tai quay',
        NULL
    );

    SELECT LAST_INSERT_ID() AS invoice_item_id;
END $$

DROP PROCEDURE IF EXISTS sp_invoice_attach_pdf $$
CREATE PROCEDURE sp_invoice_attach_pdf(
    IN p_invoice_code VARCHAR(50),
    IN p_pdf_link VARCHAR(255)
)
BEGIN
    UPDATE invoices
    SET pdf_link = p_pdf_link,
        updated_date = NOW()
    WHERE invoice_code = p_invoice_code;

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_invoice_get_detail $$
CREATE PROCEDURE sp_invoice_get_detail(IN p_invoice_code VARCHAR(50))
BEGIN
    SELECT i.invoice_code,
           i.subtotal_money,
           i.discount_money,
           i.total_money,
           i.voucher_code,
           ii.product_name,
           ii.quantity,
           ii.unit_price,
           ii.created_date
    FROM invoice_items ii
    JOIN invoices i ON i.id = ii.invoice_id
    WHERE i.invoice_code = p_invoice_code
    ORDER BY ii.id ASC;
END $$

DROP PROCEDURE IF EXISTS sp_invoice_get_all $$
CREATE PROCEDURE sp_invoice_get_all()
BEGIN
    SELECT i.id,
           i.invoice_code,
           c.full_name AS customer_name,
           u.full_name AS sale_people,
           i.total_money,
           i.quantity,
           i.invoice_status,
           i.pdf_link,
           i.created_date
    FROM invoices i
    JOIN customers c ON c.id = i.customer_id
    JOIN users u ON u.id = i.user_id
    ORDER BY i.created_date DESC;
END $$

DROP PROCEDURE IF EXISTS sp_invoice_complete_checkout $$
CREATE PROCEDURE sp_invoice_complete_checkout(IN p_staff_id BIGINT)
BEGIN
    UPDATE staff_carts
    SET cart_status = 'CHECKED_OUT',
        updated_at = NOW()
    WHERE sale_people_id = p_staff_id
      AND cart_status = 'OPEN';

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_order_create $$
CREATE PROCEDURE sp_order_create(
    IN p_order_code VARCHAR(50),
    IN p_customer_id BIGINT,
    IN p_subtotal_money INT,
    IN p_shipping_fee INT,
    IN p_discount_money INT,
    IN p_total_money INT,
    IN p_recipient_name VARCHAR(150),
    IN p_recipient_phone VARCHAR(20),
    IN p_shipping_address VARCHAR(255),
    IN p_voucher_id BIGINT,
    IN p_voucher_code VARCHAR(50),
    IN p_voucher_name VARCHAR(150),
    IN p_voucher_type VARCHAR(30),
    IN p_voucher_value INT,
    IN p_voucher_min_order_value INT,
    IN p_note VARCHAR(255)
)
BEGIN
    INSERT INTO customer_orders(
        order_code, customer_id, subtotal_money, shipping_fee, discount_money, total_money,
        recipient_name, recipient_phone, shipping_address,
        voucher_id, voucher_code, voucher_name, voucher_type, voucher_value, voucher_min_order_value,
        note
    ) VALUES (
        p_order_code, p_customer_id, p_subtotal_money, p_shipping_fee, p_discount_money, p_total_money,
        p_recipient_name, p_recipient_phone, p_shipping_address,
        p_voucher_id, p_voucher_code, p_voucher_name, p_voucher_type, p_voucher_value, p_voucher_min_order_value,
        p_note
    );

    SELECT LAST_INSERT_ID() AS order_id;
END $$

DROP PROCEDURE IF EXISTS sp_order_add_item $$
CREATE PROCEDURE sp_order_add_item(
    IN p_order_id BIGINT,
    IN p_product_id BIGINT,
    IN p_quantity INT,
    IN p_unit_price INT
)
BEGIN
    DECLARE v_product_name VARCHAR(200);
    DECLARE v_bar_code VARCHAR(50);
    DECLARE v_quantity_after INT;

    SELECT name, bar_code
    INTO v_product_name, v_bar_code
    FROM products
    WHERE id = p_product_id;

    INSERT INTO customer_order_items(
        order_id, product_id, product_name, bar_code, quantity, unit_price, line_total
    ) VALUES (
        p_order_id, p_product_id, v_product_name, v_bar_code, p_quantity, p_unit_price, p_quantity * p_unit_price
    );

    UPDATE products
    SET stock_quantity = stock_quantity - p_quantity,
        updated_date = NOW()
    WHERE id = p_product_id;

    SELECT stock_quantity INTO v_quantity_after
    FROM products
    WHERE id = p_product_id;

    INSERT INTO inventory_transactions(
        product_id, transaction_type, quantity_change, quantity_after,
        reference_type, reference_id, note, created_by
    ) VALUES (
        p_product_id,
        'ORDER',
        -p_quantity,
        v_quantity_after,
        'ORDER',
        p_order_id,
        'Dat hang online',
        NULL
    );

    SELECT LAST_INSERT_ID() AS order_item_id;
END $$

DROP PROCEDURE IF EXISTS sp_order_update_status $$
CREATE PROCEDURE sp_order_update_status(
    IN p_order_id BIGINT,
    IN p_order_status VARCHAR(20),
    IN p_payment_status VARCHAR(20)
)
BEGIN
    IF UPPER(COALESCE(p_order_status, '')) = 'CANCELLED' THEN
        CALL sp_voucher_release_for_order(p_order_id);
    END IF;

    UPDATE customer_orders
    SET order_status = p_order_status,
        payment_status = p_payment_status,
        updated_at = NOW()
    WHERE id = p_order_id;

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_order_get_detail $$
CREATE PROCEDURE sp_order_get_detail(IN p_order_code VARCHAR(50))
BEGIN
    SELECT o.order_code,
           o.order_status,
           o.payment_status,
           o.total_money,
           o.shipping_address,
           o.voucher_code,
           o.discount_money,
           oi.product_name,
           oi.quantity,
           oi.unit_price,
           oi.line_total,
           o.created_at
    FROM customer_orders o
    JOIN customer_order_items oi ON oi.order_id = o.id
    WHERE o.order_code = p_order_code
    ORDER BY oi.id ASC;
END $$

DROP PROCEDURE IF EXISTS sp_order_get_by_customer_phone $$
CREATE PROCEDURE sp_order_get_by_customer_phone(IN p_phone_number VARCHAR(20))
BEGIN
    SELECT o.id,
           o.order_code,
           c.full_name,
           o.order_status,
           o.payment_status,
           o.total_money,
           o.voucher_code,
           o.discount_money,
           o.created_at
    FROM customer_orders o
    JOIN customers c ON c.id = o.customer_id
    WHERE c.phone_number = p_phone_number
    ORDER BY o.created_at DESC;
END $$

DROP PROCEDURE IF EXISTS sp_order_get_all $$
CREATE PROCEDURE sp_order_get_all()
BEGIN
    SELECT o.id,
           o.order_code,
           c.full_name AS customer_name,
           o.recipient_name,
           o.recipient_phone,
           o.order_status,
           o.payment_status,
           o.total_money,
           o.voucher_code,
           o.discount_money,
           o.created_at
    FROM customer_orders o
    JOIN customers c ON c.id = o.customer_id
    ORDER BY o.created_at DESC, o.id DESC;
END $$

DROP PROCEDURE IF EXISTS sp_voucher_get_all $$
CREATE PROCEDURE sp_voucher_get_all(
    IN p_keyword VARCHAR(100),
    IN p_status VARCHAR(20),
    IN p_voucher_type VARCHAR(30),
    IN p_expired_only BOOLEAN
)
BEGIN
    SELECT v.id,
           v.voucher_code,
           v.voucher_name,
           v.voucher_type,
           v.discount_value,
           v.is_active,
           v.starts_at,
           v.ends_at,
           v.max_usage,
           v.used_count,
           v.min_order_value,
           CASE
               WHEN v.ends_at IS NOT NULL AND v.ends_at < NOW() THEN 'EXPIRED'
               WHEN v.is_active = FALSE THEN 'DISABLED'
               WHEN v.max_usage > 0 AND v.used_count >= v.max_usage THEN 'EXHAUSTED'
               WHEN v.starts_at IS NOT NULL AND v.starts_at > NOW() THEN 'SCHEDULED'
               ELSE 'ACTIVE'
           END AS effective_status
    FROM vouchers v
    WHERE (p_keyword IS NULL OR p_keyword = ''
           OR v.voucher_code LIKE CONCAT('%', p_keyword, '%')
           OR v.voucher_name LIKE CONCAT('%', p_keyword, '%'))
      AND (p_voucher_type IS NULL OR p_voucher_type = '' OR v.voucher_type = p_voucher_type)
      AND (
            p_status IS NULL OR p_status = ''
            OR (p_status = 'ACTIVE' AND v.is_active = TRUE
                AND (v.starts_at IS NULL OR v.starts_at <= NOW())
                AND (v.ends_at IS NULL OR v.ends_at >= NOW())
                AND (v.max_usage <= 0 OR v.used_count < v.max_usage))
            OR (p_status = 'DISABLED' AND v.is_active = FALSE)
            OR (p_status = 'EXPIRED' AND v.ends_at IS NOT NULL AND v.ends_at < NOW())
            OR (p_status = 'EXHAUSTED' AND v.max_usage > 0 AND v.used_count >= v.max_usage)
            OR (p_status = 'SCHEDULED' AND v.starts_at IS NOT NULL AND v.starts_at > NOW())
          )
      AND (COALESCE(p_expired_only, FALSE) = FALSE OR (v.ends_at IS NOT NULL AND v.ends_at < NOW()))
    ORDER BY v.created_at DESC, v.id DESC;
END $$

DROP PROCEDURE IF EXISTS sp_voucher_get_by_id $$
CREATE PROCEDURE sp_voucher_get_by_id(IN p_voucher_id BIGINT)
BEGIN
    SELECT v.id,
           v.voucher_code,
           v.voucher_name,
           v.voucher_type,
           v.discount_value,
           v.is_active,
           v.starts_at,
           v.ends_at,
           v.max_usage,
           v.used_count,
           v.min_order_value
    FROM vouchers v
    WHERE v.id = p_voucher_id;
END $$

DROP PROCEDURE IF EXISTS sp_voucher_create $$
CREATE PROCEDURE sp_voucher_create(
    IN p_voucher_code VARCHAR(50),
    IN p_voucher_name VARCHAR(150),
    IN p_voucher_type VARCHAR(30),
    IN p_discount_value INT,
    IN p_is_active BOOLEAN,
    IN p_starts_at DATETIME,
    IN p_ends_at DATETIME,
    IN p_max_usage INT,
    IN p_min_order_value INT
)
BEGIN
    IF p_voucher_code IS NULL OR TRIM(p_voucher_code) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher code is required.';
    END IF;
    IF p_voucher_name IS NULL OR TRIM(p_voucher_name) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher name is required.';
    END IF;
    IF p_voucher_type NOT IN ('PERCENT', 'FIXED_AMOUNT', 'FREE_SHIPPING') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Unsupported voucher type.';
    END IF;
    IF p_voucher_type = 'PERCENT' AND (p_discount_value <= 0 OR p_discount_value > 100) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Percent voucher must be between 1 and 100.';
    END IF;
    IF p_voucher_type = 'FIXED_AMOUNT' AND p_discount_value <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Fixed amount voucher must be greater than 0.';
    END IF;
    IF p_voucher_type = 'FREE_SHIPPING' THEN
        SET p_discount_value = 0;
    END IF;
    IF p_starts_at IS NOT NULL AND p_ends_at IS NOT NULL AND p_starts_at > p_ends_at THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher start time must be before end time.';
    END IF;

    INSERT INTO vouchers(
        voucher_code, voucher_name, voucher_type, discount_value,
        is_active, starts_at, ends_at, max_usage, used_count, min_order_value
    ) VALUES (
        UPPER(TRIM(p_voucher_code)), TRIM(p_voucher_name), p_voucher_type, p_discount_value,
        COALESCE(p_is_active, TRUE), p_starts_at, p_ends_at, COALESCE(p_max_usage, 0), 0, COALESCE(p_min_order_value, 0)
    );

    SELECT LAST_INSERT_ID() AS voucher_id;
END $$

DROP PROCEDURE IF EXISTS sp_voucher_update $$
CREATE PROCEDURE sp_voucher_update(
    IN p_voucher_id BIGINT,
    IN p_voucher_code VARCHAR(50),
    IN p_voucher_name VARCHAR(150),
    IN p_voucher_type VARCHAR(30),
    IN p_discount_value INT,
    IN p_is_active BOOLEAN,
    IN p_starts_at DATETIME,
    IN p_ends_at DATETIME,
    IN p_max_usage INT,
    IN p_min_order_value INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM vouchers WHERE id = p_voucher_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher not found.';
    END IF;
    IF EXISTS (
        SELECT 1
        FROM vouchers
        WHERE voucher_code = UPPER(TRIM(p_voucher_code))
          AND id <> p_voucher_id
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher code already exists.';
    END IF;
    IF p_voucher_type NOT IN ('PERCENT', 'FIXED_AMOUNT', 'FREE_SHIPPING') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Unsupported voucher type.';
    END IF;
    IF p_voucher_type = 'PERCENT' AND (p_discount_value <= 0 OR p_discount_value > 100) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Percent voucher must be between 1 and 100.';
    END IF;
    IF p_voucher_type = 'FIXED_AMOUNT' AND p_discount_value <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Fixed amount voucher must be greater than 0.';
    END IF;
    IF p_voucher_type = 'FREE_SHIPPING' THEN
        SET p_discount_value = 0;
    END IF;
    IF p_starts_at IS NOT NULL AND p_ends_at IS NOT NULL AND p_starts_at > p_ends_at THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher start time must be before end time.';
    END IF;

    UPDATE vouchers
    SET voucher_code = UPPER(TRIM(p_voucher_code)),
        voucher_name = TRIM(p_voucher_name),
        voucher_type = p_voucher_type,
        discount_value = p_discount_value,
        is_active = COALESCE(p_is_active, is_active),
        starts_at = p_starts_at,
        ends_at = p_ends_at,
        max_usage = COALESCE(p_max_usage, 0),
        min_order_value = COALESCE(p_min_order_value, 0),
        updated_at = NOW()
    WHERE id = p_voucher_id;

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_voucher_delete $$
CREATE PROCEDURE sp_voucher_delete(IN p_voucher_id BIGINT)
BEGIN
    IF EXISTS (SELECT 1 FROM voucher_usage WHERE voucher_id = p_voucher_id LIMIT 1) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher has usage history and cannot be deleted.';
    END IF;

    DELETE FROM vouchers
    WHERE id = p_voucher_id;

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_voucher_toggle $$
CREATE PROCEDURE sp_voucher_toggle(
    IN p_voucher_id BIGINT,
    IN p_is_active BOOLEAN
)
BEGIN
    UPDATE vouchers
    SET is_active = COALESCE(p_is_active, is_active),
        updated_at = NOW()
    WHERE id = p_voucher_id;

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_voucher_validate_public $$
CREATE PROCEDURE sp_voucher_validate_public(
    IN p_voucher_code VARCHAR(50),
    IN p_subtotal_money INT,
    IN p_shipping_fee INT
)
BEGIN
    DECLARE v_id BIGINT;
    DECLARE v_name VARCHAR(150);
    DECLARE v_type VARCHAR(30);
    DECLARE v_value INT;
    DECLARE v_is_active BOOLEAN;
    DECLARE v_starts_at DATETIME;
    DECLARE v_ends_at DATETIME;
    DECLARE v_max_usage INT;
    DECLARE v_used_count INT;
    DECLARE v_min_order_value INT;
    DECLARE v_discount_money INT DEFAULT 0;

    SELECT id, voucher_name, voucher_type, discount_value, is_active,
           starts_at, ends_at, max_usage, used_count, min_order_value
    INTO v_id, v_name, v_type, v_value, v_is_active,
         v_starts_at, v_ends_at, v_max_usage, v_used_count, v_min_order_value
    FROM vouchers
    WHERE voucher_code = UPPER(TRIM(p_voucher_code))
    LIMIT 1;

    IF v_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher code does not exist.';
    END IF;
    IF v_is_active = FALSE THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher is disabled.';
    END IF;
    IF v_starts_at IS NOT NULL AND v_starts_at > NOW() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher is not active yet.';
    END IF;
    IF v_ends_at IS NOT NULL AND v_ends_at < NOW() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher has expired.';
    END IF;
    IF v_max_usage > 0 AND v_used_count >= v_max_usage THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher has reached its usage limit.';
    END IF;
    IF COALESCE(p_subtotal_money, 0) < COALESCE(v_min_order_value, 0) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Order does not meet voucher minimum value.';
    END IF;

    IF v_type = 'PERCENT' THEN
        SET v_discount_money = FLOOR(COALESCE(p_subtotal_money, 0) * v_value / 100);
    ELSEIF v_type = 'FIXED_AMOUNT' THEN
        SET v_discount_money = v_value;
    ELSEIF v_type = 'FREE_SHIPPING' THEN
        SET v_discount_money = COALESCE(p_shipping_fee, 0);
    END IF;

    SET v_discount_money = LEAST(v_discount_money, COALESCE(p_subtotal_money, 0) + COALESCE(p_shipping_fee, 0));
    SET v_discount_money = GREATEST(v_discount_money, 0);

    SELECT v_id,
           UPPER(TRIM(p_voucher_code)),
           v_name,
           v_type,
           v_value,
           COALESCE(v_min_order_value, 0),
           v_discount_money,
           COALESCE(p_subtotal_money, 0) + COALESCE(p_shipping_fee, 0) AS total_before_discount,
           GREATEST(COALESCE(p_subtotal_money, 0) + COALESCE(p_shipping_fee, 0) - v_discount_money, 0) AS total_after_discount;
END $$

DROP PROCEDURE IF EXISTS sp_voucher_consume_for_order $$
CREATE PROCEDURE sp_voucher_consume_for_order(
    IN p_order_id BIGINT,
    IN p_voucher_code VARCHAR(50),
    IN p_subtotal_money INT,
    IN p_shipping_fee INT
)
BEGIN
    DECLARE v_voucher_id BIGINT;
    DECLARE v_name VARCHAR(150);
    DECLARE v_type VARCHAR(30);
    DECLARE v_value INT;
    DECLARE v_is_active BOOLEAN;
    DECLARE v_starts_at DATETIME;
    DECLARE v_ends_at DATETIME;
    DECLARE v_max_usage INT;
    DECLARE v_used_count INT;
    DECLARE v_min_order_value INT;
    DECLARE v_discount_money INT DEFAULT 0;

    SELECT id, voucher_name, voucher_type, discount_value, is_active,
           starts_at, ends_at, max_usage, used_count, min_order_value
    INTO v_voucher_id, v_name, v_type, v_value, v_is_active,
         v_starts_at, v_ends_at, v_max_usage, v_used_count, v_min_order_value
    FROM vouchers
    WHERE voucher_code = UPPER(TRIM(p_voucher_code))
    FOR UPDATE;

    IF v_voucher_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher code does not exist.';
    END IF;
    IF v_is_active = FALSE THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher is disabled.';
    END IF;
    IF v_starts_at IS NOT NULL AND v_starts_at > NOW() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher is not active yet.';
    END IF;
    IF v_ends_at IS NOT NULL AND v_ends_at < NOW() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher has expired.';
    END IF;
    IF v_max_usage > 0 AND v_used_count >= v_max_usage THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher has reached its usage limit.';
    END IF;
    IF COALESCE(p_subtotal_money, 0) < COALESCE(v_min_order_value, 0) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Order does not meet voucher minimum value.';
    END IF;

    IF v_type = 'PERCENT' THEN
        SET v_discount_money = FLOOR(COALESCE(p_subtotal_money, 0) * v_value / 100);
    ELSEIF v_type = 'FIXED_AMOUNT' THEN
        SET v_discount_money = v_value;
    ELSEIF v_type = 'FREE_SHIPPING' THEN
        SET v_discount_money = COALESCE(p_shipping_fee, 0);
    END IF;

    SET v_discount_money = LEAST(v_discount_money, COALESCE(p_subtotal_money, 0) + COALESCE(p_shipping_fee, 0));
    SET v_discount_money = GREATEST(v_discount_money, 0);

    UPDATE vouchers
    SET used_count = used_count + 1,
        updated_at = NOW()
    WHERE id = v_voucher_id;

    INSERT INTO voucher_usage(voucher_id, order_id, voucher_code, discount_money, usage_status)
    VALUES (v_voucher_id, p_order_id, UPPER(TRIM(p_voucher_code)), v_discount_money, 'USED');

    UPDATE customer_orders
    SET voucher_id = v_voucher_id,
        voucher_code = UPPER(TRIM(p_voucher_code)),
        voucher_name = v_name,
        voucher_type = v_type,
        voucher_value = v_value,
        voucher_min_order_value = COALESCE(v_min_order_value, 0),
        discount_money = v_discount_money,
        total_money = GREATEST(subtotal_money + shipping_fee - v_discount_money, 0),
        updated_at = NOW()
    WHERE id = p_order_id;

    SELECT v_discount_money AS discount_money;
END $$

DROP PROCEDURE IF EXISTS sp_voucher_consume_for_invoice $$
CREATE PROCEDURE sp_voucher_consume_for_invoice(
    IN p_invoice_id BIGINT,
    IN p_voucher_code VARCHAR(50),
    IN p_subtotal_money INT,
    IN p_shipping_fee INT
)
BEGIN
    DECLARE v_voucher_id BIGINT;
    DECLARE v_name VARCHAR(150);
    DECLARE v_type VARCHAR(30);
    DECLARE v_value INT;
    DECLARE v_is_active BOOLEAN;
    DECLARE v_starts_at DATETIME;
    DECLARE v_ends_at DATETIME;
    DECLARE v_max_usage INT;
    DECLARE v_used_count INT;
    DECLARE v_min_order_value INT;
    DECLARE v_discount_money INT DEFAULT 0;

    SELECT id, voucher_name, voucher_type, discount_value, is_active,
           starts_at, ends_at, max_usage, used_count, min_order_value
    INTO v_voucher_id, v_name, v_type, v_value, v_is_active,
         v_starts_at, v_ends_at, v_max_usage, v_used_count, v_min_order_value
    FROM vouchers
    WHERE voucher_code = UPPER(TRIM(p_voucher_code))
    FOR UPDATE;

    IF v_voucher_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher code does not exist.';
    END IF;
    IF v_is_active = FALSE THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher is disabled.';
    END IF;
    IF v_starts_at IS NOT NULL AND v_starts_at > NOW() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher is not active yet.';
    END IF;
    IF v_ends_at IS NOT NULL AND v_ends_at < NOW() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher has expired.';
    END IF;
    IF v_max_usage > 0 AND v_used_count >= v_max_usage THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher has reached its usage limit.';
    END IF;
    IF COALESCE(p_subtotal_money, 0) < COALESCE(v_min_order_value, 0) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Order does not meet voucher minimum value.';
    END IF;

    IF v_type = 'PERCENT' THEN
        SET v_discount_money = FLOOR(COALESCE(p_subtotal_money, 0) * v_value / 100);
    ELSEIF v_type = 'FIXED_AMOUNT' THEN
        SET v_discount_money = v_value;
    ELSEIF v_type = 'FREE_SHIPPING' THEN
        SET v_discount_money = COALESCE(p_shipping_fee, 0);
    END IF;

    SET v_discount_money = LEAST(v_discount_money, COALESCE(p_subtotal_money, 0) + COALESCE(p_shipping_fee, 0));
    SET v_discount_money = GREATEST(v_discount_money, 0);

    UPDATE vouchers
    SET used_count = used_count + 1,
        updated_at = NOW()
    WHERE id = v_voucher_id;

    INSERT INTO voucher_usage(voucher_id, order_id, invoice_id, voucher_code, discount_money, usage_status)
    VALUES (v_voucher_id, NULL, p_invoice_id, UPPER(TRIM(p_voucher_code)), v_discount_money, 'USED');

    UPDATE invoices
    SET voucher_id = v_voucher_id,
        voucher_code = UPPER(TRIM(p_voucher_code)),
        voucher_name = v_name,
        voucher_type = v_type,
        voucher_value = v_value,
        voucher_min_order_value = COALESCE(v_min_order_value, 0),
        discount_money = v_discount_money,
        total_money = GREATEST(subtotal_money - v_discount_money, 0),
        updated_date = NOW()
    WHERE id = p_invoice_id;

    SELECT v_discount_money AS discount_money;
END $$

DROP PROCEDURE IF EXISTS sp_voucher_release_for_order $$
CREATE PROCEDURE sp_voucher_release_for_order(IN p_order_id BIGINT)
BEGIN
    DECLARE v_voucher_id BIGINT;
    DECLARE v_usage_id BIGINT;
    DECLARE v_current_status VARCHAR(20);
    DECLARE v_released BOOLEAN;

    SELECT voucher_id, voucher_discount_released
    INTO v_voucher_id, v_released
    FROM customer_orders
    WHERE id = p_order_id
    FOR UPDATE;

    IF v_voucher_id IS NULL OR COALESCE(v_released, FALSE) = TRUE THEN
        SELECT 0 AS affected_rows;
    ELSE
        SELECT id, usage_status
        INTO v_usage_id, v_current_status
        FROM voucher_usage
        WHERE order_id = p_order_id
        FOR UPDATE;

        IF v_usage_id IS NOT NULL AND COALESCE(v_current_status, '') <> 'RELEASED' THEN
            UPDATE vouchers
            SET used_count = CASE WHEN used_count > 0 THEN used_count - 1 ELSE 0 END,
                updated_at = NOW()
            WHERE id = v_voucher_id;

            UPDATE voucher_usage
            SET usage_status = 'RELEASED',
                updated_at = NOW()
            WHERE id = v_usage_id;

            UPDATE customer_orders
            SET voucher_discount_released = TRUE,
                updated_at = NOW()
            WHERE id = p_order_id;
        END IF;

        SELECT 1 AS affected_rows;
    END IF;
END $$

DROP PROCEDURE IF EXISTS sp_chat_session_create $$
CREATE PROCEDURE sp_chat_session_create(
    IN p_session_code VARCHAR(50),
    IN p_customer_id BIGINT,
    IN p_guest_name VARCHAR(150),
    IN p_channel_name VARCHAR(30)
)
BEGIN
    INSERT INTO chat_sessions(session_code, customer_id, guest_name, channel_name)
    VALUES (p_session_code, p_customer_id, p_guest_name, COALESCE(p_channel_name, 'AGENT_WEB'));

    SELECT LAST_INSERT_ID() AS session_id;
END $$

DROP PROCEDURE IF EXISTS sp_chat_session_resolve $$
CREATE PROCEDURE sp_chat_session_resolve(IN p_session_code VARCHAR(50))
BEGIN
    SELECT id
    FROM chat_sessions
    WHERE session_code = p_session_code
    LIMIT 1;
END $$

DROP PROCEDURE IF EXISTS sp_chat_message_add $$
CREATE PROCEDURE sp_chat_message_add(
    IN p_session_id BIGINT,
    IN p_sender_type VARCHAR(20),
    IN p_message_text TEXT,
    IN p_prompt_tokens INT,
    IN p_completion_tokens INT
)
BEGIN
    INSERT INTO chat_messages(session_id, sender_type, message_text, prompt_tokens, completion_tokens)
    VALUES (
        p_session_id,
        p_sender_type,
        p_message_text,
        COALESCE(p_prompt_tokens, 0),
        COALESCE(p_completion_tokens, 0)
    );

    UPDATE chat_sessions
    SET updated_at = NOW()
    WHERE id = p_session_id;

    SELECT LAST_INSERT_ID() AS message_id;
END $$

DROP PROCEDURE IF EXISTS sp_chat_session_get_messages $$
CREATE PROCEDURE sp_chat_session_get_messages(IN p_session_code VARCHAR(50))
BEGIN
    SELECT cs.session_code,
           cm.sender_type,
           cm.message_text,
           cm.prompt_tokens,
           cm.completion_tokens,
           cm.created_at
    FROM chat_sessions cs
    JOIN chat_messages cm ON cm.session_id = cs.id
    WHERE cs.session_code = p_session_code
    ORDER BY cm.id ASC;
END $$

DROP PROCEDURE IF EXISTS sp_chat_session_close $$
CREATE PROCEDURE sp_chat_session_close(IN p_session_code VARCHAR(50))
BEGIN
    UPDATE chat_sessions
    SET session_status = 'CLOSED',
        updated_at = NOW()
    WHERE session_code = p_session_code;

    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_statistical_summary $$
CREATE PROCEDURE sp_statistical_summary()
BEGIN
    SELECT COALESCE(SUM(total_money), 0) AS total_money,
           COALESCE(SUM(quantity), 0) AS total_quantity,
           COUNT(*) AS invoice_number
    FROM invoices
    WHERE invoice_status <> 'CANCELLED';
END $$

DROP PROCEDURE IF EXISTS sp_statistical_summary_with_users $$
CREATE PROCEDURE sp_statistical_summary_with_users()
BEGIN
    SELECT
        (
            (SELECT COALESCE(SUM(total_money), 0) FROM invoices WHERE invoice_status <> 'CANCELLED')
            +
            (SELECT COALESCE(SUM(total_money), 0)
             FROM customer_orders
             WHERE payment_status = 'PAID'
               AND order_status <> 'CANCELLED')
        ) AS money,
        (
            (SELECT COALESCE(SUM(quantity), 0) FROM invoices WHERE invoice_status <> 'CANCELLED')
            +
            (SELECT COALESCE(SUM(oi.quantity), 0)
             FROM customer_orders o
             JOIN customer_order_items oi ON oi.order_id = o.id
             WHERE o.payment_status = 'PAID'
               AND o.order_status <> 'CANCELLED')
        ) AS quantity,
        (
            (SELECT COUNT(*) FROM invoices WHERE invoice_status <> 'CANCELLED')
            +
            (SELECT COUNT(*)
             FROM customer_orders
             WHERE payment_status = 'PAID'
               AND order_status <> 'CANCELLED')
        ) AS invoice_number,
        (
            SELECT COUNT(*)
            FROM users u
            JOIN user_role ur ON ur.user_id = u.id
            JOIN roles r ON r.id = ur.role_id
            WHERE r.role_code = 'STAFF'
              AND u.is_deleted = FALSE
        ) AS user_number;
END $$

DROP PROCEDURE IF EXISTS sp_statistical_by_date $$
CREATE PROCEDURE sp_statistical_by_date(
    IN p_start_date DATETIME,
    IN p_end_date DATETIME
)
BEGIN
    SELECT i.invoice_code,
           u.full_name AS sale_people,
           c.full_name AS customer_name,
           i.receive_money,
           i.excess_money,
           i.total_money,
           i.quantity,
           i.created_date
    FROM invoices i
    JOIN customers c ON c.id = i.customer_id
    JOIN users u ON u.id = i.user_id
    WHERE i.created_date BETWEEN p_start_date AND p_end_date
      AND i.invoice_status <> 'CANCELLED'
    ORDER BY i.created_date DESC;
END $$

DROP PROCEDURE IF EXISTS sp_statistical_staff_revenue $$
CREATE PROCEDURE sp_statistical_staff_revenue()
BEGIN
    SELECT u.id AS user_id,
           u.full_name,
           COUNT(i.id) AS invoice_count,
           COALESCE(SUM(i.quantity), 0) AS total_quantity,
           COALESCE(SUM(i.total_money), 0) AS total_revenue
    FROM users u
    JOIN user_role ur ON ur.user_id = u.id
    JOIN roles r ON r.id = ur.role_id
    LEFT JOIN invoices i
        ON i.user_id = u.id
       AND i.invoice_status <> 'CANCELLED'
    WHERE r.role_code = 'STAFF'
      AND u.is_deleted = FALSE
    GROUP BY u.id, u.full_name
    ORDER BY total_revenue DESC, invoice_count DESC, u.full_name ASC;
END $$

DROP PROCEDURE IF EXISTS sp_audit_log_add $$
CREATE PROCEDURE sp_audit_log_add(
    IN p_actor_user_id BIGINT,
    IN p_module_name VARCHAR(50),
    IN p_action_name VARCHAR(50),
    IN p_entity_name VARCHAR(50),
    IN p_entity_id BIGINT,
    IN p_payload_json JSON
)
BEGIN
    INSERT INTO audit_logs(actor_user_id, module_name, action_name, entity_name, entity_id, payload_json)
    VALUES (p_actor_user_id, p_module_name, p_action_name, p_entity_name, p_entity_id, p_payload_json);

    SELECT LAST_INSERT_ID() AS audit_log_id;
END $$

DELIMITER ;

-- Suggested smoke tests
-- CALL sp_auth_login('admin');
-- CALL sp_user_get_all_staff();
-- CALL sp_product_get_all();
-- CALL sp_cart_add_item(2, 1, 1);
-- CALL sp_cart_get_by_staff(2);
-- CALL sp_customer_get_by_phone('0901111111');
-- CALL sp_statistical_summary_with_users();
