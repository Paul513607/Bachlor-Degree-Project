import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { TimetableComponent } from './timetable/timetable.component';

const routes: Routes = [
  {
    path: '',
    component: TimetableComponent,
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TimetableRoutingModule { }
