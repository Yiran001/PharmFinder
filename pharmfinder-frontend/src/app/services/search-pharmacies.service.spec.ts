import { TestBed } from '@angular/core/testing';

import { SearchPharmaciesService } from './search-pharmacies.service';

describe('SearchPharmaciesService', () => {
  let service: SearchPharmaciesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SearchPharmaciesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
