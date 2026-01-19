# Customer Address Management - Complete Guide

## Overview
The Customer Address Management system allows customers to save multiple addresses with support for setting a default address. The default address ID is automatically updated in the customer table.

## Features
- ✅ Multiple addresses per customer
- ✅ Default address selection
- ✅ Address types: HOME, OFFICE, OTHER
- ✅ Soft delete (deactivate) and hard delete
- ✅ Automatic default address management
- ✅ GPS coordinates support
- ✅ Security checks (customer owns address)

## Database Schema

### customer_address Table
```sql
CREATE TABLE customer_address (
    address_id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    address_type VARCHAR(50),
    address_label VARCHAR(100),
    flat_no VARCHAR(50),
    building_name VARCHAR(200),
    street_address TEXT,
    landmark VARCHAR(200),
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(20),
    country VARCHAR(100) DEFAULT 'India',
    contact_name VARCHAR(200),
    contact_phone VARCHAR(20),
    is_default BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_customer_address_customer 
        FOREIGN KEY (customer_id) 
        REFERENCES customer(customer_id) 
        ON DELETE CASCADE
);
```

### customer Table Update
```sql
ALTER TABLE customer 
ADD COLUMN default_address_id BIGINT;

ALTER TABLE customer 
ADD CONSTRAINT fk_customer_default_address 
    FOREIGN KEY (default_address_id) 
    REFERENCES customer_address(address_id) 
    ON DELETE SET NULL;
```

## API Endpoints

### Base URL
```
http://localhost:9091/customer/address
```

### 1. Create Address
**POST** `/create`

Creates a new address for a customer. If this is the first address, it's automatically set as default.

**Request Body:**
```json
{
  "customerId": 1,
  "addressType": "HOME",
  "addressLabel": "My Home",
  "flatNo": "A-301",
  "buildingName": "Sunset Apartments",
  "streetAddress": "123 MG Road",
  "landmark": "Opposite HDFC Bank",
  "city": "Mumbai",
  "state": "Maharashtra",
  "pincode": "400001",
  "country": "India",
  "contactName": "John Doe",
  "contactPhone": "+91-9876543210",
  "isDefault": true,
  "isActive": true,
  "latitude": 19.0760,
  "longitude": 72.8777
}
```

**Response:**
```json
{
  "addressId": 1,
  "customerId": 1,
  "addressType": "HOME",
  "addressLabel": "My Home",
  "flatNo": "A-301",
  ...
  "isDefault": true,
  "isActive": true,
  "createdDate": "2025-12-12T15:30:00",
  "updatedDate": "2025-12-12T15:30:00"
}
```

**cURL:**
```bash
curl -X POST "http://localhost:9091/customer/address/create" \
  -H "Content-Type: application/json" \
  -d @sample-customer-address-home.json
```

---

### 2. Get All Addresses for Customer
**GET** `/getByCustomer/{customerId}`

Retrieves all addresses (active and inactive) for a specific customer.

**Example:**
```bash
curl -X GET "http://localhost:9091/customer/address/getByCustomer/1"
```

**Response:**
```json
[
  {
    "addressId": 1,
    "customerId": 1,
    "addressType": "HOME",
    "isDefault": true,
    ...
  },
  {
    "addressId": 2,
    "customerId": 1,
    "addressType": "OFFICE",
    "isDefault": false,
    ...
  }
]
```

---

### 3. Get Active Addresses
**GET** `/getActive/{customerId}`

Retrieves only active addresses for a customer.

**Example:**
```bash
curl -X GET "http://localhost:9091/customer/address/getActive/1"
```

---

### 4. Get Default Address
**GET** `/getDefault/{customerId}`

Retrieves the default address for a customer.

**Example:**
```bash
curl -X GET "http://localhost:9091/customer/address/getDefault/1"
```

**Response:**
```json
{
  "addressId": 1,
  "customerId": 1,
  "addressType": "HOME",
  "isDefault": true,
  ...
}
```

---

### 5. Get Address by ID
**GET** `/get/{addressId}?customerId={customerId}`

Retrieves a specific address with security check.

**Example:**
```bash
curl -X GET "http://localhost:9091/customer/address/get/1?customerId=1"
```

---

### 6. Update Address
**PUT** `/update`

Updates an existing address. If marked as default, all other addresses are unmarked.

**Request Body:**
```json
{
  "addressId": 1,
  "customerId": 1,
  "addressType": "HOME",
  "addressLabel": "Updated Home",
  "flatNo": "A-301",
  ...
  "isDefault": true
}
```

**cURL:**
```bash
curl -X PUT "http://localhost:9091/customer/address/update" \
  -H "Content-Type: application/json" \
  -d '{
    "addressId": 1,
    "customerId": 1,
    "addressLabel": "Updated Home",
    "streetAddress": "Updated street address",
    "isDefault": true
  }'
```

---

### 7. Set Address as Default
**PUT** `/setDefault/{addressId}?customerId={customerId}`

Marks a specific address as default. All other addresses are automatically unmarked.

**Example:**
```bash
curl -X PUT "http://localhost:9091/customer/address/setDefault/2?customerId=1"
```

**Response:**
```json
{
  "status": "SUCCESS",
  "errorMessage": "Address set as default successfully"
}
```

**What Happens:**
1. All addresses for customer are unmarked as default
2. Specified address is marked as default
3. `customer.default_address_id` is updated to this address ID

---

### 8. Delete Address
**DELETE** `/delete/{addressId}?customerId={customerId}`

Permanently deletes an address. If it was the default, another address is auto-selected as default.

**Example:**
```bash
curl -X DELETE "http://localhost:9091/customer/address/delete/2?customerId=1"
```

**Behavior:**
- If deleted address was default → First remaining active address becomes default
- If no addresses remain → `customer.default_address_id` is set to NULL

---

### 9. Deactivate Address (Soft Delete)
**PUT** `/deactivate/{addressId}?customerId={customerId}`

Marks address as inactive without deleting it.

**Example:**
```bash
curl -X PUT "http://localhost:9091/customer/address/deactivate/2?customerId=1"
```

**Behavior:**
- Sets `is_active = false`
- If this was default → Another active address is set as default
- Address remains in database for record-keeping

---

### 10. Get Addresses by Type
**GET** `/getByType/{customerId}/{addressType}`

Retrieves addresses filtered by type (HOME, OFFICE, OTHER).

**Example:**
```bash
curl -X GET "http://localhost:9091/customer/address/getByType/1/HOME"
```

---

### 11. Get Address Count
**GET** `/count/{customerId}`

Returns total number of addresses for a customer.

**Example:**
```bash
curl -X GET "http://localhost:9091/customer/address/count/1"
```

**Response:**
```json
3
```

---

## Address Types

| Type | Description | Use Case |
|------|-------------|----------|
| HOME | Home address | Personal deliveries |
| OFFICE | Office/workplace | Work-related deliveries |
| OTHER | Custom address | Parents house, friend's place, etc. |

## Business Logic

### Default Address Management

1. **First Address Created:**
   - Automatically set as default
   - `customer.default_address_id` is updated

2. **New Address Marked as Default:**
   - All other addresses unmarked
   - New address marked as default
   - `customer.default_address_id` updated

3. **Default Address Deleted:**
   - First remaining active address becomes default
   - If no addresses remain, `default_address_id` set to NULL

4. **Default Address Deactivated:**
   - First remaining active address becomes default
   - Original address kept but marked inactive

### Security
- All operations require `customerId` parameter
- Prevents users from accessing/modifying other users' addresses
- Repository queries filter by `customer_id`

## Database Migration

Run the migration script to create tables and add constraints:

```bash
psql -U postgres -d rentinapp -f database_migration_customer_address.sql
```

## Testing Scenarios

### Scenario 1: Create First Address
```bash
# Create first address (automatically becomes default)
curl -X POST "http://localhost:9091/customer/address/create" \
  -H "Content-Type: application/json" \
  -d @sample-customer-address-home.json

# Verify in database
SELECT * FROM customer WHERE customer_id = 1;
-- default_address_id should be set to the new address_id
```

### Scenario 2: Add Multiple Addresses
```bash
# Create home address
curl -X POST "http://localhost:9091/customer/address/create" \
  -H "Content-Type: application/json" \
  -d @sample-customer-address-home.json

# Create office address
curl -X POST "http://localhost:9091/customer/address/create" \
  -H "Content-Type: application/json" \
  -d @sample-customer-address-office.json

# Get all addresses
curl -X GET "http://localhost:9091/customer/address/getByCustomer/1"
```

### Scenario 3: Change Default Address
```bash
# Set office address as default
curl -X PUT "http://localhost:9091/customer/address/setDefault/2?customerId=1"

# Verify default changed
curl -X GET "http://localhost:9091/customer/address/getDefault/1"
```

### Scenario 4: Delete Default Address
```bash
# Delete current default address
curl -X DELETE "http://localhost:9091/customer/address/delete/1?customerId=1"

# Check new default was auto-selected
curl -X GET "http://localhost:9091/customer/address/getDefault/1"
```

## Integration with Order Management

When creating an order, use the default address:

```bash
# Get customer's default address
curl -X GET "http://localhost:9091/customer/address/getDefault/1"

# Use the addressId in order creation
curl -X POST "http://localhost:9092/order/create" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "deliveryAddressId": <address_id_from_above>,
    ...
  }'
```

## Error Handling

### Common Errors

1. **Customer Not Found**
```json
{
  "status": "FAILED",
  "errorMessage": "Customer not found with ID: 999"
}
```

2. **No Default Address**
```json
{
  "status": "FAILED",
  "errorMessage": "No default address found for customer ID: 1"
}
```

3. **Address Doesn't Belong to Customer**
```json
{
  "status": "FAILED",
  "errorMessage": "Address not found or doesn't belong to customer"
}
```

## Best Practices

1. **Always use customer ID for security**
   - Never trust address ID alone
   - Always pass and verify `customerId`

2. **Handle default address gracefully**
   - Check if customer has addresses before operations
   - Use `getDefault` endpoint for order creation

3. **Use soft delete when possible**
   - Deactivate instead of delete for audit trail
   - Hard delete only when necessary

4. **Set meaningful labels**
   - Use `addressLabel` for user-friendly display
   - Example: "Parent's House", "Weekend Home"

5. **Include GPS coordinates**
   - Helps with delivery optimization
   - Enables location-based features

## Database Indexes

For optimal performance, the following indexes are created:

```sql
-- Customer ID lookup
CREATE INDEX idx_customer_address_customer_id 
    ON customer_address(customer_id);

-- Default address lookup
CREATE INDEX idx_customer_address_is_default 
    ON customer_address(customer_id, is_default);

-- Active addresses lookup
CREATE INDEX idx_customer_address_is_active 
    ON customer_address(customer_id, is_active);

-- Type-based lookup
CREATE INDEX idx_customer_address_type 
    ON customer_address(customer_id, address_type);

-- Location-based lookup
CREATE INDEX idx_customer_address_pincode 
    ON customer_address(pincode);
    
CREATE INDEX idx_customer_address_city 
    ON customer_address(city);
```

## Swagger UI

Access the Swagger documentation at:
```
http://localhost:9091/swagger-ui.html
```

Look for "Customer Address Management" section.

---

**Last Updated**: December 12, 2025  
**Version**: 1.0  
**Status**: ✅ Implementation Complete
