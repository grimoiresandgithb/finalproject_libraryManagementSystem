# Library Management System

CPRG211 Final Project - Database Connected Application

## Problem It Solves

Libraries need to track three things: what items they own, who their members
are, and who currently has what checked out. Doing this with paper records or
spreadsheets breaks down quickly - items get "lost" because a checkout wasn't
recorded, members accumulate overdue items nobody noticed, and searching the
collection is slow.

This application solves that by providing a single command-line tool backed
by a MariaDB database. Staff can add and manage books and DVDs, register
members, check items in and out, search the catalog, and see which loans
are overdue. All data is stored persistently in the database.

## How the System Works

The application is organized into six packages that each have a single
responsibility:

- **model**         - Domain classes: Item (abstract), Book, DVD, User
                      (abstract), Member, Librarian, Loan. Uses inheritance
                      to share common fields and behavior.
- **interfaces**    - `Manageable<T>` defines the CRUD contract every data-
                      access class follows. `Searchable<T>` adds keyword
                      search for managers that support it.
- **data**          - `DatabaseConnection` centralizes JDBC setup.
                      `ItemManager`, `UserManager`, and `LoanManager` each
                      implement `Manageable` and execute parameterized SQL
                      against their table.
- **service**       - `LibraryService` coordinates between the managers and
                      enforces business rules (e.g., "can't check out an
                      item that's already on loan").
- **exception**     - Four custom checked exceptions:
                      `ItemNotFoundException`, `UserNotFoundException`,
                      `ItemUnavailableException`, `InvalidInputException`.
- **app**           - `Main` contains the CLI menu loop. All input passes
                      through validation helpers before reaching the
                      service layer.

When the user launches the app, `Main` builds the managers and service,
then enters a menu loop. Each menu choice dispatches to a method that
validates input, calls into `LibraryService`, and formats the result.
The managers handle JDBC connections via try-with-resources so connections
always close, even on errors.

## Setup Instructions

### 1. Install MariaDB

Install MariaDB locally (https://mariadb.org/download/) and make sure the
server is running. Default port is 3306.

### 3. Install a lightweight GUI for MariaDB, such as HeidiSQL

Install HeidiSQL (https://www.heidisql.com/) to manager MariaDB, and for ease of use running the `schema.sql` file.

### 2. Create the Database

Run `schema.sql` to create the database, tables, and sample
data.

### 3. Add the JDBC Driver

If not in `lib`, download the latest mariadb jar file from
https://mariadb.com/docs/connectors/mariadb-connector-j/about-mariadb-connector-j
and place it in the `lib/` folder. The .classpath file should redirect automatically to the jar file. If the version is incorrect, change the version number to match what was downloaded in the classpath file.

### 4. Update Credentials

Open `src/data/DatabaseConnection.java` and update `USERNAME` and `PASSWORD`
to match your local MariaDB setup.

### 5. Import into Eclipse

- File -> Import -> Existing Projects into Workspace
- Browse to this folder and click Finish
- Right-click the project -> Refresh (F5)
- Right-click the project -> Build Path -> Configure Build Path ->
  verify `mariadb-java-client-3.X.X.jar` is listed under Libraries

### 6. Run

Right-click `src/app/Main.java` -> Run As -> Java Application

## Usage

The main menu offers:

    1. Manage Books       (Add / View / Update / Delete)
    2. Manage DVDs        (Add / View / Update / Delete)
    3. Manage Members     (Add / View / Update / Delete / View Loans /
                           Add Librarian)
    4. Checkout / Return  (Issue a loan or return an item)
    5. Search             (By title/author/director, or by member name)
    6. View Overdue Loans
    0. Exit

All input is validated. Invalid entries produce a clear error message and
re-prompt rather than crashing the application.

## File Overview

    LibraryManagementSystem/
    +-- .classpath                      Eclipse build path
    +-- .project                        Eclipse project descriptor
    +-- README.md                       This file
    +-- schema.sql                      Database schema + sample data
    +-- lib/
    |   +-- README.txt                  Where to put the MariaDB JAR
    +-- src/
        +-- app/
        |   +-- Main.java               Entry point + CLI menu
        +-- data/
        |   +-- DatabaseConnection.java Centralized JDBC setup
        |   +-- ItemManager.java        CRUD for items
        |   +-- LoanManager.java        CRUD for loans
        |   +-- UserManager.java        CRUD for users
        +-- exception/
        |   +-- InvalidInputException.java
        |   +-- ItemNotFoundException.java
        |   +-- ItemUnavailableException.java
        |   +-- UserNotFoundException.java
        +-- interfaces/
        |   +-- Manageable.java         Generic CRUD contract
        |   +-- Searchable.java         Keyword-search contract
        +-- model/
        |   +-- Book.java
        |   +-- DVD.java
        |   +-- Item.java               Abstract parent
        |   +-- Librarian.java
        |   +-- Loan.java
        |   +-- Member.java
        |   +-- User.java               Abstract parent
        +-- service/
            +-- LibraryService.java     Business logic coordinator
