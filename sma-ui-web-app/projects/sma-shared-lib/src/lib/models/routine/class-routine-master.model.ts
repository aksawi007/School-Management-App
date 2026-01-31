export interface ClassRoutineMaster {
  id?: number;
  schoolId: number;
  academicYearId: number;
  classId: number;
  sectionId: number;
  dayOfWeek: 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';
  timeSlotId: number;
  subjectId: number;
  teacherId: number;
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
