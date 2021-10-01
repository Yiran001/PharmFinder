import {TestBed} from '@angular/core/testing';

import {MedicineService} from './medicine.service';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";

describe('MedicineService', () => {
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;
  let service: MedicineService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers:
        [MedicineService]
    });
    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
    service = TestBed.inject(MedicineService);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it("good: createMedicines should medicine in backend", () => {
    let medicine = {
      pzn: "00499175",
      friendlyName: "Baldriparan Stark für die Nacht überzogene Tab. 30 St",
      medicineForm: "PILL",
      amount: 98
    }
    const parameters = new HttpParams().set("pzn", medicine.pzn).set("friendlyName", medicine.friendlyName).set("medicineForm", medicine.medicineForm).set("username", "abc").set("amount", medicine.amount);


    const httpOptions = {
      headers: new HttpHeaders({
        //'Content-Type': 'application/x-www-form-urlencoded',
        'Content-Type': 'application/json',
        responseType: 'text'
      }),
      params: parameters,
    };

    service.createMedicine(medicine);
    //expect(response?.subscribe).toEqual(medicine);
    const req = httpTestingController.expectOne(service.createURL);
    expect(req.request.method).toEqual('POST');
    expect(req.request.responseType).toEqual('json');
  });
});
