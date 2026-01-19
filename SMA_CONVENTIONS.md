# SMA Development Conventions - Based on RentIn Reference Implementation

## Key Conventions Extracted from rentin-cust-mngt-app

### 1. **Application Entry Point**
```java
@RentInApplication  // Custom annotation instead of @SpringBootApplication
public class CustMngtApp {
    public static void main(String[] args) {
        SpringApplication.run(CustMngtApp.class, args);
    }
}
```
**Convention**: Use custom `@RentInApplication` annotation (equivalent to `@SmaApplication` for SMA)

---

### 2. **Controller Layer**
```java
@APIController  // Custom annotation instead of @RestController
@RequestMapping("/customer")
@Api(tags = "Customer API")  // Swagger annotation
public class CustomerController extends ApiRestServiceBinding {
    
    @Autowired
    CustomerBusinessService customerBusinessService;
    
    @GetMapping("/getByMobNum")
    ResponseEntity<CustomerModel> getCustomerByMobileNum(
        @RequestParam("mobNo") String mobNo) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("OperationName", arg1, arg2);
        // Business logic via service
        return processResponse(context, result);
    }
}
```

**Conventions**:
- Use `@APIController` (custom annotation) instead of `@RestController`
- Extend `ApiRestServiceBinding` for common response handling
- Use `ServiceRequestContext` for request tracking
- Use `createServiceRequestContext()` method
- Use `processResponse()` for response handling
- Include Swagger `@Api` annotation
- Throw proper exceptions

---

### 3. **Business Service Layer**
```java
@Component  // Use @Component, NOT @Service
public class CustomerBusinessService {
    
    @Autowired
    CustomerRepository customerRepository;
    
    @Autowired
    OTPRepository otpRepository;
    
    public CustomerModel registerCustomer(ServiceRequestContext context, 
                                         CustomerRegistrationRequest request) throws RentInException {
        // 1. Validate
        if (request.getFirstName() == null || request.getFirstName().isEmpty()) {
            throw new RentInException("First Name is mandatory");
        }
        
        // 2. Check existing
        CustomerAuthModel existing = customerAuthRepository.findByMobileNum(request.getMobileNum());
        if (existing != null) {
            throw new RentInException("Mobile number already registered");
        }
        
        // 3. Create entity
        CustomerModel customerModel = new CustomerModel();
        customerModel.setFirstName(request.getFirstName());
        // ... set other fields
        
        // 4. Save to repository
        CustomerModel savedCustomer = customerRepository.save(customerModel);
        
        return savedCustomer;
    }
}
```

**Conventions**:
- Use `@Component` annotation (NOT `@Service`)
- Accept `ServiceRequestContext` as first parameter
- Accept request model as second parameter
- Throw `RentInException` (or `SmaException`)
- Perform validation first
- Check for existing records
- Return entity model
- Business logic: separate from controller

---

### 4. **AMQP Binding Layer - Sender (Producer)**
```java
@AMQPSender  // Custom annotation for producers
@ConfigurationProperties(prefix = "rentin.app.amqp.customer.register")
public class AMQPCustomerRegisterSender extends RabbitMqMessageSender<CustomerModel> {

    @Override
    public void sendRentInMessage(ServiceRequestContext context, RentInMessage message) {
        sendMessage(context, message);
    }

    @Override
    protected String getInterfaceName() {
        return "Customer_Register_Sender";
    }
}
```

**Conventions**:
- Use `@AMQPSender` annotation for message publishers
- Use `@ConfigurationProperties` with prefix
- Extend `RabbitMqMessageSender<T>` generic base class
- Override `sendRentInMessage()` method
- Override `getInterfaceName()` method
- Use generic type for message payload

---

### 5. **AMQP Binding Layer - Receiver (Consumer)**
```java
@AMQPReceiver  // Custom annotation for consumers
@ConfigurationProperties(prefix = "rentin.app.amqp.customer.register")
public class AMQPCustomeRegisterReceiver extends RabbitMqMessageReceiver<CustomerRegistrationRequest> {

    @Autowired
    CustomerBusinessService customerBusinessService;
    
    @Override
    protected void processMessage(ServiceRequestContext context, RentInMessage message) {
        CustomerRegistrationRequest request = (CustomerRegistrationRequest) message.getPayload();
        try {
            CustomerModel result = customerBusinessService.registerCustomer(context, request);
        } catch (RentInException e) {
            throw new RentInException("Unable to register customer.", e);
        }
    }

    @Override
    protected String getInterfaceName() {
        return "Customer_Register_Receiver";
    }
}
```

**Conventions**:
- Use `@AMQPReceiver` annotation for message consumers
- Use `@ConfigurationProperties` with prefix
- Extend `RabbitMqMessageReceiver<T>` generic base class
- Override `processMessage()` method
- Override `getInterfaceName()` method
- Cast message payload from `RentInMessage`
- Call business service to process

---

### 6. **Configuration Properties Pattern**
```properties
# application.properties

###############################################################################
## AMQP Configuration
###############################################################################
rentin.app.amqp.customer.register.queue=customer.register.queue
rentin.app.amqp.customer.register.exchange=customer.exchange
rentin.app.amqp.customer.register.routing-key=customer.register

rentin.app.amqp.customer.update.queue=customer.update.queue
rentin.app.amqp.customer.update.exchange=customer.exchange
rentin.app.amqp.customer.update.routing-key=customer.update
```

**Conventions**:
- Use hierarchical properties with dots
- Prefix: `{app}.app.amqp.{feature}.{operation}`
- Group related configs together with comments
- Use `.properties` format (NOT `.yaml` in this implementation)

---

### 7. **Data Models**
- **Request Models**: `CustomerRegistrationRequest` → Used in API endpoints and AMQP receivers
- **Response Models**: `CustomerDetailResponse` → Used in API responses
- **Entity Models**: `CustomerModel`, `CustomerAuthModel` → Used in repository layer
- **Message Models**: `RentInMessage` → Used in AMQP communication

**Conventions**:
- Separate request/response/entity models
- Keep models in `sma-jpa-postgresql` for database entities
- Keep DTO models in `sma-common-datamodel` for shared usage

---

### 8. **Exception Handling**
```java
throw new RentInException("First Name is mandatory for Customer registration");
// Custom exception with context-specific messages
```

**Conventions**:
- Use `RentInException` (or `SmaException`) for domain-specific exceptions
- Include descriptive messages
- Wrap and rethrow with additional context

---

### 9. **Maven Dependencies Pattern**
```xml
<parent>
    <groupId>org.rentin.app</groupId>
    <artifactId>rentin-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.rentin.app</groupId>
        <artifactId>rentin-core-service</artifactId>
        <version>${rentin-app-version}</version>
    </dependency>
    <dependency>
        <groupId>org.rentin.app</groupId>
        <artifactId>rentin-api-platform-amqp-service</artifactId>
        <version>${rentin-app-version}</version>
    </dependency>
    <dependency>
        <groupId>org.rentin.app</groupId>
        <artifactId>rentin-jpa-postgresql</artifactId>
        <version>${rentin-app-version}</version>
    </dependency>
</dependencies>
```

**Conventions**:
- All apps depend on parent POM
- All apps depend on core-service
- All apps depend on AMQP service if using messaging
- All apps depend on JPA-PostgreSQL
- Use property variable for version: `${rentin-app-version}`

---

### 10. **Custom Annotations**
- `@APIController` - For REST controllers (instead of @RestController)
- `@AMQPSender` - For message producers
- `@AMQPReceiver` - For message consumers
- `@RentInApplication` - For main app class

---

## Summary of Conventions for SMA

1. **Controllers**: `@APIController`, extend `ApiRestServiceBinding`, use `ServiceRequestContext`
2. **Services**: `@Component` (not `@Service`), accept `ServiceRequestContext` as first param
3. **AMQP Sender**: `@AMQPSender`, extend `RabbitMqMessageSender<T>`, `@ConfigurationProperties`
4. **AMQP Receiver**: `@AMQPReceiver`, extend `RabbitMqMessageReceiver<T>`, `@ConfigurationProperties`
5. **Config**: `application.properties` format with hierarchical prefixes
6. **Exceptions**: Custom domain exceptions with context
7. **Models**: Separate request/response/entity models
8. **Dependencies**: Follow POM hierarchy, use version properties

