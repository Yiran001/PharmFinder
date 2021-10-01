import {ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';

import {MedicinePageComponent} from './medicine-page.component';
import {RouterTestingModule} from "@angular/router/testing";
import {FormsModule} from "@angular/forms";
import {MedicineService} from "../services/medicine.service";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {HttpClient} from "@angular/common/http";

fdescribe('ProfilePageComponent', () => {
  let component: MedicinePageComponent;
  let fixture: ComponentFixture<MedicinePageComponent>;
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;
  let matDialog: MatDialog;
  let service: MedicineService;


  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatDialogModule, HttpClientTestingModule, RouterTestingModule, FormsModule],
      providers: [
        {provide: MedicineService, MatDialog}
      ],
      declarations: [MedicinePageComponent]
    })
      .compileComponents();
    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
    matDialog = TestBed.inject(MatDialog);
    service = TestBed.inject(MedicineService);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MedicinePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(' no medicine is shown when user opens site and no medicine is registered to user', fakeAsync(() => {

    expect(component.medicineList.length).toEqual(0);
    component.getMedicines();
    fixture.detectChanges();
    // expect(spy).toHaveBeenCalled();
    expect(component.medicineList.length).toEqual(0);
  }));
  it('medicine is shown when user opens site and medicine is registered to user', fakeAsync(() => {
    expect(service).toBeTruthy();
    let medicine = {
      pzn: "00499175",
      friendlyName: "Baldriparan Stark für die Nacht überzogene Tab. 30 St",
      medicineForm: "PILL",
      amount: 98
    }

    expect(component.medicineList.length).toEqual(0);
    component.getMedicines();
    fixture.detectChanges();
    // expect(spy).toHaveBeenCalled();
    expect(component.medicineList.length).toBeGreaterThan(0);
  }));
});
