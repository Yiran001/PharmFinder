import {AfterViewInit, Component, ElementRef} from '@angular/core';
import {Title} from "@angular/platform-browser";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements AfterViewInit{
  title = 'Apothekenfinder';
  page1 = 'Suchen';
  page2 = 'Bestand';

  websiteTitel='Apothekenfinder';

  constructor(private elementRef: ElementRef, private titleService: Title){
    this.titleService.setTitle(this.websiteTitel);

  }
  ngAfterViewInit(){
    this.elementRef.nativeElement.ownerDocument.body.style.backgroundColor = '';
  }

}

