package com.vtoptunov.passwordgenerator.domain.model

data class Lesson(
    val id: String,
    val title: String,
    val titleRu: String,
    val icon: String,
    val durationMinutes: Int,
    val xpReward: Int,
    val difficulty: LessonDifficulty,
    val topics: List<LessonTopic>,
    val quiz: List<QuizQuestion>,
    val isCompleted: Boolean = false,
    val unlockLevel: Int = 1
)

data class LessonTopic(
    val title: String,
    val titleRu: String,
    val content: String,
    val contentRu: String,
    val example: String? = null,
    val exampleRu: String? = null,
    val tip: String? = null,
    val tipRu: String? = null
)

data class QuizQuestion(
    val question: String,
    val questionRu: String,
    val answers: List<String>,
    val answersRu: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val explanationRu: String
)

enum class LessonDifficulty(val displayName: String, val displayNameRu: String) {
    BEGINNER("Beginner", "Начинающий"),
    INTERMEDIATE("Intermediate", "Средний"),
    ADVANCED("Advanced", "Продвинутый")
}

object LessonLibrary {
    fun getAllLessons(): List<Lesson> = listOf(
        // Lesson 1: Password Basics
        Lesson(
            id = "password_basics",
            title = "Password Security Basics",
            titleRu = "Основы безопасности паролей",
            icon = "🔐",
            durationMinutes = 5,
            xpReward = 50,
            difficulty = LessonDifficulty.BEGINNER,
            unlockLevel = 1,
            topics = listOf(
                LessonTopic(
                    title = "What makes a password strong?",
                    titleRu = "Что делает пароль надежным?",
                    content = "A strong password is long (12+ characters), uses a mix of uppercase, lowercase, numbers, and symbols, and is unique for each account.",
                    contentRu = "Надежный пароль длинный (12+ символов), использует заглавные и строчные буквы, цифры и символы, и уникален для каждого аккаунта.",
                    example = "Weak: password123\nStrong: K#9mP$2vL@xT4q",
                    exampleRu = "Слабый: password123\nСильный: K#9mP$2vL@xT4q",
                    tip = "Use a password manager to remember complex passwords!",
                    tipRu = "Используйте менеджер паролей для запоминания сложных паролей!"
                ),
                LessonTopic(
                    title = "Common mistakes to avoid",
                    titleRu = "Частые ошибки",
                    content = "Never use: personal info (birthdays, names), dictionary words, simple patterns (123456, qwerty), or the same password everywhere.",
                    contentRu = "Никогда не используйте: личную информацию (даты рождения, имена), словарные слова, простые паттерны (123456, qwerty), или один пароль везде.",
                    example = "❌ john1990\n❌ password\n❌ 123456",
                    exampleRu = "❌ ivan1990\n❌ parol\n❌ 123456"
                ),
                LessonTopic(
                    title = "Password entropy explained",
                    titleRu = "Что такое энтропия пароля",
                    content = "Entropy measures password randomness in bits. Higher entropy = harder to crack. Aim for 60+ bits for good security.",
                    contentRu = "Энтропия измеряет случайность пароля в битах. Больше энтропия = сложнее взломать. Стремитесь к 60+ битам для хорошей защиты.",
                    tip = "Each additional character dramatically increases entropy!",
                    tipRu = "Каждый дополнительный символ резко увеличивает энтропию!"
                )
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "What is the minimum recommended password length?",
                    questionRu = "Какая минимальная рекомендуемая длина пароля?",
                    answers = listOf("6 characters", "8 characters", "12 characters", "16 characters"),
                    answersRu = listOf("6 символов", "8 символов", "12 символов", "16 символов"),
                    correctAnswerIndex = 2,
                    explanation = "12+ characters provide strong protection against brute-force attacks.",
                    explanationRu = "12+ символов обеспечивают надежную защиту от атак перебором."
                ),
                QuizQuestion(
                    question = "Which password is strongest?",
                    questionRu = "Какой пароль самый надежный?",
                    answers = listOf("Password123", "john1990!", "K#9mP$2vL@xT", "qwerty12345"),
                    answersRu = listOf("Password123", "ivan1990!", "K#9mP$2vL@xT", "qwerty12345"),
                    correctAnswerIndex = 2,
                    explanation = "Random mix of characters with no predictable patterns is strongest.",
                    explanationRu = "Случайная смесь символов без предсказуемых паттернов - самая надежная."
                )
            )
        ),
        
        // Lesson 2: Two-Factor Authentication
        Lesson(
            id = "2fa_basics",
            title = "Two-Factor Authentication (2FA)",
            titleRu = "Двухфакторная аутентификация (2FA)",
            icon = "🔑",
            durationMinutes = 4,
            xpReward = 40,
            difficulty = LessonDifficulty.BEGINNER,
            unlockLevel = 2,
            topics = listOf(
                LessonTopic(
                    title = "What is 2FA?",
                    titleRu = "Что такое 2FA?",
                    content = "2FA adds an extra security layer. Even if someone steals your password, they can't access your account without the second factor.",
                    contentRu = "2FA добавляет дополнительный уровень защиты. Даже если кто-то украдет ваш пароль, они не смогут войти без второго фактора.",
                    example = "1st factor: Password\n2nd factor: Code from phone app",
                    exampleRu = "1-й фактор: Пароль\n2-й фактор: Код из приложения"
                ),
                LessonTopic(
                    title = "Types of 2FA",
                    titleRu = "Типы 2FA",
                    content = "SMS codes (least secure), Authenticator apps (TOTP), Hardware keys (most secure), Biometrics.",
                    contentRu = "SMS коды (наименее надежно), Приложения-аутентификаторы (TOTP), Аппаратные ключи (наиболее надежно), Биометрия.",
                    tip = "Authenticator apps are more secure than SMS!",
                    tipRu = "Приложения-аутентификаторы надежнее SMS!"
                )
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "What does 2FA stand for?",
                    questionRu = "Что означает 2FA?",
                    answers = listOf("2 Fast Authentication", "Two-Factor Authentication", "Twice Factored Access", "Two-File Authorization"),
                    answersRu = listOf("2 Быстрых Аутентификации", "Двухфакторная Аутентификация", "Дважды Факторный Доступ", "Двухфайловая Авторизация"),
                    correctAnswerIndex = 1,
                    explanation = "2FA = Two-Factor Authentication, requiring two proof methods.",
                    explanationRu = "2FA = Двухфакторная Аутентификация, требующая два способа подтверждения."
                )
            )
        ),
        
        // Lesson 3: Phishing Awareness
        Lesson(
            id = "phishing_awareness",
            title = "Recognizing Phishing Attacks",
            titleRu = "Распознавание фишинга",
            icon = "🎣",
            durationMinutes = 6,
            xpReward = 60,
            difficulty = LessonDifficulty.INTERMEDIATE,
            unlockLevel = 3,
            topics = listOf(
                LessonTopic(
                    title = "What is phishing?",
                    titleRu = "Что такое фишинг?",
                    content = "Phishing is when attackers impersonate legitimate services to steal your credentials. They send fake emails, messages, or create fake websites.",
                    contentRu = "Фишинг - это когда атакующие выдают себя за легитимные сервисы чтобы украсть ваши данные. Они отправляют фальшивые письма, сообщения или создают поддельные сайты.",
                    example = "Fake: paypаl.com (Cyrillic 'а')\nReal: paypal.com",
                    exampleRu = "Подделка: paypаl.com (русская 'а')\nНастоящий: paypal.com"
                ),
                LessonTopic(
                    title = "Red flags to watch for",
                    titleRu = "Красные флаги",
                    content = "Suspicious URLs, urgency ('Act NOW!'), poor grammar, requests for passwords, mismatched sender addresses.",
                    contentRu = "Подозрительные URL, срочность ('Действуйте СЕЙЧАС!'), плохая грамматика, запросы паролей, несовпадающие адреса отправителей.",
                    tip = "Always check the URL before entering credentials!",
                    tipRu = "Всегда проверяйте URL перед вводом данных!"
                ),
                LessonTopic(
                    title = "How to stay safe",
                    titleRu = "Как оставаться в безопасности",
                    content = "Never click suspicious links, verify sender identity, use 2FA, check for HTTPS, hover over links to see real URL.",
                    contentRu = "Никогда не кликайте подозрительные ссылки, проверяйте отправителя, используйте 2FA, проверяйте HTTPS, наводите на ссылки чтобы увидеть настоящий URL.",
                    tip = "When in doubt, go directly to the website instead of clicking links!",
                    tipRu = "Если сомневаетесь, зайдите на сайт напрямую вместо клика по ссылке!"
                )
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "Which is a sign of phishing?",
                    questionRu = "Что является признаком фишинга?",
                    answers = listOf("HTTPS in URL", "Verified sender", "Urgent action required", "Professional design"),
                    answersRu = listOf("HTTPS в URL", "Проверенный отправитель", "Требуется срочное действие", "Профессиональный дизайн"),
                    correctAnswerIndex = 2,
                    explanation = "Creating false urgency is a classic phishing tactic to make you act without thinking.",
                    explanationRu = "Создание ложной срочности - классическая тактика фишинга, чтобы вы действовали не думая."
                ),
                QuizQuestion(
                    question = "What should you do if you receive a suspicious email?",
                    questionRu = "Что делать при получении подозрительного письма?",
                    answers = listOf("Click to verify", "Reply with password", "Delete and report", "Forward to friends"),
                    answersRu = listOf("Кликнуть для проверки", "Ответить с паролем", "Удалить и сообщить", "Переслать друзьям"),
                    correctAnswerIndex = 2,
                    explanation = "Delete suspicious emails and report them to prevent others from falling victim.",
                    explanationRu = "Удаляйте подозрительные письма и сообщайте о них, чтобы защитить других."
                )
            )
        ),
        
        // Lesson 4: Password Managers
        Lesson(
            id = "password_managers",
            title = "Using Password Managers",
            titleRu = "Использование менеджеров паролей",
            icon = "🔒",
            durationMinutes = 5,
            xpReward = 50,
            difficulty = LessonDifficulty.INTERMEDIATE,
            unlockLevel = 5,
            topics = listOf(
                LessonTopic(
                    title = "Why use a password manager?",
                    titleRu = "Зачем нужен менеджер паролей?",
                    content = "Password managers remember all your passwords, so you only need to remember one master password. They generate strong, unique passwords for each site.",
                    contentRu = "Менеджеры паролей запоминают все ваши пароли, вам нужно помнить только один мастер-пароль. Они генерируют надежные, уникальные пароли для каждого сайта.",
                    tip = "You're already using one - PassForge!",
                    tipRu = "Вы уже используете один - PassForge!"
                ),
                LessonTopic(
                    title = "Best practices",
                    titleRu = "Лучшие практики",
                    content = "Use a VERY strong master password, enable 2FA for your password manager, never share your master password, regularly update your passwords.",
                    contentRu = "Используйте ОЧЕНЬ надежный мастер-пароль, включите 2FA для менеджера паролей, никогда не делитесь мастер-паролем, регулярно обновляйте пароли.",
                    example = "Master password entropy: 100+ bits",
                    exampleRu = "Энтропия мастер-пароля: 100+ бит"
                )
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "What is a master password?",
                    questionRu = "Что такое мастер-пароль?",
                    answers = listOf("The most important website password", "The password to unlock all other passwords", "A password for your email", "A backup password"),
                    answersRu = listOf("Самый важный пароль от сайта", "Пароль для доступа ко всем остальным паролям", "Пароль от email", "Резервный пароль"),
                    correctAnswerIndex = 1,
                    explanation = "The master password unlocks your password manager and all stored passwords.",
                    explanationRu = "Мастер-пароль открывает доступ к менеджеру паролей и всем сохраненным паролям."
                )
            )
        ),
        
        // Lesson 5: Social Engineering
        Lesson(
            id = "social_engineering",
            title = "Social Engineering Defense",
            titleRu = "Защита от социальной инженерии",
            icon = "🎭",
            durationMinutes = 7,
            xpReward = 70,
            difficulty = LessonDifficulty.ADVANCED,
            unlockLevel = 7,
            topics = listOf(
                LessonTopic(
                    title = "What is social engineering?",
                    titleRu = "Что такое социальная инженерия?",
                    content = "Social engineering is manipulating people into revealing confidential information. Attackers exploit human psychology rather than technical hacking.",
                    contentRu = "Социальная инженерия - это манипуляция людьми для получения конфиденциальной информации. Атакующие используют психологию вместо технических методов взлома.",
                    example = "Fake call: 'Hi, this is IT support, I need your password to fix an issue'",
                    exampleRu = "Поддельный звонок: 'Здравствуйте, это техподдержка, мне нужен ваш пароль для решения проблемы'"
                ),
                LessonTopic(
                    title = "Common tactics",
                    titleRu = "Распространенные тактики",
                    content = "Pretexting (fake scenario), Baiting (tempting offer), Quid Pro Quo (service for info), Tailgating (physical access), Fear tactics (creating panic).",
                    contentRu = "Претекстинг (ложный сценарий), Baiting (заманчивое предложение), Quid Pro Quo (услуга за информацию), Tailgating (физический доступ), Запугивание (создание паники).",
                    tip = "Attackers use urgency and authority to pressure you!",
                    tipRu = "Атакующие используют срочность и авторитет чтобы давить на вас!"
                ),
                LessonTopic(
                    title = "How to protect yourself",
                    titleRu = "Как защититься",
                    content = "Verify caller identity through official channels, never rush decisions, don't share passwords/codes, trust your instincts, report suspicious contacts.",
                    contentRu = "Проверяйте личность звонящего через официальные каналы, не спешите с решениями, не делитесь паролями/кодами, доверяйте интуиции, сообщайте о подозрительных контактах.",
                    example = "If bank calls: Hang up → Call official number → Verify",
                    exampleRu = "Если звонит банк: Повесьте трубку → Позвоните на официальный номер → Проверьте"
                )
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "'Your account will be locked in 1 hour unless you verify now!' What is this?",
                    questionRu = "'Ваш аккаунт будет заблокирован через час, если не подтвердите сейчас!' Что это?",
                    answers = listOf("Legitimate warning", "Social engineering attack", "System error", "Routine security check"),
                    answersRu = listOf("Легитимное предупреждение", "Атака социальной инженерии", "Системная ошибка", "Обычная проверка безопасности"),
                    correctAnswerIndex = 1,
                    explanation = "Creating false urgency is a classic social engineering tactic to bypass critical thinking.",
                    explanationRu = "Создание ложной срочности - классическая тактика социальной инженерии для обхода критического мышления."
                ),
                QuizQuestion(
                    question = "Best response to suspicious IT support call?",
                    questionRu = "Лучший ответ на подозрительный звонок от техподдержки?",
                    answers = listOf("Provide password", "Hang up & call official number", "Ask for their name", "Email the password"),
                    answersRu = listOf("Сообщить пароль", "Повесить трубку и позвонить на официальный номер", "Спросить имя", "Отправить пароль по email"),
                    correctAnswerIndex = 1,
                    explanation = "Always verify through official channels. Real IT never asks for passwords.",
                    explanationRu = "Всегда проверяйте через официальные каналы. Настоящая техподдержка никогда не просит пароли."
                )
            )
        ),
        
        // Lesson 6: Safe Browsing
        Lesson(
            id = "safe_browsing",
            title = "Safe Internet Browsing",
            titleRu = "Безопасный веб-серфинг",
            icon = "🌐",
            durationMinutes = 5,
            xpReward = 50,
            difficulty = LessonDifficulty.BEGINNER,
            unlockLevel = 4,
            topics = listOf(
                LessonTopic(
                    title = "HTTPS vs HTTP",
                    titleRu = "HTTPS vs HTTP",
                    content = "HTTPS encrypts your connection. Always check for the lock icon 🔒 in the address bar before entering sensitive information.",
                    contentRu = "HTTPS шифрует ваше соединение. Всегда проверяйте значок замка 🔒 в адресной строке перед вводом важной информации.",
                    example = "✅ https://bank.com (Secure)\n❌ http://bank.com (Insecure)",
                    exampleRu = "✅ https://bank.com (Безопасно)\n❌ http://bank.com (Небезопасно)",
                    tip = "Even phishing sites can have HTTPS - check the domain too!",
                    tipRu = "Даже фишинговые сайты могут иметь HTTPS - проверяйте домен!"
                ),
                LessonTopic(
                    title = "Browser privacy settings",
                    titleRu = "Настройки приватности браузера",
                    content = "Enable tracking protection, block third-party cookies, clear browsing data regularly, use private/incognito mode for sensitive tasks.",
                    contentRu = "Включите защиту от отслеживания, блокируйте сторонние куки, регулярно очищайте данные браузера, используйте приватный режим для важных задач.",
                    tip = "Incognito mode doesn't make you anonymous - use VPN for that!",
                    tipRu = "Режим инкогнито не делает вас анонимным - для этого нужен VPN!"
                ),
                LessonTopic(
                    title = "Dangerous downloads",
                    titleRu = "Опасные загрузки",
                    content = "Only download from official sources, check file extensions (.exe can be dangerous), scan downloads with antivirus, avoid pirated software.",
                    contentRu = "Скачивайте только с официальных источников, проверяйте расширения файлов (.exe может быть опасен), сканируйте загрузки антивирусом, избегайте пиратского ПО.",
                    example = "⚠️ movie.mp4.exe ← This is malware!",
                    exampleRu = "⚠️ film.mp4.exe ← Это вирус!"
                )
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "What does HTTPS guarantee?",
                    questionRu = "Что гарантирует HTTPS?",
                    answers = listOf("Website is safe", "Encrypted connection", "No viruses", "Website is real"),
                    answersRu = listOf("Сайт безопасен", "Зашифрованное соединение", "Нет вирусов", "Сайт настоящий"),
                    correctAnswerIndex = 1,
                    explanation = "HTTPS only encrypts the connection between you and the server. It doesn't verify content safety.",
                    explanationRu = "HTTPS только шифрует соединение между вами и сервером. Это не проверяет безопасность содержимого."
                ),
                QuizQuestion(
                    question = "You received 'invoice.pdf.exe' file. What to do?",
                    questionRu = "Вы получили файл 'invoice.pdf.exe'. Что делать?",
                    answers = listOf("Open it", "Delete immediately", "Scan with antivirus first", "Rename it"),
                    answersRu = listOf("Открыть", "Немедленно удалить", "Сначала просканировать антивирусом", "Переименовать"),
                    correctAnswerIndex = 1,
                    explanation = "Double extensions (.pdf.exe) are a common malware trick. Delete such files immediately!",
                    explanationRu = "Двойные расширения (.pdf.exe) - распространенный трюк вирусов. Немедленно удаляйте такие файлы!"
                )
            )
        ),
        
        // Lesson 7: Data Privacy
        Lesson(
            id = "data_privacy",
            title = "Personal Data Privacy",
            titleRu = "Приватность персональных данных",
            icon = "🛡️",
            durationMinutes = 6,
            xpReward = 60,
            difficulty = LessonDifficulty.INTERMEDIATE,
            unlockLevel = 6,
            topics = listOf(
                LessonTopic(
                    title = "What is personal data?",
                    titleRu = "Что такое персональные данные?",
                    content = "Name, address, phone, email, photos, financial info, browsing history, location data. All this can be used to identify or track you.",
                    contentRu = "Имя, адрес, телефон, email, фото, финансовая информация, история браузера, данные о местоположении. Всё это может быть использовано для идентификации или отслеживания.",
                    tip = "Even 'anonymous' data can often be de-anonymized!",
                    tipRu = "Даже 'анонимные' данные часто можно деанонимизировать!"
                ),
                LessonTopic(
                    title = "App permissions",
                    titleRu = "Разрешения приложений",
                    content = "Check what permissions apps request. Does a flashlight app really need access to your contacts? Camera? Location? If it seems excessive, don't install it.",
                    contentRu = "Проверяйте какие разрешения запрашивают приложения. Действительно ли фонарику нужен доступ к контактам? Камере? Местоположению? Если кажется избыточным - не устанавливайте.",
                    example = "🔦 Flashlight app:\n✅ Camera (for flash)\n❌ Contacts (suspicious!)",
                    exampleRu = "🔦 Приложение фонарика:\n✅ Камера (для вспышки)\n❌ Контакты (подозрительно!)"
                ),
                LessonTopic(
                    title = "Social media privacy",
                    titleRu = "Приватность в соцсетях",
                    content = "Review privacy settings regularly, limit who can see your posts, don't share sensitive info publicly, be careful with location tags, think before posting.",
                    contentRu = "Регулярно проверяйте настройки приватности, ограничьте кто может видеть ваши посты, не делитесь важной информацией публично, будьте осторожны с геотегами, думайте перед публикацией.",
                    tip = "Once posted online, it's there forever - even if you delete it!",
                    tipRu = "Опубликованное в интернете остается навсегда - даже если вы удалите!"
                )
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "Calculator app requests contacts access. Should you allow?",
                    questionRu = "Калькулятор запрашивает доступ к контактам. Разрешить?",
                    answers = listOf("Yes, it's safe", "No, it's suspicious", "Only once", "Ask developer"),
                    answersRu = listOf("Да, это безопасно", "Нет, это подозрительно", "Только один раз", "Спросить разработчика"),
                    correctAnswerIndex = 1,
                    explanation = "Calculator doesn't need contacts access. Excessive permissions often indicate malware or data harvesting.",
                    explanationRu = "Калькулятору не нужен доступ к контактам. Избыточные разрешения часто указывают на вирусы или сбор данных."
                ),
                QuizQuestion(
                    question = "Best way to protect social media privacy?",
                    questionRu = "Лучший способ защитить приватность в соцсетях?",
                    answers = listOf("Make everything public", "Only friends can see posts", "Never post anything", "Use fake name"),
                    answersRu = listOf("Сделать всё публичным", "Только друзья видят посты", "Никогда ничего не публиковать", "Использовать фейковое имя"),
                    correctAnswerIndex = 1,
                    explanation = "Limiting visibility to friends-only is the best balance between privacy and normal use.",
                    explanationRu = "Ограничение видимости только друзьями - лучший баланс между приватностью и обычным использованием."
                )
            )
        ),
        
        // Lesson 8: Mobile Security
        Lesson(
            id = "mobile_security",
            title = "Mobile Device Security",
            titleRu = "Безопасность мобильных устройств",
            icon = "📱",
            durationMinutes = 5,
            xpReward = 50,
            difficulty = LessonDifficulty.BEGINNER,
            unlockLevel = 3,
            topics = listOf(
                LessonTopic(
                    title = "Lock screen protection",
                    titleRu = "Защита экрана блокировки",
                    content = "Use strong PIN (not 1234!), fingerprint, or face unlock. Set short auto-lock timeout. Never leave phone unattended and unlocked.",
                    contentRu = "Используйте надежный PIN (не 1234!), отпечаток пальца или разблокировку лицом. Установите короткий таймаут автоблокировки. Никогда не оставляйте телефон без присмотра разблокированным.",
                    example = "❌ PIN: 1234, 0000, 1111\n✅ PIN: 7392, 8146",
                    exampleRu = "❌ PIN: 1234, 0000, 1111\n✅ PIN: 7392, 8146",
                    tip = "Avoid patterns that form simple shapes on PIN pad!",
                    tipRu = "Избегайте паттернов образующих простые фигуры на клавиатуре!"
                ),
                LessonTopic(
                    title = "App store safety",
                    titleRu = "Безопасность магазинов приложений",
                    content = "Only install from official stores (Google Play, App Store). Check reviews, ratings, number of downloads. Verify developer name.",
                    contentRu = "Устанавливайте только из официальных магазинов (Google Play, App Store). Проверяйте отзывы, рейтинги, количество загрузок. Проверяйте имя разработчика.",
                    tip = "Fake apps often mimic popular ones with similar names!",
                    tipRu = "Поддельные приложения часто имитируют популярные с похожими названиями!"
                ),
                LessonTopic(
                    title = "OS and app updates",
                    titleRu = "Обновления ОС и приложений",
                    content = "Always install security updates promptly. Enable automatic updates. Outdated software has known vulnerabilities that hackers exploit.",
                    contentRu = "Всегда устанавливайте обновления безопасности быстро. Включите автоматические обновления. Устаревшее ПО имеет известные уязвимости которые используют хакеры.",
                    tip = "Updates aren't just new features - they patch security holes!",
                    tipRu = "Обновления - это не только новые функции, они закрывают уязвимости!"
                )
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "Your phone PIN should be:",
                    questionRu = "PIN вашего телефона должен быть:",
                    answers = listOf("Easy to remember (1234)", "Your birthday", "Random 4-6 digits", "Written on phone case"),
                    answersRu = listOf("Легко запомнить (1234)", "Ваш день рождения", "Случайные 4-6 цифр", "Написан на чехле"),
                    correctAnswerIndex = 2,
                    explanation = "Use random digits that aren't related to personal info. Avoid common patterns.",
                    explanationRu = "Используйте случайные цифры не связанные с личной информацией. Избегайте распространенных паттернов."
                ),
                QuizQuestion(
                    question = "Why are OS updates important for security?",
                    questionRu = "Почему обновления ОС важны для безопасности?",
                    answers = listOf("They add new emojis", "They patch security vulnerabilities", "They speed up phone", "They're not important"),
                    answersRu = listOf("Добавляют новые эмодзи", "Закрывают уязвимости безопасности", "Ускоряют телефон", "Они не важны"),
                    correctAnswerIndex = 1,
                    explanation = "Security updates patch known vulnerabilities before hackers can exploit them.",
                    explanationRu = "Обновления безопасности закрывают известные уязвимости до того как хакеры их используют."
                )
            )
        )
    )
}
