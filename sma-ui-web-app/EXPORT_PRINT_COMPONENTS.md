# Export and Print Components

Common reusable components and services for exporting data to various formats and printing.

## Features

- **Export to CSV** - Export table data to comma-separated values
- **Export to Excel (XLSX)** - Export table data to Microsoft Excel format
- **Print** - Print table data with customizable layout
- **PDF Export** - Use browser's print-to-PDF functionality

## Installation

The services and components are available in `sma-shared-lib`. Make sure to install the xlsx dependency:

```bash
npm install xlsx
```

## Services

### ExportService

Handles data export to CSV and Excel formats.

```typescript
import { ExportService, ExportColumn } from 'sma-shared-lib';

constructor(private exportService: ExportService) {}

// Define columns
const columns: ExportColumn[] = [
  { header: 'Name', field: 'name' },
  { header: 'Email', field: 'email' },
  { header: 'Age', field: 'age', transform: (val) => val + ' years' }
];

// Export to CSV
this.exportService.exportToCSV(data, columns, 'my-report');

// Export to Excel
this.exportService.exportToExcel(data, columns, 'my-report', 'Sheet1');
```

### PrintService

Handles printing content with customizable options.

```typescript
import { PrintService, PrintOptions } from 'sma-shared-lib';

constructor(private printService: PrintService) {}

// Print table
const options: PrintOptions = {
  title: 'My Report',
  orientation: 'landscape',
  pageSize: 'A4'
};

this.printService.printTable(data, columns, options);

// Print custom HTML
this.printService.printContent('<h1>Custom Content</h1>', options);

// Print element by ID
this.printService.printElementById('myElementId', options);
```

## Component

### ExportPrintToolbarComponent

A ready-to-use toolbar component with export and print buttons.

#### Basic Usage

```html
<lib-export-print-toolbar
  [data]="myData"
  [columns]="exportColumns"
  [fileName]="'my-report'"
  [printTitle]="'My Report Title'"
  buttonStyle="raised">
</lib-export-print-toolbar>
```

#### Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `data` | `any[]` | `[]` | Data to export/print |
| `columns` | `ExportColumn[]` | `[]` | Column definitions |
| `fileName` | `string` | `'export'` | Base filename for exports |
| `printTitle` | `string` | `'Report'` | Title shown in print view |
| `sheetName` | `string` | `'Sheet1'` | Excel sheet name |
| `printOptions` | `PrintOptions` | `{}` | Additional print options |
| `showExport` | `boolean` | `true` | Show export button |
| `showPrint` | `boolean` | `true` | Show print button |
| `buttonStyle` | `'icon' \| 'raised' \| 'flat'` | `'raised'` | Button style |

#### Events

| Event | Payload | Description |
|-------|---------|-------------|
| `beforeExport` | `{ format: string, data: any[] }` | Fired before export |
| `beforePrint` | `any[]` | Fired before print |

#### Example with Events

```typescript
export class MyComponent {
  data = [...];
  
  exportColumns: ExportColumn[] = [
    { header: 'Subject', field: 'subject' },
    { header: 'Teacher', field: 'teacherName' },
    { header: 'Total Hours', field: 'totalHours', transform: (val) => val?.toFixed(2) }
  ];

  onBeforeExport(event: { format: string; data: any[] }) {
    console.log(`Exporting ${event.data.length} rows as ${event.format}`);
  }

  onBeforePrint(data: any[]) {
    console.log(`Printing ${data.length} rows`);
  }
}
```

```html
<lib-export-print-toolbar
  [data]="data"
  [columns]="exportColumns"
  [fileName]="'routine-summary'"
  [printTitle]="'Weekly Routine Summary'"
  (beforeExport)="onBeforeExport($event)"
  (beforePrint)="onBeforePrint($event)">
</lib-export-print-toolbar>
```

## ExportColumn Interface

```typescript
interface ExportColumn {
  header: string;        // Column header text
  field: string;         // Property name (supports dot notation: 'user.name')
  transform?: (value: any) => any;  // Optional transform function
}
```

## PrintOptions Interface

```typescript
interface PrintOptions {
  title?: string;                        // Document title
  styles?: string;                       // Additional CSS styles
  orientation?: 'portrait' | 'landscape'; // Page orientation
  pageSize?: 'A4' | 'Letter';            // Page size
}
```

## Complete Example - Routine Summary Dialog

```typescript
// Component
import { ExportColumn } from 'sma-shared-lib';

export class RoutineSummaryDialogComponent {
  summaryData: any[] = [];
  
  exportColumns: ExportColumn[] = [
    { header: 'Subject', field: 'subject' },
    { header: 'Department', field: 'department' },
    { header: 'Staff Name', field: 'staffName' },
    { header: 'Total Classes', field: 'totalClasses' },
    { header: 'Total Hours', field: 'totalHours', transform: (val) => val?.toFixed(2) }
  ];
}
```

```html
<!-- Template -->
<h2 mat-dialog-title>
  <span>Weekly Routine Summary</span>
  <lib-export-print-toolbar
    [data]="summaryData"
    [columns]="exportColumns"
    [fileName]="'weekly-routine-summary'"
    [printTitle]="'Weekly Routine Summary'"
    buttonStyle="icon">
  </lib-export-print-toolbar>
</h2>
```

## Tips

1. **PDF Export**: Use the browser's Print dialog and select "Save as PDF"
2. **Large Datasets**: For very large datasets, consider server-side export generation
3. **Custom Transforms**: Use the `transform` function to format dates, numbers, or complex objects
4. **Nested Fields**: Use dot notation in `field` property: `'user.profile.name'`
5. **Styling**: Override component styles using `::ng-deep` in your component styles

## Browser Compatibility

- Chrome/Edge: Full support
- Firefox: Full support
- Safari: Full support
- Excel export requires modern browser with Blob support
