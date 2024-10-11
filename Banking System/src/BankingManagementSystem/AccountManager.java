package BankingManagementSystem;

import java.sql.*;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Scanner;

public class AccountManager {
    private Connection connection;
    private Scanner scanner;

    public AccountManager(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void credit_money(String email) {
        System.out.print("Enter amount you want to credit: ");
        double creditAmount = scanner.nextDouble();
        System.out.print("Enter security pin: ");
        long securityPin = scanner.nextLong();
        try {
            connection.setAutoCommit(false);
            String query1 = "UPDATE accounts SET balance = balance  + ? WHERE email = ? AND security_pin = ? ; ";
            PreparedStatement preparedStatement = connection.prepareStatement(query1);
            preparedStatement.setDouble(1, creditAmount);
            preparedStatement.setString(2, email);
            preparedStatement.setLong(3, securityPin);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                System.out.println("\n\u20B9" + creditAmount + " credited to your account successfully. \n");
            } else {
                connection.rollback();
                System.out.println("Transaction failed!!!");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        try {
            String query2 = "INSERT INTO transaction_history(account_number,credit_amount,balance) VALUES (?,?,?);";
            Accounts accounts = new Accounts(connection, scanner);
            long account_number = accounts.getAccount_number(email, securityPin);
            double updated_balance = accounts.get_balance(email, securityPin);
            PreparedStatement preparedStatement = connection.prepareStatement(query2);
            preparedStatement.setDouble(1, account_number);
            preparedStatement.setDouble(2, creditAmount);
            preparedStatement.setDouble(3, updated_balance);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public void debit_money(String email, double debitAmount, long securityPin) {
        if (check_balance(email, securityPin) >= debitAmount) {
            String query = "UPDATE accounts SET balance = balance - ? WHERE email = ? AND security_pin = ? ; ";
            try {
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setDouble(1, debitAmount);
                preparedStatement.setString(2, email);
                preparedStatement.setLong(3, securityPin);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    connection.commit();
                    System.out.println("\n\u20B9" + debitAmount + " debited from your account successfully. \n");
                } else {
                    connection.rollback();
                    System.out.println("\nTransaction failed!!!\n");
                }

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            try {
                String query2 = "INSERT INTO transaction_history(account_number,debit_amount,balance) VALUES (?,?,?);";
                Accounts accounts = new Accounts(connection, scanner);
                long account_number = accounts.getAccount_number(email, securityPin);
                double updated_balance = accounts.get_balance(email, securityPin);
                PreparedStatement preparedStatement = connection.prepareStatement(query2);
                preparedStatement.setDouble(1, account_number);
                preparedStatement.setDouble(2, debitAmount);
                preparedStatement.setDouble(3, updated_balance);
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nInsufficient balance!!!\n");
        }
    }

    public void transfer_money(String email) {
        System.out.print("Enter recivers account number: ");
        long reciversAccount = scanner.nextLong();
        Accounts accounts = new Accounts(connection, scanner);
        boolean account_exists = accounts.account_exists(reciversAccount);
        if (account_exists) {
            System.out.print("Enter amount you want to transfer: ");
            double transferAmount = scanner.nextDouble();
            System.out.print("Enter security pin: ");
            long securityPin = scanner.nextLong();
            if (check_balance(email, securityPin) >= transferAmount) {
                // withdraw the amount from senders account
                debit_money(email, transferAmount, securityPin);
                try {
                    // send money to recivers account
                    connection.setAutoCommit(false);
                    String query = "UPDATE accounts SET balance = balance + ? WHERE account_number= ? ;";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setDouble(1, transferAmount);
                    preparedStatement.setLong(2, reciversAccount);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        connection.commit();
                        System.out.println("TRANSACTION SUCCESFUL : \u20B9" + transferAmount + " has been transferd to " + reciversAccount + "\n");
                    } else {
                        connection.rollback();
                        System.out.println("\nTRANSACTION FAILED!!!\n");
                    }

                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                try {
                    String query2 = "INSERT INTO transaction_history(account_number,credit_amount,balance) VALUES (?,?,?);";
                    double updated_balance = accounts.getBalance_byAccount(reciversAccount);
                    PreparedStatement preparedStatement = connection.prepareStatement(query2);
                    preparedStatement.setDouble(1, reciversAccount);
                    preparedStatement.setDouble(2, transferAmount);
                    preparedStatement.setDouble(3, updated_balance);
                    preparedStatement.executeUpdate();

                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

            } else {
                System.out.println("\nTRANSACTION FAILED : Insufficient balance!!!\n");
            }
        } else {
            System.out.println("\nINVALID ACCOUNT NUMBER!!!\n");
        }
    }

    public double check_balance(String email, long securityPin) {
        String query = "SELECT balance FROM accounts WHERE email = ? AND security_pin = ? ; ";
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setLong(2, securityPin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                connection.commit();
                return resultSet.getDouble("balance");
            } else {
                connection.rollback();
                System.out.println("Invalid Pin!!!");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new RuntimeException("Failed to check balance");
    }

    public void transaction_history(String email) {
        System.out.print("Enter security Pin: ");
        long security_pin = scanner.nextLong();
        Accounts accounts = new Accounts(connection, scanner);
        long account_number = accounts.getAccount_number(email, security_pin);
        try {
            String query = " SELECT * FROM transaction_history WHERE account_number= ? ;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, account_number);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("\n+-------+----------------+--------------+---------------+-----------+-------------------------+");
            System.out.println("| Sr.no | account number | debit amount | credit amount | balance   | date                    |");
            System.out.println("+-------+----------------+--------------+---------------+-----------+-------------------------+");
            while (resultSet.next()) {
                int srNo = resultSet.getInt("Sr_no");
                long accountNumber = resultSet.getLong("account_number");
                long debitAmount = resultSet.getLong("debit_amount");
                long creditAmount = resultSet.getLong("credit_amount");
                long balance = resultSet.getLong("balance");
                Timestamp date = resultSet.getTimestamp("date");
                System.out.printf("| %-5d | %-14d | %-12d | %-13d | %-9d | %-23s |\n", srNo, accountNumber, debitAmount, creditAmount, balance, date);
            }
            System.out.println("+-------+----------------+--------------+---------------+-----------+-------------------------+\n");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


}
