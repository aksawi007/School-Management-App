import { Component, OnInit, ViewChild, ViewContainerRef, ComponentFactoryResolver } from '@angular/core';
import { loadRemoteModule } from '@angular-architects/module-federation';
import { SchoolContextService } from 'sma-shared-lib';

@Component({
  selector: 'app-staff-module',
  templateUrl: './staff-module.component.html',
  styleUrls: ['./staff-module.component.scss']
})
export class StaffModuleComponent implements OnInit {
  @ViewChild('moduleContent', { read: ViewContainerRef, static: true })
  viewContainer!: ViewContainerRef;

  constructor(
    private componentFactoryResolver: ComponentFactoryResolver,
    private schoolContextService: SchoolContextService
  ) {}

  async ngOnInit() {
    // Set school context before loading module
    const context = this.schoolContextService.getSchoolContext();
    console.log('Loading staff module with context:', context);

    try {
      const module = await loadRemoteModule({
        type: 'module',
        remoteEntry: 'http://localhost:4201/remoteEntry.js',
        exposedModule: './Module'
      });

      const moduleRef = module.AppModule;
      // Note: Actual component loading would require more complex setup
      console.log('Staff module loaded successfully', moduleRef);
    } catch (error) {
      console.error('Error loading staff module:', error);
    }
  }
}
