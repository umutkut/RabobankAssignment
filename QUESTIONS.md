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
- Yes, must follow IBAN format (e.g., NL91RABO1234567890)

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
- No, permanent until revoked

---

### 5. Lifecycle Management

**Q1:** Can a Power of Attorney be revoked?
- Yes, need "revoke" endpoint (soft delete with flag)

**Q2:** Who can revoke a PoA?
- Only the grantor

**Q3:** What happens if an account is closed?
- Out of scope for this assignment. 
- If there is time left, we can add a "close" endpoint that revokes all PoAs for the account. 

---
