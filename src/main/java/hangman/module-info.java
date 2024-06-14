module hangman {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;

    opens hangman to javafx.fxml;
    exports hangman;
    exports hangman.Model;
    opens hangman.Model to javafx.fxml;
}