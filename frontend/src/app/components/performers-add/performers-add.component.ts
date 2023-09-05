import { Component, OnInit } from '@angular/core';
import {EventCreation} from '../../enums/event-creation';
import {NgForm} from '@angular/forms';
import {Performer} from '../../dtos/performer';
import {AuthService} from '../../services/auth.service';
import {EventService} from '../../services/event.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {PerformerService} from '../../services/performer.service';
import {Router} from '@angular/router';
import {ArtistService} from '../../services/artist.service';
import {BandService} from '../../services/band.service';
import {Band} from '../../dtos/band';
import {Artist} from '../../dtos/artist';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-performers-add',
  templateUrl: './performers-add.component.html',
  styleUrls: ['./performers-add.component.scss']
})
export class PerformersAddComponent implements OnInit {

  selectedOption: string;

  performer: Performer = {
    id: null,
    performerName: null,
    firstName: null,
    lastName: null,
    events: null
  };

  artist: Artist = {
    id: null,
    performerName: null,
    firstName: null,
    lastName: null,
    events: null,
    bands: null
  };

  band: Band = {
    id: null,
    performerName: null,
    artists: null,
    events: null
  };
  availableArtists: Observable<Artist[]>;



  constructor(private authService: AuthService,
              private eventService: EventService,
              private _snackBar: MatSnackBar,
              private performerService: PerformerService,
              private artistService: ArtistService,
              private bandService: BandService,
              private router: Router) { }

  public get heading(): string {
    if(this.selectedOption === 'artist'){
      return 'Add Artist';
    } else if(this.selectedOption === 'band'){
      return 'Add Band';
    } else {
      return 'Add Performer';
    }
  }

  ngOnInit(): void {
    this.resetForm();
    this.availableArtists = this.artistService.getAll();
  }

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      this.performer.performerName = this.performer.performerName.trim();
      this.performer.firstName = this.performer.firstName.trim();
      this.performer.lastName = this.performer.lastName.trim();
      let observable;
      if(this.selectedOption==='artist'){
        observable = this.artistService.addArtist(this.artist);
      } else {
        observable = this.bandService.addBand(this.band);
      }
      observable.subscribe({
        next: data => {
          if(this.selectedOption==='artist'){
            this.openSnackBar(`Performer "${this.artist.performerName}" successfully created.`, 'Dismiss');
            this.resetArtist();
          } else {
            this.openSnackBar(`Performer "${this.band.performerName}" successfully created.`, 'Dismiss');
            this.resetBand();
          }
          this.performer = data;
          console.log(this.performer);
          this.selectedOption=null;
          //this.router.navigate(['/performers']);
        },
        error: error => {
          console.log(error);
          this.openSnackBar(`Performer couldn't be created: ${error.error}`, 'Dismiss');
        }
      });
    }
  }

  openSnackBar(message: string, action: string) {
    this._snackBar.open(message, action, {horizontalPosition: 'right', verticalPosition: 'top', duration: 3000});
  }

  resetForm(): void {
    this.performer.performerName = '';
    this.performer.firstName = '';
    this.performer.lastName = '';
  }

  resetArtist(): void {
    this.artist.performerName = '';
    this.artist.firstName = '';
    this.artist.lastName = '';
  }

  resetBand(): void {
    this.band.performerName = '';
    this.band.artists = null;
  }


}
