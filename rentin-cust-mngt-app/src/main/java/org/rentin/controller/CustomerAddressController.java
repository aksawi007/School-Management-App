package org.rentin.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.rentin.jpa.model.customer.CustomerAddressModel;
import org.rentin.platform.common.datamodel.app.ResultStatus;
import org.rentin.platform.common.datamodel.app.Status;
import org.rentin.platform.core.annotation.APIController;
import org.rentin.platform.core.exception.RentInException;
import org.rentin.platform.core.restcontroller.ApiRestServiceBinding;
import org.rentin.platform.core.service.ServiceRequestContext;
import org.rentin.service.CustomerAddressBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@APIController
@RequestMapping("/customer/address")
@Api(tags = {"Customer Address Management"}, description = "Manage multiple addresses for customers with default address support")
public class CustomerAddressController extends ApiRestServiceBinding {

    @Autowired
    private CustomerAddressBusinessService addressBusinessService;

    @PostMapping("/create")
    @ApiOperation(
        value = "Create a new address for a customer",
        notes = "Creates a new address. If this is the first address, it's automatically set as default. " +
                "If marked as default, all other addresses for the customer are unmarked as default. " +
                "The customer's default_address_id is automatically updated.",
        response = CustomerAddressModel.class
    )
    public ResponseEntity<CustomerAddressModel> createAddress(
            @ApiParam(value = "Address details including customer ID and address information", required = true)
            @RequestBody CustomerAddressModel address) {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext("CustomerAddress-Create",
            address.getCustomerId() + "", address);
        
        try {
            CustomerAddressModel createdAddress = addressBusinessService.createAddress(address);
            return processResponse(serviceRequestContext, createdAddress);
        } catch (RentInException e) {
            return processResponse(serviceRequestContext, null);
        }
    }

    @GetMapping("/getByCustomer/{customerId}")
    @ApiOperation(
        value = "Get all addresses for a customer",
        notes = "Retrieves all addresses (active and inactive) for a specific customer",
        response = CustomerAddressModel.class,
        responseContainer = "List"
    )
    public ResponseEntity<List<CustomerAddressModel>> getAddressesByCustomer(
            @ApiParam(value = "Customer ID", required = true, example = "1")
            @PathVariable long customerId) {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext("CustomerAddress-GetByCustomer",
            customerId + "", null);
        
        List<CustomerAddressModel> addresses = addressBusinessService.getAddressesByCustomerId(customerId);
        return processResponse(serviceRequestContext, addresses);
    }

    @GetMapping("/getActive/{customerId}")
    @ApiOperation(
        value = "Get all active addresses for a customer",
        notes = "Retrieves only active addresses for a specific customer",
        response = CustomerAddressModel.class,
        responseContainer = "List"
    )
    public ResponseEntity<List<CustomerAddressModel>> getActiveAddresses(
            @ApiParam(value = "Customer ID", required = true, example = "1")
            @PathVariable String customerId) {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext("CustomerAddress-GetActive",
            customerId + "", null);
        
        List<CustomerAddressModel> addresses = addressBusinessService.getActiveAddresses(customerId);
        return processResponse(serviceRequestContext, addresses);
    }

    @GetMapping("/getDefault/{customerId}")
    @ApiOperation(
        value = "Get default address for a customer",
        notes = "Retrieves the default address for a customer. Returns error if no default address is set.",
        response = CustomerAddressModel.class
    )
    public ResponseEntity<CustomerAddressModel> getDefaultAddress(
            @ApiParam(value = "Customer ID", required = true, example = "1")
            @PathVariable long customerId) {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext("CustomerAddress-GetDefault",
            customerId + "", null);
        
        try {
            CustomerAddressModel address = addressBusinessService.getDefaultAddress(customerId);
            return processResponse(serviceRequestContext, address);
        } catch (RentInException e) {
            return processResponse(serviceRequestContext, null);
        }
    }

    @GetMapping("/get/{addressId}")
    @ApiOperation(
        value = "Get address by ID",
        notes = "Retrieves a specific address by ID. Requires customer ID for security verification.",
        response = CustomerAddressModel.class
    )
    public ResponseEntity<CustomerAddressModel> getAddressById(
            @ApiParam(value = "Address ID", required = true, example = "1")
            @PathVariable long addressId,
            @ApiParam(value = "Customer ID for security check", required = true, example = "1")
            @RequestParam String customerId) {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext("CustomerAddress-GetById",
            addressId + "", null);
        
        try {
            CustomerAddressModel address = addressBusinessService.getAddressById(addressId, customerId);
            return processResponse(serviceRequestContext, address);
        } catch (RentInException e) {
            return processResponse(serviceRequestContext, null);
        }
    }

    @PutMapping("/update")
    @ApiOperation(
        value = "Update an existing address",
        notes = "Updates address details. If marked as default, all other addresses are unmarked. " +
                "Customer's default_address_id is updated accordingly.",
        response = CustomerAddressModel.class
    )
    public ResponseEntity<CustomerAddressModel> updateAddress(
            @ApiParam(value = "Updated address details with address ID", required = true)
            @RequestBody CustomerAddressModel address) {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext("CustomerAddress-Update",
            address.getAddressId() + "", address);
        
        try {
            CustomerAddressModel updatedAddress = addressBusinessService.updateAddress(address);
            return processResponse(serviceRequestContext, updatedAddress);
        } catch (RentInException e) {
            return processResponse(serviceRequestContext, null);
        }
    }

    @PutMapping("/setDefault/{addressId}")
    @ApiOperation(
        value = "Set an address as default",
        notes = "Marks the specified address as default for the customer. All other addresses are unmarked. " +
                "Updates customer's default_address_id in the customer table.",
        response = ResultStatus.class
    )
    public ResponseEntity<ResultStatus> setAsDefault(
            @ApiParam(value = "Address ID to set as default", required = true, example = "1")
            @PathVariable long addressId,
            @ApiParam(value = "Customer ID", required = true, example = "1")
            @RequestParam String customerId) {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext("CustomerAddress-SetDefault",
            addressId + "", null);
        
        try {
            addressBusinessService.setAsDefault(addressId, customerId);
            ResultStatus resultStatus = new ResultStatus();
            resultStatus.setStatus(Status.SUCCESS);
            resultStatus.setErrorMessage("Address set as default successfully");
            return processResponse(serviceRequestContext, resultStatus);
        } catch (RentInException e) {
            ResultStatus resultStatus = new ResultStatus();
            resultStatus.setStatus(Status.FAILED);
            resultStatus.setErrorMessage(e.getMessage());
            return processResponse(serviceRequestContext, resultStatus);
        }
    }

    @DeleteMapping("/delete/{addressId}")
    @ApiOperation(
        value = "Delete an address",
        notes = "Permanently deletes an address. If the deleted address was default, " +
                "the first remaining active address is automatically set as default. " +
                "If no addresses remain, customer's default_address_id is set to null.",
        response = ResultStatus.class
    )
    public ResponseEntity<ResultStatus> deleteAddress(
            @ApiParam(value = "Address ID to delete", required = true, example = "1")
            @PathVariable long addressId,
            @ApiParam(value = "Customer ID for security check", required = true, example = "1")
            @RequestParam String customerId) {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext("CustomerAddress-Delete",
            addressId + "", null);
        
        try {
            addressBusinessService.deleteAddress(addressId, customerId);
            ResultStatus resultStatus = new ResultStatus();
            resultStatus.setStatus(Status.SUCCESS);
            resultStatus.setErrorMessage("Address deleted successfully");
            return processResponse(serviceRequestContext, resultStatus);
        } catch (RentInException e) {
            ResultStatus resultStatus = new ResultStatus();
            resultStatus.setStatus(Status.FAILED);
            resultStatus.setErrorMessage(e.getMessage());
            return processResponse(serviceRequestContext, resultStatus);
        }
    }

    @PutMapping("/deactivate/{addressId}")
    @ApiOperation(
        value = "Deactivate an address (soft delete)",
        notes = "Marks an address as inactive without deleting it. " +
                "If this was the default address, another active address is automatically set as default.",
        response = ResultStatus.class
    )
    public ResponseEntity<ResultStatus> deactivateAddress(
            @ApiParam(value = "Address ID to deactivate", required = true, example = "1")
            @PathVariable long addressId,
            @ApiParam(value = "Customer ID for security check", required = true, example = "1")
            @RequestParam String customerId) {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext("CustomerAddress-Deactivate",
            addressId + "", null);
        
        try {
            addressBusinessService.deactivateAddress(addressId, customerId);
            ResultStatus resultStatus = new ResultStatus();
            resultStatus.setStatus(Status.SUCCESS);
            resultStatus.setErrorMessage("Address deactivated successfully");
            return processResponse(serviceRequestContext, resultStatus);
        } catch (RentInException e) {
            ResultStatus resultStatus = new ResultStatus();
            resultStatus.setStatus(Status.FAILED);
            resultStatus.setErrorMessage(e.getMessage());
            return processResponse(serviceRequestContext, resultStatus);
        }
    }

    @GetMapping("/getByType/{customerId}/{addressType}")
    @ApiOperation(
        value = "Get addresses by type for a customer",
        notes = "Retrieves addresses filtered by type (HOME, OFFICE, OTHER) for a specific customer",
        response = CustomerAddressModel.class,
        responseContainer = "List"
    )
    public ResponseEntity<List<CustomerAddressModel>> getAddressesByType(
            @ApiParam(value = "Customer ID", required = true, example = "1")
            @PathVariable long customerId,
            @ApiParam(value = "Address type (HOME, OFFICE, OTHER)", required = true, example = "HOME")
            @PathVariable String addressType) {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext("CustomerAddress-GetByType",
            customerId + "", null);
        
        List<CustomerAddressModel> addresses = addressBusinessService.getAddressesByType(customerId, addressType);
        return processResponse(serviceRequestContext, addresses);
    }

    @GetMapping("/count/{customerId}")
    @ApiOperation(
        value = "Get total address count for a customer",
        notes = "Returns the total number of addresses (active and inactive) for a customer",
        response = Long.class
    )
    public ResponseEntity<Long> getAddressCount(
            @ApiParam(value = "Customer ID", required = true, example = "1")
            @PathVariable String customerId) {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext("CustomerAddress-Count",
            customerId + "", null);
        
        long count = addressBusinessService.countAddresses(customerId);
        return processResponse(serviceRequestContext, count);
    }
}
