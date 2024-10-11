package BankingManagementSystem;

import com.mysql.cj.conf.PropertyDefinitions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Accounts {
    private Connection connection;
    private Scanner scanner;

    public Accounts(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public long open_account(String email) {
        if (!account_exists(email)) {
            scanner.nextLine();
            System.out.print("Enter full name: ");
            String full_name = scanner.nextLine();
            System.out.println("Enter " + "\u20B9" + 2000 + " or more to open account: ");
            long balance = scanner.nextLong();
            if (balance < 2000) {
                System.out.println("Please add minimum  " + "\u20B9" + 2000 + " to open account ");
                throw new RuntimeException("Please add minimum " + "\u20B9" + 2000 + " to open account ");
            }
            System.out.print("Enter security pin : ");
            long security_pin = scanner.nextLong();
            long account_number = generateAccount_number();

            String query1 = "INSERT INTO accounts (account_number,full_name,email,balance,security_pin) VALUES (?,?,?,?,?);";
            String query2 = "INSERT INTO transaction_history (account_number,credit_amount,balance) VALUES (?,?,?)  ;";

            try {
                connection.setAutoCommit(false);
                PreparedStatement addToAccount = connection.prepareStatement(query1);

                addToAccount.setLong(1, account_number);
                addToAccount.setString(2, full_name);
                addToAccount.setString(3, email);
                addToAccount.setLong(4, balance);
                addToAccount.setLong(5, security_pin);
                int rowsAffected1 = addToAccount.executeUpdate();

                PreparedStatement addToTransaction = connection.prepareStatement(query2);
                addToTransaction.setLong(1, account_number);
                addToTransaction.setLong(2, balance);
                addToTransaction.setLong(3, balance);
                int rowsAffected2 = addToTransaction.executeUpdate();

                if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                    connection.commit();
                    return account_number;
                } else {
                    connection.rollback();
                    throw new RuntimeException("Account creation failed!!!");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        throw new RuntimeException("Account already Exists");
    }

    public long getAccount_number(String email, long security_Pin) {
        String query = "SELECT account_number FROM accounts WHERE email = ? AND security_pin = ? ";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setLong(2, security_Pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                long account_number = resultSet.getLong("account_number");
                return account_number;
            } else {
                System.out.println("\nINVALID PIN!!!\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new RuntimeException("Invalid pin");
    }

    public double get_balance(String email, long security_Pin) {
        String query = "SELECT balance FROM accounts WHERE email = ? AND security_pin = ? ";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setLong(2, security_Pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("balance");
            } else {
                System.out.println("\nINVALID PIN!!!\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new RuntimeException("Invalid pin");
    }

    public double getBalance_byAccount(long account_number) {
        String query = "SELECT balance FROM accounts WHERE account_number = ? ;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, account_number);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("balance");
            } else {
                System.out.println("\nINVALID PIN!!!\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new RuntimeException("Invalid pin");
    }

    public long generateAccount_number() {
        String query = "SELECT account_number FROM accounts ORDER BY account_number DESC LIMIT 1";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                long last_account_number = resultSet.getLong("account_number");
                return last_account_number + 1;
            } else {
                throw new RuntimeException("Failed to generate account number");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new RuntimeException("Failed to generate account number");
    }

    public boolean account_exists(String email) {
        String query = "SELECT * FROM accounts WHERE email=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean account_exists(long account_number) {
        String query = "SELECT * FROM accounts WHERE account_number=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, account_number);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public void change_securityPin(String email) {
        scanner.nextLine();
        System.out.print("Enter your current security pin: ");
        String currentSecurityPin = scanner.nextLine();
        System.out.print("Enter your new security pin: ");
        String newSecurityPin = scanner.nextLine();

        boolean account_exists = account_exists(email);
        if (account_exists) {
            try {
                String query = "UPDATE accounts SET security_pin = ?  where email = ? ";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, newSecurityPin);
                preparedStatement.setString(2, email);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("\nsecurity Pin has been changed successfully.\n");
                } else {
                    System.out.println("\nError while changing security Pin!!!\n");
                }


            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nInvalid Email or Security Pin\n");
        }
    }

    public boolean close_account(String email) {
        scanner.nextLine();
        System.out.print("Enter Account no : ");
        long accountNumber = scanner.nextLong();
        System.out.print("Enter Security Pin: ");
        long securityPin = scanner.nextLong();
        System.out.print("\nClosing account will close your account permanently, Are you sure (Y|N) :  \n");
        String decision = scanner.next();
        if (decision.equalsIgnoreCase("Y")) {
            boolean account_exists = account_exists(email);
            if (account_exists) {
                try {
                    connection.setAutoCommit(false);
                    String query1 = "DELETE FROM accounts WHERE account_number = ? AND security_pin = ? ; ";
                    PreparedStatement deleteFromAccounts = connection.prepareStatement(query1);
                    deleteFromAccounts.setLong(1, accountNumber);
                    deleteFromAccounts.setLong(2, securityPin);
                    int rowsAffected1 = deleteFromAccounts.executeUpdate();


                    String query2 = "DELETE FROM transaction_history WHERE account_number = ? ; ";
                    PreparedStatement deleteFromTransactionHistory = connection.prepareStatement(query2);
                    deleteFromTransactionHistory.setLong(1, accountNumber);
                    int rowsAffected2 = deleteFromTransactionHistory.executeUpdate();


                    if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                        connection.commit();
                        System.out.println("\nACCOUNT CLOSED PERMANENTLY\n");
                        return true;
                    } else {
                        connection.rollback();
                        System.out.println("\nError while closing account!!!\n");
                        return false;
                    }


                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("\nInvalid Account no OR Security pin\n");
            }
        }
        return false;
    }
}
