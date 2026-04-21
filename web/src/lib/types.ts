export interface AuthResponse {
  accessToken: string;
  expiresIn: number;
}

export interface UserResponse {
  id: string;
  email: string;
  displayName: string;
  emailVerified: boolean;
  subscriptionStatus: string;
  pro: boolean;
  remindersEnabled: boolean;
  createdAt: string;
}

export interface CheckInResponse {
  id: string;
  weekStart: string;
  wins: string | null;
  friction: string | null;
  energyRating: number | null;
  signalMoment: string | null;
  intentions: string | null;
  completed: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CheckInRequest {
  wins?: string;
  friction?: string;
  energyRating?: number;
  signalMoment?: string;
  intentions?: string;
  completed?: boolean;
}

export interface InsightResponse {
  id: string;
  checkInId: string;
  content: string;
  createdAt: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  last: boolean;
}

export interface ApiError {
  error: string;
  status: number;
}
