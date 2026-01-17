# PassForge Academy: New Features Design

## 1. Security Breach Detection Lesson 🚨

### Concept
Educational lesson teaching users how to recognize when they've been hacked or compromised.

### Topics Covered

#### 1.1 WiFi Network Compromise Signs
- **Unusual devices in network:** Unknown MAC addresses, suspicious device names
- **Slow internet speed:** Bandwidth being used by attackers
- **Changed router settings:** Password changed, new port forwards, DNS hijacked
- **Router admin page warns:** Firmware outdated, unknown logins
- **Strange network activity:** Unexplained data usage, unusual traffic patterns
- **WiFi password known by others:** Neighbors have access, password shared

#### 1.2 Phone/Computer Compromise Signs
- **Battery drains faster:** Malware running in background
- **Overheating:** Crypto miners or botnets active
- **Slower performance:** Resources being used by malicious software
- **Unknown apps installed:** Trojans disguised as legitimate apps
- **Increased data usage:** Information being exfiltrated
- **Pop-ups and ads:** Adware infection
- **Camera/mic indicators:** Active when you're not using them
- **Changed settings:** Security settings disabled, unknown accounts added
- **Strange emails sent:** Your account sending spam
- **Bank transactions:** Unauthorized charges, unfamiliar purchases
- **Two-factor codes received:** Someone trying to access your accounts
- **Files encrypted:** Ransomware attack

#### 1.3 Account Compromise Signs
- **Login notifications:** From unknown locations or devices
- **Changed passwords:** Can't log in with your usual password
- **New sessions active:** See active sessions you didn't start
- **Email forwarding rules:** Your emails being forwarded elsewhere
- **Changed recovery info:** Phone number or email changed
- **Unusual activity:** Posts you didn't make, messages you didn't send
- **Account locked:** Too many failed login attempts

### Interactive Quiz
15 scenario-based questions where users identify whether a situation indicates a breach.

### Lesson Structure
```kotlin
Lesson(
    id = "breach_detection",
    titleEn = "🚨 How to Detect Security Breaches",
    titleRu = "🚨 Как распознать взлом",
    descriptionEn = "Learn to identify when your devices and accounts have been compromised",
    descriptionRu = "Научись определять, когда твои устройства и аккаунты взломаны",
    xpReward = 150,
    difficulty = LessonDifficulty.ADVANCED
)
```

---

## 2. Network Defense Game 🛡️

### Game Concept
Tower defense-style game where you build and configure network security to defend against cyber attacks.

### Game Mechanics

#### 2.1 Core Gameplay
- **Your Home Network:** Starts vulnerable with default router settings
- **Incoming Threats:** Waves of different attack types (viruses, worms, hackers, DDoS)
- **Defense Building:** Place and configure security measures
- **Resource Management:** Limited "security budget" to spend on defenses
- **Progressive Difficulty:** More sophisticated attacks in later levels

#### 2.2 Defense Types

**Layer 1: Network Perimeter**
- **Firewall** (Basic)
  - Cost: 100 points
  - Blocks 60% of attacks
  - Upgrade: Stateful inspection (+20%)
  
- **NAT (Network Address Translation)**
  - Cost: 80 points
  - Hides internal IP addresses
  - Makes you "invisible" to port scanners

- **Intrusion Detection System (IDS)**
  - Cost: 150 points
  - Alerts you to attacks in progress
  - Can auto-block suspicious IPs

**Layer 2: Device Security**
- **Antivirus/Antimalware**
  - Cost: 120 points
  - Blocks viruses and trojans
  - Needs regular updates (resource cost)

- **Device Hardening**
  - Cost: 100 points
  - Disables unnecessary services
  - Reduces attack surface

**Layer 3: Application Security**
- **VPN (Virtual Private Network)**
  - Cost: 150 points
  - Encrypts traffic
  - Prevents MITM attacks

- **DNS Filtering**
  - Cost: 80 points
  - Blocks access to malicious domains
  - Prevents phishing

**Layer 4: Advanced Defenses**
- **Honeypot**
  - Cost: 200 points
  - Decoy system to waste attacker's time
  - Provides intelligence on attack methods

- **SIEM (Security Information & Event Management)**
  - Cost: 250 points
  - Correlates security events
  - Predicts and prevents attacks

#### 2.3 Attack Types

**Wave 1-5: Basic Attacks**
- **Port Scanner:** Tries to find open ports
- **Brute Force:** Attempts to guess passwords
- **Malware Email:** Tries to infect via phishing

**Wave 6-10: Intermediate Attacks**
- **SQL Injection:** Attacks web applications
- **DDoS:** Overwhelms network bandwidth
- **Man-in-the-Middle:** Intercepts communications
- **Ransomware:** Encrypts files if it gets through

**Wave 11-20: Advanced Attacks**
- **Zero-Day Exploit:** Unknown vulnerability
- **APT (Advanced Persistent Threat):** Stealthy, long-term attack
- **Social Engineering:** Tricks users into disabling security
- **Supply Chain Attack:** Compromised software update

**Wave 21+: Elite Attacks**
- **Nation-State Actor:** Highly sophisticated, multi-vector attack
- **Polymorphic Malware:** Changes signature to evade detection
- **Fileless Attack:** Runs in memory, hard to detect
- **AI-Powered Attack:** Adapts to your defenses

#### 2.4 Level Progression

**Tutorial (Level 1)**
- Basic firewall setup
- One attack type (port scanner)
- Learn interface

**Beginner (Levels 2-10)**
- 3-5 waves per level
- 2-3 attack types
- Budget: 500 points
- Time: 5 minutes per level

**Intermediate (Levels 11-25)**
- 5-7 waves per level
- 3-5 attack types (can combine)
- Budget: 800 points
- Time: 7 minutes per level
- New mechanic: Attack variants

**Advanced (Levels 26-50)**
- 7-10 waves per level
- 4-6 attack types (often combined)
- Budget: 1200 points
- Time: 10 minutes per level
- New mechanic: Social engineering (random security disable)

**Expert (Levels 51-100)**
- 10-15 waves per level
- 5-8 attack types
- Budget: 1500 points
- Time: 12 minutes per level
- New mechanic: Zero-day vulnerabilities (random defense failure)

**Elite (Levels 101-1000)**
- Endless survival mode
- All attack types active
- Adaptive AI attackers
- Budget scales with level
- Leaderboards

#### 2.5 Educational Tooltips

Each defense and attack includes educational content:

```kotlin
Defense(
    name = "Firewall",
    description = "Network security system that monitors and controls traffic",
    educationalInfo = """
        A firewall acts as a barrier between trusted internal networks 
        and untrusted external networks. It examines packets and decides 
        whether to allow or block them based on configured rules.
        
        Types:
        • Packet-filtering: Basic, checks packet headers
        • Stateful: Tracks connection state
        • Application-layer: Deep packet inspection
        
        Real-world: Your home router has a basic firewall built-in!
    """,
    wikipediaLink = "https://en.wikipedia.org/wiki/Firewall_(computing)"
)
```

#### 2.6 XP & Rewards

**Per Level:**
- Base XP: 75
- Perfect defense (0 breaches): +50 XP bonus
- Speed bonus (finished quickly): +25 XP
- Efficiency bonus (minimal defenses): +25 XP

**Achievements:**
- 🛡️ **First Defense:** Complete level 1
- 🔥 **Firewall Master:** Block 1000 attacks with firewall
- 🕵️ **Honeypot Expert:** Catch 100 attackers in honeypot
- 🎯 **Perfect Defense:** Complete 10 levels with 0 breaches
- 📊 **SIEM Specialist:** Use SIEM to predict 50 attacks
- 🌐 **NAT Ninja:** Hide your network for 100 levels
- 🔒 **Security Architect:** Unlock all defense types

### Game UI Design

```
┌─────────────────────────────────────────────────────────┐
│  Network Defense 🛡️                    Level 15 | Wave 3/7 │
├─────────────────────────────────────────────────────────┤
│  ❤️ Network Health: ████████░░ 80%                       │
│  💰 Budget: 450 pts | ⏱️ Time: 04:32 | 🎯 Blocked: 23   │
├─────────────────────────────────────────────────────────┤
│                                                          │
│     Internet ☁️   →  [Firewall] → [NAT] → 🏠 Your Home  │
│                                                          │
│     Attack 1: Port Scanner 🔍 → ❌ Blocked by Firewall   │
│     Attack 2: DDoS 💥 → ⚠️ Partially blocked            │
│     Attack 3: Malware 🦠 → ❌ Blocked by Antivirus      │
│                                                          │
├─────────────────────────────────────────────────────────┤
│  Available Defenses:                                     │
│  [🔥 Firewall: 100] [🔒 NAT: 80] [👁️ IDS: 150]         │
│  [🛡️ Antivirus: 120] [🔐 VPN: 150] [🍯 Honeypot: 200] │
├─────────────────────────────────────────────────────────┤
│  Next Wave: Advanced Persistent Threat in 00:15         │
│  💡 Tip: APTs are stealthy. Consider using IDS!         │
└─────────────────────────────────────────────────────────┘
```

---

## 3. Implementation Plan

### Phase 1: Data Models (Week 1)
- [ ] Create `NetworkDefenseLevel` model
- [ ] Create `DefenseType` enum with all security measures
- [ ] Create `AttackType` enum with all threat types
- [ ] Create `NetworkDefenseState` for game state management
- [ ] Add breach detection lesson to `Lessons.kt`

### Phase 2: Game Logic (Week 2)
- [ ] Implement `GenerateNetworkDefenseLevelUseCase`
- [ ] Implement defense placement logic
- [ ] Implement attack wave generation
- [ ] Implement damage calculation
- [ ] Add budget management

### Phase 3: UI (Week 3)
- [ ] Create `NetworkDefenseScreen.kt`
- [ ] Create defense selection UI
- [ ] Create network visualization
- [ ] Add attack animations
- [ ] Add educational tooltips

### Phase 4: Integration (Week 4)
- [ ] Add game to `AcademyGame` enum
- [ ] Set unlock level (recommend: Level 10)
- [ ] Add game to navigation
- [ ] Integrate with XP system
- [ ] Add achievements

### Phase 5: Content (Week 5)
- [ ] Add breach detection lesson content (EN/RU)
- [ ] Write educational descriptions for all defenses
- [ ] Write educational descriptions for all attacks
- [ ] Add quiz questions
- [ ] Create tutorial level

### Phase 6: Testing & Polish (Week 6)
- [ ] Unit tests for game logic
- [ ] UI tests for game screen
- [ ] Balance testing (difficulty curve)
- [ ] Performance optimization
- [ ] Localization review

---

## 4. Data Models (Initial Implementation)

### 4.1 Game Models

```kotlin
enum class DefenseType(
    val displayName: String,
    val cost: Int,
    val effectiveness: Float,
    val educationalInfo: String
) {
    FIREWALL(
        "Firewall",
        100,
        0.6f,
        "Network security system monitoring and controlling traffic"
    ),
    NAT(
        "NAT",
        80,
        0.5f,
        "Hides internal IP addresses from external networks"
    ),
    IDS(
        "Intrusion Detection",
        150,
        0.7f,
        "Monitors network for suspicious activity"
    ),
    ANTIVIRUS(
        "Antivirus",
        120,
        0.65f,
        "Detects and removes malicious software"
    ),
    VPN(
        "VPN",
        150,
        0.75f,
        "Encrypts all network traffic"
    ),
    DNS_FILTER(
        "DNS Filtering",
        80,
        0.55f,
        "Blocks access to malicious domains"
    ),
    HONEYPOT(
        "Honeypot",
        200,
        0.8f,
        "Decoy system to trap and study attackers"
    ),
    SIEM(
        "SIEM",
        250,
        0.85f,
        "Correlates security events to predict attacks"
    )
}

enum class AttackType(
    val displayName: String,
    val damage: Int,
    val speed: Float,
    val weakness: DefenseType?
) {
    PORT_SCAN("Port Scanner", 5, 1.0f, DefenseType.FIREWALL),
    BRUTE_FORCE("Brute Force", 10, 0.8f, DefenseType.FIREWALL),
    MALWARE("Malware", 15, 0.7f, DefenseType.ANTIVIRUS),
    PHISHING("Phishing Email", 12, 0.9f, DefenseType.DNS_FILTER),
    DDOS("DDoS", 20, 1.2f, DefenseType.FIREWALL),
    MITM("Man-in-the-Middle", 18, 0.6f, DefenseType.VPN),
    SQL_INJECTION("SQL Injection", 16, 0.8f, DefenseType.FIREWALL),
    RANSOMWARE("Ransomware", 30, 0.5f, DefenseType.ANTIVIRUS),
    ZERO_DAY("Zero-Day Exploit", 25, 0.7f, null),
    APT("Advanced Persistent Threat", 35, 0.4f, DefenseType.IDS),
    SOCIAL_ENGINEERING("Social Engineering", 20, 0.9f, null)
}

data class NetworkDefenseLevel(
    val levelNumber: Int,
    val waves: List<AttackWave>,
    val budget: Int,
    val timeLimit: Int, // seconds
    val xpReward: Int
)

data class AttackWave(
    val waveNumber: Int,
    val attacks: List<Attack>,
    val delayBeforeNextWave: Int // seconds
)

data class Attack(
    val type: AttackType,
    val count: Int,
    val arrivalDelay: Float // seconds between attacks
)

data class PlacedDefense(
    val type: DefenseType,
    val level: Int = 1,
    val isActive: Boolean = true
)

data class NetworkDefenseState(
    val level: NetworkDefenseLevel? = null,
    val currentWave: Int = 0,
    val placedDefenses: List<PlacedDefense> = emptyList(),
    val networkHealth: Float = 100f,
    val remainingBudget: Int = 0,
    val attacksBlocked: Int = 0,
    val attacksSuccessful: Int = 0,
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false,
    val xpEarned: Int = 0
)
```

### 4.2 Lesson Content

```kotlin
val breachDetectionLesson = Lesson(
    id = "breach_detection",
    titleEn = "🚨 How to Detect Security Breaches",
    titleRu = "🚨 Как распознать взлом",
    descriptionEn = "Learn to identify when your devices, networks, and accounts have been compromised",
    descriptionRu = "Научись определять, когда твои устройства, сети и аккаунты взломаны",
    iconEmoji = "🚨",
    difficulty = LessonDifficulty.ADVANCED,
    durationMinutes = 20,
    xpReward = 150,
    topics = listOf(
        LessonTopic(
            titleEn = "WiFi Network Compromise",
            titleRu = "Взлом WiFi сети",
            contentEn = """
                Your WiFi network is often the first target for attackers. 
                Learn to recognize these warning signs:
                
                🔴 Unknown devices connected
                🔴 Slower than usual internet speed
                🔴 Changed router admin password
                🔴 Suspicious port forwarding rules
                🔴 DNS settings changed
                🔴 Unexpected data usage spikes
            """.trimIndent(),
            contentRu = """
                Твоя WiFi сеть часто становится первой целью атак.
                Научись распознавать эти признаки:
                
                🔴 Неизвестные устройства подключены
                🔴 Медленнее обычного интернет
                🔴 Изменен пароль админа роутера
                🔴 Подозрительные правила проброса портов
                🔴 Изменены настройки DNS
                🔴 Неожиданные скачки трафика
            """.trimIndent()
        ),
        // More topics...
    ),
    quiz = listOf(
        QuizQuestion(
            questionEn = "You notice your router admin password has changed. What should you do?",
            questionRu = "Ты заметил, что пароль админа роутера изменился. Что делать?",
            options = listOf(
                QuizOption("Reset router to factory settings", "Сбросить роутер к заводским настройкам", true),
                QuizOption("Ignore it, probably forgot the password", "Игнорировать, наверное забыл пароль", false),
                QuizOption("Try common passwords", "Попробовать распространенные пароли", false),
                QuizOption("Buy a new router", "Купить новый роутер", false)
            ),
            explanationEn = "Changed router password is a clear sign of compromise. Reset to factory settings, update firmware, and set a strong new password.",
            explanationRu = "Изменённый пароль роутера - явный признак взлома. Сброс к заводским настройкам, обновление прошивки и установка нового сильного пароля необходимы."
        )
        // More questions...
    )
)
```

---

## 5. Future Enhancements

### 5.1 Multiplayer Mode
- Cooperative: Team up to defend against attacks
- Competitive: Attack other players' networks
- Leaderboards: Best defenders globally

### 5.2 Realistic Scenarios
- Based on real CVEs and attack patterns
- Monthly challenges based on recent security news
- Historical attacks (WannaCry, NotPetya, etc.)

### 5.3 Custom Networks
- Design your own network topology
- Share custom levels with community
- Rate and play user-created challenges

### 5.4 Career Path Integration
- Security Analyst track
- Network Administrator track
- Penetration Tester track
- Each unlocks specialized defenses/knowledge

---

## 6. Success Metrics

### Engagement
- Average time per game session: Target 10+ minutes
- Return rate: Target 60% daily active users
- Completion rate: Target 70% of started levels

### Education
- Quiz pass rate: Target 80%+
- User-reported learning: Survey after each lesson
- Real-world application: Track if users enable recommended security

### Monetization (Premium)
- Advanced defense types locked behind premium
- Custom network builder (premium feature)
- Ad-free experience
- Exclusive attack scenarios

---

## 7. Technical Considerations

### Performance
- Smooth 60 FPS during gameplay
- Efficient attack simulation (max 50 simultaneous attacks)
- Background processing for wave generation

### Accessibility
- Colorblind mode
- Adjustable game speed
- Tutorial can be replayed
- Educational mode (no time limit)

### Localization
- Full EN/RU support
- Community translations for other languages
- Technical terms with tooltips

---

**Version:** 1.0  
**Last Updated:** January 17, 2026  
**Status:** Design Phase - Ready for Implementation
