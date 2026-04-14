USE phone_store;

CREATE TABLE IF NOT EXISTS vouchers (
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

ALTER TABLE customer_orders ADD COLUMN IF NOT EXISTS voucher_id BIGINT NULL;
ALTER TABLE customer_orders ADD COLUMN IF NOT EXISTS voucher_code VARCHAR(50) NULL;
ALTER TABLE customer_orders ADD COLUMN IF NOT EXISTS voucher_name VARCHAR(150) NULL;
ALTER TABLE customer_orders ADD COLUMN IF NOT EXISTS voucher_type VARCHAR(30) NULL;
ALTER TABLE customer_orders ADD COLUMN IF NOT EXISTS voucher_value INT NULL;
ALTER TABLE customer_orders ADD COLUMN IF NOT EXISTS voucher_min_order_value INT NULL;
ALTER TABLE customer_orders ADD COLUMN IF NOT EXISTS voucher_discount_released BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS voucher_id BIGINT NULL;
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS voucher_code VARCHAR(50) NULL;
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS voucher_name VARCHAR(150) NULL;
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS voucher_type VARCHAR(30) NULL;
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS voucher_value INT NULL;
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS voucher_min_order_value INT NULL;

CREATE TABLE IF NOT EXISTS voucher_usage (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    voucher_id BIGINT NOT NULL,
    order_id BIGINT NULL,
    invoice_id BIGINT NULL,
    voucher_code VARCHAR(50) NOT NULL,
    discount_money INT NOT NULL DEFAULT 0,
    usage_status VARCHAR(20) NOT NULL DEFAULT 'USED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_voucher_usage_order (order_id),
    UNIQUE KEY uq_voucher_usage_invoice (invoice_id)
);

ALTER TABLE voucher_usage MODIFY order_id BIGINT NULL;
ALTER TABLE voucher_usage ADD COLUMN IF NOT EXISTS invoice_id BIGINT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_voucher_usage_invoice ON voucher_usage(invoice_id);
ALTER TABLE voucher_usage ADD CONSTRAINT fk_voucher_usage_order FOREIGN KEY (order_id) REFERENCES customer_orders(id);
ALTER TABLE voucher_usage ADD CONSTRAINT fk_voucher_usage_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id);

INSERT INTO vouchers(voucher_code, voucher_name, voucher_type, discount_value, is_active, starts_at, ends_at, max_usage, used_count, min_order_value)
SELECT 'WELCOME15', 'Voucher chao mung 15%', 'PERCENT', 15, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), 200, 0, 1000000
WHERE NOT EXISTS (SELECT 1 FROM vouchers WHERE voucher_code = 'WELCOME15');

INSERT INTO vouchers(voucher_code, voucher_name, voucher_type, discount_value, is_active, starts_at, ends_at, max_usage, used_count, min_order_value)
SELECT 'SAVE100K', 'Giam ngay 100K', 'FIXED_AMOUNT', 100000, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 45 DAY), 100, 0, 1500000
WHERE NOT EXISTS (SELECT 1 FROM vouchers WHERE voucher_code = 'SAVE100K');

INSERT INTO vouchers(voucher_code, voucher_name, voucher_type, discount_value, is_active, starts_at, ends_at, max_usage, used_count, min_order_value)
SELECT 'FREESHIP', 'Mien phi van chuyen', 'FREE_SHIPPING', 0, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 300, 0, 500000
WHERE NOT EXISTS (SELECT 1 FROM vouchers WHERE voucher_code = 'FREESHIP');

DELIMITER $$

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

DROP PROCEDURE IF EXISTS sp_invoice_get_detail $$
CREATE PROCEDURE sp_invoice_get_detail(IN p_invoice_code VARCHAR(50))
BEGIN
    SELECT i.invoice_code, i.subtotal_money, i.discount_money, i.total_money, i.voucher_code,
           ii.product_name, ii.quantity, ii.unit_price, ii.created_date
    FROM invoice_items ii
    JOIN invoices i ON i.id = ii.invoice_id
    WHERE i.invoice_code = p_invoice_code
    ORDER BY ii.id ASC;
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

DROP PROCEDURE IF EXISTS sp_voucher_get_all $$
CREATE PROCEDURE sp_voucher_get_all(
    IN p_keyword VARCHAR(100),
    IN p_status VARCHAR(20),
    IN p_voucher_type VARCHAR(30),
    IN p_expired_only BOOLEAN
)
BEGIN
    SELECT v.id, v.voucher_code, v.voucher_name, v.voucher_type, v.discount_value,
           v.is_active, v.starts_at, v.ends_at, v.max_usage, v.used_count, v.min_order_value,
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
    SELECT v.id, v.voucher_code, v.voucher_name, v.voucher_type, v.discount_value,
           v.is_active, v.starts_at, v.ends_at, v.max_usage, v.used_count, v.min_order_value
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

    INSERT INTO vouchers(voucher_code, voucher_name, voucher_type, discount_value, is_active, starts_at, ends_at, max_usage, used_count, min_order_value)
    VALUES (UPPER(TRIM(p_voucher_code)), TRIM(p_voucher_name), p_voucher_type, p_discount_value,
            COALESCE(p_is_active, TRUE), p_starts_at, p_ends_at, COALESCE(p_max_usage, 0), 0, COALESCE(p_min_order_value, 0));

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
    IF EXISTS (SELECT 1 FROM vouchers WHERE voucher_code = UPPER(TRIM(p_voucher_code)) AND id <> p_voucher_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Voucher code already exists.';
    END IF;

    UPDATE vouchers
    SET voucher_code = UPPER(TRIM(p_voucher_code)),
        voucher_name = TRIM(p_voucher_name),
        voucher_type = p_voucher_type,
        discount_value = CASE WHEN p_voucher_type = 'FREE_SHIPPING' THEN 0 ELSE p_discount_value END,
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

    DELETE FROM vouchers WHERE id = p_voucher_id;
    SELECT ROW_COUNT() AS affected_rows;
END $$

DROP PROCEDURE IF EXISTS sp_voucher_toggle $$
CREATE PROCEDURE sp_voucher_toggle(IN p_voucher_id BIGINT, IN p_is_active BOOLEAN)
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

    SELECT id, voucher_name, voucher_type, discount_value, is_active, starts_at, ends_at, max_usage, used_count, min_order_value
    INTO v_id, v_name, v_type, v_value, v_is_active, v_starts_at, v_ends_at, v_max_usage, v_used_count, v_min_order_value
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

    SELECT v_id, UPPER(TRIM(p_voucher_code)), v_name, v_type, v_value, COALESCE(v_min_order_value, 0),
           v_discount_money,
           COALESCE(p_subtotal_money, 0) + COALESCE(p_shipping_fee, 0),
           GREATEST(COALESCE(p_subtotal_money, 0) + COALESCE(p_shipping_fee, 0) - v_discount_money, 0);
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

    SELECT id, voucher_name, voucher_type, discount_value, is_active, starts_at, ends_at, max_usage, used_count, min_order_value
    INTO v_voucher_id, v_name, v_type, v_value, v_is_active, v_starts_at, v_ends_at, v_max_usage, v_used_count, v_min_order_value
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
    SET used_count = used_count + 1, updated_at = NOW()
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
            SET usage_status = 'RELEASED', updated_at = NOW()
            WHERE id = v_usage_id;

            UPDATE customer_orders
            SET voucher_discount_released = TRUE, updated_at = NOW()
            WHERE id = p_order_id;
        END IF;

        SELECT 1 AS affected_rows;
    END IF;
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

    SELECT id, voucher_name, voucher_type, discount_value, is_active, starts_at, ends_at, max_usage, used_count, min_order_value
    INTO v_voucher_id, v_name, v_type, v_value, v_is_active, v_starts_at, v_ends_at, v_max_usage, v_used_count, v_min_order_value
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
    SET used_count = used_count + 1, updated_at = NOW()
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
    SELECT o.order_code, o.order_status, o.payment_status, o.total_money, o.shipping_address,
           o.voucher_code, o.discount_money, oi.product_name, oi.quantity, oi.unit_price, oi.line_total, o.created_at
    FROM customer_orders o
    JOIN customer_order_items oi ON oi.order_id = o.id
    WHERE o.order_code = p_order_code
    ORDER BY oi.id ASC;
END $$

DROP PROCEDURE IF EXISTS sp_order_get_by_customer_phone $$
CREATE PROCEDURE sp_order_get_by_customer_phone(IN p_phone_number VARCHAR(20))
BEGIN
    SELECT o.id, o.order_code, c.full_name, o.order_status, o.payment_status, o.total_money,
           o.voucher_code, o.discount_money, o.created_at
    FROM customer_orders o
    JOIN customers c ON c.id = o.customer_id
    WHERE c.phone_number = p_phone_number
    ORDER BY o.created_at DESC;
END $$

DROP PROCEDURE IF EXISTS sp_order_get_all $$
CREATE PROCEDURE sp_order_get_all()
BEGIN
    SELECT o.id, o.order_code, c.full_name AS customer_name, o.recipient_name, o.recipient_phone,
           o.order_status, o.payment_status, o.total_money, o.voucher_code, o.discount_money, o.created_at
    FROM customer_orders o
    JOIN customers c ON c.id = o.customer_id
    ORDER BY o.created_at DESC, o.id DESC;
END $$

DELIMITER ;
