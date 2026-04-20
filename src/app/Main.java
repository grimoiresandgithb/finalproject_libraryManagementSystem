package app;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import data.*;
import exception.*;
import model.*;
import service.LibraryService;

/* Main.java
Date: April 19 2026

Application entry point and command-line interface. Creates the data-access managers, wires them into LibraryService, and runs an
interactive menu loop. All user input is validated before being passed to the service layer - invalid input results in a clear
error message and a re-prompt rather than a crash. */
public class Main {

    /*Shared Scanner - reused across all input helpers so we don't
    accidentally close System.in partway through the session.*/
    private static final Scanner SC = new Scanner(System.in);

    /*Managers and service - initialized in main() and passed around
    as static fields to keep menu methods readable.*/
    private static ItemManager itemManager;
    private static UserManager userManager;
    private static LoanManager loanManager;
    private static LibraryService service;

    // Entry point
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  Library Management System");
        System.out.println("==========================================");

        /*Initialize the data layer. If the database is unreachable,
         DatabaseConnection will throw a RuntimeException with a clear
        explanation.*/
        try {
            itemManager = new ItemManager();
            userManager = new UserManager();
            loanManager = new LoanManager(itemManager, userManager);
            service = new LibraryService(itemManager, userManager, loanManager);
        } catch (RuntimeException e) {
            System.err.println("Startup failed: " + e.getMessage());
            System.err.println("Check that MariaDB is running and the "
                    + "credentials in DatabaseConnection.java are correct.");
            return;
        }

        mainMenu();

        System.out.println("Goodbye!");
        SC.close();
    }

    // Main menu
    private static void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("---------- Main Menu ----------");
            System.out.println("1. Manage Books");
            System.out.println("2. Manage DVDs");
            System.out.println("3. Manage Members");
            System.out.println("4. Checkout / Return");
            System.out.println("5. Search");
            System.out.println("6. View Overdue Loans");
            System.out.println("0. Exit");
            int choice = readMenuChoice(0, 6);

            switch (choice) {
                case 1: manageBooksMenu();     break;
                case 2: manageDVDsMenu();      break;
                case 3: manageMembersMenu();   break;
                case 4: checkoutReturnMenu(); break;
                case 5: searchMenu();          break;
                case 6: viewOverdueLoans();   break;
                case 0: running = false;       break;
                default: // unreachable because readMenuChoice enforces range
            }
        }
    }

    // Books menu
    private static void manageBooksMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("---------- Manage Books ----------");
            System.out.println("1. Add Book");
            System.out.println("2. View All Books");
            System.out.println("3. Update Book");
            System.out.println("4. Delete Book");
            System.out.println("0. Back to Main Menu");
            int choice = readMenuChoice(0, 4);

            switch (choice) {
                case 1: addBook();         break;
                case 2: viewAllBooks();    break;
                case 3: updateBook();      break;
                case 4: deleteItem("book"); break;
                case 0: back = true;       break;
            }
        }
    }

    private static void addBook() {
        System.out.println("\n-- Add Book --");
        try {
            String title = readNonEmptyString("Title: ");
            String author = readNonEmptyString("Author: ");
            String isbn = readIsbn("ISBN (10 or 13 digits): ");
            int year = readYear("Publication year: ");

            Book book = new Book(0, title, true, author, isbn, year);
            itemManager.add(book);
            System.out.println("Book added successfully with ID " + book.getId());
        } catch (InvalidInputException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void viewAllBooks() {
        System.out.println("\n-- All Books --");
        List<Item> items = itemManager.getAll();
        boolean any = false;
        for (Item it : items) {
            if (it instanceof Book) {
                System.out.println(it);
                any = true;
            }
        }
        if (!any) System.out.println("(no books found)");
    }

    private static void updateBook() {
        System.out.println("\n-- Update Book --");
        try {
            int id = readPositiveInt("Book ID to update: ");
            Item item = itemManager.getById(id);
            if (!(item instanceof Book)) {
                System.err.println("Error: item " + id + " is not a book.");
                return;
            }
            Book book = (Book) item;

            System.out.println("Leave blank to keep current value.");
            String title = readOptionalString("Title [" + book.getTitle() + "]: ");
            if (!title.isEmpty()) book.setTitle(title);

            String author = readOptionalString("Author [" + book.getAuthor() + "]: ");
            if (!author.isEmpty()) book.setAuthor(author);

            String isbn = readOptionalString("ISBN [" + book.getIsbn() + "]: ");
            if (!isbn.isEmpty()) {
                if (!isValidIsbn(isbn)) {
                    throw new InvalidInputException("ISBN must be 10 or 13 digits.");
                }
                book.setIsbn(isbn);
            }

            String yearStr = readOptionalString("Year [" + book.getPublicationYear() + "]: ");
            if (!yearStr.isEmpty()) {
                int year = parsePositiveInt(yearStr, "year");
                if (year > LocalDate.now().getYear()) {
                    throw new InvalidInputException("Year cannot be in the future.");
                }
                book.setPublicationYear(year);
            }

            itemManager.update(book);
            System.out.println("Book updated successfully.");
        } catch (ItemNotFoundException | InvalidInputException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // DVDs menu
    private static void manageDVDsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("---------- Manage DVDs ----------");
            System.out.println("1. Add DVD");
            System.out.println("2. View All DVDs");
            System.out.println("3. Update DVD");
            System.out.println("4. Delete DVD");
            System.out.println("0. Back to Main Menu");
            int choice = readMenuChoice(0, 4);

            switch (choice) {
                case 1: addDVD();          break;
                case 2: viewAllDVDs();     break;
                case 3: updateDVD();       break;
                case 4: deleteItem("dvd"); break;
                case 0: back = true;       break;
            }
        }
    }

    private static void addDVD() {
        System.out.println("\n-- Add DVD --");
        try {
            String title = readNonEmptyString("Title: ");
            String director = readNonEmptyString("Director: ");
            int runtime = readPositiveInt("Runtime (minutes): ");

            DVD dvd = new DVD(0, title, true, director, runtime);
            itemManager.add(dvd);
            System.out.println("DVD added successfully with ID " + dvd.getId());
        } catch (InvalidInputException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void viewAllDVDs() {
        System.out.println("\n-- All DVDs --");
        List<Item> items = itemManager.getAll();
        boolean any = false;
        for (Item it : items) {
            if (it instanceof DVD) {
                System.out.println(it);
                any = true;
            }
        }
        if (!any) System.out.println("(no DVDs found)");
    }

    private static void updateDVD() {
        System.out.println("\n-- Update DVD --");
        try {
            int id = readPositiveInt("DVD ID to update: ");
            Item item = itemManager.getById(id);
            if (!(item instanceof DVD)) {
                System.err.println("Error: item " + id + " is not a DVD.");
                return;
            }
            DVD dvd = (DVD) item;

            System.out.println("Leave blank to keep current value.");
            String title = readOptionalString("Title [" + dvd.getTitle() + "]: ");
            if (!title.isEmpty()) dvd.setTitle(title);

            String director = readOptionalString("Director [" + dvd.getDirector() + "]: ");
            if (!director.isEmpty()) dvd.setDirector(director);

            String rtStr = readOptionalString("Runtime [" + dvd.getRuntimeMinutes() + "]: ");
            if (!rtStr.isEmpty()) {
                dvd.setRuntimeMinutes(parsePositiveInt(rtStr, "runtime"));
            }

            itemManager.update(dvd);
            System.out.println("DVD updated successfully.");
        } catch (ItemNotFoundException | InvalidInputException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /** Shared delete flow for books and DVDs. */
    private static void deleteItem(String expectedType) {
        System.out.println("\n-- Delete " + expectedType.toUpperCase() + " --");
        try {
            int id = readPositiveInt("ID to delete: ");
            Item item = itemManager.getById(id);
            if (!item.getItemType().equalsIgnoreCase(expectedType)) {
                System.err.println("Error: item " + id + " is a "
                        + item.getItemType() + ", not a " + expectedType + ".");
                return;
            }
            System.out.println("About to delete: " + item);
            String confirm = readOptionalString("Type 'yes' to confirm: ");
            if (!"yes".equalsIgnoreCase(confirm)) {
                System.out.println("Delete cancelled.");
                return;
            }
            itemManager.delete(id);
            System.out.println("Deleted successfully.");
        } catch (ItemNotFoundException | InvalidInputException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (RuntimeException e) {
            // Foreign key constraint - item has active loans
            System.err.println("Cannot delete: item may have active loans. "
                    + "Return the item first and then try again.");
        }
    }
	
    // Members menu
    private static void manageMembersMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("---------- Manage Members ----------");
            System.out.println("1. Add Member");
            System.out.println("2. View All Members");
            System.out.println("3. Update Member");
            System.out.println("4. Delete Member");
            System.out.println("5. View Member's Loans");
            System.out.println("6. Add Librarian (staff)");
            System.out.println("0. Back to Main Menu");
            int choice = readMenuChoice(0, 6);

            switch (choice) {
                case 1: addMember();         break;
                case 2: viewAllUsers();      break;
                case 3: updateMember();      break;
                case 4: deleteUser();        break;
                case 5: viewMemberLoans();   break;
                case 6: addLibrarian();      break;
                case 0: back = true;         break;
            }
        }
    }

    private static void addMember() {
        System.out.println("\n-- Add Member --");
        try {
            String name = readNonEmptyString("Name: ");
            String email = readEmail("Email: ");
            String type = readMembershipType("Membership type (standard / premium): ");
            Member member = new Member(0, name, email, type);
            userManager.add(member);
            System.out.println("Member added successfully with ID " + member.getUserId());
        } catch (InvalidInputException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void addLibrarian() {
        System.out.println("\n-- Add Librarian --");
        try {
            String name = readNonEmptyString("Name: ");
            String email = readEmail("Email: ");
            String empId = readNonEmptyString("Employee ID: ");
            Librarian lib = new Librarian(0, name, email, empId);
            userManager.add(lib);
            System.out.println("Librarian added with ID " + lib.getUserId());
        } catch (InvalidInputException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void viewAllUsers() {
        System.out.println("\n-- All Users --");
        List<User> users = userManager.getAll();
        if (users.isEmpty()) {
            System.out.println("(no users found)");
        } else {
            for (User u : users) System.out.println(u);
        }
    }

    private static void updateMember() {
        System.out.println("\n-- Update Member --");
        try {
            int id = readPositiveInt("Member ID: ");
            User user = userManager.getById(id);
            if (!(user instanceof Member)) {
                System.err.println("Error: user " + id + " is not a member.");
                return;
            }
            Member m = (Member) user;

            System.out.println("Leave blank to keep current value.");
            String name = readOptionalString("Name [" + m.getName() + "]: ");
            if (!name.isEmpty()) m.setName(name);

            String email = readOptionalString("Email [" + m.getEmail() + "]: ");
            if (!email.isEmpty()) {
                if (!isValidEmail(email)) {
                    throw new InvalidInputException("Invalid email format.");
                }
                m.setEmail(email);
            }

            String type = readOptionalString(
                    "Membership type [" + m.getMembershipType() + "]: ");
            if (!type.isEmpty()) {
                if (!type.equalsIgnoreCase("standard") && !type.equalsIgnoreCase("premium")) {
                    throw new InvalidInputException(
                            "Membership type must be 'standard' or 'premium'.");
                }
                m.setMembershipType(type.toLowerCase());
            }

            userManager.update(m);
            System.out.println("Member updated successfully.");
        } catch (UserNotFoundException | InvalidInputException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        System.out.println("\n-- Delete User --");
        try {
            int id = readPositiveInt("User ID: ");
            User u = userManager.getById(id);
            System.out.println("About to delete: " + u);
            String confirm = readOptionalString("Type 'yes' to confirm: ");
            if (!"yes".equalsIgnoreCase(confirm)) {
                System.out.println("Delete cancelled.");
                return;
            }
            userManager.delete(id);
            System.out.println("User deleted.");
        } catch (UserNotFoundException | InvalidInputException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Cannot delete: user may have active loans.");
        }
    }

    private static void viewMemberLoans() {
        System.out.println("\n-- Member's Loans --");
        try {
            int id = readPositiveInt("Member ID: ");
            // Verify they exist first for a clearer error message
            User u = userManager.getById(id);
            if (!(u instanceof Member)) {
                System.err.println("Error: user " + id + " is not a member.");
                return;
            }
            List<Loan> loans = service.getMemberLoans(id);
            if (loans.isEmpty()) {
                System.out.println("(no loans found for this member)");
            } else {
                for (Loan l : loans) System.out.println(l);
            }
        } catch (UserNotFoundException | InvalidInputException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Checkout / Return menu
    private static void checkoutReturnMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("---------- Checkout / Return ----------");
            System.out.println("1. Checkout Item");
            System.out.println("2. Return Item");
            System.out.println("0. Back to Main Menu");
            int choice = readMenuChoice(0, 2);

            switch (choice) {
                case 1: checkoutItem(); break;
                case 2: returnItem();   break;
                case 0: back = true;    break;
            }
        }
    }

    private static void checkoutItem() {
        System.out.println("\n-- Checkout Item --");
        try {
            int memberId = readPositiveInt("Member ID: ");
            int itemId = readPositiveInt("Item ID: ");
            Loan loan = service.checkoutItem(memberId, itemId);
            System.out.println("Checkout successful!");
            System.out.println(loan);
        } catch (UserNotFoundException
                | ItemNotFoundException
                | ItemUnavailableException
                | InvalidInputException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void returnItem() {
        System.out.println("\n-- Return Item --");
        try {
            int itemId = readPositiveInt("Item ID: ");
            Loan loan = service.returnItem(itemId);
            System.out.println("Return successful!");
            System.out.println(loan);
        } catch (ItemNotFoundException | UserNotFoundException | InvalidInputException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Search menu
    private static void searchMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("---------- Search ----------");
            System.out.println("1. Search Items (title / author / director)");
            System.out.println("2. Search Users (name / email)");
            System.out.println("0. Back to Main Menu");
            int choice = readMenuChoice(0, 2);

            switch (choice) {
                case 1: searchItems(); break;
                case 2: searchUsers(); break;
                case 0: back = true;   break;
            }
        }
    }

    private static void searchItems() {
        try {
            String keyword = readNonEmptyString("Keyword: ");
            List<Item> results = service.searchItems(keyword);
            if (results.isEmpty()) {
                System.out.println("(no matches)");
            } else {
                for (Item i : results) System.out.println(i);
            }
        } catch (InvalidInputException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void searchUsers() {
        try {
            String keyword = readNonEmptyString("Keyword: ");
            List<User> results = service.searchUsers(keyword);
            if (results.isEmpty()) {
                System.out.println("(no matches)");
            } else {
                for (User u : results) System.out.println(u);
            }
        } catch (InvalidInputException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Overdue loans
    private static void viewOverdueLoans() {
        System.out.println("\n-- Overdue Loans --");
        List<Loan> overdue = service.getOverdueLoans();
        if (overdue.isEmpty()) {
            System.out.println("(no overdue loans - nice!)");
        } else {
            for (Loan l : overdue) System.out.println(l);
        }
    }

    // Input validation helpers

    /* Read an integer menu choice and re-prompt until it is within the
     allowed range. Non-numeric input is also caught and re-prompted.*/
    private static int readMenuChoice(int min, int max) {
        while (true) {
            System.out.print("Choice: ");
            String line = SC.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.err.println("Please enter a number between "
                            + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.err.println("That isn't a number. Please try again.");
            }
        }
    }

    private static String readNonEmptyString(String prompt) throws InvalidInputException {
        System.out.print(prompt);
        String line = SC.nextLine().trim();
        if (line.isEmpty()) {
            throw new InvalidInputException("Input cannot be empty.");
        }
        return line;
    }

    private static String readOptionalString(String prompt) {
        System.out.print(prompt);
        return SC.nextLine().trim();
    }

    private static int readPositiveInt(String prompt) throws InvalidInputException {
        System.out.print(prompt);
        String line = SC.nextLine().trim();
        return parsePositiveInt(line, "value");
    }

    private static int parsePositiveInt(String raw, String label)
            throws InvalidInputException {
        try {
            int v = Integer.parseInt(raw);
            if (v <= 0) {
                throw new InvalidInputException(
                        label + " must be a positive integer.");
            }
            return v;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(
                    label + " must be a valid integer (got: '" + raw + "').");
        }
    }

    private static int readYear(String prompt) throws InvalidInputException {
        int year = readPositiveInt(prompt);
        if (year > LocalDate.now().getYear()) {
            throw new InvalidInputException(
                    "Publication year cannot be in the future.");
        }
        return year;
    }

    private static String readIsbn(String prompt) throws InvalidInputException {
        String isbn = readNonEmptyString(prompt);
        if (!isValidIsbn(isbn)) {
            throw new InvalidInputException(
                    "ISBN must be exactly 10 or 13 digits (no dashes).");
        }
        return isbn;
    }

    private static boolean isValidIsbn(String isbn) {
        if (isbn == null) return false;
        String clean = isbn.replace("-", "").replace(" ", "");
        return clean.matches("\\d{10}") || clean.matches("\\d{13}");
    }

    private static String readEmail(String prompt) throws InvalidInputException {
        String email = readNonEmptyString(prompt);
        if (!isValidEmail(email)) {
            throw new InvalidInputException(
                    "Email must contain '@' and a domain (e.g. name@example.com).");
        }
        return email;
    }

    private static boolean isValidEmail(String email) {
        if (email == null) return false;
        // Basic format check: something@something.something
        return email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    private static String readMembershipType(String prompt)
            throws InvalidInputException {
        String type = readNonEmptyString(prompt).toLowerCase();
        if (!type.equals("standard") && !type.equals("premium")) {
            throw new InvalidInputException(
                    "Membership type must be 'standard' or 'premium'.");
        }
        return type;
    }
}
