import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { 
  StudentService, 
  Student, 
  StudentRequest, 
  GuardianRequest,
  AddressRequest,
  GENDER, 
  STUDENT_STATUS, 
  BLOOD_GROUPS,
  RELIGIONS,
  CASTE_CATEGORIES,
  NATIONALITIES,
  GUARDIAN_RELATIONS,
  ADDRESS_TYPES,
  INDIAN_STATES
} from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';

interface SchoolContext {
  schoolId: number;
  schoolName: string;
  schoolCode: string;
}

@Component({
  selector: 'app-student-form',
  templateUrl: './student-form.component.html',
  styleUrls: ['./student-form.component.scss']
})
export class StudentFormComponent implements OnInit {
  studentForm: FormGroup;
  isEditMode = false;
  studentId?: string;
  schoolId = 0;
  selectedSchool: SchoolContext | null = null;
  
  genders = Object.values(GENDER);
  statuses = Object.values(STUDENT_STATUS);
  bloodGroups = BLOOD_GROUPS;
  religions = RELIGIONS;
  casteCategories = CASTE_CATEGORIES;
  nationalities = NATIONALITIES;
  guardianRelations = GUARDIAN_RELATIONS;
  addressTypes = ADDRESS_TYPES;
  indianStates = INDIAN_STATES;

  constructor(
    private fb: FormBuilder,
    private studentService: StudentService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.studentForm = this.createForm();
  }

  ngOnInit(): void {
    // Get route params first
    this.studentId = this.route.snapshot.paramMap.get('id') || undefined;
    this.isEditMode = !!this.studentId && this.route.snapshot.url[this.route.snapshot.url.length - 1].path === 'edit';

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
          
          // Load student data if in edit mode and we now have schoolId
          if (this.isEditMode && this.studentId && this.schoolId) {
            this.loadStudent();
          }
        }
      }
    });
    
    console.log('Student form component initialized, requesting school context...');
    
    // Request context from parent after Angular is ready
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }
  }

  createForm(): FormGroup {
    return this.fb.group({
      // Basic Information
      admissionNo: [''], // Auto-generated if not provided
      status: ['ACTIVE'],
      firstName: ['', Validators.required],
      middleName: [''],
      lastName: ['', Validators.required],
      dateOfBirth: ['', Validators.required],
      gender: ['', Validators.required],
      
      // Contact Information
      phone: [''],
      email: ['', Validators.email],
      
      // Personal Details
      bloodGroup: [''],
      religion: [''],
      caste: [''],
      nationality: ['Indian', Validators.required],
      motherTongue: [''],
      aadharNo: [''],
      
      // Admission Details
      admissionDate: ['', Validators.required],
      photoUrl: [''],
      remarks: [''],
      
      // Medical Information
      medicalConditions: [''],
      allergies: [''],
      
      // Guardians Array
      guardians: this.fb.array([]),
      
      // Addresses Array
      addresses: this.fb.array([])
    });
  }
  
  get guardians(): FormArray {
    return this.studentForm.get('guardians') as FormArray;
  }
  
  get addresses(): FormArray {
    return this.studentForm.get('addresses') as FormArray;
  }
  
  createGuardianForm(): FormGroup {
    return this.fb.group({
      relation: ['', Validators.required],
      name: ['', Validators.required],
      phone: ['', Validators.required],
      alternatePhone: [''],
      email: ['', Validators.email],
      occupation: [''],
      annualIncome: [''],
      education: [''],
      isPrimary: [false],
      aadharNo: [''],
      panNo: [''],
      photoUrl: ['']
    });
  }
  
  createAddressForm(): FormGroup {
    return this.fb.group({
      addressType: ['', Validators.required],
      line1: ['', Validators.required],
      line2: [''],
      city: ['', Validators.required],
      state: ['', Validators.required],
      pincode: ['', Validators.required],
      country: ['India', Validators.required],
      landmark: ['']
    });
  }
  
  addGuardian(): void {
    this.guardians.push(this.createGuardianForm());
  }
  
  removeGuardian(index: number): void {
    this.guardians.removeAt(index);
  }
  
  addAddress(): void {
    this.addresses.push(this.createAddressForm());
  }
  
  removeAddress(index: number): void {
    this.addresses.removeAt(index);
  }

  loadStudent(): void {
    if (!this.schoolId || !this.studentId) return;

    this.studentService.getStudent(this.schoolId, this.studentId).subscribe({
      next: (student: any) => {
        // Patch basic student fields
        this.studentForm.patchValue(student);
        
        // Load guardians if they exist in the response
        if (student.guardians && student.guardians.length > 0) {
          // Clear existing guardians
          while (this.guardians.length) {
            this.guardians.removeAt(0);
          }
          // Add loaded guardians
          student.guardians.forEach((guardian: any) => {
            this.guardians.push(this.fb.group({
              relation: [guardian.relation, Validators.required],
              name: [guardian.name, Validators.required],
              phone: [guardian.phone, Validators.required],
              alternatePhone: [guardian.alternatePhone],
              email: [guardian.email, Validators.email],
              occupation: [guardian.occupation],
              annualIncome: [guardian.annualIncome],
              education: [guardian.education],
              isPrimary: [guardian.isPrimary],
              aadharNo: [guardian.aadharNo],
              panNo: [guardian.panNo],
              photoUrl: [guardian.photoUrl]
            }));
          });
        }
        
        // Load addresses if they exist in the response
        if (student.addresses && student.addresses.length > 0) {
          // Clear existing addresses
          while (this.addresses.length) {
            this.addresses.removeAt(0);
          }
          // Add loaded addresses
          student.addresses.forEach((address: any) => {
            this.addresses.push(this.fb.group({
              addressType: [address.addressType, Validators.required],
              line1: [address.line1, Validators.required],
              line2: [address.line2],
              city: [address.city, Validators.required],
              state: [address.state, Validators.required],
              pincode: [address.pincode, Validators.required],
              country: [address.country, Validators.required],
              landmark: [address.landmark]
            }));
          });
        }
      },
      error: (error) => {
        console.error('Error loading student:', error);
        this.snackBar.open('Error loading student details', 'Close', { duration: 3000 });
      }
    });
  }

  onSubmit(): void {
    if (!this.schoolId || this.schoolId === 0) {
      this.snackBar.open('School ID is missing. Please select a school first.', 'Close', { duration: 3000 });
      return;
    }
    
    if (this.studentForm.invalid) {
      this.snackBar.open('Please fill all required fields', 'Close', { duration: 3000 });
      return;
    }

    const formValue = this.studentForm.value;
    const studentData: any = {
      schoolId: this.schoolId,
      admissionNo: formValue.admissionNo || undefined,
      status: formValue.status,
      firstName: formValue.firstName,
      middleName: formValue.middleName,
      lastName: formValue.lastName,
      dateOfBirth: formValue.dateOfBirth,
      gender: formValue.gender,
      phone: formValue.phone,
      email: formValue.email,
      bloodGroup: formValue.bloodGroup,
      religion: formValue.religion,
      caste: formValue.caste,
      nationality: formValue.nationality,
      motherTongue: formValue.motherTongue,
      aadharNo: formValue.aadharNo,
      admissionDate: formValue.admissionDate,
      photoUrl: formValue.photoUrl,
      remarks: formValue.remarks,
      medicalConditions: formValue.medicalConditions,
      allergies: formValue.allergies,
      guardians: formValue.guardians,
      addresses: formValue.addresses
    };

    const operation = this.isEditMode && this.studentId
      ? this.studentService.updateStudent(this.schoolId, this.studentId, studentData)
      : this.studentService.createStudent(this.schoolId, studentData);

    operation.subscribe({
      next: () => {
        this.snackBar.open(
          `Student ${this.isEditMode ? 'updated' : 'created'} successfully`,
          'Close',
          { duration: 3000 }
        );
        this.router.navigate(['/students']);
      },
      error: (error) => {
        console.error('Error saving student:', error);
        this.snackBar.open('Error saving student', 'Close', { duration: 3000 });
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/students']);
  }
}
