package com.example.nostalgiaapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Download Page - Final stage of the photobooth application
 * Displays the finalized photo strip and provides download/restart options
 */
public class DownloadPage extends BorderPane {

    private static final Logger LOGGER = Logger.getLogger(DownloadPage.class.getName());

    // UI Components
    private VBox photoStripContainer;
    private VBox messagePanel;
    private HBox buttonContainer;
    private Label nostalgiaLogo;

    // Application state
    private List<Image> capturedPhotos;
    private Image finalPhotoStrip;
    private Stage currentStage;
    private NostalgiaApp mainApp;

    // Callback interface for navigation
    public interface NavigationCallback {
        void onRestartSession();
        void onDownloadComplete();
    }

    private NavigationCallback navigationCallback;

    /**
     * Constructor for the download page
     * @param capturedPhotos List of processed photos from editing phase
     * @param stage Current stage
     * @param mainApp Reference to the main app
     */
    public DownloadPage(List<Image> capturedPhotos, Stage stage, NostalgiaApp mainApp) {
        this.capturedPhotos = capturedPhotos;
        this.currentStage = stage;
        this.mainApp = mainApp;

        // Set up navigation callback
        this.navigationCallback = new NavigationCallback() {
            @Override
            public void onRestartSession() {
                if (mainApp != null) {
                    currentStage.close();
                    mainApp.showMainWindow();
                }
            }

            @Override
            public void onDownloadComplete() {
                // Optional: Can add specific behavior after download
            }
        };

        try {
            // Initialize UI components
            initializeComponents();

            // Set background color
            this.setStyle("-fx-background-color: #F5E6D3;");

            // Create main layout
            setupLayout();

            // Display the photostrip if available
            if (capturedPhotos != null && !capturedPhotos.isEmpty()) {
                // The first (and should be only) image is our final photostrip
                finalPhotoStrip = capturedPhotos.get(0);
                displayPhotoStrip(finalPhotoStrip);
            } else {
                // Show a placeholder if no photos
                createPlaceholderPhotoStrip();
                finalPhotoStrip = null; // Make sure it's null for error handling
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing download page", e);
            showErrorDialog("Initialization Error", "Failed to load download page. Please try again.");
        }
    }


    private void initializeComponents() {
        // Initialize photo strip container with flexible sizing
        photoStripContainer = new VBox();
        photoStripContainer.setAlignment(Pos.CENTER);
        photoStripContainer.setSpacing(5);
        photoStripContainer.setPadding(new Insets(20));
        photoStripContainer.setStyle("-fx-background-color: #B8E6FF; -fx-background-radius: 15; " +
                "-fx-border-color: #87CEEB; -fx-border-width: 2; -fx-border-radius: 15;");

        // FIXED: Don't set fixed heights - let it adapt to content
        photoStripContainer.setPrefWidth(200);
        photoStripContainer.setMinWidth(200);
        photoStripContainer.setMaxWidth(450); // Allow wider for horizontal strips

        // Initialize message panel
        messagePanel = createMessagePanel();

        // Initialize button container
        buttonContainer = createButtonContainer();

        // Initialize Nostalgia logo
        nostalgiaLogo = createNostalgiaLogo();
    }



    private void setupLayout() {
        // Top-right section: Logo
        HBox topSection = new HBox();
        topSection.setAlignment(Pos.CENTER_RIGHT);
        topSection.getChildren().add(nostalgiaLogo);
        topSection.setPadding(new Insets(10));
        this.setTop(topSection);

        // Center content
        HBox mainContent = new HBox(30);
        mainContent.setPadding(new Insets(20));
        mainContent.setAlignment(Pos.CENTER);

        // PREVENT the main content from stretching
        mainContent.setFillHeight(false);

        // Left section: Message panel and buttons
        VBox leftSection = new VBox(30);
        leftSection.setAlignment(Pos.CENTER_LEFT);
        leftSection.getChildren().addAll(messagePanel, buttonContainer);
        leftSection.setPadding(new Insets(0, 40, 0, 40));

        // FIXED width for left section
        leftSection.setPrefWidth(400);
        leftSection.setMaxWidth(400);

        // Right section: Photo strip with wrapper
        VBox centerSection = new VBox();
        centerSection.setAlignment(Pos.CENTER);
        centerSection.getChildren().add(photoStripContainer);
        centerSection.setPadding(new Insets(20));

        // FIXED: More flexible width for center section
        centerSection.setPrefWidth(240);
        centerSection.setMaxWidth(450); // Allow wider for horizontal strips
        centerSection.setFillWidth(false);

        mainContent.getChildren().addAll(leftSection, centerSection);
        this.setCenter(mainContent);
    }


    private VBox createMessagePanel() {
        VBox panel = new VBox(10);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(25));
        panel.setMaxWidth(350);
        panel.setStyle("-fx-background-color: #D2B48C; -fx-background-radius: 20; " +
                "-fx-border-color: #8B4513; -fx-border-width: 2; -fx-border-radius: 20;");

        // Main thank you message
        Label thankYouLabel = new Label("Thanks for the memories!");
        thankYouLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        thankYouLabel.setTextFill(Color.web("#4A4A4A"));
        thankYouLabel.setWrapText(true);
        thankYouLabel.setTextAlignment(TextAlignment.CENTER);

        // Secondary message
        Label secondaryLabel = new Label("We're so glad you chose Nostalgia to capture your special moments.");
        secondaryLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        secondaryLabel.setTextFill(Color.web("#4A4A4A"));
        secondaryLabel.setWrapText(true);
        secondaryLabel.setTextAlignment(TextAlignment.CENTER);

        // Encouraging message
        Label encouragingLabel = new Label("Come back anytime to relive the good times and make new ones. 📸");
        encouragingLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        encouragingLabel.setTextFill(Color.web("#4A4A4A"));
        encouragingLabel.setWrapText(true);
        encouragingLabel.setTextAlignment(TextAlignment.CENTER);

        panel.getChildren().addAll(thankYouLabel, secondaryLabel, encouragingLabel);
        return panel;
    }

    /**
     * Create the action buttons container
     * @return HBox containing styled action buttons
     */
    private HBox createButtonContainer() {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER);

        // Make Pictures Again button
        Button makePicturesBtn = new Button("📷 MAKE PICTURES AGAIN");
        makePicturesBtn.setStyle("-fx-background-color: #FF8C42; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 15; -fx-padding: 15 20; -fx-font-size: 14px; " +
                "-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);");

        makePicturesBtn.setOnMouseEntered(e ->
                makePicturesBtn.setStyle("-fx-background-color: #FF7A28; -fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-background-radius: 15; -fx-padding: 15 20; -fx-font-size: 14px; " +
                        "-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 7, 0, 0, 3);"));

        makePicturesBtn.setOnMouseExited(e ->
                makePicturesBtn.setStyle("-fx-background-color: #FF8C42; -fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-background-radius: 15; -fx-padding: 15 20; -fx-font-size: 14px; " +
                        "-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"));

        makePicturesBtn.setOnAction(e -> handleRestartSession());

        // Download button
        Button downloadBtn = new Button("⬇ DOWNLOAD");
        downloadBtn.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #4A4A4A; -fx-font-weight: bold; " +
                "-fx-background-radius: 15; -fx-padding: 15 30; -fx-font-size: 14px; " +
                "-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);");

        downloadBtn.setOnMouseEntered(e ->
                downloadBtn.setStyle("-fx-background-color: #FFC700; -fx-text-fill: #4A4A4A; -fx-font-weight: bold; " +
                        "-fx-background-radius: 15; -fx-padding: 15 30; -fx-font-size: 14px; " +
                        "-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 7, 0, 0, 3);"));

        downloadBtn.setOnMouseExited(e ->
                downloadBtn.setStyle("-fx-background-color: #FFD700; -fx-text-fill: #4A4A4A; -fx-font-weight: bold; " +
                        "-fx-background-radius: 15; -fx-padding: 15 30; -fx-font-size: 14px; " +
                        "-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"));

        downloadBtn.setOnAction(e -> handleDownload());

        container.getChildren().addAll(makePicturesBtn, downloadBtn);
        return container;
    }

    /**
     * Create the Nostalgia logo
     * @return Label styled as the Nostalgia logo
     */
    private Label createNostalgiaLogo() {
        Label logo = new Label("🏠 Nostalgia");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        logo.setTextFill(Color.web("#8B4513"));
        logo.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 1);");
        return logo;
    }

    // Updated displayPhotoStrip method in DownloadPage.java


    private void displayPhotoStrip(Image photoStripImage) {
        if (photoStripImage != null) {
            // Determine if the photostrip is horizontal or vertical by aspect ratio
            boolean isHorizontal = photoStripImage.getWidth() > photoStripImage.getHeight();

            // Create ImageView for display
            ImageView photoStripView = new ImageView(photoStripImage);

            // Clear any existing content
            photoStripContainer.getChildren().clear();

            // Adjust container based on orientation
            if (isHorizontal) {
                // For horizontal photostrips
                photoStripContainer.setPrefWidth(400);
                photoStripContainer.setMinWidth(200);
                photoStripContainer.setMaxWidth(450);
                photoStripContainer.setPrefHeight(-1); // Use computed size
                photoStripContainer.setMinHeight(-1); // Use computed size
                photoStripContainer.setMaxHeight(-1); // Use computed size

                // Set horizontal width but preserve ratio
                photoStripView.setFitWidth(360);
                photoStripView.setPreserveRatio(true);
                photoStripView.setSmooth(true);

                // Center in container
                VBox wrapper = new VBox(photoStripView);
                wrapper.setAlignment(Pos.CENTER);
                photoStripContainer.getChildren().add(wrapper);

                System.out.println("Displaying horizontal photostrip");
            } else {
                // For vertical photostrips (original behavior)
                photoStripContainer.setPrefWidth(200);
                photoStripContainer.setMinWidth(200);
                photoStripContainer.setMaxWidth(200);
                photoStripContainer.setPrefHeight(-1); // Use computed size
                photoStripContainer.setMinHeight(-1); // Use computed size
                photoStripContainer.setMaxHeight(-1); // Use computed size

                // Set vertical width but preserve ratio
                photoStripView.setFitWidth(160);
                photoStripView.setPreserveRatio(true);
                photoStripView.setSmooth(true);

                // Add directly to container
                photoStripContainer.getChildren().add(photoStripView);

                System.out.println("Displaying vertical photostrip");
            }

            // Debug output
            System.out.println("Photostrip dimensions: " +
                    photoStripImage.getWidth() + "x" + photoStripImage.getHeight());
        } else {
            System.err.println("Warning: Attempting to display null photostrip image");
            createPlaceholderPhotoStrip();
        }
    }

    private void createPlaceholderPhotoStrip() {
        final int width = 170;
        final int height = 400;

        // Create a JavaFX Pane to represent the placeholder
        Pane placeholder = new Pane();
        placeholder.setPrefSize(width, height);
        placeholder.setMinSize(width, height);
        placeholder.setMaxSize(width, height);
        placeholder.setStyle("-fx-background-color: #B8E6FF;");

        // Add placeholder photo frames
        for (int i = 0; i < 3; i++) {
            int y = 20 + (i * 130);

            // Photo frame
            Rectangle photoFrame = new Rectangle(10, y, 150, 120);
            photoFrame.setFill(Color.WHITE);

            // Hills graphic
            Polygon hills = new Polygon(
                    10, y + 120,
                    60, y + 80,
                    110, y + 90,
                    160, y + 120
            );
            hills.setFill(Color.rgb(139, 195, 74));

            // Clouds
            Circle cloud1 = new Circle(50, y + 30, 20);
            cloud1.setFill(Color.WHITE);

            Circle cloud2 = new Circle(65, y + 25, 15);
            cloud2.setFill(Color.WHITE);

            placeholder.getChildren().addAll(photoFrame, hills, cloud1, cloud2);
        }

        // Add text label
        Label noPhotosLabel = new Label("No Photos Available");
        noPhotosLabel.setLayoutX(35);
        noPhotosLabel.setLayoutY(180);
        noPhotosLabel.setTextFill(Color.web("#4A4A4A"));
        noPhotosLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        placeholder.getChildren().add(noPhotosLabel);

        photoStripContainer.getChildren().clear();
        photoStripContainer.getChildren().add(placeholder);
    }

    /**
     * Handle the restart session action
     */
    private void handleRestartSession() {
        try {
            if (navigationCallback != null) {
                navigationCallback.onRestartSession();
            } else {
                // Fallback behavior if no callback is set
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("New Session");
                alert.setHeaderText(null);
                alert.setContentText("Starting a new photobooth session...");
                alert.showAndWait();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error restarting session", e);
            showErrorDialog("Restart Error", "Unable to start new session. Please restart the application.");
        }
    }

    /**
     * Handle the download action with proper error handling
     */
    private void handleDownload() {
        try {
            // Validate that we have a photostrip to download
            if (finalPhotoStrip == null) {
                showErrorDialog("Download Error", "No photostrip available for download. Please try again.");
                return;
            }

            // Validate image dimensions
            if (finalPhotoStrip.getWidth() <= 0 || finalPhotoStrip.getHeight() <= 0) {
                showErrorDialog("Download Error", "Invalid photostrip image. Please try again.");
                return;
            }

            // Create file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Photo Strip");
            fileChooser.setInitialFileName("nostalgia_photostrip_" + System.currentTimeMillis() + ".png");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"),
                    new FileChooser.ExtensionFilter("JPEG files (*.jpg)", "*.jpg"),
                    new FileChooser.ExtensionFilter("All files", "*.*")
            );

            // Show save dialog
            File file = fileChooser.showSaveDialog(currentStage);

            if (file != null) {
                // Debug output
                System.out.println("Saving photostrip to: " + file.getAbsolutePath());
                System.out.println("Image size: " + finalPhotoStrip.getWidth() + "x" + finalPhotoStrip.getHeight());

                // Save file asynchronously to prevent UI blocking
                CompletableFuture.runAsync(() -> savePhotoStrip(file, finalPhotoStrip))
                        .thenRun(() -> javafx.application.Platform.runLater(() -> {
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Download Complete");
                            successAlert.setHeaderText(null);
                            successAlert.setContentText("Your photo strip has been saved successfully!\n\nLocation: " + file.getAbsolutePath());
                            successAlert.showAndWait();

                            if (navigationCallback != null) {
                                navigationCallback.onDownloadComplete();
                            }
                        }))
                        .exceptionally(throwable -> {
                            LOGGER.log(Level.SEVERE, "Error saving photo strip", throwable);
                            javafx.application.Platform.runLater(() ->
                                    showErrorDialog("Save Error", "Failed to save photo strip: " + throwable.getMessage()));
                            return null;
                        });
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in download process", e);
            showErrorDialog("Download Error", "An unexpected error occurred during download: " + e.getMessage());
        }
    }

    /**
     * Save the photo strip to the specified file without using SwingFXUtils
     * @param file Target file for saving
     * @param image Image to save
     */
    private void savePhotoStrip(File file, Image image) {
        try {
            // Get dimensions
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();

            // Create a BufferedImage
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // Get pixel data from the JavaFX image
            PixelReader pixelReader = image.getPixelReader();

            // Transfer pixel data
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    javafx.scene.paint.Color color = pixelReader.getColor(x, y);
                    int argb = getARGB(color);
                    bufferedImage.setRGB(x, y, argb);
                }
            }

            // Determine file format based on extension
            String extension = getFileExtension(file.getName()).toLowerCase();

            // For JPEG, we need to convert to RGB (no alpha)
            if (extension.equals("jpg") || extension.equals("jpeg")) {
                BufferedImage rgbImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                java.awt.Graphics2D g2d = rgbImage.createGraphics();
                g2d.setColor(java.awt.Color.WHITE);
                g2d.fillRect(0, 0, width, height);
                g2d.drawImage(bufferedImage, 0, 0, null);
                g2d.dispose();
                bufferedImage = rgbImage;
            }

            // Write the image to file
            boolean success = ImageIO.write(bufferedImage, extension.isEmpty() ? "png" : extension, file);

            if (!success) {
                throw new IOException("No appropriate image writer found for the file format");
            }

        } catch (IOException e) {
            throw new RuntimeException("Error saving photo strip: " + e.getMessage(), e);
        }
    }

    /**
     * Convert JavaFX Color to ARGB integer
     */
    private int getARGB(javafx.scene.paint.Color color) {
        int a = (int) (color.getOpacity() * 255);
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Get file extension from filename
     * @param filename The filename
     * @return File extension or empty string if none found
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return (lastDotIndex >= 0) ? filename.substring(lastDotIndex + 1) : "";
    }

    /**
     * Show error dialog with consistent styling
     * @param title Dialog title
     * @param message Error message
     */
    private void showErrorDialog(String title, String message) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}