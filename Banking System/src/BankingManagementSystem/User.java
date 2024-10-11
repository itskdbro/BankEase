package BankingManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
    private Connection connection;
    private Scanner scanner;

    public User(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void register() {
        scanner.nextLine();
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        if (user_exists(email, password)) {
            System.out.println("\nUser already exists,please login !!! \n");
            return;
        }
        try {
            String query = "INSERT INTO user (full_name,email,password) VALUES(?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\nNew User Registration successfull.\n");
            } else {
                System.out.println("Failed to register new User");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String login() {
        scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        if (user_exists(email, password)) {
            String login_query = "SELECT * FROM User WHERE email =? AND password=?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(login_query);
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return email;
                } else {
                    return null;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nINVALID ACCOUNT!!!\n");
        }
        return null;
    }

    public boolean user_exists(String email, String password) {
        String userExists_query = "SELECT * FROM User WHERE email = ? AND password = ? ";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(userExists_query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
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

    public void change_email(String currentEmail) {
        scanner.nextLine();
        System.out.println("Enter your new email: ");
        String newEmail = scanner.nextLine();
        System.out.println("Enter your password: ");
        String password = scanner.nextLine();

        boolean user_exists = user_exists(currentEmail, password);
        if (user_exists) {
            try {
                String query1 = "UPDATE user SET email = ?  where email = ? ";
                String query2 = "UPDATE accounts SET email = ?  where email = ? ";
                PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
                preparedStatement1.setString(1, newEmail);
                preparedStatement1.setString(2, currentEmail);
                int rowsAffected1 = preparedStatement1.executeUpdate();

                PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                preparedStatement2.setString(1, newEmail);
                preparedStatement2.setString(1, currentEmail);
                int rowsAffected2 = preparedStatement1.executeUpdate();
                if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                    System.out.println("\nEmail has been changed successfully.\n");
                } else {
                    System.out.println("\nError while changing email!!!\n");
                }


            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nInvalid Email or Password\n");
        }
    }

    public void change_password(String email) {
        scanner.nextLine();
        System.out.print("Enter your current password: ");
        String currentPassword = scanner.nextLine();
        System.out.print("Enter your new password: ");
        String newPassword = scanner.nextLine();

        boolean user_exists = user_exists(email, currentPassword);
        if (user_exists) {
            try {
                String query = "UPDATE user SET password = ?  where email = ? ";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, newPassword);
                preparedStatement.setString(2, email);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("\nPassword has been changed successfully.\n");
                } else {
                    System.out.println("\nError while changing password!!!\n");
                }


            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nInvalid Email or Password\n");
        }
    }
}
