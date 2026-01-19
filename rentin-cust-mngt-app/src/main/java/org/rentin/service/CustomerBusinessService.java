package org.rentin.service;

import org.rentin.jpa.model.customer.CustomerAuthModel;
import org.rentin.jpa.model.customer.CustomerModel;
import org.rentin.jpa.model.otp.OTPModel;
import org.rentin.jpa.repository.customer.CustomerAuthRepository;
import org.rentin.jpa.repository.customer.CustomerRepository;
import org.rentin.jpa.repository.otp.OTPRepository;
import org.rentin.platform.common.datamodel.app.CustomerDetailResponse;
import org.rentin.platform.common.datamodel.app.CustomerRegistrationRequest;
import org.rentin.platform.common.datamodel.dto.CustomerStatus;
import org.rentin.platform.core.exception.RentInException;
import org.rentin.platform.core.service.ServiceRequestContext;
import org.rentin.platform.core.utils.RentInApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CustomerBusinessService {

    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    OTPRepository otpRepository;
    @Autowired
    CustomerAuthRepository customerAuthRepository;

    private String generateCustomerId() {
        // Generate a random 10-character alphanumeric string
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return "CUST" + sb.toString();
    }

    public CustomerModel registerCustomer(ServiceRequestContext context, CustomerRegistrationRequest request) throws RentInException {
        // Validate required fields
        if (request.getFirstName() == null || request.getFirstName().isEmpty()) {
            throw new RentInException("First Name is mandatory for Customer registration");
        }
        if (request.getMobileNum() == null || request.getMobileNum().isEmpty()) {
            throw new RentInException("Mobile Number is mandatory for Customer registration");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new RentInException("Password is mandatory for Customer registration");
        }
        
        // Check if mobile number or email already exists
        CustomerAuthModel existingByMobile = customerAuthRepository.findByMobileNum(request.getMobileNum());
        if (existingByMobile != null) {
            throw new RentInException("Mobile number already registered");
        }
        
        if (request.getEmailId() != null && !request.getEmailId().isEmpty()) {
            CustomerAuthModel existingByEmail = customerAuthRepository.findByEmailId(request.getEmailId());
            if (existingByEmail != null) {
                throw new RentInException("Email already registered");
            }
        }
        
        if (request.getUserId() != null && !request.getUserId().isEmpty()) {
            CustomerAuthModel existingByUserId = customerAuthRepository.findByUserId(request.getUserId());
            if (existingByUserId != null) {
                throw new RentInException("User ID already taken");
            }
        }
        
        // Create customer profile
        CustomerModel customerModel = new CustomerModel();
        customerModel.setCustomerId(request.getCustomerId() != null ? request.getCustomerId() : generateCustomerId());
        customerModel.setFirstName(request.getFirstName());
        customerModel.setMiddleName(request.getMiddleName());
        customerModel.setLastName(request.getLastName());
        customerModel.setGender(request.getGender());
        customerModel.setTitle(request.getTitle());
        customerModel.setProfilePic(request.getProfilePic());
        customerModel.setAboutCustomer(request.getAboutCustomer());
        customerModel.setRegistoredOn(RentInApiUtils.getCurrentDateStr());
        customerModel.setAccountActive(request.isAccountActive());
        customerModel.setActiveLender(request.isActiveLender());
        customerModel.setActiveBorrower(request.isActiveBorrower());
        // Always set referralApplicable to true
        customerModel.setReferralApplicable(true);
        // Set referralCode from request
        customerModel.setReferralCode(request.getReferralCode());
        
        // Save customer profile
        CustomerModel createdCustomer = customerRepository.save(customerModel);
        
        // Create customer auth record
        CustomerAuthModel customerAuth = new CustomerAuthModel();
        customerAuth.setCustomerId(createdCustomer.getCustomerId());
        customerAuth.setUserId(request.getUserId() != null ? request.getUserId() : request.getMobileNum());
        customerAuth.setEmailId(request.getEmailId());
        customerAuth.setMobileNum(request.getMobileNum());
        customerAuth.setPassword(request.getPassword()); // TODO: Hash the password before storing
        
        // Save customer auth
        customerAuthRepository.save(customerAuth);
        
        return createdCustomer;
    }

    public int updateCustomer(ServiceRequestContext context, CustomerModel customerModel) throws RentInException {
        if (customerModel.getCustomerId() == null || customerModel.getCustomerId().trim().isEmpty()) {
            throw new RentInException("Customer ID is mandatory for Customer update");
        }
        
        int numOfRowsUpdated = customerRepository.updateCustomer(customerModel);
        return numOfRowsUpdated;
    }

    public boolean generateOTPForCustomerLogin(ServiceRequestContext serviceRequestContext, String identifier) {
        int otp = RentInApiUtils.generateRandomNo_6_digits();
        OTPModel otpModel = new OTPModel();
        otpModel.setOtpNo(otp);
        if (identifier.length() == 10 && !identifier.contains("@"))
            otpModel.setMobileNo(identifier);
        else if (identifier.contains("@") && identifier.contains(".")) {
            otpModel.setEmail_id(identifier);
        }
        otpModel.setTimeOtpExpiry(RentInApiUtils.addMinutesInCurrentTime(5));
        OTPModel otp_repo = otpRepository.save(otpModel);
        return (otp_repo.getOtpId() != 0L) ? true : false;
    }

    public CustomerModel validateOTPForCustomerLogin(ServiceRequestContext serviceRequestContext, String identifier, int otp) {
        OTPModel byOtpNo = otpRepository.findByOtpNo(otp);
        CustomerModel customerModel = null;
        if (byOtpNo == null) {
            return customerModel;
        }
        
        // TODO: Update OTP validation to work with customer_auth table
        // For now, lookup by identifier in customer_auth table to get customerId
        CustomerAuthModel customerAuth = null;
        if (identifier.length() == 10 && !identifier.contains("@")) {
            // Mobile number
            customerAuth = customerAuthRepository.findByMobileNum(identifier);
        } else if (identifier.contains("@") && identifier.contains(".")) {
            // Email
            customerAuth = customerAuthRepository.findByEmailId(identifier);
        }
        
        if (customerAuth != null && new Date().compareTo(byOtpNo.getTimeOtpExpiry()) < 0) {
            customerModel = customerRepository.findById(customerAuth.getCustomerId()).orElse(null);
        }
        
        return customerModel;
    }

    public CustomerModel updateCustomerNameByMobileNo(ServiceRequestContext serviceRequestContext, CustomerModel customerModel) {
        if (customerModel.getCustomerId() == null || customerModel.getCustomerId().trim().isEmpty()) {
            return null;
        }
        
        CustomerModel existingCustomer = customerRepository.findById(customerModel.getCustomerId()).orElse(null);
        if (existingCustomer == null) {
            return null;
        }
        
        if (customerModel.getFirstName() != null)
            existingCustomer.setFirstName(customerModel.getFirstName());
        if (customerModel.getMiddleName() != null)
            existingCustomer.setMiddleName(customerModel.getMiddleName());
        if (customerModel.getLastName() != null)
            existingCustomer.setLastName(customerModel.getLastName());

        int i = customerRepository.updateCustomer(customerModel);
        if (i == 1) {
            return existingCustomer;
        } else {
           return null;
        }
    }

    public CustomerAuthModel validateCustomerLogin(String userId, String password) throws RentInException {
        if (userId == null || userId.isEmpty()) {
            throw new RentInException("User ID is required for login");
        }
        if (password == null || password.isEmpty()) {
            throw new RentInException("Password is required for login");
        }

        CustomerAuthModel customerAuth = null;

        // Try to find by userId first
        customerAuth = customerAuthRepository.findByUserIdAndPassword(userId, password);
        
        // If not found and looks like email, try email field
        if (customerAuth == null && userId.contains("@")) {
            customerAuth = customerAuthRepository.findByEmailIdAndPassword(userId, password);
        }
        
        // If not found and looks like phone number, try mobile number field
        if (customerAuth == null && userId.matches("\\d+")) {
            customerAuth = customerAuthRepository.findByMobileNumAndPassword(userId, password);
        }

        if (customerAuth == null) {
            throw new RentInException("Invalid credentials");
        }

        return customerAuth;
    }
    
    /**
     * Get complete customer details using separate queries for customer and customer_auth tables
     * @param customerId Customer ID (optional)
     * @param emailId Email ID (optional)
     * @param mobileNum Mobile Number (optional)
     * @param firstName First Name (optional)
     * @return List of complete customer details
     */
    public List<CustomerDetailResponse> getCompleteCustomerDetails(String customerId, String emailId, String mobileNum, String firstName, CustomerStatus status) {
        List<CustomerDetailResponse> customerDetails = new ArrayList<>();
        
        // Count how many parameters are provided
        int paramCount = 0;
        if (customerId != null && !customerId.trim().isEmpty()) paramCount++;
        if (emailId != null && !emailId.isEmpty()) paramCount++;
        if (mobileNum != null && !mobileNum.isEmpty()) paramCount++;
        if (firstName != null && !firstName.isEmpty()) paramCount++;
        if (status != null) paramCount++;
        
        // If multiple parameters provided, use AND logic
        if (paramCount > 1) {
            customerDetails = getCustomersByMultipleParams(customerId, emailId, mobileNum, firstName, status);
        }
        // Single parameter - use specific methods
        else if (customerId != null && !customerId.trim().isEmpty()) {
            customerDetails = getCustomerByCustomerId(customerId);
        } else if (emailId != null && !emailId.isEmpty()) {
            customerDetails = getCustomersByEmailId(emailId);
        } else if (mobileNum != null && !mobileNum.isEmpty()) {
            customerDetails = getCustomersByMobileNum(mobileNum);
        } else if (firstName != null && !firstName.isEmpty()) {
            customerDetails = getCustomersByFirstName(firstName);
        } else if (status != null) {
            customerDetails = getCustomersByStatus(status);
        }
        
        return customerDetails;
    }
    
    /**
     * Get customer by customer ID
     */
    private List<CustomerDetailResponse> getCustomerByCustomerId(String customerId) {
        List<CustomerDetailResponse> result = new ArrayList<>();
        
        customerRepository.findById(customerId).ifPresent(customer -> {
            // Find corresponding auth record
            List<CustomerAuthModel> authRecords = customerAuthRepository.findByCustomerIdIn(
                java.util.Collections.singletonList(customer.getCustomerId()));
            CustomerAuthModel auth = authRecords.isEmpty() ? null : authRecords.get(0);
            
            result.add(mapToCustomerDetailResponse(customer, auth));
        });
        
        return result;
    }
    
    /**
     * Get customers by email ID
     */
    private List<CustomerDetailResponse> getCustomersByEmailId(String emailId) {
        List<CustomerDetailResponse> result = new ArrayList<>();
        
        // Search in customer_auth table
        List<CustomerAuthModel> authRecords = customerAuthRepository.findAllByEmailId(emailId);
        if (!authRecords.isEmpty()) {
            // Get customer IDs
            List<String> customerIds = new ArrayList<>();
            for (CustomerAuthModel auth : authRecords) {
                customerIds.add(auth.getCustomerId());
            }
            
            // Fetch customers
            List<CustomerModel> customers = customerRepository.findByCustomerIdIn(customerIds);
            
            // Merge data
            for (CustomerModel customer : customers) {
                CustomerAuthModel matchingAuth = findAuthByCustomerId(authRecords, customer.getCustomerId());
                result.add(mapToCustomerDetailResponse(customer, matchingAuth));
            }
        }
        
        return result;
    }
    
    /**
     * Get customers by mobile number
     */
    private List<CustomerDetailResponse> getCustomersByMobileNum(String mobileNum) {
        List<CustomerDetailResponse> result = new ArrayList<>();
        
        // Search in customer_auth table
        List<CustomerAuthModel> authRecords = customerAuthRepository.findAllByMobileNum(mobileNum);
        if (!authRecords.isEmpty()) {
            // Get customer IDs
            List<String> customerIds = new ArrayList<>();
            for (CustomerAuthModel auth : authRecords) {
                customerIds.add(auth.getCustomerId());
            }
            
            // Fetch customers
            List<CustomerModel> customers = customerRepository.findByCustomerIdIn(customerIds);
            
            // Merge data
            for (CustomerModel customer : customers) {
                CustomerAuthModel matchingAuth = findAuthByCustomerId(authRecords, customer.getCustomerId());
                result.add(mapToCustomerDetailResponse(customer, matchingAuth));
            }
        }
        
        return result;
    }
    
    /**
     * Get customers by first name
     */
    private List<CustomerDetailResponse> getCustomersByFirstName(String firstName) {
        List<CustomerDetailResponse> result = new ArrayList<>();
        
        // Search in customer table
        List<CustomerModel> customers = customerRepository.findByFirstName(firstName);
        if (!customers.isEmpty()) {
            // Get customer IDs
            List<String> customerIds = new ArrayList<>();
            for (CustomerModel customer : customers) {
                customerIds.add(customer.getCustomerId());
            }
            
            // Fetch auth records
            List<CustomerAuthModel> authRecords = customerAuthRepository.findByCustomerIdIn(customerIds);
            
            // Merge data
            for (CustomerModel customer : customers) {
                CustomerAuthModel matchingAuth = findAuthByCustomerId(authRecords, customer.getCustomerId());
                result.add(mapToCustomerDetailResponse(customer, matchingAuth));
            }
        }
        
        return result;
    }
    
    /**
     * Get customers by status
     */
    private List<CustomerDetailResponse> getCustomersByStatus(CustomerStatus status) {
        List<CustomerModel> customers;
        switch (status) {
            case ACTIVE:
                customers = customerRepository.findActiveCustomers();
                break;
            case IN_ACTIVE:
                customers = customerRepository.findInactiveCustomers();
                break;
            case ON_BOARDED:
                customers = customerRepository.findOnboardedCustomers();
                break;
            case VERIFIED:
                customers = customerRepository.findVerifiedCustomers();
                break;
            default:
                throw new RentInException("Invalid status: " + status);
        }
        
        List<CustomerDetailResponse> result = new ArrayList<>();
        for (CustomerModel customer : customers) {
            // Find corresponding auth record
            List<CustomerAuthModel> authRecords = customerAuthRepository.findByCustomerIdIn(
                java.util.Collections.singletonList(customer.getCustomerId()));
            CustomerAuthModel auth = authRecords.isEmpty() ? null : authRecords.get(0);
            
            result.add(mapToCustomerDetailResponse(customer, auth));
        }
        
        return result;
    }
    
    /**
     * Get customers by multiple parameters (AND condition)
     */
    private List<CustomerDetailResponse> getCustomersByMultipleParams(String customerId, String emailId, String mobileNum, String firstName, CustomerStatus status) {
        List<CustomerDetailResponse> result = new ArrayList<>();
        List<String> customerIdsFromAuth = null;
        List<String> customerIdsFromCustomer = null;
        
        // Get customer IDs from auth table if emailId or mobileNum provided
        if ((emailId != null && !emailId.isEmpty()) || (mobileNum != null && !mobileNum.isEmpty())) {
            customerIdsFromAuth = new ArrayList<>();
            
            if (emailId != null && !emailId.isEmpty()) {
                List<CustomerAuthModel> authByEmail = customerAuthRepository.findAllByEmailId(emailId);
                for (CustomerAuthModel auth : authByEmail) {
                    if (!customerIdsFromAuth.contains(auth.getCustomerId())) {
                        customerIdsFromAuth.add(auth.getCustomerId());
                    }
                }
            }
            
            if (mobileNum != null && !mobileNum.isEmpty()) {
                List<CustomerAuthModel> authByMobile = customerAuthRepository.findAllByMobileNum(mobileNum);
                List<String> mobileCustomerIds = new ArrayList<>();
                for (CustomerAuthModel auth : authByMobile) {
                    mobileCustomerIds.add(auth.getCustomerId());
                }
                
                // AND condition - intersect with previous results
                if (emailId != null && !emailId.isEmpty()) {
                    customerIdsFromAuth.retainAll(mobileCustomerIds);
                } else {
                    customerIdsFromAuth.addAll(mobileCustomerIds);
                }
            }
        }
        
        // Get customer IDs from customer table if customerId or firstName provided
        if ((customerId != null && !customerId.trim().isEmpty()) || (firstName != null && !firstName.isEmpty())) {
            customerIdsFromCustomer = new ArrayList<>();
            
            if (customerId != null && !customerId.trim().isEmpty()) {
                if (customerRepository.findById(customerId).isPresent()) {
                    customerIdsFromCustomer.add(customerId);
                }
            }
            
            if (firstName != null && !firstName.isEmpty()) {
                List<CustomerModel> customersByName = customerRepository.findByFirstName(firstName);
                List<String> nameCustomerIds = new ArrayList<>();
                for (CustomerModel customer : customersByName) {
                    nameCustomerIds.add(customer.getCustomerId());
                }
                
                // AND condition - intersect with previous results
                if (customerId != null && !customerId.trim().isEmpty()) {
                    customerIdsFromCustomer.retainAll(nameCustomerIds);
                } else {
                    customerIdsFromCustomer.addAll(nameCustomerIds);
                }
            }
        }
        
        // Intersect results from auth and customer tables (AND condition)
        List<String> finalCustomerIds = new ArrayList<>();
        if (customerIdsFromAuth != null && customerIdsFromCustomer != null) {
            // Both auth and customer filters - intersection
            for (String id : customerIdsFromAuth) {
                if (customerIdsFromCustomer.contains(id)) {
                    finalCustomerIds.add(id);
                }
            }
        } else if (customerIdsFromAuth != null) {
            finalCustomerIds = customerIdsFromAuth;
        } else if (customerIdsFromCustomer != null) {
            finalCustomerIds = customerIdsFromCustomer;
        }
        
        // Fetch final results
        if (!finalCustomerIds.isEmpty()) {
            List<CustomerModel> customers = customerRepository.findByCustomerIdIn(finalCustomerIds);
            List<CustomerAuthModel> authRecords = customerAuthRepository.findByCustomerIdIn(finalCustomerIds);
            
            for (CustomerModel customer : customers) {
                CustomerAuthModel matchingAuth = findAuthByCustomerId(authRecords, customer.getCustomerId());
                result.add(mapToCustomerDetailResponse(customer, matchingAuth));
            }
        }
        
        // Filter by status if provided
        if (status != null) {
            result = result.stream()
                .filter(response -> status.equals(computeCustomerStatus(response.isAccountActive(), response.isActiveBorrower(), response.isVerified())))
                .collect(java.util.stream.Collectors.toList());
        }
        
        return result;
    }
    
    /**
     * Helper method to find auth record by customer ID
     */
    private CustomerAuthModel findAuthByCustomerId(List<CustomerAuthModel> authRecords, String customerId) {
        for (CustomerAuthModel auth : authRecords) {
            if (auth.getCustomerId().equals(customerId)) {
                return auth;
            }
        }
        return null;
    }
    
    /**
     * Helper method to map CustomerModel and CustomerAuthModel to CustomerDetailResponse
     */
    private CustomerDetailResponse mapToCustomerDetailResponse(CustomerModel customer, CustomerAuthModel auth) {
        CustomerDetailResponse detail = new CustomerDetailResponse();
        
        // Map customer profile fields
        detail.setCustomerId(customer.getCustomerId());
        detail.setTitle(customer.getTitle());
        detail.setFirstName(customer.getFirstName());
        detail.setMiddleName(customer.getMiddleName());
        detail.setLastName(customer.getLastName());
        detail.setGender(customer.getGender());
        detail.setProfilePic(customer.getProfilePic());
        detail.setAboutCustomer(customer.getAboutCustomer());
        detail.setActiveLender(customer.isActiveLender());
        detail.setActiveBorrower(customer.isActiveBorrower());
        detail.setRegisteredOn(customer.getRegistoredOn());
        detail.setAccountActive(customer.isAccountActive());
        detail.setVerified(customer.isVerified());
        
        // Compute and set customer status
        detail.setCustomerStatus(computeCustomerStatus(customer.isAccountActive(), customer.isActiveBorrower(), customer.isVerified()));
        
        // Map customer auth fields (if available)
        if (auth != null) {
            detail.setUserId(auth.getUserId());
            detail.setEmailId(auth.getEmailId());
            detail.setMobileNum(auth.getMobileNum());
        }
        
        return detail;
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
