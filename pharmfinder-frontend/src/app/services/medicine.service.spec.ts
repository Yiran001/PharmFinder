import {TestBed} from '@angular/core/testing';

import {MedicineService} from './medicine.service';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";

fdescribe('MedicineService', () => {
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;
  let service: MedicineService;
  beforeEach(() => {

    let fakeSessionStorageData = {
      USER_KEY: "abc",
      TOKEN_KEY: 1234,
    }

    let fakeStorage = {
      getItem: (key: string): string => "abc"
    };

    spyOn(window.sessionStorage, 'getItem').and.callFake(fakeStorage.getItem);

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

  it("good: getMedicines should return medicines", () => {
    let medicine = {
      pzn: "00499175",
      friendlyName: "Baldriparan Stark für die Nacht überzogene Tab. 30 St",
      medicineForm: "PILL",
      amount: 98
    }
    const parameters = new HttpParams().set("pzn", medicine.pzn).set("friendlyName", medicine.friendlyName).set("medicineForm", medicine.medicineForm).set("username", "abc").set("amount", medicine.amount);
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        responseType: 'text'
      }),
      params: parameters,
    };

    let reqi = service.getMedicines();
    if (!!reqi) {
      reqi.subscribe(data =>
        // When observable resolves, result should match test data
        expect(data).toEqual([medicine])
      );
    }

    const req = httpTestingController.expectOne(service.getURL + "?username=abc");
    expect(req.request.method).toEqual('GET');
    expect(req.request.responseType).toEqual('json');
  });

  it("good: createMedicines should create medicines", () => {
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

    let reqi = service.createMedicine(medicine);


    const req = httpTestingController.expectOne(service.createURL);
    expect(req.request.method).toEqual('POST');
    expect(req.request.responseType).toEqual('json');
    if (!!reqi) {
      reqi.subscribe(data =>
        // When observable resolves, result should match test data
        expect(data).toEqual(medicine)
      );
    }
    const req2 = httpTestingController.expectOne(service.createURL);
    expect(req2.request.method).toEqual('POST');
    expect(req2.request.responseType).toEqual('json');
  });


  it("good: deleteMedicines should delete medicines", () => {
    let medicine = {
      pzn: "00499175",
      friendlyName: "Baldriparan Stark für die Nacht überzogene Tab. 30 St",
      medicineForm: "PILL",
      amount: 98
    }
    const parameters = new HttpParams().set("pzn", medicine.pzn).set("friendlyName", medicine.friendlyName).set("medicineForm", medicine.medicineForm).set("username", "abc").set("amount", medicine.amount);

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        responseType: 'text'
      }),
      params: parameters,
    };

    let reqi = service.deleteMedicines(medicine);
    const req = httpTestingController.expectOne(service.deleteURL + "?username=abc&pzn=00499175");
    expect(req.request.method).toEqual('DELETE');
    expect(req.request.responseType).toEqual('json');
    if (!!reqi) {
      reqi.subscribe(data =>
        // When observable resolves, result should match test data
        expect(data).toEqual(medicine)
      );
    }
    const req2 = httpTestingController.expectOne(service.deleteURL + "?username=abc&pzn=00499175");
    expect(req2.request.method).toEqual('DELETE');
    expect(req2.request.responseType).toEqual('json');
  });


  it("good: updateMedicines should update medicines", () => {
    let medicine = {
      pzn: "00499175",
      friendlyName: "Baldriparan Stark für die Nacht überzogene Tab. 30 St",
      medicineForm: "PILL",
      amount: 98
    }
    const parameters = new HttpParams().set("pzn", medicine.pzn).set("friendlyName", medicine.friendlyName).set("medicineForm", medicine.medicineForm).set("username", "abc").set("amount", medicine.amount);

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        responseType: 'text'
      }),
      params: parameters,
    };

    let reqi = service.updateMedicines(medicine);
    const req = httpTestingController.expectOne(service.updateURL);
    expect(req.request.method).toEqual('PUT');
    expect(req.request.responseType).toEqual('json');
    if (!!reqi) {
      reqi.subscribe(data =>
        // When observable resolves, result should match test data
        expect(data).toEqual(medicine)
      );
    }
    const req2 = httpTestingController.expectOne(service.updateURL);
    expect(req2.request.method).toEqual('PUT');
    expect(req2.request.responseType).toEqual('json');
  });

  it("good: getMedicinesFiltered with pzn should call with pzn as parameter", () => {
    let medicine = {
      pzn: "00499175",
      friendlyName: "Baldriparan Stark für die Nacht überzogene Tab. 30 St",
      medicineForm: "PILL",
      amount: 98
    }
    const parameters = new HttpParams().set("pzn", medicine.pzn).set("friendlyName", medicine.friendlyName).set("medicineForm", medicine.medicineForm).set("username", "abc").set("amount", medicine.amount);

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        responseType: 'text'
      }),
      params: parameters,
    };

    let reqi = service.getMedicinesFiltered("pzn", "12345678");
    if (!!reqi) {
      reqi.subscribe(data =>
        // When observable resolves, result should match test data
        expect(data).toEqual([medicine])
      );
    }
    const req = httpTestingController.expectOne(service.searchAndFilterGetURL + "?username=abc&pzn=12345678");
    expect(req.request.method).toEqual('GET');
    expect(req.request.responseType).toEqual('json');
  });


  it("good: getMedicinesFiltered with medicineForm should call with medicineForm as parameter", () => {
    let medicine = {
      pzn: "00499175",
      friendlyName: "Baldriparan Stark für die Nacht überzogene Tab. 30 St",
      medicineForm: "PILL",
      amount: 98
    }
    const parameters = new HttpParams().set("pzn", medicine.pzn).set("friendlyName", medicine.friendlyName).set("medicineForm", medicine.medicineForm).set("username", "abc").set("amount", medicine.amount);

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        responseType: 'text'
      }),
      params: parameters,
    };

    let reqi = service.getMedicinesFiltered("medicineForm", "PILL");
    if (!!reqi) {
      reqi.subscribe(data =>
        // When observable resolves, result should match test data
        expect(data).toEqual([medicine])
      );
    }
    const req = httpTestingController.expectOne(service.searchAndFilterGetURL + "?username=abc&medicineForm=PILL");
    expect(req.request.method).toEqual('GET');
    expect(req.request.responseType).toEqual('json');
  });


  it("bad: getMedicinesFiltered with wrong parameter should end up in a normal get call without any filtering", () => {
    let medicine = {
      pzn: "00499175",
      friendlyName: "Baldriparan Stark für die Nacht überzogene Tab. 30 St",
      medicineForm: "PILL",
      amount: 98
    }
    const parameters = new HttpParams().set("pzn", medicine.pzn).set("friendlyName", medicine.friendlyName).set("medicineForm", medicine.medicineForm).set("username", "abc").set("amount", medicine.amount);

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        responseType: 'text'
      }),
      params: parameters,
    };

    let reqi = service.getMedicinesFiltered("modalzinFarm", "PILL");
    if (!!reqi) {
      reqi.subscribe(data =>
        // When observable resolves, result should match test data
        expect(data).toEqual([medicine])
      );
    }
    const req = httpTestingController.expectNone(service.searchAndFilterGetURL)
    const req2 = httpTestingController.expectOne(service.getURL + "?username=abc");
    expect(req2.request.method).toEqual('GET');
    expect(req2.request.responseType).toEqual('json');
  });
});
