import {BrowserModule} from '@angular/platform-browser';
import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {MaterialFileInputModule} from 'ngx-material-file-input';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {httpInterceptorProviders} from './interceptors';
import {EventHallComponent} from './components/event-hall/event-hall.component';
import {SeatComponent} from './components/event-hall/sector/seating/seat/seat.component';
import {ChooseSpotsComponent} from './components/choose-spots/choose-spots.component';
import {SeatLegendComponent} from './components/event-hall/seat-legend/seat-legend.component';
import {MaterialModule} from './material.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatSelectModule} from '@angular/material/select';
import {MatInputModule} from '@angular/material/input';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatFormFieldModule} from '@angular/material/form-field';
import {SuccessComponent} from './components/success/success.component';
import {MatCardModule} from '@angular/material/card';
import {OrdersComponent} from './components/orders/orders.component';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatDividerModule} from '@angular/material/divider';
import {EventComponent} from './components/event/event.component';
import {NewsComponent} from './components/news/news.component';
import {SearchComponent} from './components/search/search.component';
import {SearchPageComponent} from './components/search-page/search-page.component';
import {ProfileComponent} from './components/profile/profile.component';
import {EditProfileComponent} from './components/edit-profile/edit-profile.component';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {
  ChangePasswordDialogComponent
} from './components/edit-profile/edit-dialogs/change-password-dialog/change-password-dialog.component';
import {MatDialogModule} from '@angular/material/dialog';
import {NewsDetailComponent} from './components/news-detail/news-detail.component';
import {EventDetailComponent} from './components/event-detail/event-detail.component';
import {EmailValidatorDirective} from './directives/email-validator.directive';
import {BlankValidatorDirective} from './directives/blank-validator.directive';
import {NewsCreateComponent} from './components/news-create/news-create.component';
import {RegisterComponent} from './components/register/register.component';
import {
  DeleteAccountDialogComponent
} from './components/edit-profile/edit-dialogs/delete-account-dialog/delete-account-dialog/delete-account-dialog.component';
import {SectorComponent} from './components/event-hall/sector/sector.component';
import {StandingComponent} from './components/event-hall/sector/standing/standing.component';
import {SeatingComponent} from './components/event-hall/sector/seating/seating.component';
import {
  DialogChooseSpotsComponent
} from './components/event-hall/sector/standing/dialog-choose-spots/dialog-choose-spots.component';
import {UserCreateComponent} from './components/user-create/user-create.component';
import {DebounceClickDirective} from './directives/debounce-click.directive';
import {LockedUsersComponent} from './components/locked-users/locked-users.component';
import {MatTableModule} from '@angular/material/table';
import {UnlockUserDialogComponent} from './components/locked-users/unlock-user-dialog/unlock-user-dialog.component';
import {EventCreateComponent} from './components/event-create/event-create.component';
import {PerformanceCreateComponent} from './components/performance-create/performance-create.component';
import {PerformersAddComponent} from './components/performers-add/performers-add.component';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {CreateFloorplanComponent} from './components/create-floorplan/create-floorplan.component';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {CreateEventhallComponent} from './components/create-eventhall/create-eventhall.component';
import {MatChipsModule} from '@angular/material/chips';
import {ColorPickerModule} from 'ngx-color-picker';
import {
  StandingSectorDialogComponent
} from './components/create-floorplan/floorplan-dialogs/standing-dialog/standing-sector-dialog/standing-sector-dialog.component';
import {
  SeatingSectorDialogComponent
} from './components/create-floorplan/floorplan-dialogs/seating-dialog/seating-sector-dialog/seating-sector-dialog.component';
import {DragDropModule} from '@angular/cdk/drag-drop';
import {CreateLocationComponent} from './components/create-location/create-location.component';
import {LocationDetailComponent} from './components/location-detail/location-detail.component';
import {
  HelpDialogComponent
} from './components/create-floorplan/floorplan-dialogs/tool-description/help-dialog/help-dialog.component';
import {TicketOverviewComponent} from './components/ticket-overview/ticket-overview.component';
import {UserOverviewComponent} from './components/user-overview/user-overview.component';
import {MatPaginatorModule} from '@angular/material/paginator';
import {ResetPasswordComponent} from './components/reset-password/reset-password.component';
import {RequestEmailComponent} from './components/reset-password/request-email/request-email.component';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {LockUserDialogComponent} from './components/user-overview/lock-user-dialog/lock-user-dialog.component';
import {LayoutAddComponent} from './components/layout-add/layout-add.component';
import {OrderEditDialogComponent, OrdersEditComponent} from './components/orders-edit/orders-edit.component';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {
  ResetPasswordDialogComponent
} from './components/user-overview/reset-password-dialog/reset-password-dialog.component';
import {AdminPanelDialogComponent} from './components/admin-panel-dialog/admin-panel-dialog.component';
import {
  LocationPerformanceListComponent
} from './components/location-performance-list/location-performance-list.component';
import {MatSliderModule} from '@angular/material/slider';
import {EventsTop10Component} from './components/events-top10/events-top10.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    EventHallComponent,
    SeatComponent,
    ChooseSpotsComponent,
    SeatLegendComponent,
    SearchComponent,
    SuccessComponent,
    OrdersComponent,
    OrdersEditComponent,
    EventComponent,
    NewsComponent,
    SearchPageComponent,
    ProfileComponent,
    EditProfileComponent,
    ChangePasswordDialogComponent,
    NewsDetailComponent,
    EventDetailComponent,
    NewsCreateComponent,
    RegisterComponent,
    EmailValidatorDirective,
    BlankValidatorDirective,
    DeleteAccountDialogComponent,
    SectorComponent,
    StandingComponent,
    SeatingComponent,
    DialogChooseSpotsComponent,
    UserCreateComponent,
    DebounceClickDirective,
    LockedUsersComponent,
    UnlockUserDialogComponent,
    DeleteAccountDialogComponent,
    EventCreateComponent,
    PerformanceCreateComponent,
    PerformersAddComponent,
    CreateFloorplanComponent,
    CreateEventhallComponent,
    StandingSectorDialogComponent,
    SeatingSectorDialogComponent,
    CreateLocationComponent,
    LocationDetailComponent,
    HelpDialogComponent,
    TicketOverviewComponent,
    UserOverviewComponent,
    ResetPasswordComponent,
    RequestEmailComponent,
    LockUserDialogComponent,
    ResetPasswordDialogComponent,
    LayoutAddComponent,
    OrderEditDialogComponent,
    AdminPanelDialogComponent,
    LocationPerformanceListComponent,
    EventsTop10Component
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        ReactiveFormsModule,
        HttpClientModule,
        FormsModule,
        BrowserAnimationsModule,
        MaterialModule,
        MatToolbarModule,
        MatFormFieldModule,
        MatSelectModule,
        MatInputModule,
        MatListModule,
        MatIconModule,
        MatButtonModule,
        MatCardModule,
        MatExpansionModule,
        MatDividerModule,
        MatSnackBarModule,
        MatDialogModule,
        MaterialFileInputModule,
        MatTableModule,
        MatButtonToggleModule,
        MatSlideToggleModule,
        MatChipsModule,
        ColorPickerModule,
        DragDropModule,
        MatProgressSpinnerModule,
        MatPaginatorModule,
        MatCheckboxModule,
        MatSliderModule
    ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
