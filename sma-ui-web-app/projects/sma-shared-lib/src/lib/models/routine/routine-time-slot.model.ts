export interface RoutineTimeSlot {
  id?: number;
  schoolId: number;
  slotName: string;
  startTime: string;
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
