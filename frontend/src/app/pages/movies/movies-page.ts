import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { Movie } from '../../models/awards.models';
import { AwardsApi } from '../../services/awards-api';

type MovieFilter = 'all' | 'winners' | 'nominees';

@Component({
  selector: 'app-movies-page',
  imports: [RouterLink],
  templateUrl: './movies-page.html',
})
export class MoviesPage {
  private readonly api = inject(AwardsApi);

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly movies = signal<Movie[]>([]);
  readonly query = signal('');
  readonly filter = signal<MovieFilter>('all');

  readonly visibleMovies = computed(() => {
    const term = this.query().trim().toLocaleLowerCase();
    const filter = this.filter();
    return this.movies().filter((movie) => {
      if (filter === 'winners' && !movie.winner) {
        return false;
      }
      if (filter === 'nominees' && movie.winner) {
        return false;
      }
      if (!term) {
        return true;
      }
      const haystack = [movie.title, String(movie.year), ...movie.studios, ...movie.producers]
        .join(' ')
        .toLocaleLowerCase();
      return haystack.includes(term);
    });
  });

  constructor() {
    this.api.movies().pipe(takeUntilDestroyed()).subscribe({
      next: (movies) => {
        this.movies.set(movies);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Não foi possível carregar os filmes. Confira se a API está em http://localhost:8080.');
        this.loading.set(false);
      },
    });
  }

  onQuery(event: Event): void {
    this.query.set((event.target as HTMLInputElement).value);
  }

  setFilter(filter: MovieFilter): void {
    this.filter.set(filter);
  }
}
