# JWT / Spring Security Homework Project

This project is a homework for [T1 Open School of Java development][1], demonstrating use of [Spring Security][2] and [JWT Tokens][12] for creating a security layer in a basic web application. [Spring Boot][3] is used for developing of basic web application having traditional controller, repository and service layers. The application features an API, consisting of public unsecured part and secured part, where user credentials are needed for access. 

API complies with [OpenAPI Specification][4] v.[3.0.0][5] and the code is generated with help of [OpenAPI Generator plugin for Gradle][6]. The API definition file can be found at: `/scr/main/resources/apidef.yaml`

API documentation is generated using [springdoc-openapi][7] java library and is accessible locally as [JSON][8] or [Swagger UI][9] after running the application.

Access rights are checked based on Spring Security system, `@PreAuthorize` annotations secure methods where access requires different roles. Information about user and his roles is contained in JWT token that user gets after authenticating at authentication endpoint `/public/token/auth`. If user is registered in database ([PostgreSQL][10] is used as database in this project) and has provided correct password, access and refresh tokens are granted by the endpoint. Access token is used to access secured part of the application. It has configurable lifetime (which should be rather short for security reasons) and when the token expires, a new one can be received at the refresh endpoint `/public/token/refresh`. Refresh tokens are stored in [Redis][11] database.

Another unsecured endpoint is `/public/newuser` and is used for creation of a new user. An anonymous user can also visit `/public/info` which imitates information in public domain, accessible without registration.

To mock resources which are accessible only to registered users and furthermore, authorized users, endpoints `/user/info`, `/moderator/info` and `/admin/info` are used. Every endpoint requires user to have corresponding roles. For simplicity's sake, roles are set at the moment of user creation.

## Prerequisites

In order to run this project...

- you must have access to PostgreSQL instance which holds a database named `postgres` and user `postgres`. You can modify `application.yaml` file to use your specific settings and names
- you must have access to Redis instance for storing refresh tokens

Project features `docker-compose.yaml` that can be used to quickly set up an environment, featuring Redis and PostgreSQL instances with their standard respective ports. Also an [Adminer][13] instance is available on port `8088`.

> Note: project features rather detailed error messages. This, among other things, is not recommended in real-life application for security reasons

## API

### Public API

---
- `GET` /public/info

Returns an unprotected resource
- parameters:

  *none*
- responses:
    - `200` successful operation
        - **string** `information`
    - `500` internal error

---
- `POST` /public/token/refresh

Use 'POST' request with refresh token sent in body to receive new security tokens
- parameters:

  *none*
- request body:

  **`refreshToken`**: *string* example: *aWNoYmlu.ZWlu.dG9rZW4=*
- responses:
    - `200` successful authentication
        - **string** `token` example: *aWNoYmlu.ZWlu.dG9rZW4=*
        - **string** `refreshToken` example: *WkdWeWVtaHBiWE41WVE9PQ==*
    - `403` operation failure
        - **string** `information`
    - `500` internal error

---
- `POST` /public/token/auth

Use 'POST' request with username and password sent in body to authenticate yourself and get security tokens
- parameters:

  *none*
- request body:

  - **`username`**: *string* example: *user123*
  - **`password`**: *string* example: *passWord!123*
- responses:
    - `200` successful authentication
        - **string** `token` example: *aWNoYmlu.ZWlu.dG9rZW4=*
        - **string** `refreshToken` example: *WkdWeWVtaHBiWE41WVE9PQ==*
    - `403` operation failure
        - **string** `information`
    - `500` internal error

---
- `POST` /public/newuser

Use 'POST' request for creating a new user with a set of specified roles
- parameters:

  *none*
- request body:

  - **`username`**: *string* example: *user123*
  - **`password`**: *string* example: *passWord!123*
  - **`email`**: *string* example: *mail@server.com*
  - **`roles`**: **array** of *string* example: *[USER, ADMIN]*
- responses:
    - `202` request accepted
    - `409` operation failure
        - **string** `information`
    - `500` internal error

---

### User API

---
- `GET` /user/info

Returns resource, accessible only for users with role 'USER'
- parameters:

  *none*
- responses:
    - `200` successful operation
        - **string** `information`
    - `403` operation failure
        - **string** `information`
    - `500` internal error

---

### Moderator API

---
- `GET` /moderator/info

Returns resource, accessible only for users with role 'MODERATOR'
- parameters:

  *none*
- responses:
    - `200` successful operation
        - **string** `information`
    - `403` operation failure
        - **string** `information`
    - `500` internal error

---

### Admin API

---
- `GET` /admin/info

Returns resource, accessible only for users with role 'ADMIN'
- parameters:

  *none*
- responses:
    - `200` successful operation
        - **string** `information`
    - `403` operation failure
        - **string** `information`
    - `500` internal error

---

[1]: https://t1.ru/internship/item/otkrytaya-shkola-dlya-java-razrabotchikov/
[2]: https://docs.spring.io/spring-framework/reference/core/aop.html
[3]: https://spring.io/projects/spring-boot
[4]: https://www.openapis.org/
[5]: https://swagger.io/specification/v3/
[6]: https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-gradle-plugin
[7]: https://springdoc.org/
[8]: http://localhost:8080/v3/api-docs
[9]: http://localhost:8080/swagger-ui/index.html
[10]: https://www.postgresql.org/
[11]: https://redis.io/
[12]: https://jwt.io/
[13]: https://www.adminer.org/
