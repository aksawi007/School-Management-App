import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface Staff {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  department: string;
}

@Component({
  selector: 'app-staff-management',
  templateUrl: './staff-management.component.html',
  styleUrls: ['./staff-management.component.scss']
})
export class StaffManagementComponent implements OnInit {
  staffList: Staff[] = [];
  displayedColumns: string[] = ['id', 'name', 'email', 'role', 'department', 'actions'];
  searchText: string = '';

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.loadStaff();
  }

  loadStaff(): void {
    // Mock data - replace with actual API call
    this.staffList = [
      { id: 1, firstName: 'John', lastName: 'Doe', email: 'john.doe@school.com', role: 'Teacher', department: 'Mathematics' },
      { id: 2, firstName: 'Jane', lastName: 'Smith', email: 'jane.smith@school.com', role: 'Principal', department: 'Administration' },
      { id: 3, firstName: 'Mike', lastName: 'Johnson', email: 'mike.j@school.com', role: 'Teacher', department: 'Science' }
    ];
  }

  addStaff(): void {
    console.log('Add staff clicked');
  }

  editStaff(staff: Staff): void {
    console.log('Edit staff:', staff);
  }

  deleteStaff(staff: Staff): void {
    console.log('Delete staff:', staff);
  }
}
