package com.example.nostalgiaapp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;

public class CameraSessionPage {

    private VBox mainContainer;
    private ImageView mainViewfinder;
    private StackPane viewfinderPane;
    private VBox thumbnailBox;
    private Text overlayText;
    private Button editButton, retakeButton, takePhotoBtn, cameraToggleBtn;
    private VideoCapture capture;
    private ArrayList<Mat> photosTaken = new ArrayList<>();
    private int currentPhotoIndex = 0;
    private boolean isCapturing = false;
    private boolean isCameraOn = false;
    private Timeline cameraTimeline;
    private NostalgiaApp mainApp;
    private Stage currentStage;
    private boolean cameraInitialized = false;

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

    public void show(Stage stage, NostalgiaApp mainApp) {
        this.mainApp = mainApp;
        this.currentStage = stage;

        mainContainer = new VBox();
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setSpacing(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: #FFF4C5;");

        // FIXED: Top bar with back button on left, title properly centered
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(20);

        Button backButton = new Button("←");
        backButton.setStyle("-fx-background-color: #FF8C42; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 50; -fx-min-width: 40; -fx-min-height: 40;");
        backButton.setOnAction(e -> {
            closeCamera();
            stage.close();
            if (mainApp != null) {
                mainApp.showMainWindow();
            }
        });

        Text titleText = new Text("Nostalgia");
        titleText.setFont(Font.font("Brush Script MT", FontWeight.NORMAL, 32));
        titleText.setFill(Color.web("#8B4513"));

        // Position back button and title with proper spacing
        topBar.getChildren().addAll(backButton, titleText);

        // Center content
        HBox centerBox = new HBox();
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setSpacing(40);

        // Left side - camera viewfinder and buttons
        VBox leftView = new VBox();
        leftView.setAlignment(Pos.CENTER);
        leftView.setSpacing(15);

        // FIXED: Camera viewfinder with proper placeholder and toggle button
        viewfinderPane = new StackPane();
        viewfinderPane.setStyle("-fx-background-color: #EEE5C7; -fx-border-color: #8B4513; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10;");
        viewfinderPane.setPrefSize(400, 400);

        mainViewfinder = new ImageView();
        mainViewfinder.setFitWidth(400);
        mainViewfinder.setFitHeight(400);
        mainViewfinder.setPreserveRatio(false);

        // FIXED: Overlay text with proper sizing
        overlayText = new Text("");
        overlayText.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        overlayText.setFill(Color.BLACK);
        overlayText.setStroke(Color.WHITE);
        overlayText.setStrokeWidth(1);

        // FIXED: Camera toggle button in top-right corner
        cameraToggleBtn = new Button("📷 ON");
        cameraToggleBtn.setPrefWidth(70);
        cameraToggleBtn.setPrefHeight(30);
        cameraToggleBtn.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-font-size: 10px;");
        cameraToggleBtn.setOnAction(e -> toggleCamera());

        // Position toggle button in top-right corner
        StackPane.setAlignment(cameraToggleBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(cameraToggleBtn, new Insets(10, 10, 0, 0));

        // Create initial camera placeholder
        createCameraPlaceholder();

        viewfinderPane.getChildren().addAll(mainViewfinder, overlayText, cameraToggleBtn);

        // Button container for main photo control
        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setSpacing(10);

        takePhotoBtn = new Button("📷 TAKE PHOTO");
        takePhotoBtn.setPrefWidth(180);
        takePhotoBtn.setPrefHeight(40);
        takePhotoBtn.setStyle("-fx-background-color: #FF8C42; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-font-size: 12px;");
        takePhotoBtn.setVisible(false); // Initially hidden until camera is on
        takePhotoBtn.setOnAction(e -> {
            if (!isCapturing && isCameraOn) {
                startPhotoSession();
            }
        });

        buttonContainer.getChildren().add(takePhotoBtn);
        leftView.getChildren().addAll(viewfinderPane, buttonContainer);

        // Right side - thumbnail previews (empty squares initially)
        thumbnailBox = new VBox();
        thumbnailBox.setSpacing(15);
        thumbnailBox.setAlignment(Pos.TOP_CENTER);

        for (int i = 0; i < 3; i++) {
            // FIXED: Create empty thumbnail placeholders
            StackPane thumbContainer = new StackPane();
            thumbContainer.setPrefSize(100, 100);
            thumbContainer.setStyle("-fx-background-color: #FAF0DC; -fx-border-color: #8B4513; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.5, 3, 3);");

            ImageView thumb = new ImageView();
            thumb.setFitWidth(96);
            thumb.setFitHeight(96);
            thumb.setPreserveRatio(false);

            thumbContainer.getChildren().add(thumb);
            thumbnailBox.getChildren().add(thumbContainer);
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
        stage.setOnCloseRequest(e -> {
            closeCamera();
            if (mainApp != null) {
                mainApp.showMainWindow();
            }
        });
        stage.show();
    }

    // FIXED: Proper camera toggle that maintains interface
    private void toggleCamera() {
        if (!isCameraOn) {
            // Turn camera ON
            initializeCamera();
            if (cameraInitialized) {
                startCameraFeed();
                isCameraOn = true;
                cameraToggleBtn.setText("📷 OFF");
                cameraToggleBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-font-size: 10px;");
                takePhotoBtn.setVisible(true);
                overlayText.setText("");
            }
        } else {
            // Turn camera OFF - FIXED: Only stop feed, keep interface
            stopCameraFeed();
            isCameraOn = false;
            cameraToggleBtn.setText("📷 ON");
            cameraToggleBtn.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-font-size: 10px;");
            takePhotoBtn.setVisible(false);
            createCameraPlaceholder();
            overlayText.setText("");
        }
    }

    // FIXED: Better camera placeholder with proper camera icon
    private void createCameraPlaceholder() {
        Platform.runLater(() -> {
            // Create a camera icon background
            StackPane placeholder = new StackPane();
            placeholder.setPrefSize(400, 400);
            placeholder.setStyle("-fx-background-color: #FAF0DC;");

            // Create camera icon
            Text cameraIcon = new Text("📷");
            cameraIcon.setFont(Font.font(80));
            cameraIcon.setFill(Color.web("#8B4513"));

            placeholder.getChildren().add(cameraIcon);

            // Clear the main viewfinder
            mainViewfinder.setImage(null);
        });
    }

    // FIXED: Improved camera initialization with better error handling
    private void initializeCamera() {
        try {
            if (capture != null && capture.isOpened()) {
                capture.release();
            }

            capture = new VideoCapture(0);

            if (!capture.isOpened()) {
                System.out.println("❌ Failed to open camera on index 0. Trying other indices...");
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

            // FIXED: Optimized camera settings for better performance
            capture.set(3, 640); // Width
            capture.set(4, 480); // Height
            capture.set(5, 20);  // FPS - increased for smoother feed
            capture.set(6, 0);   // Auto exposure

            cameraInitialized = true;

        } catch (Exception e) {
            System.err.println("Error initializing camera: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> {
                overlayText.setText("Camera Error");
                overlayText.setFill(Color.RED);
            });
        }
    }

    // FIXED: Improved camera feed with reduced latency
    private void startCameraFeed() {
        if (cameraTimeline != null) {
            cameraTimeline.stop();
        }

        // FIXED: Reduced interval for smoother feed (was 100ms, now 50ms)
        cameraTimeline = new Timeline(new KeyFrame(Duration.millis(50), e -> updateCameraFeed()));
        cameraTimeline.setCycleCount(Timeline.INDEFINITE);
        cameraTimeline.play();
    }

    private void stopCameraFeed() {
        if (cameraTimeline != null) {
            cameraTimeline.stop();
        }
    }

    // FIXED: Optimized camera feed update
    private void updateCameraFeed() {
        if (capture != null && capture.isOpened() && cameraInitialized && isCameraOn) {
            Mat frame = new Mat();
            try {
                if (capture.read(frame) && !frame.empty()) {
                    Mat squareFrame = cropToSquare(frame);
                    javafx.scene.image.Image fxImage = OpenCVUtils.mat2Image(squareFrame);
                    Platform.runLater(() -> {
                        if (mainViewfinder != null) {
                            mainViewfinder.setImage(fxImage);
                        }
                    });
                    squareFrame.release(); // Clean up
                }
                frame.release(); // Clean up
            } catch (Exception e) {
                System.err.println("Error updating camera feed: " + e.getMessage());
            }
        }
    }

    private Mat cropToSquare(Mat frame) {
        int width = frame.width();
        int height = frame.height();
        int size = Math.min(width, height);

        int x = (width - size) / 2;
        int y = (height - size) / 2;

        Rect cropRect = new Rect(x, y, size, size);
        Mat croppedFrame = new Mat(frame, cropRect);

        Mat resizedFrame = new Mat();
        Imgproc.resize(croppedFrame, resizedFrame, new Size(400, 400));

        croppedFrame.release(); // Clean up
        return resizedFrame;
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
            StackPane thumbContainer = (StackPane) thumbnailBox.getChildren().get(i);
            ImageView thumb = (ImageView) thumbContainer.getChildren().get(0);
            thumb.setImage(null);
        }

        // Hide buttons during capture
        editButton.setVisible(false);
        retakeButton.setVisible(false);
        takePhotoBtn.setVisible(false);

        takeNextPhoto();
    }

    private void takeNextPhoto() {
        if (currentPhotoIndex >= 3) {
            Platform.runLater(() -> {
                overlayText.setText("Picture is already taken!!!");
                overlayText.setFill(Color.GREEN);

                // Show EDIT and RETAKE buttons after all photos
                editButton.setVisible(true);
                retakeButton.setVisible(true);
                isCapturing = false;

                // FIXED: After completion, show empty viewfinder
                Timeline delay = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
                    overlayText.setText("");
                    createCameraPlaceholder();
                }));
                delay.play();
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
        try {
            if (capture.read(frame) && !frame.empty()) {
                Mat squareFrame = cropToSquare(frame);
                Mat capturedFrame = squareFrame.clone();
                photosTaken.add(capturedFrame);

                javafx.scene.image.Image image = OpenCVUtils.mat2Image(capturedFrame);
                StackPane thumbContainer = (StackPane) thumbnailBox.getChildren().get(currentPhotoIndex);
                ImageView thumb = (ImageView) thumbContainer.getChildren().get(0);
                thumb.setImage(image);

                currentPhotoIndex++;
                overlayText.setText("");

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

                            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(2), ev -> {
                                overlayText.setText("");
                                createCameraPlaceholder();
                            }));
                            delay.play();
                        });
                    }
                }));
                pause.play();

                squareFrame.release(); // Clean up
            }
            frame.release(); // Clean up
        } catch (Exception e) {
            System.err.println("Error capturing photo: " + e.getMessage());
            overlayText.setText("Capture Failed");
            overlayText.setFill(Color.RED);
        }
    }

    // FIXED: Proper session reset
    private void resetSession() {
        currentPhotoIndex = 0;
        photosTaken.clear();
        isCapturing = false;
        editButton.setVisible(false);
        retakeButton.setVisible(false);
        overlayText.setText("");

        // Clear thumbnails
        for (int i = 0; i < 3; i++) {
            StackPane thumbContainer = (StackPane) thumbnailBox.getChildren().get(i);
            ImageView thumb = (ImageView) thumbContainer.getChildren().get(0);
            thumb.setImage(null);
        }

        // Reset to initial state
        if (isCameraOn) {
            takePhotoBtn.setVisible(true);
        } else {
            takePhotoBtn.setVisible(false);
            createCameraPlaceholder();
        }
    }

    // FIXED: Proper resource cleanup
    private void closeCamera() {
        if (cameraTimeline != null) {
            cameraTimeline.stop();
        }
        if (capture != null && capture.isOpened()) {
            capture.release();
            System.out.println("📷 Camera released");
        }
        // Clean up captured photos
        for (Mat photo : photosTaken) {
            if (photo != null) {
                photo.release();
            }
        }
        photosTaken.clear();
        cameraInitialized = false;
        isCameraOn = false;
    }
}