import {ComponentFixture, fakeAsync, TestBed} from '@angular/core/testing';

import {MedicinePageComponent} from './medicine-page.component';
import {RouterTestingModule} from "@angular/router/testing";
import {FormsModule} from "@angular/forms";
import {Medicine, MedicineService} from "../services/medicine.service";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {HttpClient, HttpResponse} from "@angular/common/http";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {CUSTOM_ELEMENTS_SCHEMA} from "@angular/core";
import {Observable, of, throwError} from "rxjs";

describe('MedicinePageComponent', () => {
  let component: MedicinePageComponent;
  let fixture: ComponentFixture<MedicinePageComponent>;
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;
  let matDialog: MatDialog;
  let mockMedicineService;
  let medicineSerrviceStub: Partial<MedicineService>;

  medicineSerrviceStub = {
    getMedicines(): Observable<Medicine[]> {
      let data = 'token : fewfwifwfew.cwcqc.qcewqcf.fbdsbb.trbeterbrebnlf'
      let medicines = [{
        pzn: "00499175",
        friendlyName: "Baldriparan Stark für die Nacht überzogene Tab. 30 St",
        medicineForm: "PILL",
        amount: 98
      }]
      new Observable().subscribe()
      const expectedResponse = new HttpResponse({status: 200, body: medicines});
      const obsof2 = of(data);
      return throwError(expectedResponse);
    },
  };
  beforeEach(async () => {

    await TestBed.configureTestingModule({
      imports: [MatDialogModule, HttpClientTestingModule, RouterTestingModule, FormsModule],
      providers: [
        {provide: MedicineService, useValue: medicineSerrviceStub}, {provide: MatDialog}
      ],
      declarations: [MedicinePageComponent], schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
      .compileComponents();

    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
    matDialog = TestBed.inject(MatDialog);
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
    console.log(component.medicineList)
    fixture.detectChanges();
    expect(component.medicineList.length).toEqual(0);
  }));


  it('medicine is shown when user opens site and medicine is registered to user', fakeAsync(() => {

    expect(component.medicineList.length).toEqual(0);
    component.getMedicines();
    fixture.detectChanges();
    expect(component.medicineList.length).toBeGreaterThan(0);
  }));

});
