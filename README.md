<<<<<<< HEAD
# Phone Store Application

He thong gom 3 module Spring Boot:

- `core-api` tren cong `8081`
- `cms-web` tren cong `8082`
- `ecommerce-web` tren cong `8083`

Tat ca trang web giao tiep voi nhau thong qua `core-api`. `core-api` dung Hibernate/JPA de goi stored procedure, khong viet SQL nghiep vu truc tiep trong service.

## Chuc nang da hoan thien

### Core API

- dang nhap theo vai tro `admin` / `staff`
- doi mat khau, first-login, reset password
- danh muc san pham, tim kiem, them/sua/xoa mem san pham
- khach hang, lich su mua hang
- gio hang tai quay va tao hoa don
- thong ke dashboard va doanh thu nhan vien
- dat hang online va tra cuu don hang
- agent chat tu van san pham va luu lich su hoi dap

### CMS Web

- login theo vai tro
- dashboard
- quan ly nhan vien
- quan ly profile
- man hinh ban hang tai quay, gio hang, lookup khach hang, checkout hoa don

### Ecommerce Web

- xem catalog
- tim kiem san pham
- tao don hang online
- tra cuu don theo so dien thoai
- xem chi tiet don theo ma don
- chat tu van san pham ngay tren trang shop

## Database

- script setup chuan: `scripts/phone_store_full_setup.sql`
- mo ta schema: `docs/sql/phone_store_db_design.md`

## Cach chay nhanh

Tu thu muc goc project:

```powershell
.\start-phone-store.ps1 -WithCms
```

hoac:

```cmd
start-phone-store.cmd -WithCms
```

Script se:

- tu tim JDK 17+
- kiem tra MySQL `localhost:3306`
- import schema/procedure tu `scripts/phone_store_full_setup.sql`
- build 3 module
- mo `core-api`, `ecommerce-web`, va `cms-web`

## URL

- CMS: `http://127.0.0.1:8082/login`
- Shop: `http://127.0.0.1:8083/`
- API product: `http://127.0.0.1:8081/api/products`
- API order: `http://127.0.0.1:8081/api/orders/by-phone?phoneNumber=0901111111`
- API chat: `http://127.0.0.1:8081/api/chat`

## Tai khoan mau

- `admin / admin123`
- `staff1 / staff123`

## Build tay

Neu can build thu cong:

```cmd
set JAVA_HOME=C:\Users\thien lam\.jdks\openjdk-26
set PATH=%JAVA_HOME%\bin;%PATH%
mvnw.cmd clean package -DskipTests
```

## Ghi chu

- `core-api` dang tro vao database `phone_store`
- cac script DB legacy da duoc loai bo khoi luong chay chinh
- neu muon chay end-to-end, can MySQL local dang bat va cho phep user `root`
=======
# phone_web
>>>>>>> 02194c0586ac6a9d8013c656cd99aec029548817
