export interface StudentAttendance {
  id?: number;
  classSessionId: number;
  studentId: number;
  attendanceStatus: 'PRESENT' | 'ABSENT' | 'LATE' | 'EXCUSED' | 'SICK_LEAVE';
  markedAt?: string;
  markedBy?: number;
  remarks?: string;
  // Populated fields
  studentName?: string;
  admissionNo?: string;
  rollNo?: string;
}

export interface BulkAttendanceRequest {
  sessionId: number;
  attendanceList: StudentAttendanceItem[];
  markedBy: number;
}

export interface StudentAttendanceItem {
  studentId: number;
  attendanceStatus: string;
  remarks?: string;
}

export interface AttendanceSummary {
  status: string;
  count: number;
}
