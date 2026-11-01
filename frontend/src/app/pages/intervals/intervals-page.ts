import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { AwardIntervals } from '../../models/awards.models';
import { AwardsApi } from '../../services/awards-api';

@Component({
  selector: 'app-intervals-page',
  imports: [RouterLink],
  templateUrl: './intervals-page.html',
})
export class IntervalsPage {
  private readonly api = inject(AwardsApi);

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly intervals = signal<AwardIntervals | null>(null);
  readonly movieCount = signal(0);
  readonly winnerCount = signal(0);
  readonly firstYear = signal<number | null>(null);
  readonly lastYear = signal<number | null>(null);

  constructor() {
    this.api.awardIntervals().pipe(takeUntilDestroyed()).subscribe({
      next: (intervals) => {
        this.intervals.set(intervals);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Não foi possível carregar os intervalos. Confira se a API está em http://localhost:8080.');
        this.loading.set(false);
      },
    });

    this.api.movies().pipe(takeUntilDestroyed()).subscribe({
      next: (movies) => {
        this.movieCount.set(movies.length);
        this.winnerCount.set(movies.filter((movie) => movie.winner).length);
        if (movies.length > 0) {
          this.firstYear.set(movies[0].year);
          this.lastYear.set(movies[movies.length - 1].year);
        }
      },
      error: () => undefined,
    });
  }

  yearsLabel(interval: number): string {
    return interval === 1 ? '1 ano' : `${interval} anos`;
  }
}
