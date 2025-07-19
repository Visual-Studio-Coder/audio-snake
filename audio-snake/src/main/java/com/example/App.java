package com.example;

import java.io.IOException;
import java.lang.Math;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    private int score = 0;
    private Label scoreDisplay = new Label("Score: 0");
    private Label directionDisplay = new Label("Direction: UP"); // Initial direction
    private Line line = new Line(500, 0, 500, 500);
    private Label modeDisplay = new Label("Mode: Classic");

    private KeyCode currentDirection = KeyCode.UP;
    private Timeline timeline;
    private boolean isAudioMode = false;
    private Snake snake;
    private Speech speech;
    private Group root;

    public static void main(String[] args) {
        System.out.println("Starting Snake Game with Voice Control!");
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        root = new Group();
        snake = new Snake();
        speech = new Speech();
        Scene scene = new Scene(root, 700, 500);
        scoreDisplay.setLayoutX(400);
        scoreDisplay.setLayoutY(0);
        directionDisplay.setLayoutX(510);
        directionDisplay.setLayoutY(0);
        modeDisplay.setLayoutX(510);
        modeDisplay.setLayoutY(20);

        root.getChildren().add(scoreDisplay);
        root.getChildren().add(directionDisplay);
        root.getChildren().add(modeDisplay);
        primaryStage.setTitle("JavaFX Snake Game with Voice Mode");
        primaryStage.setScene(scene);
        primaryStage.show();
        renderSnake(root, snake, scoreDisplay, directionDisplay, modeDisplay);

        speech
            .keyCodeProperty()
            .addListener((observable, oldKeyCode, newKeyCode) -> {
                if (!isAudioMode || snake.isDead() || newKeyCode == null) {
                    return;
                }
                System.out.println(
                    "Voice command (KeyCode) received: " + newKeyCode
                );

                if (
                    isValidDirection(newKeyCode, snake) &&
                    newKeyCode != currentDirection
                ) {
                    currentDirection = newKeyCode;
                    directionDisplay.setText(
                        "Voice Direction: " + currentDirection.toString()
                    );
                    moveSnake(snake, root);
                    if (timeline != null) {
                        timeline.stop();
                        timeline.playFromStart();
                    }
                } else if (newKeyCode != currentDirection) {
                    directionDisplay.setText(
                        "Voice: Can't move " + newKeyCode.toString()
                    );
                }
            });

        Button classicModeButton = new Button("Classic Mode!");
        Button audioModeButton = new Button("Audio (Voice) Mode!");
        Button startButton = new Button("START!");

        classicModeButton.setTranslateX(150);
        classicModeButton.setTranslateY(250);
        audioModeButton.setTranslateX(300);
        audioModeButton.setTranslateY(250);

        root.getChildren().addAll(classicModeButton, audioModeButton);

        classicModeButton.setOnAction(event -> {
            isAudioMode = false;
            modeDisplay.setText("Mode: Classic\nKeyboard input enabled");
            if (speech != null) {
                speech.stopRecognition();
            }
            root.getChildren().removeAll(classicModeButton, audioModeButton);
            startButton.setTranslateX(250);
            startButton.setTranslateY(250);
            root.getChildren().add(startButton);
        });

        audioModeButton.setOnAction(event -> {
            isAudioMode = true;
            modeDisplay.setText("Mode: Audio (Voice)\nVoice input enabled");
            root.getChildren().removeAll(classicModeButton, audioModeButton);
            startButton.setTranslateX(250);
            startButton.setTranslateY(250);
            root.getChildren().add(startButton);
        });

        // Timeline is now initialized in startButton.setOnAction

        startButton.setOnAction(event -> {
            root.getChildren().remove(startButton);

            long delayMillis = isAudioMode ? 1000 : 500;
            modeDisplay.setText(
                modeDisplay.getText() +
                "\\nSpeed: " +
                (delayMillis == 1000 ? "1s/move" : "0.5s/move")
            );

            timeline = new Timeline(
                // based on what mode they want, the millis
                new KeyFrame(Duration.millis(delayMillis), e -> {
                    if (currentDirection != null && !snake.isDead()) {
                        moveSnake(snake, root);
                    }
                })
            );
            timeline.setCycleCount(Timeline.INDEFINITE);

            if (isAudioMode) {
                try {
                    speech.startRecognition();
                } catch (IOException ex) {
                    System.err.println(
                        "Error starting voice recognition: " + ex.getMessage()
                    );
                    modeDisplay.setText("Mode: Audio (Error)\nVoice disabled");
                    isAudioMode = false;
                    if (speech != null) {
                        speech.stopRecognition();
                    }
                }
            }
            timeline.playFromStart();
        });

        scene.setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();
            // don't reverse into the snakes =l;body
            if (
                isValidDirection(keyCode, snake) &&
                keyCode != currentDirection &&
                !snake.isDead()
            ) {
                currentDirection = keyCode;
                directionDisplay.setText("Direction: " + keyCode.toString());
                moveSnake(snake, root);
                if (timeline != null) {
                    timeline.stop();
                    timeline.playFromStart();
                }
            } else {
                directionDisplay.setText("You can't move that way :(");
            }
        });
    }

    @Override
    public void stop() throws Exception {
        if (speech != null) {
            speech.stopRecognition();
        }
        super.stop();
        System.out.println(
            "Application stopped, speech recognition shut down if active."
        );
    }

    private boolean isValidDirection(KeyCode keyCode, Snake snake) {
        if (snake.getLength() <= 1) return true;
        Coordinate head = snake.getHeadCoordinate();
        Coordinate secondBodyPart = snake.getBody(1);
        switch (keyCode) {
            case UP:
                return !(
                    head.getX() == secondBodyPart.getX() &&
                    head.getY() == secondBodyPart.getY() + 1
                );
            case DOWN:
                return !(
                    head.getX() == secondBodyPart.getX() &&
                    head.getY() == secondBodyPart.getY() - 1
                );
            case LEFT:
                return !(
                    head.getX() == secondBodyPart.getX() + 1 &&
                    head.getY() == secondBodyPart.getY()
                );
            case RIGHT:
                return !(
                    head.getX() == secondBodyPart.getX() - 1 &&
                    head.getY() == secondBodyPart.getY()
                );
            default:
                return false;
        }
    }

    private void moveSnake(Snake snake, Group root) {
        switch (currentDirection) {
            case UP:
                if (snake.willEatApple('u', Apple.getCurrentPosition())) {
                    snake.eatApple(Apple.getCurrentPosition());
                    Apple.relocateApple(snake, 10);
                    updateScore();
                } else {
                    snake.moveUp();
                }
                directionDisplay.setText("You moved up!");
                break;
            case DOWN:
                if (snake.willEatApple('d', Apple.getCurrentPosition())) {
                    snake.eatApple(Apple.getCurrentPosition());
                    Apple.relocateApple(snake, 10);
                    updateScore();
                } else {
                    snake.moveDown();
                }
                directionDisplay.setText("You moved down!");
                break;
            case LEFT:
                if (snake.willEatApple('l', Apple.getCurrentPosition())) {
                    snake.eatApple(Apple.getCurrentPosition());
                    Apple.relocateApple(snake, 10);
                    updateScore();
                } else {
                    snake.moveLeft();
                }
                directionDisplay.setText("You moved left!");
                break;
            case RIGHT:
                if (snake.willEatApple('r', Apple.getCurrentPosition())) {
                    snake.eatApple(Apple.getCurrentPosition());
                    Apple.relocateApple(snake, 10);
                    updateScore();
                } else {
                    snake.moveRight();
                }
                directionDisplay.setText("You moved right!");
                break;
            default:
                break;
        }
        if (!snake.isDead()) {
            renderSnake(
                root,
                snake,
                scoreDisplay,
                directionDisplay,
                modeDisplay
            );
        } else {
            System.out.println("the snake has died.");
            if (timeline != null) {
                timeline.stop();
            }
            Button keepPlayingButton = new Button("Keep playing!");
            Button stopPlayingButton = new Button("Exit...");
            keepPlayingButton.setTranslateX(200);
            keepPlayingButton.setTranslateY(250);
            root.getChildren().add(keepPlayingButton);
            stopPlayingButton.setTranslateX(300);
            stopPlayingButton.setTranslateY(250);
            root.getChildren().add(stopPlayingButton);
            keepPlayingButton.setOnAction(event -> {
                root.getChildren().remove(keepPlayingButton);
                root.getChildren().remove(stopPlayingButton);
                startNewGame(root);
            });
            stopPlayingButton.setOnAction(event -> {
                root.getChildren().remove(keepPlayingButton);
                root.getChildren().remove(stopPlayingButton);
                Platform.exit();
                System.exit(0);
            });
            return;
        }
        System.out.println("rendered snake!");
    }

    private void renderSnake(
        Group root,
        Snake snake,
        Label scoreDisplay,
        Label directionDisplay,
        Label modeDisplay
    ) {
        root.getChildren().clear();
        renderApple(root, snake);
        root.getChildren().add(line);
        // filter through snake coords and then render on screen
        Rectangle head = new Rectangle(
            snake.getBody(0).getX() * 50,
            snake.getBody(0).getY() * 50,
            45,
            45
        );
        head.setFill(Color.GREEN);
        root.getChildren().add(head);
        for (int i = 1; i < snake.getLength(); i++) {
            Rectangle body = new Rectangle(
                snake.getBody(i).getX() * 50,
                snake.getBody(i).getY() * 50,
                45,
                45
            );
            body.setFill(Color.YELLOWGREEN);
            root.getChildren().add(body);
        }
        root.getChildren().add(scoreDisplay);
        root.getChildren().add(directionDisplay);
        root.getChildren().add(modeDisplay);
    }

    private void updateScore() {
        score += 1;
        scoreDisplay.setText("Score:" + score);
    }

    private void renderApple(Group root, Snake snake) {
        Circle appleRect = new Circle(
            Apple.getCurrentPosition().getX() * 50 + 45 / 2.0, // using new position
            Apple.getCurrentPosition().getY() * 50 + 45 / 2.0,
            45 / 2.0
        );

        appleRect.setFill(Color.RED);
        root.getChildren().add(appleRect);
    }

    private void startNewGame(Group root) {
        //setting everything back to default (same as above)
        root.getChildren().clear();
        snake = new Snake();
        score = 0;
        scoreDisplay.setText("Score: 0");
        directionDisplay.setText("Direction: UP");
        root.getChildren().clear();
        root
            .getChildren()
            .addAll(scoreDisplay, directionDisplay, line, modeDisplay);
        renderSnake(root, snake, scoreDisplay, directionDisplay, modeDisplay);
        currentDirection = KeyCode.UP;

        timeline.stop();
        timeline.playFromStart();

        root
            .getScene()
            .setOnKeyPressed(event -> {
                KeyCode keyCode = event.getCode();
                // don't reverse into the snakes =l;body
                if (
                    isValidDirection(keyCode, snake) &&
                    keyCode != currentDirection &&
                    !snake.isDead()
                ) {
                    currentDirection = keyCode;
                    directionDisplay.setText(
                        "Direction: " + keyCode.toString()
                    );

                    moveSnake(snake, root);
                    if (timeline != null) {
                        timeline.stop();
                        timeline.playFromStart();
                    }
                } else {
                    directionDisplay.setText("You can't move that way :(");
                }
            });

        if (isAudioMode) {
            System.out.println(
                "New game started in Audio Mode. Voice recognition should be active if previously started."
            );
        }
    }
}
