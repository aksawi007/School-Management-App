package org.sma.admin.core.app.model.request;

/**
 * Request model for creating/updating academic year
 */
public class AcademicYearRequest {

    private String yearName; // e.g., "2024-2025"
    private String startDate; // yyyy-MM-dd
    private String endDate; // yyyy-MM-dd
    private boolean currentYear;
    private String description;

    // Constructors
    public AcademicYearRequest() {
    }

    // Getters & Setters
    public String getYearName() {
        return yearName;
    }

    public void setYearName(String yearName) {
        this.yearName = yearName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(boolean currentYear) {
        this.currentYear = currentYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
