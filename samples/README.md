# Примеры запросов

Готовые тела запросов для `POST /v1/generator`. Каждый файл — самостоятельный запрос: его можно отправить как есть и посмотреть результат.

## Как запустить

Сервис должен быть запущен (см. [README](../README.md#сборка-и-запуск)). Команды выполняются из корня репозитория.

bash:

```bash
curl -X POST "http://localhost:8103/v1/generator" -H "Content-Type: application/json" --data-binary @samples/algorithm/int.json
```

PowerShell:

```powershell
curl.exe -X POST "http://localhost:8103/v1/generator" -H "Content-Type: application/json" --data-binary "@samples/algorithm/int.json"
```

Чтобы сохранить результат в файл, добавьте `-o result.csv`. Для примеров с `"fileType": "xlsx"` это обязательно: `-o result.xlsx`.

В колонках примеров имя говорит о том, что показывает колонка: `random` — случайные значения, `shift` — последовательность, `unique` — без повторов, `nullable` — с пропусками (`null`), `valid` — с корректным контрольным разрядом.

## Алгоритмическая генерация

Папка [`algorithm`](algorithm) — по одному файлу на каждый тип, без конвертеров. Имя файла совпадает с именем типа.

| Файл | Тип | Что показано |
|---|---|---|
| [int.json](algorithm/int.json) | Целые числа | Случайные в диапазоне, уникальные, с `null`, последовательность с шагом, последовательность с правой границей |
| [double.json](algorithm/double.json) | Числа с плавающей точкой | Случайные в диапазоне, уникальные, с `null`, последовательность с шагом |
| [str.json](algorithm/str.json) | Строки | Заданная длина, уникальные, с `null` |
| [bool.json](algorithm/bool.json) | Логические значения | Случайные, с `null`, чередование начиная с `true` или `false` |
| [uuid.json](algorithm/uuid.json) | UUID | Без параметров |
| [date.json](algorithm/date.json) | Даты | Случайные в диапазоне, уникальные, с `null`, шаг в день и в неделю |
| [time.json](algorithm/time.json) | Время | Случайное в диапазоне, с `null`, шаг 30 минут |
| [datetime.json](algorithm/datetime.json) | Дата и время | Случайные в диапазоне, уникальные, с `null`, шаг в час |
| [zoned_datetime.json](algorithm/zoned_datetime.json) | Дата и время с часовым поясом | Зона с названием и со смещением, оба формата `start`, шаг в день, шаг с правой границей |
| [zone_id.json](algorithm/zone_id.json) | Идентификаторы часовых поясов | Случайные, уникальные, с `null`, по алфавиту |
| [decimal.json](algorithm/decimal.json) | Точные десятичные числа | Два и четыре знака после запятой, уникальные, с `null`, последовательность с шагом |
| [snils.json](algorithm/snils.json) | СНИЛС | С корректным контрольным числом, уникальные, с `null`, без проверки |
| [inn_org.json](algorithm/inn_org.json) | ИНН организации | То же |
| [inn_pers.json](algorithm/inn_pers.json) | ИНН физического лица | То же |
| [ogrn.json](algorithm/ogrn.json) | ОГРН | То же |
| [ogrnip.json](algorithm/ogrnip.json) | ОГРНИП | То же |
| [okpo.json](algorithm/okpo.json) | ОКПО | То же |
| [kpp.json](algorithm/kpp.json) | КПП | Случайные, уникальные, с `null` |
| [bik.json](algorithm/bik.json) | БИК | Случайные, уникальные, с `null` |
| [bank_account.json](algorithm/bank_account.json) | Расчётный счёт | Контрольный ключ под БИК из поля `start`, уникальные, с `null`, без проверки ключа |
| [imei.json](algorithm/imei.json) | IMEI | С корректной контрольной цифрой, уникальные, с `null`, без проверки |
| [card_mir.json](algorithm/card_mir.json) | Номер карты «Мир» | То же |
| [card_visa.json](algorithm/card_visa.json) | Номер карты Visa | То же |
| [card_mastercard.json](algorithm/card_mastercard.json) | Номер карты Mastercard | То же |

В примерах `snils`, `inn_org`, `inn_pers` и `ogrn` присутствуют поля `rangeType`, `start` и `end`: их требует валидация, на результат они не влияют.

## Остальные стратегии

Папка [`strategies`](strategies).

| Файл | Что показано |
|---|---|
| [constant.json](strategies/constant.json) | Одинаковое значение во всех строках: строка, целое и дробное число, логическое значение |
| [dictionary.json](strategies/dictionary.json) | Выбор из словаря: случайно, по порядку, без повторов, с `null`, из нескольких словарей |
| [regexp.json](strategies/regexp.json) | Строки по регулярным выражениям: номер заказа, телефон, автомобильный номер, несколько выражений в одной колонке |
| [faker.json](strategies/faker.json) | Имена, email, телефоны и адреса на русском и английском, несколько топиков в одной колонке |
| [expression.json](strategies/expression.json) | Колонки, вычисляемые по другим колонкам строки: сумма по цене и количеству, скидка по условию, email по имени и фамилии с конвертерами, пропуск и обработка `null`, дата окончания по дате начала, ссылка на колонку с пробелом в имени |

## Конвертеры

Папка [`converters`](converters). В большинстве примеров есть колонка `source` с исходными значениями, чтобы результат было с чем сравнить.

| Файл | Что показано |
|---|---|
| [int.json](converters/int.json) | Арифметика над целыми числами и цепочка из двух конвертеров |
| [double.json](converters/double.json) | Арифметика, округление, логарифм, синус, нормализация значений колонки |
| [str.json](converters/str.json) | Все строковые конвертеры: регистр, пробелы, подстрока, замена, транслитерация, Base64, MD5, случайная замена символов |
| [common.json](converters/common.json) | Перемешивание, сортировка, замена на `null`, применение конвертера к части строк через `probability` |
| [date_format.json](converters/date_format.json) | Форматирование дат, даты и времени, даты и времени с часовым поясом |
| [post_converters.json](converters/post_converters.json) | Строковые конвертеры для нестроковых типов через `convertToString` и `postConverters`: префикс к числу, валюта, маскирование ИНН |

## Параметры запроса

Папки [`options`](options) и [`output`](output).

| Файл | Что показано |
|---|---|
| [options/seed.json](options/seed.json) | Воспроизводимая генерация: колонки с `seed` дают одинаковые значения при каждом запуске, колонка без `seed` — разные |
| [options/error_policy.json](options/error_policy.json) | Политика `IGNORE`: колонка, которую невозможно сгенерировать (100 уникальных строк из одного символа), заполняется значением `defaultValue` |
| [output/sql.json](output/sql.json) | Выгрузка в SQL: имя таблицы, кавычки для строк, экранирование апострофа, `NULL` |
| [output/xlsx.json](output/xlsx.json) | Выгрузка в XLSX |

## Таблицы целиком

Папка [`tables`](tables) — приближённые к реальным таблицы, в которых стратегии и типы используются вместе.

| Файл | Что показано |
|---|---|
| [users.json](tables/users.json) | Пользователи: идентификаторы, ФИО, email, телефон, СНИЛС, статус, баланс, дата регистрации. Выгрузка в SQL |
| [organizations.json](tables/organizations.json) | Организации: название, ИНН, КПП, ОГРН, ОКПО, БИК и расчётный счёт с ключом под этот БИК. Выгрузка в SQL |
| [payments.json](tables/payments.json) | Платежи: номер карты, сумма, валюта, статус, причина отказа, время с часовым поясом |
| [orders.json](tables/orders.json) | Заказы: номер заказа по идентификатору, скидка по количеству, сумма по цене, количеству и скидке, дата доставки по дате создания, статус по сроку доставки. Выгрузка в SQL |
