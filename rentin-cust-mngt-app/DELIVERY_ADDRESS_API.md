# Add Delivery Address API

## Overview
Endpoint to add delivery addresses for customers during order checkout or profile management.

## Endpoint Details

### Add Delivery Address
**URL**: `POST /customer/address/addDeliveryAddress`

**Description**: Adds a new delivery address for a customer. This is a convenience endpoint that automatically sets the address as active and optionally as the default delivery address.

**Query Parameters**:
- `setAsDefault` (optional, default: false) - Set this address as the customer's default delivery address

**Request Body**:
```json
{
  "customerId": 1,
  "addressType": "HOME",
  "addressLabel": "My Home",
  "flatNo": "A-102",
  "buildingName": "Sunshine Apartments",
  "streetAddress": "MG Road",
  "landmark": "Near Metro Station",
  "city": "Bangalore",
  "state": "Karnataka",
  "pincode": "560001",
  "country": "India",
  "contactName": "John Doe",
  "contactPhone": "+91-9876543210",
  "latitude": 12.9716,
  "longitude": 77.5946
}
```

**Response**:
```json
{
  "addressId": 1,
  "customerId": 1,
  "addressType": "HOME",
  "addressLabel": "My Home",
  "flatNo": "A-102",
  "buildingName": "Sunshine Apartments",
  "streetAddress": "MG Road",
  "landmark": "Near Metro Station",
  "city": "Bangalore",
  "state": "Karnataka",
  "pincode": "560001",
  "country": "India",
  "contactName": "John Doe",
  "contactPhone": "+91-9876543210",
  "isDefault": true,
  "isActive": true,
  "latitude": 12.9716,
  "longitude": 77.5946,
  "createdDate": "2025-12-12T10:30:00",
  "updatedDate": "2025-12-12T10:30:00"
}
```

## Usage Examples

### 1. Add Regular Delivery Address
```bash
curl -X POST "http://localhost:9091/customer/address/addDeliveryAddress" \
  -H "Content-Type: application/json" \
  -d @sample-delivery-address.json
```

### 2. Add and Set as Default
```bash
curl -X POST "http://localhost:9091/customer/address/addDeliveryAddress?setAsDefault=true" \
  -H "Content-Type: application/json" \
  -d @sample-delivery-address.json
```

### 3. Postman Example
- **Method**: POST
- **URL**: `http://localhost:9091/customer/address/addDeliveryAddress?setAsDefault=true`
- **Headers**: 
  - `Content-Type: application/json`
- **Body**: (Select raw JSON and paste the request body from above)

## Address Types
- **HOME** - Residential address
- **OFFICE** - Work address
- **OTHER** - Any other type

## Behavior

### First Address
- Automatically becomes the default delivery address
- `isDefault` is set to `true`
- Customer's `default_address_id` is updated

### Subsequent Addresses
- Added as additional delivery options
- Can be set as default using `setAsDefault=true` parameter
- When set as default, previous default is automatically unmarked

### Active Status
- All addresses added via this endpoint are automatically marked as `isActive = true`
- Inactive addresses are ignored during order checkout

## Integration with Order Creation

### Get Default Delivery Address
```bash
# Get customer's default delivery address
curl -X GET "http://localhost:9091/customer/address/getDefault/{customerId}"
```

### Use in Order Creation
```json
{
  "customerId": 1,
  "deliveryAddressId": 1,  // Use addressId from delivery address
  "products": [...],
  "paymentDetails": {...}
}
```

## Testing Workflow

### Step 1: Add First Delivery Address
```bash
# Will automatically become default
curl -X POST "http://localhost:9091/customer/address/addDeliveryAddress" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "addressType": "HOME",
    "addressLabel": "Home Address",
    "streetAddress": "123 Main St",
    "city": "Mumbai",
    "state": "Maharashtra",
    "pincode": "400001",
    "country": "India",
    "contactName": "John Doe",
    "contactPhone": "+91-9876543210"
  }'
```

### Step 2: Add Office Address
```bash
# Add second address
curl -X POST "http://localhost:9091/customer/address/addDeliveryAddress" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "addressType": "OFFICE",
    "addressLabel": "Office",
    "streetAddress": "456 Business Park",
    "city": "Mumbai",
    "state": "Maharashtra",
    "pincode": "400051",
    "country": "India",
    "contactName": "John Doe",
    "contactPhone": "+91-9876543210"
  }'
```

### Step 3: Change Default to Office
```bash
# Set office address as default
curl -X POST "http://localhost:9091/customer/address/addDeliveryAddress?setAsDefault=true" \
  -H "Content-Type: application/json" \
  -d '{...office address with different label or update...}'

# OR use the setDefault endpoint
curl -X PUT "http://localhost:9091/customer/address/setDefault/{addressId}?customerId=1"
```

### Step 4: Get All Delivery Addresses
```bash
# View all addresses
curl -X GET "http://localhost:9091/customer/address/getActive/1"
```

### Step 5: Get Default for Order
```bash
# Get default for quick checkout
curl -X GET "http://localhost:9091/customer/address/getDefault/1"
```

## Error Handling

### Customer Not Found
```json
{
  "status": "ERROR",
  "message": "Customer not found"
}
```

### Invalid Address Data
```json
{
  "status": "ERROR",
  "message": "Required fields missing: streetAddress, city, pincode"
}
```

### Duplicate Default (Handled Automatically)
- System automatically resets other addresses when setting new default
- No manual intervention needed

## Best Practices

### 1. Collect Complete Information
- Always collect contact name and phone for delivery
- Get landmark for easier delivery location
- Collect GPS coordinates if available (helps delivery agents)

### 2. Address Validation
- Validate pincode format
- Verify city/state combinations
- Check phone number format

### 3. User Experience
- Show existing addresses before adding new one
- Highlight default address in UI
- Allow quick selection during checkout

### 4. Security
- Always verify customerId belongs to logged-in user
- Don't allow adding addresses for other customers
- Validate customer session/token before API call

## API Comparison

| Endpoint | Use Case | Auto Active | Default Option |
|----------|----------|-------------|----------------|
| `/create` | General address creation | No | Manual in body |
| `/addDeliveryAddress` | Order/delivery specific | ✅ Yes | Query param |

## Swagger UI
Access the interactive API documentation at:
```
http://localhost:9091/swagger-ui.html
```
Look for "Customer Address Management" section.

---

**Created**: December 12, 2025  
**Status**: ✅ Ready for Use  
**Module**: rentin-cust-mngt-app
