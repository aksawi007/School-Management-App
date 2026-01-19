package org.rentin.controller;

import io.swagger.annotations.Api;
import org.rentin.jpa.model.customer.CustomerFavouriteModel;
import org.rentin.platform.core.annotation.APIController;
import org.rentin.platform.core.exception.RentInException;
import org.rentin.platform.core.restcontroller.ApiRestServiceBinding;
import org.rentin.platform.core.service.ServiceRequestContext;
import org.rentin.service.CustomerFavouriteBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@APIController
@RequestMapping("/customer/favourite")
@Api(tags = "Favourite API")
public class FavouriteController extends ApiRestServiceBinding {

    @Autowired
    CustomerFavouriteBusinessService customerFavBusinessService;

    @GetMapping("/getall")
    public ResponseEntity<List<CustomerFavouriteModel>> getCustomerFavourite(
            @RequestParam("customerId") Integer customerId) {
        try {
            ServiceRequestContext serviceRequestContext = new ServiceRequestContext(
                    "Get-Customer-Favourites",
                    customerId.toString(),
                    customerId
            );

            List<CustomerFavouriteModel> favourites = customerFavBusinessService.findByCustomerId(customerId);
            return processResponse(serviceRequestContext, favourites);
        } catch (Exception e) {
            throw new RentInException(
                "Error fetching customer favourites: " + e.getMessage());
        }
    }

    @PostMapping("/add/{customerId}/{productId}")
    public ResponseEntity<CustomerFavouriteModel> addCustomerFavourite(
            @PathVariable("customerId") Integer customerId,
            @PathVariable("productId") String productId) {
        try {
            ServiceRequestContext serviceRequestContext = new ServiceRequestContext(
                    "Add-Customer-Favourite",
                    customerId + "-" + productId,
                    customerId
            );

            CustomerFavouriteModel savedFavourite = customerFavBusinessService.saveCustomerFavourite(customerId, productId);
            return processResponse(serviceRequestContext, savedFavourite);
        } catch (IllegalArgumentException e) {
            throw new RentInException("Invalid request: " + e.getMessage());
        } catch (Exception e) {
            throw new RentInException(
                "Error adding customer favourite: " + e.getMessage());
        }
    }

    @DeleteMapping("/remove/{customerId}/{productId}")
    public ResponseEntity<Boolean> removeCustomerFavourite(
            @PathVariable("customerId") Integer customerId,
            @PathVariable("productId") String productId) {
        try {
            ServiceRequestContext serviceRequestContext = new ServiceRequestContext(
                    "Remove-Customer-Favourite",
                    customerId + "-" + productId,
                    customerId
            );

            boolean deletedFavourite = customerFavBusinessService.removeCustomerFavourite(customerId, productId);
            return processResponse(serviceRequestContext, deletedFavourite);
        } catch (IllegalArgumentException e) {
            throw new RentInException("Invalid request: " + e.getMessage());
        } catch (Exception e) {
            throw new RentInException(
                "Error removing customer favourite: " + e.getMessage());
        }
    }
}
