#!/usr/bin/env python3
"""Phase 3: Final massive cleanup"""
import re, os

# Comprehensive final mappings
FINAL_MAPS = {
    'Text("Watch Ad")': 'Text(stringResource(R.string.watch_ad))',
    'Text("Exit")': 'Text(stringResource(R.string.exit))',
    'Text("Play Again")': 'Text(stringResource(R.string.play_again))',
    'Text("Choose Your Game")': 'Text(stringResource(R.string.choose_game))',
    'Text("Completed!")': 'Text(stringResource(R.string.completed_exclamation))',
    '"Password Generator"': 'stringResource(R.string.password_generator)',
    '"PASSFORGE"': 'stringResource(R.string.passforge)',
    
    # Dashboard
    'text = "DASHBOARD"': 'text = stringResource(R.string.dashboard_title)',
    '"DASHBOARD"': 'stringResource(R.string.dashboard_title)',
    
    # Academy
    '"Learn While Playing"': 'stringResource(R.string.learn_while_playing)',
    
    # Premium
    '"BEST VALUE"': 'stringResource(R.string.best_value)',
    
    # Lesson
    'Text("Check Answer")': 'Text(stringResource(R.string.check_answer))',
    'Text("Example")': 'Text(stringResource(R.string.example))',
    
    # Onboarding
    'Text("Enable")': 'Text(stringResource(R.string.enable))',
    'Text("Skip")': 'Text(stringResource(R.string.skip))',
    'Text("Get Started")': 'Text(stringResource(R.string.get_started))',
}

def process(content, file_path):
    m = content
    c = 0
    for old, new in FINAL_MAPS.items():
        if old in m:
            m = m.replace(old, new)
            c += 1
    return m, c

def ensure_imports(content):
    if 'stringResource(R.' not in content:
        return content
    
    has_sr = 'import androidx.compose.ui.res.stringResource' in content
    has_r = 'import com.vtoptunov.passwordgenerator.R' in content
    
    if has_sr and has_r:
        return content
    
    lines = content.split('\n')
    for i, line in enumerate(lines):
        if line.startswith('package '):
            idx = i + 1
            if not has_r:
                lines.insert(idx, 'import com.vtoptunov.passwordgenerator.R')
                idx += 1
            if not has_sr:
                lines.insert(idx, 'import androidx.compose.ui.res.stringResource')
            return '\n'.join(lines)
    return content

def process_file(path):
    try:
        with open(path, 'r') as f:
            c = f.read()
        m, n = process(c, path)
        if n > 0:
            m = ensure_imports(m)
            with open(path, 'w') as f:
                f.write(m)
            return n
        return 0
    except Exception as e:
        return 0

if __name__ == '__main__':
    base = 'app/src/main/kotlin/com/vtoptunov/passwordgenerator/presentation'
    t = 0
    for r, d, fs in os.walk(base):
        for f in fs:
            if f.endswith('.kt'):
                n = process_file(os.path.join(r, f))
                if n > 0:
                    t += n
                    print(f"✅ {f}: {n}")
    print(f"\n📊 Phase 3: {t} replacements")
