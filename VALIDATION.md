# Local validation — 31 October 2026

The API started successfully with Java 21.0.12.1, Spring Boot 3.5.16 and PostgreSQL 18, on port 18080. Tests used the separate `codex_midterm_validation_20261031` schema rather than the application's existing public-schema records.

`node tools/check-api.cjs --shift-dates` passed **274 assertions across 145 requests**. These are local HTTP checks executing the collection's scripts, not a claim of a completed Postman live demonstration.

Verified behavior includes:

- Endpoint-based creation of all Step 0 records, with status changes through PATCH.
- All five required entities' successful save/get/update/delete lifecycles and missing-record cases.
- Required derived-query and JPQL endpoints, duplicate-free farmers, unused crop types and free warehouses.
- Past-date rejection, duplicate-visit rejection and forced initial SUBMITTED status.
- Valid status change and rejection of a transition out of DELIVERED.
- Additive crop assignment and duplicate rejection; warehouse occupancy rejection and freeing the old warehouse after a move.
- Delivered-only weighing-ticket creation, farmer history, protected forecast deletion and ticket deletion.
- Status aggregation, transactional bulk cancellation with its affected-row count, and preservation of DELIVERED records.
- Dashboard key order and pagination totals/content size.

Database inspection confirmed seven tables (six entities and the join table), UUID IDs, date/integer/string columns, six foreign keys and the unique farmer-code constraint. SQL output confirmed the joins, reports and single bulk UPDATE.

Historical seed dates were shifted together into the future for this run; the required `LocalDate.now()` guard was unchanged. Exact historical seed creation remains subject to the date conflict documented in README. The collection itself retains the paper's October/November 2026 dates.

The examination's public repository, deadline and live explanation requirements were not performed by this validation.

Maven's fresh compilation emitted the 28 application classes, which were used for the successful API run, but its compiler then failed while closing dependency archives with a Windows `AccessDeniedException`. Incremental Maven compilation reported success afterward. A clean-build success is therefore not claimed; the environment's archive/file permissions still need resolution for reproducible fresh Maven builds.

The validation application was stopped and its temporary schema removed after testing. Existing public-schema records were left unchanged.

After the Java readability changes, the same 145 requests and 274 assertions passed again against a fresh `codex_midterm_readability_20261031` schema. Changes used explicit Optional lookup, named intermediate values for visit conflicts and dashboard counts, and clearer warehouse/crop-type variables. Endpoint contracts and query types were preserved. The fresh Maven compilation still encountered the archive-permission error described above after emitting the application classes. The second test instance and schema were also cleaned up.
