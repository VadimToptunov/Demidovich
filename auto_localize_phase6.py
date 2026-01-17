#!/usr/bin/env python3
"""Phase 6: Dynamic strings and remaining screens"""
import re, os

MAPS = {
    # GameScreen specifics
    'text = "Memorize this password"': 'text = stringResource(R.string.memorize_this_password)',
    'text = "Select the correct password"': 'text = stringResource(R.string.select_correct_password)',
    'text = "Focus on the details!"': 'text = stringResource(R.string.focus_on_details)',
    'Text("Memory Training")': 'Text(stringResource(R.string.memory_training))',
    'Text("Get an Extra Attempt")': 'Text(stringResource(R.string.get_extra_attempt))',
    'Text("Watch a short ad to try again")': 'Text(stringResource(R.string.watch_ad_to_try_again))',
    'Text("Choose Difficulty")': 'Text(stringResource(R.string.choose_difficulty))',
    'Text("Correct Password:")': 'Text(stringResource(R.string.correct_password))',
    'text = "Memory Training"': 'text = stringResource(R.string.memory_training)',
    'text = "Correct Password:"': 'text = stringResource(R.string.correct_password)',
    
    # Premium specifics
    'Text("Ad-Free Experience")': 'Text(stringResource(R.string.ad_free_experience))',
    'Text("Priority Support")': 'Text(stringResource(R.string.priority_support))',
    
    # Transfer
    'Text("Select All")': 'Text(stringResource(R.string.select_all))',
    'Text("Deselect All")': 'Text(stringResource(R.string.deselect_all))',
    
    # SavedPasswords
    'Text("All")': 'Text(stringResource(R.string.all))',
}

# Regex patterns for dynamic strings
PATTERNS = [
    # GameScreen dynamic
    (r'"Time: (\$\{[^}]+\})s"', r'stringResource(R.string.time_format, \1)'),
    (r'"Length: (\$\{[^}]+\})\+ chars"', r'stringResource(R.string.length_format, \1)'),
    (r'"Options: (\$\{[^}]+\})"', r'stringResource(R.string.options_format, \1)'),
    (r'"Level (\$\{[^}]+\})"', r'stringResource(R.string.level_format, \1)'),
    (r'"\+(\$\{[^}]+\}) XP"', r'stringResource(R.string.xp_plus_format, \1)'),
]

def proc(c, fp):
    m = c
    n = 0
    
    # Simple maps
    for o, nw in MAPS.items():
        if o in m:
            m = m.replace(o, nw)
            n += 1
    
    # Pattern-based (commented out for now - complex to handle in Python regex)
    # for pat, repl in PATTERNS:
    #     matches = re.findall(pat, m)
    #     if matches:
    #         m = re.sub(pat, repl, m)
    #         n += len(matches)
    
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
    for r, d, fs in os.walk(b):
        for f in fs:
            if f.endswith('.kt') and 'ViewModel' not in f:
                n = pf(os.path.join(r, f))
                if n > 0:
                    t += n
                    print(f"✅ {f}: {n}")
    print(f"\n📊 Phase 6: {t} replacements")
