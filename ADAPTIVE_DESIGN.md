# 📐 Adaptive Design System - PassForge

## Overview

PassForge uses a **responsive design system** that adapts to different screen sizes, from small phones (4") to large tablets (10"+).

Based on **Material Design 3** window size classes.

---

## 🖥️ Window Size Classes

### 1. **COMPACT** (< 600dp)
- **Devices**: Phones in portrait mode
- **Examples**: Pixel 5, iPhone 13 Mini
- **Grid**: 1 column
- **Icon size**: 24dp (medium)
- **Spacing**: 16dp (medium)

### 2. **MEDIUM** (600dp - 840dp)
- **Devices**: Phones in landscape, small tablets
- **Examples**: Pixel 7 Pro landscape, iPad Mini
- **Grid**: 2 columns
- **Icon size**: 28dp (medium)
- **Spacing**: 20dp (medium)

### 3. **EXPANDED** (> 840dp)
- **Devices**: Large tablets, foldables, desktop
- **Examples**: iPad Pro, Galaxy Tab S8, ChromeOS
- **Grid**: 3 columns
- **Icon size**: 32dp (medium)
- **Spacing**: 24dp (medium)

---

## 💻 Usage Examples

### Basic Usage

```kotlin
@Composable
fun MyScreen() {
    val dimensions = LocalDimensions.current
    
    Column(
        modifier = Modifier.padding(dimensions.spacingMedium)
    ) {
        Icon(
            Icons.Default.Star,
            modifier = Modifier.size(dimensions.iconLarge)
        )
        
        Spacer(modifier = Modifier.height(dimensions.spacingSmall))
        
        Text("Hello World")
    }
}
```

### Conditional Layout

```kotlin
@Composable
fun AdaptiveLayout() {
    val dimensions = LocalDimensions.current
    
    when (dimensions.windowSize) {
        WindowSize.COMPACT -> {
            // Single column layout
            Column { /* content */ }
        }
        WindowSize.MEDIUM, WindowSize.EXPANDED -> {
            // Two/three column layout
            Row { /* content */ }
        }
    }
}
```

### Grid Adaptation

```kotlin
@Composable
fun GameGrid() {
    val dimensions = LocalDimensions.current
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(dimensions.gridColumns)
    ) {
        // Items
    }
}
```

---

## 📏 Dimension Reference

### Spacing Scale

| Name | Compact | Medium | Expanded | Usage |
|------|---------|--------|----------|-------|
| `spacingExtraSmall` | 4dp | 6dp | 8dp | Tiny gaps |
| `spacingSmall` | 8dp | 12dp | 16dp | Small padding |
| `spacingMedium` | 16dp | 20dp | 24dp | Default padding |
| `spacingLarge` | 24dp | 32dp | 40dp | Section spacing |
| `spacingExtraLarge` | 32dp | 48dp | 64dp | Large gaps |

### Icon Sizes

| Name | Compact | Medium | Expanded | Usage |
|------|---------|--------|----------|-------|
| `iconSmall` | 16dp | 20dp | 24dp | Inline icons |
| `iconMedium` | 24dp | 28dp | 32dp | Nav icons |
| `iconLarge` | 32dp | 40dp | 48dp | Feature icons |
| `iconExtraLarge` | 48dp | 64dp | 80dp | Hero icons |

### Component Sizes

| Name | Compact | Medium | Expanded | Usage |
|------|---------|--------|----------|-------|
| `buttonHeight` | 56dp | 64dp | 72dp | Button height |
| `cardCornerRadius` | 12dp | 16dp | 20dp | Card corners |
| `cardElevation` | 4dp | 6dp | 8dp | Card shadow |
| `maxContentWidth` | 600dp | 840dp | 1200dp | Max width |

---

## 🎨 Migration Guide

### Before (Hardcoded)
```kotlin
Icon(
    Icons.Default.Settings,
    modifier = Modifier.size(28.dp)
)

Spacer(modifier = Modifier.height(16.dp))

Card(
    shape = RoundedCornerShape(12.dp)
)
```

### After (Adaptive)
```kotlin
val dimensions = LocalDimensions.current

Icon(
    Icons.Default.Settings,
    modifier = Modifier.size(dimensions.iconMedium)
)

Spacer(modifier = Modifier.height(dimensions.spacingMedium))

Card(
    shape = RoundedCornerShape(dimensions.cardCornerRadius)
)
```

---

## 🚀 Setup

### 1. Wrap your app with ProvideDimensions

```kotlin
@Composable
fun App() {
    PassForgeTheme {
        ProvideDimensions {
            // Your content
            MainScreen()
        }
    }
}
```

### 2. Use in composables

```kotlin
@Composable
fun MyScreen() {
    val dimen = LocalDimensions.current
    
    // Use dimen.* everywhere
}
```

---

## 🧪 Testing Different Sizes

### In Android Studio

1. **Emulator Preview**:
   - Compact: Pixel 5 (1080x2340, 393dp x 851dp)
   - Medium: Pixel C (2560x1800, 900dp x 1280dp)
   - Expanded: Pixel Tablet (2560x1600, 840dp x 1340dp)

2. **Layout Inspector**: 
   - Run app → Tools → Layout Inspector
   - See actual dp values

3. **Preview Annotations**:
```kotlin
@Preview(device = "spec:width=411dp,height=891dp") // Compact
@Preview(device = "spec:width=673dp,height=841dp") // Medium  
@Preview(device = "spec:width=1280dp,height=800dp") // Expanded
@Composable
fun PreviewMyScreen() {
    ProvideDimensions {
        MyScreen()
    }
}
```

---

## 📱 Real Device Testing

| Device | Size | Expected Class |
|--------|------|----------------|
| Pixel 5 (Portrait) | 393dp | COMPACT |
| Pixel 7 Pro (Portrait) | 412dp | COMPACT |
| Pixel 7 Pro (Landscape) | 915dp | EXPANDED |
| Galaxy Fold (Unfolded) | 884dp | EXPANDED |
| iPad Mini | 744dp | MEDIUM |
| iPad Pro 12.9" | 1024dp | EXPANDED |

---

## 🎯 Best Practices

### ✅ DO

- Use semantic names: `spacingMedium` not `16dp`
- Adapt layouts for different sizes
- Test on multiple screen sizes
- Use `maxContentWidth` for large screens

### ❌ DON'T

- Hardcode dp values directly
- Assume single screen size
- Forget landscape mode
- Ignore tablets

---

## 🔄 Future Enhancements

- [ ] Font scaling support
- [ ] Dynamic color based on size
- [ ] Orientation-specific layouts
- [ ] Accessibility scaling
- [ ] Desktop-specific components

---

## 📚 References

- [Material Design 3 - Window Size Classes](https://m3.material.io/foundations/layout/applying-layout/window-size-classes)
- [Android Developers - Responsive UI](https://developer.android.com/guide/topics/large-screens/support-different-screen-sizes)
- [Compose Adaptive Layouts](https://developer.android.com/jetpack/compose/layouts/adaptive)

---

Made with ❤️ for PassForge
