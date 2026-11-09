# CampusEarn – Backend & Supabase Architecture Setup Guide

**Tagline:** Learn. Work. Earn.  
**Target Market:** India-wide College Students (INR ₹ Currency)

---

## 1. Quick Supabase Project Setup

1. Create a new project at [Supabase.com](https://supabase.com).
2. Note down your **Project URL** and **Public Anon Key** from `Project Settings -> API`.
3. Open the **SQL Editor** in your Supabase dashboard.
4. Copy the entire contents of `/docs/schema.sql` and run the script. This will provision:
   - `profiles` (with multi-role support for Student, Employer, and Admin)
   - `jobs` (with 10 predefined student gig categories)
   - `applications` (with duplicate application prevention)
   - `work_submissions` (task uploads, reviews, revisions)
   - `conversations` & `messages` (private messaging)
   - `payments` (transparent ledger for ₹ earnings & payouts)
   - `reviews` (verified reviews post completion)
   - `reports` (student fraud & scam moderation)
   - `notifications`
   - Complete Row Level Security (RLS) policies.

---

## 2. Storage Buckets Configuration

Navigate to **Storage -> New Bucket**:
1. `resumes-portfolios`: Private bucket with authenticated read/write for student PDF portfolios.
2. `work-deliverables`: Private bucket for completed zip files, designs, and code bundles.
3. `avatars`: Public bucket for user and employer profile photos.

---

## 3. Environment Variables

Create `.env` (or configure secrets in Android AI Studio Secrets Panel):

```env
SUPABASE_URL=https://your-project-id.supabase.co
SUPABASE_ANON_KEY=your-anon-public-key
APP_CURRENCY=INR
MIN_PAYOUT_AMOUNT=100
```

---

## 4. Admin Account Provisioning

To grant admin access to an account, run this query in SQL Editor:

```sql
UPDATE public.profiles
SET roles = array_append(roles, 'admin')
WHERE email = 'admin@campusearn.in';
```

---

## 5. Security & Safety Checklist

- **Zero Upfront Fees:** CampusEarn strictly enforces that employers cannot charge students registration or application fees.
- **Offline Gigs Safety:** Offline gigs (event crews, store assistants) mandate public venue checks and verified company badges.
- **Legitimate Escrow Ledger:** All payments are logged with immutable transaction references before student payouts.
