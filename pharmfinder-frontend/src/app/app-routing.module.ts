import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {FrontPageComponent} from "./front-page/front-page.component";
import {AboutPageComponent} from "./about-page/about-page.component";
import {DatenschutzerklaerungComponent} from "./datenschutzerklaerung/datenschutzerklaerung.component";
import {RegisterComponent} from "./register/register.component";
import {LoginComponent} from "./login/login.component";
import {ProfilePageComponent} from "./profile-page/profile-page.component";
import {SearchPharmaciesComponent} from "./search-pharmacies/search-pharmacies.component";

const routes: Routes = [
  {path: 'home', component:FrontPageComponent},
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'about', component:AboutPageComponent },
  { path: 'datenschutzerklaerung', component:DatenschutzerklaerungComponent },
  {path: 'register', component:RegisterComponent},
  {path: 'login', component:LoginComponent},
  {path: 'suchen', component:SearchPharmaciesComponent},
  { path: 'profile', component:ProfilePageComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
