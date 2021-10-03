import {Component, Inject, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {Medicine, MedicineService} from "../services/medicine.service";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {HttpErrorResponse} from "@angular/common/http";

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

  medicineList: Array<Medicine> = []
  searchParam: string = "";
  searchCriteriaString: string = "";
  sortCriteriaString: string = "";
  sortDirectionString: string = "";

  searchCriteria: DropdownDict[] = [
    {value: 'pzn', viewValue: 'PZN'},
    {value: 'friendlyName', viewValue: 'Name'},
    {value: 'medicineForm', viewValue: 'Darreichungsform'},
    {value: 'amount', viewValue: 'Menge'}
  ];
  sortDirections: DropdownDict[] = [
    {value: 'ascending', viewValue: 'Aufsteigend'},
    {value: 'descending', viewValue: 'Absteigend'},
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
    this.medicineService.getMedicinesFiltered(this.searchCriteriaString, this.searchParam).subscribe(
      (response: Medicine[]) => {
        this.medicineList = response
      });
  }

  sortMedicines() {
    switch (this.sortCriteriaString) {
      case "pzn":
        if (this.sortDirectionString == "ascending")
          this.medicineList.sort((a, b) => (a.pzn < b.pzn ? -1 : 1))
        else
          this.medicineList.sort((a, b) => (a.pzn > b.pzn ? -1 : 1))
        break
      case "friendlyName":
        if (this.sortDirectionString == "ascending")
          this.medicineList.sort((a, b) => (a.friendlyName < b.friendlyName ? -1 : 1))
        else
          this.medicineList.sort((a, b) => (a.friendlyName > b.friendlyName ? -1 : 1))
        break
      case "medicineForm":
        if (this.sortDirectionString == "ascending")
          this.medicineList.sort((a, b) => (a.medicineForm < b.medicineForm ? -1 : 1))
        else
          this.medicineList.sort((a, b) => (a.medicineForm > b.medicineForm ? -1 : 1))
        break
      case "amount":
        if (this.sortDirectionString == "ascending")
          this.medicineList.sort((a, b) => (a.amount < b.amount ? -1 : 1))
        else
          this.medicineList.sort((a, b) => (a.amount > b.amount ? -1 : 1))
        break
      default:
        break
    }
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
  medicine: Medicine;
  pznLengthAcceptable: boolean = false;
  allFieldsFilled: boolean = false;
  buttonPressed: boolean = false;
  pznAlreadyExists: boolean = false;
  creationFailed: boolean = false;
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

  constructor(public dialogRef: MatDialogRef<NewMedicineDialog>, private medicineService: MedicineService) {
    this.medicine = {
      pzn: "",
      friendlyName: "",
      medicineForm: "",
      amount: 0
    }
  }

  handleChanges() {
    this.buttonPressed = true;
    this.pznAlreadyExists = false;
    if (this.medicine.pzn != "" && this.medicine.friendlyName != "" && this.medicine.medicineForm != "" && this.medicine.amount != 0) {
      this.allFieldsFilled = true;
        this.pznLengthAcceptable = true;
        const request = this.medicineService.createMedicine(this.medicine);
        if (!!request) {
          request.subscribe(error => {
            console.error('error' + error);

            if (error instanceof HttpErrorResponse) {
              if (error.status === 409)
                this.pznAlreadyExists = true;
              this.creationFailed = true
            }
          });
          if (!this.creationFailed)
            this.dialogRef.close();

      } else
        this.allFieldsFilled = false;
    }


  }
}

