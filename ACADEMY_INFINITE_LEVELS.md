# Academy Games: Infinite Level System

## Overview

All academy games in PassForge now support effectively infinite levels (up to 1000+), allowing players to continuously improve their cybersecurity skills without hitting arbitrary level caps.

## Game Scaling

### 1. Memory Match 🧠

**Difficulty Progression:**
- **Levels 1-5:** BEGINNER (3 decoys, 10s memorize time, 50 XP)
- **Levels 6-10:** EASY (5 decoys, 8s memorize time, 75 XP)
- **Levels 11-20:** MEDIUM (7 decoys, 7s memorize time, 100 XP)
- **Levels 21-35:** HARD (9 decoys, 6s memorize time, 150 XP)
- **Levels 36-1000+:** EXPERT (11 decoys, 5s memorize time, 200 XP)

**Infinite Scaling:**
- After level 35, all games remain at EXPERT difficulty
- Password length and decoy similarity continue to challenge players
- XP rewards remain consistent at 200 per success

### 2. Password Cracker 🔓

**Tier Progression:**
- **Tier 1 (Levels 1-5):** Basic weak passwords (password, 123456, qwerty)
- **Tier 2 (Levels 6-10):** Simple variations (password1, 12345678)
- **Tier 3 (Levels 11-15):** Basic complexity (Password1, Welcome1)
- **Tier 4 (Levels 16-20):** Simple substitutions (P@ssword, Passw0rd)
- **Tier 5 (Levels 21-30):** Medium complexity (Password123, Welcome2024)
- **Tier 6 (Levels 31-40):** Better patterns (P@ssw0rd1, Summer2024)
- **Tier 7 (Levels 41-50):** Personal info (MyPassword, JohnDoe123)
- **Tier 8 (Levels 51+):** Complex weak passwords (P@ssw0rd!, Tr0ub4dor&3)

**Hints & Time Limits:**
- **Levels 1-10:** 3 hints, 60 seconds
- **Levels 11-25:** 2 hints, 45 seconds
- **Levels 26-50:** 1 hint, 30 seconds
- **Levels 51-100:** 1 hint, 25 seconds
- **Levels 101-200:** 0 hints, 20 seconds
- **Levels 201-500:** 0 hints, 15 seconds
- **Levels 501-1000+:** 0 hints, 10 seconds (minimum 5s)

**XP Rewards:**
- Base: 50 XP
- Tier bonus: +10 XP per tier
- Weakness bonus: +5 XP per identified weakness

### 3. Phishing Hunter 🎣

**Difficulty Progression:**
- **Levels 1-10:** OBVIOUS (30 XP)
  - HTTP URLs, obvious typos (g00gle.com)
  - Clear suspicious patterns
  - Multiple red flags
  
- **Levels 11-25:** SUBTLE (60 XP)
  - HTTPS but suspicious domains
  - More sophisticated social engineering
  - Fewer obvious red flags
  
- **Levels 26-100+:** SOPHISTICATED (100 XP + bonus)
  - Nearly legitimate-looking URLs
  - Professional language and formatting
  - Minimal red flags requiring careful inspection

**Infinite Scaling:**
- After level 100, bonus XP increases: +5 XP per 10 levels
- Example: Level 150 = 100 + 25 = 125 XP

### 4. Social Engineering 🎭

**Scenario Types (Cycles):**
1. **Pretexting** - False scenarios to extract information
2. **Baiting** - Tempting offers to lure victims
3. **Quid Pro Quo** - "Something for something" exchanges
4. **Tailgating** - Physical access manipulation
5. **Impersonation** - Fake identity attacks

**XP Rewards:**
- Base: 75 XP + (tactics × 10 XP)
- After level 50: +10 XP per 10 levels

**Infinite Scaling:**
- Scenarios cycle through all 5 types infinitely
- XP rewards scale with level
- Example: Level 150 = 75 + 40 + 100 = 215 XP (varies by tactics)

## XP Requirements for Player Levels

The player's overall academy level requires exponentially increasing XP:

```
Level 1 → 2: 100 XP (total: 100)
Level 2 → 3: 150 XP (total: 250)
Level 3 → 4: 200 XP (total: 450)
Level 4 → 5: 250 XP (total: 700)
...
```

**Formula:** `25 × (level - 1) × (level + 2)`

This means that even with infinite game levels, player progression remains balanced and challenging.

## Technical Implementation

### Key Changes

1. **Password Cracker** (`GeneratePasswordCrackerLevelUseCase`):
   - Tier calculation now scales beyond level 50
   - Time limits decrease progressively to level 500+
   - Hints available scale more gradually

2. **Phishing Hunter** (`GeneratePhishingScenarioUseCase`):
   - XP rewards include bonus scaling after level 100
   - Difficulty caps at SOPHISTICATED but rewards continue to increase

3. **Social Engineering** (`GenerateSocialEngineeringScenarioUseCase`):
   - XP rewards scale with level after level 50
   - Scenario types cycle infinitely

4. **Memory Match** (`GameDifficulty.fromLevel`):
   - Difficulty caps at EXPERT after level 35
   - Game remains challenging through password complexity

### No Hard Caps

- No maximum level variable (`MAX_LEVEL`) exists in the codebase
- Level numbers are simple integers that can grow indefinitely
- All game generation logic handles arbitrary level numbers gracefully

## Game Unlocking

Games unlock based on player's overall academy level:

- **Level 1:** Memory Match 🧠
- **Level 3:** Password Cracker 🔓
- **Level 5:** Phishing Hunter 🎣
- **Level 7:** Social Engineering 🎭

## Benefits

1. **Continuous Learning:** Players can always improve their skills
2. **No Artificial Limits:** Game doesn't feel "finished"
3. **Balanced Progression:** Higher levels become appropriately challenging
4. **Fair XP Rewards:** XP scales to match difficulty
5. **Replayability:** Each game type offers 1000+ unique challenges

## Testing

Run unit tests to verify the scaling:

```bash
./gradlew test --tests "*PasswordCrackerLevel*"
./gradlew test --tests "*PhishingScenario*"
./gradlew test --tests "*MemoryGame*"
```

## Future Enhancements

Potential improvements for even better infinite scaling:

1. **Dynamic Scenario Generation:** Use AI to generate unique scenarios at high levels
2. **Community Content:** User-submitted scenarios for variety
3. **Difficulty Modifiers:** Custom challenges (e.g., no hints, reduced time)
4. **Leaderboards:** Compare progress with other players
5. **Achievement System:** Special rewards for reaching milestones (100, 500, 1000)

---

Last Updated: January 17, 2026
Version: 3.0.0
