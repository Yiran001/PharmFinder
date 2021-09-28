import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {Medicine, MedicineService} from "../services/medicine.service";


@Component({
  selector: 'app-profile-page',
  templateUrl: './medicine-page.component.html',
  providers: [MedicineService],
  styleUrls: ['./medicine-page.component.css']
})
export class MedicinePageComponent implements OnInit {

  pharmacy: string = ""
  medicineAmountMap: Map<Medicine, number> = new Map<Medicine, number>();


  constructor(private medicineService: MedicineService, private router: Router) {
  }

  ngOnInit(): void {

    this.getMedicines();
  }

  async getMedicines(): Promise<void> {
    this.medicineAmountMap = await this.medicineService.getMedicines().toPromise();

    console.log(this.medicineAmountMap);

  }
}
