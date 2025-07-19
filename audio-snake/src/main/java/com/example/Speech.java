package com.example;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import java.io.IOException;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.KeyCode;

public class Speech {

    private LiveSpeechRecognizer recognizer;
    private final ObjectProperty<KeyCode> keyCodeProperty;
    private volatile boolean recognitionActive = false; // volotile means visible from all threads cuz we're doing multiple threads interaction
    private Thread recognitionThread;

    public Speech() {
        this.keyCodeProperty = new SimpleObjectProperty<>(null);
    }

    public ObjectProperty<KeyCode> keyCodeProperty() {
        return keyCodeProperty;
    }

    private void initializeRecognizer() throws IOException {
        if (recognizer == null) {
            Configuration configuration = new Configuration();
            configuration.setAcousticModelPath(
                "resource:/edu/cmu/sphinx/models/en-us/en-us"
            );

            String dictionaryPath = getClass().getResource("/5490.dic") != null
                ? getClass().getResource("/5490.dic").toString()
                : null;
            String languageModelPath = getClass().getResource("/5490.lm") !=
                null
                ? getClass().getResource("/5490.lm").toString()
                : null;

            configuration.setDictionaryPath(dictionaryPath);
            configuration.setLanguageModelPath(languageModelPath);

            recognizer = new LiveSpeechRecognizer(configuration);
        }
    }

    public void startRecognition() throws IOException {
        if (recognitionActive) {
            return;
        }
        initializeRecognizer();
        recognitionActive = true;
        recognizer.startRecognition(true);
        keyCodeProperty.set(null);

        recognitionThread = new Thread(() -> {
            System.out.println(
                "Speech recognition thread started. Listening for commands..."
            );
            while (recognitionActive) {
                SpeechResult result = recognizer.getResult();
                if (result != null) {
                    String command = result.getHypothesis();
                    if (command != null && !command.isEmpty()) {
                        Platform.runLater(() -> {
                            if (command.equalsIgnoreCase("UP")) {
                                keyCodeProperty.set(KeyCode.UP);
                            } else if (command.equalsIgnoreCase("DOWN")) {
                                keyCodeProperty.set(KeyCode.DOWN);
                            } else if (command.equalsIgnoreCase("LEFT")) {
                                keyCodeProperty.set(KeyCode.LEFT);
                            } else if (command.equalsIgnoreCase("RIGHT")) {
                                keyCodeProperty.set(KeyCode.RIGHT);
                            } else {
                                keyCodeProperty.set(null);
                            }
                        });
                    }
                }
            }
            System.out.println("Speech recognition thread finished.");
        });
        recognitionThread.setDaemon(true);
        recognitionThread.start();
    }

    public void stopRecognition() {
        recognitionActive = false;
        if (recognizer != null) {
            recognizer.stopRecognition();
        }
        if (recognitionThread != null) {
            try {
                recognitionThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        recognizer = null;
        System.out.println(
            "Speech recognition stopped and resources released."
        );
    }
}
