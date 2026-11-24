# Шаблон отчёта о качестве

Репозиторий: https://github.com/Djhdjd/JewelryShop. Версия/commit: заполнить. Дата, устройство, API, SDK/JDK: заполнить.

## Реестр дефектов (анализ кода, подтверждение запуском необходимо)
| ID | Дефект/наблюдение | Приоритет | Изменение | Статус |
|---|---|---|---|---|
| D01 | Целочисленный floor вместо верхнего платежа (намеренно в Debug Lab) | P1 | ShopRules.monthlyPayment | исправленный вариант реализован; измерить |
| D02 | Перекрытие текста (намеренная демонстрация) | P1 | раздельное расположение | демонстрация подготовлена |
| D03 | Удержание Activity статическим полем (намеренно в debug) | P0 | очистка ссылки | подтвердить LeakCanary |
| D04 | Синхронный commit заказа на UI | P1 | background thread, IdlingResource | исправление реализовано; проверить |
| D05 | Двойная отрисовка каталога при старте | P1 | удалён повторный render | измерить |
| D06 | Отсутствие сетевой интеграции и Room в исходнике | P2 | учебное API/Room cache | проверить |

## Метрики (фактические, не заполнены)
| Метрика | До | После | Источник |
|---|---|---|---|
| JUnit total/pass/fail | | | HTML/XML |
| Instrumented total/pass/fail | | | HTML/XML |
| JaCoCo line/branch | | | HTML/XML |
| Lint errors/warnings / Detekt | | | HTML |
| CPU main thread | | | Profiler |
| Heap / retained Activity | | | heap/LeakCanary |
| Energy / Network | | | Profiler |
| Cold start p50 / p90 | | | Macrobenchmark |
| Frame timing | | | Macrobenchmark |
| Monkey / ANR / crash | | | log |
| Firebase devices | | | console report/video |
| CI run URL | | | Actions |

Для заданий 1–15: цель, инструмент, шаги, фрагмент кода, результат и скриншот. Не указывать результаты до выполнения. Рекомендации: отслеживать предупреждения Lint, не обновлять зависимости без тестов, уменьшить legacy SharedPreferences I/O, расширить сценарии, улучшить локализацию и тему. Итоговые выводы заполнить после реальных прогонов.
