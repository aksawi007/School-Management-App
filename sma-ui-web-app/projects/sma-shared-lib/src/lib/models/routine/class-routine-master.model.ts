export interface ClassRoutineMaster {
  id?: number;
  schoolId?: number;
  academicYearId?: number;
  classId?: number;
  sectionId?: number;
  dayOfWeek: 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';
  timeSlotId: number;
  subjectId?: number;
  teacherId?: number;
  remarks?: string;
  isActive?: boolean;
  // Populated fields
  className?: string;
  sectionName?: string;
  subjectName?: string;
  teacherName?: string;
  timeSlotName?: string;
  startTime?: string;
  endTime?: string;
  // Related entities
  school?: any;
  academicYear?: any;
  classMaster?: any;
  section?: any;
  timeSlot?: {
    id: number;
    slotName: string;
    startTime: string;
    endTime: string;
    displayOrder?: number;
    slotType?: string;
  };
  subject?: {
    id: number;
    subjectName: string;
    subjectCode: string;
    departmentId?: number;
  };
  teacher?: {
    id: number;
    firstName: string;
    middleName?: string;
    lastName: string;
    employeeCode: string;
    email?: string;
    phoneNumber?: string;
    staffName?: string;
  };
}

export interface ClassRoutineMasterRequest {
  schoolId: number;
  academicYearId: number;
  classId: number;
  sectionId: number;
  dayOfWeek: string;
  timeSlotId: number;
  subjectId: number;
  teacherId: number;
  remarks?: string;
}
