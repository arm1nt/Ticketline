import {Component, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {News} from '../../dtos/news';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';
import {NewsService} from '../../services/news.service';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-news-create',
  templateUrl: './news-create.component.html',
  styleUrls: ['./news-create.component.scss']
})
export class NewsCreateComponent implements OnInit {

  news: News = {
    id: 0,
    publishedAt: new Date(),
    title: '',
    text: '',
    image: null
  };

  constructor(
    private router: Router,
    private authService: AuthService,
    private newsService: NewsService,
    private _snackBar: MatSnackBar
  ) {
  }

  public get heading(): string {
    return 'Create a news entry';
  }

  public get uploadButtonText(): string {
    if (this.news.image != null) {
      return 'Upload completed';
    }
    return 'Upload Image';
  }

  public onSubmit(form: NgForm): void {
    if (form.valid && this.isAdmin()) {
      console.log(this.news);
      delete this.news.publishedAt;
      delete this.news.id;
      this.news.title = this.news.title.trim();
      this.news.text = this.news.text.trim();
      const observable = this.newsService.create(this.news);
      observable.subscribe({
        next: _data => {
          this.openSnackBar(`News "${this.news.title}" successfully published.`, 'Dismiss', true);
          this.router.navigate(['/news']);
        },
        error: error => {
          console.log(error);
          this.openSnackBar(`News "${this.news.title}" couldn't be published.`, 'Dismiss', false);
        }
      });
    }
  }

  hasImageUploaded(): boolean {
    return this.news.image !== null;
  }

  onFileSelected() {
    const inputNode: any = document.querySelector('#file');
    if (typeof (FileReader) !== 'undefined') {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.news.image = e.target.result;
      };
      reader.readAsDataURL(inputNode.files[0]);
    }
  }

  openSnackBar(message: string, action: string, success: boolean) {
    if (success) {
      this._snackBar.open(message, action,
        {horizontalPosition: 'right', verticalPosition: 'top', duration: 3000, panelClass: ['snack-success']});
    } else {
      this._snackBar.open(message, action,
        {horizontalPosition: 'right', verticalPosition: 'top', duration: 3000, panelClass: ['snack-error']});
    }
  }

  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  ngOnInit(): void {
    if (!this.isAdmin()) {
      this.router.navigate(['/news']);
    }
  }

}
