import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {ChooseSpotsComponent} from './components/choose-spots/choose-spots.component';
import {SuccessComponent} from './components/success/success.component';
import {OrdersComponent} from './components/orders/orders.component';
import {EventComponent} from './components/event/event.component';
import {NewsComponent} from './components/news/news.component';
import {SearchPageComponent} from './components/search-page/search-page.component';
import {ProfileComponent} from './components/profile/profile.component';
import {EditProfileComponent} from './components/edit-profile/edit-profile.component';
import {NewsDetailComponent} from './components/news-detail/news-detail.component';
import {EventDetailComponent} from './components/event-detail/event-detail.component';
import {NewsCreateComponent} from './components/news-create/news-create.component';
import {RegisterComponent} from './components/register/register.component';
import {UserCreateComponent} from './components/user-create/user-create.component';
import {EventCreateComponent} from './components/event-create/event-create.component';
import {PerformanceCreateComponent} from './components/performance-create/performance-create.component';
import {LockedUsersComponent} from './components/locked-users/locked-users.component';
import {PerformersAddComponent} from './components/performers-add/performers-add.component';
import {CreateFloorplanComponent} from './components/create-floorplan/create-floorplan.component';
import {CreateEventhallComponent} from './components/create-eventhall/create-eventhall.component';
import {CreateLocationComponent} from './components/create-location/create-location.component';
import {UserOverviewComponent} from './components/user-overview/user-overview.component';
import {RequestEmailComponent} from './components/reset-password/request-email/request-email.component';
import {ResetPasswordComponent} from './components/reset-password/reset-password.component';
import {LayoutAddComponent} from './components/layout-add/layout-add.component';
import {OrdersEditComponent} from './components/orders-edit/orders-edit.component';
import {
  LocationPerformanceListComponent
} from './components/location-performance-list/location-performance-list.component';
import {EventsTop10Component} from './components/events-top10/events-top10.component';

const routes: Routes = [
  {path: '', redirectTo: 'news', pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'create-user', canActivate: [AuthGuard], component: UserCreateComponent},
  {path: 'request-password-reset', component: RequestEmailComponent},
  {path: 'reset-password', component: ResetPasswordComponent},
  {path: 'profile', canActivate: [AuthGuard], component: ProfileComponent},
  {path: 'edit', canActivate: [AuthGuard], component: EditProfileComponent},
  {path: 'locked', canActivate: [AuthGuard], component: LockedUsersComponent},
  {path: 'users', canActivate: [AuthGuard], component: UserOverviewComponent},
  {path: 'success/:id', canActivate: [AuthGuard], component: SuccessComponent},
  {
    path: 'orders', children: [
      {path: '', canActivate: [AuthGuard], component: OrdersComponent},
      {path: ':id', canActivate: [AuthGuard], component: OrdersEditComponent},
    ]
  },
  {
    path: 'news', children: [
      {path: '', component: NewsComponent},
      {path: 'create', canActivate: [AuthGuard], component: NewsCreateComponent},
      {path: ':id', canActivate: [AuthGuard], component: NewsDetailComponent},
    ]
  },
  {
    path: 'event', children: [
      {path: '', canActivate: [AuthGuard], component: EventComponent},
      {path: 'top10', component: EventsTop10Component},
      {path: 'create', canActivate: [AuthGuard], component: EventCreateComponent},
      {path: ':id', canActivate: [AuthGuard], component: EventDetailComponent},
    ]
  },
  {path: 'performance/create', canActivate: [AuthGuard], component: PerformanceCreateComponent},
  {path: 'search', canActivate: [AuthGuard], component: SearchPageComponent},
  {path: 'profile', canActivate: [AuthGuard], component: ProfileComponent},
  {path: 'performance/:id/choose-spots', canActivate: [AuthGuard], component: ChooseSpotsComponent},
  {path: 'edit', canActivate: [AuthGuard], component: EditProfileComponent},
  {path: 'event/:id', component: EventDetailComponent},
  {path: 'locked', canActivate: [AuthGuard], component: LockedUsersComponent},
  {path: 'performers/add', canActivate: [AuthGuard], component: PerformersAddComponent},
  {path: 'floorplan', canActivate: [AuthGuard], component: CreateFloorplanComponent},
  {path: 'eventhall', canActivate: [AuthGuard], component: CreateEventhallComponent},
  {
    path: 'location', children: [
      {path: '', canActivate: [AuthGuard], component: CreateLocationComponent},
      {path: ':id', canActivate: [AuthGuard], component: LocationPerformanceListComponent},
    ]
  },
  {path: 'users', canActivate: [AuthGuard], component: UserOverviewComponent},
  {path: 'performers/add', canActivate: [AuthGuard], component: PerformersAddComponent},
  {
    path: 'performance', children: [
      {path: 'create', canActivate: [AuthGuard], component: PerformanceCreateComponent},
      {path: ':id/choose-spots', canActivate: [AuthGuard], component: ChooseSpotsComponent}
    ]
  },
  {path: 'performers/add', canActivate: [AuthGuard], component: PerformersAddComponent},
  {path: 'search', canActivate: [AuthGuard], component: SearchPageComponent},
  {path: 'layout/add', canActivate: [AuthGuard], component: LayoutAddComponent}
];

// @ts-ignore
@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
