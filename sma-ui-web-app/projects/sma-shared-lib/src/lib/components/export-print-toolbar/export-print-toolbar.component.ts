import { Component, Input, Output, EventEmitter } from '@angular/core';
import { ExportService, ExportColumn } from '../../services/export/export.service';
import { PrintService, PrintOptions } from '../../services/print/print.service';

@Component({
  selector: 'lib-export-print-toolbar',
  templateUrl: './export-print-toolbar.component.html',
  styleUrls: ['./export-print-toolbar.component.scss']
})
export class ExportPrintToolbarComponent {
  @Input() data: any[] = [];
  @Input() columns: ExportColumn[] = [];
  @Input() fileName: string = 'export';
  @Input() printTitle: string = 'Report';
  @Input() sheetName: string = 'Sheet1';
  @Input() printOptions: PrintOptions = {};
  @Input() showExport: boolean = true;
  @Input() showPrint: boolean = true;
  @Input() buttonStyle: 'icon' | 'raised' | 'flat' = 'raised';

  @Output() beforeExport = new EventEmitter<{ format: string; data: any[] }>();
  @Output() beforePrint = new EventEmitter<any[]>();

  showExportMenu = false;

  constructor(
    private exportService: ExportService,
    private printService: PrintService
  ) { }

  onExport(format: 'csv' | 'excel'): void {
    this.showExportMenu = false;
    
    // Emit before export event
    this.beforeExport.emit({ format, data: this.data });
    
    // Perform export
    this.exportService.exportTable(
      this.data,
      this.columns,
      format,
      this.fileName,
      this.sheetName
    );
  }

  onPrint(): void {
    // Emit before print event
    this.beforePrint.emit(this.data);
    
    // Perform print
    const options: PrintOptions = {
      title: this.printTitle,
      ...this.printOptions
    };
    
    this.printService.printTable(this.data, this.columns, options);
  }

  toggleExportMenu(): void {
    this.showExportMenu = !this.showExportMenu;
  }

  closeExportMenu(): void {
    this.showExportMenu = false;
  }
}
