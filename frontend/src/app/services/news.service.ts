import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {News} from '../dtos/news';

@Injectable({
  providedIn: 'root'
})
export class NewsService {

  private newsBaseUri: string = this.globals.backendUri + '/news';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  create(news: News): Observable<News> {
    return this.httpClient.post<News>(this.newsBaseUri, news);
  }

  getUnreadNews(): Observable<News[]> {
    return this.httpClient.get<News[]>(this.newsBaseUri + '/new');
  }

  getReadNews(): Observable<News[]> {
    return this.httpClient.get<News[]>(this.newsBaseUri + '/old');
  }

  getAllNews(): Observable<News[]> {
    return this.httpClient.get<News[]>(this.newsBaseUri);
  }

  getNewsById(id: number): Observable<News> {
    return this.httpClient.get<News>(this.newsBaseUri + '/' + id);
  }
}
