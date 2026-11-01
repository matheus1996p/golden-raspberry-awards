import { Routes } from '@angular/router';
import { IntervalsPage } from './pages/intervals/intervals-page';
import { MovieDetailPage } from './pages/movies/movie-detail-page';
import { MoviesPage } from './pages/movies/movies-page';

export const routes: Routes = [
  { path: '', component: IntervalsPage },
  { path: 'movies', component: MoviesPage },
  { path: 'movies/:id', component: MovieDetailPage },
  { path: '**', redirectTo: '' },
];
