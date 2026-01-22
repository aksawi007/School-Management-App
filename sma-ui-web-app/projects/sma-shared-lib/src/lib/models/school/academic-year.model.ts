/**
 * Academic Year Model
 * Represents an academic year configuration
 */
export interface AcademicYear {
  yearId?: number;
  yearName: string;
  startDate: string;
  endDate: string;
  isCurrent: boolean;
  description?: string;
}

/**
 * Academic Year Request
 * Used for creating/updating academic years
 */
export interface AcademicYearRequest {
  yearName: string;
  startDate: string;
  endDate: string;
  description?: string;
}

/**
 * Academic Year Response
 * Response structure from backend
 */
export interface AcademicYearResponse extends AcademicYear {
  yearId: number;
}
