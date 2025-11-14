# Requirements Clarification Questionnaire

This document is used to clarify requirements for the Power of Attorney API. 
The questions are self-answered and **sake of simplicity** is taken into account.

## Business Requirements

### 1. Power of Attorney Creation

**Q1:** Can a grantor create multiple Power of Attorney grants for the **same account** to the **same grantee**?
- Don't allow duplicates. If a duplicate is attempted, return 409 Conflict.

**Q2:** Can a grantor grant access to an account they don't own?
- No, must validate that `grantorName` matches `accountHolderName`

**Q3:** Should there be any validation on account numbers?
- ~~Yes, must follow IBAN format (e.g., NL91RABO1234567890)~~ Account creation is out of scope for this assignment.

**Q4:** Can the same person be both grantor and grantee (self-granting) for the same account?
- Yes, if a person owns the account, he/she should automatically get WRITE access  

**Q5:** Is there a limit on how many PoAs a grantor can create?
- No limits

---

### 2. Authorization Types & Account Types

**Q1:** Are there different rules for PAYMENT vs SAVINGS accounts?
- No different rules

**Q2:** Can authorization types be upgraded/downgraded later?
- Yes, need UPDATE endpoint to change authorization type

---

### 3. Power of Attorney Retrieval

**Q1:** When retrieving accounts for a grantee, should we return:
- List of all PoAs

**Q2:** Should the response include grantor information?
- Yes, include `grantorName` in response

**Q3:** Should results be sorted?
- Should return paginated results. If no sorting specified, sort by `accountNumber` 

**Q4:** Should there be pagination?
- Yes, with the default page size: 20

---

### 4. Data Integrity & Validation

**Q1:** Should we validate that names (grantor/grantee) are real people?
- No, accept any non-empty string (not in project scope)

**Q2:** What should happen if account balance is negative or zero?
- Allow any value (including negative)

**Q3:** Should we store audit information?
- Yes, creation + last modified + modified by user (lower priority, if there is time to do it)

**Q4:** Is there a concept of expiration for PoAs?

- No, permanent until deleted

---

### 5. Lifecycle Management

**Q1:** Can a Power of Attorney be deleted?

- Yes, need "delete" endpoint

**Q2:** Who can delete a PoA?
- Only the grantor

**Q3:** What happens if an account is closed?
- Out of scope for this assignment. 
- If there is time left, we can add a "close" endpoint that deletes all PoAs for the account.

---

## System Requirements

### 6. API Design

**Q1:** What format should error responses use?
- Use ResponseEntity with `HttpStatus` and `XApiResponse` body

**Q2:** What endpoints are required?
- POST - Create PoA
- GET - Retrieve accounts by grantee
- GET - Retrieve accounts by grantor
- GET - Retrieve specific account by account number
- PUT - Update PoA
- DELETE - Delete PoA

---

### 7. Data Storage

**Q1:** What's the expected data volume?
- Hundreds of PoAs (Sake of simplicity, not realistic)

---

### 8. Security & Authentication

**Q1:** Is authentication required for this assignment?
- No, for the sake of simplicity. Assume a dedicated service handles authentication/authorization.

**Q2:** Should there be role-based access control?
- No, for the sake of simplicity.

**Q3:** Should there be rate limiting?
- No, for the sake of simplicity.

---

### 9. Testing Requirements

**Q1:** What level of test coverage is expected?
- Unit + integration tests
- Minimum coverage: 80%

**Q2:** Should we use embedded MongoDB for tests?
- Mock repositories only

---

### 10. Non-Functional Requirements

**Q1:** What should be the API response time?
- Under 200ms

**Q2:** Should the system be production-ready?

- Yes, the code should be production-ready.

**Q3:** Should we implement caching?
-  For the sake of simplicity, assume no caching.

---

### 11. Documentation Requirements

**Q1:** Should we provide API documentation?
- Yes, Swagger/OpenAPI

**Q2:** Should code have JavaDoc comments?
- No, code should be self-documenting

**Q3:** Should there be a deployment guide?
- For the sake of simplicity, assume no external dependencies.
