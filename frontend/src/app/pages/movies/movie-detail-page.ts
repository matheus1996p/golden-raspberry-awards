import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { catchError, EMPTY, filter, map, switchMap, tap } from 'rxjs';
import { Movie } from '../../models/awards.models';
import { AwardsApi } from '../../services/awards-api';

@Component({
  selector: 'app-movie-detail-page',
  imports: [RouterLink],
  templateUrl: './movie-detail-page.html',
})
export class MovieDetailPage {
  private readonly api = inject(AwardsApi);
  private readonly routeId = toSignal(
    inject(ActivatedRoute).paramMap.pipe(map((params) => Number(params.get('id')))),
    { initialValue: Number.NaN },
  );

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly movie = signal<Movie | null>(null);

  constructor() {
    toObservable(this.routeId)
      .pipe(
        filter((id) => Number.isFinite(id)),
        tap(() => {
          this.loading.set(true);
          this.error.set(null);
        }),
        switchMap((id) =>
          this.api.movie(id).pipe(
            tap((movie) => {
              this.movie.set(movie);
              this.loading.set(false);
            }),
            catchError((error: unknown) => {
              this.movie.set(null);
              this.loading.set(false);
              if (error instanceof HttpErrorResponse && error.status === 404) {
                this.error.set('Esse filme não está na lista.');
              } else {
                this.error.set('Não foi possível carregar o filme.');
              }
              return EMPTY;
            }),
          ),
        ),
        takeUntilDestroyed(),
      )
      .subscribe();
  }
}
