import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {FrontPageComponent} from './front-page/front-page.component';
import {AboutPageComponent} from './about-page/about-page.component';
import {DatenschutzerklaerungComponent} from './datenschutzerklaerung/datenschutzerklaerung.component';
import {RegisterComponent} from './register/register.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {LoginComponent} from './login/login.component';
import {RouterModule} from "@angular/router";

import {ProfilePageComponent} from './profile-page/profile-page.component';
import {HttpClientModule} from "@angular/common/http";
import {MedicinePageComponent} from "./medicine-page/medicine-page.component";


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
    MedicinePageComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule,

  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
