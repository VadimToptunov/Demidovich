#!/usr/bin/env python3
"""Phase 4: Big files cleanup"""
import re, os

MAPS = {
    # Phishing specific strings
    '"Phishing Hunter 🎣"': 'stringResource(R.string.phishing_hunter)',
    '"Is this legitimate or a phishing attempt?"': 'stringResource(R.string.is_legitimate_or_phishing)',
    '"Email Details"': 'stringResource(R.string.email_details)',
    '"Website URL"': 'stringResource(R.string.website_url)',
    '"Red flags in this scenario:"': 'stringResource(R.string.red_flags_in_scenario)',
    '"This scenario appears legitimate with no obvious red flags. Always verify through official channels when in doubt!"': 'stringResource(R.string.legitimate_scenario)',
    '"This was LEGITIMATE"': 'stringResource(R.string.legitimate_message)',
    '"This was a PHISHING attempt"': 'stringResource(R.string.phishing_attempt)',
    '"Next Scenario →"': 'stringResource(R.string.next_scenario)',
    
    # Phishing red flags
    '"Asks for password"': 'stringResource(R.string.requests_password)',
    '"Contains spelling errors"': 'stringResource(R.string.spelling_errors)',
    '"Creates false urgency"': 'stringResource(R.string.false_urgency)',
    '"Domain has suspicious numbers"': 'stringResource(R.string.numbers_in_domain)',
    '"Domain name looks suspicious"': 'stringResource(R.string.suspicious_domain)',
    '"Email address is suspicious"': 'stringResource(R.string.suspicious_email)',
    '"Has suspicious attachment"': 'stringResource(R.string.suspicious_attachment)',
    '"Offer seems too good to be true"': 'stringResource(R.string.too_good_to_be_true)',
    '"URL contains typos"': 'stringResource(R.string.typo_in_url)',
    '"Uses generic greeting"': 'stringResource(R.string.generic_greeting)',
    '"Website doesn\'t use HTTPS encryption"': 'stringResource(R.string.no_https)',
    '"URL doesn\'t match claimed sender"': 'stringResource(R.string.mismatched_url)',
    
    # Dashboard
    '"Average Strength"': 'stringResource(R.string.average_strength)',
    '"All Clear!"': 'stringResource(R.string.all_clear)',
    '"No security issues detected"': 'stringResource(R.string.no_security_issues)',
    '"Avg Entropy"': 'stringResource(R.string.avg_entropy)',
    '"Total Passwords"': 'stringResource(R.string.total_passwords)',
    '"Weak Passwords"': 'stringResource(R.string.weak_passwords)',
    '"Reused Passwords"': 'stringResource(R.string.reused_passwords)',
    '"Security Score"': 'stringResource(R.string.security_score)',
    '"Password Health"': 'stringResource(R.string.password_health)',
    
    # Generator
    '"A-Z"': 'stringResource(R.string.uppercase_letters)',
    '"a-z"': 'stringResource(R.string.lowercase_letters)',
    '"0-9"': 'stringResource(R.string.numbers_label)',
    '"!@#"': 'stringResource(R.string.symbols_label)',
    '"GENERATOR"': 'stringResource(R.string.generator_title)',
    '"PASSWORD"': 'stringResource(R.string.password_title)',
    
    # Lesson
    '"Correct"': 'stringResource(R.string.correct)',
    '"Example"': 'stringResource(R.string.example)',
    
    # Premium
    '"BEST VALUE"': 'stringResource(R.string.best_value)',
    '"Monthly"': 'stringResource(R.string.monthly)',
    '"Yearly"': 'stringResource(R.string.yearly)',
    '"Lifetime"': 'stringResource(R.string.lifetime)',
    '"Dismiss"': 'stringResource(R.string.dismiss)',
    
    # Academy
    '"Each game teaches you real cybersecurity concepts."': 'stringResource(R.string.each_game_teaches)',
    '"Complete levels to earn XP and unlock new games!"': 'stringResource(R.string.complete_levels_to_unlock)',
    '"Locked"': 'stringResource(R.string.locked)',
    '"Learn cybersecurity"': 'stringResource(R.string.learn_cybersecurity)',
    '"Start Lesson"': 'stringResource(R.string.start_lesson_button)',
    
    # Onboarding
    '"Welcome to PassForge"': 'stringResource(R.string.welcome_to_passforge)',
    '"Enable Biometric Lock?"': 'stringResource(R.string.enable_biometric_lock)',
    '"Fort Knox Security"': 'stringResource(R.string.fort_knox_security)',
    '"PassForge Academy"': 'stringResource(R.string.passforge_academy_title)',
    '"Biometric Protection"': 'stringResource(R.string.biometric_protection)',
    
    # Password Cracker
    '"Crack Password"': 'stringResource(R.string.crack_password)',
    '"Enter the weak password"': 'stringResource(R.string.enter_weak_password)',
    '"Hint"': 'stringResource(R.string.hint)',
    
    # Social Engineering
    '"Choose your response:"': 'stringResource(R.string.choose_your_response)',
    '"Always verify the identity of people requesting information"': 'stringResource(R.string.always_verify)',
    '"Be suspicious of unsolicited offers. If it seems too good to be true, it probably is!"': 'stringResource(R.string.be_suspicious)',
    
    # Transfer
    '"Choose Transfer Mode"': 'stringResource(R.string.choose_transfer_mode)',
    '"Export"': 'stringResource(R.string.export)',
    '"Import"': 'stringResource(R.string.import_action)',
    '"Scan QR Code"': 'stringResource(R.string.scan_qr_code)',
    '"Camera"': 'stringResource(R.string.camera)',
}

def proc(c, p):
    m = c
    n = 0
    for o, nw in MAPS.items():
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
    b = 'app/src/main/kotlin/com/vtoptunov/passwordgenerator/presentation'
    t = 0
    for r, d, fs in os.walk(b):
        for f in fs:
            if f.endswith('.kt'):
                n = pf(os.path.join(r, f))
                if n > 0:
                    t += n
                    print(f"✅ {f}: {n}")
    print(f"\n📊 Phase 4: {t} replacements")
