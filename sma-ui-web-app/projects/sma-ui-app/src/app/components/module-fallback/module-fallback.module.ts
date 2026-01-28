import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { ModuleFallbackComponent } from './module-fallback.component';

const routes: Routes = [
  {
    path: '',
    component: ModuleFallbackComponent
  }
];

@NgModule({
  declarations: [ModuleFallbackComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    MatIconModule,
    MatButtonModule
  ]
})
export class ModuleFallbackModule { }
