package hangman.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import hangman.Model.DatabaseManager;
import hangman.Model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;
import java.util.*;

import static java.time.temporal.ChronoUnit.SECONDS;

public class GameController implements Initializable {
    String word;
    Random rand = new Random();

    List<Label> labels = new ArrayList<>();

    LocalTime startTime;
    LocalTime endTime;

    int wrongAnswers = 0;
    int trueAnswers = 0;

    @FXML
    private AnchorPane mainPanel;

    @FXML
    private AnchorPane buttonsPanel;

    @FXML
    private Line bodyShape;

    @FXML
    private Circle headShape;

    @FXML
    private Line leftArmShape;

    @FXML
    private Line leftLegShape;

    @FXML
    private Line rightArmShape;

    @FXML
    private Line rightLegShape;

    @FXML
    private HBox wordBox;

    @FXML
    private Label winLabel;

    @FXML
    private Label loseLabel;

    public static User user;
    public String getWord() {
        try {
            URL url = new URL("https://api.api-ninjas.com/v1/randomword");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("X-Api-Key", "uvpIFPXS7SEX6mroJq8uxPyUf5q9vyWhLxfv4D6U");
            InputStream responseStream = connection.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseStream);

            System.out.println(root.path("word").asText());
            return root.path("word").asText();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        word = getWord();

        for (int i = 0; i < word.length(); i++) {
            Label wordLabel = new Label(" - ");
            wordLabel.getStyleClass().add("letterBox");

            wordBox.getChildren().add(wordLabel);
            labels.add(wordLabel);
        }

        for (Node button : buttonsPanel.getChildren()) {
            Button btn = (Button) button;
            btn.setOnAction(event -> {
                try {
                    pressLetter(btn);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            });
        }

        startTime = LocalTime.now();
    }

    void pressLetter(Button button) throws IOException {
        char letter = button.getText().toLowerCase().charAt(0);
        if (word.contains(String.valueOf(letter))) {
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == letter) {
                    labels.get(i).setText(" " + letter + " ");
                    trueAnswers++;
                }
            }
        } else {
            wrongAnswers++;
            punishPlayer(wrongAnswers);
        }

        button.setDisable(true);

        if (trueAnswers == word.length()) {
            endGame(true);
        }
    }

    void punishPlayer(int wrongAnswers) throws IOException {
        if (wrongAnswers == 6) {
            endGame(false);
            return;
        }

        mainPanel.getChildren().get(wrongAnswers + 4).setVisible(true);
    }

    void endGame(boolean win) throws IOException {
        endTime = LocalTime.now();
        int time = (int) SECONDS.between(startTime, endTime);

        if (win) {
            winLabel.setVisible(true);
        } else {
            loseLabel.setVisible(true);
        }

        DatabaseManager.createGame(user.getUsername(), word, wrongAnswers, time, win);

        Stage gameStage = (Stage) buttonsPanel.getScene().getWindow();
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/hangman/leaderboard-view.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        gameStage.close();
    }
}