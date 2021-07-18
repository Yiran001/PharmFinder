import {Component, OnInit} from '@angular/core';
import {Address, ProfileService, User} from "../services/profile.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.component.html',
  providers: [ProfileService],
  styleUrls: ['./profile-page.component.css']
})
export class ProfilePageComponent implements OnInit {

  users: Array<User> = [];
  user: User | undefined;

  username = "";
  email = "";
  isPharmacist = "";
  addresses: Address[] = [];
  address: Address | undefined;
  street = "";
  houseNumber = "";
  postcode = "";

  constructor(private profileService: ProfileService, private router: Router) {
  }

  ngOnInit(): void {
    this.getUsers()
    this.getAddresses()

  }

  async getUsers(): Promise<void> {
    this.users = await this.profileService.getUsers().toPromise();
    console.log(this.users);
    this.user = this.users[0];
    this.username = this.user.username;
    this.email = this.user.email;
    if (this.user.pharmacist) {
      this.isPharmacist = "Ja";
    } else {
      this.isPharmacist = "Nein";
    }


  }

  async getAddresses(): Promise<void> {

    this.addresses = await this.profileService.getAddresses().toPromise();
    console.log(this.addresses)
    this.addresses.forEach(address => {
      if (address.addressUsers[0].username == this.username) {
        this.street = address.street;
        this.houseNumber = address.houseNumber;
        this.postcode = address.postcode;
      }
    })
  }
}
