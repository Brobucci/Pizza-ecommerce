CREATE SCHEMA pizzadb;

-- Creazione Categorie
CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Creazione Prodotti (Pizze, Bibite, ecc.)
CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL,
    description TEXT,
    is_configurable BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (category_id) REFERENCES category(id)
);

-- Creazione Ingredienti Base e Aggiunte
CREATE TABLE ingredient (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    extra_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    contains_lactose BOOLEAN DEFAULT FALSE,
    is_removable_with_discount BOOLEAN DEFAULT FALSE
);

-- Tabella Ponte: Le "Ricette" dei prodotti
CREATE TABLE product_ingredient (
    product_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    standard_quantity INT DEFAULT 1,
    PRIMARY KEY (product_id, ingredient_id),
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredient(id) ON DELETE CASCADE
);

-- Creazione Testata Ordine
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    address VARCHAR(255),
    order_type ENUM('DELIVERY', 'TAKEOUT') NOT NULL,
    status ENUM('PENDING', 'PREPARING', 'SHIPPED', 'COMPLETED') DEFAULT 'PENDING',
    requested_time DATETIME NOT NULL,
    estimated_delivery_time DATETIME,
    total_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    delivery_fee DECIMAL(10, 2) DEFAULT 0.00
);

-- Singoli elementi nel carrello
CREATE TABLE order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    orders_id BIGINT NOT NULL,
    quantity INT DEFAULT 1,
    size ENUM('MINI', 'NORMAL', 'MAXI') DEFAULT 'NORMAL',
    is_lactose_free_base BOOLEAN DEFAULT FALSE,
    unit_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (orders_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Le parti dell'elemento (Intera o Metà)
CREATE TABLE order_item_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_item_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL, -- es. punta alla Diavola
    fraction DECIMAL(3, 2) DEFAULT 1.00, -- 1.00 per intera, 0.50 per metà
    FOREIGN KEY (order_item_id) REFERENCES order_item(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- Modifiche agli ingredienti per quella specifica parte
CREATE TABLE config_ingredient (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    final_quantity INT NOT NULL, -- 0 rimosso, 1 normale, 2 doppio...
    FOREIGN KEY (config_id) REFERENCES order_item_config(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredient(id)
);

-- Coda di Produzione (Smart Queue)
CREATE TABLE production_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    slot_time DATETIME NOT NULL,
    capacity_units INT NOT NULL,
    current_load INT DEFAULT 0
);

INSERT INTO category (name) VALUES ('Pizze'), ('Bibite');

INSERT INTO ingredient (name, extra_price, contains_lactose, is_removable_with_discount) VALUES
('Salsa di Pomodoro', 0.50, FALSE, FALSE),
('Mozzarella', 1.00, TRUE, FALSE),
('Basilico', 0.00, FALSE, FALSE),
('Salame Piccante', 1.50, FALSE, TRUE);
 
INSERT INTO product (category_id, name, base_price, description, is_configurable) VALUES 
(1, 'Margherita', 6.00, 'La classica pizza napoletana', TRUE),
(1, 'Diavola', 7.50, 'Per chi ama il piccante', TRUE);

INSERT INTO product (category_id, name, base_price, description, is_configurable) VALUES 
(2, 'Coca-Cola 33cl', 2.50, 'In lattina', FALSE); 

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
    is_default BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (customer_id) REFERENCES app_user(id) ON DELETE CASCADE
);

ALTER TABLE orders
    ADD COLUMN customer_id BIGINT NULL,
    ADD COLUMN contact_email VARCHAR(150),
    ADD COLUMN delivery_notes VARCHAR(255),
    ADD FOREIGN KEY (customer_id) REFERENCES app_user(id);

CREATE TABLE category_size_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    size ENUM('MINI', 'NORMAL', 'MAXI') NOT NULL,
    adjustment_type ENUM('DELTA', 'MULTIPLIER') NOT NULL,
    value DECIMAL(10,2) NOT NULL,
    UNIQUE KEY uq_category_size (category_id, size),
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS production_queue;

-- 1. Stazioni di lavoro (risorse con capacità limitata)
CREATE TABLE station (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL
);

INSERT INTO station (code, name) VALUES
('DOUGH', 'Pizzaiolo e forno'),
('FRYER', 'Friggitrice'),
('RIDER', 'Consegna');

-- 2. Profilo di capacità ricorrente per fascia oraria/giorno
CREATE TABLE capacity_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_id BIGINT NOT NULL,
    day_of_week TINYINT NOT NULL, -- 1=lunedì ... 7=domenica
    time_from TIME NOT NULL,
    time_to TIME NOT NULL,
    capacity_per_slot DECIMAL(6,2) NOT NULL, -- in load_units, non pezzi fisici
    note VARCHAR(100),
    FOREIGN KEY (station_id) REFERENCES station(id)
);

-- Esempio sabato sera (day_of_week = 6), 20:00-22:00
INSERT INTO capacity_profile (station_id, day_of_week, time_from, time_to, capacity_per_slot, note) VALUES
(1, 6, '20:00:00', '22:00:00', 8.00, 'DOUGH: ~250 pizze/2h in unità NORMAL-equivalenti, ~31-32 ogni 15 min'),
(3, 6, '20:00:00', '22:00:00', 1.00, 'RIDER: 2 rider, ~30 min/consegna');

-- 3. Slot reali generati giorno per giorno (snapshot immutabile del profilo)
CREATE TABLE production_slot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_id BIGINT NOT NULL,
    slot_start DATETIME NOT NULL,
    slot_end DATETIME NOT NULL,
    capacity_units DECIMAL(6,2) NOT NULL,
    current_load DECIMAL(6,2) NOT NULL DEFAULT 0,
    UNIQUE KEY uq_station_slot (station_id, slot_start),
    FOREIGN KEY (station_id) REFERENCES station(id)
);

-- 4. Peso di ogni taglia sul carico, per stazione
CREATE TABLE station_size_load (
    station_id BIGINT NOT NULL,
    size ENUM('MINI', 'NORMAL', 'MAXI') NOT NULL,
    load_units DECIMAL(4,2) NOT NULL,
    PRIMARY KEY (station_id, size),
    FOREIGN KEY (station_id) REFERENCES station(id)
);

INSERT INTO station_size_load (station_id, size, load_units) VALUES
(1, 'NORMAL', 1.00),  -- DOUGH: 14 per infornata = riferimento
(1, 'MAXI',   3.50),  -- 14/4
(1, 'MINI',   0.70),  -- stima provvisoria, da affinare
(2, 'NORMAL', 0.60),  -- FRYER: segnaposto, da tarare
(2, 'MAXI',   1.00),
(2, 'MINI',   0.35);

-- 5. Tempo fisso "a valle" non in coda (es. condimento banco)
ALTER TABLE product
    ADD COLUMN finishing_minutes DECIMAL(4,1) NOT NULL DEFAULT 0;

-- 6. Sequenza di stazioni per prodotto (fasi di lavorazione)
CREATE TABLE product_stage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    station_id BIGINT NOT NULL,
    sequence_order TINYINT NOT NULL,
    fixed_minutes DECIMAL(4,1) NULL, -- durata fissa; NULL se dipende da station_size_load
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    FOREIGN KEY (station_id) REFERENCES station(id)
);

-- 7. Prenotazione reale di ogni item sulle stazioni che attraversa
CREATE TABLE order_item_stage_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_item_id BIGINT NOT NULL,
    station_id BIGINT NOT NULL,
    sequence_order TINYINT NOT NULL,
    slot_id BIGINT NOT NULL,
    load_units DECIMAL(4,2) NOT NULL,
    stage_start DATETIME NOT NULL,
    stage_end DATETIME NOT NULL,
    FOREIGN KEY (order_item_id) REFERENCES order_item(id) ON DELETE CASCADE,
    FOREIGN KEY (station_id) REFERENCES station(id),
    FOREIGN KEY (slot_id) REFERENCES production_slot(id)
);

-- 8. Coda rider, legata all'ordine intero (non al singolo item)
CREATE TABLE order_delivery_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    slot_id BIGINT NOT NULL,
    load_units DECIMAL(4,2) NOT NULL DEFAULT 1.00,
    kitchen_ready_at DATETIME NOT NULL,
    estimated_delivered_at DATETIME NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (slot_id) REFERENCES production_slot(id)
);

-- 9. Parametri configurabili senza toccare il codice
CREATE TABLE system_setting (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value VARCHAR(50) NOT NULL
);

INSERT INTO system_setting (setting_key, setting_value) VALUES
('error_buffer_percent', '5'),
('display_range_minutes', '10'),
('avg_delivery_minutes', '30');

-- Aggiunto is_active per il soft delete, is_avalaible per la disponibilità dei prodotti
ALTER TABLE product ADD COLUMN is_active BOOLEAN DEFAULT TRUE; 
ALTER TABLE product ADD COLUMN is_available BOOLEAN DEFAULT TRUE;