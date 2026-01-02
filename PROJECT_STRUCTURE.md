# 📂 Project Structure

## Core Documentation

| File | Purpose |
|------|---------|
| `README.md` | Main project documentation for developers and users |
| `PRIVACY_POLICY.md` | Privacy policy for Google Play compliance |
| `GOOGLE_PLAY_LISTING.md` | Complete Google Play Store listing content |
| `ALL_BUGS_FIXED.md` | Technical bug fixes summary (10 bugs resolved) |

## Architecture Overview

```
app/src/main/kotlin/com/vtoptunov/passwordgenerator/
│
├── presentation/              # UI Layer (Jetpack Compose)
│   ├── screens/
│   │   ├── generator/        # Password generation screen
│   │   ├── dashboard/        # Security health dashboard
│   │   ├── game/             # CyberSafe Academy games
│   │   ├── transfer/         # QR code transfer
│   │   └── settings/         # App settings
│   ├── navigation/           # Navigation graph
│   └── theme/                # Design system (colors, typography)
│
├── domain/                    # Business Logic Layer
│   ├── model/                # Data models
│   ├── repository/           # Repository interfaces
│   └── usecase/              # Business logic use cases
│       ├── password/         # Password generation
│       ├── analytics/        # Health calculations
│       ├── game/             # Game logic
│       └── transfer/         # QR export/import
│
├── data/                      # Data Layer
│   ├── local/
│   │   ├── database/         # Room + SQLCipher
│   │   ├── dao/              # Data Access Objects
│   │   └── entity/           # Database entities
│   ├── repository/           # Repository implementations
│   └── security/             # Keystore, Biometric
│
└── di/                        # Dependency Injection (Hilt)
    ├── DatabaseModule.kt
    ├── RepositoryModule.kt
    └── UseCaseModule.kt
```

## Key Features

### 1. Password Generation (5 Styles)
- **Random**: Cryptographically secure with customizable charset
- **XKCD**: Memorable word-based (correct-horse-battery-staple)
- **Phonetic**: NATO phonetic alphabet (Alpha-Bravo-Charlie)
- **Story**: Narrative-based passwords
- **Pronounceable**: Fake but pronounceable words

### 2. Security Infrastructure
- **SQLCipher**: 256-bit AES database encryption
- **Android Keystore**: Hardware-backed key storage
- **EncryptedSharedPreferences**: Secure settings storage
- **Biometric Authentication**: Fingerprint/Face unlock
- **No Cloud Sync**: Complete offline operation

### 3. CyberSafe Academy
- **Memory Match**: Password memorization training
- **Progressive Difficulty**: Easy → Medium → Hard → Expert
- **XP System**: Gamified learning progression
- **Educational Content**: Cybersecurity lessons

### 4. Security Dashboard
- **Health Score**: 0-100 based on password quality
- **Entropy Calculation**: Shannon entropy (bits)
- **Crack Time Estimation**: Against modern GPU attacks
- **Duplicate Detection**: Find reused passwords
- **Age Tracking**: Rotation reminders

### 5. QR Transfer
- **Encrypted Export**: SHA-256 checksum verification
- **Offline Transfer**: No network required
- **Camera Scanning**: ZXing library integration
- **Base64 Encoding**: Binary-safe data transfer

## Technology Stack

### Frontend
- **Jetpack Compose**: Modern declarative UI
- **Material Design 3**: Latest design system
- **Kotlin Coroutines**: Async operations
- **Flow**: Reactive data streams

### Backend
- **Room Database**: Local persistence
- **SQLCipher**: Database encryption
- **Hilt**: Dependency injection
- **WorkManager**: Background tasks

### Security
- **Android Keystore**: Secure key generation
- **BiometricPrompt API**: Biometric authentication
- **SecureRandom**: Cryptographic randomness
- **Base64**: Binary-safe encoding

### Libraries
| Library | Purpose | Version |
|---------|---------|---------|
| Compose | UI Framework | 1.6.0 |
| Hilt | DI | 2.50 |
| Room | Database | 2.6.1 |
| SQLCipher | Encryption | 4.5.4 |
| ZXing | QR Codes | 3.5.2 |
| Biometric | Authentication | 1.2.0-alpha05 |

## Build Configuration

### Requirements
- **Android Studio**: Hedgehog (2023.1.1) or newer
- **Gradle**: 8.2
- **Kotlin**: 1.9.22
- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 34 (Android 14)

### Signing
```gradle
signingConfigs {
    release {
        storeFile file(System.getenv("KEYSTORE_FILE") ?: "keystore.jks")
        storePassword System.getenv("KEYSTORE_PASSWORD")
        keyAlias System.getenv("KEY_ALIAS")
        keyPassword System.getenv("KEY_PASSWORD")
    }
}
```

### ProGuard Rules
```proguard
-keep class com.vtoptunov.passwordgenerator.domain.model.** { *; }
-keep class net.sqlcipher.** { *; }
-dontwarn org.bouncycastle.**
```

## Testing Strategy

### Unit Tests
- Password generation logic
- Entropy calculations
- Crack time estimations
- Game difficulty algorithms

### Integration Tests
- Database operations
- Keystore management
- Repository implementations

### UI Tests
- Screen navigation
- User interactions
- Form validation

## Security Best Practices

✅ **No Hardcoded Secrets**: All keys generated at runtime  
✅ **SQLCipher Encryption**: Military-grade database protection  
✅ **Base64 Encoding**: Prevents UTF-8 corruption  
✅ **Lifecycle-Aware**: Proper state management  
✅ **Memory Safety**: Sensitive data cleared after use  
✅ **ProGuard**: Code obfuscation for release  

## Performance Optimizations

- **LazyColumn/Grid**: Efficient list rendering
- **remember**: Caching expensive calculations
- **derivedStateOf**: Optimized computed values
- **Dispatchers.IO**: Background database operations
- **Flow**: Reactive data with backpressure

## Accessibility

- **Content Descriptions**: Screen reader support
- **Minimum Touch Targets**: 48dp sizing
- **Color Contrast**: WCAG AA compliant
- **Dynamic Type**: Scalable text sizes

## Localization

Currently supported:
- English (en)

Planned:
- Ukrainian (uk)
- Russian (ru)
- Spanish (es)
- German (de)
- French (fr)

## CI/CD Pipeline

```yaml
stages:
  - lint
  - test
  - build
  - deploy

lint:
  - ktlint
  - detekt
  
test:
  - unit tests
  - integration tests
  
build:
  - debug APK
  - release AAB
  
deploy:
  - Google Play (internal track)
```

## Version History

### v1.0.0 (Current)
- Initial release
- 5 password generation styles
- Security dashboard
- Memory training game
- QR transfer
- Biometric authentication
- 10 critical bug fixes

### Roadmap v1.1
- Offline breach checker (Bloom filter)
- Google Play Billing integration
- 3 additional Academy games
- Password import/export
- Dark/Light theme toggle

### Roadmap v2.0
- Browser extension
- Desktop app
- Team features
- Hardware key support

## License

Copyright © 2026 Vadim Toptunov. All rights reserved.

Proprietary software - see LICENSE file for details.

## Contact

- **Developer**: Vadim Toptunov
- **Email**: vadim@vtoptunov.com
- **Support**: support@cybersafe-app.com
- **Website**: https://cybersafe-app.com

---

**Last Updated**: January 2, 2026  
**Status**: ✅ Ready for Google Play submission

