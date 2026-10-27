module com.example.schoolsupermarktsimulator {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.junit.jupiter.api;

    // Geef JUnit reflectie-toegang tot de shelves package:
    opens com.example.schoolsupermarktsimulator.objects.shelves to org.junit.platform.commons;

    opens com.example.schoolsupermarktsimulator to javafx.fxml;
    exports com.example.schoolsupermarktsimulator;
}