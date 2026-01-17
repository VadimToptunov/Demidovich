# CyberSafe Password Generator

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="CyberSafe Logo" width="120"/>
</p>

<p align="center">
  <strong>Powerful Password Security Made Simple</strong>
</p>

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.vtoptunov.passwordgenerator">
    <img src="https://img.shields.io/badge/Google%20Play-Coming%20Soon-green" alt="Coming to Google Play"/>
  </a>
  <img src="https://img.shields.io/badge/Android-8.0%2B-blue" alt="Android 8.0+"/>
  <img src="https://img.shields.io/badge/License-Proprietary-red" alt="License"/>
</p>

---

## 🎯 Overview

**CyberSafe** is a free, open-source password generator that helps you create strong passwords and learn about online security. Generate memorable passwords, check your security, and play fun educational games!

### Key Features

- **🎲 Smart Password Generation** - 5 unique styles to fit any situation
- **🔒 Bank-Level Security** - Your passwords never leave your device
- **📊 Security Dashboard** - See how secure your passwords are
- **🎮 CyberSafe Academy** - Learn cybersecurity by playing mini-games
- **🔐 Fingerprint Lock** - Optional biometric protection
- **📱 QR Transfer** - Move passwords between your devices
- **⚡ Cracking Simulator** - See how fast passwords can be cracked
- **🌙 Beautiful UI** - Cyberpunk-inspired design

---

## 🚀 Why CyberSafe?

### Stay Safe Online
- Create strong, memorable passwords in seconds
- Learn how hackers work and how to protect yourself
- Train your brain with fun security games
- Complete privacy - no cloud, no tracking

### Educational & Fun
- Visual crack time simulator shows password strength
- Mini-games teach real cybersecurity concepts
- Learn to spot phishing and scams
- Progressive difficulty keeps you engaged

### 100% Private
- All passwords stored locally on your device
- No internet connection required
- No ads, no tracking, no data collection
- You own your data

---

## 🎨 Password Generation Styles

### 1. Random
**Best for**: Maximum security  
**Example**: `K#9mP@2vQ$7nX`  
Strong cryptographic passwords with customizable character sets.

### 2. XKCD (Memorable)
**Best for**: Everyday accounts  
**Example**: `correct-horse-battery-staple`  
Easy to remember, extremely hard to crack. Based on [XKCD #936](https://xkcd.com/936/).

### 3. Phonetic
**Best for**: Phone verification  
**Example**: `Alpha-Bravo-9-Charlie`  
Easy to communicate over phone or radio.

### 4. Story
**Best for**: Long-term memory  
**Example**: `TheDog7AteMy8Homework!`  
Memorable narratives that create strong passwords.

### 5. Pronounceable
**Best for**: Typing speed  
**Example**: `Figlurty-Mabston-42`  
Fake words that sound real but are unique.

---

## 🛡️ Security Features

### Strong Encryption
- **256-bit AES** encryption protects your passwords
- **Secure key storage** using Android's hardware security
- **No cloud sync** - everything stays on your device
- **Biometric lock** - optional fingerprint/face unlock

### Password Health Check
- See how strong your passwords really are
- Find weak or reused passwords
- Get reminded to update old passwords
- Crack time estimation shows real-world security

### Privacy First
- Works completely offline
- No account required
- No data collection
- Open source code you can verify

---

## 🎮 CyberSafe Academy

Learn cybersecurity through interactive games:

### Memory Match
Test your ability to remember secure passwords. Choose from Easy, Medium, Hard difficulty levels.

### Phishing Detective
Identify fake websites and emails. Improve your threat detection skills.

### Hash Cracker
Understand how password hashing works. Learn why strong passwords matter.

### Social Engineering Simulator
Practice defending against manipulation tactics.

**Coming Soon**: More games and challenges!

---

## 📸 Screenshots

### Password Generator
Modern, intuitive interface with real-time strength analysis and visual crack time simulation.

### Security Dashboard
Comprehensive overview of your password health, including weak passwords, duplicates, and achievements.

### CyberSafe Academy
Gamified learning experience with progressive difficulty and XP rewards.

### QR Transfer
Securely transfer passwords between devices using encrypted QR codes with SHA-256 verification.

---

## 🔐 Privacy Policy

**We respect your privacy. Period.**

- ✅ **Zero data collection** - We don't track, store, or transmit your data
- ✅ **No internet required** - Works completely offline
- ✅ **No ads** - Clean, distraction-free experience (Premium)
- ✅ **No analytics** - Your usage stays private
- ✅ **Open about permissions** - We only request what's necessary

### Permissions Explained

| Permission | Why We Need It |
|------------|----------------|
| Camera | QR code scanning for password transfer |
| Biometric | Fingerprint/face unlock (optional) |
| Internet | Breach database updates (optional) |

---

## 🏗️ Technical Stack

### Architecture
- **MVVM** (Model-View-ViewModel) with Clean Architecture
- **Jetpack Compose** for modern, declarative UI
- **Kotlin Coroutines** for efficient async operations
- **Hilt** for dependency injection

### Security Libraries
- **SQLCipher** - Database encryption
- **Android Keystore** - Secure key storage
- **EncryptedSharedPreferences** - Settings protection
- **Biometric API** - Fingerprint/face authentication

### Database
- **Room** - Local persistence layer
- **SQLCipher** - Encryption wrapper
- **Flow** - Reactive data streams

---

## 📊 Password Strength Calculation

### Entropy Formula
```
Entropy (bits) = log₂(charset_size^password_length)
```

### Crack Time Estimation
Assumes modern GPU (100 billion attempts/second):
- **< 60 bits**: Hours to Days
- **60-80 bits**: Years to Decades
- **80-128 bits**: Centuries to Millennia
- **> 128 bits**: Longer than age of universe

### Strength Categories
| Strength | Entropy | Use Case |
|----------|---------|----------|
| Very Weak | < 28 bits | ❌ Never use |
| Weak | 28-36 bits | ⚠️ Low-security only |
| Fair | 36-60 bits | 👍 Basic accounts |
| Strong | 60-128 bits | 💪 Important accounts |
| Very Strong | > 128 bits | 🔒 Critical systems |

---

## 🆓 Free vs Premium

### Free Version
- ✅ Generate unlimited passwords
- ✅ Save up to 50 passwords
- ✅ Basic security dashboard
- ✅ Memory Match game
- ⚠️ Includes ads

### Premium ($4.99 one-time)
- ✅ **Everything in Free, plus:**
- ✅ Save unlimited passwords
- ✅ All 4 Academy games
- ✅ Advanced security analytics
- ✅ QR password transfer
- ✅ Offline breach checker (coming soon)
- ✅ **No ads, ever**
- ✅ Support indie development

**Why One-Time Payment?**
No subscriptions, no monthly fees. Pay once, use forever. That's how it should be!

---

## 🛠️ Installation

### Requirements
- Android 8.0 (Oreo) or higher
- 50 MB free storage
- Biometric sensor (optional)
- Camera (optional, for QR transfer)

### Download
1. Visit [Google Play Store](#)
2. Search "CyberSafe Password Generator"
3. Tap "Install"
4. Open and start generating secure passwords!

---

## 🔄 Updates & Roadmap

### Version 1.0 (Current)
- ✅ 5 password generation styles
- ✅ Security dashboard
- ✅ Memory training game
- ✅ QR transfer
- ✅ Biometric authentication

### Version 1.1 (Coming Soon)
- 🔜 Offline breach checker
- 🔜 3 additional Academy games
- 🔜 Password import/export
- 🔜 Dark/Light theme toggle
- 🔜 Widget support

### Version 2.0 (Future)
- 📅 Browser extension
- 📅 Desktop app (Windows, macOS, Linux)
- 📅 Team features (business plan)
- 📅 Hardware key support (YubiKey)

---

## 💬 Support

Need help? Support is available:

- 📧 **Email**: vtoptunov88@gmail.com
- 🐛 **Bug Reports**: Please include Android version and steps to reproduce
- 💡 **Feature Requests**: Always welcome!

---

## 📜 License

**MIT License** - Free to use, modify, and share!

See [LICENSE](LICENSE) for full details.

Made with ❤️ for everyone who wants to stay safe online.

---

## 👨‍💻 About

**CyberSafe** is an indie project made by a developer who cares about your privacy.

**Why This Exists:**
- Tired of password managers that track users
- Want to help people learn cybersecurity
- Believe in honest, transparent software
- No corporate interests, no data mining

**Open Source:**
- MIT License
- Code is available for review
- Community feedback welcome
- Transparent about how it works

---

<p align="center">
  <strong>Stay secure, stay safe! 🔐</strong>
</p>
