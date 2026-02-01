import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { StudentService, Student } from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormGroup, FormControl } from '@angular/forms';
import { MatPaginator, PageEvent } from '@angular/material/paginator';

interface SchoolContext {
  schoolId: number;
  schoolName: string;
  schoolCode: string;
}

@Component({
  selector: 'app-student-list',
  templateUrl: './student-list.component.html',
  styleUrls: ['./student-list.component.scss']
})
export class StudentListComponent implements OnInit {
  students: Student[] = [];
  displayedColumns: string[] = ['admissionNo', 'fullName', 'age', 'gender', 'phone', 'actions'];
  loading = false;
  schoolId = 0;
  selectedSchool: SchoolContext | null = null;
  searchForm: FormGroup = new FormGroup({
    firstName: new FormControl(''),
    admissionNo: new FormControl(''),
    phone: new FormControl(''),
    status: new FormControl(''),
    sortBy: new FormControl('admissionNo'),
    sortDir: new FormControl('ASC')
  });
  totalElements = 0;
  pageSize = 20;
  pageIndex = 0;
  pageSizeOptions = [10, 20, 50];
  lastSearchParams: any = null;
  lastSortBy = 'admissionNo';
  lastSortDir = 'ASC';

  constructor(
    private studentService: StudentService,
    private router: Router,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    // searchForm already initialized on declaration to satisfy strict initialization

    // Listen for school context from parent window (shell app)
    window.addEventListener('message', (event) => {
      // Verify origin for security
      if (event.origin !== 'http://localhost:4300') {
        return;
      }
      
      if (event.data && event.data.type === 'SCHOOL_CONTEXT') {
        console.log('Received school context from parent:', event.data);
        this.selectedSchool = event.data.school;
        
        if (this.selectedSchool) {
          this.schoolId = this.selectedSchool.schoolId;
          this.loadStudents();
        }
      }
    });
    
    console.log('Student list component initialized, requesting school context...');
    
    // Request context from parent after Angular is ready
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }
  }

  loadStudents(): void {
    if (!this.schoolId) {
      this.snackBar.open('Please configure school ID', 'Close', { duration: 3000 });
      return;
    }
    this.loading = true;
    this.studentService.getStudentsPage(this.schoolId, this.pageIndex, this.pageSize, this.lastSortBy, this.lastSortDir).subscribe({
      next: (resp) => {
        this.students = resp.content || [];
        this.totalElements = resp.totalElements || (this.students.length || 0);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading students (paged):', error);
        this.snackBar.open('Error loading students', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  viewStudent(student: Student): void {
    this.router.navigate(['/students', student.id]);
  }

  editStudent(student: Student): void {
    this.router.navigate(['/students', student.id, 'edit']);
  }

  createStudent(): void {
    this.router.navigate(['/students/new']);
  }

  getAge(dob?: string | null): string {
    if (!dob) return '-';
    const bd = new Date(dob);
    if (isNaN(bd.getTime())) return '-';
    const today = new Date();
    let age = today.getFullYear() - bd.getFullYear();
    const m = today.getMonth() - bd.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < bd.getDate())) {
      age--;
    }
    return age >= 0 ? String(age) : '-';
  }

  search(): void {
    const values = this.searchForm.value || {};
    const firstName = (values.firstName || '').trim();

    if (!this.schoolId) {
      this.snackBar.open('Please configure school ID', 'Close', { duration: 3000 });
      return;
    }

    this.loading = true;
    // Build backend-friendly params: backend expects `search` and optional `status`.
    // Put provided fields into a single `search` string if any present.
    let searchStr = '';
    if (firstName) searchStr += firstName;
    if (values.admissionNo) searchStr += (searchStr ? ' ' : '') + values.admissionNo.trim();
    if (values.phone) searchStr += (searchStr ? ' ' : '') + values.phone.trim();

    const params: any = {};
    if (searchStr) params.search = searchStr;
    if (values.status) params.status = values.status;
    // remember for pagination
    this.lastSearchParams = Object.keys(params).length ? params : null;

    const sortBy = values.sortBy || 'admissionNo';
    const sortDir = values.sortDir || 'ASC';
    this.lastSortBy = sortBy;
    this.lastSortDir = sortDir;

    // Use backend search if available (paged)
    this.studentService.searchStudentsPaged(this.schoolId, params, this.pageIndex, this.pageSize, sortBy, sortDir).subscribe({
      next: (students) => {
        // when paged response comes back, it contains content and metadata
        if (students && students.content) {
          this.students = students.content || [];
          this.totalElements = students.totalElements || (this.students.length || 0);
        } else {
          this.students = students || [];
          this.totalElements = this.students.length || 0;
        }
        this.loading = false;
      },
      error: (err) => {
        console.error('Search error, falling back to local filter:', err);
        // Fallback: fetch all and filter client-side
        this.studentService.getAllStudents(this.schoolId).subscribe({
          next: (all) => {
            const filtered = (all || []).filter(s => (s.firstName || '').toLowerCase().includes(firstName.toLowerCase()));
            this.students = filtered;
            this.totalElements = filtered.length;
            this.loading = false;
          },
          error: () => {
            this.snackBar.open('Error searching students', 'Close', { duration: 3000 });
            this.loading = false;
          }
        });
      }
    });
  }

  onPageChange(event: PageEvent) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    // If there is an active search, use paged search; otherwise load page
    if (this.lastSearchParams) {
      this.loading = true;
      this.studentService.searchStudentsPaged(this.schoolId, this.lastSearchParams, this.pageIndex, this.pageSize, this.lastSortBy, this.lastSortDir).subscribe({
        next: (resp) => {
          this.students = resp.content || [];
          this.totalElements = resp.totalElements || (this.students.length || 0);
          this.loading = false;
        },
        error: (err) => {
          console.error('Paged search error:', err);
          this.loading = false;
        }
      });
    } else {
      this.loadStudents();
    }
  }

  clearSearch(): void {
    this.searchForm.reset();
    if (this.schoolId) {
      this.loadStudents();
    }
  }
}
