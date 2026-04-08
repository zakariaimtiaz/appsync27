# appsync27 - A Spring boot application for data syncronization
A flexible server-client data synchronization framework supporting unidirectional and bidirectional database sync with version_no and sent_flag tracking.


## Overview

This application provides a robust data synchronization framework between **SERVER** and **CLIENT** instances. The configuration is organized into four main sections:

| Section | Purpose | Availability |
|---------|---------|--------------|
| **App Configuration** | Defines application role (SERVER or CLIENT), determining available menu options and system behavior | Always available |
| **Server Configuration** | Defines which server a client connects to (name + address) | When App Type = `CLIENT` |
| **Client Configuration** | Defines unique identifiers (Name + Code) for each connecting client | When App Type = `SERVER` |
| **Database Configuration** | Defines target databases for data operations (URL, Schema, Status) | Always available |
| **Table Configuration** | Defines tables and synchronization patterns | Always available |

### Synchronization Patterns

| Pattern | Table Type | Direction | Tracking Key |
|---------|------------|-----------|--------------|
| Server → Client | `MASTER` | Server to Client | `version_no` |
| Client → Server | `CLIENT` | Client to Server | `sent_flag` |
| Bi-directional | `MASTER-CLIENT` | Both directions | Both keys |

> **⚠️ Critical Requirement:** For correct synchronization, **Table Type**, **Table Name**, and **Table Code** must be **identical** in both Server and Client applications. SQL queries are database-specific, and the `version_no` field should be updated automatically via database triggers.
## App Configuration

This configuration defines whether the application would be `SERVER` or `CLIENT`:

- If Type set as `SERVER`:
  - Client configuration options will appear in the left menu
  - This instance can manage multiple client applications
  - Can push configurations to connected clients
  - Will act as the central data repository

- If Type set as `CLIENT`:
  - Server configuration options will appear in the left menu
  - This instance will connect to a server application
  - Will receive configurations from the server
  - Will sync data periodically with the server

> **Note:** Changing this setting will modify the available menu options immediately.

## Server Configuration

Server configuration defines which server this application communicates with:

- **Server Name:** Unique identifier for servers
- **Server Address:** Connection URL

| Server Name | Server Address |
|-------------|----------------|
| {{db.name}} | {{db.address}} |

> **Note:** Server configuration is only available when the application is set as `CLIENT` type.

## Client Configuration

Client configuration defines its unique identifier for communicating with the master:

- **Name:** Name of the client
- **Code:** Unique identifier for client

| Name | Code |
|------|------|
| {{db.name}} | {{db.code}} |

> **Note:** Client configuration is only available when the application is set as `SERVER` type.

## Database Configuration

Database Configuration defines the target databases where this application will transfer, update, insert, and synchronize data. Each configured database represents a destination for data operations from this application:

- **Config name** - Unique identifier for database
- **URL** - Connection URL of the database
- **Schema** - Schema name (PostgreSQL), Database name (MySQL)
- **Status** - Defines connection URL is valid

| Name | URL | Schema | Type | Status |
|------|-----|--------|------|--------|
| {{db.name}} | {{db.url}} | {{db.schema}} | {{db.type}} | {{db.status}} |

## Table Configuration

Table Configuration defines the specific tables that participate in data transfer, insert, update, and synchronization operations. Each configured database represents a destination for data operations performed by this application.

**While transferring data, the table type, table name, and table code must be identical in both the Server and Client applications to ensure correct mapping and synchronization.**

> **Note:** SQL queries are **database-specific**. The `version_no` field must be updated automatically after data operations, preferably by using database triggers.

### Scenario 1: Transfer data from Server to Client
- Set Table Type as MASTER
- Server App must have a SELECT query using `S TRACKING KEY` e.g version_no
- Client App provides its maximum version_no
- Server sends records where version_no > :version_no
- Client App upserts the received data into its target table

#### Server Configuration (MASTER)

| Field | Value | Explanation |
|-------|-------|-------------|
| **APP TYPE** | SERVER | Indicates this configuration belongs to the central server |
| **DATABASE** | eHospital | Name of the connected database |
| **TABLE TYPE** | MASTER (M) | Defines synchronization category |
| **NAME** | user_info | Physical database table name |
| **CODE** | TM001 | Unique internal table identifier |
| **PRIMARY KEY** | attendance_id | Primary key column used for record identification. Column used as unique identifier and referenced in ON CONFLICT (...) during upsert |
| **S TRACKING KEY** | version_no | Server-side tracking column used to determine records eligible for synchronization |
| **SELECT SQL** | `select * from :table where version_no > :version_no order by version_no asc limit :chunk_size` | Fetches all eligible records from the server MASTER table for synchronization. Parameters (:table, :version_no, :chunk_size) remain unchanged |
| **SYNC PRIORITY** | 1001 | Acts as execution order during synchronization |

#### Client Configuration (MASTER)

| Field | Value | Explanation |
|-------|-------|-------------|
| **APP TYPE** | CLIENT | Application role in synchronization architecture |
| **DATABASE** | eHospital | Target database name |
| **TABLE TYPE** | MASTER (M) | Indicates this table acts as a master data source |
| **NAME** | user_info | Physical table name in the client database |
| **CODE** | TM001 | Unique identifier for sync configuration |
| **PRIMARY KEY** | attendance_id | Primary key column used for record identification. Column used as unique identifier and referenced in ON CONFLICT (...) during upsert |
| **S TRACKING KEY** | version_no | Client-side tracking column used to determine max reference number eligible for synchronization |
| **UPSERT SQL** | `INSERT INTO user_info (user_id, user_code, email, user_login_id, password, version_no) VALUES (:user_id::uuid, :user_code, :email, :user_login_id, :password, :version_no) ON CONFLICT (user_id) DO UPDATE SET user_code = :user_code, email = :email, user_login_id = :user_login_id, version_no = :version_no` | UPSERT statement used for synchronization. Use :parameterName format in SQL |
| **SYNC PRIORITY** | 1001 | Determines execution order during synchronization |

### Scenario 2: Insert data from Client to Server
- Set Table Type as CLIENT
- Client App must have a SELECT query using `C TRACKING KEY` e.g sent_flag
- Client sends unsent records to Server
- Server App inserts or updates data using an UPSERT query
- Client marks records as sent after successful transfer

#### Server Configuration (CLIENT)

| Field | Value | Explanation |
|-------|-------|-------------|
| **APP TYPE** | SERVER | Application role in synchronization architecture |
| **DATABASE** | eHospital | Target database name |
| **TABLE TYPE** | CLIENT (C) | Indicates this table stores client-originated data |
| **NAME** | user_info | Physical table name in the server database |
| **CODE** | TM001 | Unique identifier for sync configuration |
| **PRIMARY KEY** | attendance_id | Primary key column used for record identification. Column used as unique identifier and referenced in ON CONFLICT (...) during upsert |
| **S TRACKING KEY** | version_no | Server-side tracking column used to determine records eligible for synchronization |
| **UPSERT SQL** | `INSERT INTO user_info (user_id, user_code, email, user_login_id, password, version_no) VALUES (:user_id::uuid, :user_code, :email, :user_login_id, :password, :version_no) ON CONFLICT (user_id) DO UPDATE SET user_code = :user_code, email = :email, user_login_id = :user_login_id, version_no = :version_no` | UPSERT statement used during synchronization from client to server. Use :parameterName format in SQL |
| **SYNC PRIORITY** | 1001 | Determines execution order during synchronization |

#### Client Configuration (CLIENT)

| Field | Value | Explanation |
|-------|-------|-------------|
| **APP TYPE** | CLIENT | Application role in synchronization architecture |
| **DATABASE** | eHospital | Source database name on client side |
| **TABLE TYPE** | CLIENT (C) | Indicates this table originates from client environment |
| **NAME** | user_info | Physical table name in the client database |
| **CODE** | TM001 | Unique sync configuration identifier |
| **PRIMARY KEY** | attendance_id | Primary key column used for record identification. Column used as unique identifier and referenced in ON CONFLICT (...) during upsert |
| **C TRACKING KEY** | sent_flag | Client-side tracking column used to determine max reference number eligible for synchronization |
| **SELECT SQL** | `select * from :table where sent_flag = FALSE order by disease_id asc limit :chunk_size` | Query used to fetch unsynchronized records in batches from client |
| **SYNC PRIORITY** | 1001 | Determines execution order during synchronization |

### Scenario 3: Bi-directional data transfer (Server - Client)
- Set Table Type as MASTER-CLIENT
- Both Server and Client Apps must have SELECT and UPSERT queries
- Server App fetches data using version_no
- Client App fetches data using sent_flag
- Data flows safely in both directions
