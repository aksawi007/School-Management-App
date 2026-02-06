import { Injectable } from '@angular/core';

export interface PrintOptions {
  title?: string;
  styles?: string;
  orientation?: 'portrait' | 'landscape';
  pageSize?: 'A4' | 'Letter';
}

@Injectable({
  providedIn: 'root'
})
export class PrintService {

  constructor() { }

  /**
   * Print HTML content
   */
  printContent(content: string, options: PrintOptions = {}): void {
    const printWindow = window.open('', '_blank', 'width=800,height=600');
    
    if (!printWindow) {
      alert('Please allow pop-ups to print');
      return;
    }

    const title = options.title || 'Print';
    const orientation = options.orientation || 'portrait';
    const pageSize = options.pageSize || 'A4';
    
    const htmlContent = `
      <!DOCTYPE html>
      <html>
      <head>
        <title>${title}</title>
        <style>
          @media print {
            @page {
              size: ${pageSize} ${orientation};
              margin: 20mm;
            }
            body {
              -webkit-print-color-adjust: exact;
              print-color-adjust: exact;
            }
          }
          
          body {
            font-family: Arial, sans-serif;
            padding: 20px;
            margin: 0;
          }
          
          table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
          }
          
          table th,
          table td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
          }
          
          table th {
            background-color: #f5f5f5;
            font-weight: bold;
          }
          
          table tr:nth-child(even) {
            background-color: #f9f9f9;
          }
          
          h1, h2, h3 {
            color: #333;
          }
          
          .print-header {
            margin-bottom: 20px;
            border-bottom: 2px solid #333;
            padding-bottom: 10px;
          }
          
          .print-footer {
            margin-top: 20px;
            border-top: 1px solid #ddd;
            padding-top: 10px;
            font-size: 12px;
            color: #666;
          }
          
          ${options.styles || ''}
        </style>
      </head>
      <body>
        ${content}
      </body>
      </html>
    `;
    
    printWindow.document.write(htmlContent);
    printWindow.document.close();
    
    // Wait for content to load before printing
    printWindow.onload = () => {
      setTimeout(() => {
        printWindow.focus();
        printWindow.print();
        // Don't close automatically to allow save as PDF
        // printWindow.close();
      }, 250);
    };
  }

  /**
   * Print table data
   */
  printTable(
    data: any[], 
    columns: { header: string; field: string; transform?: (value: any) => any }[],
    options: PrintOptions = {}
  ): void {
    if (!data || data.length === 0) {
      alert('No data to print');
      return;
    }

    let tableHTML = '<table>';
    
    // Add header
    tableHTML += '<thead><tr>';
    columns.forEach(col => {
      tableHTML += `<th>${col.header}</th>`;
    });
    tableHTML += '</tr></thead>';
    
    // Add body
    tableHTML += '<tbody>';
    data.forEach(row => {
      tableHTML += '<tr>';
      columns.forEach(col => {
        let value = this.getNestedValue(row, col.field);
        if (col.transform) {
          value = col.transform(value);
        }
        tableHTML += `<td>${value || ''}</td>`;
      });
      tableHTML += '</tr>';
    });
    tableHTML += '</tbody>';
    tableHTML += '</table>';
    
    // Add header with title and date
    const printDate = new Date().toLocaleDateString();
    const content = `
      <div class="print-header">
        <h2>${options.title || 'Report'}</h2>
        <p>Generated on: ${printDate}</p>
      </div>
      ${tableHTML}
      <div class="print-footer">
        <p>Page printed on ${new Date().toLocaleString()}</p>
      </div>
    `;
    
    this.printContent(content, options);
  }

  /**
   * Print element by ID
   */
  printElementById(elementId: string, options: PrintOptions = {}): void {
    const element = document.getElementById(elementId);
    
    if (!element) {
      console.error(`Element with ID '${elementId}' not found`);
      return;
    }
    
    const content = element.innerHTML;
    this.printContent(content, options);
  }

  /**
   * Get nested property value using dot notation
   */
  private getNestedValue(obj: any, path: string): any {
    return path.split('.').reduce((current, prop) => current?.[prop], obj);
  }
}
