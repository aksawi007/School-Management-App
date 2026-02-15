# Fee Management UI Design

## Overview
Complete Angular UI implementation for the School Management Application fee management system with:
- Fee Category Management (Core structure)
- Student Fee Allocation (Manual assignment)

---

## Architecture

### Service Layer (Shared Library)

#### 1. **Models** (`sma-shared-lib/models/fee/fee.model.ts`)
```typescript
- FeeCategoryRequest
- FeeCategoryResponse
- StudentFeeAllocationRequest
- StudentFeeAllocationResponse
```

#### 2. **Services**
- **`fee-category.service.ts`** - Fee category CRUD operations
- **`student-fee-allocation.service.ts`** - Fee allocation operations

---

## Components

### Fee Category Management

#### 1. **Fee Category List** (`fee-category-list/`)
**Purpose:** Display and manage all fee categories

**Features:**
- ✅ Table view with all fee categories
- ✅ Filter by category type (TUITION, TRANSPORT, LIBRARY, etc.)
- ✅ View category status (Active/Inactive)
- ✅ Quick edit access
- ✅ Activate/Deactivate categories
- ✅ Responsive design

**Columns:**
- Category Code
- Category Name
- Type (with color chips)
- Mandatory (Yes/No icons)
- Refundable (Yes/No icons)
- Display Order
- Status (Active/Inactive chips)
- Actions (Edit, Activate/Deactivate)

**Usage:**
```typescript
// Navigate to list
this.router.navigate(['/admin/fee-categories']);

// Add new category
this.router.navigate(['/admin/fee-category/new']);

// Edit category
this.router.navigate(['/admin/fee-category/edit', categoryId]);
```

#### 2. **Fee Category Form** (`fee-category-form/`)
**Purpose:** Create or edit fee categories

**Features:**
- ✅ Create new fee category
- ✅ Edit existing category
- ✅ Category code (unique, read-only in edit mode)
- ✅ Category name
- ✅ Category type selection
- ✅ Mandatory/Refundable flags
- ✅ Display order
- ✅ Description
- ✅ Form validation
- ✅ Loading states

**Fields:**
1. **Category Code*** - Unique identifier (TUITION, TRANSPORT, etc.)
2. **Category Name*** - Display name
3. **Category Type*** - Dropdown (TUITION, TRANSPORT, LIBRARY, EXAM, MISCELLANEOUS)
4. **Display Order** - Numeric order
5. **Mandatory Fee** - Checkbox
6. **Refundable Fee** - Checkbox
7. **Description** - Textarea (max 500 chars)

**Validation:**
- Category code: Required, max 50 chars
- Category name: Required, max 150 chars
- Category type: Required
- Description: Max 500 chars

---

### Student Fee Allocation

#### 3. **Fee Allocation List** (`fee-allocation-list/`)
**Purpose:** View and manage student fee allocations

**Features:**
- ✅ Table view with all allocations
- ✅ Filter by status (PENDING, PAID, OVERDUE, etc.)
- ✅ Filter by month
- ✅ Load overdue fees
- ✅ Load month-wise fees
- ✅ View allocation details
- ✅ Update payment
- ✅ Cancel allocation
- ✅ Currency formatting (₹)
- ✅ Date formatting
- ✅ Status color coding
- ✅ Responsive design

**Columns:**
- Allocation Code
- Student Name & Roll Number
- Fee Category & Type
- Fee Amount (₹)
- Duration Type
- Month/Quarter
- Due Date
- Status (color-coded chips)
- Pending Amount (highlighted if overdue)
- Actions (View, Payment, Cancel)

**Status Colors:**
- `PENDING` - Warn (orange)
- `PARTIALLY_PAID` - Accent (blue)
- `PAID` - Primary (green)
- `OVERDUE` - Warn (red text)
- `CANCELLED` - Default (gray)

**Usage:**
```typescript
// Load overdue fees
loadOverdueAllocations();

// Load fees by month
loadAllocationsByMonth('JANUARY');

// Update payment
updatePayment(allocation);

// Cancel allocation
cancelAllocation(allocation);
```

#### 4. **Fee Allocation Form** (`fee-allocation-form/`)
**Purpose:** Manually allocate fees to students

**Features:**
- ✅ Student ID input
- ✅ Fee category selection
- ✅ Amount input with currency
- ✅ Duration type selection
- ✅ Dynamic month/quarter fields
- ✅ Due date picker
- ✅ Payment deadline picker
- ✅ Mandatory flag
- ✅ Discount with reason
- ✅ Remarks
- ✅ Conditional validation
- ✅ Form sections
- ✅ Loading states

**Sections:**

**1. Student Information**
- Student ID* (numeric input)

**2. Fee Details**
- Fee Category* (dropdown from active categories)
- Fee Amount* (currency input)
- Duration Type* (MONTHLY, QUARTERLY, HALF_YEARLY, ANNUAL, ONE_TIME)
- Applicable Month* (shown only for MONTHLY)
- Quarter* (shown only for QUARTERLY)
- Due Date* (date picker)
- Payment Deadline (optional date picker)
- Mandatory Fee (checkbox)

**3. Discount (Optional)**
- Discount Amount (currency input)
- Discount Reason* (shown only when discount > 0)

**4. Additional Information**
- Remarks (textarea, max 500 chars)

**Validation Logic:**
```typescript
// Duration-based validation
if (durationType === 'MONTHLY') {
  applicableMonth: required
}
if (durationType === 'QUARTERLY') {
  quarter: required
}

// Discount validation
if (discountAmount > 0) {
  discountReason: required
}
```

---

## Integration Steps

### Step 1: Update Shared Library Exports

Update `sma-shared-lib/src/public-api.ts`:
```typescript
// Fee Models
export * from './lib/models/fee/fee.model';

// Fee Services
export * from './lib/services/fee-category.service';
export * from './lib/services/student-fee-allocation.service';
```

### Step 2: Update Admin Module

Update `sma-admin-ui-app/src/app/app.module.ts`:
```typescript
import { FeeCategoryListComponent } from './components/fee-category-list/fee-category-list.component';
import { FeeCategoryFormComponent } from './components/fee-category-form/fee-category-form.component';
import { FeeAllocationListComponent } from './components/fee-allocation-list/fee-allocation-list.component';
import { FeeAllocationFormComponent } from './components/fee-allocation-form/fee-allocation-form.component';

@NgModule({
  declarations: [
    // ... existing declarations
    FeeCategoryListComponent,
    FeeCategoryFormComponent,
    FeeAllocationListComponent,
    FeeAllocationFormComponent
  ],
  // ... rest of module
})
```

### Step 3: Add Routes

Update `sma-admin-ui-app/src/app/app-routing.module.ts`:
```typescript
const routes: Routes = [
  // ... existing routes
  {
    path: 'fee-categories',
    component: FeeCategoryListComponent
  },
  {
    path: 'fee-category/new',
    component: FeeCategoryFormComponent
  },
  {
    path: 'fee-category/edit/:id',
    component: FeeCategoryFormComponent
  },
  {
    path: 'fee-allocations',
    component: FeeAllocationListComponent
  },
  {
    path: 'fee-allocation/new',
    component: FeeAllocationFormComponent
  }
];
```

### Step 4: Update Dashboard

Update `dashboard.component.ts` to add fee management cards:
```typescript
{
  title: 'Fee Categories',
  description: 'Manage core fee structure',
  icon: 'category',
  route: '/fee-categories',
  color: '#ed6c02',
  category: 'financial'
},
{
  title: 'Fee Allocation',
  description: 'Allocate fees to students',
  icon: 'payments',
  route: '/fee-allocations',
  color: '#ed6c02',
  category: 'financial'
}
```

### Step 5: Build Shared Library

```bash
cd sma-ui-web-app
ng build sma-shared-lib
```

---

## API Integration

### Fee Category Service

```typescript
// Create category
feeCategoryService.createFeeCategory(schoolId, request)
  .subscribe(response => {
    // Handle success
  });

// Get all active categories
feeCategoryService.getAllActiveFeeCategories(schoolId)
  .subscribe(categories => {
    // Use categories
  });

// Update category
feeCategoryService.updateFeeCategory(categoryId, request)
  .subscribe(response => {
    // Handle success
  });

// Toggle status
feeCategoryService.deactivateFeeCategory(categoryId)
  .subscribe(() => {
    // Handle success
  });
```

### Fee Allocation Service

```typescript
// Allocate fee
feeAllocationService.allocateFeeToStudent(
  schoolId,
  academicYearId,
  allocatedBy,
  request
).subscribe(response => {
  // Handle success
});

// Get student allocations
feeAllocationService.getStudentFeeAllocations(studentId, academicYearId)
  .subscribe(allocations => {
    // Use allocations
  });

// Get overdue fees
feeAllocationService.getOverdueFeeAllocations(schoolId, academicYearId)
  .subscribe(allocations => {
    // Use allocations
  });

// Update payment
feeAllocationService.updateAllocationStatus(allocationId, 'PAID', paidAmount)
  .subscribe(response => {
    // Handle success
  });

// Cancel allocation
feeAllocationService.cancelFeeAllocation(allocationId, reason)
  .subscribe(() => {
    // Handle success
  });
```

---

## User Workflows

### Workflow 1: Setup Fee Categories

1. Navigate to **Dashboard**
2. Click **"Fee Categories"** card
3. View list of existing categories
4. Click **"Add Fee Category"**
5. Fill form:
   - Code: TUITION
   - Name: Tuition Fee
   - Type: TUITION
   - Mandatory: Yes
   - Display Order: 1
6. Click **"Create"**
7. Category appears in list

### Workflow 2: Allocate Fee to Student

1. Navigate to **Dashboard**
2. Click **"Fee Allocation"** card
3. View existing allocations
4. Click **"Allocate Fee"**
5. Fill form:
   - Student ID: 12345
   - Fee Category: Tuition Fee
   - Amount: ₹5000
   - Duration: MONTHLY
   - Month: JANUARY
   - Due Date: 2024-01-10
   - Discount: ₹500 (Scholarship)
6. Click **"Allocate Fee"**
7. Allocation appears in list with PENDING status

### Workflow 3: Update Payment

1. Navigate to **Fee Allocation** list
2. Filter by status: PENDING
3. Find student allocation
4. Click **Payment icon** (₹)
5. Enter paid amount: 2000
6. Status updates to PARTIALLY_PAID
7. Pending amount recalculates: ₹2500

### Workflow 4: View Overdue Fees

1. Navigate to **Fee Allocation** list
2. System automatically loads overdue fees
3. Filter by status: OVERDUE
4. View all overdue allocations
5. Pending amounts shown in red
6. Take action (payment/waive/cancel)

---

## Styling & Theming

### Material Design
- Uses Angular Material components
- Consistent color scheme
- Responsive breakpoints
- Mobile-first approach

### Color Palette
```scss
Primary: #3f51b5 (Blue)
Accent: #ff4081 (Pink)
Warn: #f44336 (Red)
Financial: #ed6c02 (Orange)
```

### Responsive Breakpoints
```scss
Mobile: < 768px
Tablet: 768px - 1024px
Desktop: > 1024px
```

---

## Features Summary

### Fee Category Management
✅ Create core fee categories
✅ Edit existing categories
✅ Filter by type
✅ Activate/Deactivate
✅ Display order management
✅ Mandatory/Refundable flags
✅ Full CRUD operations

### Student Fee Allocation
✅ Manual fee allocation
✅ Duration-based allocation (Monthly/Quarterly/etc.)
✅ Month/Quarter specific
✅ Discount support with reason
✅ Payment tracking
✅ Status management (Pending/Paid/Overdue)
✅ Overdue monitoring
✅ Month-wise filtering
✅ Payment updates
✅ Cancellation with reason
✅ Currency formatting (₹)
✅ Date formatting

### General Features
✅ School context handling
✅ Academic year awareness
✅ Loading states
✅ Error handling
✅ Snackbar notifications
✅ Form validation
✅ Responsive design
✅ Material Design UI
✅ Icon-based actions
✅ Color-coded status

---

## Testing Checklist

### Fee Category
- [ ] Create new category
- [ ] Edit existing category
- [ ] Filter by type
- [ ] Deactivate category
- [ ] Activate category
- [ ] Validate unique code
- [ ] Form validation
- [ ] Display order sorting

### Fee Allocation
- [ ] Allocate monthly fee
- [ ] Allocate quarterly fee
- [ ] Allocate one-time fee
- [ ] Apply discount
- [ ] Update payment
- [ ] Cancel allocation
- [ ] Filter by status
- [ ] Filter by month
- [ ] View overdue fees
- [ ] Currency formatting
- [ ] Date formatting
- [ ] Status color coding

### Integration
- [ ] School context received
- [ ] Academic year context received
- [ ] API calls successful
- [ ] Error handling works
- [ ] Loading states shown
- [ ] Navigation works
- [ ] Responsive on mobile
- [ ] Responsive on tablet

---

## Next Steps

1. **Build & Test**
   ```bash
   ng build sma-shared-lib
   ng serve sma-admin-ui-app
   ```

2. **Configure Backend Proxy**
   Update `proxy.conf.json` to route API calls

3. **Add Navigation**
   Update main navigation menu

4. **Add Permissions**
   Implement role-based access control

5. **Enhanced Features**
   - Student search/autocomplete
   - Bulk fee allocation
   - Fee collection dashboard
   - Payment gateway integration
   - SMS/Email notifications
   - Print fee receipts
   - Export to Excel/PDF

---

## File Structure

```
sma-shared-lib/
├── models/
│   └── fee/
│       └── fee.model.ts
├── services/
│   ├── fee-category.service.ts
│   └── student-fee-allocation.service.ts

sma-admin-ui-app/
└── components/
    ├── fee-category-list/
    │   ├── fee-category-list.component.ts
    │   ├── fee-category-list.component.html
    │   └── fee-category-list.component.scss
    ├── fee-category-form/
    │   ├── fee-category-form.component.ts
    │   ├── fee-category-form.component.html
    │   └── fee-category-form.component.scss
    ├── fee-allocation-list/
    │   ├── fee-allocation-list.component.ts
    │   ├── fee-allocation-list.component.html
    │   └── fee-allocation-list.component.scss
    └── fee-allocation-form/
        ├── fee-allocation-form.component.ts
        ├── fee-allocation-form.component.html
        └── fee-allocation-form.component.scss
```

---

## Summary

Complete UI implementation with:
- ✅ 4 Components (List + Form for each module)
- ✅ 2 Services (Fee Category, Fee Allocation)
- ✅ TypeScript models
- ✅ Material Design
- ✅ Responsive layout
- ✅ Form validation
- ✅ Error handling
- ✅ Loading states
- ✅ School/Academic year context
- ✅ Currency & date formatting
- ✅ Status color coding
- ✅ Complete CRUD operations

Ready for integration and testing!
