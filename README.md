# Bazaario – Full-Stack E-Commerce Web App

A full-stack online store: an **Angular** single-page frontend talking to a **Spring Boot** REST API secured with **JWT**, backed by **MariaDB / MySQL**. Customers browse, search, add to cart, check out, pay and track orders, and receive email notifications. Admins manage products, orders and users.

**Tech:** Java 21 · Spring Boot 4 · Spring Security (JWT) · Spring Data JPA / Hibernate · MariaDB / MySQL · Angular 21 · TypeScript · Maven

---

## Features

**Storefront (customers)**
- Home page with hero banner, category shortcuts, featured products, promo banners and newsletter box
- Header search and a category navigation bar, both connected to the product list
- Register and log in (JWT authentication)
- Product list with keyword search and category filter, plus a product detail page
- Cart that respects available stock, with a live item count in the header
- Checkout with a shipping address, then payment (UPI / card / net banking / cash on delivery)
- Order history with expandable item details
- Edit profile and change password
- Email notifications (see below)

**Admin panel**
- Add, edit and delete products
- View all orders and move them through `PENDING → CONFIRMED → SHIPPED → DELIVERED` (or `CANCELLED`, which returns the items to stock)
- View users and delete those who have no orders
- Category management through the REST API

**Security and engineering**
- Passwords hashed with BCrypt; stateless JWT sessions (24-hour tokens)
- Role-based access: everything under `/api/admin/**` requires the `ADMIN` role
- Ownership checks, so a user can only read their own cart, orders and payments
- Checkout runs in one `@Transactional` method: if any item is out of stock, the whole order rolls back
- Order items store the price at purchase time, so later price changes don't rewrite history
- Money handled with `BigDecimal`, never `double`
- Java `record` DTOs, Bean Validation, and one central `@RestControllerAdvice` for errors
- Angular route guards (login / admin) and an HTTP interceptor that attaches the token

---

## Email notifications

| When | Email sent |
|---|---|
| A user registers | Welcome email |
| A user logs in | Sign-in alert |
| An order is placed | Order confirmation |
| A payment is made | Payment receipt |

- Emails are sent on a background thread pool, so API responses stay fast.
- Order and payment emails go out only after the database transaction commits, so no email is sent for a rolled-back order.
- If email is not configured or sending fails, the failure is only logged and the app keeps working.

---

## Architecture

```
Browser ──► Angular SPA (:4200) ──HTTP + JSON + JWT──► Spring Boot API (:8080) ──JPA/Hibernate──► MariaDB / MySQL
```

Backend layers: **Controller → Service (interface + implementation) → Repository → Entity**

### Project structure

```
bazaario/
├── bazaario-backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/bazaario/
│       │   ├── config/        SecurityConfig (JWT, CORS), DataSeeder
│       │   ├── controller/    REST endpoints
│       │   ├── dto/           record-based request/response objects
│       │   ├── entity/        JPA entities and enums
│       │   ├── exception/     custom exceptions + global handler
│       │   ├── repository/    Spring Data JPA repositories
│       │   ├── security/      JWT utility, filter, UserDetails
│       │   └── service/       business logic and email sending
│       └── resources/
│           ├── application.properties
│           └── static/images/products/   product images
└── bazaario-frontend/
    └── src/app/
        ├── core/          services, interceptor, guards, API config
        ├── models/        TypeScript interfaces
        └── pages/         home, login, register, products, cart, checkout, orders, profile, admin pages
```

### Database tables

`users`, `categories`, `products`, `cart`, `cart_items`, `orders`, `order_items`, `payments` — related through foreign keys and created automatically by Hibernate from the entity classes.

---

## Getting started

### Prerequisites
- JDK 21
- Maven (or an IDE such as Eclipse / IntelliJ that can run the project)
- Node.js (current LTS) and npm
- MariaDB or MySQL

### 1. Database
```sql
CREATE DATABASE ecommerce;
```
Tables are created automatically on first run (`spring.jpa.hibernate.ddl-auto=update`).

### 2. Backend configuration
Open `bazaario-backend/src/main/resources/application.properties` and set:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3307/ecommerce
spring.datasource.username=root
spring.datasource.password=your_db_password
app.jwt.secret=put-a-long-random-string-of-at-least-32-characters-here
```

- The project was developed on **MariaDB 10.4** with the MariaDB JDBC driver. For MySQL, switch to MySQL Connector/J and a `jdbc:mysql://` URL.
- The default port in the file is `3307`. Standard installs usually listen on `3306`, so use whichever port your server uses.
- Keep real secrets out of Git. Prefer environment variables, for example `app.jwt.secret=${JWT_SECRET}`.

### 3. Email (optional)
Email is configured through these properties:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME:your-email@gmail.com}
spring.mail.password=${MAIL_PASSWORD:your-app-password}
```

Set the environment variables `MAIL_USERNAME` and `MAIL_PASSWORD`. For Gmail, `MAIL_PASSWORD` must be an **App Password** (Google Account → Security → 2-Step Verification → App passwords), not your normal password. Without them the app still runs, but no emails are delivered.

### 4. Run the backend
```bash
cd bazaario-backend
mvn spring-boot:run
```
or run `BazaarioApplication.java` from your IDE. The API starts on **http://localhost:8080**.

### 5. Run the frontend
```bash
cd bazaario-frontend
npm install        # if it reports a peer-dependency error: npm install --legacy-peer-deps
npm start          # same as: ng serve
```
Open **http://localhost:4200**. The backend URL is set in one place: `src/app/core/api.config.ts`.

### Demo account
On first start the app seeds an admin user, 4 categories and 3 sample products:

| Email | Password |
|---|---|
| `admin@bazaario.com` | `admin123` |

These are demo credentials for local use only. Change the password after logging in.

### Optional: more sample products
If you want a fuller shop, import `database/bazaario_products.sql` with your database client after the first run.

---

## API overview

| Area | Endpoints |
|---|---|
| Auth (public) | `POST /api/auth/register`, `POST /api/auth/login` |
| Catalogue (public) | `GET /api/products` (`?keyword=` / `?categoryId=`), `GET /api/products/{id}`, `GET /api/categories` |
| Images (public) | `GET /images/products/{file}` |
| Cart (login) | `GET/DELETE /api/cart`, `POST /api/cart/items`, `PUT/DELETE /api/cart/items/{id}` |
| Orders (login) | `POST /api/orders`, `GET /api/orders`, `GET /api/orders/{id}` |
| Payments (login) | `POST /api/payments`, `GET /api/payments/order/{orderId}` |
| Profile (login) | `GET/PUT /api/profile`, `PUT /api/profile/password` |
| Admin | `/api/admin/products`, `/api/admin/categories`, `GET /api/admin/orders`, `PUT /api/admin/orders/{id}/status`, `GET /api/admin/users`, `DELETE /api/admin/users/{id}` |

### Checkout flow
1. **Place order**: validates stock, deducts it, saves the order as `PENDING` and clears the cart, all in one transaction.
2. **Pay**: the payment is recorded and the order becomes `CONFIRMED`. Cash on delivery leaves the payment `PENDING` until delivery.

---

## Concepts demonstrated

| Area | Where it shows up |
|---|---|
| **Core Java** | OOP, enums (`Role`, `OrderStatus`, `PaymentStatus`), `BigDecimal`, `Optional`, exception handling, collections |
| **Advanced Java** | Records, Stream API, lambdas and method references, generics, interfaces, a thread pool (`ExecutorService`) for background email |
| **Spring / Spring Boot** | Dependency injection, REST controllers, Spring Data JPA, Spring Security + JWT, `@Transactional`, Bean Validation, centralized exception handling, mail sending |
| **Angular** | Standalone components, signals, services with `HttpClient`, functional interceptor, route guards, template-driven forms, query-parameter routing |
| **MySQL / MariaDB** | Relational schema with foreign keys, one-to-many and one-to-one mappings through JPA |

---

## Known limitations and roadmap

- [ ] Payments are **simulated**. Integrate a real gateway (e.g. Razorpay) with server-side signature verification
- [ ] Product image **upload** from the admin page (images are currently placed in the backend's static folder)
- [ ] The newsletter box on the home page is UI only and does not store emails yet
- [ ] Pagination and sorting on the product list
- [ ] Automated backend tests (service and controller layers)
- [ ] Refresh tokens and HttpOnly cookies instead of a token in `localStorage`
- [ ] Docker setup and deployment

<!--
## Screenshots
Add 3-4 screenshots to a docs/screenshots folder, then remove the comment markers around this section.

![Home page](docs/screenshots/home.png)
![Shop and cart](docs/screenshots/shop.png)
![Admin panel](docs/screenshots/admin.png)
-->

---

## Author

Built by **YOUR NAME** — [GitHub](https://github.com/YOUR-USERNAME)
