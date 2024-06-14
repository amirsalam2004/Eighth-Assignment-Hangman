module hangman {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    opens hangman to javafx.fxml;
    exports hangman;
    exports hangman.Controller;
    opens hangman.Controller to javafx.fxml;
    exports hangman.Model;
    opens hangman.Model to javafx.fxml;
}