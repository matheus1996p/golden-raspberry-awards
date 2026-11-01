import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AwardIntervals, Movie } from '../models/awards.models';

@Injectable({ providedIn: 'root' })
export class AwardsApi {
  private readonly http = inject(HttpClient);

  awardIntervals(): Observable<AwardIntervals> {
    return this.http.get<AwardIntervals>('/api/producers/award-intervals');
  }

  movies(winner?: boolean): Observable<Movie[]> {
    let params = new HttpParams();
    if (winner !== undefined) {
      params = params.set('winner', winner);
    }
    return this.http.get<Movie[]>('/api/movies', { params });
  }

  movie(id: number): Observable<Movie> {
    return this.http.get<Movie>(`/api/movies/${id}`);
  }
}
