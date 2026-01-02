# Privacy Policy

**Effective Date**: January 2, 2026  
**Last Updated**: January 2, 2026

## Overview

CyberSafe Password Generator ("we", "our", or "the app") is committed to protecting your privacy. This Privacy Policy explains our data practices for the CyberSafe mobile application.

**TL;DR**: We don't collect, store, or transmit any of your personal data. Period.

---

## Data Collection

### What We DO NOT Collect

❌ **Personal Information** - No names, emails, phone numbers, or addresses  
❌ **Generated Passwords** - Never transmitted or stored on our servers  
❌ **Saved Passwords** - Remain encrypted on your device only  
❌ **Usage Analytics** - No tracking of how you use the app  
❌ **Crash Reports** - No automatic error reporting (you can opt-in)  
❌ **Location Data** - We never access your location  
❌ **Device Information** - No fingerprinting or device identification  
❌ **Advertising IDs** - We don't use ad networks (Premium) or tracking  

### What We DO Collect (Optional)

✅ **Breach Database Updates** (Optional, Premium)  
- When you enable "Check for breached passwords"
- We download a public list of compromised password hashes
- Downloaded once, stored locally, checked offline
- No passwords are ever uploaded

✅ **Crash Reports** (Opt-in Only)  
- You must explicitly enable "Send crash reports"
- Helps us fix bugs and improve stability
- Contains no personal data or passwords
- Can be disabled anytime in Settings

---

## Data Storage

### Local Storage Only

All your data is stored **exclusively on your device**:

1. **Passwords** - Encrypted with SQLCipher (256-bit AES)
2. **Master Key** - Secured in Android Keystore (hardware-backed)
3. **Settings** - Encrypted with EncryptedSharedPreferences
4. **Game Progress** - Stored locally, never synced

### No Cloud Sync

Unlike other password managers:
- ❌ No cloud backup
- ❌ No account creation
- ❌ No server communication
- ✅ Complete offline operation

**Important**: If you uninstall the app or lose your device, your passwords cannot be recovered. We recommend using the QR Transfer feature to backup important passwords.

---

## Permissions

### Why We Request Permissions

| Permission | Purpose | Required? |
|------------|---------|-----------|
| **Camera** | QR code scanning for password transfer | Optional |
| **Biometric** | Fingerprint/face unlock | Optional |
| **Internet** | Breach database updates (Premium only) | Optional |

### How to Revoke Permissions

Android Settings → Apps → CyberSafe → Permissions → Toggle off

---

## Third-Party Services

### Google Play Billing (Premium)

When you purchase Premium:
- **What Google Collects**: Transaction ID, purchase timestamp
- **What We Receive**: Anonymous purchase token (no personal info)
- **Google's Policy**: [Google Play Privacy Policy](https://policies.google.com/privacy)

### No Other Third Parties

We do not integrate:
- ❌ Analytics platforms (Google Analytics, Firebase, etc.)
- ❌ Advertising networks
- ❌ Social media SDKs
- ❌ Cloud storage providers
- ❌ Crash reporting services (unless you opt-in)

---

## Security

### How We Protect Your Data

1. **Encryption at Rest**
   - SQLCipher with 256-bit AES encryption
   - PBKDF2 key derivation (100,000 iterations)
   - Unique key per device (stored in Android Keystore)

2. **Encryption in Transit** (QR Transfer)
   - SHA-256 checksum verification
   - Base64 encoding for data integrity
   - No network transmission

3. **Biometric Protection**
   - Optional fingerprint/face unlock
   - Managed by Android BiometricPrompt API
   - No biometric data stored by the app

4. **Secure Coding Practices**
   - Regular security audits
   - No hardcoded secrets
   - Memory wiping for sensitive data
   - ProGuard code obfuscation

---

## Children's Privacy

CyberSafe is intended for users **13 years and older**. We do not knowingly collect data from children under 13. If you believe a child has used the app, please contact us immediately.

---

## International Users

### GDPR Compliance (EU Users)

**Good news**: Since we don't collect data, GDPR doesn't apply!

- ✅ Right to Access - We don't have your data
- ✅ Right to Deletion - Already deleted (never collected)
- ✅ Right to Portability - Export via QR Transfer
- ✅ Right to Object - Nothing to object to

### CCPA Compliance (California Users)

We comply with CCPA by default:
- ✅ We don't sell personal information
- ✅ We don't share personal information
- ✅ We don't collect personal information

---

## Data Retention

### Automatic Deletion

When you uninstall CyberSafe:
- ✅ All passwords are deleted from your device
- ✅ All settings are removed
- ✅ No data remains anywhere

### Manual Deletion

Settings → Security → Clear All Data → Confirm

⚠️ **Warning**: This action cannot be undone.

---

## Changes to This Policy

We may update this Privacy Policy occasionally. When we do:

1. We'll update the "Last Updated" date
2. Significant changes will be announced in-app
3. You'll be prompted to review the new policy

**Your continued use of CyberSafe after policy updates constitutes acceptance.**

---

## Contact Us

Questions about privacy?

📧 **Email**: [Your Email]  
🌐 **Website**: [Your Website] (optional)  
📍 **Business Address**: [Your Address] (if required by jurisdiction)

**Response Time**: Direct developer communication

---

## Transparency Report

### Data Requests (2026)

| Type | Received | Complied |
|------|----------|----------|
| Law Enforcement | 0 | 0 |
| Government | 0 | 0 |
| Civil Subpoenas | 0 | 0 |

**Why?** We have no data to provide.

---

## Your Rights

Depending on your location, you may have the following rights:

✅ **Right to Know** - We're telling you: we collect nothing  
✅ **Right to Delete** - Uninstall = instant deletion  
✅ **Right to Opt-Out** - No tracking to opt out of  
✅ **Right to Non-Discrimination** - All users treated equally  

---

## Open Source Components

CyberSafe uses open-source libraries. See [LICENSES.md](LICENSES.md) for details.

None of these libraries collect data:
- Android Jetpack (Google)
- SQLCipher (Zetetic)
- Kotlinx Coroutines (JetBrains)
- Hilt (Google)
- ZXing (Google)

---

<p align="center">
  <strong>Privacy is a right, not a privilege.</strong><br>
  <sub>We'll never compromise yours.</sub>
</p>

---

**Questions?** Email [Your Email]

