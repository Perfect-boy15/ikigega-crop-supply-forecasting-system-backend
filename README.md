# Ikigega Crop Supply Forecasting System

Student: **KWIZERA Parfait**  
Student ID: **27603**  
Course: INSY 8322 Web Technology and Internet

Spring Boot backend for farmers, agronomists, crop types, warehouses, harvest forecasts and weighing tickets. Uses Java 21, Maven, Spring Web, Spring Data JPA and PostgreSQL.

## Run

1. Install JDK 21, Maven and PostgreSQL. Open a terminal in this directory (the directory containing `pom.xml`).
2. Create a PostgreSQL database, for example `CREATE DATABASE ikigega_db;`, owned by your database user.
3. Set `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD` for your database. In PowerShell:

   ```powershell
   $env:JAVA_HOME = 'C:\Program Files\Java\jdk-21.0.12.1' # adjust to your installation
   $env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5432/ikigega_db'
   $env:SPRING_DATASOURCE_USERNAME = 'your_database_user'
   $env:SPRING_DATASOURCE_PASSWORD = 'your_database_password'
   mvn spring-boot:run
   ```

4. The API runs at `http://localhost:8080/api`. Hibernate creates/updates tables; SQL logging is enabled.
5. Compile with `mvn compile`. If Maven selects an inaccessible local repository, explicitly pass a writable local repository: `mvn "-Dmaven.repo.local=C:/path/to/maven-cache" compile`.

## Postman and seed data

Import `ikigega-crop-supply-forecasting-system-midterm.postman_collection.json` into Postman. Set its `baseUrl` collection variable if necessary. Requests capture generated UUIDs into collection variables; no IDs are hardcoded. The collection contains assertions for status codes, exact messages and selected response contents. Providing assertions does not establish that they have been executed in Postman.

Use a **fresh demo database**. Run folders in this order:

1. **Step 0** creates four crop types, four warehouses, three agronomists, five farmers and ten forecasts through the save endpoints. Status changes use the PATCH endpoint. The first forecast on 20 October is withdrawn before the second is created, satisfying the visit conflict rule.
2. **Requirements** exercises queries and bonuses and creates a weighing ticket for a delivered forecast.
3. **Business rules and failure cases** checks 400/404/409 cases and mutates the seed data. Run once against the fresh seed; repeated runs need fresh data or adjusted expectations.
4. **A2 CRUD reference** is for individual requests. Foreign keys can prevent deletion of records still referenced by forecasts or join-table rows; delete dependent demo data first.

### Historical date conflict in the examination paper

Step 0 requires forecasts on **2026-10-20**, while D1 rejects forecast dates before `LocalDate.now()`. On 31 October 2026 or later those historical records cannot be newly created through the final save endpoint. Body statuses are deliberately ignored on save and must be changed through the status endpoint.

The application preserves the required `LocalDate.now()` rule. To reproduce the exact historical fixture through save endpoints, use a disposable demo environment whose date is no later than 20 October 2026, with the lecturer's agreement, or use records legitimately seeded earlier. Do not change the host clock or disable the rule merely to obtain passing results. For a present-day demo, agree on equivalent future dates and update the seed requests and date-query expectations together. The collection's separate D1 checks compute a future date automatically.

## Requirement map

| ID | Endpoint / implementation |
| --- | --- |
| A1 | Six UUID entities; inspect database tables and crop-type JSON |
| A2 | `/api/{crop-types,warehouses,agronomists,farmers,harvest-forecasts}`: POST `/save`, GET `/all`, GET `/{id}`, PUT `/update/{id}`, DELETE `/delete/{id}` |
| B1 | GET `/api/farmers/by-last-name?lastName=niyonzima`; derived ignore-case ordered query |
| B2 | GET `/api/harvest-forecasts/by-status?status=SUBMITTED`; derived enum query |
| B3 | GET `/api/harvest-forecasts/between?start=2026-10-01&end=2026-10-31`; derived inclusive range |
| C1 | GET `/api/agronomists/by-crop-type?name=maize`; JPQL collection join and LOWER |
| C2 | GET `/api/warehouses/free`; JPQL NOT EXISTS |
| C3 | GET `/api/crop-types/unused`; JPQL IS EMPTY |
| C4 | GET `/api/farmers/of-agronomist/{agronomistId}`; JPQL DISTINCT and path navigation |
| D1 | POST `/api/harvest-forecasts/save`; past-date and derived visit-conflict checks |
| D2 | PATCH `/api/harvest-forecasts/{id}/status?status=VERIFIED`; enum transition rules |
| D3 | PUT `/api/agronomists/{agronomistId}/crop-types/{cropTypeId}`; transactional owner-list addition |
| D4 | PATCH `/api/agronomists/{agronomistId}/warehouse?warehouseNumber=4`; derived warehouse lookup and existsByWarehouse |
| E1 | POST `/api/weighing-tickets/save?harvestForecastId=...`; delivered-only tickets |
| E2 | GET `/api/weighing-tickets/farmer/{farmerCode}`; JPQL path navigation and descending date |
| E3 | DELETE `/api/weighing-tickets/delete/{id}`; forecast delete checks derived existsByHarvestForecast |
| F1 | GET `/api/harvest-forecasts/stats/by-status`; JPQL GROUP BY and COUNT |
| F2 | PATCH `/api/harvest-forecasts/cancel-day?agronomistId=...&date=2026-10-20`; one modifying JPQL UPDATE in a transaction |
| Bonus | GET `/api/dashboard/summary`; ordered Map of counts |
| Bonus | GET `/api/harvest-forecasts/page?page=0&size=5&sort=forecastDate,desc`; Page metadata |

Ordinary CRUD services return String messages, Lists or Optionals. Controllers select HTTP statuses with conditionals. The specified reporting and bonus methods return `List<Object[]>`, `Page<HarvestForecast>` and `Map<String, Long>`. General forecast PUT updates its editable fields and relationships while preserving status. Only the status PATCH endpoint changes a forecast's status individually; F2 performs the specified bulk withdrawal.

Regenerate the collection after editing its source with `node tools/generate-postman.cjs` (Node.js is only needed for regeneration, not to run the API).

For automated local validation against a **fresh isolated database/schema**, start a separate API instance on port 18080 and run `node tools/check-api.cjs --shift-dates`. The script executes the collection requests and assertions, shifting the October/November fixture together so 20 October corresponds to tomorrow. Set `IKIGEGA_TEST_URL` to override the test API URL. It writes records and performs deletions, so do not point it at your normal populated database. This small runner supports the assertions used by this collection; Postman remains the required live-demo client.

Git publication, deadline compliance and the live demonstration are separate submission steps. No local automated check can establish the student's ability to explain the code during the live demo.
