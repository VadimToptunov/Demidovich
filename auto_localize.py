#!/usr/bin/env python3
"""
Automated localization script for PassForge.
Replaces hardcoded strings with stringResource() calls.
"""

import re
import os

# Mapping of common hardcoded strings to their resource keys
STRING_MAPPINGS = {
    # Common
    '"Back"': 'R.string.back',
    '"Cancel"': 'R.string.cancel',
    '"Delete"': 'R.string.delete',
    '"Copy"': 'R.string.copy',
    '"Close"': 'R.string.close',
    '"Save"': 'R.string.save',
    '"OK"': 'R.string.ok',
    '"All"': 'R.string.all',
    
    # Memory Match
    '"Confirm Selection"': 'R.string.confirm_selection',
    '"Memorize the Password"': 'R.string.memorize_password',
    '"Select the Password"': 'R.string.select_password',
    '"Game Over"': 'R.string.game_over',
    '"You Won!"': 'R.string.you_won',
    '"Try Again"': 'R.string.try_again',
    '"Attempts"': 'R.string.attempts',
    '"Score"': 'R.string.score',
    
    # contentDescription
    'contentDescription = "Back"': 'contentDescription = stringResource(R.string.back)',
    'contentDescription = "Collapse"': 'contentDescription = stringResource(R.string.collapse)',
    'contentDescription = "Expand"': 'contentDescription = stringResource(R.string.expand)',
    'contentDescription = "Show"': 'contentDescription = stringResource(R.string.show)',
    'contentDescription = "Hide"': 'contentDescription = stringResource(R.string.hide)',
    'contentDescription = "Clear"': 'contentDescription = stringResource(R.string.clear)',
    
    # Conditional contentDescription
    'contentDescription = if (expanded) "Collapse" else "Expand"': 
        'contentDescription = stringResource(if (expanded) R.string.collapse else R.string.expand)',
    'contentDescription = if (isExpanded) "Collapse" else "Expand"': 
        'contentDescription = stringResource(if (isExpanded) R.string.collapse else R.string.expand)',
    'contentDescription = if (showPassword) "Hide" else "Show"': 
        'contentDescription = stringResource(if (showPassword) R.string.hide else R.string.show)',
}

def needs_import(content):
    """Check if file needs stringResource import."""
    return 'import androidx.compose.ui.res.stringResource' not in content

def add_import(content):
    """Add stringResource import after other compose.ui imports."""
    # Find the last compose.ui import
    lines = content.split('\n')
    last_compose_import_idx = -1
    
    for i, line in enumerate(lines):
        if 'import androidx.compose.ui' in line and 'res.stringResource' not in line:
            last_compose_import_idx = i
    
    if last_compose_import_idx >= 0:
        lines.insert(last_compose_import_idx + 1, 'import androidx.compose.ui.res.stringResource')
        return '\n'.join(lines)
    
    # If no compose.ui imports, add after package
    for i, line in enumerate(lines):
        if line.startswith('import'):
            lines.insert(i, 'import androidx.compose.ui.res.stringResource')
            return '\n'.join(lines)
    
    return content

def replace_strings(content):
    """Replace hardcoded strings with stringResource() calls."""
    modified = content
    replacements_made = 0
    
    for old_string, resource_key in STRING_MAPPINGS.items():
        if old_string in modified:
            if 'contentDescription =' in old_string:
                # Already has stringResource in replacement
                modified = modified.replace(old_string, resource_key)
            else:
                # Wrap in stringResource()
                modified = modified.replace(old_string, f'stringResource({resource_key})')
            replacements_made += 1
    
    return modified, replacements_made

def process_file(file_path):
    """Process a single Kotlin file."""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        
        # Replace strings
        content, replacements = replace_strings(content)
        
        if replacements > 0:
            # Add import if needed
            if needs_import(content):
                content = add_import(content)
            
            # Write back
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            
            return replacements
        
        return 0
    except Exception as e:
        print(f"Error processing {file_path}: {e}")
        return 0

if __name__ == '__main__':
    screens_dir = 'app/src/main/kotlin/com/vtoptunov/passwordgenerator/presentation/screens'
    
    print("🔧 Starting automated localization...")
    total_replacements = 0
    files_modified = 0
    
    for root, dirs, files in os.walk(screens_dir):
        for file in files:
            if file.endswith('.kt'):
                file_path = os.path.join(root, file)
                replacements = process_file(file_path)
                
                if replacements > 0:
                    files_modified += 1
                    total_replacements += replacements
                    print(f"✅ {os.path.basename(file_path)}: {replacements} replacements")
    
    print(f"\n📊 Summary:")
    print(f"   Files modified: {files_modified}")
    print(f"   Total replacements: {total_replacements}")
    print(f"\n✅ Done! Run 'git diff' to review changes.")
