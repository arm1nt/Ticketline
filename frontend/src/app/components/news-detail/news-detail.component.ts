import {Component, OnInit} from '@angular/core';
import {News} from '../../dtos/news';
import {ActivatedRoute} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {NewsService} from '../../services/news.service';

@Component({
  selector: 'app-news-detail',
  templateUrl: './news-detail.component.html',
  styleUrls: ['./news-detail.component.scss']
})
export class NewsDetailComponent implements OnInit {
  news: News;

  constructor(
    private authService: AuthService,
    private newsService: NewsService,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.loadNews(Number(this.route.snapshot.paramMap.get('id')));
  }

  toTimesStampSubtitle(date: Date): string {
    return 'Posted on ' + new Date(date).toLocaleDateString() + ' at ' + new Date(date).toLocaleTimeString();
  }

  private loadNews(id: number) {
    this.newsService.getNewsById(id).subscribe({
      next: news => {
        this.news = news;
      },
      error: error => {
        console.log(error);
      }
    });
  }

}
