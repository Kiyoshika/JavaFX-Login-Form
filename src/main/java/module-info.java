module com.mycompany.loginform {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires bcrypt;

    opens com.mycompany.loginform to javafx.fxml;
    exports com.mycompany.loginform;
}
