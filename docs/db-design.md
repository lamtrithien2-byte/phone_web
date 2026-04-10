# Phone Store DB Design

Tai lieu nay mo ta thiet ke DB duoc rut ra truc tiep tu source hien tai cua `core-api`, `cms-web`, `ecommerce-web` va script [phone_store_full_setup.sql](/C:/Users/thien%20lam/Desktop/Phone_Store%20_Application/Phone_Store%20_Application/scripts/phone_store_full_setup.sql).

## 1. Muc tieu he thong

He thong gom 3 phan:

- `core-api`: trung tam xu ly nghiep vu
- `cms-web`: staff/admin thao tac noi bo
- `ecommerce-web`: web user dat hang online

Tat ca du lieu di qua `core-api`, va `core-api` goi stored procedure de lam viec voi DB.

## 2. Nhom nghiep vu chinh

DB can phuc vu 6 nhom chinh:

1. `Auth + User + Role`
2. `Product Catalog`
3. `Customer`
4. `POS / Cart / Invoice`
5. `Ecommerce Order`
6. `Chat / Audit / Dashboard`

## 3. So do bang tong the

### 3.1 Auth va nguoi dung

#### `roles`
- `id`
- `role_code`
- `role_name`
- `description`
- `is_system`
- `created_at`

#### `users`
- `id`
- `user_name`
- `password_hash`
- `full_name`
- `email`
- `address`
- `phone_number`
- `avatar`
- `gender`
- `date_of_birth`
- `activation_token`
- `activation_expires`
- `is_activated`
- `first_login`
- `is_deleted`
- `last_login_at`
- `created_at`
- `updated_at`

#### `user_role`
- `user_id`
- `role_id`

Quan he:
- `users` N-N `roles` qua `user_role`

Ly do:
- source hien tai co `admin`, `staff`, va mo rong duoc cho `customer`
- role tach rieng de phan quyen admin/staff ro rang

### 3.2 Danh muc va san pham

#### `brands`
- `id`
- `brand_name`
- `brand_slug`
- `logo_url`
- `description`
- `is_deleted`
- `created_at`
- `updated_at`

#### `categories`
- `id`
- `category_name`
- `category_slug`
- `description`
- `is_deleted`
- `created_at`
- `updated_at`

#### `products`
- `id`
- `bar_code`
- `sku`
- `name`
- `product_slug`
- `brand_id`
- `category_id`
- `screen_size`
- `ram`
- `rom`
- `chipset`
- `battery_capacity`
- `operating_system`
- `color`
- `warranty_months`
- `import_price`
- `price_sale`
- `stock_quantity`
- `sale_number`
- `description`
- `image_link`
- `is_featured`
- `is_deleted`
- `created_by`
- `updated_by`
- `created_date`
- `updated_date`

#### `product_images`
- `id`
- `product_id`
- `image_link`
- `sort_order`
- `is_primary`
- `created_at`

Quan he:
- `brands` 1-N `products`
- `categories` 1-N `products`
- `products` 1-N `product_images`
- `users` 1-N `products` qua `created_by`, `updated_by`

Ly do:
- `ecommerce-web` dung `products` de hien thi danh sach, chi tiet, sort theo gia, ban chay, moi nhat
- `cms-web` dung `products` cho luong staff ban hang

### 3.3 Khach hang

#### `customers`
- `id`
- `customer_code`
- `phone_number`
- `full_name`
- `email`
- `address`
- `ward`
- `district`
- `city`
- `note`
- `loyalty_points`
- `is_deleted`
- `created_at`
- `updated_at`

Ly do:
- staff va web user deu quy ve cung mot thuc the customer
- hien tai he thong xac dinh customer chu yeu bang `phone_number`

### 3.4 POS tai quay

#### `staff_carts`
- `id`
- `sale_people_id`
- `cart_status`
- `created_at`
- `updated_at`

#### `staff_cart_items`
- `id`
- `cart_id`
- `product_id`
- `name`
- `bar_code`
- `image_link`
- `sale_price`
- `quantity`
- `total_money`
- `created_at`
- `updated_at`

#### `invoices`
- `id`
- `invoice_code`
- `customer_id`
- `user_id`
- `cart_id`
- `sales_channel`
- `invoice_status`
- `payment_method`
- `receive_money`
- `excess_money`
- `subtotal_money`
- `discount_money`
- `total_money`
- `quantity`
- `note`
- `pdf_link`
- `created_date`
- `updated_date`

#### `invoice_items`
- `id`
- `invoice_id`
- `product_id`
- `product_name`
- `bar_code`
- `quantity`
- `unit_price`
- `line_total`
- `created_date`

Quan he:
- `users` 1-N `staff_carts`
- `staff_carts` 1-N `staff_cart_items`
- `customers` 1-N `invoices`
- `users` 1-N `invoices`
- `invoices` 1-N `invoice_items`

Ly do:
- `cms-web/sales` dang co cart tam cho moi staff
- checkout POS tao invoice tu cart dang mo
- invoice co the dinh kem file PDF

### 3.5 Don hang online

#### `customer_orders`
- `id`
- `order_code`
- `customer_id`
- `order_status`
- `payment_status`
- `subtotal_money`
- `shipping_fee`
- `discount_money`
- `total_money`
- `recipient_name`
- `recipient_phone`
- `shipping_address`
- `note`
- `created_at`
- `updated_at`

#### `customer_order_items`
- `id`
- `order_id`
- `product_id`
- `product_name`
- `bar_code`
- `quantity`
- `unit_price`
- `line_total`
- `created_at`

Quan he:
- `customers` 1-N `customer_orders`
- `customer_orders` 1-N `customer_order_items`
- `products` 1-N `customer_order_items`

Ly do:
- `ecommerce-web` dat hang tren checkout
- `cms-web` xem don online, mo chi tiet, thu tien va doi `UNPAID -> PAID`
- `tracking` ben web user doc lai tu bang nay

### 3.6 Ton kho va log

#### `inventory_transactions`
- `id`
- `product_id`
- `transaction_type`
- `quantity_change`
- `quantity_after`
- `reference_type`
- `reference_id`
- `note`
- `created_by`
- `created_at`

#### `audit_logs`
- `id`
- `actor_user_id`
- `module_name`
- `action_name`
- `entity_name`
- `entity_id`
- `payload_json`
- `created_at`

Ly do:
- theo doi bien dong ton kho do ban tai quay hoac dat online
- audit thao tac nghiep vu quan trong

### 3.7 Chat agent

#### `chat_sessions`
- `id`
- `session_code`
- `customer_id`
- `guest_name`
- `channel_name`
- `session_status`
- `created_at`
- `updated_at`

#### `chat_messages`
- `id`
- `session_id`
- `sender_type`
- `message_text`
- `prompt_tokens`
- `completion_tokens`
- `created_at`

Ly do:
- bubble chat trong `ecommerce-web`
- luu lich su hoi dap de tiep tuc session

## 4. Quan he nghiep vu quan trong

### Luong online

1. user chon `products`
2. tao `customer` neu chua ton tai
3. tao `customer_orders`
4. tao `customer_order_items`
5. tru ton kho va ghi `inventory_transactions`
6. staff ben CMS xem lai don
7. CMS doi `payment_status` thanh `PAID`
8. `tracking` hien `COMPLETED / PAID`

### Luong POS

1. staff tao `staff_carts`
2. them `staff_cart_items`
3. tim/tao `customers`
4. checkout tao `invoices`
5. tao `invoice_items`
6. tru ton kho va ghi `inventory_transactions`
7. gan `pdf_link` neu xuat hoa don PDF

## 5. Trang thai khuyen nghi

### `customer_orders.order_status`
- `PENDING`
- `CONFIRMED`
- `SHIPPING`
- `DELIVERED`
- `COMPLETED`
- `CANCELLED`

### `customer_orders.payment_status`
- `UNPAID`
- `PAID`
- `REFUNDED`

### `staff_carts.cart_status`
- `OPEN`
- `CHECKED_OUT`
- `CANCELLED`

### `invoices.invoice_status`
- `PAID`
- `CANCELLED`

## 6. Index quan trong

Nen giu cac index sau:

- `products(category_id)`
- `products(brand_id)`
- `products(name)`
- `products(sale_number)`
- `customers(phone_number)`
- `invoices(created_date)`
- `customer_orders(created_at)`
- `chat_messages(session_id, created_at)`

Bo sung neu du lieu lon hon:

- `customer_orders(customer_id, created_at)`
- `customer_orders(payment_status, order_status, created_at)`
- `products(price_sale, is_deleted)`

## 7. Stored procedure mapping

### Auth
- `sp_auth_login`
- `sp_auth_create_staff`
- `sp_auth_activate_account`
- `sp_auth_change_first_password`
- `sp_auth_create_reset_token`
- `sp_auth_reset_password`

### User / profile
- `sp_user_get_all_staff`
- `sp_user_get_profile`
- `sp_user_update_profile`
- `sp_user_update_avatar`
- `sp_user_change_status`

### Product
- `sp_product_get_all`
- `sp_product_get_by_id`
- `sp_product_create`
- `sp_product_update`
- `sp_product_delete_soft`
- `sp_product_adjust_stock`

### Cart / POS
- `sp_cart_ensure_open`
- `sp_cart_get_by_staff`
- `sp_cart_add_item`
- `sp_cart_update_quantity`
- `sp_cart_delete_item`
- `sp_cart_clear_by_staff`

### Customer
- `sp_customer_get_by_phone`
- `sp_customer_search`
- `sp_customer_create_if_not_exists`
- `sp_customer_purchase_history`

### Invoice
- `sp_invoice_create`
- `sp_invoice_item_create`
- `sp_invoice_attach_pdf`
- `sp_invoice_get_detail`
- `sp_invoice_get_all`
- `sp_invoice_complete_checkout`

### Online order
- `sp_order_create`
- `sp_order_add_item`
- `sp_order_update_status`
- `sp_order_get_detail`
- `sp_order_get_by_customer_phone`
- `sp_order_get_all`

### Chat
- `sp_chat_session_create`
- `sp_chat_session_resolve`
- `sp_chat_message_add`
- `sp_chat_session_get_messages`
- `sp_chat_session_close`

### Dashboard / audit
- `sp_statistical_summary`
- `sp_statistical_summary_with_users`
- `sp_statistical_by_date`
- `sp_statistical_staff_revenue`
- `sp_audit_log_add`

## 8. Khoa ngoai toi thieu can giu

- `user_role.user_id -> users.id`
- `user_role.role_id -> roles.id`
- `products.brand_id -> brands.id`
- `products.category_id -> categories.id`
- `staff_carts.sale_people_id -> users.id`
- `staff_cart_items.cart_id -> staff_carts.id`
- `staff_cart_items.product_id -> products.id`
- `invoices.customer_id -> customers.id`
- `invoices.user_id -> users.id`
- `invoice_items.invoice_id -> invoices.id`
- `invoice_items.product_id -> products.id`
- `customer_orders.customer_id -> customers.id`
- `customer_order_items.order_id -> customer_orders.id`
- `customer_order_items.product_id -> products.id`
- `inventory_transactions.product_id -> products.id`
- `chat_sessions.customer_id -> customers.id`
- `chat_messages.session_id -> chat_sessions.id`
- `audit_logs.actor_user_id -> users.id`

## 9. De xuat chuan hoa them

Neu muon nang cap sau nay, co the tach them:

- `payment_transactions`: luu tung lan thanh toan online/POS
- `shipping_addresses`: 1 customer co nhieu dia chi
- `product_variants`: mau sac/dung luong theo variant thay vi nhieu dong `products`
- `promotions`: giam gia, ma coupon
- `order_status_history`: lich su chuyen trang thai don

## 10. Ket luan

DB hien tai da hop ly cho source dang co, va xoay quanh 4 truc chinh:

- `User/Role`
- `Product/Inventory`
- `Customer + POS Invoice`
- `Online Order + Tracking`

Neu anh/chị muon, buoc tiep theo minh co the lam tiep 1 trong 2 viec:

1. ve ERD bang text/mermaid tu schema nay
2. toi uu lai script SQL de nhom bang/procedure ro hon cho nop do an
