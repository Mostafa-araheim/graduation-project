export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
  error: ErrorResponse | null;
  timestamp: string;
}

export interface ErrorResponse {
  code?: string;
  details?: string[];
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface PaginationParams {
  page?: number;
  size?: number;
  sort?: string;
}
