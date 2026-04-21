# Reflect — Marketing Strategy Guide

| Field | Value |
|-------|-------|
| Document Version | **1.0** |
| Product | Reflect — Guided Weekly Review & AI Insight Platform |
| Pricing | **£7.99/mo** or **£59.99/yr** (save 37%) |
| Author | Jeffrey Jordan |
| Date | April 2026 |

> This document contains copy-paste-ready scripts, templates, Claude Code prompts, and
> step-by-step delivery instructions for every marketing channel. It is the operational
> companion to [reflect-market-research-v1.0.md](reflect-market-research-v1.0.md).

---

## Table of Contents

1. [Brand Voice & Positioning](#1-brand-voice--positioning)
2. [Twitter/X Build-in-Public](#2-twitterx-build-in-public)
3. [Reddit & Community Marketing](#3-reddit--community-marketing)
4. [SEO Blog Content](#4-seo-blog-content)
5. [Newsletter (Beehiiv)](#5-newsletter-beehiiv)
6. [Product Hunt Launch](#6-product-hunt-launch)
7. [Hacker News (Show HN)](#7-hacker-news-show-hn)
8. [Landing Page Copy](#8-landing-page-copy)
9. [Email Templates](#9-email-templates)
10. [LinkedIn](#10-linkedin)
11. [Claude Code Prompts for Content](#11-claude-code-prompts-for-content)
12. [Monitoring & Analytics](#12-monitoring--analytics)
13. [Weekly Execution Calendar](#13-weekly-execution-calendar)

---

## 1. Brand Voice & Positioning

### Voice

Calm, direct, warm. Not hustle-culture. Not corporate. Not self-help.

| Do | Don't |
|----|-------|
| "5 questions, 15 minutes, every Sunday" | "Supercharge your productivity!" |
| "The pattern you can't see in the moment" | "Revolutionary AI insights" |
| "Built for people who think for a living" | "For high-performers and go-getters" |
| "Friction, not blockers" | "Unlock your full potential" |
| "The number doesn't lie" | "Game-changing analytics" |

### One-Line Descriptions (use across all channels)

- **10 words:** "A guided weekly review with AI pattern insights."
- **20 words:** "Five structured prompts every Sunday. AI reads across your weeks to surface the patterns you can't see yourself."
- **Tagline:** "Know how your weeks actually went."

### Key Phrases (build brand recognition by repeating these)

| Phrase | When to use |
|--------|-------------|
| "5 questions, 15 minutes, every Sunday" | Core habit description |
| "The pattern you can't see in the moment" | AI insight value |
| "Friction, not blockers" | Differentiating the prompt design |
| "The moment still on your mind" | Signal moment prompt |
| "A feedback loop for your working life" | Product description |
| "Structured reflection, not blank-page journaling" | Competitive positioning |
| "The paywall is the intelligence, not the journal" | Pricing philosophy |
| "Built for people who think for a living" | Target audience |

---

## 2. Twitter/X Build-in-Public

### 2.1 Profile Setup

**Name format:** `Jeff Jordan | building Reflect`
Update as milestones change: "building" -> "launching" -> "growing"

**Bio (160 chars):**
```
Building Reflect — a guided weekly review app with AI pattern insights.
Helping knowledge workers think clearly about their weeks. Solo founder.
Ships weekly.
```

**Pinned tweet (pre-launch):**
```
I'm building Reflect — a weekly review app for knowledge workers.

Every Sunday, 5 structured prompts guide you through your week:
-> What moved forward
-> Where you felt resistance
-> Your energy level
-> The moment still on your mind
-> What matters next

AI spots the patterns you can't see yourself.

Launching soon. Follow along as I build it in public.

[link to landing page]
```

**Pinned tweet (post-launch):**
```
Reflect is live.

5 structured prompts. One weekly habit. AI that reads across weeks
to surface the patterns you miss.

Built for engineers, PMs, freelancers -- anyone who thinks for a
living and wants to do it better.

£7.99/mo · £59.99/yr (save 37%)

Try it -> [link]
```

---

### 2.2 Content Pillars (5 ready-to-post tweets each)

#### PILLAR 1: Build-in-Public Updates

**Tweet 1 — Shipping update:**
```
Shipped streak tracking this week in Reflect.

If you complete your weekly review every Sunday, you build a streak.
Simple consecutive-week counter with a visual badge.

Tiny feature. But the thing that makes habits stick isn't the app --
it's the thing that makes you feel bad about breaking the chain.
```

**Tweet 2 — Technical decision:**
```
Decided to build Reflect as a monolith.

No microservices. One Spring Boot JAR. One deployment target.

I know this is unfashionable. I also know that as a solo founder,
operational complexity is the thing most likely to kill my product
before users do.

Monolith until it hurts.
```

**Tweet 3 — Metrics transparency:**
```
Reflect week 8 numbers:

- 47 signups
- 23 completed at least one check-in
- 11 have checked in 3+ weeks in a row
- 0 paying customers (still free beta)

The 11 who keep coming back are the signal. Talking to all of them
this week.
```

**Tweet 4 — Decision sharing:**
```
Pricing decision for Reflect:

£7.99/mo or £59.99/yr.

Not free. Not freemium with a crippled free tier.

Free tier gets 4 check-ins. Enough to feel the value. Not enough to
get the real benefit (AI pattern insights need 6+ weeks of data).

The paywall is the AI, not the journal.
```

**Tweet 5 — Honest setback:**
```
Spent 3 days debugging a Flyway migration issue that corrupted my
test database.

The fix was one line.

Solo founder lesson I keep relearning: when you're stuck for more
than 2 hours, explain the problem out loud. To anyone. A rubber
duck. A tweet. Your cat.

The act of explaining is the fix.
```

#### PILLAR 2: Productivity / Reflection Thought Leadership

**Tweet 1:**
```
Most people don't lack ambition. They lack a feedback loop.

You set goals in January. You check them in December. 11 months
of no signal.

A weekly review closes the loop to 7 days. That's the whole insight.
```

**Tweet 2:**
```
The five questions that changed how I think about my weeks:

1. What actually moved forward?
2. Where did I feel resistance?
3. What's my energy level, honestly?
4. What moment is still on my mind?
5. What matters most next week -- and why?

Takes 15 minutes. Returns hours of clarity.
```

**Tweet 3:**
```
"Friction" is a better question than "blockers."

Blockers are external. Someone else's fault.

Friction includes the task you kept avoiding. The conversation you
didn't have. The thing you did but resented.

Friction is where the growth is hiding.
```

**Tweet 4:**
```
I've done weekly reviews for 60+ weeks now.

The thing nobody tells you: the insights don't come from any
single week.

They come from reading weeks 14, 27, and 41 next to each other
and realising you've written the same friction three times and
done nothing about it.
```

**Tweet 5:**
```
Unpopular opinion: journaling apps that give you a blank page
are setting you up to fail.

A blank page requires creativity. Weekly review requires structure.

The best review isn't the one where you wrote the most. It's the
one where you answered the right questions.
```

#### PILLAR 3: Behind-the-Scenes (Technical / Design)

**Tweet 1:**
```
Design decision in Reflect: the check-in has exactly 5 prompts.
Not 3. Not 10.

3 felt like a survey. 10 felt like homework.

5 hits the sweet spot where you think deeply but finish before
the motivation runs out. ~12 minutes average completion time.
```

**Tweet 2:**
```
Building AI pattern detection for Reflect.

The hard part isn't the AI. The hard part is defining what a
"pattern" is.

Current approach: the AI reads your last 8 weeks and looks for
recurring themes in your friction and signal moment fields.

If you wrote about the same coworker conflict 4 times, that's
a pattern. Not a coincidence.
```

**Tweet 3:**
```
Reflect's energy rating is a 1-10 slider. Not a text field.
Not an emoji picker.

Numbers are searchable. Numbers trend over time. Numbers don't
lie the way words do.

When your 12-week energy average drops from 7.2 to 5.1, the
chart says what you won't type.
```

**Tweet 4:**
```
I'm building MCP integration so you can submit your weekly
review from inside Claude or Cursor.

Why: the most reflective developers I know live inside AI tools.
Making them context-switch to a web app for a 10-minute ritual
is friction I can eliminate.

The best tool meets you where you are.
```

**Tweet 5:**
```
Rate limiting decision for Reflect:

5 requests per minute per IP on auth endpoints. Simple bucket
approach via Spring Boot + Redis.

Is it over-engineered for 50 users? Yes.

Will I be glad it's there the first time someone tries to
brute-force a login? Also yes.

Security isn't a scaling problem. It's a day-one problem.
```

#### PILLAR 4: Engagement Bait (Questions, Polls, Hot Takes)

**Tweet 1:**
```
Genuine question for engineers and PMs:

Do you do any kind of weekly review or reflection habit?

If yes -- what does it look like?
If no -- what stopped you from starting?

(Building something in this space and trying to understand the gap)
```

**Tweet 2:**
```
When you think about your work week, what's hardest to be
honest about?

- What you actually accomplished
- Where you wasted time
- How you feel about your energy
- Interpersonal friction with colleagues
```

**Tweet 3:**
```
Hot take: most "productivity systems" are procrastination
dressed in cargo pants.

You don't need a second brain. You need 15 minutes on Sunday
to answer: what happened, what did I feel, and what matters next.

The system isn't the value. The reflection is.
```

**Tweet 4:**
```
Would you rather:

A) An AI that plans your week for you
B) An AI that reviews your last 8 weeks and tells you the
   pattern you keep repeating

I'm building B. Curious how many people want A.
```

**Tweet 5:**
```
Your manager's weekly 1:1 question -- "how's everything going?"
-- is the worst reflection prompt ever designed.

It invites "fine." Every time.

Better: "Where did you feel resistance this week?"

That question has teeth. That's the one that surfaces something real.
```

#### PILLAR 5: Personal Founder Story

**Tweet 1:**
```
Why I'm building Reflect:

I'm an engineer. I spent years being "busy" without ever stopping
to ask whether I was working on the right things or just the
urgent ones.

Started doing structured weekly reviews. In 3 months I'd set
boundaries, changed how I work, and slept better.

That's the app I'm building.
```

**Tweet 2:**
```
Solo founder confession:

The hardest part isn't the code. It's the silence.

No standup. No Slack messages. No one asking how the sprint went.

My weekly review in Reflect is the one place I'm honest about
how the week actually went. I use my own product every Sunday.

Dogfooding isn't optional when you're alone.
```

**Tweet 3:**
```
Things I've learned building a solo SaaS:

- You will massively overestimate what you can build in a week
- You will massively underestimate what you can build in 3 months
- The feature you're most proud of is never the one users care about
- Shipping consistently > shipping perfectly
```

**Tweet 4:**
```
I built Reflect for people like me:

You're good at your job. You deliver. People respect you.

But privately you're not sure if you're growing or just performing.

Weekly structured reflection is the difference. Not journaling.
Not therapy. Just 15 minutes of honest review with good questions.
```

**Tweet 5:**
```
The moment I decided to build Reflect:

I was in a 1:1 and my manager asked "how have the last few
weeks been?"

I blanked. Not because things were bad -- because I genuinely
couldn't remember. Weeks had blurred together.

That blank is the problem. Reflect is the answer.
```

---

### 2.3 Full Thread Templates

#### THREAD 1: "Why I'm Building a Weekly Review App" (Launch Thread)

```
Tweet 1/10:
I quit my day job mentality to build a weekly review app.

Here's why I think most knowledge workers are flying blind --
and what I'm doing about it.

A thread.

Tweet 2/10:
The problem:

Engineers, PMs, freelancers -- we all set goals.

But almost none of us have a structured way to check in with
ourselves weekly.

We have standups for our teams. Retros for our sprints.
Nothing for ourselves.

Tweet 3/10:
I started doing weekly reviews with a Google Doc.

Same 5 questions every Sunday:
- What moved forward?
- Where did I feel resistance?
- Energy level (1-10)?
- What moment is still on my mind?
- What matters most next week?

15 minutes. Every week.

Tweet 4/10:
After 12 weeks, something unexpected happened.

I re-read my entries and saw a pattern I couldn't see in the moment:

I'd written about the same interpersonal friction in 8 out of
12 weeks.

I thought I was fine. The data said otherwise.

Tweet 5/10:
That pattern recognition -- seeing what you can't see in the
moment -- is the real value.

Not the act of writing. The act of reviewing what you wrote
across weeks and months.

Humans are terrible at this. AI is good at it.

Tweet 6/10:
So I'm building Reflect.

5 structured prompts. Weekly cadence. AI that reads across your
history and surfaces the patterns you keep repeating.

Not a journal. Not a to-do app. A feedback loop for your
working life.

Tweet 7/10:
The stack:

- Spring Boot 3.3 (Java 21) backend
- Next.js 14 frontend
- PostgreSQL on Neon
- Redis on Upstash
- Railway + Vercel deployment

Monolith architecture. One person. Ship fast.

Tweet 8/10:
What the AI does:

After 6+ weeks of check-ins, specialist agents analyse your data:

- Pattern detection across friction and energy
- Insight synthesis from recurring themes
- Adaptive prompts that evolve with your needs
- Nudges when behaviour doesn't match stated intentions

Tweet 9/10:
Pricing: £7.99/mo or £59.99/yr (save 37%).

Free tier: 4 check-ins. Enough to build the habit.
Pro tier: Unlimited + AI insights + streak tracking.

The paywall is the intelligence layer, not the reflection itself.

Tweet 10/10:
I'm building this in public. Every week I'll share:

- What I shipped
- User feedback
- Revenue (when it exists)
- Mistakes and lessons

Follow along if you're interested in the intersection of
reflection, AI, and building a sustainable solo business.

[landing page link]
```

#### THREAD 2: "What 12 Weeks of Weekly Reviews Taught Me" (Thought Leadership)

```
Tweet 1/9:
I've done structured weekly reviews for 60+ weeks now.

Here are 8 things I learned that surprised me -- and changed
how I work.

Tweet 2/9:
1. You don't know how you feel about your job.

When someone asked "how's work?" I'd say "fine."

But when I rated my energy every week on a 1-10 scale, my
8-week average was 4.3.

That's not "fine." The number didn't lie. My mouth did.

Tweet 3/9:
2. Friction is more valuable than wins.

Writing about what went well feels good. But the growth is
in the friction question:

"Where did you feel resistance this week -- a task, a person,
or yourself?"

That question broke open every meaningful change I've made.

Tweet 4/9:
3. The patterns take 6 weeks to appear.

Week 1: you feel productive.
Week 6: you realise you've mentioned the same unresolved
conversation four times.

Short-term review is cathartic. Long-term review is diagnostic.

Tweet 5/9:
4. Your "signal moment" predicts your next move.

Every week I answer: "What interaction or moment is still on
your mind?"

After 12 weeks, I noticed: the moments that lingered were
always about autonomy. Being overruled. Not being heard.

That pattern led me to change everything.

Tweet 6/9:
5. Intentions without "why" are just to-do lists.

"Ship the auth module" is a task.
"Ship the auth module because users can't sign up" is an intention.

Adding "why does it matter?" changed how I prioritised completely.

Tweet 7/9:
6. Your energy is the leading indicator of everything.

Motivation, output quality, patience with others, creative thinking
-- they all follow energy.

Tracking it weekly on a simple 1-10 scale gave me a dashboard for
my own performance no project management tool ever has.

Tweet 8/9:
7. Consistency beats depth.

My best reviews aren't the longest. They're the ones I showed up
for even when I didn't feel like it.

A 5-minute review done 52 weeks in a row is worth more than a
2-hour reflection done twice.

Tweet 9/9:
8. It changed my relationships, not just my work.

The friction prompt forced me to name interpersonal tension I'd
been ignoring.

Once I named it on paper, I could address it in person.

That's what structured reflection does. Not productivity hacks.
Self-honesty with a system that holds you accountable.

If you want to start: 5 questions, 15 minutes, every Sunday.

I built an app to make it easier -> [link]
```

#### THREAD 3: "I Just Launched on Product Hunt" (Launch Day)

```
Tweet 1/12:
Reflect is live on Product Hunt today.

It took [X] months to build. Here's the full story -- from the
Google Doc that started it all to today.

(link at the end if you want to support it)

Tweet 2/12:
It started with a Google Doc.

Every Sunday for a year, I answered 5 questions about my week.
Same questions. No app. Just a doc.

After 12 weeks I could see patterns in my work life that I was
completely blind to in the moment.

I thought: this should be a product.

Tweet 3/12:
The core insight:

Individual weekly reviews are useful. But the real value is
pattern recognition across weeks.

You can't remember what you wrote about friction 6 weeks ago.
But AI can -- and it can tell you that you've had the same
complaint 5 times without acting on it.

Tweet 4/12:
The build:

- Spring Boot 3.3 + Java 21 backend
- Next.js 14 frontend
- PostgreSQL (Neon) + Redis (Upstash)
- Railway + Vercel deployment
- [X] total commits

Solo founder. Every line of code.

Tweet 5/12:
The 5 check-in prompts:

1. Progress -- What moved forward this week?
2. Friction -- Where did you feel resistance?
3. Energy -- 1-10 slider
4. Signal moment -- What's still on your mind?
5. Intentions -- What matters most next week, and why?

Designed to take 12-15 minutes.

Tweet 6/12:
The AI agents:

PatternSentinel: detects recurring themes in friction and energy
InsightSynthesis: generates cross-week insights
PromptAdaptation: evolves prompts based on your history
NudgeAgent: flags when behaviour doesn't match intentions

They run after each check-in. Results on your dashboard.

Tweet 7/12:
Pricing: £7.99/mo or £59.99/yr (save 37%).

Free tier: 4 check-ins. Enough to try the habit.
Pro tier: Unlimited + AI insights + streak tracking.

I wanted the free tier to be genuinely useful but make AI
insights the clear reason to upgrade.

Tweet 8/12:
What I'd do differently:

- Start talking to potential users 4 weeks earlier
- Build the landing page before the product
- Ship a broken v0.1 faster instead of polishing v0.9

Every solo founder says this. It's still true every time.

Tweet 9/12:
The numbers at launch:

- [X] beta users
- [X] completed check-ins in beta
- [X] average weekly retention rate
- [X] paying customers
- [X] MRR

Real numbers because build-in-public means nothing if the
numbers are hidden.

Tweet 10/12:
What's next:

- AI insights v2 (cross-quarter pattern analysis)
- Mobile-optimised check-in flow
- MCP integration (check in from Claude/Cursor)
- Team plans (Phase 4)

But right now: launch day. One thing at a time.

Tweet 11/12:
Thank you to:

- [X] beta users who showed up every Sunday
- Everyone who followed along as I built in public
- The indie hacker community for the honest feedback

This thing exists because of you.

Tweet 12/12:
If you're an engineer, PM, freelancer, or founder who wants a
structured way to reflect on your weeks with AI that spots the
patterns you miss:

Reflect is live on Product Hunt today.

[Product Hunt link]

A try means everything. An upvote means a lot too.
```

---

### 2.4 Daily Posting Schedule

**Optimal times (UK timezone for UK + US overlap):**

| Time (BST) | US equivalent | Purpose |
|------------|---------------|---------|
| 08:00 | 3am ET | Catch UK morning scroll |
| 13:00 | 8am ET | **Best overlap window** — UK lunch + US East Coast morning |
| 17:30 | 12:30pm ET | **Highest reach** — UK end-of-day + US peak |

**Volume:** 1-2 original tweets/day + 5-10 replies to other accounts

**Weekly calendar:**

| Day | Post Type | Pillar |
|-----|-----------|--------|
| Monday | Shipping update (what you built last week) | Build-in-public |
| Tuesday | Thought leadership (reflection/productivity insight) | Thought leadership |
| Wednesday | Behind-the-scenes (technical/design decision) | Behind-the-scenes |
| Thursday | Thread day (long-form, gets best engagement Thu-Sat) | Any |
| Friday | Personal founder story or engagement question | Story / Engagement |
| Saturday | Quote tweet or light engagement | Engagement |
| Sunday | "Just did my weekly review" ritual post | Personal |

**Sunday ritual tweet template (vary weekly):**
```
Just finished my weekly review in Reflect.

This week's signal moment: [genuine insight from your week]

[X]-week streak. 15 minutes that change how I think about the
next 7 days.
```

---

### 2.5 Engagement Strategy

**Search queries to run weekly on X:**
```
"weekly review" -filter:replies
"weekly reflection" -filter:replies
"journaling habit" -filter:replies
"burnout engineer" -filter:replies
"productivity system" -filter:replies
"building in public" -filter:replies
```

**Reply templates for common conversations:**

*Someone tweets about burnout:*
```
I tracked my energy on a 1-10 scale every week for a year.
The pattern that predicted burnout wasn't low energy -- it was
consistently rating 6-7 while writing about friction with the
same project.

The "I'm fine" plateau is the danger zone.
```

*Someone tweets about productivity systems:*
```
The one thing that actually stuck for me was stripping it down
to 5 questions, once a week, every Sunday.

Not a system. A habit. The constraint is what made it work.
```

*Someone tweets about AI tools:*
```
The most underrated use of AI isn't generating content. It's
pattern recognition across your own data.

I use AI to read my last 8 weeks of weekly reviews and surface
recurring themes I missed. Turns out I'm terrible at noticing
my own patterns in real-time.
```

**Rule:** Never pitch Reflect in replies unless directly asked. Let your profile do the selling.

---

## 3. Reddit & Community Marketing

### 3.1 Target Subreddits (Ranked by Relevance)

| Subreddit | Members | Culture | Approach |
|-----------|---------|---------|----------|
| **r/productivity** | 2.8M | Practical tips, tools, systems. Anti-hustle-culture. | Value-first answers about weekly reviews |
| **r/selfimprovement** | 2.5M+ | Personal growth, habits, mindset. Authentic stories. | Share personal reflection journey |
| **r/getdisciplined** | 1.2M+ | Accountability, habit formation. Supportive. | Weekly review as a discipline framework |
| **r/journaling** | 200K+ | Passionate about the practice. Anti-app-spam. | Contribute to "methods" discussions |
| **r/Entrepreneur** | 2M+ | Business building, side projects. | Build-in-public updates |
| **r/SideProject** | 200K+ | Supportive of indie builders. | Project showcase (after karma building) |
| **r/SaaS** | 100K+ | SaaS metrics, growth, product. | Revenue/growth updates |
| **r/webdev** / **r/programming** | 2M+ / 5M+ | Technical. Allergic to marketing. | MCP integration angle only |

### 3.2 Karma-Building Phase (Weeks 1-6)

**Week 1-2: Lurk and understand**
- Read the top 20 posts of all time in each target subreddit
- Note which types of posts get upvoted vs removed
- Identify recurring questions you can answer well

**Week 3-4: Start commenting (no product mentions)**
- Answer 2-3 questions per day in r/productivity and r/selfimprovement
- Focus on topics you genuinely know about: weekly reviews, habit formation, energy tracking
- Share personal experience, not generic advice

**Week 5-6: Become a recognised contributor**
- Post your own question or discussion thread (still no product mention)
- Continue answering questions with genuine value
- Build relationships with regular posters

### 3.3 Value-First Comment Templates

**Template 1 — Someone asks "how do you do weekly reviews?"**
```
I've done weekly reviews for over a year now. Here's my exact
process — takes about 15 minutes every Sunday:

I answer the same 5 questions each week:

1. **Progress** — What moved forward this week? (Not just work
   output — includes how I worked with others)
2. **Friction** — Where did I feel resistance? (This is the most
   important one. Not "blockers" — that's too external. Friction
   includes the conversation I avoided, the task I kept putting off)
3. **Energy** — Rate 1-10. Just a number. No explanation needed.
4. **Signal moment** — What interaction or moment is still on my
   mind, and why? (This one catches the emotional residue other
   questions miss)
5. **Intentions** — What matters most next week, and *why* does
   it matter? (The "why" part stops it being a to-do list)

The magic isn't any single week. It's reading back across 6-8 weeks
and seeing patterns you were blind to in the moment. I noticed I'd
written about the same interpersonal friction 8 times before I
actually addressed it.

The structure is what makes it work. Blank-page journaling never
stuck for me because I'd only write about whatever was loudest in
my head — not what was most important.
```

**Template 2 — Someone asks "best journaling method for professionals?"**
```
I tried a lot of methods and the one that stuck was moving away
from daily journaling entirely.

Daily captures too much noise. You write about what happened
today, which is dominated by whatever frustrated you most recently.

Weekly is the sweet spot for professionals. You have enough
distance to separate signal from noise, and the time commitment
(15 minutes on Sunday) is sustainable long-term.

The other thing that made it work: structure. Not a blank page.
Specific questions. I use 5 prompts that cover wins, friction,
energy, a significant moment, and intentions for next week.

The friction question is the most valuable one by far. Not
"what blocked you" (too external) but "where did you feel
resistance" — which includes the stuff you're avoiding,
interpersonal tension, and your own procrastination.

After a few months, patterns emerge that you genuinely can't see
from inside your own weeks. That's when it gets really useful.
```

**Template 3 — Someone asks about burnout/energy management**
```
One thing that helped me was tracking my energy on a simple
1-10 scale every week. Not daily (too much noise), just weekly.

After 8 weeks, I had a trend line. And the pattern wasn't what
I expected — my lowest energy weeks weren't the busiest ones.
They were the ones where I had unresolved friction with someone
and didn't address it.

The act of rating your energy as a number forces a kind of
honesty that "how are you feeling?" doesn't. When you see your
8-week average drop from 7 to 4, you can't rationalise it away.

I'd recommend pairing the energy rating with one reflection
question: "where did I feel resistance this week?" The
combination of a number and a written answer catches things
you'd otherwise miss.
```

### 3.4 Product Launch Posts (Week 7+)

**r/SideProject or r/SaaS — Project showcase:**

Title: `I built a weekly review app with AI pattern insights — solo founder, 4 months of work`

```
Hey everyone — I've been lurking and occasionally commenting here
for a while. I just launched Reflect, and I wanted to share the
journey and get honest feedback.

**What it is:** A guided weekly review app for knowledge workers
(engineers, PMs, freelancers). Every Sunday you answer 5 structured
prompts about your week. Over time, AI reads across your entries
and surfaces patterns you can't see yourself.

**Why I built it:** I'd been doing weekly reviews in a Google Doc
for a year. Same 5 questions every Sunday. After a few months I
could see clear patterns in my friction, energy, and interpersonal
dynamics — but only when I manually re-read weeks of entries. I
thought: AI should do this reading-across-weeks part.

**The stack:** Spring Boot 3.3, Next.js 14, PostgreSQL (Neon),
Redis (Upstash), Railway + Vercel. Monolith architecture.

**Pricing:** Free tier (4 check-ins) and Pro (£7.99/mo or
£59.99/yr) for AI insights, full history, and analytics.

**What I'm looking for:** Honest feedback on the prompt design,
the AI insight quality, and whether the value proposition is clear.

Happy to answer any questions about the build, the business model,
or the technical architecture.

[link]
```

**r/productivity — Value-first with product context:**

Title: `I've done 60+ weekly reviews — here's the 5-question framework that actually stuck`

```
I've tried a lot of productivity methods. The one that genuinely
changed how I work was a structured weekly review. Not daily
journaling (I quit three times). Not a complex Notion system
(lasted two weeks). Just 5 questions, every Sunday, 15 minutes.

Here are the 5 prompts I use:

[... the 5 prompts with explanations ...]

After 12 weeks, I noticed something I couldn't see in the moment:
I'd written about the same interpersonal friction 8 out of 12
weeks. I thought I was fine. The data said otherwise.

I ended up building this into an app called Reflect because I
wanted AI to do the "reading across weeks" part that I was doing
manually. But the framework works with a Google Doc or a notebook
too. The structure is what matters.

If you try it, the friction question is the one to take most
seriously. Not "what blocked you" — "where did you feel
resistance." That distinction surfaces the stuff you're avoiding,
not just the stuff that's in your way.
```

### 3.5 IndieHackers Monthly Update Template

```
Title: Reflect Month [X] — [MRR] MRR, [X] users, [key lesson]

Hey IH —

Monthly update on Reflect, a guided weekly review app with AI
pattern insights. Solo founder, building part-time.

**Numbers:**
- MRR: £[X] (up/down from £[X] last month)
- Active Pro subscribers: [X]
- Free users: [X] cumulative
- Free-to-Pro conversion: [X]%
- Monthly churn: [X]%
- WAU (Pro): [X]%

**What shipped:**
- [Feature 1]
- [Feature 2]
- [Bug fix / improvement]

**What worked:**
- [Marketing tactic or product decision that moved the needle]

**What didn't:**
- [Something you tried that failed, and why]

**Biggest lesson:**
[One honest insight from this month of building]

**Next month:**
- [Priority 1]
- [Priority 2]

Happy to answer any questions about the product, the build, or
the numbers. Honest feedback welcome.

[link]
```

### 3.6 Hacker News Show HN

Covered in Section 7 below.

### 3.7 Discord/Slack Community Setup

**Community name:** "The Weekly Review Club" or "Reflect Community"

**Channel structure:**
- `#introductions` — new members share their role and why they're interested in weekly reviews
- `#sunday-checkins` — members post that they completed their review (no content, just accountability)
- `#patterns-and-insights` — share anonymised patterns or reflections
- `#product-feedback` — direct feedback channel for Reflect
- `#general` — off-topic, general conversation

**Welcome message:**
```
Welcome to the Weekly Review Club.

This is a community for people who do (or want to start) a
structured weekly review. Engineers, PMs, freelancers, founders
— anyone who wants to be more intentional about their weeks.

How it works:
- Every Sunday, post in #sunday-checkins when you've done your
  review (no need to share content — just the accountability)
- Share patterns or insights in #patterns-and-insights
- Ask questions, share resources, support each other in #general

One rule: be genuine. This isn't a self-help space. It's a space
for people who want to think more clearly about their work.
```

**Weekly engagement prompt (post every Friday):**
```
This week's reflection question:

[Rotate through questions like:]
- What's the one thing from this week you'll still remember
  in a month?
- If you could change one decision from this week, what would
  it be?
- What's a conversation you've been avoiding?
- When was your energy highest this week? What were you doing?

Share your answer below — or save it for your Sunday review.
```

---

## 4. SEO Blog Content

### 4.1 Content Strategy

**Domain:** Blog integrated into the Reflect Next.js app at `/blog/[slug]`
**Publishing cadence:** 2-3 articles per month
**Target:** Long-tail keywords with KD <20, informational intent

### 4.2 Article Plans (10 articles, publication order)

#### Article 1 (Publish first — pillar content)

**Title:** "The Complete Guide to Weekly Reviews: How to Reflect on Your Week in 15 Minutes"
**Target keyword:** "weekly review" / "how to do a weekly review"
**Search intent:** Informational — people want a framework
**Meta description:** "A step-by-step guide to doing a weekly review that takes 15 minutes. Includes the 5-question framework used by engineers, PMs, and freelancers to spot patterns in their work."

**Outline:**

```
H1: The Complete Guide to Weekly Reviews

Intro (200 words)
- The problem: weeks blur together, patterns invisible
- The promise: 15 minutes, 5 questions, clarity

H2: What Is a Weekly Review?
- Definition: structured reflection on the past 7 days
- Not a to-do list review (GTD style) — a self-awareness practice
- Why weekly vs daily vs monthly

H2: The 5-Question Framework
- H3: 1. Progress — "What moved forward?"
- H3: 2. Friction — "Where did you feel resistance?"
- H3: 3. Energy — "Rate your week 1-10"
- H3: 4. Signal Moment — "What's still on your mind?"
- H3: 5. Intentions — "What matters most next week, and why?"
- For each: why this question, what it surfaces, example answers

H2: How to Do Your First Weekly Review (Step by Step)
- Pick a time (Sunday morning recommended)
- Set a timer for 15 minutes
- Answer each question — first instinct, don't overthink
- Save it somewhere consistent

H2: What Happens After 4 Weeks
- Patterns start to emerge
- Re-reading entries reveals blind spots
- The value compounds over time

H2: Common Mistakes
- Making it too long
- Only writing about wins
- Not doing it when the week felt "boring"
- Treating it like a performance review

H2: Tools for Weekly Reviews
- Google Docs / Notion (free, manual)
- Reflect (structured prompts + AI pattern insights) [CTA]
- Pen and paper

H2: FAQ
- How long should a weekly review take?
- What if I miss a week?
- Should I share my reviews with anyone?

Closing CTA: "Start your first weekly review this Sunday.
Try Reflect free — 5 structured prompts, 15 minutes, done. [link]"
```

---

#### Article 2 (Publish second — high search volume)

**Title:** "50 Weekly Reflection Questions for Professional Growth"
**Target keyword:** "weekly reflection questions" / "reflection questions for work"
**Meta description:** "50 powerful weekly reflection questions organised by category — wins, friction, energy, relationships, and intentions. Used by engineers, PMs, and freelancers."

**Outline:**

```
H1: 50 Weekly Reflection Questions for Professional Growth

Intro (150 words)
- Why specific questions beat blank-page journaling
- How to use this list (pick 3-5, not all 50)

H2: Progress & Wins (10 questions)
H2: Friction & Resistance (10 questions)
H2: Energy & Wellbeing (10 questions)
H2: Relationships & Interactions (10 questions)
H2: Intentions & Goals (10 questions)

H2: Our Recommended 5-Question Framework
- The 5 prompts Reflect uses and why they were chosen
- [CTA: Try these 5 questions in Reflect — free]

H2: How to Build a Weekly Reflection Habit
- Start with 3 questions, expand to 5
- Same time, same place, every week
- Don't overthink — write what comes to mind first
```

---

#### Article 3

**Title:** "Weekly Review Template for Software Engineers"
**Target keyword:** "weekly review template engineer" / "developer weekly review"
**Meta description:** "A weekly review template designed for software engineers. 5 structured prompts covering technical progress, friction, energy, key interactions, and priorities."

---

#### Article 4

**Title:** "How to Track Your Energy Levels at Work (And Why It Matters)"
**Target keyword:** "track energy levels at work" / "energy tracking"
**Meta description:** "Track your work energy on a simple 1-10 scale weekly. After 8 weeks, you'll see patterns that predict burnout, high performance, and career satisfaction."

---

#### Article 5

**Title:** "Friction Logging: How to Track What Blocks You Each Week"
**Target keyword:** "friction logging" / "track blockers at work"
**Meta description:** "Friction logging captures the tasks you avoided, the conversations you didn't have, and the resistance you felt. Here's how to do it weekly."

---

#### Article 6

**Title:** "Signal Moments: Why One Interaction Per Week Matters More Than Your To-Do List"
**Target keyword:** "signal moments" / "meaningful interactions at work"
**Meta description:** "The interactions still on your mind at the end of the week are signals. Here's why tracking them weekly reveals patterns about your career and relationships."

---

#### Article 7

**Title:** "Why Daily Journaling Fails (And What to Do Instead)"
**Target keyword:** "daily journaling doesn't work" / "journaling alternative"
**Meta description:** "Daily journaling has a 93% dropout rate. Weekly reflection is more sustainable, more insightful, and more likely to become a lasting habit. Here's why."

---

#### Article 8

**Title:** "AI-Powered Self-Reflection: How Pattern Recognition Beats Gut Feeling"
**Target keyword:** "AI journaling" / "AI self reflection"
**Meta description:** "AI can read across months of your weekly reviews and find patterns you're blind to. Here's how AI-powered reflection works and why it matters."

---

#### Article 9

**Title:** "The Science of Self-Reflection: What Research Says About Weekly Reviews"
**Target keyword:** "science of self reflection" / "benefits of weekly review"
**Meta description:** "Harvard research shows structured reflection improves job performance by 23%. Here's the science behind weekly reviews and how to apply it."

---

#### Article 10 (publish after launch, once you have users)

**Title:** "Reflect vs Day One vs Notion: Which Weekly Review Tool Is Right for You?"
**Target keyword:** "reflect vs day one" / "best weekly review app"
**Meta description:** "An honest comparison of Reflect, Day One, and Notion for weekly reviews. Pricing, features, AI capabilities, and which one fits your workflow."

---

### 4.3 Technical SEO Checklist

- [ ] Blog at `/blog/[slug]` within the Next.js app (inherits domain authority)
- [ ] Each article has: unique `<title>`, `<meta description>`, `<h1>`, canonical URL
- [ ] Open Graph + Twitter Card meta tags on every page
- [ ] `sitemap.xml` generated automatically (include blog posts)
- [ ] `robots.txt` allows crawling of blog, blocks `/api/` endpoints
- [ ] Submit sitemap to Google Search Console
- [ ] Structured data: `Article` schema on blog posts, `SoftwareApplication` on landing page
- [ ] Internal links: every blog post links to at least 1 other blog post + the landing page
- [ ] Images: compressed, `alt` text on all images, WebP format
- [ ] Page speed: target Lighthouse 90+ (Next.js on Vercel should achieve this)

---

## 5. Newsletter (Beehiiv)

### 5.1 Setup

**Name options:**
1. "The Weekly Review" — direct, on-brand
2. "Signal & Friction" — distinctive, references the product
3. "Reflect Weekly" — simple, branded

**Recommended:** "The Weekly Review" — it's what people search for.

### 5.2 Welcome Email Sequence (3 emails)

**Email 1 — Immediately on subscribe:**

Subject: `Welcome to The Weekly Review`

```
Hi [first name],

You're in. Every week, I'll send one email about structured
reflection, working patterns, and what I'm learning as I build
Reflect.

Here's what to expect:
- One insight about weekly reviews or professional reflection
- One thing I learned or shipped that week
- One question to think about before your next week starts

No fluff. No listicles. No "10 productivity hacks." Just honest
thinking about how to understand your own weeks better.

To start: the single most important question you can ask yourself
every Sunday is "Where did I feel resistance this week?"

Not "what blocked me" — that's external. Resistance includes the
task you kept avoiding, the conversation you didn't have, the
thing you did but resented doing. That's where growth hides.

Talk next week.

— Jeff
```

**Email 2 — Day 3:**

Subject: `The question that changes everything`

```
Hi [first name],

When most people try weekly reviews, they focus on wins.
"What did I accomplish?"

Wins feel good. They're easy to write. And they're the least
useful part of a weekly review.

The question that actually changes behaviour is about friction:

"Where did you feel resistance this week — a task, a person,
or yourself?"

That question surfaces:
- The project you've been avoiding (and why)
- The conversation you need to have but haven't
- The pattern of overcommitting that you keep repeating

I've done 60+ weekly reviews. The friction question is
responsible for every meaningful change I've made. The wins
question is responsible for feeling good on Sunday night.

Both matter. But if you only have time for one question,
ask about friction.

— Jeff

P.S. If you're curious about the full 5-question framework I
use, I wrote about it here: [link to blog post #1]
```

**Email 3 — Day 7:**

Subject: `Your energy is a leading indicator`

```
Hi [first name],

Quick experiment: rate your energy this week on a 1-10 scale.

Don't think about it. First number that comes to mind. Write
it down.

Now do that every week for 8 weeks.

What you'll see: your energy isn't random. It correlates with
specific patterns in your work — types of meetings, unresolved
friction, how much deep work you got.

The number doesn't lie the way words do. When someone asks
"how's work?" you say "fine." But your 8-week average of 4.3
says otherwise.

I track mine every Sunday as part of my weekly review. It's
the simplest input with the most revealing output.

If you want to try a structured weekly review with all 5
prompts (including energy tracking), Reflect makes it easy:
[link]

Talk next week.

— Jeff
```

### 5.3 Weekly Newsletter Template

```
Subject: [Topic-based, not branded] — e.g., "The pattern I
couldn't see for 8 weeks"

Hi [first name],

[OPENING — 2-3 sentences. A personal observation, a question,
or a surprising data point from your own weekly reviews.]

---

THIS WEEK'S REFLECTION

[MAIN SECTION — 200-300 words. One insight about weekly reviews,
professional reflection, or self-awareness. Draw from your own
experience, user feedback, or research. Be specific.]

---

BUILDING REFLECT

[BUILDING SECTION — 100-150 words. One thing you shipped,
learned, or decided this week. Metrics if you have them. This
is the build-in-public component.]

---

QUESTION FOR YOUR WEEK

[ONE QUESTION — a reflection prompt for the reader to think
about before their next Sunday review. Rotate through different
themes: friction, energy, relationships, intentions, growth.]

---

See you next Sunday.

— Jeff

[Footer: link to Reflect, unsubscribe, social links]
```

### 5.4 Subject Line Formulas

| Formula | Example |
|---------|---------|
| The [surprising insight] | "The question your manager should ask (but doesn't)" |
| [Number] weeks of [thing] taught me [lesson] | "12 weeks of friction logging taught me I was the bottleneck" |
| Why [common belief] is wrong | "Why tracking wins is the least useful part of a weekly review" |
| The [thing] I couldn't see for [time] | "The pattern I couldn't see for 3 months" |
| [Action] before [time] | "Read this before your next Monday morning" |
| I [did thing]. Here's what happened. | "I rated my energy every week for a year. Here's the chart." |

### 5.5 Growth Tactics

1. **Cross-promote:** Link to newsletter signup in every blog post, Twitter bio, and Reddit profile
2. **Referral program (Beehiiv built-in):** Reward subscribers who refer others with early access to features
3. **Content upgrades:** Offer a downloadable "5-Question Weekly Review Template" PDF in exchange for email
4. **Signature line:** Add "I write The Weekly Review — [link]" to all email signatures

---

## 6. Product Hunt Launch

### 6.1 Tagline Options

1. **"5 prompts. 10 minutes. A week of clarity."** (48 chars)
2. **"Your weekly review habit — with AI pattern insights"** (53 chars)
3. **"Stop guessing how your weeks went. Start knowing."** (52 chars)

### 6.2 Maker Comment (Post immediately at launch)

```
Hi Product Hunt — I'm Jeff, and I built Reflect because I kept
having the same realisation every few months: I had no idea how
my weeks were actually going.

I'd finish a quarter and think "that was fine, I guess?" — but
I couldn't point to what I'd accomplished, what was draining me,
or why certain weeks felt heavy and others didn't.

So I built what I actually wanted: a guided weekly review that
takes about 10 minutes every Sunday.

HOW IT WORKS:

You answer 5 specific prompts — not "how was your day?" but
questions like "Where did you feel resistance this week — a
task, a person, or yourself?" and "What interaction is still on
your mind, and why?" You rate your energy on a 1-10 scale.
That's your check-in.

On the free tier, that alone is valuable. You build a searchable
history of your weeks with a streak counter.

On Pro, the AI layer reads across your full history and surfaces
patterns you genuinely cannot see yourself. Things like: "You've
mentioned friction with context-switching in 6 of your last 8
entries" or "Your energy ratings drop by an average of 2 points
in weeks where you mention back-to-back meetings."

WHAT REFLECT IS NOT:

It's not a journal — there's no blank page. It's not a mood
tracker — the prompts go deeper. It's not a productivity system
— there are no tasks or OKRs. It's a reflective practice tool
for people who want to understand their own working patterns.

Pricing is simple: free tier gives you weekly check-ins and basic
history. Pro (£7.99/mo or £59.99/yr) unlocks AI pattern insights,
full history, and analytics.

I'd genuinely love your feedback — especially on the prompt design.
The five questions went through about 30 iterations.

Thank you for checking it out.
```

### 6.3 Prepared Q&A Responses

**"How is this different from Day One?"**
```
Day One gives you a blank page and trusts you to know what to
write about. Reflect gives you 5 specific prompts designed to
surface things you'd otherwise miss — friction patterns, energy
shifts, signal moments.

The other big difference: Day One stores your entries. Reflect
reads across them over time and surfaces patterns — like "you've
mentioned resistance to a specific type of work in 7 of your
last 10 entries."

It's the difference between a diary and a structured reflective
practice with a feedback loop.
```

**"Why not just use Notion?"**
```
You absolutely can build a weekly review template in Notion —
I did exactly that before building Reflect.

Two things happened: first, the template slowly degraded. I'd
skip fields, modify the structure, eventually stop using it.
Reflect's fixed structure is a feature — the consistency is
what makes AI pattern detection work.

Second, Notion doesn't read across your entries. You'd need to
manually review months of pages to spot a pattern.

If you're already doing weekly reviews in Notion and they're
working, keep going. But if you've tried and the habit didn't
stick, the guided structure is probably what's missing.
```

**"What AI model do you use?"**
```
We use Anthropic's Claude for the pattern detection layer. The
AI doesn't generate your reflections — it reads across your
existing entries (on Pro) and identifies recurring themes,
energy correlations, and friction patterns.

Your entries are processed for your insights only and are not
used to train any model.
```

**"Is my data private?"**
```
Yes, and this is non-negotiable. Your entries are stored in an
encrypted PostgreSQL database. AI processing uses Anthropic's
Claude API (they do not train on API inputs). We do not sell
data, share data, or use your entries for any purpose other
than generating your personal insights. You can export all
your data at any time and delete your account permanently.
```

**"Why weekly instead of daily?"**
```
Daily reflection captures noise. You write about what happened
today, which is dominated by recency bias.

Weekly reflection captures signal. By Sunday, the unimportant
stuff has faded and what's still on your mind is genuinely
significant.

Weekly is also sustainable. Daily journaling has roughly a 7%
long-term retention rate. Weekly practices are dramatically
more sticky because the time investment is reasonable and the
gap between entries lets you accumulate something worth
reflecting on.
```

### 6.4 Launch Day Timeline

**T-7 days:** Landing page live, PH ship page created, waitlist email drafted
**T-3 days:** Confirm all assets, pre-write LinkedIn post, notify close contacts
**T-1 day:** Queue PH submission, have maker comment ready to paste, clear calendar

**Launch day (all times PST):**
- **12:00-12:15 AM:** Listing goes live. Post maker comment immediately. Verify links.
- **12:15-1:00 AM:** Monitor first comments, respond within 15 minutes. Post on Twitter.
- **6:00-9:00 AM:** Critical window — US wakes up. Post LinkedIn. Send waitlist email. Respond to all comments within 10 minutes.
- **9:00 AM-6:00 PM:** Continue responding. Post midday update comment with a stat or insight.
- **6:00-11:59 PM:** Final push. Post thank-you comment.

**Days 2-7:** Thank-you DMs, "day after" LinkedIn reflection, reach out to detailed feedback givers, update landing page with social proof.

---

## 7. Hacker News (Show HN)

### 7.1 Post Template

Title: `Show HN: Reflect – Guided weekly review with AI pattern insights`

```
I built Reflect because I couldn't answer a simple question:
"How have the last few weeks been?"

It's a guided weekly review tool. Every Sunday, you answer
5 structured prompts:

1. Progress — what moved forward (work output + how you
   worked with others)
2. Friction — where you felt resistance (tasks, people,
   or yourself)
3. Energy — 1-10 rating
4. Signal moment — what interaction is still on your mind
5. Intentions — what matters most next week, and why

On the free tier, you get the check-in + history. On Pro
(£7.99/mo), AI reads across your full history and surfaces
patterns — recurring friction themes, energy correlations,
interpersonal dynamics you're not noticing.

Stack: Spring Boot 3.3 (Java 21), Next.js 14, PostgreSQL
(Neon), Redis (Upstash), Railway + Vercel. Monolith.
AI insights via Claude API.

Also building MCP integration so you can submit check-ins
from inside Claude or Cursor without leaving your AI tools.

I've been doing these weekly reviews manually for 60+ weeks
and the pattern detection is what made me think this should
be a product — re-reading entries across months reveals
things you genuinely can't see from inside your own weeks.

Would love feedback on the prompt design and the AI insight
quality.

[link]
```

### 7.2 Technical Details to Include in Comments

- Why a monolith (ADR-001, solo founder constraints)
- How the AI pattern detection works (4 specialist agents, not one generic prompt)
- MCP integration architecture
- Why structured prompts vs free-form journaling (data consistency enables AI)
- Flyway migrations, PostgreSQL schema design
- Free tier economics (how you can afford to offer it)

---

## 8. Landing Page Copy

### 8.1 Hero Section

**Headline:** "Know how your weeks actually went."

**Subheadline:** "Reflect is a guided weekly review for working professionals. Five prompts. Ten minutes every Sunday. AI that reads across your entries to reveal patterns you can't see yourself."

**CTA:** "Start your first check-in — free"

**Microcopy:** "No credit card required. Free tier is free forever."

### 8.2 Problem Section

**Headline:** "You're not bad at reflection. You just don't have a system for it."

```
Most working professionals have no structured way to process
their weeks. You finish Friday, start Monday, and months blur
together.

Journaling apps give you a blank page and hope for the best.
Habit trackers count the days but miss the meaning. Notion
templates work for two weeks, then collect dust.

The problem isn't discipline. It's design.
```

### 8.3 How It Works

**Headline:** "10 minutes. Every Sunday. That's it."

**Step 1: Check in**
Answer five guided prompts about your week. Rate your energy. Submit. Done.

**Step 2: Build your record**
Each check-in adds to a private, searchable history. See energy over time. Track streaks. Notice what you keep coming back to.

**Step 3: See your patterns**
On Pro, AI reads across your full history and surfaces insights you can't see yourself. Not generic advice — specific observations from your own words.

### 8.4 Prompts Showcase

| Prompt | What it surfaces |
|--------|-----------------|
| **Progress** — "What moved forward this week?" | Accomplishments you'd forget by Monday |
| **Friction** — "Where did you feel resistance?" | Avoidance patterns and interpersonal tension you normalise |
| **Energy** — Rate 1-10 | An objective trendline across months |
| **Signal moment** — "What's still on your mind?" | Moments your subconscious flagged as significant |
| **Next week** — "What matters most, and why?" | Intention-setting that creates accountability |

### 8.5 AI Insights Preview

**Headline:** "Patterns you can't see from inside your own weeks."

Example insight cards:

> "You've mentioned friction with context-switching in 6 of your last 8 entries. Your energy averages 4.2 in those weeks versus 7.1 in weeks without it."

> "Your signal moments over the past month all involve the same team dynamic — a conversation where you felt unheard."

> "Weeks where your intention is specific correlate with energy ratings 2.3 points higher than weeks with broad intentions."

### 8.6 Pricing Section

**Headline:** "Simple pricing. No surprises."

| | Free | Pro — £7.99/mo | Pro — £59.99/yr |
|---|---|---|---|
| | Build the habit | Unlock the patterns | Save 37% |
| Weekly check-ins (5 prompts) | Yes | Yes | Yes |
| Check-in history | Last 4 weeks | Full history | Full history |
| Energy tracking | Current streak | Full trendline + analytics | Full trendline + analytics |
| Streak tracking | Yes | Yes | Yes |
| AI pattern insights | — | Yes | Yes |
| Full history search | — | Yes | Yes |
| Data export | — | Yes | Yes |

### 8.7 FAQ Section

1. **What are "AI pattern insights"?** After 4+ check-ins, AI reads across your history and identifies recurring themes, energy correlations, and friction patterns. Specific observations, not generic advice.

2. **How long does a check-in take?** About 10 minutes. The prompts are designed to be answerable without deep deliberation.

3. **Is my data private?** Yes. Encrypted database. Claude API (no training on inputs). No selling or sharing. Export or delete anytime.

4. **Why weekly and not daily?** Daily captures noise. Weekly captures signal. It's also dramatically more sustainable.

5. **Can I use this with my team?** Currently individual only. Team features on the roadmap.

6. **What if I miss a week?** Nothing bad happens. No guilt mechanism. Pick up next Sunday.

7. **Can I see a sample AI insight?** "In 5 of your last 7 entries, friction mentions meetings. Your average energy in those weeks is 4.8, compared to 7.6 in weeks where friction is task-specific."

8. **What happens when I cancel Pro?** You keep all past check-ins and can continue on the free tier. AI insights pause. Data never deleted.

### 8.8 Final CTA

**Headline:** "Your next week starts with understanding this one."
**CTA:** "Start your first check-in — free"
**Microcopy:** "No credit card required."

---

## 9. Email Templates

### 9.1 Welcome Email (After Signup)

**Subject:** Welcome to Reflect — here's how your first Sunday works

```
Hi [first name],

Welcome. Here's what happens next:

This Sunday, you'll get a reminder to complete your first
check-in. It takes about 10 minutes. Five guided prompts about
your week. That's it.

After your first check-in, you start building a private history.
After four, if you're on Pro, the AI begins surfacing patterns.

One suggestion: don't overthink your answers. The prompts work
best when you write what comes to mind first. Reflection isn't
about crafting perfect prose — it's about honest capture.

See you Sunday.

— Jeff
```

### 9.2 "Complete Your First Check-In" Nudge (Day 3)

**Subject:** Your first check-in is waiting

```
Hi [first name],

You signed up for Reflect [X] days ago but haven't completed
your first check-in yet.

The first one is the hardest because you don't know what to
expect. The truth: it takes about 10 minutes, the prompts do
the heavy lifting, and most people are surprised by what comes
up when they actually sit with the questions.

[Complete your first check-in ->]

— Jeff
```

### 9.3 "4-Week Streak" Celebration

**Subject:** Four weeks in a row.

```
Hi [first name],

You've completed four consecutive weekly check-ins. That's
not a small thing.

Most people who try weekly reflection don't make it past
week two. You've built a month of structured self-awareness.

If you're on Pro: Your first AI pattern insights are being
generated. Check your dashboard.

If you're on Free: Look back at your four weeks. Notice
anything? The friction you mentioned in week 1 — did it
show up again?

Four weeks is when this shifts from "something I'm trying"
to "something I do."

Here's to week five.

— Jeff
```

### 9.4 "Your First AI Insight Is Ready"

**Subject:** Your first AI insight is in

```
Hi [first name],

The AI has finished reading across your check-in history.
Your first pattern insight is ready.

[See your insight ->]

A note: this isn't a summary of what you wrote. It's a
connection across entries — a pattern or recurring theme
that's difficult to see from inside your own weeks.

When one lands — when the AI points at something you've been
half-aware of but never articulated — that's the moment
this tool earns its keep.

— Jeff
```

### 9.5 "Free Tier Ending" Upgrade Prompt

**Subject:** You've built something worth keeping

```
Hi [first name],

You've used your free check-ins and built a real review history.
That's valuable — and it's yours regardless of what you decide.

Here's what Pro adds:

- AI pattern insights across your full history
- Full history access (not just last 4 weeks)
- Energy analytics and trendline
- Full-text search across all entries
- Data export

£7.99/month or £59.99/year (saves 37%).

If the free tier is serving you well, keep using it. Seriously.
But if you're curious about the patterns hiding in your entries
— now's a good time to look.

[Upgrade to Pro ->]

— Jeff
```

### 9.6 Sunday Reminder (3 Variations — Rotate Weekly)

**V1:** Subject: `Sunday check-in`
```
Your weekly check-in is ready. Five prompts, about 10 minutes.

[Start your check-in ->]
```

**V2:** Subject: `How was your week?`
```
Before this week fades into the next — take 10 minutes to
capture what happened. Your prompts are waiting.

[Check in now ->]
```

**V3:** Subject: `Before Monday arrives`
```
Monday will bring its own agenda. Before it does, take a few
minutes to process the week that just happened.

[Start your check-in ->]
```

---

## 10. LinkedIn

### 10.1 Launch Day Post

```
I built a tool because I couldn't answer a simple question:
"How have your last few weeks been?"

I'd sit in a 1:1 and blank. Not because things were bad —
because I genuinely couldn't remember.

So I built Reflect.

Every Sunday, you answer five specific prompts:
-> What moved forward
-> Where you felt resistance
-> Your energy level (1-10)
-> What interaction is still on your mind
-> What matters most next week

Takes about 10 minutes. Over time, AI reads across your
entries and surfaces patterns you can't see yourself — like
the fact that your energy drops every week you mention
context-switching, or that the same interpersonal friction
has appeared in 6 of your last 8 check-ins.

It's not journaling. It's structured self-awareness for people
who want to understand their working patterns.

Free tier is genuinely free. Pro adds AI pattern insights for
£7.99/mo.

[link]
```

### 10.2 Follow-Up Templates

**Week 2 — The Insight Post:** Share a (anonymised) example AI insight that resonated with early users.

**Week 3 — The Retention Post:** Share retention data and why the weekly habit sticks better than daily journaling.

**Week 4 — Personal Reflection:** Share what you've learned about yourself from using your own product for X weeks.

---

## 11. Claude Code Prompts for Content

### 11.1 Weekly Tweet Batch

```
Generate 10 tweets for my Twitter/X build-in-public account.

Product: Reflect — guided weekly review app with AI pattern
insights for knowledge workers.
Pricing: £7.99/mo, £59.99/yr.
Audience: engineers, PMs, freelancers, knowledge workers.

This week's context:
- What I shipped: [describe]
- Technical decisions: [describe or "none"]
- Current metrics: [signups, active users, revenue]
- Something I learned/struggled with: [describe]
- User feedback: [describe or "none yet"]

Generate across these categories:
- 2 build-in-public updates
- 2 thought leadership (productivity/reflection)
- 2 behind-the-scenes (technical/design)
- 2 engagement tweets (questions/polls/hot takes)
- 2 personal founder story moments

Requirements:
- Under 280 chars each (or mark as multi-tweet)
- No hashtags
- Max 1 emoji per tweet, max 3 tweets with emojis
- Conversational tone, not marketing-speak
- Reference Reflect features where natural
```

### 11.2 Blog Post Generator

```
Write a complete SEO blog post for the Reflect blog.

Title: [title]
Target keyword: [keyword]
Word count: 1,500-2,000 words
Outline: [paste outline from Section 4]

Requirements:
- Use the target keyword naturally 4-6 times
- H2 and H3 headings with descriptive text
- Short paragraphs (2-4 sentences)
- Include 1-2 personal anecdotes or specific examples
- Mention Reflect once in the body (where natural) and
  once in a closing CTA section
- Tone: calm, direct, evidence-based. Not salesy.
- Include a meta description (155 chars max)
- Write for someone who is curious about weekly reviews
  but hasn't started one yet
```

### 11.3 Blog to Newsletter Converter

```
Convert this blog post into a newsletter issue for "The Weekly
Review" newsletter.

Blog post: [paste full text]

Newsletter format:
- Opening (2-3 sentences — personal hook)
- Main insight (200-300 words — the core idea, condensed)
- Building Reflect section (100-150 words — what I shipped)
- Question for the reader (one reflection prompt)
- Closing (1 sentence)

Requirements:
- Much shorter than the blog post (500-700 words total)
- First-person, conversational
- Don't summarise the blog — distil the core insight
- Include a link to the full blog post
- Subject line: intriguing, not clickbait, under 50 chars
```

### 11.4 Reddit Comment Generator

```
I need to write a helpful Reddit comment in response to this
thread.

Subreddit: [subreddit name]
Thread title: [title]
Thread content: [paste or summarise]

My expertise: I've done structured weekly reviews for 60+ weeks
using a 5-question framework (progress, friction, energy, signal
moment, intentions). I built a product called Reflect but should
NOT mention it unless directly relevant and natural.

Requirements:
- Genuinely helpful first — product mention only if natural
- Share specific personal experience, not generic advice
- Match the subreddit's tone and culture
- Under 300 words
- No links unless they genuinely help the person
```

### 11.5 Monthly Content Calendar

```
Generate a full month of content for Reflect's marketing.

Current month: [month]
Growth stage: [pre-launch / beta / launched / scaling]
This month's milestones: [describe]

Generate:
1. TWITTER: 12 tweets/week (Mon-Fri 2/day + Sat-Sun 1/day)
   - Mix of all 5 pillars
   - 1 thread per week (outline only)
   - Mark each with day and time slot

2. BLOG: 2-3 article outlines with titles and target keywords

3. NEWSLETTER: 4 weekly issue outlines with subject lines

4. REDDIT: 3-4 comment topics to look for and contribute to

5. LINKEDIN: 2 post drafts

Mark everything with suggested publish dates.
```

### 11.6 Metrics Analysis

```
Analyse these marketing metrics for Reflect and suggest actions.

This month's numbers:
- Twitter: [followers, impressions, engagement rate, top tweet]
- Blog: [page views, top post, organic search traffic, keywords ranking]
- Newsletter: [subscribers, open rate, click rate]
- Reddit: [karma gained, top comment/post]
- Signups: [total, by source if known]
- Conversions: [free to pro, conversion rate]
- Churn: [monthly rate]
- MRR: [amount]

Questions to answer:
1. Which channel is producing the most signups per hour invested?
2. What content type/topic is resonating most?
3. What should I do more of next month?
4. What should I stop doing?
5. Are there any warning signs in the data?

Be specific and actionable. Don't just say "keep doing what works."
Tell me exactly what to change.
```

---

## 12. Monitoring & Analytics

### 12.1 Tools Setup

| Tool | Purpose | Cost |
|------|---------|------|
| **Plausible** or **Umami** | Website analytics (privacy-first) | ~£7/mo or self-hosted free |
| **Google Search Console** | SEO performance, keyword rankings | Free |
| **Stripe Dashboard** | Revenue, conversions, churn | Included |
| **Twitter Analytics** | Tweet performance | Free |
| **Beehiiv** | Newsletter metrics | Free tier available |
| **Internal dashboard** | Check-in completion, WAU, streaks | Build into admin panel |

### 12.2 Weekly Metrics Check (Every Monday, 30 min)

- [ ] New signups (total + by referral source)
- [ ] Check-in completion rate (% of active users who checked in)
- [ ] Twitter: impressions, followers, best tweet
- [ ] Blog: page views, top post, new organic keywords
- [ ] Newsletter: open rate, click rate, new subscribers

### 12.3 Monthly Metrics Review (First Monday of month, 1 hr)

- [ ] MRR and MRR growth rate
- [ ] Free-to-Pro conversion rate
- [ ] Monthly churn rate
- [ ] LTV estimate (MRR / churn rate)
- [ ] CAC by channel (if tracking referral sources)
- [ ] Blog: total organic traffic, keyword rankings movement
- [ ] Newsletter: subscriber growth, average open rate
- [ ] Twitter: follower growth, engagement trend
- [ ] AI insight ratings (average thumbs up/down)

### 12.4 Quarterly Health Check

- [ ] Is MRR growing month-over-month? If flat 2+ months, diagnose.
- [ ] Is churn improving as data moat builds? If not, AI quality is the issue.
- [ ] Which channel drives the most Pro conversions? Double down.
- [ ] Are AI insights rated >4/5? If not, iterate prompts before new features.
- [ ] Interview 3-5 churned users. What was the reason?
- [ ] Review: is the week-by-week plan on track? What needs adjusting?

---

## 13. Weekly Execution Calendar

### Ongoing Weekly Routine (2-3 hours marketing)

| Day | Activity | Time |
|-----|----------|------|
| **Monday** | Post shipping update tweet. Check weekend metrics. | 20 min |
| **Tuesday** | Post thought leadership tweet. 15 min Reddit commenting. | 30 min |
| **Wednesday** | Post behind-the-scenes tweet. Draft blog content. | 45 min |
| **Thursday** | Post thread. Continue blog draft. | 30 min |
| **Friday** | Post personal/engagement tweet. Finalise blog post. | 30 min |
| **Saturday** | Light engagement (quote tweet, replies). | 15 min |
| **Sunday** | Do own weekly review. Post ritual tweet. Send newsletter. | 30 min |

### Monthly Extras

- **Week 1:** Publish blog post #1 of the month
- **Week 2:** Publish blog post #2. Post IndieHackers monthly update.
- **Week 3:** Review and analyse monthly metrics. Adjust strategy.
- **Week 4:** Plan next month's content calendar (use Claude Code prompt 11.5)

---

## Appendix: Quick-Reference Cheat Sheet

### When Someone Asks "What Is Reflect?"

**5-second answer:** "A weekly review app with AI pattern insights."

**15-second answer:** "Five structured prompts every Sunday — progress, friction, energy, what's on your mind, and intentions. AI reads across your weeks to show you patterns you can't see yourself. £7.99/mo."

**60-second answer:** "Most professionals have no system for learning from their own weeks. Reflect gives you 5 guided prompts every Sunday that take about 10 minutes. You rate your energy, write about friction, capture the moment still on your mind. Over time, AI reads across your entries and finds things like 'you mention the same interpersonal tension in 6 of 8 weeks' or 'your energy drops 2 points every week you skip deep work.' It's not journaling — it's structured self-awareness. Free tier for the check-ins, Pro at £7.99/mo for the AI insights."
