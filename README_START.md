# JewelryShop / РГР №3 — комплект доработок

Цель: применить все программные компоненты РГР №3 к оригинальному Java/XML проекту Артёма, сохранив его ресурсы и текущие экраны. Это **overlay + установщик**, не самостоятельный исходный ZIP: GitHub доступен только для чтения, сетевое клонирование из среды подготовки недоступно. Установщик сам адаптирует исходники в чистом клоне, никакого ручного копирования Kotlin/Java файлов. Он не удаляет оригинальные ресурсы, не коммитит и не отправляет код.

## Установка (macOS/Linux)
Распакуйте архив, откройте Terminal в папке `JewelryShop_RGR3_bundle`:

```bash
git clone https://github.com/Djhdjd/JewelryShop.git
python3 apply_rgr3.py JewelryShop
cd JewelryShop
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest :app:lintDebug :app:detekt :app:testDebugUnitTest :app:rgr3CoverageVerify
```

Windows PowerShell:

```powershell
git clone https://github.com/Djhdjd/JewelryShop.git
python apply_rgr3.py JewelryShop
cd JewelryShop
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :app:lintDebug :app:detekt :app:testDebugUnitTest :app:rgr3CoverageVerify
```

Можно указать путь к уже скачанному чистому репозиторию вместо нового клона. Требуются Python 3, Git, JDK 17, Android SDK 35. В архиве bootstrap Gradle 8.11.1; при первом запуске скачивается официальный дистрибутив. Android Studio открывает папку JewelryShop. В случае ошибки установщик проверяет исходные фрагменты до записи. Не обновляйте зависимости массово до прохождения проверок.

## После локальной проверки
Запустите тестовый эмулятор API 34 (данные приложения очищаются между тестами):

```bash
./gradlew :app:connectedDebugAndroidTest :app:rgr3CombinedCoverageReport
```

Только после успешного прогона владелец репозитория может отправить изменения:

```bash
git switch -c feat/rgr3-testing
git add -A
git commit -m "feat: implement RGR3 Android testing and diagnostics"
git push -u origin feat/rgr3-testing
```

Затем PR в `master`. В workflow разрешены PR и push в master. Программные компоненты подготовлены, но **Android SDK/Gradle и эмулятор в среде создания отсутствуют**. Не утверждайте, что CI зелёный или покрытие достигнуто до фактического прогона. Firebase и профилирование требуют реальных действий.

Подробное руководство после установки: `docs/RGR3_GUIDE.md`.
