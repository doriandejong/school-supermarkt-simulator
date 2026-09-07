module com.example.schoolsupermarktsimulator {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.schoolsupermarktsimulator to javafx.fxml;
    exports com.example.schoolsupermarktsimulator;
}