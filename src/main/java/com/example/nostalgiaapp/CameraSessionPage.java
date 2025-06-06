package com.example.nostalgiaapp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;

public class CameraSessionPage {

    private VBox mainContainer;
    private ImageView mainViewfinder;
    private VBox thumbnailBox;
    private Text overlayText;
    private Button editButton, retakeButton;
    private VideoCapture capture;
    private ArrayList<Mat> photosTaken = new ArrayList<>();
    private int currentPhotoIndex = 0;
    private boolean isCapturing = false;
    private Timeline cameraTimeline;

    // Load OpenCV library
    static {
        try {
            nu.pattern.OpenCV.loadShared();
            System.out.println("✅ OpenCV library loaded successfully using nu.pattern.OpenCV");
        } catch (Exception e1) {
            try {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                System.out.println("✅ OpenCV library loaded successfully using System.loadLibrary");
            } catch (Exception e2) {
                System.err.println("❌ Failed to load OpenCV library with both methods:");
                System.err.println("Method 1 error: " + e1.getMessage());
                System.err.println("Method 2 error: " + e2.getMessage());
            }
        }
    }

    public void show(Stage stage) {
        mainContainer = new VBox();
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setSpacing(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: #FFF4C5;");

        // Top bar with title and back button
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(20);

        Button backButton = new Button("←");
        backButton.setStyle("-fx-background-color: #FF8C42; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 50; -fx-min-width: 40; -fx-min-height: 40;");
        backButton.setOnAction(e -> {
            closeCamera();
            stage.close();
        });

        Text titleText = new Text("Nostalgia");
        titleText.setFont(Font.font("Brush Script MT", FontWeight.NORMAL, 32));
        titleText.setFill(Color.web("#8B4513"));

        topBar.getChildren().addAll(backButton, titleText);

        // Center content
        HBox centerBox = new HBox();
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setSpacing(40);

        // Left side - camera viewfinder and button
        VBox leftView = new VBox();
        leftView.setAlignment(Pos.CENTER);
        leftView.setSpacing(15);

        // Camera viewfinder with overlay
        StackPane viewfinderPane = new StackPane();
        viewfinderPane.setStyle("-fx-background-color: #EEE5C7; -fx-border-color: #8B4513; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10;");

        mainViewfinder = new ImageView();
        mainViewfinder.setFitWidth(400);
        mainViewfinder.setFitHeight(300);
        mainViewfinder.setPreserveRatio(false);
        mainViewfinder.setStyle("-fx-background-color: #F0F0F0;");

        overlayText = new Text("");
        overlayText.setFont(Font.font("Verdana", FontWeight.BOLD, 48));
        overlayText.setFill(Color.BLACK);
        overlayText.setStroke(Color.WHITE);
        overlayText.setStrokeWidth(2);

        viewfinderPane.getChildren().addAll(mainViewfinder, overlayText);

        Button takePhotoBtn = new Button("📷 TAKE PHOTO");
        takePhotoBtn.setPrefWidth(200);
        takePhotoBtn.setPrefHeight(40);
        takePhotoBtn.setStyle("-fx-background-color: #FF8C42; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-font-size: 14px;");
        takePhotoBtn.setOnAction(e -> {
            if (!isCapturing) {
                startPhotoSession();
            }
        });

        leftView.getChildren().addAll(viewfinderPane, takePhotoBtn);

        // Right side - thumbnail previews
        thumbnailBox = new VBox();
        thumbnailBox.setSpacing(15);
        thumbnailBox.setAlignment(Pos.TOP_CENTER);

        for (int i = 0; i < 3; i++) {
            ImageView thumb = new ImageView();
            thumb.setFitWidth(100);
            thumb.setFitHeight(100);
            thumb.setPreserveRatio(false);
            thumb.setStyle("-fx-background-color: #FAF0DC; -fx-border-color: #8B4513; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.5, 3, 3);");
            thumbnailBox.getChildren().add(thumb);
        }

        centerBox.getChildren().addAll(leftView, thumbnailBox);

        // Bottom section
        VBox bottomBox = new VBox();
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setSpacing(15);

        Text statusText = new Text("CAPTURE A GREAT MOMENTS WITH YOUR FRIENDS");
        statusText.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        statusText.setFill(Color.web("#8B4513"));
        statusText.setUnderline(true);

        HBox controlButtons = new HBox();
        controlButtons.setSpacing(20);
        controlButtons.setAlignment(Pos.CENTER);

        editButton = new Button("✏️ EDIT PHOTOS");
        editButton.setPrefWidth(150);
        editButton.setPrefHeight(40);
        editButton.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-font-size: 14px;");
        editButton.setVisible(false);
        editButton.setOnAction(e -> {
            System.out.println("Edit Photos - Not implemented yet");
        });

        retakeButton = new Button("📷 RETAKE");
        retakeButton.setPrefWidth(150);
        retakeButton.setPrefHeight(40);
        retakeButton.setStyle("-fx-background-color: #A47148; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-font-size: 14px;");
        retakeButton.setVisible(false);
        retakeButton.setOnAction(e -> resetSession());

        controlButtons.getChildren().addAll(editButton, retakeButton);
        bottomBox.getChildren().addAll(statusText, controlButtons);

        mainContainer.getChildren().addAll(topBar, centerBox, bottomBox);

        Scene scene = new Scene(mainContainer, 1000, 700);
        stage.setTitle("Nostalgia - Camera Session");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> closeCamera());
        stage.show();

        // Initialize camera
        initializeCamera();
    }

    private void initializeCamera() {
        try {
            capture = new VideoCapture(0);

            if (!capture.isOpened()) {
                System.out.println("❌ Failed to open camera. Trying different camera indices...");
                // Try other camera indices
                for (int i = 1; i < 5; i++) {
                    capture = new VideoCapture(i);
                    if (capture.isOpened()) {
                        System.out.println("📷 Camera opened successfully on index " + i);
                        break;
                    }
                }
            } else {
                System.out.println("📷 Camera opened successfully on index 0");
            }

            if (!capture.isOpened()) {
                Platform.runLater(() -> {
                    overlayText.setText("Camera not available");
                    overlayText.setFill(Color.RED);
                });
                return;
            }

            // Set camera properties
            capture.set(3, 640); // Width
            capture.set(4, 480); // Height

            // Start camera feed
            startCameraFeed();

        } catch (Exception e) {
            System.err.println("Error initializing camera: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> {
                overlayText.setText("Camera Error");
                overlayText.setFill(Color.RED);
            });
        }
    }

    private void startCameraFeed() {
        cameraTimeline = new Timeline(new KeyFrame(Duration.millis(33), e -> updateCameraFeed()));
        cameraTimeline.setCycleCount(Timeline.INDEFINITE);
        cameraTimeline.play();
    }

    private void updateCameraFeed() {
        if (capture != null && capture.isOpened()) {
            Mat frame = new Mat();
            if (capture.read(frame) && !frame.empty()) {
                javafx.scene.image.Image fxImage = OpenCVUtils.mat2Image(frame);
                Platform.runLater(() -> mainViewfinder.setImage(fxImage));
            }
        }
    }

    private void startPhotoSession() {
        if (capture == null || !capture.isOpened()) {
            System.out.println("❌ Camera not available for photo session");
            return;
        }

        currentPhotoIndex = 0;
        photosTaken.clear();
        isCapturing = true;

        // Clear thumbnails
        for (int i = 0; i < 3; i++) {
            ((ImageView) thumbnailBox.getChildren().get(i)).setImage(null);
        }

        editButton.setVisible(false);
        retakeButton.setVisible(false);

        takeNextPhoto();
    }

    private void takeNextPhoto() {
        if (currentPhotoIndex >= 3) {
            Platform.runLater(() -> {
                overlayText.setText("Picture is already taken!!!");
                overlayText.setFill(Color.GREEN);
                editButton.setVisible(true);
                retakeButton.setVisible(true);
                isCapturing = false;
            });
            return;
        }

        Timeline countdown = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> {
                    overlayText.setText("3");
                    overlayText.setFill(Color.RED);
                }),
                new KeyFrame(Duration.seconds(1), e -> {
                    overlayText.setText("2");
                    overlayText.setFill(Color.ORANGE);
                }),
                new KeyFrame(Duration.seconds(2), e -> {
                    overlayText.setText("1");
                    overlayText.setFill(Color.YELLOW);
                }),
                new KeyFrame(Duration.seconds(3), e -> {
                    overlayText.setText("Smile!");
                    overlayText.setFill(Color.GREEN);
                }),
                new KeyFrame(Duration.seconds(3.5), e -> captureAndDisplay())
        );
        countdown.play();
    }

    private void captureAndDisplay() {
        if (capture == null || !capture.isOpened()) {
            System.out.println("❌ Camera not available for capture");
            return;
        }

        Mat frame = new Mat();
        if (capture.read(frame) && !frame.empty()) {
            // Store the captured frame
            Mat capturedFrame = frame.clone();
            photosTaken.add(capturedFrame);

            // Convert to JavaFX image and display in thumbnail
            javafx.scene.image.Image image = OpenCVUtils.mat2Image(capturedFrame);
            ImageView thumb = (ImageView) thumbnailBox.getChildren().get(currentPhotoIndex);
            thumb.setImage(image);

            currentPhotoIndex++;

            // Clear overlay text briefly
            overlayText.setText("");

            // Schedule next photo or finish
            Timeline pause = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
                if (currentPhotoIndex < 3) {
                    takeNextPhoto();
                } else {
                    Platform.runLater(() -> {
                        overlayText.setText("Picture is already taken!!!");
                        overlayText.setFill(Color.GREEN);
                        editButton.setVisible(true);
                        retakeButton.setVisible(true);
                        isCapturing = false;
                    });
                }
            }));
            pause.play();
        } else {
            System.out.println("❌ Failed to capture photo.");
            overlayText.setText("Capture Failed");
            overlayText.setFill(Color.RED);
        }
    }

    private void resetSession() {
        currentPhotoIndex = 0;
        photosTaken.clear();
        isCapturing = false;
        editButton.setVisible(false);
        retakeButton.setVisible(false);
        overlayText.setText("");

        // Clear thumbnails
        for (int i = 0; i < 3; i++) {
            ((ImageView) thumbnailBox.getChildren().get(i)).setImage(null);
        }
    }

    private void closeCamera() {
        if (cameraTimeline != null) {
            cameraTimeline.stop();
        }
        if (capture != null && capture.isOpened()) {
            capture.release();
            System.out.println("📷 Camera released");
        }
    }
}