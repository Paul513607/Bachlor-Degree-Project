import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TimetableComponent } from './timetable/timetable.component';
import { MatTableModule } from '@angular/material/table';
import { MatSelectModule } from '@angular/material/select';
import { MatRadioModule } from '@angular/material/radio';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatCardModule } from '@angular/material/card';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { TimetableRoutingModule } from './timetable-routing.module';
import { SidebarComponent } from './sidebar/sidebar.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { EventCardComponent } from './event-card/event-card.component';
import { AssignEventComponent } from './assign-event/assign-event.component';
import { FileTransferComponent } from './file-transfer/file-transfer.component';




@NgModule({
  declarations: [
    TimetableComponent,
    SidebarComponent,
    EventCardComponent,
    AssignEventComponent,
    FileTransferComponent,
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    TimetableRoutingModule,
    FormsModule,
    MatTableModule,
    MatSelectModule,
    MatRadioModule,
    MatExpansionModule,
    MatCardModule,
    MatButtonToggleModule,
    MatIconModule,
    MatInputModule,
  ]
})
export class TimetableModule { }
