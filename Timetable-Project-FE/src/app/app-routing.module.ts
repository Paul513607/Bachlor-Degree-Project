import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    loadChildren: () => 
      import('./modules/timetable/timetable.module').then((m) => m.TimetableModule)
  }, 
  {
    path: 'timetable',
    loadChildren: () => 
      import('./modules/timetable/timetable.module').then((m) => m.TimetableModule)
    
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
