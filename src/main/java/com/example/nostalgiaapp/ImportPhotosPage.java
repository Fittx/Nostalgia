package com.example.nostalgiaapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import java.util.ArrayList;


import java.io.File;
import java.util.List;

public class ImportPhotosPage {

    private Stage stage;
    private NostalgiaApp mainApp;
    private VBox[] photoSlots = new VBox[3];
    private ImageView[] photoViews = new ImageView[3];
    private Button[] deleteButtons = new Button[3];
    private int selectedSlot = -1;
    private boolean[] hasPhoto = new boolean[3];
    private ArrayList<Image> importedPhotos = new ArrayList<>();

    public void show(Stage stage, NostalgiaApp mainApp) {
        this.stage = stage;
        this.mainApp = mainApp;

        // Main container with cream background
        VBox mainContainer = new VBox();
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setSpacing(30);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: #FFF4C5;");

        // Header with back button and title
        HBox header = createHeader();

        // Upload title
        Text uploadTitle = new Text("Upload your photo/s!");
        uploadTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        uploadTitle.setFill(Color.web("#333333"));

        // Photo slots container
        HBox photosContainer = new HBox();
        photosContainer.setAlignment(Pos.CENTER);
        photosContainer.setSpacing(30);
        photosContainer.setPadding(new Insets(30, 0, 30, 0));

        // Create three photo slots
        for (int i = 0; i < 3; i++) {
            VBox photoSlot = createPhotoSlot(i);
            photoSlots[i] = photoSlot;
            photosContainer.getChildren().add(photoSlot);
        }

        // Submit button
        Button submitBtn = new Button("Submit Images");
        submitBtn.setPrefWidth(200);
        submitBtn.setPrefHeight(45);
        submitBtn.setStyle("-fx-background-color: #FF8C42; -fx-text-fill: white; " +
                "-fx-background-radius: 25; -fx-font-size: 14px; -fx-font-weight: bold;");

        submitBtn.setOnAction(e -> {
            // Collect only non-null images
            ArrayList<Image> photosForEdit = new ArrayList<>();
            for (Image img : importedPhotos) {
                if (img != null) {
                    photosForEdit.add(img);
                }
            }

            if (!photosForEdit.isEmpty()) {
                try {
                    PhotoEditingPage editPage = new PhotoEditingPage(photosForEdit, stage, mainApp);
                    Scene editScene = new Scene(editPage, 1000, 700);

                    stage.setScene(editScene);
                    stage.setTitle("Nostalgia - Edit Photos");
                    stage.setMinHeight(700);
                    stage.setMinWidth(1000);
                    stage.setResizable(true);

                    System.out.println("✅ Navigated to Photo Editing page with " + photosForEdit.size() + " photos");

                } catch (Exception ex) {
                    System.err.println("❌ Error navigating to edit page: " + ex.getMessage());
                    ex.printStackTrace();

                    // Show error dialog and stay on import page
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Navigation Error");
                        alert.setHeaderText("Cannot open photo editor");
                        alert.setContentText("There was an error opening the photo editing page. Please try again.");
                        alert.showAndWait();
                    });
                }
            } else {
                System.out.println("❌ No photos available to edit");

                // Show info dialog
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("No Photos");
                    alert.setHeaderText("No photos to edit");
                    alert.setContentText("Please import some photos first before trying to edit them.");
                    alert.showAndWait();
                });
            }
        });


        // Bottom tagline
        Text tagline = new Text("CAPTURE A GREAT MOMENTS WITH YOUR FRIENDS");
        tagline.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        tagline.setFill(Color.web("#333333"));
        tagline.setUnderline(true);

        // Add all elements to main container
        mainContainer.getChildren().addAll(header, uploadTitle, photosContainer, submitBtn, tagline);

        Scene scene = new Scene(mainContainer, 1000, 700);
        stage.setTitle("Import Photos - Nostalgia");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(700);
        stage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 20, 10, 20));

        // Back button
        Button backBtn = new Button("←");
        backBtn.setPrefWidth(40);
        backBtn.setPrefHeight(40);
        backBtn.setStyle("-fx-background-color: #FFD700; -fx-background-radius: 20; " +
                "-fx-font-size: 18px; -fx-font-weight: bold;");
        backBtn.setOnAction(e -> {
            stage.close();
            mainApp.showMainWindow();
        });

        // Nostalgia title
        Text nostalgiaTitle = new Text("  Nostalgia");
        nostalgiaTitle.setFont(Font.font("Brush Script MT", FontWeight.NORMAL, 24));
        nostalgiaTitle.setFill(Color.web("#8B4513"));

        header.getChildren().addAll(backBtn, nostalgiaTitle);
        return header;
    }

    private VBox createPhotoSlot(int index) {
        VBox slot = new VBox();
        slot.setAlignment(Pos.CENTER);
        slot.setPrefWidth(250);
        slot.setPrefHeight(250);
        slot.setMinWidth(250);  // Add minimum width constraint
        slot.setMinHeight(250); // Add minimum height constraint
        slot.setMaxWidth(250);
        slot.setMaxHeight(250);
        slot.setStyle("-fx-background-color: #F5F5DC; -fx-background-radius: 20; " +
                "-fx-border-color: #D3D3D3; -fx-border-width: 2; -fx-border-radius: 20; " +
                "-fx-padding: 15;");

        // Create placeholder image icon
        ImageView placeholderIcon = new ImageView();
        try {
            Image galleryImage = new Image(getClass().getResourceAsStream("/com/example/nostalgiaapp/Images Background/gallery.png"));
            placeholderIcon.setImage(galleryImage);
            placeholderIcon.setFitWidth(60);
            placeholderIcon.setFitHeight(60);
            placeholderIcon.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("Could not load gallery icon: " + e.getMessage());
        }

        // Create ImageView for actual photos (initially hidden)
        ImageView imageView = new ImageView();
        imageView.setFitWidth(220);
        imageView.setFitHeight(220);
        imageView.setPreserveRatio(true);  // Keep aspect ratio to avoid stretching
        imageView.setSmooth(true);
        imageView.setVisible(false);

        // Create rounded rectangle clip for the image
        javafx.scene.shape.Rectangle imageClip = new javafx.scene.shape.Rectangle(220, 220);
        imageClip.setArcWidth(15);
        imageClip.setArcHeight(15);
        imageView.setClip(imageClip);

        photoViews[index] = imageView;

        // Create delete button (initially hidden)
        Button deleteBtn = new Button();
        deleteBtn.setPrefWidth(25);
        deleteBtn.setPrefHeight(25);
        deleteBtn.setStyle("-fx-background-color: #FF4444; -fx-text-fill: white; " +
                "-fx-background-radius: 12.5; -fx-font-size: 12px; -fx-font-weight: bold;");

        // Load and set the delete icon
        try {
            Image deleteIcon = new Image(getClass().getResourceAsStream("/com/example/nostalgiaapp/Images Background/delete icon.png"));
            ImageView deleteIconView = new ImageView(deleteIcon);
            deleteIconView.setFitWidth(12);
            deleteIconView.setFitHeight(12);
            deleteIconView.setPreserveRatio(true);
            deleteBtn.setGraphic(deleteIconView);
        } catch (Exception e) {
            System.out.println("Could not load delete icon, using text fallback: " + e.getMessage());
            deleteBtn.setText("✕");
        }

        deleteBtn.setVisible(false);
        deleteBtn.setOnAction(e -> removePhoto(index));
        deleteButtons[index] = deleteBtn;

        // Stack pane to overlay delete button on top of images
        StackPane stackPane = new StackPane();
        stackPane.setPrefWidth(220);
        stackPane.setPrefHeight(220);
        stackPane.setMinWidth(220);  // Add minimum constraints
        stackPane.setMinHeight(220); // Add minimum constraints
        stackPane.setMaxWidth(220);  // Add maximum constraints
        stackPane.setMaxHeight(220); // Add maximum constraints
        stackPane.getChildren().addAll(placeholderIcon, imageView, deleteBtn);
        StackPane.setAlignment(deleteBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(deleteBtn, new Insets(3, 3, 0, 0));

        slot.getChildren().add(stackPane);

        // Click handler for file selection
        slot.setOnMouseClicked(e -> handleSlotClick(index));

        // Drag and drop handlers
        slot.setOnDragOver(this::handleDragOver);
        slot.setOnDragDropped(e -> handleDragDropped(e, index));

        return slot;
    }

    private void handleSlotClick(int index) {
        if (!hasPhoto[index]) {
            // Open file chooser for empty slots
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
            );

            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                loadImageToSlot(selectedFile, index);
            }
        } else {
            // Select the slot if it has a photo
            selectSlot(index);
        }
    }

    private void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != event.getSource() &&
                event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    private void handleDragDropped(DragEvent event, int index) {
        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasFiles()) {
            List<File> files = db.getFiles();
            if (!files.isEmpty()) {
                File file = files.get(0);
                if (isImageFile(file)) {
                    loadImageToSlot(file, index);
                    success = true;
                }
            }
        }

        event.setDropCompleted(success);
        event.consume();
    }

    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") ||
                name.endsWith(".jpeg") || name.endsWith(".gif") ||
                name.endsWith(".bmp");
    }

    private void loadImageToSlot(File file, int index) {
        try {
            Image originalImage = new Image(file.toURI().toString());

            // Create permanently cropped image
            Image croppedImage = createCroppedSquareImage(originalImage);

            ImageView imageView = photoViews[index];

            // Store the cropped image for editing
            if (index < importedPhotos.size()) {
                importedPhotos.set(index, croppedImage);
            } else {
                // Ensure the list is large enough
                while (importedPhotos.size() <= index) {
                    importedPhotos.add(null);
                }
                importedPhotos.set(index, croppedImage);
            }

            // Set the cropped image to the ImageView
            imageView.setImage(croppedImage);
            imageView.setFitWidth(220);
            imageView.setFitHeight(220);
            imageView.setPreserveRatio(true);
            imageView.setViewport(null); // Clear any existing viewport

            imageView.setVisible(true);
            deleteButtons[index].setVisible(true);
            hasPhoto[index] = true;

            // Hide placeholder icon
            VBox slot = photoSlots[index];
            StackPane stackPane = (StackPane) slot.getChildren().get(0);
            stackPane.getChildren().get(0).setVisible(false); // Hide placeholder

            selectSlot(index);
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Image createCroppedSquareImage(Image originalImage) {
        double originalWidth = originalImage.getWidth();
        double originalHeight = originalImage.getHeight();

        // Determine the size of the square (use the smaller dimension)
        double squareSize = Math.min(originalWidth, originalHeight);

        // Calculate the starting position to center the crop
        double startX = (originalWidth - squareSize) / 2;
        double startY = (originalHeight - squareSize) / 2;

        // Create a new WritableImage for the cropped result
        javafx.scene.image.WritableImage croppedImage = new javafx.scene.image.WritableImage((int) squareSize, (int) squareSize);

        // Create a Canvas to draw the cropped portion
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(squareSize, squareSize);
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();

        // Draw the cropped portion of the original image
        gc.drawImage(originalImage, startX, startY, squareSize, squareSize, 0, 0, squareSize, squareSize);

        // Take a snapshot of the canvas to create the final cropped image
        javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);

        return canvas.snapshot(params, croppedImage);
    }

    private void removePhoto(int index) {
        photoViews[index].setImage(null);
        photoViews[index].setViewport(null); // Clear viewport
        photoViews[index].setVisible(false);
        deleteButtons[index].setVisible(false);
        hasPhoto[index] = false;

        // Remove from stored images
        if (index < importedPhotos.size()) {
            importedPhotos.set(index, null);
        }

        // Show placeholder icon again
        VBox slot = photoSlots[index];
        StackPane stackPane = (StackPane) slot.getChildren().get(0);
        stackPane.getChildren().get(0).setVisible(true); // Show placeholder

        // Remove selection if this slot was selected
        if (selectedSlot == index) {
            selectedSlot = -1;
            updateSlotSelection();
        }
    }

    private void selectSlot(int index) {
        selectedSlot = index;
        updateSlotSelection();
    }

    private void updateSlotSelection() {
        for (int i = 0; i < 3; i++) {
            if (i == selectedSlot && hasPhoto[i]) {
                // Purple border for selected slot (matching your reference image)
                photoSlots[i].setStyle("-fx-background-color: #F5F5DC; -fx-background-radius: 20; " +
                        "-fx-border-color: #8A2BE2; -fx-border-width: 3; -fx-border-radius: 20; " +
                        "-fx-padding: 15;");
            } else {
                // Normal border
                photoSlots[i].setStyle("-fx-background-color: #F5F5DC; -fx-background-radius: 20; " +
                        "-fx-border-color: #D3D3D3; -fx-border-width: 2; -fx-border-radius: 20; " +
                        "-fx-padding: 15;");
            }
        }
    }


}