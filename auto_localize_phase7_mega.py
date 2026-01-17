#!/usr/bin/env python3
"""PHASE 7 FINAL MEGA: Comprehensive cleanup of ALL remaining screens"""
import re, os

# Comprehensive mapping of ALL remaining strings
MEGA_MAP = {
    # SavedPasswordsScreen
    '"Saved Passwords"': 'stringResource(R.string.saved_passwords_title)',
    'text = "Generate and save passwords to see them here"': 'text = stringResource(R.string.generate_and_save_passwords_description)',
    '"Delete Password?"': 'stringResource(R.string.delete_password_title)',
    
    # GameScreen
    '"Time: ${difficulty.memorizeTime}s"': 'stringResource(R.string.time_format, difficulty.memorizeTime)',
    '"Length: ${difficulty.minPasswordLength}+ chars"': 'stringResource(R.string.length_format, difficulty.minPasswordLength)',
    '"Options: ${difficulty.decoyCount + 1}"': 'stringResource(R.string.options_format, difficulty.decoyCount + 1)',
    '"Level ${playerStats.level}"': 'stringResource(R.string.level_format, playerStats.level)',
    '"+${difficulty.xpReward} XP"': 'stringResource(R.string.xp_plus_format, difficulty.xpReward)',
    '"+${result.xpEarned}"': 'stringResource(R.string.xp_reward_format, result.xpEarned)',
    '"${result.timeSpentSeconds}s"': 'stringResource(R.string.time_spent_format, result.timeSpentSeconds)',
    '"${result.attemptsUsed}"': '"${result.attemptsUsed}"',  # Keep as is - dynamic
    
    # PasswordCrackerScreen
    '"Password was: ${state.currentPassword}"': '"Password was: ${state.currentPassword}"',  # Complex, keep
    
    # PremiumScreen
    '"Unlock PassForge Premium"': 'stringResource(R.string.unlock_premium)',
    '"Ad-Free Experience"': 'stringResource(R.string.ad_free_experience)',
    '"Priority Support"': 'stringResource(R.string.priority_support)',
    '"Exclusive Security Tips"': 'stringResource(R.string.exclusive_security_tips)',
    '"Extra Attempts in Games"': 'stringResource(R.string.extra_attempts)',
    
    # TransferScreen
    '"Select All"': 'stringResource(R.string.select_all)',
    '"Deselect All"': 'stringResource(R.string.deselect_all)',
    '"Generating QR Code..."': 'stringResource(R.string.generating_qr_code)',
    '"Camera Scanner"': 'stringResource(R.string.camera_scanner)',
    '"Export Passwords"': 'stringResource(R.string.export_passwords)',
    '"Scan QR Code"': 'stringResource(R.string.scan_qr_code)',
    
    # OnboardingScreen
    'text = "Enable"': 'text = stringResource(R.string.enable)',
    
    # AcademyHomeScreen
    '"Choose Your Game"': 'stringResource(R.string.choose_your_game)',
    
    # General
    '"Favorite"': 'stringResource(R.string.favorite)',
    '"Cancel"': 'stringResource(R.string.cancel)',
    '"Delete"': 'stringResource(R.string.delete)',
    '"Dismiss"': 'stringResource(R.string.dismiss)',
}

def proc(c, fp):
    m = c
    n = 0
    for o, nw in MEGA_MAP.items():
        if o in m:
            m = m.replace(o, nw)
            n += 1
    return m, n

def imp(c):
    if 'stringResource(R.' not in c:
        return c
    h1 = 'import androidx.compose.ui.res.stringResource' in c
    h2 = 'import com.vtoptunov.passwordgenerator.R' in c
    if h1 and h2:
        return c
    ls = c.split('\n')
    for i, l in enumerate(ls):
        if l.startswith('package '):
            x = i + 1
            if not h2:
                ls.insert(x, 'import com.vtoptunov.passwordgenerator.R')
                x += 1
            if not h1:
                ls.insert(x, 'import androidx.compose.ui.res.stringResource')
            return '\n'.join(ls)
    return c

def pf(p):
    try:
        with open(p, 'r') as f:
            c = f.read()
        m, n = proc(c, p)
        if n > 0:
            m = imp(m)
            with open(p, 'w') as f:
                f.write(m)
            return n
        return 0
    except:
        return 0

if __name__ == '__main__':
    b = 'app/src/main/kotlin/com/vtoptunov/passwordgenerator/presentation/screens'
    t = 0
    files_done = []
    for r, d, fs in os.walk(b):
        for f in fs:
            if f.endswith('.kt') and 'ViewModel' not in f:
                n = pf(os.path.join(r, f))
                if n > 0:
                    t += n
                    files_done.append(f)
                    print(f"✅ {f}: {n}")
    print(f"\n📊 Phase 7 MEGA: {t} replacements in {len(files_done)} files")
