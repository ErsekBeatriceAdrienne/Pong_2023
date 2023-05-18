module com.example.pong {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.datatransfer;
    requires java.desktop;
    requires javafx.media;
    requires slf4j.api;
    requires spring.boot.starter;

    opens com.example.pong to javafx.fxml;
    exports com.example.pong;
    exports com.example.pong.interfaces;
    opens com.example.pong.interfaces to javafx.fxml;
    exports com.example.pong.obejcts;
    opens com.example.pong.obejcts to javafx.fxml;
}