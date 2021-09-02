import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchPharmaciesComponent } from './search-pharmacies.component';

describe('SearchPharmaciesComponent', () => {
  let component: SearchPharmaciesComponent;
  let fixture: ComponentFixture<SearchPharmaciesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SearchPharmaciesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchPharmaciesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
