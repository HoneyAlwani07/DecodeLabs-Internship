import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

//Class1: Transaction
class Transaction {

    private String type;
    private double amount;
    private double balanceAfter;
    private LocalDateTime timestamp;

    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public double getBalanceAfter() { return balanceAfter; }

    public String getTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return timestamp.format(formatter);
    }

    @Override
    public String toString() {
        if (type.equals("BALANCE_CHECK")) {
            return String.format("| %-15s | %-10s | Rs.%,.2f | %s |",
                    type, "---", balanceAfter, getTimestamp());
        }
        return String.format("| %-15s | Rs.%,9.2f | Rs.%,.2f | %s |",
                type, amount, balanceAfter, getTimestamp());
    }
}

//Class2: BankAccount 
class BankAccount {

    // Private fields (Encapsulation)
    private String accountHolderName;
    private String accountNumber;
    private int pin;
    private double balance;
    private List<Transaction> transactionHistory;

    // Constructor
    public BankAccount(String accountHolderName, String accountNumber, int pin, double initialBalance) {
        this.accountHolderName = accountHolderName;
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();

        if (initialBalance > 0) {
            transactionHistory.add(new Transaction("INITIAL_DEPOSIT", initialBalance, balance));
        }
    }

    public String getAccountHolderName() { return accountHolderName; }
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }

    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    // PIN validation
    public boolean validatePin(int inputPin) {
        return this.pin == inputPin;
    }

    // DEPOSIT method
    public boolean deposit(double amount) {
        if (amount <= 0) {
            System.out.println("  Invalid amount! Deposit must be greater than Rs.0.00");
            return false;
        }
        if (amount > 1000000) {
            System.out.println("  Deposit limit exceeded! Max Rs.10,00,000 per transaction");
            return false;
        }

        balance += amount;
        transactionHistory.add(new Transaction("DEPOSIT", amount, balance));
        System.out.println("  Rs." + String.format("%,.2f", amount) + " deposited successfully!");
        System.out.println("  New Balance: Rs." + String.format("%,.2f", balance));
        return true;
    }

    // WITHDRAWAL method
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("  Invalid amount! Withdrawal must be greater than Rs.0.00");
            return false;
        }
        if (amount > balance) {
            System.out.println("  Insufficient Funds!");
            System.out.println("  Available Balance: Rs." + String.format("%,.2f", balance));
            System.out.println("  Requested Amount:  Rs." + String.format("%,.2f", amount));
            System.out.println("  Shortage:          Rs." + String.format("%,.2f", (amount - balance)));
            return false;
        }
        if (amount < 100) {
            System.out.println("  Minimum withdrawal is Rs.100.00");
            return false;
        }
        if (amount % 100 != 0) {
            System.out.println("  Withdrawal amount must be in multiples of Rs.100");
            return false;
        }

        balance -= amount;
        transactionHistory.add(new Transaction("WITHDRAWAL", amount, balance));
        System.out.println("  Rs." + String.format("%,.2f", amount) + " withdrawn successfully!");
        System.out.println("  Remaining Balance: Rs." + String.format("%,.2f", balance));
        return true;
    }

    // BALANCE CHECK method
    public void checkBalance() {
        System.out.println("+------------------------------------------+");
        System.out.println("|          BALANCE ENQUIRY                 |");
        System.out.println("+------------------------------------------+");
        System.out.println("  Account Holder : " + accountHolderName);
        System.out.println("  Account Number : " + accountNumber);
        System.out.println("  Balance        : Rs." + String.format("%,.2f", balance));
        System.out.println("+------------------------------------------+");
        transactionHistory.add(new Transaction("BALANCE_CHECK", 0, balance));
    }

    // TRANSACTION HISTORY method (Bonus feature)
    public void displayTransactionHistory() {
        System.out.println("\n+====================================================================+");
        System.out.println("|                     TRANSACTION HISTORY                            |");
        System.out.println("+====================================================================+");

        if (transactionHistory.isEmpty()) {
            System.out.println("|  No transactions found.                                            |");
        } else {
            System.out.printf("| %-15s | %-12s | %-14s | %-19s |\n",
                    "TYPE", "AMOUNT", "BALANCE", "DATE & TIME");
            System.out.println("+--------------------------------------------------------------------+");
            for (Transaction t : transactionHistory) {
                System.out.println("| " + t.toString() + " |");
            }
        }
        System.out.println("+====================================================================+");
    }
}

//Class3: ATM
class ATM {

    private Map<String, BankAccount> accounts;
    private BankAccount currentAccount;
    private boolean isAuthenticated;
    private Scanner scanner;
    private static final int MAX_PIN_ATTEMPTS = 3;

    public ATM() {
        this.accounts = new HashMap<>();
        this.isAuthenticated = false;
        this.currentAccount = null;
        this.scanner = new Scanner(System.in);
    }

    // Add account to ATM
    public void addAccount(BankAccount account) {
        accounts.put(account.getAccountNumber(), account);
    }

    // PIN-based Login (Bonus feature)
    public void authenticate() {
        System.out.println("\n+------------------------------------------+");
        System.out.println("|            ATM LOGIN                     |");
        System.out.println("+------------------------------------------+");

        System.out.print("  Enter Account Number: ");
        String accountNumber = scanner.nextLine().trim();

        if (!accounts.containsKey(accountNumber)) {
            System.out.println("  Account not found! Check your account number.");
            return;
        }

        BankAccount account = accounts.get(accountNumber);
        int attempts = 0;

        while (attempts < MAX_PIN_ATTEMPTS) {
            System.out.print("  Enter 4-digit PIN: ");
            String pinInput = scanner.nextLine().trim();

            int pin;
            try {
                pin = Integer.parseInt(pinInput);
            } catch (NumberFormatException e) {
                attempts++;
                System.out.println("  Invalid PIN format! Enter digits only.");
                System.out.println("  Attempts remaining: " + (MAX_PIN_ATTEMPTS - attempts));
                continue;
            }

            if (account.validatePin(pin)) {
                isAuthenticated = true;
                currentAccount = account;
                System.out.println("\n  Authentication Successful!");
                System.out.println("  Welcome, " + account.getAccountHolderName() + "!");
                return;
            } else {
                attempts++;
                System.out.println("  Incorrect PIN!");
                System.out.println("  Attempts remaining: " + (MAX_PIN_ATTEMPTS - attempts));
            }
        }

        System.out.println("\n  Maximum attempts exceeded! Account temporarily locked.");
    }

    // Start ATM
    public void start() {
        displayWelcome();
        authenticate();

        if (!isAuthenticated) return;

        boolean running = true;
        while (running && isAuthenticated) {
            displayMenu();
            int choice = getValidChoice();

            switch (choice) {
                case 1: handleDeposit(); break;
                case 2: handleWithdrawal(); break;
                case 3: currentAccount.checkBalance(); break;
                case 4: currentAccount.displayTransactionHistory(); break;
                case 5:
                    System.out.println("\n  Logging out...");
                    System.out.println("  Account: " + currentAccount.getAccountHolderName());
                    System.out.println("  Final Balance: Rs." + String.format("%,.2f", currentAccount.getBalance()));
                    isAuthenticated = false;
                    currentAccount = null;
                    running = false;
                    break;
                default:
                    System.out.println("  Invalid option! Please select 1-5.");
            }
        }

        System.out.println("\n  Thank you for using DecodeLabs ATM!");
    }

    private void displayWelcome() {
        System.out.println("\n+================================================+");
        System.out.println("|                                                |");
        System.out.println("|         DECODELABS ATM                         |");
        System.out.println("|                                                |");
        System.out.println("|    Industrial Training Kit | Batch 2026        |");
        System.out.println("|                                                |");
        System.out.println("+================================================+");
    }

    private void displayMenu() {
        System.out.println("\n+------------------------------------------+");
        System.out.println("|          ATM MAIN MENU                   |");
        System.out.println("+------------------------------------------+");
        System.out.println("|  1. Deposit Money                        |");
        System.out.println("|  2. Withdraw Money                       |");
        System.out.println("|  3. Check Balance                        |");
        System.out.println("|  4. Transaction History                  |");
        System.out.println("|  5. Logout                               |");
        System.out.println("+------------------------------------------+");
        System.out.print("  Enter your choice (1-5): ");
    }

    private int getValidChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void handleDeposit() {
        System.out.println("\n  --- DEPOSIT ---");
        System.out.print("  Enter deposit amount (Rs.): ");
        double amount = getValidAmount();
        if (amount != -1) currentAccount.deposit(amount);
    }

    private void handleWithdrawal() {
        System.out.println("\n  --- WITHDRAWAL ---");
        System.out.println("  Note: Min Rs.100, multiples of Rs.100 only");
        System.out.print("  Enter withdrawal amount (Rs.): ");
        double amount = getValidAmount();
        if (amount != -1) currentAccount.withdraw(amount);
    }

    private double getValidAmount() {
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());
            if (amount <= 0) {
                System.out.println("  Amount must be greater than Rs.0.00");
                return -1;
            }
            return amount;
        } catch (NumberFormatException e) {
            System.out.println("  Invalid input! Enter a valid numeric amount.");
            return -1;
        }
    }
}

//Class4:Main
public class Main {

    public static void main(String[] args) {

        // Create ATM object
        ATM atm = new ATM();

        // Create BankAccount objects
        BankAccount acc1 = new BankAccount("Arjun Sharma", "1001", 1234, 50000.00);
        BankAccount acc2 = new BankAccount("Priya Gupta",   "1002", 5678, 25000.00);
        BankAccount acc3 = new BankAccount("Rahul Verma",   "1003", 9012, 100000.00);

        // Register accounts
        atm.addAccount(acc1);
        atm.addAccount(acc2);
        atm.addAccount(acc3);

        // Start ATM
        atm.start();
    }
}