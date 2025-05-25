module GuessNumGame {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.guessnumgame to javafx.fxml;
    exports com.example.guessnumgame;
}
