package com.example.nostalgiaapp;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class DownloadPage {
    public void start(Stage stage) {
        // Root container
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #fff4c5;");

        // VBox for vertical layout
        VBox vbox = new VBox(30); // spacing
        vbox.setAlignment(Pos.CENTER);

        // Define button size
        double buttonWidth = 250;
        double buttonHeight = 50;

        // Continue button
        Button continueButton = new Button("MAKE MEMORIES AGAIN");
        continueButton.setPrefSize(buttonWidth, buttonHeight);
        continueButton.setStyle(
                "-fx-background-color: #ff914d;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 15;" +
                        "-fx-font-size: 16px;" +
                        "-fx-cursor: hand;"
        );

        // Download button
        Button downloadButton = new Button("Download");
        downloadButton.setPrefSize(buttonWidth, buttonHeight);
        downloadButton.setStyle(
                "-fx-background-color: #ffde59;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 15;" +
                        "-fx-font-size: 16px;" +
                        "-fx-cursor: hand;"
        );

        // Thank You Message Box with rounded corners and black border
        Label messageLabel = new Label("Thanks for the memories!\n" +
                "We’re so glad you chose Nostalgia to capture your special moments.\n" +
                "Come back anytime to relive the good times and make new ones. 💛");
        messageLabel.setWrapText(true);
        messageLabel.setTextFill(Color.WHITE);
        messageLabel.setFont(Font.font("Sorts Mill Goudy", 18));
        messageLabel.setAlignment(Pos.CENTER);

        StackPane messageBox = new StackPane(messageLabel);
        messageBox.setStyle(
                "-fx-background-color: #bc9e9e;" +
                        "-fx-border-color: black;" +
                        "-fx-border-width: 2px;" +
                        "-fx-background-radius: 20;" +  // Rounded corners
                        "-fx-border-radius: 20;"        // Rounded border
        );
        messageBox.setPadding(new Insets(30));
        messageBox.setMaxWidth(500);
        messageBox.setMinHeight(200);

        // Add elements to vbox
        vbox.getChildren().addAll(messageBox, continueButton, downloadButton);
        root.getChildren().add(vbox);

        // Set up scene
        Scene scene = new Scene(root, 700, 1000);
        stage.setScene(scene);
        stage.setTitle("Download Page");

        stage.setMinWidth(700);
        stage.setMinHeight(1000);
        stage.setResizable(true);

        stage.show();
    }
}