#!/usr/bin/env python3
"""Helper script to find all hardcoded strings that need localization."""

import os
import re
from collections import defaultdict

# Patterns to find hardcoded strings
PATTERNS = [
    r'"([A-Z][^"]+)"',  # Strings starting with capital letter
    r'title\s*=\s*"([^"]+)"',
    r'description\s*=\s*"([^"]+)"',
    r'Text\("([^"]+)"\)',
    r'label\s*=\s*"([^"]+)"',
]

def find_hardcoded_strings(file_path):
    """Find all hardcoded strings in a Kotlin file."""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    strings = set()
    for pattern in PATTERNS:
        matches = re.findall(pattern, content)
        strings.update(matches)
    
    # Filter out imports, comments, etc.
    filtered = {s for s in strings if not s.startswith('import') and len(s) > 2}
    return sorted(filtered)

def scan_directory(dir_path):
    """Scan all Kotlin files in directory."""
    results = defaultdict(list)
    
    for root, dirs, files in os.walk(dir_path):
        for file in files:
            if file.endswith('.kt'):
                file_path = os.path.join(root, file)
                strings = find_hardcoded_strings(file_path)
                if strings:
                    results[file_path] = strings
    
    return results

if __name__ == '__main__':
    screens_dir = 'app/src/main/kotlin/com/vtoptunov/passwordgenerator/presentation/screens'
    
    print("🔍 Scanning for hardcoded strings...")
    results = scan_directory(screens_dir)
    
    total = 0
    for file_path, strings in sorted(results.items()):
        print(f"\n📄 {os.path.basename(file_path)} ({len(strings)} strings):")
        for s in strings[:5]:  # Show first 5
            print(f"   - {s[:60]}")
        if len(strings) > 5:
            print(f"   ... and {len(strings) - 5} more")
        total += len(strings)
    
    print(f"\n\n📊 Total: {total} hardcoded strings found in {len(results)} files")
