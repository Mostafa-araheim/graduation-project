import { UserRole } from './enums.model';

// ── Login Flow ──────────────────────────────────────────

export interface LoginStartRequest {
  email: string;
  role: UserRole;
}

export interface LoginStartResponse {
  loginId: string;
  message: string;
}

export interface LoginVerifyRequest {
  loginId: string;
  code: string;
}

export interface AuthVerification {
  userId: number;
  email: string;
}

// ── Signup Flow ─────────────────────────────────────────

export interface SignupStartRequest {
  email: string;
  name: string;
  role: UserRole;
}

export interface SignupStartResponse {
  signupId: string;
  message: string;
}

export interface SignupVerifyRequest {
  signupId: string;
  code: string;
}
