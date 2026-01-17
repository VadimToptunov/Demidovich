#!/usr/bin/env python3
"""Phase 5 FINAL: Complete remaining localization"""
import re, os

FINAL = {
    # Exact Text() replacements
    'Text("Correct!")': 'Text(stringResource(R.string.correct_exclamation))',
    'Text("Incorrect!")': 'Text(stringResource(R.string.incorrect_exclamation))',
    'Text("Attempt")': 'Text(stringResource(R.string.attempt))',
    'Text("Attempts Used")': 'Text(stringResource(R.string.attempts_used))',
    'Text("Choose Difficulty")': 'Text(stringResource(R.string.choose_difficulty))',
    'Text("Confirm")': 'Text(stringResource(R.string.confirm_selection))',
    'Text("Done")': 'Text(stringResource(R.string.done))',
    'Text("Finish")': 'Text(stringResource(R.string.finish))',
    'Text("Completed!")': 'Text(stringResource(R.string.completed_exclamation))',
    'Text("Theme")': 'Text(stringResource(R.string.theme))',
    'Text("Dashboard")': 'Text(stringResource(R.string.dashboard))',
    
    # Title/description patterns
    'title = "Delete Password?"': 'title = stringResource(R.string.delete_password_title)',
    'description = "3.0.0 (Generator v2)"': 'description = stringResource(R.string.app_version)',
    'description = "Exclusive security tips"': 'description = stringResource(R.string.exclusive_security_tips)',
    
    # Labels
    'label = "Difficulty"': 'label = stringResource(R.string.difficulty_label)',
    'label = "Explanation"': 'label = stringResource(R.string.explanation_label)',
    'label = "Crack Time"': 'label = stringResource(R.string.crack_time_label)',
    
    # Simple strings in code
    '"Choose your preferred theme"': 'stringResource(R.string.choose_preferred_theme)',
    '"☀️ Light"': 'stringResource(R.string.light_theme)',
    '"🌙 Dark"': 'stringResource(R.string.dark_theme)',
    '"📱 System"': 'stringResource(R.string.system_theme)',
    '"Deselect All"': 'stringResource(R.string.deselect_all)',
    '"Generating QR Code..."': 'stringResource(R.string.generating_qr_code)',
    '"Camera Scanner"': 'stringResource(R.string.camera_scanner)',
    '"Enable"': 'stringResource(R.string.enable)',
    '"Completed!"': 'stringResource(R.string.completed_exclamation)',
    '"Correct!"': 'stringResource(R.string.correct_exclamation)',
    '"Check Answer"': 'stringResource(R.string.check_answer)',
    '"Favorite"': 'stringResource(R.string.favorite)',
    '"Difficulty"': 'stringResource(R.string.difficulty_label)',
    '"Crack Password"': 'stringResource(R.string.crack_password)',
    '"Enter the weak password"': 'stringResource(R.string.enter_weak_password)',
    
    # Transfer specific
    '"Export Passwords"': 'stringResource(R.string.export_passwords)',
    '"Scan QR Code"': 'stringResource(R.string.scan_qr_code)',
    '"Select All"': 'stringResource(R.string.select_all)',
    
    # Theme picker
    'Text("Choose your preferred theme")': 'Text(stringResource(R.string.choose_preferred_theme))',
    
    # Settings
    'title = "Confirm your fingerprint or face to enable biometric lock"': 'title = activity.getString(R.string.verify_fingerprint)',
    'subtitle = "Enable Biometric Authentication"': 'subtitle = activity.getString(R.string.enable_biometric_auth_title)',
    'description = "Verify your identity"': 'description = activity.getString(R.string.verify_your_identity)',
}

def proc(c, fp):
    m = c
    n = 0
    for o, nw in FINAL.items():
        if o in m:
            m = m.replace(o, nw)
            n += 1
    return m, n

def imp(c):
    if 'stringResource(R.' not in c and 'getString(R.' not in c:
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
    b = 'app/src/main/kotlin/com/vtoptunov/passwordgenerator/presentation'
    t = 0
    for r, d, fs in os.walk(b):
        for f in fs:
            if f.endswith('.kt') and 'ViewModel' not in f:  # Skip ViewModels
                n = pf(os.path.join(r, f))
                if n > 0:
                    t += n
                    print(f"✅ {f}: {n}")
    print(f"\n📊 Phase 5 FINAL: {t} replacements")
