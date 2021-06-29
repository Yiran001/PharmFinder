import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {FrontPageComponent} from "./front-page/front-page.component";
import {AboutPageComponent} from "./about-page/about-page.component";
import {DatenschutzerklaerungComponent} from "./datenschutzerklaerung/datenschutzerklaerung.component";

const routes: Routes = [
  {path: 'home', component:FrontPageComponent},
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'about', component:AboutPageComponent },
  { path: 'datenschutzerklaerung', component:DatenschutzerklaerungComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
