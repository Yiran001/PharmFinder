import {Component, Inject, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {Medicine, MedicineService} from "../services/medicine.service";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";

export interface DialogData {
  dialogMedicine: Medicine;
}

interface DropdownDict {
  value: string;
  viewValue: string;
}

@Component({
  selector: 'app-profile-page',
  templateUrl: './medicine-page.component.html',
  providers: [MedicineService],
  styleUrls: ['./medicine-page.component.css']
})
export class MedicinePageComponent implements OnInit {

  pharmacy: string = ""
  medicineList: Array<Medicine> = []
  searchParam: string = "";
  searchCriteriaString: string = "";
  searchCriteria: DropdownDict[] = [
    {value: 'PZN', viewValue: 'PZN'},
    {value: 'NAME', viewValue: 'Name'},
    {value: 'MEDICINE_FORM', viewValue: 'Darreichungsform'},
    {value: 'AMOUNT', viewValue: 'Menge'}
  ];

  constructor(private medicineService: MedicineService, private router: Router, private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.getMedicines();
  }

  async getMedicines(): Promise<void> {
    this.medicineList = await this.medicineService.getMedicines().toPromise();


  }

  async getMedicinesFiltered(): Promise<void> {
    this.medicineList = await this.medicineService.getMedicinesFiltered(this.searchCriteriaString, this.searchParam).toPromise();
  }

  openDialog(medicine: Medicine) {
    const dialogRef = this.dialog.open(MedicineManagementDialog, {
      data: {
        dialogMedicine: medicine
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      this.getMedicines();
    });
  }

  openNewMedicineDialog() {
    const dialogRef = this.dialog.open(NewMedicineDialog);
    dialogRef.afterClosed().subscribe(result => {
      this.getMedicines();
    });
  }
}

@Component({
  selector: 'medicine-management-dialog',
  templateUrl: 'medicine-management-dialog.html',
})
export class MedicineManagementDialog {


  constructor(public dialogRef: MatDialogRef<MedicineManagementDialog>, @Inject(MAT_DIALOG_DATA) public data: DialogData, private medicineService: MedicineService) {
  }

  handleChanges() {
    this.medicineService.updateMedicines(this.data.dialogMedicine);
    this.dialogRef.close();

  }

  deleteMedicine() {
    this.medicineService.deleteMedicines(this.data.dialogMedicine);
    this.dialogRef.close();
  }
}


@Component({
  selector: 'new-medicine-dialog',
  templateUrl: 'new-medicine-dialog.html',
})
export class NewMedicineDialog {
  public medicine: Medicine;
  medicineForms: DropdownDict[] = [
    {value: 'PILL', viewValue: 'Pille'},
    {value: 'SYRUP', viewValue: 'Sirup'},
    {value: 'SOLUTION', viewValue: 'Lösung'},
    {value: 'POWDER', viewValue: 'Puder'},
    {value: 'PASTE', viewValue: 'Salbe'},
    {value: 'INHALATION', viewValue: 'Inhalation'},
    {value: 'VAGINAL', viewValue: 'Vaginal'},
    {value: 'URETHRAL', viewValue: 'Urethral'},
    {value: 'NASAL', viewValue: 'Nasal'},
    {value: 'OTHER', viewValue: 'Andere'}
  ];

  constructor(public dialogRef: MatDialogRef<MedicineManagementDialog>, private medicineService: MedicineService) {
    this.medicine = {
      pzn: "",
      friendlyName: "",
      medicineForm: "",
      amount: 0
    }
  }

  handleChanges() {
    this.medicineService.createMedicine(this.medicine);
    this.dialogRef.close();
  }
}
