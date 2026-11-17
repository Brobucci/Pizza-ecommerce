-- =====================================================================
-- V1__init.sql — Schema iniziale del database pizzadb
-- Percorso nel progetto: backend/src/main/resources/db/migration/
--
-- Niente CREATE SCHEMA qui: Flyway lavora dentro il database indicato
-- in spring.datasource.url, che deve già esistere (vuoto).
--
-- Rispetto al vecchio pizzadb.sql (vedi claude/SCHEMA.md):
--   * category.name UNIQUE
--   * order_item.orders_id -> order_id (nome uniforme per tutte le FK verso orders)
--   * orders: estimated_delivery_time sostituito da promised_from / promised_until,
--     aggiunti delivery_zone_id, stati READY e CANCELLED
--   * nuove tabelle: delivery_zone, delivery_run, payment, order_status_history
--   * order_delivery_queue agganciata al giro (delivery_run_id) invece che a uno slot
--   * system_setting: impostazioni dei giri e del pagamento, rimosso avg_delivery_minutes
-- =====================================================================


-- ============ CATALOGO ============

CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL,
    description TEXT,
    is_configurable BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,      -- soft delete: tolto dal menu, ma resta per gli ordini storici
    is_available BOOLEAN DEFAULT TRUE,   -- disponibilità del giorno: in menu ma esaurito oggi
    finishing_minutes DECIMAL(4, 1) NOT NULL DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE ingredient (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    extra_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    contains_lactose BOOLEAN DEFAULT FALSE,
    is_removable_with_discount BOOLEAN DEFAULT FALSE,
    is_available BOOLEAN DEFAULT TRUE
);

CREATE TABLE product_ingredient (
    product_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    standard_quantity INT DEFAULT 1,
    PRIMARY KEY (product_id, ingredient_id),
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredient(id) ON DELETE CASCADE
);


-- ============ PREZZI PER TAGLIA ============

CREATE TABLE category_size_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    size ENUM('MINI', 'NORMAL', 'MAXI') NOT NULL,
    adjustment_type ENUM('DELTA', 'MULTIPLIER') NOT NULL,
    value DECIMAL(10, 2) NOT NULL,
    UNIQUE KEY uq_category_size (category_id, size),
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE
);


-- ============ UTENTI ============

CREATE TABLE app_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    role ENUM('CUSTOMER', 'STAFF', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    is_enabled BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE customer_address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    label VARCHAR(50),
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(10),
    notes VARCHAR(255),
    is_default BOOLEAN DEFAULT FALSE,    -- un solo default per cliente: regola applicativa
    FOREIGN KEY (customer_id) REFERENCES app_user(id) ON DELETE CASCADE
);


-- ============ ZONE DI CONSEGNA ============
-- Prima di orders, perché orders la referenzia.

CREATE TABLE delivery_zone (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    travel_minutes DECIMAL(4, 1) NOT NULL,   -- percorrenza stimata pizzeria -> zona
    is_active BOOLEAN DEFAULT TRUE           -- soft delete, come per product
);


-- ============ ORDINI ============

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NULL,                 -- NULL = ordine guest
    customer_name VARCHAR(100) NOT NULL,     -- snapshot, anche per utenti registrati
    phone_number VARCHAR(20),
    contact_email VARCHAR(150),
    address VARCHAR(255),
    delivery_zone_id BIGINT NULL,            -- valorizzato solo per DELIVERY
    delivery_notes VARCHAR(255),
    order_type ENUM('DELIVERY', 'TAKEOUT') NOT NULL,
    status ENUM('PENDING', 'PREPARING', 'READY', 'SHIPPED', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    requested_time DATETIME NOT NULL,
    promised_from DATETIME NULL,             -- range promesso al cliente, congelato (snapshot):
    promised_until DATETIME NULL,            -- ritardo = consegna/ritiro oltre promised_until
    total_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    delivery_fee DECIMAL(10, 2) DEFAULT 0.00,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES app_user(id),
    FOREIGN KEY (delivery_zone_id) REFERENCES delivery_zone(id)
);

CREATE TABLE order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    quantity INT DEFAULT 1,
    size ENUM('MINI', 'NORMAL', 'MAXI') DEFAULT 'NORMAL',
    is_lactose_free_base BOOLEAN DEFAULT FALSE,
    unit_price DECIMAL(10, 2) NOT NULL,      -- snapshot del prezzo
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE TABLE order_item_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_item_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    fraction DECIMAL(3, 2) DEFAULT 1.00,
    FOREIGN KEY (order_item_id) REFERENCES order_item(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE config_ingredient (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    final_quantity INT NOT NULL,
    FOREIGN KEY (config_id) REFERENCES order_item_config(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredient(id)
);

-- Storico dei cambi di stato: una riga per ogni transizione.
CREATE TABLE order_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    from_status ENUM('PENDING', 'PREPARING', 'READY', 'SHIPPED', 'COMPLETED', 'CANCELLED') NULL,  -- NULL alla creazione
    to_status ENUM('PENDING', 'PREPARING', 'READY', 'SHIPPED', 'COMPLETED', 'CANCELLED') NOT NULL,
    changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    changed_by BIGINT NULL,                  -- utente staff/admin; NULL = cambio fatto dal sistema
    note VARCHAR(255),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES app_user(id)
);


-- ============ PAGAMENTI ============
-- Una riga per ogni TENTATIVO di pagamento (un pagamento online fallito e poi
-- riprovato produce due righe). I dati della carta non passano MAI da qui:
-- li gestisce il provider, noi salviamo solo il suo identificativo.

CREATE TABLE payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    method ENUM('CASH', 'CARD_ON_DELIVERY', 'ONLINE') NOT NULL,
    status ENUM('PENDING', 'PAID', 'FAILED', 'EXPIRED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    amount DECIMAL(10, 2) NOT NULL,          -- snapshot dell'importo al momento del tentativo
    provider VARCHAR(30) NULL,               -- es. 'STRIPE'; NULL per contanti e POS
    provider_reference VARCHAR(100) NULL UNIQUE,  -- id del pagamento presso il provider
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    paid_at DATETIME NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);


-- ============ CODA DI PRODUZIONE ============

CREATE TABLE station (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,        -- DOUGH, FRYER, RIDER
    name VARCHAR(50) NOT NULL
);

-- Regola ricorrente. Per RIDER, capacity_per_slot = numero di rider attivi nella fascia.
CREATE TABLE capacity_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_id BIGINT NOT NULL,
    day_of_week TINYINT NOT NULL,
    time_from TIME NOT NULL,
    time_to TIME NOT NULL,
    capacity_per_slot DECIMAL(6, 2) NOT NULL,
    note VARCHAR(100),
    FOREIGN KEY (station_id) REFERENCES station(id)
);

-- Snapshot generato dal profilo, blocchi da 15 minuti. Non generato per RIDER.
CREATE TABLE production_slot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_id BIGINT NOT NULL,
    slot_start DATETIME NOT NULL,
    slot_end DATETIME NOT NULL,
    capacity_units DECIMAL(6, 2) NOT NULL,   -- snapshot da capacity_profile
    current_load DECIMAL(6, 2) NOT NULL DEFAULT 0,
    UNIQUE KEY uq_station_slot (station_id, slot_start),
    FOREIGN KEY (station_id) REFERENCES station(id)
);

CREATE TABLE station_size_load (
    station_id BIGINT NOT NULL,
    size ENUM('MINI', 'NORMAL', 'MAXI') NOT NULL,
    load_units DECIMAL(4, 2) NOT NULL,
    PRIMARY KEY (station_id, size),
    FOREIGN KEY (station_id) REFERENCES station(id)
);

CREATE TABLE product_stage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    station_id BIGINT NOT NULL,
    sequence_order TINYINT NOT NULL,
    fixed_minutes DECIMAL(4, 1) NULL,
    UNIQUE KEY uq_product_sequence (product_id, sequence_order),
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    FOREIGN KEY (station_id) REFERENCES station(id)
);

CREATE TABLE order_item_stage_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_item_id BIGINT NOT NULL,
    station_id BIGINT NOT NULL,
    sequence_order TINYINT NOT NULL,
    slot_id BIGINT NOT NULL,
    load_units DECIMAL(4, 2) NOT NULL,       -- snapshot da station_size_load
    stage_start DATETIME NOT NULL,
    stage_end DATETIME NOT NULL,
    UNIQUE KEY uq_item_sequence_queue (order_item_id, sequence_order),
    FOREIGN KEY (order_item_id) REFERENCES order_item(id) ON DELETE CASCADE,
    FOREIGN KEY (station_id) REFERENCES station(id),
    FOREIGN KEY (slot_id) REFERENCES production_slot(id)
);


-- ============ CONSEGNE ============

-- Il giro: unità di lavoro del rider. Campi previsti (dallo scheduler) e reali (dallo staff).
CREATE TABLE delivery_run (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estimated_departure_at DATETIME NOT NULL,
    estimated_return_at DATETIME NOT NULL,
    departed_at DATETIME NULL,
    returned_at DATETIME NULL,
    current_load DECIMAL(4, 2) NOT NULL DEFAULT 0,   -- somma dei load_units degli ordini del giro
    status ENUM('PLANNED', 'DEPARTED', 'RETURNED') NOT NULL DEFAULT 'PLANNED'
);

CREATE TABLE order_delivery_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    delivery_run_id BIGINT NOT NULL,
    stop_sequence TINYINT NOT NULL,          -- posizione della consegna nel giro
    load_units DECIMAL(4, 2) NOT NULL DEFAULT 1.00,  -- peso dell'ordine nel giro (piccolo = 1)
    kitchen_ready_at DATETIME NOT NULL,      -- massimo tra gli stage_end degli item dell'ordine
    estimated_delivered_at DATETIME NOT NULL,
    delivered_at DATETIME NULL,              -- reale, registrato dallo staff
    UNIQUE KEY uq_run_stop (delivery_run_id, stop_sequence),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (delivery_run_id) REFERENCES delivery_run(id)
);


-- ============ CONFIGURAZIONE ============

CREATE TABLE system_setting (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value VARCHAR(50) NOT NULL
);

-- Valori iniziali indicativi: vanno tarati sulla pizzeria reale.
INSERT INTO system_setting (setting_key, setting_value) VALUES
('error_buffer_percent', '5'),
('display_range_minutes', '10'),
('run_normal_max_units', '5'),      -- giro "pieno"
('run_hard_max_units', '6'),        -- superabile solo per evitare un ritardo
('run_max_wait_minutes', '5'),      -- quanto il giro può aspettare un ordine in arrivo
('minutes_per_stop', '5'),          -- tempo per ogni consegna oltre la percorrenza
('payment_hold_minutes', '10');     -- per quanto un ordine online non pagato tiene il suo posto in coda
