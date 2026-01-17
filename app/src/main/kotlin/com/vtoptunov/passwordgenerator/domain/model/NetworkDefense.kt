package com.vtoptunov.passwordgenerator.domain.model

/**
 * Network Defense Game - Tower defense style cybersecurity game
 * Players build network security defenses against waves of cyber attacks
 */

// Defense Types
enum class DefenseType(
    val displayNameEn: String,
    val displayNameRu: String,
    val cost: Int,
    val effectiveness: Float,
    val icon: String,
    val educationalInfoEn: String,
    val educationalInfoRu: String
) {
    FIREWALL(
        "Firewall",
        "Файрвол",
        100,
        0.6f,
        "🔥",
        "Network security system that monitors and controls incoming/outgoing traffic based on security rules",
        "Система сетевой безопасности, которая контролирует входящий и исходящий трафик по правилам безопасности"
    ),
    
    NAT(
        "NAT",
        "NAT",
        80,
        0.5f,
        "🔒",
        "Network Address Translation hides internal IP addresses from external networks, making you 'invisible'",
        "Трансляция сетевых адресов скрывает внутренние IP-адреса от внешних сетей, делая тебя 'невидимым'"
    ),
    
    IDS(
        "Intrusion Detection",
        "Обнаружение вторжений",
        150,
        0.7f,
        "👁️",
        "Monitors network traffic for suspicious activity and alerts you to potential threats in real-time",
        "Отслеживает сетевой трафик на предмет подозрительной активности и предупреждает о потенциальных угрозах"
    ),
    
    ANTIVIRUS(
        "Antivirus",
        "Антивирус",
        120,
        0.65f,
        "🛡️",
        "Detects, prevents, and removes malicious software like viruses, trojans, and worms",
        "Обнаруживает, предотвращает и удаляет вредоносное ПО: вирусы, трояны и черви"
    ),
    
    VPN(
        "VPN",
        "VPN",
        150,
        0.75f,
        "🔐",
        "Virtual Private Network encrypts all traffic, preventing Man-in-the-Middle attacks",
        "Виртуальная частная сеть шифрует весь трафик, предотвращая атаки типа 'человек посередине'"
    ),
    
    DNS_FILTER(
        "DNS Filtering",
        "Фильтрация DNS",
        80,
        0.55f,
        "🚫",
        "Blocks access to known malicious domains, preventing phishing and malware downloads",
        "Блокирует доступ к известным вредоносным доменам, предотвращая фишинг и загрузку вредоносов"
    ),
    
    HONEYPOT(
        "Honeypot",
        "Ловушка",
        200,
        0.8f,
        "🍯",
        "Decoy system that looks like a real target, traps attackers and provides intelligence about their methods",
        "Система-приманка, которая выглядит как настоящая цель, ловит атакующих и предоставляет информацию об их методах"
    ),
    
    SIEM(
        "SIEM",
        "SIEM",
        250,
        0.85f,
        "📊",
        "Security Information and Event Management correlates security events to predict and prevent complex attacks",
        "Система управления информацией и событиями безопасности коррелирует события для предсказания сложных атак"
    );
    
    fun canBlockAttack(attackType: AttackType): Float {
        return when {
            attackType.weakness == this -> effectiveness * 1.5f
            else -> effectiveness * 0.8f
        }
    }
}

// Attack Types
enum class AttackType(
    val displayNameEn: String,
    val displayNameRu: String,
    val damage: Int,
    val speed: Float,
    val icon: String,
    val weakness: DefenseType?,
    val descriptionEn: String,
    val descriptionRu: String
) {
    PORT_SCAN(
        "Port Scanner",
        "Сканер портов",
        5,
        1.0f,
        "🔍",
        DefenseType.FIREWALL,
        "Scans your network to find open ports and services",
        "Сканирует сеть для поиска открытых портов и сервисов"
    ),
    
    BRUTE_FORCE(
        "Brute Force",
        "Перебор паролей",
        10,
        0.8f,
        "🔨",
        DefenseType.FIREWALL,
        "Tries thousands of password combinations to break into your system",
        "Пробует тысячи комбинаций паролей для взлома системы"
    ),
    
    MALWARE(
        "Malware",
        "Вредонос",
        15,
        0.7f,
        "🦠",
        DefenseType.ANTIVIRUS,
        "Malicious software that can steal data, encrypt files, or open backdoors",
        "Вредоносное ПО, которое может красть данные, шифровать файлы или открывать бэкдоры"
    ),
    
    PHISHING(
        "Phishing Email",
        "Фишинговое письмо",
        12,
        0.9f,
        "🎣",
        DefenseType.DNS_FILTER,
        "Fake email trying to trick you into revealing passwords or downloading malware",
        "Поддельное письмо, пытающееся обманом выудить пароли или заставить скачать вредонос"
    ),
    
    DDOS(
        "DDoS",
        "DDoS",
        20,
        1.2f,
        "💥",
        DefenseType.FIREWALL,
        "Distributed Denial of Service floods your network with traffic to make it unavailable",
        "Распределённая атака отказа в обслуживании заваливает сеть трафиком"
    ),
    
    MITM(
        "Man-in-the-Middle",
        "Человек посередине",
        18,
        0.6f,
        "👤",
        DefenseType.VPN,
        "Attacker intercepts communication between you and websites to steal data",
        "Атакующий перехватывает связь между тобой и сайтами для кражи данных"
    ),
    
    SQL_INJECTION(
        "SQL Injection",
        "SQL-инъекция",
        16,
        0.8f,
        "💉",
        DefenseType.FIREWALL,
        "Exploits vulnerable web applications to access or modify database",
        "Эксплуатирует уязвимые веб-приложения для доступа к базе данных"
    ),
    
    RANSOMWARE(
        "Ransomware",
        "Шифровальщик",
        30,
        0.5f,
        "🔐",
        DefenseType.ANTIVIRUS,
        "Encrypts your files and demands payment to decrypt them",
        "Шифрует файлы и требует оплату для их расшифровки"
    ),
    
    ZERO_DAY(
        "Zero-Day Exploit",
        "0-day эксплойт",
        25,
        0.7f,
        "⚡",
        null,
        "Exploits unknown vulnerability - no defense exists yet",
        "Эксплуатирует неизвестную уязвимость - защиты пока не существует"
    ),
    
    APT(
        "Advanced Persistent Threat",
        "Продвинутая постоянная угроза",
        35,
        0.4f,
        "🎯",
        DefenseType.IDS,
        "Sophisticated, stealthy attack that stays hidden for long periods",
        "Изощрённая скрытная атака, которая остаётся незамеченной долгое время"
    ),
    
    SOCIAL_ENGINEERING(
        "Social Engineering",
        "Социальная инженерия",
        20,
        0.9f,
        "🎭",
        null,
        "Manipulates people into disabling security or revealing sensitive information",
        "Манипулирует людьми, заставляя отключить защиту или раскрыть информацию"
    ),
    
    WORM(
        "Network Worm",
        "Сетевой червь",
        22,
        1.1f,
        "🐛",
        DefenseType.FIREWALL,
        "Self-replicating malware that spreads across networks automatically",
        "Самовоспроизводящийся вредонос, который автоматически распространяется по сети"
    ),
    
    TROJAN(
        "Trojan Horse",
        "Троянский конь",
        18,
        0.7f,
        "🐴",
        DefenseType.ANTIVIRUS,
        "Disguises as legitimate software but contains malicious payload",
        "Маскируется под легитимное ПО, но содержит вредоносную нагрузку"
    ),
    
    BOTNET(
        "Botnet Attack",
        "Атака ботнета",
        28,
        0.6f,
        "🤖",
        DefenseType.IDS,
        "Network of infected computers controlled remotely to attack your system",
        "Сеть заражённых компьютеров, удалённо управляемых для атаки на систему"
    ),
    
    DNS_POISONING(
        "DNS Poisoning",
        "Отравление DNS",
        14,
        0.8f,
        "☠️",
        DefenseType.DNS_FILTER,
        "Corrupts DNS cache to redirect you to malicious websites",
        "Портит кэш DNS, чтобы перенаправить тебя на вредоносные сайты"
    ),
    
    CRYPTO_MINER(
        "Crypto Miner",
        "Крипто-майнер",
        12,
        0.9f,
        "⛏️",
        DefenseType.ANTIVIRUS,
        "Uses your device's resources to mine cryptocurrency without permission",
        "Использует ресурсы устройства для майнинга криптовалюты без разрешения"
    )
}

// Attack Wave
data class AttackWave(
    val waveNumber: Int,
    val attacks: List<Attack>,
    val delayBeforeNextWave: Int // seconds
) {
    val totalDamage: Int
        get() = attacks.sumOf { it.type.damage * it.count }
}

// Individual Attack
data class Attack(
    val type: AttackType,
    val count: Int,
    val arrivalDelay: Float // seconds between individual attacks
)

// Network Defense Level
data class NetworkDefenseLevel(
    val levelNumber: Int,
    val waves: List<AttackWave>,
    val startingBudget: Int,
    val timeLimit: Int, // seconds (0 = no limit)
    val xpReward: Int,
    val difficultyTier: DefenseDifficultyTier
)

enum class DefenseDifficultyTier {
    TUTORIAL,      // Level 1
    BEGINNER,      // Levels 2-10
    INTERMEDIATE,  // Levels 11-25
    ADVANCED,      // Levels 26-50
    EXPERT,        // Levels 51-100
    ELITE          // Levels 101+
}

// Placed Defense (in player's network)
data class PlacedDefense(
    val id: String,
    val type: DefenseType,
    val level: Int = 1,
    val isActive: Boolean = true,
    val totalBlockedAttacks: Int = 0
) {
    val upgradeCost: Int
        get() = type.cost / 2 * level
    
    val effectiveStrength: Float
        get() = type.effectiveness * (1 + level * 0.15f)
}

// Game State
data class NetworkDefenseState(
    val level: NetworkDefenseLevel? = null,
    val currentWave: Int = 0,
    val placedDefenses: List<PlacedDefense> = emptyList(),
    val networkHealth: Float = 100f,
    val remainingBudget: Int = 0,
    val attacksBlocked: Int = 0,
    val attacksSuccessful: Int = 0,
    val totalXpEarned: Int = 0,
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false,
    val isPaused: Boolean = false,
    val gameTimeElapsed: Int = 0, // seconds
    val streakBonus: Int = 0 // consecutive perfect levels
)

// Game Events
sealed class NetworkDefenseEvent {
    object StartLevel : NetworkDefenseEvent()
    data class PlaceDefense(val defenseType: DefenseType) : NetworkDefenseEvent()
    data class UpgradeDefense(val defenseId: String) : NetworkDefenseEvent()
    data class RemoveDefense(val defenseId: String) : NetworkDefenseEvent()
    object StartWave : NetworkDefenseEvent()
    object PauseGame : NetworkDefenseEvent()
    object ResumeGame : NetworkDefenseEvent()
    object RestartLevel : NetworkDefenseEvent()
    object ExitGame : NetworkDefenseEvent()
    data class ShowDefenseInfo(val defenseType: DefenseType) : NetworkDefenseEvent()
    data class ShowAttackInfo(val attackType: AttackType) : NetworkDefenseEvent()
}

// Game Result
data class NetworkDefenseResult(
    val levelNumber: Int,
    val isVictory: Boolean,
    val xpEarned: Int,
    val attacksBlocked: Int,
    val attacksSuccessful: Int,
    val networkHealthRemaining: Float,
    val timeElapsed: Int,
    val perfectDefense: Boolean, // 100% health remaining
    val speedBonus: Boolean, // finished quickly
    val efficiencyBonus: Boolean // used minimal defenses
)

// Player Progress for Network Defense
data class NetworkDefenseProgress(
    val highestLevel: Int = 1,
    val totalXp: Int = 0,
    val totalAttacksBlocked: Int = 0,
    val perfectLevels: Int = 0,
    val favoriteDefense: DefenseType? = null,
    val achievementsUnlocked: Set<NetworkDefenseAchievement> = emptySet()
)

// Achievements
enum class NetworkDefenseAchievement(
    val titleEn: String,
    val titleRu: String,
    val descriptionEn: String,
    val descriptionRu: String,
    val icon: String,
    val xpReward: Int
) {
    FIRST_DEFENSE(
        "First Defense",
        "Первая защита",
        "Complete your first level",
        "Пройди первый уровень",
        "🛡️",
        50
    ),
    
    FIREWALL_MASTER(
        "Firewall Master",
        "Мастер файрвола",
        "Block 1000 attacks with firewall",
        "Заблокируй 1000 атак с помощью файрвола",
        "🔥",
        100
    ),
    
    HONEYPOT_EXPERT(
        "Honeypot Expert",
        "Эксперт по ловушкам",
        "Catch 100 attackers in honeypot",
        "Поймай 100 атакующих в ловушку",
        "🍯",
        150
    ),
    
    PERFECT_DEFENSE(
        "Perfect Defense",
        "Идеальная защита",
        "Complete 10 levels with 100% health",
        "Пройди 10 уровней со 100% здоровья",
        "💯",
        200
    ),
    
    SIEM_SPECIALIST(
        "SIEM Specialist",
        "Специалист по SIEM",
        "Use SIEM to predict 50 attacks",
        "Используй SIEM для предсказания 50 атак",
        "📊",
        150
    ),
    
    NAT_NINJA(
        "NAT Ninja",
        "NAT ниндзя",
        "Hide your network for 100 levels",
        "Скрывай свою сеть 100 уровней",
        "🔒",
        100
    ),
    
    SECURITY_ARCHITECT(
        "Security Architect",
        "Архитектор безопасности",
        "Unlock and use all defense types",
        "Разблокируй и используй все виды защиты",
        "🏗️",
        250
    ),
    
    ELITE_DEFENDER(
        "Elite Defender",
        "Элитный защитник",
        "Reach level 100",
        "Достигни 100 уровня",
        "🏆",
        500
    ),
    
    BUDGET_MASTER(
        "Budget Master",
        "Мастер бюджета",
        "Complete 20 levels using less than 50% of budget",
        "Пройди 20 уровней, используя менее 50% бюджета",
        "💰",
        150
    ),
    
    SPEED_RUNNER(
        "Speed Runner",
        "Спидраннер",
        "Complete 10 levels in under 2 minutes each",
        "Пройди 10 уровней менее чем за 2 минуты каждый",
        "⚡",
        200
    )
}
