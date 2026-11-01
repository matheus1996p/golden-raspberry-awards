export interface ProducerAwardInterval {
  producer: string;
  interval: number;
  previousWin: number;
  followingWin: number;
}

export interface AwardIntervals {
  min: ProducerAwardInterval[];
  max: ProducerAwardInterval[];
}

export interface Movie {
  id: number;
  year: number;
  title: string;
  studios: string[];
  producers: string[];
  winner: boolean;
}
