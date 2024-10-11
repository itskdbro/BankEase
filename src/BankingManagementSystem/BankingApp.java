package BankingManagementSystem;

import com.sun.security.jgss.GSSUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Scanner;

public class BankingApp {
    private static final String url = "jdbc:mysql://localhost:3306/banking_system";
    private static final String username = "root";
    private static final String password = "keshav0220";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Scanner scanner = new Scanner(System.in);
            User user = new User(connection, scanner);
            Accounts accounts = new Accounts(connection, scanner);
            AccountManager accountManager = new AccountManager(connection, scanner);
            String email;
            Long accountNumber;

            while (true) {
                System.out.println("***** WELCOME TO BANKING SYSTEM *****");
                System.out.println();
                System.out.println("1. Resgister");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Enter your choice : ");
                int choice1 = scanner.nextInt();

                switch (choice1) {
                    case 1: {
                        System.out.println("\n***REGISTER***\n");
                        user.register();
                        break;
                    }
                    case 2: {
                        System.out.println("\n***LOGIN***\n");
                        email = user.login();
                        if (email != null) {
                            System.out.println();
                            System.out.println("\nUser logged IN...\n");
                            if (accounts.account_exists(email)) {
                                int choice2 = 0;
                                while (choice2 != 7) {
                                    System.out.println("\n***MAIN MENU***\n");
                                    System.out.println("1. Debit money");
                                    System.out.println("2. Credit money");
                                    System.out.println("3. Transfer money");
                                    System.out.println("4. Check balance");
                                    System.out.println("5. Transaction history");
                                    System.out.println("6. Account Settings");
                                    System.out.println("7. Logout");
                                    System.out.print("Enter your choice: ");
                                    choice2 = scanner.nextInt();
                                    switch (choice2) {
                                        case 1: {
                                            System.out.print("Enter amount you want to debit: ");
                                            double debitAmount = scanner.nextDouble();
                                            System.out.print("Enter security pin: ");
                                            long securityPin = scanner.nextLong();
                                            accountManager.debit_money(email, debitAmount, securityPin);
                                            break;
                                        }
                                        case 2: {
                                            accountManager.credit_money(email);
                                            break;
                                        }
                                        case 3: {
                                            accountManager.transfer_money(email);
                                            break;
                                        }
                                        case 4: {
                                            System.out.print("Enter security pin: ");
                                            long securityPin = scanner.nextLong();
                                            double balance = accountManager.check_balance(email, securityPin);
                                            System.out.println("\nBalance: " + "\u20B9" + balance + "\n");
                                            break;
                                        }
                                        case 5: {
                                            accountManager.transaction_history(email);
                                            break;
                                        }
                                        case 6: {
                                            int choice3 = 0;
                                            while (choice3 != 5) {
                                                System.out.println("\n***ACCOUNT SETTINGS***\n");
                                                System.out.println("1. Change email");
                                                System.out.println("2. Change password");
                                                System.out.println("3. Change security pin");
                                                System.out.println("4. Close Account");
                                                System.out.println("5. Back to menu");
                                                System.out.print("Enter your choice : ");
                                                choice3 = scanner.nextInt();
                                                switch (choice3) {
                                                    case 1: {
                                                        System.out.println("\n***CHANGE EMAIL***\n");
                                                        user.change_email(email);
                                                        break;
                                                    }
                                                    case 2: {
                                                        System.out.println("\n***CHANGE PASSWORD***\n");
                                                        user.change_password(email);
                                                        break;
                                                    }
                                                    case 3: {
                                                        System.out.println("\n***CHANGE SECURITY PIN***\n");
                                                        accounts.change_securityPin(email);
                                                        break;
                                                    }
                                                    case 4: {
                                                        System.out.println("\n***CLOSE ACCOUNT***\n");
                                                        boolean result = accounts.close_account(email);
                                                        if (result) {
                                                            return;
                                                        } else {
                                                            break;
                                                        }
                                                    }
                                                    case 5:
                                                        break;
                                                    default: {
                                                        System.out.println("Enter Valid choice");
                                                    }
                                                }
                                            }
                                        }
                                        case 7:
                                            System.out.println("\nLogout successful.\n");
                                            break;
                                        default:
                                            System.out.println("Enter valid choice");
                                            break;
                                    }
                                }

                            } else {
                                System.out.println("1. Open a new Bank account");
                                System.out.println("2. exit");
                                System.out.print("Enter your choice: ");
                                int choice3 = scanner.nextInt();
                                switch (choice3) {
                                    case 1: {
                                        accountNumber = accounts.open_account(email);
                                        System.out.println("Account created successfully.");
                                        System.out.println("Your account number is: " + accountNumber);
                                        break;
                                    }
                                    case 2:
                                        return;
                                }

                            }

                        }
                        break;
                    }
                    case 3: {
                        exit();
                        return;
                    }
                    default:
                        System.out.println("Enter Valid choice");
                        break;

                }

            }

        } catch (SQLException | RuntimeException e) {
            System.out.println(e.getMessage());
        }

    }

    public static void exit() {
        System.out.print("\nEXITING SYSTEM");
        int i = 5;
        while (i != 0) {
            System.out.print(".");
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            i--;
        }
        System.out.println("\nTHANK YOU FOR USING BANKING SYSTEM.");

    }
}