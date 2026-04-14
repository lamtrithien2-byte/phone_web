# Procedure Mapping

Tai lieu nay liet ke theo dang:

- `Chuc nang`
- `Stored procedure`
- `Bang tac dong`

Nguon doi chieu:

- [phone_store_full_setup.sql](/C:/Users/thien%20lam/Desktop/Phone_Store%20_Application/Phone_Store%20_Application/scripts/phone_store_full_setup.sql)

## 1. Auth va tai khoan

### Dang nhap
- Procedure: `sp_auth_login`
- Bang doc:
  - `users`
  - `user_role`
  - `roles`

### Tao staff moi
- Procedure: `sp_auth_create_staff`
- Bang tac dong:
  - `users`
  - `user_role`
  - `roles`
  - `audit_logs`

### Kich hoat tai khoan
- Procedure: `sp_auth_activate_account`
- Bang tac dong:
  - `users`

### Doi mat khau lan dau
- Procedure: `sp_auth_change_first_password`
- Bang tac dong:
  - `users`

### Tao token quen mat khau
- Procedure: `sp_auth_create_reset_token`
- Bang tac dong:
  - `users`

### Dat lai mat khau
- Procedure: `sp_auth_reset_password`
- Bang tac dong:
  - `users`

## 2. Nguoi dung va profile

### Lay danh sach staff
- Procedure: `sp_user_get_all_staff`
- Bang doc:
  - `users`
  - `user_role`
  - `roles`

### Lay profile
- Procedure: `sp_user_get_profile`
- Bang doc:
  - `users`
  - `user_role`
  - `roles`

### Cap nhat profile
- Procedure: `sp_user_update_profile`
- Bang tac dong:
  - `users`
  - `audit_logs`

### Cap nhat avatar
- Procedure: `sp_user_update_avatar`
- Bang tac dong:
  - `users`
  - `audit_logs`

### Khoa / mo tai khoan
- Procedure: `sp_user_change_status`
- Bang tac dong:
  - `users`
  - `audit_logs`

## 3. Danh muc va san pham

### Lay toan bo san pham
- Procedure: `sp_product_get_all`
- Bang doc:
  - `products`
  - `categories`
  - `brands`

### Lay chi tiet san pham theo id
- Procedure: `sp_product_get_by_id`
- Bang doc:
  - `products`
  - `categories`
  - `brands`

### Tao san pham
- Procedure: `sp_product_create`
- Bang tac dong:
  - `products`
  - `audit_logs`

### Cap nhat san pham
- Procedure: `sp_product_update`
- Bang tac dong:
  - `products`
  - `audit_logs`

### Xoa mem san pham
- Procedure: `sp_product_delete_soft`
- Bang tac dong:
  - `products`
  - `audit_logs`

### Dieu chinh ton kho
- Procedure: `sp_product_adjust_stock`
- Bang tac dong:
  - `products`
  - `inventory_transactions`
  - `audit_logs`

### Anh san pham
- Bang lien quan:
  - `product_images`
- Ghi chu:
  - hien tai script giu bang anh san pham, nhung luong source dang uu tien `image_link` tren `products`

## 4. Cart POS cho staff

### Tao cart dang mo neu chua co
- Procedure: `sp_cart_ensure_open`
- Bang tac dong:
  - `staff_carts`

### Lay cart theo staff
- Procedure: `sp_cart_get_by_staff`
- Bang doc:
  - `staff_carts`
  - `staff_cart_items`

### Them san pham vao cart
- Procedure: `sp_cart_add_item`
- Bang tac dong:
  - `staff_carts`
  - `staff_cart_items`
  - `products`

### Cap nhat so luong item trong cart
- Procedure: `sp_cart_update_quantity`
- Bang tac dong:
  - `staff_cart_items`

### Xoa item khoi cart
- Procedure: `sp_cart_delete_item`
- Bang tac dong:
  - `staff_cart_items`

### Xoa toan bo cart theo staff
- Procedure: `sp_cart_clear_by_staff`
- Bang tac dong:
  - `staff_cart_items`
  - `staff_carts`

## 5. Khach hang

### Tim khach theo so dien thoai
- Procedure: `sp_customer_get_by_phone`
- Bang doc:
  - `customers`

### Tim khach theo tu khoa
- Procedure: `sp_customer_search`
- Bang doc:
  - `customers`

### Tao khach neu chua ton tai
- Procedure: `sp_customer_create_if_not_exists`
- Bang tac dong:
  - `customers`

### Lay lich su mua hang cua khach
- Procedure: `sp_customer_purchase_history`
- Bang doc:
  - `customers`
  - `invoices`
  - `users`

## 6. Hoa don tai quay

### Tao hoa don
- Procedure: `sp_invoice_create`
- Bang tac dong:
  - `invoices`
  - `staff_carts`
  - `customers`
  - `users`

### Tao dong san pham trong hoa don
- Procedure: `sp_invoice_item_create`
- Bang tac dong:
  - `invoice_items`
  - `products`
  - `inventory_transactions`

### Gan link PDF cho hoa don
- Procedure: `sp_invoice_attach_pdf`
- Bang tac dong:
  - `invoices`

### Lay chi tiet hoa don
- Procedure: `sp_invoice_get_detail`
- Bang doc:
  - `invoices`
  - `invoice_items`

### Lay tat ca hoa don
- Procedure: `sp_invoice_get_all`
- Bang doc:
  - `invoices`
  - `customers`
  - `users`

### Dong cart sau checkout
- Procedure: `sp_invoice_complete_checkout`
- Bang tac dong:
  - `staff_carts`

## 7. Don hang online

### Tao don hang online
- Procedure: `sp_order_create`
- Bang tac dong:
  - `customer_orders`
  - `customers`

### Tao item cua don online
- Procedure: `sp_order_add_item`
- Bang tac dong:
  - `customer_order_items`
  - `products`
  - `inventory_transactions`

### Cap nhat trang thai don / thanh toan
- Procedure: `sp_order_update_status`
- Bang tac dong:
  - `customer_orders`

### Lay chi tiet don theo ma don
- Procedure: `sp_order_get_detail`
- Bang doc:
  - `customer_orders`
  - `customer_order_items`

### Lay don theo so dien thoai khach
- Procedure: `sp_order_get_by_customer_phone`
- Bang doc:
  - `customer_orders`
  - `customers`

### Lay toan bo don online
- Procedure: `sp_order_get_all`
- Bang doc:
  - `customer_orders`
  - `customers`

## 8. Chat agent

### Tao session chat
- Procedure: `sp_chat_session_create`
- Bang tac dong:
  - `chat_sessions`
  - `customers`

### Tim session chat theo code
- Procedure: `sp_chat_session_resolve`
- Bang doc:
  - `chat_sessions`

### Them tin nhan vao session
- Procedure: `sp_chat_message_add`
- Bang tac dong:
  - `chat_messages`
  - `chat_sessions`

### Lay lich su tin nhan
- Procedure: `sp_chat_session_get_messages`
- Bang doc:
  - `chat_sessions`
  - `chat_messages`

### Dong session chat
- Procedure: `sp_chat_session_close`
- Bang tac dong:
  - `chat_sessions`

## 9. Dashboard va thong ke

### Tong quan doanh thu
- Procedure: `sp_statistical_summary`
- Bang doc:
  - `invoices`

### Tong quan doanh thu + so nhan vien
- Procedure: `sp_statistical_summary_with_users`
- Bang doc:
  - `invoices`
  - `users`
  - `user_role`
  - `roles`

### Thong ke theo khoang ngay
- Procedure: `sp_statistical_by_date`
- Bang doc:
  - `invoices`
  - `customers`
  - `users`

### Doanh thu theo staff
- Procedure: `sp_statistical_staff_revenue`
- Bang doc:
  - `users`
  - `user_role`
  - `roles`
  - `invoices`

## 10. Audit log

### Ghi log thao tac
- Procedure: `sp_audit_log_add`
- Bang tac dong:
  - `audit_logs`
  - `users`

## 11. Tong ket theo module

### Module `core-api`
- Dung toan bo nhom procedure:
  - `auth`
  - `user`
  - `product`
  - `cart`
  - `customer`
  - `invoice`
  - `order`
  - `chat`
  - `statistical`
  - `audit`

### Module `cms-web`
- Goi qua `core-api`, nghiep vu chinh lien quan:
  - `sp_auth_login`
  - `sp_user_get_all_staff`
  - `sp_user_get_profile`
  - `sp_cart_*`
  - `sp_customer_*`
  - `sp_invoice_*`
  - `sp_order_get_all`
  - `sp_order_get_detail`
  - `sp_order_update_status`
  - `sp_statistical_*`

### Module `ecommerce-web`
- Goi qua `core-api`, nghiep vu chinh lien quan:
  - `sp_product_get_all`
  - `sp_product_get_by_id`
  - `sp_order_create`
  - `sp_order_add_item`
  - `sp_order_get_by_customer_phone`
  - `sp_order_get_detail`
  - `sp_chat_session_*`
  - `sp_chat_message_add`

## 12. Procedure can cho tat ca chuc nang hien co

Neu anh/chị can tra loi nhanh trong bao cao, co the dung cau sau:

`Tat ca chuc nang hien co trong source da duoc phu boi stored procedure, tap trung trong file phone_store_full_setup.sql, va duoc nhom theo 10 nhom nghiep vu: auth, user, product, cart, customer, invoice, order, chat, dashboard, audit.`
