package com.example.guessnumgame;

/* Import is used to include classes, interfaces,
 or other elements defined in other packages into your own class file.
 */
import javafx.application.Application;
import javafx.geometry.Insets; //It is used to define the padding or margin of the text.
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*; // VBox,HBox,GridPane,StackPane,BorderPane contains layout classes such as Pane.
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import java.util.ArrayList;
import java.util.Random;
import java.util.Map; //Sometimes Map is used to store control data or user options.
import java.util.Stack;


public class RandomAndGuess extends Application {

    private int randomNumber;
    private int timeLeft = 40;
    private int totalScore = 0;
    private int currentPoint = 100;
    private Timeline timeline;
    private ArrayList<Integer> guesses = new ArrayList<>();
    private boolean hintUsed1 = false;
    private UserManager userManager;
    private UserAuth userAuth;

    // UI Elements
    private Label totalScoreLabel = new Label("Total Score = 0");
    private Label currentPointLabel = new Label("Point = 0");
    private GridPane guessesContainer = new GridPane(); // Sets the forecast labels side by side.
    private Label statusBubble = new Label("");
    private Label timeLabel = new Label("Time: 40");
    private TextField inputField = new TextField();
    private Button hintButton = new Button("HINT");
    private Button highScore = new Button("OTHER SCORES");
    private Stack<Scene> sceneHistory = new Stack<>(); //It keeps previous scenes (screens) so that the user can return to the previous screen with the "back" button.


    @Override
    public void start(Stage primaryStage) {
        userManager = new UserManager();
        userAuth = new UserAuth(primaryStage, userManager, () -> {
            totalScore = userManager.getCurrentUser().getHighScore();
            showIntroScreen(primaryStage);
        });
        userAuth.showLogin(); // Manages the user login and registration screen.
    }

    // for style.
    private Button createStyledButton(String text, String baseColor) {
        Button button = new Button(text);
        String style = String.format("-fx-font-size: 18px;" + " -fx-padding: 10 20;"+
                        "-fx-font-family: 'Arial';"+ " -fx-background-color: %s;"+
                        " -fx-text-fill: %s;",
                baseColor, baseColor.equals("#E85FAD") ? "#000000" : "white");
        button.setStyle(style);

        // hover effect.
        button.setOnMouseEntered(e -> button.setStyle(style.replace(baseColor,
                baseColor.equals("#E85FAD") ? "#E987BF" : "#666666")));
        button.setOnMouseExited(e -> button.setStyle(style.replace(baseColor,
                baseColor.equals("#E85FAD") ? "#E85FAD" : "#444444")));

        return button;
    }

    private void showIntroScreen(Stage stage) {
        sceneHistory.push(stage.getScene()); // Saves the scene for a comeback.
        Label gameTitle = new Label("GUESS THE NUMBER");
        gameTitle.setStyle("-fx-font-size: 45px; -fx-font-weight: bold; -fx-font-family: 'Algerian'; -fx-text-fill: #000000;");

        Button playButton = createStyledButton("PLAY", "#E85FAD");
        Button scoresButton = createStyledButton("OTHER SCORES", "#555555");
        Button logoutButton = createStyledButton("LOG OUT", "#555555");

        playButton.setOnAction(e -> showStartScreen(stage));
        scoresButton.setOnAction(e -> showHighScores(stage));
        logoutButton.setOnAction(e -> {
            userManager.logout();
            userAuth.showLogin();
        });

        // Animation for the play button.(large-small)
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(1.2), playButton);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.08);
        pulse.setToY(1.08);
        pulse.setCycleCount(ScaleTransition.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();


        VBox buttonBox = new VBox(15); //With 15 px space.
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(playButton, scoresButton, logoutButton);

        VBox layout = new VBox(40, gameTitle, buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FFBAEA;");
        
        Scene scene = new Scene(layout, 800, 500);
        stage.setScene(scene);
        stage.setTitle("GUESS THE NUMBER");
        stage.show();
    }


    private void showHighScores(Stage stage) {
        sceneHistory.push(stage.getScene());
        VBox scoresLayout = new VBox(20);
        scoresLayout.setAlignment(Pos.CENTER);
        scoresLayout.setPadding(new Insets(20));
        scoresLayout.setStyle("-fx-background-color: #FFBAEA;");

        Label titleLabel = new Label("OTHER SCORES");
        titleLabel.setStyle("-fx-font-size: 35px; -fx-font-weight: bold; -fx-font-family: 'Arial'; -fx-text-fill: #000000;");

        // About scores.
        VBox scoresBox = new VBox(10);
        scoresBox.setStyle("-fx-background-color: #E85FAD; -fx-padding: 20; -fx-background-radius: 10;");
        scoresBox.setAlignment(Pos.CENTER);

        // Used to display the scoreboard.
        Map<String, Integer> highScores = userManager.getAllHighScores();
        for (Map.Entry<String, Integer> score : highScores.entrySet()) {
            Label scoreLabel = new Label(score.getKey() + ": " + score.getValue());
            scoreLabel.setStyle("-fx-font-size: 18px; -fx-font-family: 'Arial'; -fx-text-fill: #000000;");
            scoresBox.getChildren().add(scoreLabel);
        }

        // When we click the BACK button, we return to the previous page we entered.
        Button backButton = createStyledButton("BACK", "#555555");
        backButton.setOnAction(e ->  {
            if (!sceneHistory.isEmpty()) {
                stage.setScene(sceneHistory.pop());
            }
        });

        scoresLayout.getChildren().addAll(titleLabel, scoresBox, backButton);

        Scene scene = new Scene(scoresLayout, 800, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void showStartScreen(Stage stage) {
        sceneHistory.push(stage.getScene());
        totalScoreLabel.setText("Total Score = " + totalScore);

        Label welcome = new Label("WELCOME!");
        welcome.setStyle("-fx-font-size: 28px; -fx-font-family: 'Cooper Black'; -fx-background-color: #F0F0F2; -fx-padding: 15;"+
                " -fx-border-radius: 15;");
        welcome.setTextFill(Color.INDIANRED); // Starting color.

        // A Timeline is being created that will change the colors sequentially.
        Color[] colors = { Color.DEEPPINK, Color.PURPLE, Color.LIGHTYELLOW };

        Timeline colorTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> welcome.setTextFill(colors[0])),
                new KeyFrame(Duration.seconds(0.5), e -> welcome.setTextFill(colors[1])),
                new KeyFrame(Duration.seconds(1.0), e -> welcome.setTextFill(colors[2]))
        );
        // Infinite loop for welcome label's color.
        colorTimeline.setCycleCount(Timeline.INDEFINITE);
        colorTimeline.play();


        Label scoreTitleLabel = new Label("Total Score:");
        totalScoreLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: black;" +
                        "-fx-background-color: #64B5F6;" +
                        "-fx-padding: 5;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-font-family: 'Georgia';" );

        Label info = new Label("""
                                             Hello!
                    I kept a number between 0 and 100.
                         CAN YOU GUESS THIS NUMBER?
                    Please write a number and let's try it.
            
                               Your Time: 40 seconds.
            
              When the game starts, you will have 100 points.
              You will lose 5 points for each wrong guess.
              If you use a hint, you will lose 10 points.
            """);
        info.setStyle("-fx-font-size: 14px; -fx-font-size: 15px; -fx-font-family: 'Georgia'; -fx-background-color: #64B5F6; -fx-padding: 20;");

        Button startButton = new Button("START");
        startButton.setStyle("-fx-background-color: #555555;-fx-font-family: 'Arial'; -fx-text-fill: white; -fx-font-size: 20px;");
        startButton.setOnAction(e -> showCountdownScreen(stage)); // When click on this button, the game start.

        // Changes the color of the button according to the mouse movement.
        startButton.setOnMouseEntered(e -> startButton.setStyle("-fx-background-color: #666666; -fx-font-size: 20px; -fx-font-family: 'Arial'; -fx-text-fill: white;"));
        startButton.setOnMouseExited(e -> startButton.setStyle("-fx-background-color: #444444;-fx-font-family: 'Arial'; -fx-text-fill: white; -fx-font-size: 20px;"));


        VBox layout = new VBox(20, totalScoreLabel, welcome, info, startButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #bbdefb;");

        Scene scene = new Scene(layout, 800, 500);
        stage.setScene(scene);
        stage.setTitle("GUESS THE NUMBER");
        stage.show();
    }

    private void showCountdownScreen(Stage stage) {
        sceneHistory.push(stage.getScene());
        // It is about the hint button. It makes active this button when start the new game.
        hintButton.setDisable(false); //active
        hintButton.setVisible(true); //visible
        hintButton.setOpacity(1.0); //completely opaque

        highScore.setDisable(true); //active
        highScore.setVisible(false);
        highScore.setOpacity(0.0);

        // it shows countdown screen.
        Label countdownLabel = new Label("3");
        countdownLabel.setStyle("-fx-font-size: 72px; -fx-font-weight: bold; -fx-text-fill: white;");

        // (StackPane) It stacks the elements added to it and centers them.
        StackPane countdownLayout = new StackPane(countdownLabel);
        countdownLayout.setStyle("-fx-background-color: #1976D2;");
        Scene countdownScene = new Scene(countdownLayout, 800, 500);
        stage.setScene(countdownScene);

        // The scene has different colors here.It is like an animation.
        Timeline countdown = new Timeline(
                createKeyFrame(0, "3", countdownLabel, countdownLayout, "#4DF068"),
                createKeyFrame(1, "2", countdownLabel, countdownLayout, "#FAEA11"),
                createKeyFrame(2, "1", countdownLabel, countdownLayout, "#F57C00"),
                createKeyFrame(3, "GO!", countdownLabel, countdownLayout, "#FF2142"),
                new KeyFrame(Duration.seconds(4), e -> showGameScreen(stage))
        );
        countdown.play();
    }

    // Animation for countdown.
    private KeyFrame createKeyFrame(int second, String text, Label label, StackPane layout, String bgColor) {
        return new KeyFrame(Duration.seconds(second), e -> {
            label.setText(text);
            layout.setStyle("-fx-background-color: " + bgColor + ";");

            // ANIMATION: large - small
            label.setScaleX(1); //normal scale
            label.setScaleY(1);
            ScaleTransition scale = new ScaleTransition(Duration.millis(400), label);
            scale.setFromX(0.5); //small scale
            scale.setFromY(0.5);
            scale.setToX(1.2); //large scale
            scale.setToY(1.2);
            scale.setAutoReverse(true);
            scale.setCycleCount(2); // animation runs 2 times.
            scale.play();
        });
    }
    private void showGameScreen(Stage stage) {
        sceneHistory.push(stage.getScene());
        resetGame();

        totalScoreLabel.setText("Total SCORE = " + totalScore);
        totalScoreLabel.setStyle("-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"+
                "-fx-font-family: 'Georgia';" +
                " -fx-text-fill: white; " +
                "-fx-background-color: #1976D2;" +
                " -fx-padding: 10; -fx-border-radius: 10;" +
                " -fx-background-radius: 10;");

        Label guessLabel = new Label("Write your prediction.");
        guessLabel.setStyle("-fx-background-color: white;-fx-font-family: 'Georgia'; -fx-text-fill: black; -fx-padding: 5 10;");

        // The size is taken under control with these codes.
        inputField.setPromptText("Enter number");
        inputField.setPrefWidth(120);
        inputField.setMaxWidth(120);

        inputField.setOnAction(e -> checkGuess());

        /* GridPane is a layout code that organizes
        elements in rows and columns. We use it for "all predictions" part.
         */
        GridPane guessesGrid = new GridPane();
        guessesGrid.setHgap(10); // Horizontal gap.
        guessesGrid.setVgap(10); // Vertical gap.
        guessesGrid.setPadding(new Insets(10));
        guessesGrid.setAlignment(Pos.CENTER);

        this.guessesContainer = guessesGrid;

        Label allPredictionsTitle = new Label("- All Predictions -");
        allPredictionsTitle.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-background-color: #BCBCBC; " +
                        "-fx-padding: 5px 10px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );

        HBox predictionsTitleBox = new HBox(allPredictionsTitle);
        predictionsTitleBox.setAlignment(Pos.TOP_RIGHT);
        predictionsTitleBox.setPadding(new Insets(10, 20, 0, 0));

        VBox predictionsBox = new VBox(predictionsTitleBox, guessesGrid);
        predictionsBox.setAlignment(Pos.TOP_RIGHT);
        predictionsBox.setPadding(new Insets(10, 20, 0, 0));

        statusBubble.setStyle("-fx-background-color: #E85FAD; -fx-padding: 10; -fx-font-size: 18px; -fx-font-family: 'Georgia';");
        statusBubble.setVisible(false);

        Button newGameBtn = new Button("NEW GAME");
        newGameBtn.setOnAction(e -> showCountdownScreen(stage));
        newGameBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        newGameBtn.setOnMouseEntered(e -> newGameBtn.setStyle("-fx-background-color: #666666; -fx-text-fill: white;"));
        newGameBtn.setOnMouseExited(e -> newGameBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;"));

        hintButton.setOnAction(e -> giveHint());
        hintButton.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        hintButton.setOnMouseEntered(e -> hintButton.setStyle("-fx-background-color: #666666; -fx-text-fill: white;"));
        hintButton.setOnMouseExited(e -> hintButton.setStyle("-fx-background-color: #444444; -fx-text-fill: white;"));

        Button homeButton = new Button("HOME");
        homeButton.setOnAction(e -> showIntroScreen(stage));
        homeButton.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        homeButton.setOnMouseEntered(e -> homeButton.setStyle("-fx-background-color: #666666; -fx-text-fill: white;"));
        homeButton.setOnMouseExited(e -> homeButton.setStyle("-fx-background-color: #444444; -fx-text-fill: white;"));

        highScore.setOnAction(e -> showHighScores(stage) );
        highScore.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        highScore.setOnMouseEntered(e -> highScore.setStyle("-fx-background-color: #666666; -fx-text-fill: white;"));
        highScore.setOnMouseExited(e -> highScore.setStyle("-fx-background-color: #444444; -fx-text-fill: white;"));


        // It align horizontal these buttons.
        HBox buttonBox = new HBox(20,highScore, newGameBtn, homeButton, hintButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 40, 20, 40));

        timeLabel.setStyle("-fx-font-size: 20px;-fx-font-family: 'Georgia'; -fx-font-weight: bold;");

        HBox totalBox = new HBox(10, totalScoreLabel);
        totalBox.setAlignment(Pos.BASELINE_LEFT);
        totalBox.setPadding(new Insets(0, 0, 0, 20));

        // It align the prediction area as a vertical on the center.
        VBox predictionArea = new VBox(15,
                timeLabel,
                guessLabel,
                inputField,
                statusBubble,
                buttonBox
        );
        predictionArea.setAlignment(Pos.CENTER);

        VBox centerBox = new VBox(20,  // with 20 pixels space.
                currentPointLabel,
                totalBox,
                predictionArea
        );
        centerBox.setAlignment(Pos.CENTER);

        // BorderPane has 5 main region: top, bottom,left,right,center.
        BorderPane root = new BorderPane();
        root.setCenter(centerBox);
        root.setStyle("-fx-background-color: #bbdefb;");
        root.setRight(predictionsBox);

        //After loading the page, the timer starts.
        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);
        stage.show();

        startTimer();
    }


    //When a player starts a new game, everything is reset.
    private void resetGame() {
        randomNumber = new Random().nextInt(101); // It choose a new number.
        currentPoint = 100;
        currentPointLabel.setStyle("-fx-font-size: 16px; -fx-font-family: 'Georgia';  -fx-font-weight: bold;"+
                " -fx-text-fill: white; -fx-background-color: #1976D2; -fx-padding: 10; -fx-border-radius: 10; -fx-background-radius: 10;");
        timeLeft = 40;
        guesses.clear();
        hintUsed1 = false;
        currentPointLabel.setText("Point = 100");
        totalScoreLabel.setText("Total SCORE = " + totalScore);
        timeLabel.setText(" Time =  40");
        timeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: 'Georgia';"+
                " -fx-text-fill: white; -fx-background-color: #1976D2; -fx-padding: 10; -fx-border-radius: 10; -fx-background-radius: 10;");
        statusBubble.setVisible(false); // If there is a result (won/lost etc.) bubble, it will be hidden.
        currentPointLabel.setVisible(true); // The score label appears.
        currentPointLabel.setOpacity(1.0); // The score label becomes opaque.
        guessesContainer.getChildren().clear(); // It clears previous predicitions.
        inputField.setDisable(false); // The enter bar appears.
        inputField.setOpacity(1.0);
    }


    /*It starts the timer when you start the
     game or reset it and automatically keeps track of the time.
     */
    private void startTimer() {
        if (timeline != null) timeline.stop();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            timeLabel.setText("Time: " + timeLeft);
            if (timeLeft <= 0) {
                timeline.stop();
                endGame(false);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);// The Key Frame continues until time is stopped.
        timeline.play();
    }

    // Manages the flow of the game.
    /* The error is prevented against incorrect
    inputs (letters, etc.) with try-catch. */
    private void checkGuess() {
        try {
            int guess = Integer.parseInt(inputField.getText());

            /* Does not accept predictions outside
            of 0–100. The user is warned
             and the input field is cleared.
             */
            if (guess < 0 || guess > 100) {
                statusBubble.setText("Please enter a number between 0 and 100.");
                statusBubble.setVisible(true);
                inputField.clear();
                return;
            }

            guesses.add(guess); // for prediction history.

            // It creates bubbles for prediction history.
            Label guessBubble = new Label(String.valueOf(guess));
            guessBubble.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-border-color: #444444;" +
                            "-fx-border-radius: 5px;" +
                            "-fx-background-radius: 5px;" +
                            "-fx-padding: 5 10;" +
                            "-fx-font-family: 'Georgia';"
            );

            // It consists of a three-row grid with three columns.
            int index = guessesContainer.getChildren().size();
            int col = index % 3;
            int row = index / 3;
            guessesContainer.add(guessBubble, col, row); // GridPane'e yerleştir
            inputField.clear();

            if (guess == randomNumber) {
                timeline.stop();
                endGame(true);
            } else {
                currentPoint -= 5;
                currentPointLabel.setText("Point = " + currentPoint);
                if (guess > randomNumber) {
                    statusBubble.setText("Try smaller!");
                } else {
                    statusBubble.setText("Try bigger!");
                }
                statusBubble.setVisible(true);
            }
        } catch (NumberFormatException e) {
            statusBubble.setText("Invalid number!");
            statusBubble.setVisible(true);
        }
    }

    // for the hint.
    private void giveHint() {
        if (!hintUsed1) {
            String parity = (randomNumber % 2 == 0) ? "even" : "odd";
            statusBubble.setText("Hint: The number is " + parity);
            currentPoint -= 10;
            hintUsed1 = true;
        } else {
            statusBubble.setText("No hints left.");
        }

        currentPointLabel.setText("Point = " + currentPoint);
        statusBubble.setVisible(true);
    }



    private void endGame(boolean won) {
        if (timeline != null) {
            timeline.stop();
        }

        // Enable high score button.
        FadeTransition fadeHighScore = new FadeTransition(Duration.millis(600), highScore);
        fadeHighScore.setFromValue(0.0);
        fadeHighScore.setToValue(1.0);
        fadeHighScore.setOnFinished(ev -> {
            highScore.setVisible(true);
            highScore.setDisable(false);
        });
        fadeHighScore.play();

        // Disable hint button
        FadeTransition fadeHint = new FadeTransition(Duration.millis(600), hintButton);
        fadeHint.setFromValue(1.0);
        fadeHint.setToValue(0.0);
        fadeHint.setOnFinished(ev -> {
            hintButton.setVisible(false);
            hintButton.setDisable(true);
        });
        fadeHint.play();


        // Update scores and show message.
        String message = won ? "CONGRATULATIONS!" : "YOU LOST!\nNumber was: " + randomNumber;
        statusBubble.setText(message);
        statusBubble.setVisible(true);

        if (won) {
            handleWin();
        } else {
            handleLoss();
        }

    }

    // When we won,this code runs.
    private void handleWin() {
        int scoreChange = currentPoint;
        totalScore += scoreChange;
        userManager.updateCurrentUserScore(totalScore); // for user data registration.

        totalScoreLabel.setText("Total SCORE: " + totalScore);

        statusBubble.setStyle("-fx-background-color: #6CFF52; -fx-text-fill: black; -fx-padding: 15;"+
                " -fx-font-size: 24px; -fx-font-family: 'Georgia'; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Win animation for statusBubble.
        ScaleTransition scaleBubble = new ScaleTransition(Duration.millis(700), statusBubble);
        scaleBubble.setFromX(0.8);
        scaleBubble.setFromY(0.8);
        scaleBubble.setToX(1.3);
        scaleBubble.setToY(1.3);
        scaleBubble.setCycleCount(6);
        scaleBubble.setAutoReverse(true);
        scaleBubble.play();

        animateScore(true);
    }

    // When we lost, this code runs.
    private void handleLoss() {
        int scoreChange = currentPoint;
        if (scoreChange < 0) {
            totalScore += scoreChange * 2; // If your point is negative, it takes place.
        } else {
            totalScore -= scoreChange;
        }
        userManager.updateCurrentUserScore(totalScore); // for user data registration.

        totalScoreLabel.setText("Total SCORE: " + totalScore);

        statusBubble.setStyle("-fx-background-color: #FF3B30; -fx-text-fill: black; -fx-padding: 15;"+
                " -fx-font-size: 20px; -fx-font-family: 'Georgia'; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Loss animation
        ScaleTransition shrinkBubble = new ScaleTransition(Duration.millis(700), statusBubble);
        shrinkBubble.setFromX(1.0);
        shrinkBubble.setFromY(1.0);
        shrinkBubble.setToX(0.7);
        shrinkBubble.setToY(0.7);
        shrinkBubble.setCycleCount(4);
        shrinkBubble.play();

        animateScore(false);
    }

    private void animateScore(boolean won) {
        String color = won ? "#6CFF52" : "#FF3B30";
        totalScoreLabel.setStyle(String.format(
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: 'Georgia'; -fx-text-fill: black; -fx-background-color: %s; -fx-padding: 10; -fx-border-radius: 10; -fx-background-radius: 10;",
                color
        ));


        // Animated score change
        int scoreChange = currentPoint;
        int oldScore;
        if (won) {
            oldScore = totalScore - scoreChange;
        } else {
            oldScore = (scoreChange < 0) ? totalScore - scoreChange * 2 : totalScore + scoreChange;
        }

        int steps = 30;
        int durationMillis = 700;
        Timeline scoreAnimation = new Timeline();

        for (int i = 0; i <= steps; i++) {
            final int step = i;
            KeyFrame frame = new KeyFrame(Duration.millis((durationMillis / steps) * step), e -> {
                int interpolated = oldScore + (totalScore - oldScore) * step / steps; // Account the score.
                totalScoreLabel.setText("Total SCORE: " + interpolated);
            });
            scoreAnimation.getKeyFrames().add(frame);
        }

        scoreAnimation.play();


        FadeTransition fadeOutPoint = new FadeTransition(Duration.millis(1000), currentPointLabel);
        fadeOutPoint.setFromValue(1.0);
        fadeOutPoint.setToValue(0.0);
        fadeOutPoint.setOnFinished(e -> currentPointLabel.setVisible(false));
        fadeOutPoint.play();


        // Makes the guess box inactive and semi-transparent when the game is over.
        inputField.setOnKeyPressed(null);
        inputField.setDisable(true);
        inputField.setOpacity(0.3);

    }

    // Starts the JavaFX application.
    public static void main(String[] args) {
        launch(args);
    }
}