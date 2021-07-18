import {Component, Injectable, OnInit} from '@angular/core';
import {ProfileService, User} from "../services/profile.service";
import {Router} from "@angular/router";
import { Observable } from 'rxjs';
import {InjectableDecoratorHandler} from "@angular/compiler-cli/src/ngtsc/annotations";

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.component.html',
  styleUrls: ['./profile-page.component.css']
})
export class ProfilePageComponent implements OnInit {
  username: Observable<User> | undefined;
  constructor(private profileService: ProfileService,private router: Router) { }

  ngOnInit(): void {
    this.profileService.getUsers();
  }

}
