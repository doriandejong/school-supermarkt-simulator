module com.example.schoolsupermarktsimulator {
    requires javafx.controls;
    requires org.junit.jupiter.api;

    // Geef JUnit reflectie-toegang tot de shelves package:
    opens com.example.schoolsupermarktsimulator.objects.shelves to org.junit.platform.commons;

    exports com.example.schoolsupermarktsimulator;
}