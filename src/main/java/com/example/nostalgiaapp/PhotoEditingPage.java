package com.example.nostalgiaapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class PhotoEditingPage extends BorderPane {

    private VBox photostripPreview;
    private HBox horizontalPhotostripPreview;
    private List<ImageView> photoImages;
    private List<Image> sourceImages;
    private boolean isVerticalLayout = true;
    private Color selectedBackgroundColor = Color.WHITE;
    private Color selectedPhotostripColor = Color.web("#ADD8E6"); // Light blue as shown in image
    private String selectedFilter = "no-filter";
    private boolean enableDate = true;
    private Stage currentStage;
    private NostalgiaApp mainApp;

    // Layout selection buttons for highlighting
    private Button verticalLayoutBtn;
    private Button horizontalLayoutBtn;

    // Color options matching the design
    private final Color[] backgroundColorOptions = {
            Color.BLACK, Color.BLUE, Color.web("#8B4513"), Color.LIGHTGRAY,
            Color.HOTPINK, Color.GOLD, Color.LIGHTPINK, Color.WHITE
    };

    private final Color[] photostripColorOptions = {
            Color.BLACK, Color.BLUE, Color.web("#8B4513"), Color.WHITE,
            Color.HOTPINK, Color.GOLD, Color.LIGHTPINK, Color.LIGHTGRAY
    };

    // Filter options
    private final String[] filterOptions = {
            "B&W", "Bright", "Sepia", "Warm", "Cold"
    };

    // Sticker options (placeholder emojis)
    private final String[] stickerOptions = {
            "🧠", "🌍", "🐯", "❌", "🌟", "⭐", "🦄", "🗑️"
    };

    public PhotoEditingPage(List<Image> photos, Stage stage, NostalgiaApp mainApp) {
        this.sourceImages = photos != null ? photos : new ArrayList<>();
        this.currentStage = stage;
        this.mainApp = mainApp;
        this.photoImages = new ArrayList<>();

        // Set main background color
        this.setStyle("-fx-background-color: #FFF4C5;");

        initializeComponents();
        setupLayout();
        loadPhotosIntoPreview();
    }

    private void initializeComponents() {
        // Create header
        HBox header = createHeader();
        this.setTop(header);

        // Create main content
        HBox mainContent = createMainContent();
        this.setCenter(mainContent);
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_RIGHT);
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setStyle("-fx-background-color: #FFF4C5;");

        // Home button
        Button homeBtn = new Button("🏠");
        homeBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 24px; -fx-text-fill: #8B4513;");
        homeBtn.setOnAction(e -> handleHomeButton());

        // Title
        Text title = new Text("Nostalgique");
        title.setFont(Font.font("Brush Script MT", FontWeight.NORMAL, 32));
        title.setFill(Color.web("#8B4513"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(homeBtn, spacer, title);
        return header;
    }

    private HBox createMainContent() {
        HBox mainContent = new HBox(30);
        mainContent.setPadding(new Insets(20, 40, 20, 40));
        mainContent.setAlignment(Pos.TOP_CENTER);

        // Left side - Photo preview
        VBox leftSide = createPhotoPreviewSection();

        // Right side - Controls
        VBox rightSide = createControlsSection();

        mainContent.getChildren().addAll(leftSide, rightSide);
        return mainContent;
    }

    private VBox createPhotoPreviewSection() {
        VBox leftSide = new VBox(20);
        leftSide.setAlignment(Pos.TOP_CENTER);
        leftSide.setPrefWidth(450);

        createPhotostripPreviews();

        // Initially show vertical layout
        leftSide.getChildren().add(photostripPreview);

        return leftSide;
    }

    private void createPhotostripPreviews() {
        // Vertical photostrip preview
        photostripPreview = new VBox(8);
        photostripPreview.setPadding(new Insets(25));
        photostripPreview.setAlignment(Pos.TOP_RIGHT);
        photostripPreview.setStyle("-fx-background-color: #ADD8E6; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 2, 2);");
        photostripPreview.setPrefWidth(0);  // CHANGE THIS TO: 347.1
        photostripPreview.setPrefHeight(1013.7/3);

        // Horizontal photostrip preview
        horizontalPhotostripPreview = new HBox(8);
        horizontalPhotostripPreview.setPadding(new Insets(25));
        horizontalPhotostripPreview.setAlignment(Pos.CENTER);
        horizontalPhotostripPreview.setStyle("-fx-background-color: #ADD8E6; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 2, 2);");
        horizontalPhotostripPreview.setPrefWidth(0);
        horizontalPhotostripPreview.setPrefHeight(347.1/3);
    }

    private VBox createControlsSection() {
        VBox rightSide = new VBox(25);
        rightSide.setPrefWidth(500);
        rightSide.setAlignment(Pos.TOP_LEFT);

        // Layout chooser section
        VBox layoutSection = createLayoutSection();

        // Stickers section
        VBox stickersSection = createStickersSection();

        // Background and Photostrip color sections
        HBox colorsSection = createColorsSection();

        // Filters section
        VBox filtersSection = createFiltersSection();

        // Date toggle section
        VBox dateSection = createDateSection();

        // Action buttons
        HBox actionButtons = createActionButtons();

        rightSide.getChildren().addAll(
                layoutSection, stickersSection, colorsSection,
                filtersSection, dateSection, actionButtons
        );

        return rightSide;
    }

    private VBox createLayoutSection() {
        VBox layoutSection = new VBox(15);

        Text layoutLabel = new Text("Choose Layout");
        layoutLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        layoutLabel.setFill(Color.web("#333333"));

        HBox layoutOptions = new HBox(20);
        layoutOptions.setAlignment(Pos.CENTER_LEFT);

        // Vertical layout option
        VBox verticalOption = new VBox(8);
        verticalOption.setAlignment(Pos.CENTER);

        // Create vertical layout preview
        VBox verticalPreview = new VBox(3);
        verticalPreview.setAlignment(Pos.CENTER);
        for (int i = 0; i < 4; i++) {
            javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(35, 25);
            rect.setFill(Color.BLACK);
            rect.setStroke(Color.GRAY);
            rect.setStrokeWidth(1);
            verticalPreview.getChildren().add(rect);
        }

        verticalLayoutBtn = new Button();
        verticalLayoutBtn.setGraphic(verticalPreview);
        verticalLayoutBtn.setPrefSize(60, 80);
        verticalLayoutBtn.setStyle("-fx-background-color: " + (isVerticalLayout ? "#4CAF50" : "white") +
                "; -fx-border-color: #333; -fx-border-width: 2; -fx-background-radius: 5;");
        verticalLayoutBtn.setOnAction(e -> switchToVerticalLayout());

        verticalOption.getChildren().add(verticalLayoutBtn);

        // Horizontal layout option
        VBox horizontalOption = new VBox(8);
        horizontalOption.setAlignment(Pos.CENTER);

        // Create horizontal layout preview
        HBox horizontalPreview = new HBox(3);
        horizontalPreview.setAlignment(Pos.CENTER);
        for (int i = 0; i < 3; i++) {
            javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(25, 35);
            rect.setFill(Color.BLACK);
            rect.setStroke(Color.GRAY);
            rect.setStrokeWidth(1);
            horizontalPreview.getChildren().add(rect);
        }

        horizontalLayoutBtn = new Button();
        horizontalLayoutBtn.setGraphic(horizontalPreview);
        horizontalLayoutBtn.setPrefSize(80, 60);
        horizontalLayoutBtn.setStyle("-fx-background-color: " + (!isVerticalLayout ? "#4CAF50" : "white") +
                "; -fx-border-color: #333; -fx-border-width: 2; -fx-background-radius: 5;");
        horizontalLayoutBtn.setOnAction(e -> switchToHorizontalLayout());

        horizontalOption.getChildren().add(horizontalLayoutBtn);

        layoutOptions.getChildren().addAll(verticalOption, horizontalOption);
        layoutSection.getChildren().addAll(layoutLabel, layoutOptions);

        return layoutSection;
    }

    private VBox createStickersSection() {
        VBox stickersSection = new VBox(15);

        Text stickersLabel = new Text("Stickers");
        stickersLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        stickersLabel.setFill(Color.web("#333333"));

        // Create sticker grid
        GridPane stickerGrid = new GridPane();
        stickerGrid.setHgap(12);
        stickerGrid.setVgap(12);

        for (int i = 0; i < stickerOptions.length; i++) {
            Button stickerBtn = new Button(stickerOptions[i]);
            stickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                    "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 50%;");

            stickerBtn.setOnAction(e -> handleStickerSelection());

            stickerGrid.add(stickerBtn, i % 4, i / 4);
        }

        stickersSection.getChildren().addAll(stickersLabel, stickerGrid);
        return stickersSection;
    }

    private HBox createColorsSection() {
        HBox colorsSection = new HBox(40);

        // Background colors
        VBox backgroundSection = createColorSection("Background", backgroundColorOptions, true);

        // Photostrip colors
        VBox photostripSection = createColorSection("Photostrip", photostripColorOptions, false);

        colorsSection.getChildren().addAll(backgroundSection, photostripSection);
        return colorsSection;
    }

    private VBox createColorSection(String title, Color[] colors, boolean isBackground) {
        VBox colorSection = new VBox(15);

        Text colorLabel = new Text(title);
        colorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        colorLabel.setFill(Color.web("#333333"));

        GridPane colorGrid = new GridPane();
        colorGrid.setHgap(8);
        colorGrid.setVgap(8);

        for (int i = 0; i < colors.length; i++) {
            Circle colorCircle = new Circle(18);
            colorCircle.setFill(colors[i]);
            colorCircle.setStroke(Color.GRAY);
            colorCircle.setStrokeWidth(2);
            colorCircle.setStyle("-fx-cursor: hand;");

            final Color color = colors[i];
            colorCircle.setOnMouseClicked(e -> {
                if (isBackground) {
                    selectedBackgroundColor = color;
                    // Background color doesn't affect the photostrip directly in this design
                } else {
                    selectedPhotostripColor = color;
                    updatePhotostripColors();
                }
            });

            colorGrid.add(colorCircle, i % 4, i / 4);
        }

        colorSection.getChildren().addAll(colorLabel, colorGrid);
        return colorSection;
    }

    private VBox createFiltersSection() {
        VBox filtersSection = new VBox(15);

        Text filtersLabel = new Text("Filters");
        filtersLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        filtersLabel.setFill(Color.web("#333333"));

        HBox filterRow1 = new HBox(10);
        filterRow1.setAlignment(Pos.CENTER_LEFT);

        HBox filterRow2 = new HBox(10);
        filterRow2.setAlignment(Pos.CENTER_LEFT);

        for (int i = 0; i < filterOptions.length; i++) {
            Button filterBtn = new Button(filterOptions[i]);
            filterBtn.setStyle("-fx-background-color: #4A4A4A; -fx-text-fill: white; -fx-background-radius: 15; " +
                    "-fx-padding: 8 16; -fx-font-weight: bold;");
            filterBtn.setPrefWidth(80);

            final String filter = filterOptions[i];
            filterBtn.setOnAction(e -> {
                selectedFilter = filter;
                applyFilter();
            });

            if (i < 3) {
                filterRow1.getChildren().add(filterBtn);
            } else {
                filterRow2.getChildren().add(filterBtn);
            }
        }

        // Add reset button
        Button resetBtn = new Button("Reset");
        resetBtn.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: white; -fx-background-radius: 15; " +
                "-fx-padding: 8 16; -fx-font-weight: bold;");
        resetBtn.setPrefWidth(80);
        resetBtn.setOnAction(e -> {
            selectedFilter = "no-filter";
            applyFilter();
        });
        filterRow2.getChildren().add(resetBtn);

        filtersSection.getChildren().addAll(filtersLabel, filterRow1, filterRow2);
        return filtersSection;
    }

    private VBox createDateSection() {
        VBox dateSection = new VBox(15);

        HBox dateToggle = new HBox(15);
        dateToggle.setAlignment(Pos.CENTER_LEFT);

        // Create custom toggle switch
        ToggleButton dateSwitch = new ToggleButton();
        dateSwitch.setText("GENERATE DATE");
        dateSwitch.setSelected(enableDate);
        dateSwitch.setStyle("-fx-background-color: #FF8C00; -fx-text-fill: white; -fx-background-radius: 20; " +
                "-fx-padding: 10 20; -fx-font-weight: bold;");

        dateSwitch.setOnAction(e -> {
            enableDate = dateSwitch.isSelected();
            updateDateDisplay();
        });

        dateToggle.getChildren().add(dateSwitch);
        dateSection.getChildren().add(dateToggle);

        return dateSection;
    }

    private HBox createActionButtons() {
        HBox actionButtons = new HBox(20);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Button continueBtn = new Button("CONTINUE");
        continueBtn.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 10; -fx-padding: 12 25; -fx-font-size: 14px;");
        continueBtn.setOnAction(e -> handleContinue());

        actionButtons.getChildren().add(continueBtn);
        return actionButtons;
    }

    private void setupLayout() {
        updatePhotostripLayout();
    }

    private void loadPhotosIntoPreview() {
        photoImages.clear();

        try {
            int photoCount = isVerticalLayout ? Math.min(4, sourceImages.size()) : Math.min(3, sourceImages.size());

            for (int i = 0; i < photoCount; i++) {
                ImageView imageView = new ImageView();

                if (i < sourceImages.size() && sourceImages.get(i) != null) {
                    imageView.setImage(sourceImages.get(i));
                } else {
                    // Create placeholder if no image available
                    imageView.setStyle("-fx-background-color: lightgray;");
                }

                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                if (isVerticalLayout) {
                    imageView.setFitWidth(280);
                    imageView.setFitHeight(90);
                } else {
                    imageView.setFitWidth(90);
                    imageView.setFitHeight(120);
                }

                photoImages.add(imageView);
            }

            updatePhotostripLayout();

        } catch (Exception e) {
            System.err.println("Error loading photos into preview: " + e.getMessage());
            showErrorDialog("Photo Loading Error", "Unable to load photos for editing. Please try again.");
        }
    }

    private void switchToVerticalLayout() {
        if (!isVerticalLayout) {
            isVerticalLayout = true;
            updateLayoutButtons();
            loadPhotosIntoPreview();

            // Switch preview display
            VBox leftSide = (VBox) ((HBox) this.getCenter()).getChildren().get(0);
            leftSide.getChildren().clear();
            leftSide.getChildren().add(photostripPreview);
        }
    }

    private void switchToHorizontalLayout() {
        if (isVerticalLayout) {
            isVerticalLayout = false;
            updateLayoutButtons();
            loadPhotosIntoPreview();

            // Switch preview display
            VBox leftSide = (VBox) ((HBox) this.getCenter()).getChildren().get(0);
            leftSide.getChildren().clear();
            leftSide.getChildren().add(horizontalPhotostripPreview);
        }
    }

    private void updateLayoutButtons() {
        verticalLayoutBtn.setStyle("-fx-background-color: " + (isVerticalLayout ? "#4CAF50" : "white") +
                "; -fx-border-color: #333; -fx-border-width: 2; -fx-background-radius: 5;");
        horizontalLayoutBtn.setStyle("-fx-background-color: " + (!isVerticalLayout ? "#4CAF50" : "white") +
                "; -fx-border-color: #333; -fx-border-width: 2; -fx-background-radius: 5;");
    }

    private void updatePhotostripLayout() {
        if (isVerticalLayout) {
            photostripPreview.getChildren().clear();

            // Add photo frames vertically
            for (ImageView photo : photoImages) {
                photostripPreview.getChildren().add(photo);
            }

            // Add date if enabled
            if (enableDate) {
                Text dateLabel = new Text(java.time.LocalDate.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                dateLabel.setStyle("-fx-font-size: 14px; -fx-fill: #333;");
                photostripPreview.getChildren().add(dateLabel);
            }
        } else {
            horizontalPhotostripPreview.getChildren().clear();

            // Add photo frames horizontally
            for (ImageView photo : photoImages) {
                horizontalPhotostripPreview.getChildren().add(photo);
            }
        }
    }

    private void updatePhotostripColors() {
        String stripColor = colorToHex(selectedPhotostripColor);

        if (isVerticalLayout) {
            photostripPreview.setStyle("-fx-background-color: " + stripColor + "; -fx-background-radius: 15; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 2, 2);");
        } else {
            horizontalPhotostripPreview.setStyle("-fx-background-color: " + stripColor + "; -fx-background-radius: 15; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 2, 2);");
        }
    }

    private void applyFilter() {
        // Apply selected filter to all photos
        for (ImageView photo : photoImages) {
            switch (selectedFilter) {
                case "B&W":
                    photo.setStyle("-fx-effect: coloradjust(0, -1, 0, 0);"); // Black and white
                    break;
                case "Bright":
                    photo.setStyle("-fx-effect: coloradjust(0, 0, 0.3, 0);"); // Bright
                    break;
                case "Sepia":
                    photo.setStyle("-fx-effect: coloradjust(0.2, 0, 0, 0.1);"); // Sepia-like
                    break;
                case "Warm":
                    photo.setStyle("-fx-effect: coloradjust(0.1, 0, -0.2, 0);"); // Warm
                    break;
                case "Cold":
                    photo.setStyle("-fx-effect: coloradjust(-0.2, 0, 0, 0);"); // Cool
                    break;
                default:
                    photo.setStyle(""); // No filter
                    break;
            }
        }
    }

    private void updateDateDisplay() {
        updatePhotostripLayout(); // Refresh the layout to show/hide date
    }

    private void handleStickerSelection() {
        showErrorDialog("Stickers Not Available",
                "Sticker functionality is not yet implemented. This feature will be added in a future update.");
    }

    private void handleContinue() {
        showErrorDialog("Continue Not Available",
                "Continue functionality is not yet implemented. This will proceed to the next step in a future update.");
    }

    private void handleHomeButton() {
        try {
            if (mainApp != null) {
                currentStage.close();
                mainApp.showMainWindow();
            } else {
                System.exit(0);
            }
        } catch (Exception e) {
            System.err.println("Error returning to home: " + e.getMessage());
        }
    }

    private String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}