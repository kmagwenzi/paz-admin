# ⚡ PRD – Prison Alliance Zimbabwe Admin Portal  
*(Product Requirements Document | v0.1 – 15 Sep 2025)*  
**For:** Kilo Code (VS Code) → OpenRouter Qwen3 Coder 480B A35B (free tier)  
**Mode flow:** Architect → Orchestrator → Code

---

## 1. 🎯 Vision & Value
> “Replace paper forms & WhatsApp chaos with one lightning-fast web hub—so teachers preach, not chase print-outs.”  
**North-Star Metric:** 90 % of prison visits administered **without a single paper requisition** within 6 months.

---

## 2. 🧩 Component Map (Atomic Design)

| Component | Core Job | Initial Complexity | Future Enhancements |
|-----------|----------|--------------------|----------------------|
| **A. Control Panel** | Landing + deep-links + KPI cards | Low | Charts, RBAC, e-mail digests |
| **B. Teachers Task Reports** | Digital “Teaching Task Form” | Medium | Offline PWA, photo evidence |
| **C. Print Requisitions** | Digital “Printing Requisition Form” | Medium | Bulk PDF merge, printer API |
| **D. Student Reg & Attendance** | Class roster + daily sign-in sheet generator | High | QR check-in, biometric fallback |

---

## 3. 👥 Actors & Permissions (Spring Security)

| Actor | Can … | Cannot … |
|-------|-------|----------|
| **Admin** | CRUD all, print, export stats | delete audit logs |
| **Teacher** | create task report, print req, view own classes | view other teachers’ inmates |
| **Prison Liaison** | view class schedules, confirm attendance | edit biblical content |
| **Super-Admin** | manage users, restore backups | none (audit only) |

---

## 4. 🖥️ Front-End Specification (Next.js 14 + Tailwind)

### 4.1 Routing Table
```
/               → Control Panel (A)
/tasks/new      → Teachers Task Form (B)
/tasks/:id      → Task Read-only
/prints/new     → Print Requisition (C)
/prints/:id     → Print Preview (PDF blob)
/classes        → List classes (D)
/classes/new    → Create class + auto-roster
/classes/:id    → Day-by-day attendance grid
/login          → JWT spring gateway
```

### 4.2 Shared UI Components
| Name | Props | Purpose |
|------|-------|---------|
| `<ZimDatePicker />` | `onChange, value` | locks Zim timezone, disables past Sundays (no visits) |
| `<PrintBtn />` | `blobUrl, label` | opens browser print dialog, auto-fit A4 |
| `<LoadShedBanner />` | `stage` | yellow bar: “Grid at Stage 3 – auto-save every 30 s” |
| `<EcoCashTotal />` | `USD, ZWL, rate` | live RBZ mid-rate + 15 % buffer display |

---

## 5. 🔙 Back-End Specification (Spring Boot 3.2 + PostgreSQL 15)

### 5.1 Domain Model (simplified ER)

```mermaid
erDiagram
    Teacher ||--o{ TaskReport : submits
    Teacher ||--o{ PrintReq : requests
    Class ||--o{ Student : contains
    Class ||--o{ Session : schedules
    Session ||--o{ Attendance : records
    Prison ||--o{ Class : hosts
```

| Table | Key Fields |
|-------|------------|
| `teachers` | id, full_name, email, mobile, status(enum) |
| `prisons` | id, name, region, capacity, address |
| `task_reports` | id, teacher_id, prison_id, date, hours, lesson_title, saved_persons(int), issues(text), created_at |
| `print_reqs` | id, teacher_id, prison_id, module, copies, req_date, fulfil_date, status(enum), pdf_url |
| `classes` | id, prison_id, title, start_date, end_date, time_slot, location_detail |
| `students` | id, class_id, prison_num, full_name, cell_block, dob |
| `sessions` | id, class_id, date, topic, teacher_id |
| `attendances` | id, session_id, student_id, present(bool), notes(text) |

### 5.2 API Contract (sample)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/tasks` | Create task report (returns 201 + Location header) |
| GET | `/api/tasks?teacherId=1&from=2025-09-01` | List w/ filters |
| POST | `/api/prints` | Create print req (triggers PDF gen micro-service) |
| GET | `/api/prints/{id}/pdf` | Stream PDF blob (Content-Disposition: inline) |
| POST | `/api/classes/{id}/sessions/{date}/attendance` | Bulk attendance patch |
| GET | `/api/dashboard` | KPI card payload for Control Panel |

### 5.3 Security & Env
- Spring Security JWT (access 15 min, refresh 7 d)
- CORS allowed origins: `https://paz-admin.vercel.app`, `http://localhost:3000`
- PostgreSQL SSL mode `require`; secrets in Vault / AWS SM
- Audit table `audit_log` (who, what, when, prison)

---

## 6. 🖨️ Print Module Deep-Dive (Component C)

### Workflow
1. Teacher fills digital requisition (prison, module, copies, date-needed).  
2. System auto-generates **PDF** (iText 7) with QR code containing req-ID.  
3. Admin sees queue → clicks **“Print”** → browser print dialog → physical printer.  
4. Status flips `PENDING → PRINTED → DELIVERED`.

### PDF Template Mock-up
```
+--------------------------------------------------+
|  Prison Alliance Zimbabwe - Printing Req         |
|  Req #: 000042  |  Date: 15 Sep 2025            |
|  Prison: Harare Central                        |
|  Teacher: John Moyo  |  Module: Who is God? L2   |
|  Copies: 25  |  Date Needed: 18 Sep 2025       |
|  QR (links to /prints/42)                       |
+--------------------------------------------------+
```

---

## 7. 📊 Control Panel MVP (Component A – Iteration 1)

| Widget | Data Source | Refresh |
|--------|-------------|---------|
| **Open Print Jobs** | `SELECT COUNT(*) FROM print_reqs WHERE status='PENDING'` | 30 s poll |
| **Active Classes This Week** | `SELECT COUNT(*) FROM classes WHERE start_date <= TODAY AND end_date >= TODAY` | 60 s |
| **Hours Taught MTD** | `SELECT SUM(hours) FROM task_reports WHERE date >= DATE_TRUNC('month', NOW())` | 5 min |
| **Quick Links** | Static icons → routes | — |

*No charts yet; plain Bootstrap cards.*

---

## 8. 🏃‍♂️ MVP Cut-Line (what we SKIP for v0.1)
- Offline mode / PWA sync
- Multi-language (Shona/Ndebele) UI
- Printer hardware API (manual browser print only)
- Biometric attendance
- RBAC granular per field
- Mobile app

---

## 9. 📅 Release Roadmap (aligned to AI-Suplex cash engine)

| Sprint | Dates | Outcome | Kilo Code Mode |
|--------|-------|---------|----------------|
| **Sprint 0** | 16-20 Sep 2025 | Repo scaffold, DB schema, login flow | Architect |
| **Sprint 1** | 21-27 Sep 2025 | Task Reports CRUD + PDF | Orchestrator |
| **Sprint 2** | 28-04 Oct 2025 | Print Requisitions + queue | Orchestrator |
| **Sprint 3** | 05-11 Oct 2025 | Student Reg + attendance sheet generator | Orchestrator |
| **Sprint 4** | 12-15 Oct 2025 | Control Panel widgets, QA, docs | Orchestrator |
| **Launch** | 16 Oct 2025 | v0.1 live → upsell $49/mo to PAZ | — |

---

## 10. 💰 Monetisation Hooks (Phase 0 friendly)
- **$49 setup** (docker-compose + 1 h Zoom)  
- **$29 / month** cloud hosting (Railway Pro + DB)  
- **$7** per extra prison site (>1 prison)  
- **White-label** footer removal: **$199** one-time

---

## 11. 🛠️ Dev-Ready Repo Structure (for Kilo Code)
```
paz-admin/
├── backend/
│  ├── src/main/java/zw/org/paz/
│  ├── docker/Dockerfile
│  └── pom.xml
├── frontend/
│  ├── pages/
│  ├── components/ui/
│  └── next.config.js
├── docker-compose.yml
├── .env.example
└── README.md (contains this PRD link)
```

---

## 12. ✅ Acceptance Criteria (v0.1 Definition of Done)
1. Teacher can create **Task Report** < 2 min on 3G.  
2. Admin can print **25-copy requisition** in 3 clicks.  
3. Attendance sheet PDF generates **< 5 s** for 30 inmates.  
4. All pages load **< 3 s** on Chrome Lite (1 Mbps).  
5. **Zero paper** for above flows (100 % digital export). 