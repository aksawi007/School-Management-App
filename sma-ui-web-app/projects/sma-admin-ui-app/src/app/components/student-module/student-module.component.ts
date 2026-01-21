import { Component, OnInit, ViewChild, ViewContainerRef, ComponentFactoryResolver } from '@angular/core';
import { loadRemoteModule } from '@angular-architects/module-federation';
import { SchoolContextService } from 'sma-shared-lib';

@Component({
  selector: 'app-student-module',
  templateUrl: './student-module.component.html',
  styleUrls: ['./student-module.component.scss']
})
export class StudentModuleComponent implements OnInit {
  @ViewChild('moduleContent', { read: ViewContainerRef, static: true })
  viewContainer!: ViewContainerRef;

  constructor(
    private componentFactoryResolver: ComponentFactoryResolver,
    private schoolContextService: SchoolContextService
  ) {}

  async ngOnInit() {
    // Set school context before loading module
    const context = this.schoolContextService.getSchoolContext();
    console.log('Loading student module with context:', context);

    try {
      const module = await loadRemoteModule({
        type: 'module',
        remoteEntry: 'http://localhost:4200/remoteEntry.js',
        exposedModule: './Module'
      });

      const moduleRef = module.AppModule;
      // Note: Actual component loading would require more complex setup
      console.log('Student module loaded successfully', moduleRef);
    } catch (error) {
      console.error('Error loading student module:', error);
    }
  }
}
