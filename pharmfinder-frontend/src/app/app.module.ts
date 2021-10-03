import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FrontPageComponent } from './front-page/front-page.component';
import { AboutPageComponent } from './about-page/about-page.component';
import { DatenschutzerklaerungComponent } from './datenschutzerklaerung/datenschutzerklaerung.component';
import { RegisterComponent } from './register/register.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { LoginComponent } from './login/login.component';
import {AuthInterceptor, authInterceptorProviders} from './auth.interceptor';
import {RouterModule, Routes} from "@angular/router";
import { ProfilePageComponent } from './profile-page/profile-page.component';
import {HttpClientModule,HTTP_INTERCEPTORS} from "@angular/common/http";
import { SearchPharmaciesComponent } from './search-pharmacies/search-pharmacies.component';
import {
  MedicineManagementDialog,
  MedicinePageComponent,
  NewMedicineDialog
} from "./medicine-page/medicine-page.component";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatDialogModule} from "@angular/material/dialog";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatSelectModule} from "@angular/material/select";
import {MatOptionModule} from "@angular/material/core";

import { GoogleMapsModule } from '@angular/google-maps'

@NgModule({
  declarations: [
    AppComponent,
    FrontPageComponent,
    AboutPageComponent,
    DatenschutzerklaerungComponent,
    RegisterComponent,
    LoginComponent,

    DatenschutzerklaerungComponent,
    ProfilePageComponent,
    SearchPharmaciesComponent,
    MedicinePageComponent,
    MedicineManagementDialog,
    NewMedicineDialog,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    GoogleMapsModule,


    BrowserAnimationsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatOptionModule,
  ],
  providers: [{ provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },],
  bootstrap: [AppComponent]
})
export class AppModule { }
