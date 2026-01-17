#!/bin/bash
# Скрипт для обновления remote URL после переименования репозитория

echo "🔄 Обновление remote URL для PassForge..."

# Показать текущий remote
echo "📍 Текущий remote:"
git remote -v

# Обновить remote URL
echo ""
echo "🔧 Обновляем remote URL..."
git remote set-url origin https://github.com/VadimToptunov/PassForge.git

# Показать обновлённый remote
echo ""
echo "✅ Обновлённый remote:"
git remote -v

echo ""
echo "🎉 Готово! Репозиторий теперь указывает на PassForge"
