import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FrontPageComponent } from './front-page/front-page.component';
import { AboutPageComponent } from './about-page/about-page.component';
import { DatenschutzerklaerungComponent } from './datenschutzerklaerung/datenschutzerklaerung.component';

import { ProfilePageComponent } from './profile-page/profile-page.component';
import {HttpClientModule} from "@angular/common/http";


@NgModule({
  declarations: [
    AppComponent,
    FrontPageComponent,
    AboutPageComponent,
    DatenschutzerklaerungComponent,
    ProfilePageComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
