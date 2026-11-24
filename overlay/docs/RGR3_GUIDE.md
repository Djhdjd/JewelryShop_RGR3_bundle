# JewelryShop — РГР №3. Тестирование и отладка мобильных приложений в Android

Цель: освоить средства отладки, unit, интеграционные и UI тесты, обнаружение утечек, производительность и CI. Проект: Java/XML, 8 исходных Activity, каталог украшений, корзина, оформление, рассрочка, отзывы и отслеживание. Дополнения: Kotlin Flow для учебного HTTP/Room сценария, debug-лаборатория. Реальный склад, оплата и доставка не интегрированы: это учебные функции. Существующие корзина/заказы остаются в SharedPreferences; Room хранит локальный кэш демо-наличия, не подменяет хранилище заказов.

## Команды (macOS/Linux)

```bash
chmod +x gradlew
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest
./gradlew :app:lintDebug :app:detekt :app:testDebugUnitTest :app:rgr3CoverageReport :app:rgr3CoverageVerify
./gradlew :app:connectedDebugAndroidTest
./gradlew :app:rgr3CombinedCoverageReport
./gradlew :benchmark:connectedBenchmarkAndroidTest
```

Windows: `.\gradlew.bat` вместо `./gradlew` (PowerShell). Для Android-тестов используйте отдельный эмулятор, Orchestrator очищает данные. Для Macrobenchmark предпочтительно физическое устройство. Если название benchmark-задачи отличается: `./gradlew :benchmark:tasks --all`.

## 1. Logcat и уровни
Debug build → «Лаборатория отладки · РГР №3» на главной. Нажать Log d/i/w/e. В Logcat использовать `tag:JS_LAB`, `package:com.example.jewelryshop`, `level:ERROR`, `level:WARN`, `level:DEBUG`. Добавить товар в корзину (JS_CART), создать заказ (JS_ORDER), выполнить запрос (JS_NET). `ShopLog` проверяет BuildConfig.DEBUG; Activity и debug-демонстрации отсутствуют в release. Снимки: разные фильтры, код отключения логов.

## 2. Отладчик
Debug app, breakpoint на `DebugLabActivity.buggyMonthly` строка `int result=amount/months`. Воспроизвести расчёт 12 990 / 12: ошибочный 1082, корректный 1083 (остаток 6). Step Into/Over/Out, Watches amount/months/result, Evaluate Expression `amount % months`. Условный breakpoint `months == 12`, logpoint `"monthly=" + result` после присваивания, снять снимки. Исправленная реализация `ShopRules.monthlyPayment` используется в каталоге, карточке и калькуляторе. Уточнение: распределение 6 платежей по 1083 и 6 по 1082 сохраняет сумму 12990, а число 1083 — округлённый верхний ежемесячный платёж.

## 3. Исключения
В Android Studio View Breakpoints → Java Exception Breakpoints, включить caught exceptions. В Debug Lab по очереди вызвать NullPointerException, IndexOutOfBoundsException и NetworkOnMainThreadException. Сохранить реальные stack trace из JS_LAB и значения переменных. Если сеть/система не вызвала третий тип, не выдавать демонстрацию за подтверждённый результат. Исправление: null-check, проверка границ, OfferUseCase выполняет HTTP через Dispatchers.IO. Debug-исключения обработаны, чтобы лаборатория оставалась доступной.

## 4. Inspectors
Layout Inspector: Debug Lab, Layout defect (тексты цены/рассрочки перекрываются), Layout fixed (раздельная позиция), снимки до/после. App Inspection → Database Inspector → jewelry.db → offer_cache. Сначала открыть товар → «Демо-наличие · API и Room» → Обновить. SQL: `SELECT productId, stock, updatedAt FROM offer_cache;`. Network Inspector: тот же запрос OkHttp `GET https://dummyjson.com/products/{id}`; показать request, response, код и длительность. API учебный, значение не реальный остаток магазина. Офлайн: показать возврат кэша или сообщение об отсутствии кэша.

## 5. Unit JUnit
`ShopRulesTest`: 16 методов, `InstallmentParameterizedTest`: 8 входов. Arrange/Act/Assert, граничные значения, нулевые/отрицательные значения, переполнение суммы, расчёты рассрочки. Запуск `:app:testDebugUnitTest`. Отчёт `app/build/reports/tests/testDebugUnitTest/index.html`. Зафиксировать фактическое число успешных/ошибочных/пропущенных.

## 6. Моки и изоляция
`OfferRepositoryTest` мокает API и DAO, использует when/thenReturn/thenThrow, verify, never. `OfferUseCaseTest` подменяет репозиторий, тестирует Flow через runTest/StandardTestDispatcher; FakeDao хранит записи в памяти. Mock — проверяет взаимодействия, stub — заранее заданный ответ, fake — упрощённая рабочая реализация. Снимки классов тестов и реального отчёта.

## 7. Robolectric
`AndroidContextTest`: SharedPreferences корзины, ресурсы, Intent. SDK 33 без эмулятора. Запустить `./gradlew :app:testDebugUnitTest --tests '*AndroidContextTest'` и затем instrumented, сравнить времена по отчётам; указать разное количество тестов и не считать прямое сравнение чистым бенчмарком. Maven resolver использует repo.maven.apache.org.

## 8. Room и MockWebServer
RoomTest — in-memory CRUD, отдельный migration test создаёт реальную SQLite v1 таблицу, запускает Room v2 миграцию, проверяет сохранность и DEFAULT 0. Схема v1 демонстрационная (в оригинальном магазине Room не было), не имитировать миграцию существующей БД заказов. `HttpOfferApiTest`: 200 и путь запроса, 404, 500, timeout, malformed JSON. Запустить оба набора, приложить отчёты.

## 9. Espresso
ShopUiTest: добавление товара → корзина → форма → результат на TrackingActivity; короткое имя; explicit Intent в корзину (stub Intent только в отдельном тесте навигации). `onView`, `perform`, `check`, `Intents`, `IdlingResource` на OperationTracker. Реальный сценарий сохранения заказа выполняется без заглушки. Снимки формы/результата и отчёта.

## 10. Page Object / стабильность
`ShopPage`: селекторы с ID и уникальным тегом add_0, scrollTo перед действиями вне видимой области. Test Orchestrator, clearPackageData, animationsDisabled; операции checkout отслеживаются IdlingResource, нет sleep. Повторить 3 независимых прогона `./gradlew :app:connectedDebugAndroidTest --rerun-tasks`. Записать время, результат и причины выявленных flaky тестов, если они были. Не выдавать предполагаемую стабильность за результат.

## 11. Ручное тестирование
См. TEST_PLAN.md. Заполнить реальную матрицу: API 24/30/34/35 или доступные, компактный/большой экран, portrait/landscape, light/dark, русский/другой системный язык, онлайн/офлайн. Исходный дизайн фиксирован в светлой гамме и русском тексте: проверить читаемость/отсутствие сбоя, отметить ограничения локализации и тёмной темы. Свернуть/развернуть; `adb shell am force-stop com.example.jewelryshop` затем открыть; проверить сохранение корзины/заказов. Monkey на отдельной установке, может создавать демонстрационные заказы:

```bash
mkdir -p evidence/rgr3
adb shell monkey -p com.example.jewelryshop --throttle 200 -v 1000 > evidence/rgr3/monkey.txt 2>&1
```

Записать Events injected, ANR, CRASH, оформить реальные дефекты.

## 12. JaCoCo ≥70%
`rgr3CoverageVerify` требует 70% LINE бизнес-слоя `domain.ShopRules`. HTML `app/build/reports/jacoco/rgr3/index.html`; объединённый отчёт после Android-тестов `app/build/reports/jacoco/rgr3-combined/index.html`. Проверка не отключена; отсутствующие coverage-файлы блокируют отчёт. Высокое покрытие не гарантирует отсутствия дефектов: выполненная строка не равна проверке правильности всех условий и взаимодействий.

## 13. Profiler / LeakCanary / StrictMode
Android Studio Profiler → CPU, Memory/heap dump, Network, Energy/Power (если доступно на выбранном устройстве). На исходной версии и обновлённой сравнить запуск, прокрутку каталога, оформление заказа (одинаковый эмулятор, объём данных). Изменения: устранён двойной renderList при старте; синхронный commit заказа вынесен в поток; сеть/Room выполняются через Dispatchers.IO. Legacy SharedPreferences всё ещё используются, проверить StrictMode, устранить обнаруженные дополнительные UI-дисковые нарушения после реального прогона. Debug Lab: создать утечку Activity → экран закрывается, дождаться отчёта LeakCanary; открыть лабораторию и нажать «Исправить утечку», повторить без удержания Activity, проверить heap dump. Сравнить запись на UI и в фоне; сохранить StrictMode Logcat. Намеренное удержание только debug. Зафиксировать до/после CPU, heap, retained Activity, network, energy, если измеримы. Не выдумывать цифры.

## 14. Macrobenchmark / Firebase
Отдельный `:benchmark`, non-debuggable target, profileable, 5 итераций Cold StartupTimingMetric и FrameTimingMetric при прокрутке настоящего XML ScrollView `catalogScroll`. Запуск на реальном Android 28+ (лучше API 34), `./gradlew :benchmark:connectedBenchmarkAndroidTest`; отчёт `benchmark/build/outputs/`. На эмуляторе метрики ориентировочные. Firebase: manual workflow `.github/workflows/firebase-test-lab.yml`; два разных поддерживаемых устройства, выбрать через `gcloud firebase test android models list`, добавить GCP_PROJECT_ID и GCP_SA_KEY как secrets репозитория, проверить права и стоимость, выполнить workflow. Сохранить отчёт, видео и логи. Пока не запущено — NOT RUN.

## 15. CI и итоговый отчёт
GitHub Actions Android: pull_request и push master/main. Lint, Detekt, JUnit, JaCoCo gate, build и эмулятор API 34 с Espresso/Room; публикация HTML/XML/APK и coverage даже при сбоях. Android SDK setup v4.0.4, chmod gradlew в обоих jobs. Сохранить скриншот зелёного run только после реального успеха; скачать artifacts. Заполнить QUALITY_REPORT.md: цель, описание, все задания, код, доказательства, дефекты, приоритет, статус, метрики, ссылка на репозиторий и выводы. Не считать созданный исходный код доказательством выполнения ручных измерений.

## Контрольные вопросы
1. Unit — изолированная функция/класс, JVM, без устройства. Instrumented — Android runtime, устройство/эмулятор. Интеграционные — совместная работа нескольких компонентов (DAO, API), UI — путь пользователя через интерфейс.
2. Пирамида: много быстрых unit, меньше интеграционных, ещё меньше UI; быстрее обратная связь и меньше хрупкости.
3. Mock проверяет взаимодействия, stub задаёт ответы, fake выполняет упрощённую логику. Изоляция делает тест воспроизводимым без внешних зависимостей.
4. IdlingResource сообщает Espresso о завершении асинхронной работы. Flaky-тесты нестабильны из-за гонок, анимаций, сети и общего состояния.
5. Статическая ссылка на Activity, неотменённые callbacks/корутины, незакрытые ресурсы; LeakCanary, heap dump, dominator/retained objects.
6. ANR — приложение не отвечает на события; диагностировать main thread, stack traces, Logcat, system trace и тяжёлые I/O/блокировки.
7. Тестирование обнаруживает несоответствия ожиданиям, отладка локализует причины и помогает исправить их.
