# Bazaario backend: upgraded to Spring Boot 4.1.1 + Java 21

## Versions
- Spring Boot 4.1.1 (Spring Framework 7, Spring Security 7, Hibernate 7, Jackson 3)
- Java 21 (LTS). Spring Boot 4.1 supports Java 17 through 26.
  To use a newer JDK later, change `<java.version>` in pom.xml (e.g. 25).
- jjwt 0.12.6, MySQL connector managed by Boot

## What changed
1. pom.xml: parent 4.1.1, java.version 21, spring-boot-starter-web -> spring-boot-starter-webmvc
2. SecurityConfig:
   - DaoAuthenticationProvider now takes the UserDetailsService in its constructor (Security 7)
   - requestMatchers("GET", ...) -> requestMatchers(HttpMethod.GET, ...). The old form passed
     "GET" as a URL pattern, which Security 7 rejects at startup.
3. JwtAuthFilter: removed deprecated org.springframework.lang.@NonNull
4. JwtUtil: uses javax.crypto.SecretKey directly (no cast)
5. application.properties: removed hibernate.dialect (Hibernate 7 auto-detects MySQL)

## Run
    mvn clean spring-boot:run
Requires MySQL on localhost:3307 with schema `ecommerce` (see application.properties).
Default admin: admin@bazaario.com / admin123
