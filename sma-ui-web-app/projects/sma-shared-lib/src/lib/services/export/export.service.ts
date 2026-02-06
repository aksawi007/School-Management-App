import { Injectable } from '@angular/core';
import * as XLSX from 'xlsx';

export interface ExportColumn {
  header: string;
  field: string;
  transform?: (value: any) => any;
}

@Injectable({
  providedIn: 'root'
})
export class ExportService {

  constructor() { }

  /**
   * Export data to CSV format
   */
  exportToCSV(data: any[], columns: ExportColumn[], fileName: string = 'export'): void {
    if (!data || data.length === 0) {
      alert('No data to export');
      return;
    }

    // Prepare CSV content
    const headers = columns.map(col => col.header);
    const csvRows: string[] = [];
    
    // Add header row
    csvRows.push(headers.map(h => this.escapeCSVValue(h)).join(','));
    
    // Add data rows
    data.forEach(row => {
      const values = columns.map(col => {
        let value = this.getNestedValue(row, col.field);
        if (col.transform) {
          value = col.transform(value);
        }
        return this.escapeCSVValue(value);
      });
      csvRows.push(values.join(','));
    });
    
    // Create CSV blob and download
    const csvContent = csvRows.join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    this.downloadFile(blob, `${fileName}.csv`);
  }

  /**
   * Export data to Excel (XLSX) format
   */
  exportToExcel(data: any[], columns: ExportColumn[], fileName: string = 'export', sheetName: string = 'Sheet1'): void {
    if (!data || data.length === 0) {
      alert('No data to export');
      return;
    }

    // Prepare data for Excel
    const excelData: any[] = [];
    
    // Add header row
    const headerRow: any = {};
    columns.forEach(col => {
      headerRow[col.field] = col.header;
    });
    excelData.push(headerRow);
    
    // Add data rows
    data.forEach(row => {
      const dataRow: any = {};
      columns.forEach(col => {
        let value = this.getNestedValue(row, col.field);
        if (col.transform) {
          value = col.transform(value);
        }
        dataRow[col.field] = value || '';
      });
      excelData.push(dataRow);
    });
    
    // Create worksheet
    const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(excelData, { skipHeader: true });
    
    // Set column widths
    const columnWidths = columns.map(col => ({ wch: Math.max(col.header.length, 15) }));
    ws['!cols'] = columnWidths;
    
    // Create workbook
    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, sheetName);
    
    // Save file
    XLSX.writeFile(wb, `${fileName}.xlsx`);
  }

  /**
   * Export table data (generic method)
   */
  exportTable(
    data: any[], 
    columns: ExportColumn[], 
    format: 'csv' | 'excel', 
    fileName: string = 'export',
    sheetName: string = 'Sheet1'
  ): void {
    if (format === 'csv') {
      this.exportToCSV(data, columns, fileName);
    } else if (format === 'excel') {
      this.exportToExcel(data, columns, fileName, sheetName);
    }
  }

  /**
   * Escape CSV values to handle commas, quotes, and newlines
   */
  private escapeCSVValue(value: any): string {
    if (value === null || value === undefined) {
      return '';
    }
    
    const stringValue = String(value);
    
    // If value contains comma, quote, or newline, wrap in quotes and escape quotes
    if (stringValue.includes(',') || stringValue.includes('"') || stringValue.includes('\n')) {
      return `"${stringValue.replace(/"/g, '""')}"`;
    }
    
    return stringValue;
  }

  /**
   * Get nested property value using dot notation
   */
  private getNestedValue(obj: any, path: string): any {
    return path.split('.').reduce((current, prop) => current?.[prop], obj);
  }

  /**
   * Download file helper
   */
  private downloadFile(blob: Blob, fileName: string): void {
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.href = url;
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  }
}
