export interface DailyClassSession {
  id?: number;
  schoolId: number;
  academicYearId: number;
  classId: number;
  sectionId: number;
  sessionDate: string;
  timeSlotId: number;
  routineMasterId: number;
  subjectOverride?: number;
  teacherOverride?: number;
  actualTeacherId?: number;
  sessionStatus: 'SCHEDULED' | 'CONDUCTED' | 'CANCELLED' | 'POSTPONED';
  isAttendanceMarked?: boolean;
  remarks?: string;
  // Populated fields
  className?: string;
  sectionName?: string;
  subjectName?: string;
  teacherName?: string;
  timeSlotName?: string;
  startTime?: string;
  endTime?: string;
}

export interface DailyClassSessionRequest {
  schoolId: number;
  academicYearId: number;
  classId: number;
  sectionId: number;
  sessionDate: string;
  timeSlotId: number;
  routineMasterId: number;
  subjectOverride?: number;
  teacherOverride?: number;
  actualTeacherId?: number;
  sessionStatus: string;
  remarks?: string;
}
