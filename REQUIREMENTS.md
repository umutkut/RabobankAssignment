# Requirements Document
Power of Attorney Management API

## Overview

This API allows account holders to grant and manage Power of Attorney (PoA) authorizations for their payment and savings
accounts.

## Business Rules

### Power of Attorney Creation
- A grantor can only grant access to accounts they own (grantorName must match accountHolderName)
- No duplicate PoAs allowed - same grantor/grantee/account combination returns 409 Conflict
- Account owners automatically have WRITE access to their own accounts; this access is implicit and is NOT stored as a
  PoA record (i.e., not part of PoA lifecycle, not updatable/deletable, not audited as a PoA)
- No limit on number of PoAs a grantor can create

### Authorization Types
- READ - view account information
- WRITE - full access to account operations
- Both PAYMENT and SAVINGS accounts support both authorization types
- Authorization can be upgraded/downgraded via UPDATE endpoint

### Account Types
- PAYMENT - payment account
- SAVINGS - savings account

### Lifecycle

- PoAs are permanent until deleted
- Only the grantor can delete a PoA
- Deletion removes the PoA from the database (there should be audit trail for this)
- No expiration dates

## API Endpoints

### Create Power of Attorney

POST /api/v1/account/authorization
- Creates new PoA authorization
- Returns 409 if duplicate exists
- Returns 403 if grantorName doesn't match accountHolderName
- User cannot grant access to him/herself

### Get Accounts by Grantee

GET /api/v1/account/accessible-by/{granteeName}
- Returns the union of:
    - Ownership-based access for the requester (always WRITE, implicit, not stored as PoA)
    - Delegated access from stored PoAs where `granteeName = requester`
- Response model: each item contains the account details plus an `authorization` field
    - For owned accounts: `authorization` is `WRITE`
    - For delegated accounts: `authorization` is the PoA-provided value (`READ` or `WRITE`)
- Pagination and sorting are not supported in the current implementation

### Get Accounts by Grantor

GET /api/v1/account/authorization/granted-by/{grantorName}
- Returns all PoAs created by a grantor
- Pagination and sorting are not supported in the current implementation

### Get Power of Attorney

GET /api/v1/account/authorization/{poaId}
- Returns PoA details of provided ID

### Update Power of Attorney

PUT /api/v1/account/authorization/{id}?newAuthorization=READ|WRITE
- Updates authorization type (READ/WRITE)
- The new authorization is provided as a query parameter `newAuthorization`
- If request comes with the same authorization type, no change is made and the existing PoA is returned

### Delete Power of Attorney

DELETE /api/v1/account/authorization/{id}?grantorName={name}
- Deletes the PoA
- Only the grantor can perform deletion (validated via `grantorName` query parameter)

## Error Handling

Error responses use ResponseEntity with HttpStatus and {X}ApiResponse body.

Common status codes:
- 200 OK - successful retrieval
- 201 Created - successful creation
- 400 Bad Request - validation errors
- 404 Not Found - resource not found
- 409 Conflict - duplicate PoA
- 500 Internal Server Error - server errors

## Data Storage

- Store in MongoDB
- Expected volume: hundreds of PoAs
- Deletion removes PoAs
- Audit fields

## Non-Functional Requirements

### Performance
- API response time: under 200ms

### Testing
- Unit tests + integration tests required
- Target coverage: 80%
- Use mocked repositories for tests

### Documentation
- Swagger/OpenAPI documentation required
- Code should be self-documenting (no JavaDoc needed)

### Security
- No authentication/authorization in this implementation
- Assume external service handles auth

### Other
- No caching required
- No rate limiting required
- No deployment dependencies

## Out of Scope
- Real person validation
- Account closure handling
- Production-level audit trails
- Role-based access control
- Rate limiting

