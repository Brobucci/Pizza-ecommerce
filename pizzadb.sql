CREATE SCHEMA pizzadb;

-- ============ CATALOGO ============

CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL,
    description TEXT,
    is_configurable BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,      -- soft delete: prodotto tolto definitivamente dal menu (ma restano gli ordini storici che lo referenziano)
    is_available BOOLEAN DEFAULT TRUE,   -- disponibilità del giorno: in menu ma esaurito oggi (concetto diverso da is_active, non sovrapporli)
    finishing_minutes DECIMAL(4,1) NOT NULL DEFAULT 0,
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
    value DECIMAL(10,2) NOT NULL,
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
    is_default BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (customer_id) REFERENCES app_user(id) ON DELETE CASCADE
);

-- ============ ORDINI ============

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NULL,
    customer_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    contact_email VARCHAR(150),
    address VARCHAR(255),
    delivery_notes VARCHAR(255),
    order_type ENUM('DELIVERY', 'TAKEOUT') NOT NULL,
    status ENUM('PENDING', 'PREPARING', 'SHIPPED', 'COMPLETED') DEFAULT 'PENDING',
    requested_time DATETIME NOT NULL,
    estimated_delivery_time DATETIME,
    total_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    delivery_fee DECIMAL(10, 2) DEFAULT 0.00,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES app_user(id)
);

CREATE TABLE order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    orders_id BIGINT NOT NULL,
    quantity INT DEFAULT 1,
    size ENUM('MINI', 'NORMAL', 'MAXI') DEFAULT 'NORMAL',
    is_lactose_free_base BOOLEAN DEFAULT FALSE,
    unit_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (orders_id) REFERENCES orders(id) ON DELETE CASCADE
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

-- ============ CODA DI PRODUZIONE ============

CREATE TABLE station (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE capacity_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_id BIGINT NOT NULL,
    day_of_week TINYINT NOT NULL,
    time_from TIME NOT NULL,
    time_to TIME NOT NULL,
    capacity_per_slot DECIMAL(6,2) NOT NULL,
    note VARCHAR(100),
    FOREIGN KEY (station_id) REFERENCES station(id)
);

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

CREATE TABLE station_size_load (
    station_id BIGINT NOT NULL,
    size ENUM('MINI', 'NORMAL', 'MAXI') NOT NULL,
    load_units DECIMAL(4,2) NOT NULL,
    PRIMARY KEY (station_id, size),
    FOREIGN KEY (station_id) REFERENCES station(id)
);

CREATE TABLE product_stage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    station_id BIGINT NOT NULL,
    sequence_order TINYINT NOT NULL,
    fixed_minutes DECIMAL(4,1) NULL,
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
    load_units DECIMAL(4,2) NOT NULL,
    stage_start DATETIME NOT NULL,
    stage_end DATETIME NOT NULL,
    UNIQUE KEY uq_item_sequence_queue (order_item_id, sequence_order),
    FOREIGN KEY (order_item_id) REFERENCES order_item(id) ON DELETE CASCADE,
    FOREIGN KEY (station_id) REFERENCES station(id),
    FOREIGN KEY (slot_id) REFERENCES production_slot(id)
);

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

-- ============ CONFIGURAZIONE ============

CREATE TABLE system_setting (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value VARCHAR(50) NOT NULL
);

INSERT INTO system_setting (setting_key, setting_value) VALUES
('error_buffer_percent', '5'),
('display_range_minutes', '10'),
('avg_delivery_minutes', '30');