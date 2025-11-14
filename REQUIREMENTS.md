# Requirements Document
Power of Attorney Management API

## Overview
This API allows account holders to grant and manage Power of Attorney authorizations for their payment and savings accounts.

## Business Rules

### Power of Attorney Creation
- A grantor can only grant access to accounts they own (grantorName must match accountHolderName)
- No duplicate PoAs allowed - same grantor/grantee/account combination returns 409 Conflict
- Account owners automatically have WRITE access to their own accounts
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
POST /api/v1/power-of-attorney
- Creates new PoA authorization
- Returns 409 if duplicate exists
- Returns 400 if grantorName doesn't match accountHolderName

### Get Accounts by Grantee
GET /api/v1/power-of-attorney/accounts?requester={requesterName}&page={num}&size={size}
- Returns paginated list of all PoAs for a grantee
- Default page size: 20
- Default sorting: by accountNumber

### Get Accounts by Grantor
GET /api/v1/power-of-attorney/accounts/granted/{requesterName}&page={num}&size={size}
- Returns all PoAs created by a grantor
- Default page size: 20
- Default sorting: by accountNumber

### Get Specific Account
GET /api/v1/power-of-attorney/account/{accountNumber}
- Returns PoA details for specific account

### Update Power of Attorney
PUT /api/v1/power-of-attorney/{id}
- Updates authorization type (READ/WRITE)

### Delete Power of Attorney
DELETE /api/v1/power-of-attorney/{id}

- Deletes the PoA
- Only the grantor can perform deletion

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
- Audit fields: createdAt, lastModifiedAt, modifiedBy (lower priority)

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

