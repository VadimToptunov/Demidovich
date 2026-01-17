#!/usr/bin/env python3
"""Phase 8 ULTIMATE: Final cleanup of remaining screens (non-ViewModels)"""
import re, os, sys

# Final comprehensive map - extracted from actual remaining strings
ULTIMATE_MAP = {
    # Dynamic formats that need special handling - commented out for safety
    # These need manual fixing with stringResource(R.string.format, param)
    
    # Simple text replacements that are safe
    'text = "Favorite"': 'text = stringResource(R.string.favorite)',
    'Text("All")': 'Text(stringResource(R.string.all))',
    
    # Add more after analyzing actual files
}

def analyze_file(path):
    """Find all hardcoded strings in a file"""
    try:
        with open(path, 'r') as f:
            content = f.read()
        
        # Find all string literals
        strings = re.findall(r'"([^"]{5,})"', content)
        return [s for s in strings if not s.startswith('$') and not re.match(r'^[\d\s]+$', s)]
    except:
        return []

def main():
    base = 'app/src/main/kotlin/com/vtoptunov/passwordgenerator/presentation/screens'
    
    # Analyze remaining files
    remaining = {
        'ThemeModePicker.kt': 1,
        'AcademyHomeScreen.kt': 3,
        'OnboardingScreen.kt': 5,
        'SavedPasswordsScreen.kt': 5,
        'PasswordCrackerScreen.kt': 8,
        'GeneratorScreen.kt': 8,
        'SocialEngineeringScreen.kt': 11,
        'LessonScreen.kt': 12,
        'DashboardScreen.kt': 14,
        'TransferScreen.kt': 15,
        'GameScreen.kt': 17,
        'PremiumScreen.kt': 17,
        'PhishingHunterScreen.kt': 26,
    }
    
    print("📊 Analyzing remaining files...")
    for filename in sorted(remaining.keys(), key=lambda x: remaining[x]):
        for root, dirs, files in os.walk(base):
            if filename in files:
                path = os.path.join(root, filename)
                strings = analyze_file(path)
                if strings:
                    print(f"\n📄 {filename} ({remaining[filename]} strings)")
                    for s in strings[:5]:  # Show first 5
                        print(f"   - {s[:70]}")

if __name__ == '__main__':
    main()
