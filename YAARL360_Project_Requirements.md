# YAARL 360 — Project Requirements Document

**Project:** YAARL 360 — Sri Lanka's Home Services Super App  
**Version:** 1.0  
**Date:** June 2026  
**Prepared by:** YAARL 360 (Pvt) Ltd

---

## 1. Project Overview

YAARL 360 is an on-demand home services platform for Sri Lanka. Customers can book verified professionals for home repairs, cleaning, maintenance and more — instantly or on a schedule. The platform includes a customer mobile app, a worker mobile app, a public website and an admin panel.

---

## 2. Goals

- Allow homeowners to book home services in under 5 minutes
- Connect verified, background-checked workers to customers
- Support both on-demand (emergency) and scheduled bookings
- Offer monthly subscription maintenance plans
- Provide real-time live tracking of the assigned worker
- Enable in-app payments and instant invoicing

---

## 3. Services Covered

| # | Service | Description |
|---|---------|-------------|
| 1 | Plumbing | Leaks, blockages, emergency burst-pipe response |
| 2 | Electrical | Wiring faults, breaker trips, certified installations |
| 3 | Deep Cleaning | Full property, kitchen or bathroom cleans |
| 4 | AC Service | Servicing, gas top-ups, breakdown repair |
| 5 | Painting | Interior and exterior painting |
| 6 | Handyman | Furniture assembly, mounting, general fixes |
| 7 | Garden Care | Lawn trimming, hedge shaping, seasonal maintenance |
| 8 | Pest Control | Safe certified treatment for termites, rodents, insects |

---

## 4. Tech Stack

| Layer | Technology |
|-------|-----------|
| Customer App | Flutter |
| Worker App | Flutter |
| Backend API | Django (Python) |
| Database | MySQL |
| Website (Public) | React.js |
| Admin Panel | React.js |

---

## 5. Platform Components

### 5.1 Customer Mobile App (Flutter)

#### Screens
- Splash / Onboarding
- Login / Register (Phone OTP)
- Home — service categories grid
- Service Detail — description, pricing, estimated time
- Booking Flow — select date/time, add notes, confirm location
- Live Tracking — map view of worker en route
- My Bookings — upcoming and history
- Subscription Plans — Basic / Premium
- Profile & Settings
- In-App Chat with worker
- Payment & Invoice

#### Key Features
- On-demand (emergency) and scheduled booking
- Real-time worker location tracking (map)
- Push notifications (booking confirmed, worker on the way, job done)
- Subscription plan management (Basic Rs. 3,500/mo · Premium Rs. 8,500/mo)
- In-app payment (card, mobile wallet)
- Rating and review after each job
- Digital invoice download

---

### 5.2 Worker Mobile App (Flutter)

#### Screens
- Login / Register
- Dashboard — available / busy toggle
- Job Requests — accept or decline incoming jobs
- Active Job — customer info, navigation, job checklist
- Earnings Summary
- Profile & Documents (license, ID upload)

#### Key Features
- Real-time job request push alerts
- GPS location sharing while on a job
- Mark job stages: On the way → Arrived → In Progress → Done
- In-app chat with customer
- Earnings history and payout summary

---

### 5.3 Backend API (Django + MySQL)

#### Core Modules
- User Management (customers and workers, OTP auth)
- Service Catalogue
- Booking Engine (on-demand and scheduled)
- Worker Matching (nearest available, skill match)
- Real-time Location (WebSocket or polling)
- Payment Gateway Integration (client-managed, usage-based)
- Push Notification Service (client-managed, usage-based)
- Subscription Management
- Rating & Review Engine
- Admin REST API
- Invoice Generation

#### Third-Party Services (Client-Side / Usage-Based)
The following are integrated by the client under their own accounts. Costs are usage-based and not included in the development budget:

| Service | Purpose |
|---------|---------|
| Maps / Geolocation API | Live tracking, address lookup |
| SMS / OTP Gateway | Phone verification |
| Payment Gateway | In-app card and wallet payments |
| Push Notification Service | Job alerts to customers and workers |
| Cloud Storage | Profile photos, documents |

---

### 5.4 Public Website (React.js)

- Fully responsive landing page (desktop, tablet, mobile)
- 3D animated hero section (Three.js — floating house island)
- 8 service cards with individual animated 3D models
- How It Works section (4-step process)
- Monthly subscription pricing (Basic / Premium)
- Call-to-action / waitlist signup
- SEO-ready structure

---

### 5.5 Admin Panel (React.js)

- Dashboard — bookings, revenue, active workers
- User Management (customers and workers)
- Booking Management — view, assign, cancel
- Worker Verification — approve documents
- Service Catalogue Management
- Subscription Plan Management
- Promotions & Coupons
- Reports & Analytics

---

## 6. Subscription Plans

| Plan | Price | Includes |
|------|-------|---------|
| Basic | Rs. 3,500 / month | 1 deep clean/month, 2 handyman checkups, 12-month calendar, auto-renew |
| Premium / Villa | Rs. 8,500 / month | Full property clean every 6 weeks, unlimited 24/7 priority checkups, visitation fees waived, top-of-queue dispatch |

---

## 7. Booking Flow

```
Customer picks service
    ↓
Selects date / time / location
    ↓
System matches nearest available worker (< 30 seconds)
    ↓
Worker accepts → Customer notified
    ↓
Live map tracking (worker en route)
    ↓
Job completed → Customer rates → Invoice sent
```

---

## 8. Delivery Timeline — Phase 1 (12 Weeks)

| Milestone | Weeks | Deliverables | Payment |
|-----------|-------|-------------|---------|
| M1 — Foundation | 1–3 | Project setup, backend scaffold, DB schema, basic auth, public website | Rs. 2,00,000 (20%) |
| M2 — Core Booking | 4–6 | Booking engine, worker matching, customer app screens (home → booking → tracking) | Rs. 2,50,000 (25%) |
| M3 — Worker App | 7–8 | Worker app — job requests, active job flow, GPS sharing | Rs. 2,00,000 (20%) |
| M4 — Payments & Subscriptions | 9–10 | Payment integration, subscription plans, invoicing, admin panel | Rs. 2,00,000 (20%) |
| M5 — QA & Launch | 11–12 | Full QA, bug fixes, app store submission, deployment | Rs. 1,50,000 (15%) |

---

## 9. Budget — Phase 1

| Item | Cost (Rs.) |
|------|-----------|
| Development | 7,50,000 |
| UI/UX Design | 75,000 |
| QA & Testing | 75,000 |
| Deployment & Support | 50,000 |
| Contingency | 50,000 |
| **Total** | **10,00,000** |

> Third-party API costs (maps, SMS, payments, push) are usage-based and billed directly to the client under their own accounts.

---

## 10. Client Responsibilities

- Provide brand assets (logo, colour guidelines)
- Register and manage third-party API accounts (maps, SMS, payment gateway, push notifications)
- Provide test devices (Android and iOS) for UAT
- Assign one point-of-contact for feedback during each milestone
- Approve milestone deliverables within 5 business days

---

## 11. Phase 2 Enhancements (Future)

- Multi-city expansion across Sri Lanka
- In-app video consultation before booking
- Worker performance badges and leaderboard
- Corporate / property management accounts
- Loyalty rewards program
- Automated yearly home health report

---

## 12. Non-Functional Requirements

| Requirement | Target |
|-------------|--------|
| Worker match response | < 30 seconds |
| App load time | < 3 seconds on 4G |
| Uptime | 99.5% |
| Platform support | Android 8+ · iOS 14+ |
| Concurrent users (Phase 1) | Up to 500 |
| Data security | HTTPS, encrypted passwords, JWT auth |

---

*YAARL 360 (Pvt) Ltd · Sri Lanka · 2026*
