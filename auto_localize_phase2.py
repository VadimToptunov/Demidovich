#!/usr/bin/env python3
"""Advanced localization - Phase 2"""
import re, os

# Comprehensive mappings
MAPPINGS = {
    # Text() calls - simple
    'Text("Saved Passwords")': 'Text(stringResource(R.string.saved_passwords_title))',
    'Text("No Results Found")': 'Text(stringResource(R.string.no_results))',
    'Text("Correct!")': 'Text(stringResource(R.string.correct_exclamation))',
    'Text("Incorrect!")': 'Text(stringResource(R.string.incorrect_exclamation))',
    'Text("DASHBOARD")': 'Text(stringResource(R.string.dashboard_title))',
    'Text("GENERATOR")': 'Text(stringResource(R.string.generator_title))',
    'Text("PASSWORD")': 'Text(stringResource(R.string.password_title))',
    'Text("PREMIUM")': 'Text(stringResource(R.string.premium_title))',
    
    # Common labels
    'label = "All"': 'label = stringResource(R.string.all)',
    'label = "Time"': 'label = stringResource(R.string.time)',
    'label = "Hints"': 'label = stringResource(R.string.hints)',
    'label = "Streak"': 'label = stringResource(R.string.streak)',
    'label = "Score"': 'label = stringResource(R.string.score)',
    
    # Titles
    'title = "Delete Password?"': 'title = stringResource(R.string.delete_password_title)',
    
    # Descriptions
    'description = "Clear"': 'description = stringResource(R.string.clear)',
    
    # Placeholders
    'placeholder = { Text("Search passwords...") }': 'placeholder = { Text(stringResource(R.string.placeholder_search)) }',
    
    # Common strings in contentDescription
    '"Info"': 'stringResource(R.string.info)',
    '"Email"': 'stringResource(R.string.email_lowercase)',
    '"URL"': 'stringResource(R.string.url_lowercase)',
    '"Watch"': 'stringResource(R.string.watch_lowercase)',
    '"Legitimate"': 'stringResource(R.string.legitimate)',
    '"Phishing"': 'stringResource(R.string.phishing)',
    '"Camera"': 'stringResource(R.string.camera)',
}

# Patterns for dynamic replacements
PATTERNS = [
    (r'"Level (\d+)"', r'stringResource(R.string.level_number, \1)'),
    (r'\+"(\d+) XP"', r'stringResource(R.string.xp_reward, \1)'),
]

def apply_replacements(content, file_path):
    modified = content
    count = 0
    
    # Simple mappings
    for old, new in MAPPINGS.items():
        if old in modified:
            modified = modified.replace(old, new)
            count += 1
    
    # Pattern-based replacements
    for pattern, replacement in PATTERNS:
        if re.search(pattern, modified):
            modified = re.sub(pattern, replacement, modified)
            count += 1
    
    return modified, count

def ensure_imports(content):
    has_string_resource = 'import androidx.compose.ui.res.stringResource' in content
    has_r_import = 'import com.vtoptunov.passwordgenerator.R' in content
    
    if not has_string_resource or not has_r_import:
        lines = content.split('\n')
        # Find package line
        for i, line in enumerate(lines):
            if line.startswith('package '):
                insert_idx = i + 1
                if not has_r_import:
                    lines.insert(insert_idx, 'import com.vtoptunov.passwordgenerator.R')
                    insert_idx += 1
                if not has_string_resource:
                    lines.insert(insert_idx, 'import androidx.compose.ui.res.stringResource')
                return '\n'.join(lines)
    
    return content

def process_kotlin_file(file_path):
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        modified, count = apply_replacements(content, file_path)
        
        if count > 0:
            modified = ensure_imports(modified)
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(modified)
            return count
        return 0
    except Exception as e:
        print(f"❌ Error in {os.path.basename(file_path)}: {e}")
        return 0

if __name__ == '__main__':
    base = 'app/src/main/kotlin/com/vtoptunov/passwordgenerator/presentation/screens'
    total = 0
    files_modified = 0
    
    for root, dirs, files in os.walk(base):
        for file in files:
            if file.endswith('.kt'):
                path = os.path.join(root, file)
                count = process_kotlin_file(path)
                if count > 0:
                    files_modified += 1
                    total += count
                    print(f"✅ {file}: {count} replacements")
    
    print(f"\n📊 Phase 2: {total} replacements in {files_modified} files")
