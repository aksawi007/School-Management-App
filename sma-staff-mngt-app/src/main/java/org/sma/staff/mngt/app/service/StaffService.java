package org.sma.staff.mngt.app.service;

import org.sma.jpa.model.master.DepartmentMaster;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.staff.DepartmentStaffMapping;
import org.sma.jpa.model.staff.Staff;
import org.sma.jpa.repository.master.DepartmentMasterRepository;
import org.sma.jpa.repository.school.SchoolProfileRepository;
import org.sma.jpa.repository.staff.DepartmentStaffMappingRepository;
import org.sma.jpa.repository.staff.StaffRepository;
import org.sma.staff.mngt.app.dto.StaffRequestDTO;
import org.sma.staff.mngt.app.dto.StaffResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for Staff Management
 */
@Service
@Transactional
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SchoolProfileRepository schoolProfileRepository;

    @Autowired
    private DepartmentMasterRepository departmentMasterRepository;

    @Autowired
    private DepartmentStaffMappingRepository departmentStaffMappingRepository;

    public StaffResponseDTO createStaff(StaffRequestDTO requestDTO) {
        SchoolProfile school = schoolProfileRepository.findById(requestDTO.getSchoolId())
                .orElseThrow(() -> new RuntimeException("School not found"));

        // Check for duplicate employee code
        Optional<Staff> existing = staffRepository.findBySchoolAndEmployeeCode(school, requestDTO.getEmployeeCode());
        if (existing.isPresent()) {
            throw new RuntimeException("Employee code already exists for this school");
        }

        Staff staff = new Staff();
        mapRequestToEntity(requestDTO, staff, school);
        
        staff = staffRepository.save(staff);
        
        // Create department-staff mappings
        if (requestDTO.getDepartmentIds() != null && requestDTO.getDepartmentIds().length > 0) {
            for (int i = 0; i < requestDTO.getDepartmentIds().length; i++) {
                final Long departmentId = requestDTO.getDepartmentIds()[i];
                final int index = i;
                DepartmentMaster department = departmentMasterRepository.findById(departmentId)
                        .orElseThrow(() -> new RuntimeException("Department not found with id: " + departmentId));
                
                DepartmentStaffMapping mapping = new DepartmentStaffMapping();
                mapping.setDepartment(department);
                mapping.setStaff(staff);
                mapping.setIsPrimaryDepartment(index == 0); // First department is primary
                mapping.setIsActive(true);
                
                departmentStaffMappingRepository.save(mapping);
            }
        }
        
        return mapEntityToResponse(staff);
    }

    public StaffResponseDTO updateStaff(Long staffId, StaffRequestDTO requestDTO) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        SchoolProfile school = schoolProfileRepository.findById(requestDTO.getSchoolId())
                .orElseThrow(() -> new RuntimeException("School not found"));

        mapRequestToEntity(requestDTO, staff, school);
        
        staff = staffRepository.save(staff);
        
        // Handle department-staff mappings intelligently
        List<DepartmentStaffMapping> existingMappings = departmentStaffMappingRepository.findByStaffId(staffId);
        
        if (requestDTO.getDepartmentIds() != null && requestDTO.getDepartmentIds().length > 0) {
            // Create sets for comparison
            java.util.Set<Long> newDepartmentIds = new java.util.HashSet<>();
            for (Long deptId : requestDTO.getDepartmentIds()) {
                newDepartmentIds.add(deptId);
            }
            
            java.util.Set<Long> existingDepartmentIds = new java.util.HashSet<>();
            for (DepartmentStaffMapping mapping : existingMappings) {
                existingDepartmentIds.add(mapping.getDepartment().getId());
            }
            
            // Delete mappings that are no longer in the new list
            for (DepartmentStaffMapping mapping : existingMappings) {
                if (!newDepartmentIds.contains(mapping.getDepartment().getId())) {
                    departmentStaffMappingRepository.delete(mapping);
                }
            }
            
            // Update isPrimaryDepartment for all mappings and add new ones
            for (int i = 0; i < requestDTO.getDepartmentIds().length; i++) {
                final Long departmentId = requestDTO.getDepartmentIds()[i];
                final int index = i;
                final boolean isPrimary = (index == 0);
                
                // Check if mapping already exists
                if (existingDepartmentIds.contains(departmentId)) {
                    // Update existing mapping's isPrimaryDepartment flag
                    for (DepartmentStaffMapping mapping : existingMappings) {
                        if (mapping.getDepartment().getId().equals(departmentId)) {
                            if (mapping.getIsPrimaryDepartment() != isPrimary) {
                                mapping.setIsPrimaryDepartment(isPrimary);
                                departmentStaffMappingRepository.save(mapping);
                            }
                            break;
                        }
                    }
                } else {
                    // Create new mapping
                    DepartmentMaster department = departmentMasterRepository.findById(departmentId)
                            .orElseThrow(() -> new RuntimeException("Department not found with id: " + departmentId));
                    
                    DepartmentStaffMapping mapping = new DepartmentStaffMapping();
                    mapping.setDepartment(department);
                    mapping.setStaff(staff);
                    mapping.setIsPrimaryDepartment(isPrimary);
                    mapping.setIsActive(true);
                    
                    departmentStaffMappingRepository.save(mapping);
                }
            }
        } else {
            // No departments selected, delete all existing mappings
            if (!existingMappings.isEmpty()) {
                departmentStaffMappingRepository.deleteAll(existingMappings);
            }
        }
        
        return mapEntityToResponse(staff);
    }

    public StaffResponseDTO getStaffById(Long staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        return mapEntityToResponse(staff);
    }

    public List<StaffResponseDTO> getAllStaffBySchool(Long schoolId) {
        SchoolProfile school = schoolProfileRepository.findById(schoolId)
                .orElseThrow(() -> new RuntimeException("School not found"));
        
        return staffRepository.findBySchoolAndStaffStatusAndIsActiveTrue(school, "ACTIVE")
                .stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    public List<StaffResponseDTO> getStaffByType(Long schoolId, String staffType) {
        SchoolProfile school = schoolProfileRepository.findById(schoolId)
                .orElseThrow(() -> new RuntimeException("School not found"));
        
        return staffRepository.findBySchoolAndStaffTypeAndStaffStatusAndIsActiveTrue(school, staffType, "ACTIVE")
                .stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    public void deleteStaff(Long staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        staff.setIsActive(false);
        staff.setStaffStatus("TERMINATED");
        staffRepository.save(staff);
    }

    private void mapRequestToEntity(StaffRequestDTO dto, Staff entity, SchoolProfile school) {
        entity.setSchool(school);
        entity.setEmployeeCode(dto.getEmployeeCode());
        entity.setFirstName(dto.getFirstName());
        entity.setMiddleName(dto.getMiddleName());
        entity.setLastName(dto.getLastName());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setGender(dto.getGender());
        entity.setBloodGroup(dto.getBloodGroup());
        entity.setEmail(dto.getEmail());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setAddressLine1(dto.getAddressLine1());
        entity.setAddressLine2(dto.getAddressLine2());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setPostalCode(dto.getPostalCode());
        entity.setStaffType(dto.getStaffType());
        entity.setDesignation(dto.getDesignation());
        
        entity.setQualification(dto.getQualification());
        entity.setSpecialization(dto.getSpecialization());
        entity.setExperienceYears(dto.getExperienceYears());
        entity.setJoiningDate(dto.getJoiningDate());
        entity.setEmploymentType(dto.getEmploymentType());
        entity.setSalary(dto.getSalary());
        entity.setStaffStatus(dto.getStaffStatus());
        entity.setPhotoUrl(dto.getPhotoUrl());
        entity.setAadharNumber(dto.getAadharNumber());
        entity.setPanNumber(dto.getPanNumber());
        entity.setBankAccountNumber(dto.getBankAccountNumber());
        entity.setBankName(dto.getBankName());
        entity.setBankIfscCode(dto.getBankIfscCode());
    }

    private StaffResponseDTO mapEntityToResponse(Staff entity) {
        StaffResponseDTO dto = new StaffResponseDTO();
        dto.setId(entity.getId());
        dto.setSchoolId(entity.getSchool().getId());
        dto.setEmployeeCode(entity.getEmployeeCode());
        dto.setFirstName(entity.getFirstName());
        dto.setMiddleName(entity.getMiddleName());
        dto.setLastName(entity.getLastName());
        dto.setFullName(entity.getFirstName() + 
                (entity.getMiddleName() != null ? " " + entity.getMiddleName() : "") + 
                " " + entity.getLastName());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setGender(entity.getGender());
        dto.setBloodGroup(entity.getBloodGroup());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setAddressLine1(entity.getAddressLine1());
        dto.setAddressLine2(entity.getAddressLine2());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setPostalCode(entity.getPostalCode());
        dto.setStaffType(entity.getStaffType());
        dto.setDesignation(entity.getDesignation());
        
        // Query department_staff_mapping to get departmentIds
        List<DepartmentStaffMapping> mappings = departmentStaffMappingRepository.findByStaffId(entity.getId());
        if (mappings != null && !mappings.isEmpty()) {
            Long[] departmentIds = mappings.stream()
                    .map(mapping -> mapping.getDepartment().getId())
                    .toArray(Long[]::new);
            dto.setDepartmentIds(departmentIds);
        }
        
        dto.setQualification(entity.getQualification());
        dto.setSpecialization(entity.getSpecialization());
        dto.setExperienceYears(entity.getExperienceYears());
        dto.setJoiningDate(entity.getJoiningDate());
        dto.setEmploymentType(entity.getEmploymentType());
        dto.setSalary(entity.getSalary());
        dto.setStaffStatus(entity.getStaffStatus());
        dto.setPhotoUrl(entity.getPhotoUrl());
        dto.setIsActive(entity.getIsActive());
        
        return dto;
    }
}
