import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignEventComponent } from './assign-event.component';

describe('AssignEventComponent', () => {
  let component: AssignEventComponent;
  let fixture: ComponentFixture<AssignEventComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AssignEventComponent]
    });
    fixture = TestBed.createComponent(AssignEventComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
