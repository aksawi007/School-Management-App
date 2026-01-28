package org.sma.admin.core.app.controller;

import io.swagger.annotations.Api;
import org.sma.admin.core.app.model.request.SectionMasterRequest;
import org.sma.admin.core.app.model.response.SectionMasterResponse;
import org.sma.admin.core.app.service.SectionMasterBusinessService;
import org.sma.platform.core.annotation.APIController;
import org.sma.platform.core.exception.SmaException;
import org.sma.platform.core.restcontroller.ApiRestServiceBinding;
import org.sma.platform.core.service.ServiceRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * SectionMaster Controller
 * Handles section master data management
 */
@APIController
@RequestMapping("/section")
@Api(tags = "Section Master API")
public class SectionMasterController extends ApiRestServiceBinding {

    @Autowired
    SectionMasterBusinessService sectionMasterBusinessService;

    @PostMapping("/create")
    ResponseEntity<SectionMasterResponse> createSection(@RequestBody SectionMasterRequest request) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("CreateSection", 
            request.getSectionName(), request.getSectionName());

        try {
            SectionMasterResponse response = sectionMasterBusinessService.createSection(context, request);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to create section: " + e.getMessage(), e);
        }
    }

    @GetMapping("/get")
    ResponseEntity<SectionMasterResponse> getSection(@RequestParam("sectionId") String sectionId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetSection", 
            sectionId, sectionId);

        try {
            SectionMasterResponse response = sectionMasterBusinessService.getSection(context, Long.parseLong(sectionId));
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to get section: " + e.getMessage(), e);
        }
    }

    @GetMapping("/school/{schoolId}/class/{classId}")
    ResponseEntity<List<SectionMasterResponse>> getSectionsByClass(@PathVariable("schoolId") Long schoolId,
                                                                    @PathVariable("classId") String classId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetSectionsByClass", 
            classId, classId);

        try {
            List<SectionMasterResponse> response = sectionMasterBusinessService.getSectionsByClass(context, schoolId, Long.parseLong(classId));
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to get sections: " + e.getMessage(), e);
        }
    }

    @PutMapping("/update")
    ResponseEntity<SectionMasterResponse> updateSection(@RequestParam("sectionId") String sectionId,
                                                        @RequestBody SectionMasterRequest request) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("UpdateSection", 
            sectionId, sectionId);

        try {
            SectionMasterResponse response = sectionMasterBusinessService.updateSection(context, Long.parseLong(sectionId), request);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to update section: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteSection(@RequestParam("sectionId") String sectionId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("DeleteSection", 
            sectionId, sectionId);

        try {
            sectionMasterBusinessService.deleteSection(context, Long.parseLong(sectionId));
            return ResponseEntity.ok().build();
        } catch (SmaException e) {
            throw new RuntimeException("Unable to delete section: " + e.getMessage(), e);
        }
    }
}
