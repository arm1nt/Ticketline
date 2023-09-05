import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {NewsService} from '../../services/news.service';
import {News} from '../../dtos/news';


export enum NewsShowMode {
  unread,
  read,
  all
}

@Component({
  selector: 'app-news',
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.scss']
})
export class NewsComponent implements OnInit {

  news: News[];
  mode: NewsShowMode = NewsShowMode.unread;

  modes: any = Object.values(NewsShowMode).filter(v => isNaN(Number(v)));

  upToDate: boolean;

  constructor(
    private authService: AuthService,
    private newsService: NewsService,
  ) {
  }


  get modeIsAll(): boolean {
    return this.mode === NewsShowMode.all;
  }

  get modeIsRead(): boolean {
    return this.mode === NewsShowMode.read;
  }

  get modeIsUnread(): boolean {
    return this.mode === NewsShowMode.unread;
  }

  modeFromName(name: string): NewsShowMode {
    return NewsShowMode[name];
  }

  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  toTimesStampSubtitle(date: Date): string {
    return 'Posted on ' + new Date(date).toLocaleDateString() + ' at ' + new Date(date).toLocaleTimeString();
  }

  getNews(): News[] {
    return this.news;
  }

  ngOnInit(): void {
    if(!this.isLoggedIn()) {
      this.mode = NewsShowMode.all;
    }
    this.loadNews();
  }

  loadNews() {
    switch (this.mode) {
      case NewsShowMode.all:
        this.loadAllNews();
        break;
      case NewsShowMode.unread:
        this.loadUnreadNews();
        break;
      case NewsShowMode.read:
        this.loadReadNews();
        break;
      default:
        break;
    }
  }

  loadAllNews() {
    this.mode = NewsShowMode.all;
    this.newsService.getAllNews().subscribe({
      next: (news: News[]) => {
        this.news = news;
        this.upToDate = false;
      },
      error: error => {
        console.log(error);
      }
    });
  }

  loadReadNews() {
    this.mode = NewsShowMode.read;
    this.newsService.getReadNews().subscribe({
      next: (news: News[]) => {
        this.news = news;
        this.upToDate = false;
      },
      error: error => {
        console.log(error);
      }
    });
  }

  loadUnreadNews() {
    this.mode = NewsShowMode.unread;
    this.newsService.getUnreadNews().subscribe({
      next: (news: News[]) => {
        this.news = news;
        if (this.news.length === 0) {
          this.upToDate = true;
        }
      },
      error: error => {
        console.log(error);
      }
    });
  }
}
