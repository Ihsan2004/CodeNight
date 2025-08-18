import axios from 'axios';

const API_BASE_URL = 'http://localhost:8000/api';

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// API endpoints
export const endpoints = {
  catalog: '/catalog',
  simulate: '/simulate',
  recommendation: '/recommendation',
  checkout: '/checkout',
  users: '/users',
} as const;

// Types based on your backend DTOs
export interface Country {
  countryCode: string;
  countryName: string;
  region: string;
}

export interface RoamingRate {
  countryCode: string;
  dataPerMb: number;
  voicePerMin: number;
  smsPerMsg: number;
  currency: string;
}

export interface RoamingPack {
  packId: number;
  name: string;
  coverage: string;
  coverageType: string;
  dataGb: number;
  voiceMin: number;
  sms: number;
  price: number;
  validityDays: number;
  currency: string;
}

export interface CatalogResponse {
  countries: Country[];
  rates: RoamingRate[];
  packs: RoamingPack[];
}

export interface TripInput {
  countryCode: string;
  startDate: string;
  endDate: string;
}

export interface ProfileInput {
  avgDailyMb: number;
  avgDailyMin: number;
  avgDailySms: number;
}

export interface SimulationRequest {
  userId: number;
  trips: TripInput[];
  profile: ProfileInput;
}

export interface SimulationResponse {
  summary: {
    days: number;
    totalNeed: {
      gb: number;
      min: number;
      sms: number;
    };
  };
  options: Array<{
    kind: string;
    packId?: number;
    nPacks: number;
    totalCost: number;
    currency: string;
    coverageHit: boolean;
    validityOk: boolean;
    overflow?: {
      overMbCost: number;
      overMinCost: number;
      overSmsCost: number;
    };
  }>;
  warnings: string[];
}

export interface RecommendationResponse {
  top3: Array<{
    label: string;
    totalCost: number;
    explanation: string;
    details: any;
  }>;
  rationale: string;
}
