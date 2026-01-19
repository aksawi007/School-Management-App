package org.rentin.service;

import org.rentin.jpa.model.customer.CustomerFavouriteModel;
import org.rentin.jpa.repository.customer.CustomerFavouriteRepository;
import org.rentin.jpa.repository.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerFavouriteBusinessService {

    @Autowired
    CustomerFavouriteRepository customerFavouriteRepository;


    public List<CustomerFavouriteModel> findByCustomerId(Integer customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        return customerFavouriteRepository.findByCustomerId(customerId);
    }

    /**
     * Save a customer favourite (add product to favourites)
     * @param customerId Customer ID
     * @param productId Product ID
     * @return Saved CustomerFavouriteModel
     * @throws IllegalArgumentException if customerId or productId is null/empty
     */
    public CustomerFavouriteModel saveCustomerFavourite(Integer customerId, String productId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }

        // Check if favourite already exists
        if (customerFavouriteRepository.existsByCustomerIdAndProductId(customerId, productId)) {
            throw new IllegalArgumentException("Product is already in favourites for this customer");
        }

        // Create and save new favourite
        CustomerFavouriteModel favourite = new CustomerFavouriteModel();
        favourite.setCustomerId(customerId);
        favourite.setProductId(productId);

        return customerFavouriteRepository.save(favourite);
    }

    /**
     * Remove a customer favourite (remove product from favourites)
     * @param customerId Customer ID
     * @param productId Product ID
     * @return The deleted CustomerFavouriteModel
     * @throws IllegalArgumentException if customerId or productId is null/empty or favorite doesn't exist
     */
    public boolean removeCustomerFavourite(Integer customerId, String productId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }

        // Check if favourite exists
        if (!customerFavouriteRepository.existsByCustomerIdAndProductId(customerId, productId)) {
            throw new IllegalArgumentException("Product is not in favourites for this customer");
        }

        // Delete the favourite
        customerFavouriteRepository.deleteByCustomerIdAndProductId(customerId, productId);

        return true;
    }

    /**
     * Check if a product is in customer's favourites
     * @param customerId Customer ID
     * @param productId Product ID
     * @return true if product is in favourites, false otherwise
     */
    public boolean isFavourite(Integer customerId, String productId) {
        if (customerId == null || productId == null || productId.trim().isEmpty()) {
            return false;
        }
        return customerFavouriteRepository.existsByCustomerIdAndProductId(customerId, productId);
    }
}
