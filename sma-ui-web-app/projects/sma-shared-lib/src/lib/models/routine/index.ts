export interface RoutineTimeSlot {
  id?: number;
  schoolId: number;
  slotName: string;
  startTime: string; // HH:mm format
  endTime: string;
  displayOrder: number;
  slotType: 'TEACHING' | 'BREAK' | 'LUNCH' | 'ASSEMBLY';
  isActive?: boolean;
}

export interface RoutineTimeSlotRequest {
  schoolId: number;
  slotName: string;
  startTime: string;
  endTime: string;
  displayOrder: number;
  slotType: string;
}

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
  // Populated fields
  timeSlot?: RoutineTimeSlot;
  subject?: any;
  teacher?: any;
  classMaster?: any;
  section?: any;
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

export interface DailyClassSession {
  id?: number;
  schoolId: number;
  academicYearId: number;
  classId: number;
  sectionId: number;
  sessionDate: string; // YYYY-MM-DD format
  timeSlotId: number;
  routineMasterId: number;
  subjectOverride?: number;
  teacherOverride?: number;
  actualTeacherId?: number;
  sessionStatus: 'SCHEDULED' | 'CONDUCTED' | 'CANCELLED' | 'POSTPONED';
  isAttendanceMarked?: boolean;
  remarks?: string;
  // Populated fields
  routineMaster?: ClassRoutineMaster;
  timeSlot?: RoutineTimeSlot;
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

export interface StudentAttendance {
  id?: number;
  classSessionId: number;
  studentId: number;
  attendanceStatus: 'PRESENT' | 'ABSENT' | 'LATE' | 'EXCUSED' | 'SICK_LEAVE';
  markedAt?: string;
  markedBy?: number;
  remarks?: string;
  // Populated fields
  student?: any;
  classSession?: DailyClassSession;
}

export interface BulkAttendanceRequest {
  sessionId: number;
  markedBy: number;
  attendanceList: AttendanceItem[];
}

export interface AttendanceItem {
  studentId: number;
  attendanceStatus: string;
  remarks?: string;
}
