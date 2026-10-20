# Примеры запросов

Готовые тела запросов для `POST /v1/generator`. Каждый файл — самостоятельный запрос: его можно отправить как есть и посмотреть результат.

Отдельно лежат [схемы](#схемы) для `POST /v1/schemas/convert` — по ним сервис строит такие запросы сам.

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
| [okved.json](algorithm/okved.json) | ОКВЭД | Случайные, уникальные, с `null` |
| [oktmo.json](algorithm/oktmo.json) | ОКТМО | То же |
| [okato.json](algorithm/okato.json) | ОКАТО | То же |
| [kbk.json](algorithm/kbk.json) | КБК | То же |
| [oms.json](algorithm/oms.json) | Полис ОМС | С корректной контрольной цифрой, уникальные, с `null`, без проверки |
| [sts.json](algorithm/sts.json) | СТС | Случайные, уникальные, с `null` |
| [pts.json](algorithm/pts.json) | ПТС | То же |
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
| [dictionary.json](strategies/dictionary.json) | Выбор из словаря: случайно, с весами (значение с весом 0 не встречается), по порядку, без повторов, с `null`, из нескольких словарей |
| [regexp.json](strategies/regexp.json) | Строки по регулярным выражениям: номер заказа, телефон, автомобильный номер, несколько выражений в одной колонке |
| [faker.json](strategies/faker.json) | Имена, email, телефоны и адреса на русском и английском, несколько топиков в одной колонке, текст заданной длины, координаты |
| [faker_topics.json](strategies/faker_topics.json) | Все топики стратегии `faker`, по одной колонке на топик: имена, адреса, телефоны, интернет, банковские реквизиты, штрихкоды, автомобили, паспорта, текст и координаты |
| [expression.json](strategies/expression.json) | Колонки, вычисляемые по другим колонкам строки: сумма по цене и количеству, скидка по условию, email по имени и фамилии с конвертерами, пропуск и обработка `null`, дата окончания по дате начала, ссылка на колонку с пробелом в имени |
| [expression_functions.json](strategies/expression_functions.json) | Остальные функции и операторы выражений: `lower`, `upper`, `length`, `min`, `max`, `abs`, `pow`, `toStr`, `plusYears`, `daysBetween`, остаток от деления, `between`, `!`, `or` и замена `null` через `?:` |

## Конвертеры

Папка [`converters`](converters). В большинстве примеров есть колонка `source` с исходными значениями, чтобы результат было с чем сравнить.

| Файл | Что показано |
|---|---|
| [int.json](converters/int.json) | Арифметика над целыми числами и цепочка из двух конвертеров |
| [double.json](converters/double.json) | Арифметика, округление, логарифм, синус, нормализация значений колонки |
| [str.json](converters/str.json) | Все строковые конвертеры: регистр, пробелы, дополнение до длины, подстрока и обрезка, замена, маскирование, транслитерация, Base64, MD5, случайная замена символов |
| [common.json](converters/common.json) | Перемешивание, сортировка, замена на `null`, применение конвертера к части строк через `probability` |
| [date_format.json](converters/date_format.json) | Форматирование дат, даты и времени, даты и времени с часовым поясом |
| [post_converters.json](converters/post_converters.json) | Строковые конвертеры для нестроковых типов через `convertToString` и `postConverters`: номер с ведущими нулями и префиксом, валюта, маскирование ИНН |

## Параметры запроса

Папки [`options`](options) и [`output`](output).

| Файл | Что показано |
|---|---|
| [options/seed.json](options/seed.json) | Воспроизводимая генерация: колонки с `seed` дают одинаковые значения при каждом запуске, колонка без `seed` — разные |
| [options/request_seed.json](options/request_seed.json) | Seed всего запроса: каждый запуск даёт тот же файл, колонки с одинаковыми настройками получают разные значения, а колонка со своим `seed` от seed запроса не зависит |
| [options/distribution.json](options/distribution.json) | Распределения случайных значений: нормальное для возраста и времени визита, экспоненциальное для суммы и для дат с преобладанием свежих, Ципфа для идентификатора клиента и оценки. Для сравнения — колонка с равномерным распределением |
| [options/error_policy.json](options/error_policy.json) | Политика `IGNORE`: колонка, которую невозможно сгенерировать (100 уникальных строк из одного символа), заполняется значением `defaultValue` |
| [options/weights.json](options/weights.json) | Веса значений словаря: доли в процентах, вес у одного значения (у остальных — 1), исключение значения нулевым весом, дробные веса, значение из двух словарей, веса в колонке с `null` |
| [options/nulls.json](options/nulls.json) | Как получить нужную долю `null`: половина при `notNull: false`, 10 % и 80 % через конвертер `to_null`, пропуск и обработка `null` в выражениях, замена `null` значением по умолчанию, не больше одного `null` в колонке с `unique` |
| [options/unique.json](options/unique.json) | Значения без повторов: целые числа из узкого диапазона, даты, время, логины, промокоды из словаря, ИНН и IMEI, последовательность десятичных чисел с шагом |
| [output/sql.json](output/sql.json) | Выгрузка в SQL: имя таблицы, кавычки для строк, экранирование апострофа, `NULL` |
| [output/sql_postgresql.json](output/sql_postgresql.json), [sql_mysql.json](output/sql_mysql.json), [sql_mssql.json](output/sql_mssql.json), [sql_oracle.json](output/sql_oracle.json), [sql_sqlite.json](output/sql_sqlite.json) | Тот же запрос в диалектах PostgreSQL, MySQL, SQL Server, Oracle и SQLite: имена в кавычках диалекта, имя таблицы со схемой, запись логических значений и строк, отдельный `INSERT` на строку для Oracle |
| [output/xlsx.json](output/xlsx.json) | Выгрузка в XLSX |
| [output/sql_reserved_words.json](output/sql_reserved_words.json) | Зачем нужен диалект: таблица `user` и колонки `order`, `group`, `select`, имя с пробелом и регистром, строки с апострофом и обратной косой чертой: в диалекте PostgreSQL имена заключаются в кавычки, и ключевые слова не ломают оператор |

## Таблицы целиком

Папка [`tables`](tables) — приближённые к реальным таблицы, в которых стратегии и типы используются вместе.

| Файл | Что показано |
|---|---|
| [users.json](tables/users.json) | Пользователи: идентификаторы, ФИО, email, телефон, СНИЛС, статус, баланс, дата регистрации. Выгрузка в SQL |
| [organizations.json](tables/organizations.json) | Организации: название, ИНН, КПП, ОГРН, ОКПО, БИК и расчётный счёт с ключом под этот БИК. Выгрузка в SQL |
| [payments.json](tables/payments.json) | Платежи: номер карты, сумма, валюта, статус, причина отказа, время с часовым поясом |
| [orders.json](tables/orders.json) | Заказы: номер заказа по идентификатору, скидка по количеству, сумма по цене, количеству и скидке, дата доставки по дате создания, статус по сроку доставки. Выгрузка в SQL |
| [employees.json](tables/employees.json) | Сотрудники: табельный номер с ведущими нулями, email по имени и фамилии, паспорт, ИНН и СНИЛС, должность и оклад по грейду, конец испытательного срока по дате приёма, дата увольнения у части строк. Выгрузка в SQL |
| [customers.json](tables/customers.json) | Клиенты с данными на английском: имя, адрес и телефон, IBAN и BIC, маскирование email, паспорта и номера карты, хеш пароля, кредитный лимит по сегменту, часовой пояс |
| [products.json](tables/products.json) | Товары: артикул, штрихкод EAN-13, название по категории, цена и цена со скидкой, остаток с распределением Ципфа и признак наличия. Выгрузка в SQL |
| [vehicles.json](tables/vehicles.json) | Автомобили: VIN, госномер, СТС и ПТС, возраст и пробег по году выпуска, признак техосмотра, владелец |
| [patients.json](tables/patients.json) | Пациенты: номер карты, возраст и возрастная группа по дате рождения, полис ОМС, СНИЛС, группа крови, дата следующего осмотра по возрасту, температура с нормальным распределением |
| [tax_payments.json](tables/tax_payments.json) | Налоговые платежи: ИНН и КПП плательщика, ОКВЭД, КБК, ОКТМО и ОКАТО, налоговый период, дата оплаты и пени по дням просрочки |
| [sensor_readings.json](tables/sensor_readings.json) | Показания датчика — временной ряд: время с шагом в час, накопительный счётчик по возрастанию, заряд батареи по убыванию, температура, пропуски сигнала, статус по показаниям |
| [access_logs.json](tables/access_logs.json) | Журнал HTTP-запросов: время по возрастанию с неровными промежутками, IP, метод, путь, код ответа и уровень записи по нему, длительность с экспоненциальным распределением, идентификатор пользователя по Ципфу |
| [bank_transactions.json](tables/bank_transactions.json) | Банковские операции: время по возрастанию, расчётный счёт с ключом под БИК, замаскированный номер карты, тип, валюта и статус с весами, сумма с экспоненциальным распределением, комиссия только за переводы, признак проверки по сумме и валюте |
| [hotel_bookings.json](tables/hotel_bookings.json) | Бронирования отеля: категория номера с весами и цена по категории, заезд с пиком летом, число ночей по Ципфу, дата выезда и стоимость по ним, сезон по месяцу заезда. Выгрузка в SQL для PostgreSQL |
| [flights.json](tables/flights.json) | Авиарейсы: маршрут с весами и аэропорты вылета и прилёта, извлечённые из него, самолёт и число мест по типу, загрузка с нормальным распределением, пассажиры по загрузке, задержка и статус по ней |
| [support_tickets.json](tables/support_tickets.json) | Обращения в поддержку: номер с префиксом, канал и приоритет с весами, срок ответа по приоритету и признак его нарушения, исполнитель не у всех обращений, оценка от клиента не у всех и чаще высокая |
| [exam_results.json](tables/exam_results.json) | Результаты экзаменов: группа и предмет, номер попытки с весами, оценка с нормальным распределением, оценка словами, сдал ли и положена ли стипендия. Выгрузка в XLSX |
| [insurance_policies.json](tables/insurance_policies.json) | Полисы ОСАГО: номер полиса, VIN и госномер, базовый тариф и коэффициенты с весами, премия по ним, срок действия ровно год. Выгрузка в SQL для SQL Server |
| [subscriptions.json](tables/subscriptions.json) | Подписки на сервис: тариф и способ оплаты с весами, цена по тарифу со скидкой за год, дата начала с преобладанием свежих, срок подписки и дата продления по ней, выручка за всё время. Задан `seed` запроса — файл при каждом запуске один и тот же |

## Схемы

Папка [`schemas`](schemas) — файлы миграций для `POST /v1/schemas/convert`. В ответ на каждый сервис возвращает запросы на генерацию, по одному на таблицу, и предупреждения о том, что в схеме учтено неточно.

bash:

```bash
curl -X POST "http://localhost:8103/v1/schemas/convert?size=50" -F "file=@samples/schemas/V1__shop.sql"
```

PowerShell:

```powershell
curl.exe -X POST "http://localhost:8103/v1/schemas/convert?size=50" -F "file=@samples/schemas/V1__shop.sql"
```

Параметр `size` задаёт количество строк в запросах на генерацию, `fileType` — формат файла (по умолчанию `sql`). Любой элемент массива `requests` из ответа можно отправить в `POST /v1/generator` как есть.

| Файл | Формат | Что показано |
|---|---|---|
| [V1__shop.sql](schemas/V1__shop.sql) | SQL-скрипт (PostgreSQL) | Три таблицы: первичные и уникальные ключи, `serial` и `IDENTITY`, внешние ключи, составной ключ, перечисление, `jsonb`, вычисляемая колонка, изменения через `ALTER TABLE` и `CREATE UNIQUE INDEX` |
| [V2__blog_mysql.sql](schemas/V2__blog_mysql.sql) | SQL-скрипт (MySQL) | Три таблицы: имена в обратных апострофах, `AUTO_INCREMENT`, `UNSIGNED`, `enum(...)`, `tinyint(1)`, ключи и индексы внутри `CREATE TABLE`, изменения через `ALTER TABLE` |
| [V3__warehouse_mssql.sql](schemas/V3__warehouse_mssql.sql) | SQL-скрипт (SQL Server) | Две таблицы: имена в квадратных скобках со схемой `dbo`, `IDENTITY`, типы `uniqueidentifier`, `bit`, `money` и `datetimeoffset`, изменения через `ALTER TABLE` и `CREATE UNIQUE INDEX` |
| [changelog.xml](schemas/changelog.xml) | Журнал изменений Liquibase | Две таблицы: `autoIncrement`, ограничения в `constraints`, внешний ключ, изменения через `addColumn`, `addNotNullConstraint`, `addUniqueConstraint` и `dropColumn` |
