package com.mycompany.loginform;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class App extends Application {

    private static Scene scene;
    private String username;
    private String password;
    
    private String db_admin_user = "sql9383962";
    private String db_admin_pass = "NX3NZRSXMT";

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"));
        stage.setScene(scene);
        stage.show();
        
        // UI components
        TextField usernameField = (TextField)scene.lookup("#usernameField");
        PasswordField passwordField = (PasswordField)scene.lookup("#passwordField");
        Button newuserButton = (Button)scene.lookup("#newuserButton");
        Button loginButton = (Button)scene.lookup("#loginButton");
        
        // button events
        newuserButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                username = usernameField.getText();
                password = passwordField.getText();
                
                try {
                    // setup connection
                    createUser();
                } catch (SQLException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                username = usernameField.getText();
                password = passwordField.getText();
                try {
                    loginUser();
                } catch (SQLException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
    }

    private void createUser() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://sql9.freemysqlhosting.net:3306/sql9383962", db_admin_user, db_admin_pass);
        // check if user exists (select username from user_table where username = 'example';)
        ResultSet fetchUsername = conn.createStatement().executeQuery("select username from user_table where username = '" + username + "';");
        if (fetchUsername.next() == false) {
            // if user doesn't exist, write user to database
            String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
            conn.createStatement().executeUpdate("insert into user_table (username, password) values ('" + username + "', '" + hashedPassword + "');");
            System.out.println("New user created!");
        } else {
            System.out.println("User already exists!");
        }
        conn.close();
    }
    
    private void loginUser() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://sql9.freemysqlhosting.net:3306/sql9383962", db_admin_user,db_admin_pass);
        // check if user exists (select username from user_table where username = 'example';)
        ResultSet fetchUsername = conn.createStatement().executeQuery("select username from user_table where username = '" + username + "';");
        
        if (fetchUsername.next() == false) {
            System.out.println("Invalid username");
        } else {
            // compare passwords
            ResultSet fetchHashedPassword = conn.createStatement().executeQuery("select password from user_table where username = '" + username + "';");
            String storedPassword = "";
            while (fetchHashedPassword.next()) {
                storedPassword = fetchHashedPassword.getString("password");
            }
            
            BCrypt.Result pwdCompare = BCrypt.verifyer().verify(password.toCharArray(), storedPassword);
            if (pwdCompare.verified) {
                System.out.println("Login successful!");
            } else {
                System.out.println("Invalid password!");
            }
        }
        conn.close();
    }
    
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}