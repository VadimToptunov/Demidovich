# 🔄 Migration Checklist - Adaptive Design

## Priority Order

### ✅ Phase 1: Core Screens (High Priority)
- [x] GeneratorScreen.kt - **DONE** ✅
- [ ] AcademyHomeScreen.kt
- [ ] GameScreen.kt (Memory Match)
- [ ] SavedPasswordsScreen.kt
- [ ] SettingsScreen.kt

### 📋 Phase 2: Game Screens (Medium Priority)
- [ ] PasswordCrackerScreen.kt
- [ ] PhishingHunterScreen.kt  
- [ ] SocialEngineeringScreen.kt
- [ ] LessonScreen.kt

### 📦 Phase 3: Secondary Screens (Low Priority)
- [ ] DashboardScreen.kt
- [ ] PremiumScreen.kt
- [ ] OnboardingScreen.kt
- [ ] TransferScreen.kt
- [ ] SplashScreen.kt

---

## Migration Steps (Per Screen)

### 1. Add dimensions variable
```kotlin
@Composable
fun MyScreen() {
    val dimensions = LocalDimensions.current  // ✅ Add this
    
    // Rest of code...
}
```

### 2. Replace hardcoded spacing
```diff
- .padding(16.dp)
+ .padding(dimensions.spacingMedium)

- Arrangement.spacedBy(8.dp)
+ Arrangement.spacedBy(dimensions.spacingSmall)

- Spacer(modifier = Modifier.height(24.dp))
+ Spacer(modifier = Modifier.height(dimensions.spacingLarge))
```

### 3. Replace hardcoded icon sizes
```diff
- modifier = Modifier.size(24.dp)
+ modifier = Modifier.size(dimensions.iconMedium)

- modifier = Modifier.size(20.dp)
+ modifier = Modifier.size(dimensions.iconSmall)
```

### 4. Replace card properties
```diff
- shape = RoundedCornerShape(12.dp)
+ shape = RoundedCornerShape(dimensions.cardCornerRadius)

- elevation = CardDefaults.cardElevation(4.dp)
+ elevation = CardDefaults.cardElevation(dimensions.cardElevation)
```

### 5. Adapt grid layouts
```diff
- LazyVerticalGrid(columns = GridCells.Fixed(2))
+ LazyVerticalGrid(columns = GridCells.Fixed(dimensions.gridColumns))
```

### 6. Test on different sizes
- ✅ Compact (Pixel 5)
- ✅ Medium (iPad Mini landscape)
- ✅ Expanded (Tablet)

---

## Common Patterns

### Pattern 1: Conditional Layout
```kotlin
when (dimensions.windowSize) {
    WindowSize.COMPACT -> {
        // Single column
        Column { /* content */ }
    }
    WindowSize.MEDIUM, WindowSize.EXPANDED -> {
        // Multiple columns
        Row { /* content */ }
    }
}
```

### Pattern 2: Adaptive Grid
```kotlin
LazyVerticalGrid(
    columns = GridCells.Adaptive(minSize = 150.dp),  // Better than Fixed
    horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall),
    verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
)
```

### Pattern 3: Max Content Width
```kotlin
Column(
    modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = dimensions.maxContentWidth)  // Constrain on large screens
        .padding(dimensions.spacingMedium)
)
```

---

## Dimension Mapping Guide

| Old Hardcoded | New Adaptive | Notes |
|---------------|--------------|-------|
| `4.dp` | `spacingExtraSmall` | Tiny gaps |
| `8.dp` | `spacingSmall` | Small padding |
| `12.dp` | `spacingSmall` or `spacingMedium` | Context-dependent |
| `16.dp` | `spacingMedium` | Default padding |
| `20.dp` | `iconSmall` | Small icons |
| `24.dp` | `spacingLarge` or `iconMedium` | Check context |
| `28.dp` | `iconMedium` | Nav icons |
| `32.dp` | `spacingExtraLarge` or `iconLarge` | Check context |
| `48.dp` | `iconExtraLarge` | Hero icons |
| `56.dp` | `buttonHeight` | Button height |
| `12.dp` (radius) | `cardCornerRadius` | Card corners |

---

## Testing Script

```bash
# Run on different emulator configs
./gradlew connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.size=compact

./gradlew connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.size=medium

./gradlew connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.size=expanded
```

---

## Verification Checklist (Per Screen)

- [ ] No hardcoded `dp` values for spacing
- [ ] No hardcoded `dp` values for icons
- [ ] Grid layouts use `dimensions.gridColumns` or `Adaptive`
- [ ] Cards use `dimensions.cardCornerRadius`
- [ ] Content constrained with `maxContentWidth` on large screens
- [ ] Tested on 3 screen sizes (Compact, Medium, Expanded)
- [ ] No horizontal scrolling needed
- [ ] All elements properly visible and accessible

---

## Progress Tracker

**Total Screens**: 13  
**Migrated**: 1 (7.7%)  
**Remaining**: 12  

**Estimated Time**: ~2-3 hours (10-15 min per screen)

---

Last updated: 2026-01-17
