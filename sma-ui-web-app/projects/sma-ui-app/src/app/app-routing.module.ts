import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SchoolSelectionComponent } from './components/school-selection/school-selection.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { loadRemoteModule } from '@angular-architects/module-federation';

const routes: Routes = [
  {
    path: '',
    component: SchoolSelectionComponent
  },
  {
    path: 'dashboard',
    component: DashboardComponent
  },
  {
    path: 'admin',
    loadChildren: () =>
      loadRemoteModule({
        type: 'module',
        remoteEntry: 'http://localhost:4202/remoteEntry.js',
        exposedModule: './Module'
      })
        .then(m => m.AppModule)
        .catch(err => {
          console.error('Error loading admin module:', err);
          return import('./components/module-fallback/module-fallback.module').then(m => m.ModuleFallbackModule);
        })
  },
  {
    path: 'student',
    loadChildren: () =>
      loadRemoteModule({
        type: 'module',
        remoteEntry: 'http://localhost:4200/remoteEntry.js',
        exposedModule: './Module'
      })
        .then(m => m.AppModule)
        .catch(err => {
          console.error('Error loading student module:', err);
          return import('./components/module-fallback/module-fallback.module').then(m => m.ModuleFallbackModule);
        })
  },
  {
    path: 'staff',
    loadChildren: () =>
      loadRemoteModule({
        type: 'module',
        remoteEntry: 'http://localhost:4201/remoteEntry.js',
        exposedModule: './Module'
      })
        .then(m => m.AppModule)
        .catch(err => {
          console.error('Error loading staff module:', err);
          return import('./components/module-fallback/module-fallback.module').then(m => m.ModuleFallbackModule);
        })
  },
  {
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
