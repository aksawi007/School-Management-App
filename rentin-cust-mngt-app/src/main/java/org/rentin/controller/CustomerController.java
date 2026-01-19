package org.rentin.controller;

import io.swagger.annotations.Api;
import org.rentin.amqp.AMQPCustomerRegisterSender;
import org.rentin.amqp.AMQPCustomerUpdateSender;
import org.rentin.jpa.model.customer.CustomerFavouriteModel;
import org.rentin.jpa.model.customer.CustomerModel;
import org.rentin.jpa.model.customer.CustomerAuthModel;
import org.rentin.jpa.repository.customer.CustomerRepository;
import org.rentin.jpa.repository.customer.CustomerAuthRepository;
import org.rentin.platform.common.datamodel.app.*;
import org.rentin.platform.common.datamodel.dto.CustomerStatus;
import org.rentin.platform.core.annotation.APIController;
import org.rentin.platform.core.exception.RentInException;
import org.rentin.platform.core.restcontroller.ApiRestServiceBinding;
import org.rentin.platform.core.service.ServiceRequestContext;
import org.rentin.platform.core.utils.RentInApiUtils;
import org.rentin.service.CustomerBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@APIController
@RequestMapping("/customer")
@Api(tags = "Customer API")
public class CustomerController extends ApiRestServiceBinding {

    @Autowired
    AMQPCustomerRegisterSender amqpCustomerRegisterSender;
    @Autowired
    AMQPCustomerUpdateSender amqpCustomerUpdateSender;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CustomerAuthRepository customerAuthRepository;

    @Autowired
    CustomerBusinessService customerBusinessService;

    private String generateCustomerId() {
        // Generate a random 10-character alphanumeric string
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return "CUST" + sb.toString();
    }

    @GetMapping("/getByMobNum")
    ResponseEntity<CustomerModel> getCustomerByMobileNum(@RequestParam("mobNo") String mobNo) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetMappingTest", mobNo + "", mobNo);

        CustomerAuthModel customerAuth = customerAuthRepository.findByMobileNum(mobNo);
        if (customerAuth == null) {
            throw new RuntimeException("Unable to find customer with mobile number => " + mobNo);
        }
        
        CustomerModel customer = customerRepository.findById(customerAuth.getCustomerId()).orElse(null);
        if (customer == null) {
            throw new RuntimeException("Customer profile not found for mobile number => " + mobNo);
        }

        // Compute and set customer status
        customer.setCustomerStatus(computeCustomerStatus(customer.isAccountActive(), customer.isActiveBorrower(), customer.isVerified()));
        
        ResponseEntity<CustomerModel> customerResponse = processResponse(context, customer);

        return customerResponse;
    }

    @GetMapping("/getCustomer")
    ResponseEntity<List<CustomerDetailResponse>> getCustomer(
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "emailId", required = false) String emailId,
            @RequestParam(value = "mobileNum", required = false) String mobileNum,
            @RequestParam(value = "customerId", required = false) String customerId,
            @RequestParam(value = "status", required = false) CustomerStatus status) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("GetCustomer", 
                String.valueOf(customerId != null ? customerId : (emailId != null ? emailId : (mobileNum != null ? mobileNum : (firstName != null ? firstName : status)))), 
                customerId != null ? customerId : (emailId != null ? emailId : (mobileNum != null ? mobileNum : (firstName != null ? firstName : status))));

        // Check if at least one parameter is provided
        if (customerId == null && (emailId == null || emailId.isEmpty()) && 
            (mobileNum == null || mobileNum.isEmpty()) && (firstName == null || firstName.isEmpty()) && status == null) {
            throw new RentInException("At least one query parameter (firstName, emailId, mobileNum, customerId, or status) must be provided");
        }
        
        // Call business service to get complete customer details
        List<CustomerDetailResponse> customerDetails = customerBusinessService.getCompleteCustomerDetails(
            customerId, 
            (emailId != null && !emailId.isEmpty()) ? emailId : null,
            (mobileNum != null && !mobileNum.isEmpty()) ? mobileNum : null,
            (firstName != null && !firstName.isEmpty()) ? firstName : null,
            status
        );

        return processResponse(context, customerDetails);
    }

    @PostMapping("/register")
    public ResponseEntity<ResultStatus> registerCustomer(@RequestBody CustomerRegistrationRequest request) {
        // Generate customerId if not provided
        if (request.getCustomerId() == null || request.getCustomerId().isEmpty()) {
            request.setCustomerId(generateCustomerId());
        }
        
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext("Customer-Register", 
                request.getMobileNum() != null ? request.getMobileNum() : request.getEmailId(), 
                request);
        RentInMessage message = new RentInMessage(request, null);
        amqpCustomerRegisterSender.sendRentInMessage(serviceRequestContext, message);
        // in case of OTP service is down than also we will have customer mob no in queue.
        ResultStatus resultStatus = new ResultStatus();
        resultStatus.setStatus(Status.SUCCESS);
        return processResponse(serviceRequestContext, resultStatus);
    }
    
    @PostMapping("/login")
    public ResponseEntity<CustomerModel> loginCustomer(@RequestParam("userId") String userId, @RequestParam("password") String password) {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext("Customer-Login", userId, null);
        
        try {
            var customerAuth = customerBusinessService.validateCustomerLogin(userId, password);
            
            // Fetch customer details
            CustomerModel customer = customerRepository.findById(customerAuth.getCustomerId()).orElse(null);
            
            if (customer == null) {
                throw new RentInException("Customer not found");
            }
            
            return processResponse(serviceRequestContext, customer);
        } catch (RentInException e) {
            throw new RentInException("Login failed: " + e.getMessage());
        }
    }

    @GetMapping("/generateOtpForCustomer")
    public ResponseEntity<ResultStatus> generateOtpForCustomer(@RequestParam("identifier") String identifier) {
        //TODO - check identifier is mob no or email id, based on that insert in DB.(using regex)
        ServiceRequestContext serviceRequestContext = createServiceRequestContext("GetOTP", identifier + "", identifier);

       boolean isSuccess = customerBusinessService.generateOTPForCustomerLogin(serviceRequestContext, identifier);
        ResultStatus resultStatus = new ResultStatus();
        resultStatus.setStatus((isSuccess) ? Status.SUCCESS: Status.FAILED);
        return processResponse(serviceRequestContext, resultStatus);
    }

    @GetMapping("/validateOtpForCustomer")
    public ResponseEntity<CustomerModel> validateOtpForCustomer(@RequestParam("identifier") String identifier, @RequestParam("otp") String otp) {
        //TODO - check identifier is mob no or email id, based on that insert in DB.(using regex)
        ServiceRequestContext serviceRequestContext = createServiceRequestContext("ValidateOTP", identifier + "", identifier);
        int otpValue = Integer.parseInt(otp);
        CustomerModel customerModel = customerBusinessService.validateOTPForCustomerLogin(serviceRequestContext, identifier, otpValue);
        if(customerModel!=null){
            return processResponse(serviceRequestContext, customerModel);
        } else {
            throw new RentInException("Otp Verification Failed.");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ResultStatus> updateCustomer(@RequestBody CustomerModel customerModel) {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext("Customer-Update", String.valueOf(customerModel.getCustomerId()), customerModel);

        RentInMessage message = new RentInMessage<>(customerModel, null);
        amqpCustomerUpdateSender.sendRentInMessage(serviceRequestContext, message);
        // in case of OTP service is down than also we will have customer mob no in queue.
        ResultStatus resultStatus = new ResultStatus();
        resultStatus.setStatus(Status.SUCCESS);
        return processResponse(serviceRequestContext, resultStatus);

    }

    @PutMapping("/updateCustomerNameByMobNo")
    public ResponseEntity<CustomerModel> updateCustomerNameByMobileNo(@RequestBody CustomerModel customerModel) {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext("Customer-Name-Update", String.valueOf(customerModel.getCustomerId()), customerModel);

        CustomerModel customerModelRes = customerBusinessService.updateCustomerNameByMobileNo(serviceRequestContext, customerModel);
        if(customerModelRes!=null){
            return processResponse(serviceRequestContext, customerModel);
        } else {
            throw new RentInException("Customer Name not updated.");
        }

    }

    @GetMapping("/getCustomerCount")
    public ResponseEntity<Long> getCustomerCount(@RequestParam("status") CustomerStatus status) {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext("Get-Customer-Count", status.name(), status);

        long count;
        switch (status) {
            case ACTIVE:
                count = customerRepository.countActiveCustomers();
                break;
            case IN_ACTIVE:
                count = customerRepository.countInactiveCustomers();
                break;
            case ON_BOARDED:
                count = customerRepository.countOnboardedCustomers();
                break;
            case VERIFIED:
                count = customerRepository.countVerifiedCustomers();
                break;
            default:
                throw new RentInException("Invalid status: " + status);
        }

        return processResponse(serviceRequestContext, count);
    }

    private CustomerStatus computeCustomerStatus(boolean isAccountActive, boolean isActiveBorrower, boolean isVerified) {
        if (isAccountActive && isActiveBorrower && isVerified) {
            return CustomerStatus.ACTIVE;
        } else if (!isAccountActive && isActiveBorrower && isVerified) {
            return CustomerStatus.IN_ACTIVE;
        } else if (!isAccountActive && isActiveBorrower && !isVerified) {
            return CustomerStatus.ON_BOARDED;
        } else if (!isAccountActive && isActiveBorrower && isVerified) {
            return CustomerStatus.VERIFIED;
        } else {
            // Default or other cases, perhaps ON_BOARDED or something
            return CustomerStatus.ON_BOARDED;
        }
    }
}

