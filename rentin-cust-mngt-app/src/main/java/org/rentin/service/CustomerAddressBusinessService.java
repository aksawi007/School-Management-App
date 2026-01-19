package org.rentin.service;

import org.rentin.jpa.model.customer.CustomerAddressModel;
import org.rentin.jpa.model.customer.CustomerModel;
import org.rentin.jpa.repository.customer.CustomerAddressRepository;
import org.rentin.jpa.repository.customer.CustomerRepository;
import org.rentin.platform.core.exception.RentInException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class CustomerAddressBusinessService {

    @Autowired
    private CustomerAddressRepository customerAddressRepository;
    
    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Create a new address for a customer
     * @param address CustomerAddressModel
     * @return Created address
     */
    @Transactional
    public CustomerAddressModel createAddress(CustomerAddressModel address) throws RentInException {
        try {
            // Verify customer exists
            Optional<CustomerModel> customer = customerRepository.findById(address.getCustomerId());
            if (!customer.isPresent()) {
                throw new RentInException("Customer not found with ID: " + address.getCustomerId());
            }
            
            // Set created date
            address.setCreatedDate(new Date());
            address.setUpdatedDate(new Date());
            
            // Set default to true if this is the first address
            long existingAddressCount = customerAddressRepository.countByCustomerId(address.getCustomerId());
            if (existingAddressCount == 0) {
                address.setDefault(true);
            }
            
            // If this address is marked as default, reset other defaults
            if (address.isDefault()) {
                customerAddressRepository.resetDefaultAddresses(address.getCustomerId());
            }
            
            // Set active to true by default
            if (!address.isActive()) {
                address.setActive(true);
            }
            
            // Save address
            CustomerAddressModel savedAddress = customerAddressRepository.save(address);
            
            // If this is the default address, update customer table
            if (savedAddress.isDefault()) {
                updateCustomerDefaultAddress(address.getCustomerId(), savedAddress.getAddressId());
            }
            
            return savedAddress;
        } catch (RentInException e) {
            throw e;
        } catch (Exception e) {
            throw new RentInException("Failed to create address: " + e.getMessage(), e);
        }
    }

    /**
     * Get all addresses for a customer
     * @param customerId Customer ID
     * @return List of addresses
     */
    public List<CustomerAddressModel> getAddressesByCustomerId(long customerId) {
        return customerAddressRepository.findByCustomerId(customerId);
    }

    /**
     * Get all active addresses for a customer
     * @param customerId Customer ID
     * @return List of active addresses
     */
    public List<CustomerAddressModel> getActiveAddresses(String customerId) {
        return customerAddressRepository.findByCustomerIdAndIsActive(customerId, true);
    }

    /**
     * Get default address for a customer
     * @param customerId Customer ID
     * @return Default address
     */
    public CustomerAddressModel getDefaultAddress(long customerId) throws RentInException {
        Optional<CustomerAddressModel> address = customerAddressRepository.findByCustomerIdAndIsDefault(customerId, true);
        if (!address.isPresent()) {
            throw new RentInException("No default address found for customer ID: " + customerId);
        }
        return address.get();
    }

    /**
     * Get address by ID
     * @param addressId Address ID
     * @param customerId Customer ID (for security check)
     * @return Address
     */
    public CustomerAddressModel getAddressById(long addressId, String customerId) throws RentInException {
        Optional<CustomerAddressModel> address = customerAddressRepository.findByAddressIdAndCustomerId(addressId, customerId);
        if (!address.isPresent()) {
            throw new RentInException("Address not found with ID: " + addressId + " for customer: " + customerId);
        }
        return address.get();
    }

    /**
     * Update an existing address
     * @param address Updated address
     * @return Updated address
     */
    @Transactional
    public CustomerAddressModel updateAddress(CustomerAddressModel address) throws RentInException {
        try {
            // Verify address exists and belongs to customer
            Optional<CustomerAddressModel> existing = customerAddressRepository.findByAddressIdAndCustomerId(
                address.getAddressId(), address.getCustomerId());
            
            if (!existing.isPresent()) {
                throw new RentInException("Address not found or doesn't belong to customer");
            }
            
            // Update timestamp
            address.setUpdatedDate(new Date());
            
            // If setting as default, reset other defaults
            if (address.isDefault()) {
                customerAddressRepository.resetDefaultAddresses(address.getCustomerId());
            }
            
            // Save updated address
            CustomerAddressModel updatedAddress = customerAddressRepository.save(address);
            
            // Update customer table if this is now the default
            if (updatedAddress.isDefault()) {
                updateCustomerDefaultAddress(address.getCustomerId(), updatedAddress.getAddressId());
            }
            
            return updatedAddress;
        } catch (RentInException e) {
            throw e;
        } catch (Exception e) {
            throw new RentInException("Failed to update address: " + e.getMessage(), e);
        }
    }

    /**
     * Set an address as default for a customer
     * @param addressId Address ID
     * @param customerId Customer ID
     */
    @Transactional
    public void setAsDefault(long addressId, String customerId) throws RentInException {
        try {
            // Verify address exists and belongs to customer
            Optional<CustomerAddressModel> address = customerAddressRepository.findByAddressIdAndCustomerId(addressId, customerId);
            if (!address.isPresent()) {
                throw new RentInException("Address not found or doesn't belong to customer");
            }
            
            // Reset all defaults for this customer
            customerAddressRepository.resetDefaultAddresses(customerId);
            
            // Set this address as default
            customerAddressRepository.setAsDefault(addressId, customerId);
            
            // Update customer table
            updateCustomerDefaultAddress(customerId, addressId);
        } catch (RentInException e) {
            throw e;
        } catch (Exception e) {
            throw new RentInException("Failed to set default address: " + e.getMessage(), e);
        }
    }

    /**
     * Delete an address
     * @param addressId Address ID
     * @param customerId Customer ID (for security check)
     */
    @Transactional
    public void deleteAddress(long addressId, String customerId) throws RentInException {
        try {
            // Verify address exists and belongs to customer
            Optional<CustomerAddressModel> address = customerAddressRepository.findByAddressIdAndCustomerId(addressId, customerId);
            if (!address.isPresent()) {
                throw new RentInException("Address not found or doesn't belong to customer");
            }
            
            boolean wasDefault = address.get().isDefault();
            
            // Delete the address
            customerAddressRepository.deleteById(addressId);
            
            // If deleted address was default, set another address as default
            if (wasDefault) {
                List<CustomerAddressModel> remainingAddresses = customerAddressRepository.findByCustomerIdAndIsActive(customerId, true);
                if (!remainingAddresses.isEmpty()) {
                    // Set first remaining address as default
                    setAsDefault(remainingAddresses.get(0).getAddressId(), customerId);
                } else {
                    // No remaining addresses, clear default in customer table
                    updateCustomerDefaultAddress(customerId, null);
                }
            }
        } catch (RentInException e) {
            throw e;
        } catch (Exception e) {
            throw new RentInException("Failed to delete address: " + e.getMessage(), e);
        }
    }

    /**
     * Deactivate an address (soft delete)
     * @param addressId Address ID
     * @param customerId Customer ID
     */
    @Transactional
    public void deactivateAddress(long addressId, String customerId) throws RentInException {
        try {
            CustomerAddressModel address = getAddressById(addressId, customerId);
            address.setActive(false);
            address.setUpdatedDate(new Date());
            
            // If this was the default address, set another as default
            if (address.isDefault()) {
                List<CustomerAddressModel> activeAddresses = customerAddressRepository.findByCustomerIdAndIsActive(customerId, true);
                if (!activeAddresses.isEmpty()) {
                    setAsDefault(activeAddresses.get(0).getAddressId(), customerId);
                }
            }
            
            customerAddressRepository.save(address);
        } catch (RentInException e) {
            throw e;
        } catch (Exception e) {
            throw new RentInException("Failed to deactivate address: " + e.getMessage(), e);
        }
    }

    /**
     * Get addresses by type
     * @param customerId Customer ID
     * @param addressType Address type (HOME, OFFICE, OTHER)
     * @return List of addresses
     */
    public List<CustomerAddressModel> getAddressesByType(long customerId, String addressType) {
        return customerAddressRepository.findByCustomerIdAndAddressType(customerId, addressType);
    }

    /**
     * Count total addresses for a customer
     * @param customerId Customer ID
     * @return Count of addresses
     */
    public long countAddresses(String customerId) {
        return customerAddressRepository.countByCustomerId(customerId);
    }

    /**
     * Private helper method to update default address ID in customer table
     * @param customerId Customer ID
     * @param addressId Address ID (can be null)
     */
    private void updateCustomerDefaultAddress(String customerId, Long addressId) throws RentInException {
        try {
            Optional<CustomerModel> customerOpt = customerRepository.findById(customerId);
            if (customerOpt.isPresent()) {
                CustomerModel customer = customerOpt.get();
                customer.setDefaultAddressId(addressId);
                customerRepository.save(customer);
            }
        } catch (Exception e) {
            throw new RentInException("Failed to update customer default address: " + e.getMessage(), e);
        }
    }
}
