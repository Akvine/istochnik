-- Миграция для SQL Server: имена в квадратных скобках, IDENTITY, типы uniqueidentifier, bit, money и datetimeoffset.
-- По ней POST /v1/schemas/convert возвращает два запроса на генерацию.
-- Операторы разделены точкой с запятой: разделитель пакетов GO сервис не понимает.

CREATE TABLE [dbo].[warehouses] (
    [id]         INT IDENTITY(1, 1) NOT NULL,
    [code]       NCHAR(6)           NOT NULL,
    [title]      NVARCHAR(100)      NOT NULL,
    [is_active]  BIT                NOT NULL DEFAULT 1,
    [opened_on]  DATE               NOT NULL,
    CONSTRAINT [pk_warehouses] PRIMARY KEY ([id]),
    CONSTRAINT [uk_warehouses_code] UNIQUE ([code])
);

CREATE TABLE [dbo].[stock_items] (
    [id]           BIGINT IDENTITY(1, 1) NOT NULL PRIMARY KEY,
    [row_guid]     UNIQUEIDENTIFIER   NOT NULL,
    [warehouse_id] INT                NOT NULL,
    [sku]          VARCHAR(20)        NOT NULL,
    [quantity]     DECIMAL(12, 3)     NOT NULL,
    [unit_price]   MONEY              NOT NULL,
    [weight_kg]    REAL,
    [shelf]        TINYINT,
    [comment]      NVARCHAR(MAX),
    [updated_at]   DATETIMEOFFSET     NOT NULL,
    CONSTRAINT [fk_stock_items_warehouses] FOREIGN KEY ([warehouse_id]) REFERENCES [dbo].[warehouses] ([id])
);

-- Изменения после создания таблиц тоже учитываются
ALTER TABLE [dbo].[stock_items] ADD [batch_number] VARCHAR(30) NULL;
CREATE UNIQUE INDEX [ux_stock_items_row_guid] ON [dbo].[stock_items] ([row_guid]);
