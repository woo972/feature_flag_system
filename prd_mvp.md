# Feature Flag System MVP - Product Requirements Document

## Project Overview
The MVP of the feature flag system will enable basic dynamic control over application features through an admin dashboard. It will provide essential mechanisms for determining feature availability based on predefined criteria. The system includes a feature flag SDK, a dashboard for admin operations, and a feature flag store for maintaining feature flag definitions.

## Goals and Objectives
- Enable basic feature toggling (ON/OFF) without requiring application redeployment.
- Provide an admin dashboard for managing feature flags (create, update, delete).
- Offer an SDK for retrieving and caching feature flag data.
- Ensure scalable and low-latency performance for evaluating feature flags.

## Key Features

### Feature Flag SDK

#### Functions:
- Fetch and cache feature flag data from the core.
- Evaluate feature flag status based on simple criteria (e.g., user role, platform).

#### Criteria Supported in MVP:
- User roles: (e.g., non_member, member).
- Platform: (e.g., IOS, ANDROID).

### Feature Flag Dashboard

#### Functions:
- Create, update, delete feature flags.
- View all feature flags and their statuses (ON/OFF).

#### User Management:
- Role-based access (Admin, Viewer).

### Feature Flag Core

- Acts as an intermediary between the SDK and the feature flag store.

#### Functions:
- Retrieve feature flag data from the store.
- Cache frequently accessed feature flags for faster evaluations.

#### Supported API Endpoints:
- GET /api/v1/evaluate: Evaluate feature flag for a request.
- GET /api/v1/cache/refresh: Refresh cached feature flags.

### Feature Flag Store

- Stores feature flag definitions with minimal schema.

#### Schema for MVP:
```json
{
  "id": "1234",
  "name": "new_ui_feature",
  "status": "ON", // OFF
  "criteria": {
    "user_roles": ["member"],
    "platforms": ["IOS"]
  },
  "created_at": "2024-12-08T10:00:00Z"
}
