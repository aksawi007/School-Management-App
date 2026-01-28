import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StaffService, StaffResponse } from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';

interface SchoolContext {
  schoolId: number;
  schoolName: string;
  schoolCode: string;
}

@Component({
  selector: 'app-staff-detail',
  templateUrl: './staff-detail.component.html',
  styleUrls: ['./staff-detail.component.scss']
})
export class StaffDetailComponent implements OnInit {
  staff?: StaffResponse;
  staffId!: string;
  loading = true;
  selectedSchool: SchoolContext | null = null;

  constructor(
    private staffService: StaffService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    // Listen for school context from parent window
    window.addEventListener('message', (event) => {
      if (event.origin !== 'http://localhost:4300') {
        return;
      }

      if (event.data && event.data.type === 'SCHOOL_CONTEXT') {
        console.log('Received school context in staff detail:', event.data);
        this.selectedSchool = event.data.school;
        
        if (this.selectedSchool && this.staffId) {
          this.loadStaffDetails();
        }
      }
    });

    // Request context from parent
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }

    this.staffId = this.route.snapshot.paramMap.get('id')!;
  }

  loadStaffDetails(): void {
    if (!this.staffId) return;

    const id = parseInt(this.staffId);
    this.staffService.getStaff(id).subscribe({
      next: (staff) => {
        this.staff = staff;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading staff:', error);
        this.snackBar.open('Error loading staff details', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  editStaff(): void {
    this.router.navigate(['/staff', this.staffId, 'edit']);
  }

  goBack(): void {
    this.router.navigate(['/staff']);
  }
}
