import {AfterViewInit, Component, ElementRef} from '@angular/core';
import {Title} from "@angular/platform-browser";
import {TokenStorageService} from "./services/token-storage.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements AfterViewInit {
  title = 'Apothekenfinder';
  page1 = 'Suchen';
  page2 = 'Bestand';

  websiteTitel = 'Apothekenfinder';
  isLoggedIn = false;
  isPharmacist = false;

  constructor(private elementRef: ElementRef, private titleService: Title, private tokenStorageService: TokenStorageService) {
    this.titleService.setTitle(this.websiteTitel);
  }

  ngOnInit(): void {
    this.isLoggedIn = !!this.tokenStorageService.getToken();

    if (this.isLoggedIn) {
      const user = this.tokenStorageService.getUser();
    }
  }

  ngAfterViewInit() {
    this.elementRef.nativeElement.ownerDocument.body.style.backgroundColor = '';
  }

  logout() {
    this.tokenStorageService.signOut();
    window.location.reload();
  }
}

