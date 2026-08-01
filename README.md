# Smart Pizzeria E-Commerce & Production Queue Engine

Un'applicazione Full-Stack E-Commerce progettata per gestire la vendita online e l'ottimizzazione dei flussi di produzione/consegna ad alto carico per una pizzeria d'asporto.

Il progetto risolve una problematica critica del settore: **la congestione degli ordini nelle fasce orarie di punta (es. sabato sera)**, integrando un algoritmo di calcolo della capacità produttiva reale basato su stazioni di lavoro e unità di carico dinamiche.

---

## Tech Stack

### **Backend**
* **Java** (Spring Boot, Spring Data JPA, Spring Security)
* **MySQL** (Relational Database Management)
* **Postman** (API Testing & Documentation)

### **Frontend**
* **React** + **TypeScript**
* **HTML5 / CSS3 / Tailwind CSS**

---

## Key Features

### 1. Customization & Ordering Engine
* **Composizione avanzata**: Gestione pizze intere, a metà (50/50), dimensioni variabile (`MINI`, `NORMAL`, `MAXI`) e regole di prezzo dinamiche.
* **Personalizzazione ingredienti**: Aggiunte, rimozioni con eventuale sconto e gestione intolleranze (es. flag lattosio).

### 2. Smart Production Queue (Algoritmo di Coda Smart)
* **Modellazione Stazioni**: Flusso di lavoro suddiviso in stazioni operative autonome (`DOUGH`, `FRYER`, `RIDER`).
* **Unità di Carico Dinamiche**: Ogni dimensione pizza ha un peso diverso sulla stazione (es. una pizza `MAXI` occupa $3.5$ unità di carico rispetto a $1.0$ di una `NORMAL`).
* **Pianificazione Slot orari**: Assegnazione automatica degli ordini a slot temporali di produzione (`production_slot`) in base al profilo di capacità attiva in quella fascia oraria.
* **Stima Consegna Real-time**: Calcolo del tempo stimato di consegna incrociando i tempi di preparazione cucina e le disponibilità della flotta rider.

### 3. User & Role Management
* **Autenticazione & Autorizzazione**: Distinzione dei ruoli `CUSTOMER`, `STAFF` e `ADMIN`.
* **Rubrica Indirizzi & Storico Ordini**: Gestione profili e dettagli per la consegna a domicilio.

---

## Architettura del Database (Schema Overview)

Il database è stato progettato per supportare sia il normale flusso e-commerce sia la complessa logica della coda di produzione:

* **Catalogo & Regole**: `product`, `category`, `ingredient`, `category_size_rule`.
* **Configurazione Ordine**: `orders`, `order_item`, `order_item_config`, `config_ingredient`.
* **Motore di Coda**: `station`, `capacity_profile`, `production_slot`, `station_size_load`, `product_stage`, `order_item_stage_queue`, `order_delivery_queue`.

---

## Getting Started

### Prerequisiti
* Java 17+
* Node.js & npm
* MySQL Server 8.0+

### Setup Database
1. Crea il database locale ed esegui lo script DDL contenuto in `schema.sql`.
2. Configura le credenziali di accesso nel file `src/main/resources/application.properties`.