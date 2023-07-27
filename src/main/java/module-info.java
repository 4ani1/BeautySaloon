module com.example.deepthort {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;


    opens com.example.deepthort to javafx.fxml;
    exports com.example.deepthort;
}