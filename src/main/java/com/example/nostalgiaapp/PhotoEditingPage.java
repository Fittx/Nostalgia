package com.example.nostalgiaapp;

import java.util.ArrayList;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import java.util.List;
import javafx.scene.effect.ColorAdjust;


public class PhotoEditingPage extends BorderPane {

    private VBox photostripPreview;
    private HBox horizontalPhotostripPreview;
    private VBox photostripBackgroundVertical;
    private VBox photostripBackgroundHorizontal;
    private List<ImageView> photoImages;
    private List<Image> sourceImages;
    private boolean isVerticalLayout = true;
    private Color selectedBackgroundColor = Color.WHITE;
    private Color selectedPhotostripColor = Color.web("#ADD8E6"); // Light blue as shown in image
    private String selectedFilter = "no-filter";
    private boolean enableDate = true;
    private Stage currentStage;
    private NostalgiaApp mainApp;
    private List<String> axolotlStickerPaths;
    private List<String> catStickerPaths;
    private List<String> pandaStickerPaths;
    private List<ImageView> stickerViews;
    private String currentStickerType = "none";


    private void initializeStickerPaths() {
        axolotlStickerPaths = new ArrayList<>();
        catStickerPaths = new ArrayList<>();
        pandaStickerPaths = new ArrayList<>(); // ADD THIS LINE
        stickerViews = new ArrayList<>();

        // Add all axolotl sticker paths (existing code)
        String axolotlBasePath = "/com/example/nostalgiaapp/Stickers/axolotl-stickers/";
        axolotlStickerPaths.add(axolotlBasePath + "angry.png");
        axolotlStickerPaths.add(axolotlBasePath + "eat.png");
        axolotlStickerPaths.add(axolotlBasePath + "happy.png");
        axolotlStickerPaths.add(axolotlBasePath + "hi.png");
        axolotlStickerPaths.add(axolotlBasePath + "laugh.png");
        axolotlStickerPaths.add(axolotlBasePath + "love.png");
        axolotlStickerPaths.add(axolotlBasePath + "ok.png");
        axolotlStickerPaths.add(axolotlBasePath + "read.png");
        axolotlStickerPaths.add(axolotlBasePath + "sad.png");
        axolotlStickerPaths.add(axolotlBasePath + "sleep.png");

        // Add all cat sticker paths (existing code)
        String catBasePath = "/com/example/nostalgiaapp/Stickers/cat-stickers/";
        catStickerPaths.add(catBasePath + "angry.png");
        catStickerPaths.add(catBasePath + "eat.png");
        catStickerPaths.add(catBasePath + "full.png");
        catStickerPaths.add(catBasePath + "love.png");
        catStickerPaths.add(catBasePath + "mocking.png");
        catStickerPaths.add(catBasePath + "no.png");
        catStickerPaths.add(catBasePath + "run.png");
        catStickerPaths.add(catBasePath + "sad.png");
        catStickerPaths.add(catBasePath + "sing.png");
        catStickerPaths.add(catBasePath + "yes.png");

        // ADD ALL PANDA STICKER PATHS (NEW)
        String pandaBasePath = "/com/example/nostalgiaapp/Stickers/panda-stickers/";
        pandaStickerPaths.add(pandaBasePath + "angry.png");
        pandaStickerPaths.add(pandaBasePath + "birthday.png");
        pandaStickerPaths.add(pandaBasePath + "eat.png");
        pandaStickerPaths.add(pandaBasePath + "fever.png");
        pandaStickerPaths.add(pandaBasePath + "full.png");
        pandaStickerPaths.add(pandaBasePath + "happy.png");
        pandaStickerPaths.add(pandaBasePath + "hello.png");
        pandaStickerPaths.add(pandaBasePath + "love.png");
        pandaStickerPaths.add(pandaBasePath + "mocking.png");
        pandaStickerPaths.add(pandaBasePath + "peace.png");
    }



    // Layout selection buttons for highlighting
    private Button verticalLayoutBtn;
    private Button horizontalLayoutBtn;

    // Color options matching the design
    private final Color[] backgroundColorOptions = {
            Color.BLACK, Color.web("#c9edf7"), Color.web("#8B4513"), Color.LIGHTGRAY,
            Color.HOTPINK, Color.GOLD, Color.LIGHTPINK, Color.WHITE
    };

    private final Color[] photostripColorOptions = {
            Color.BLACK, Color.web("#c9edf7"), Color.web("#8B4513"), Color.LIGHTGRAY,
            Color.HOTPINK, Color.GOLD, Color.LIGHTPINK, Color.WHITE
    };

    // Filter options
    private final String[] filterOptions = {
            "B&W", "Bright", "Sepia", "Warm", "Cold"
    };


    public PhotoEditingPage(List<Image> photos, Stage stage, NostalgiaApp mainApp) {
        this.sourceImages = photos != null ? photos : new ArrayList<>();
        this.currentStage = stage;
        this.mainApp = mainApp;
        this.photoImages = new ArrayList<>();

        // Initialize sticker paths
        initializeStickerPaths();

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
        Text title = new Text("Nostalgia");
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
        leftSide.getChildren().add(photostripBackgroundVertical);

        return leftSide;
    }

    private void createPhotostripPreviews() {
        // Create background containers for photostrips
        photostripBackgroundVertical = new VBox();
        photostripBackgroundVertical.setAlignment(Pos.CENTER);
        photostripBackgroundVertical.setPadding(new Insets(15));
        photostripBackgroundVertical.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 2, 2);");

        photostripBackgroundHorizontal = new VBox();
        photostripBackgroundHorizontal.setAlignment(Pos.CENTER);
        photostripBackgroundHorizontal.setPadding(new Insets(15));
        photostripBackgroundHorizontal.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 2, 2);");

        // Create StackPane containers for layering photos and stickers
        StackPane verticalStackPane = new StackPane();
        StackPane horizontalStackPane = new StackPane();

        // Vertical photostrip preview
        photostripPreview = new VBox(5);
        photostripPreview.setPadding(new Insets(10));
        photostripPreview.setAlignment(Pos.CENTER);
        photostripPreview.setStyle("-fx-background-color: #ADD8E6; -fx-background-radius: 10;");
        photostripPreview.setPrefWidth(180/2);
        photostripPreview.setMaxWidth(180/2);
        photostripPreview.setPrefHeight(350);
        photostripPreview.setMaxHeight(350);

        // Horizontal photostrip preview
        horizontalPhotostripPreview = new HBox(5);
        horizontalPhotostripPreview.setPadding(new Insets(10));
        horizontalPhotostripPreview.setAlignment(Pos.CENTER);
        horizontalPhotostripPreview.setStyle("-fx-background-color: #ADD8E6; -fx-background-radius: 10;");
        horizontalPhotostripPreview.setPrefWidth(380);
        horizontalPhotostripPreview.setMaxWidth(380);
        horizontalPhotostripPreview.setPrefHeight(120);
        horizontalPhotostripPreview.setMaxHeight(120);

        // Add photostrips to StackPanes
        verticalStackPane.getChildren().add(photostripPreview);
        horizontalStackPane.getChildren().add(horizontalPhotostripPreview);

        // Add StackPanes to backgrounds
        photostripBackgroundVertical.getChildren().add(verticalStackPane);
        photostripBackgroundHorizontal.getChildren().add(horizontalStackPane);

        // Set background container sizes
        photostripBackgroundVertical.setPrefWidth(212/2);
        photostripBackgroundVertical.setMaxWidth(212/2);
        photostripBackgroundVertical.setPrefHeight(380);
        photostripBackgroundVertical.setMaxHeight(380);

        photostripBackgroundHorizontal.setPrefWidth(410);
        photostripBackgroundHorizontal.setMaxWidth(410);
        photostripBackgroundHorizontal.setPrefHeight(150);
        photostripBackgroundHorizontal.setMaxHeight(150);
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

        // Date and Continue section combined
        HBox dateAndContinueSection = createDateAndContinueSection();

        rightSide.getChildren().addAll(
                layoutSection, stickersSection, colorsSection,
                filtersSection, dateAndContinueSection
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
        VBox verticalPreview = new VBox(2);
        verticalPreview.setAlignment(Pos.CENTER);
        for (int i = 0; i < 4; i++) {
            javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(25, 18);
            rect.setFill(Color.BLACK);
            rect.setStroke(Color.GRAY);
            rect.setStrokeWidth(0.5);
            verticalPreview.getChildren().add(rect);
        }

        verticalLayoutBtn = new Button();
        verticalLayoutBtn.setGraphic(verticalPreview);
        verticalLayoutBtn.setPrefSize(50, 90);
        verticalLayoutBtn.setStyle("-fx-background-color: " + (isVerticalLayout ? "#4CAF50" : "white") +
                "; -fx-border-color: #333; -fx-border-width: 2; -fx-background-radius: 5; -fx-cursor: hand;");
        verticalLayoutBtn.setOnAction(e -> switchToVerticalLayout());

        verticalOption.getChildren().add(verticalLayoutBtn);

        // Horizontal layout option
        VBox horizontalOption = new VBox(8);
        horizontalOption.setAlignment(Pos.CENTER);

        // Create horizontal layout preview
        HBox horizontalPreview = new HBox(2);
        horizontalPreview.setAlignment(Pos.CENTER);
        for (int i = 0; i < 3; i++) {
            javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(18, 25);
            rect.setFill(Color.BLACK);
            rect.setStroke(Color.GRAY);
            rect.setStrokeWidth(0.5);
            horizontalPreview.getChildren().add(rect);
        }

        horizontalLayoutBtn = new Button();
        horizontalLayoutBtn.setGraphic(horizontalPreview);
        horizontalLayoutBtn.setPrefSize(90, 50);
        horizontalLayoutBtn.setStyle("-fx-background-color: " + (!isVerticalLayout ? "#4CAF50" : "white") +
                "; -fx-border-color: #333; -fx-border-width: 2; -fx-background-radius: 5; -fx-cursor: hand;");
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

        // Create sticker grid - now only 4 buttons in a single row
        GridPane stickerGrid = new GridPane();
        stickerGrid.setHgap(12);
        stickerGrid.setVgap(12);

        // DECLARE ONLY THE FOUR BUTTONS WE NEED
        Button axolotlStickerBtn = new Button();
        Button catStickerBtn = new Button();
        Button pandaStickerBtn = new Button();
        Button resetStickerBtn = new Button();

        // Set up axolotl button appearance
        try {
            Image axolotlIcon = new Image(getClass().getResourceAsStream("/com/example/nostalgiaapp/Stickers/axolotl-stickers/hi.png"));
            ImageView iconView = new ImageView(axolotlIcon);
            iconView.setFitWidth(30);
            iconView.setFitHeight(30);
            iconView.setPreserveRatio(true);
            axolotlStickerBtn.setGraphic(iconView);
        } catch (Exception e) {
            axolotlStickerBtn.setText("🐯");
        }
        axolotlStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 50%; -fx-cursor: hand;");

        // Set up cat button appearance
        try {
            Image catIcon = new Image(getClass().getResourceAsStream("/com/example/nostalgiaapp/Stickers/cat-stickers/yes.png"));
            ImageView catIconView = new ImageView(catIcon);
            catIconView.setFitWidth(30);
            catIconView.setFitHeight(30);
            catIconView.setPreserveRatio(true);
            catStickerBtn.setGraphic(catIconView);
        } catch (Exception e) {
            catStickerBtn.setText("🐱");
        }
        catStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 50%; -fx-cursor: hand;");

        // Set up panda button appearance
        try {
            Image pandaIcon = new Image(getClass().getResourceAsStream("/com/example/nostalgiaapp/Stickers/panda-stickers/mocking.png"));
            ImageView pandaIconView = new ImageView(pandaIcon);
            pandaIconView.setFitWidth(30);
            pandaIconView.setFitHeight(30);
            pandaIconView.setPreserveRatio(true);
            pandaStickerBtn.setGraphic(pandaIconView);
        } catch (Exception e) {
            pandaStickerBtn.setText("🐼");
        }
        pandaStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 50%; -fx-cursor: hand;");

        // Set up reset button appearance with the PNG image
        try {
            Image resetIcon = new Image(getClass().getResourceAsStream("/com/example/nostalgiaapp/Images Background/reset bin.png"));
            ImageView resetIconView = new ImageView(resetIcon);
            resetIconView.setFitWidth(25);
            resetIconView.setFitHeight(25);
            resetIconView.setPreserveRatio(true);
            resetStickerBtn.setGraphic(resetIconView);
        } catch (Exception e) {
            resetStickerBtn.setText("🗑️"); // Fallback text if image fails to load
        }
        resetStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 50%; -fx-cursor: hand;");

        // SET THE ACTIONS FOR ALL BUTTONS
        axolotlStickerBtn.setOnAction(e -> {
            addRandomAxolotlStickers();
            currentStickerType = "axolotl";
            axolotlStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                    "-fx-background-color: white; -fx-border-color: #4CAF50; -fx-border-width: 3; -fx-border-radius: 50%; -fx-cursor: hand;");
            // Reset other buttons
            catStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                    "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 50%; -fx-cursor: hand;");
            pandaStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                    "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 50%; -fx-cursor: hand;");
            resetStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                    "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 50%; -fx-cursor: hand;");
        });

        catStickerBtn.setOnAction(e -> {
            addRandomCatStickers();
            currentStickerType = "cat";
            catStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                    "-fx-background-color: white; -fx-border-color: #4CAF50; -fx-border-width: 3; -fx-border-radius: 50%; -fx-cursor: hand;");
            // Reset other buttons
            axolotlStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                    "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 50%; -fx-cursor: hand;");
            pandaStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                    "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 50%; -fx-cursor: hand;");
            resetStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                    "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 50%; -fx-cursor: hand;");
        });

        pandaStickerBtn.setOnAction(e -> {
            addRandomPandaStickers();
            currentStickerType = "panda";
            pandaStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                    "-fx-background-color: white; -fx-border-color: #4CAF50; -fx-border-width: 3; -fx-border-radius: 50%; -fx-cursor: hand;");
            // Reset other buttons
            axolotlStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                    "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 50%; -fx-cursor: hand;");
            catStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                    "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 50%; -fx-cursor: hand;");
            resetStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                    "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 50%; -fx-cursor: hand;");
        });

        resetStickerBtn.setOnAction(e -> {
            clearStickers();
            currentStickerType = "none";
            // Reset all button styles
            axolotlStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                    "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 50%; -fx-cursor: hand;");
            catStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                    "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 50%; -fx-cursor: hand;");
            pandaStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                    "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 50%; -fx-cursor: hand;");

            // Visual feedback for reset button
            resetStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                    "-fx-background-color: white; -fx-border-color: #FF6B6B; -fx-border-width: 3; -fx-border-radius: 50%; -fx-cursor: hand;");
            // Reset the reset button style after delay
            javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), event -> {
                        resetStickerBtn.setStyle("-fx-font-size: 20px; -fx-background-radius: 50%; -fx-min-width: 45; -fx-min-height: 45; " +
                                "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 50%; -fx-cursor: hand;");
                    })
            );
            timeline.play();
        });

        // Add all 4 buttons to the grid in a single row
        stickerGrid.add(axolotlStickerBtn, 0, 0);
        stickerGrid.add(catStickerBtn, 1, 0);
        stickerGrid.add(pandaStickerBtn, 2, 0);
        stickerGrid.add(resetStickerBtn, 3, 0);

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
                    updateBackgroundColors();
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
                    "-fx-padding: 8 16; -fx-font-weight: bold; -fx-cursor: hand;");
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
                "-fx-padding: 8 16; -fx-font-weight: bold; -fx-cursor: hand;");
        resetBtn.setPrefWidth(80);
        resetBtn.setOnAction(e -> {
            selectedFilter = "no-filter";
            applyFilter();
        });
        filterRow2.getChildren().add(resetBtn);

        filtersSection.getChildren().addAll(filtersLabel, filterRow1, filterRow2);
        return filtersSection;
    }

    // FIXED: Combined date toggle and continue button in one horizontal section
    private HBox createDateAndContinueSection() {
        HBox dateAndContinueSection = new HBox(20);
        dateAndContinueSection.setAlignment(Pos.CENTER_LEFT);

        // Date toggle
        ToggleButton dateSwitch = new ToggleButton();
        dateSwitch.setText("GENERATE DATE");
        dateSwitch.setSelected(enableDate);
        dateSwitch.setStyle("-fx-background-color: #FF8C00; -fx-text-fill: white; -fx-background-radius: 20; " +
                "-fx-padding: 10 20; -fx-font-weight: bold; -fx-cursor: hand;");

        dateSwitch.setOnAction(e -> {
            enableDate = dateSwitch.isSelected();
            updateDateDisplay();
        });

        // Continue button
        Button continueBtn = new Button("CONTINUE");
        continueBtn.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 10; -fx-padding: 12 25; -fx-font-size: 14px; -fx-cursor: hand;");

        continueBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Continue Not Available");
            alert.setHeaderText(null);
            alert.setContentText("Continue functionality is not yet implemented. This will proceed to the next step in a future update.");
            alert.showAndWait();
        });

        dateAndContinueSection.getChildren().addAll(dateSwitch, continueBtn);
        return dateAndContinueSection;
    }

    private void setupLayout() {
        updatePhotostripLayout();
    }

    private void addRandomPandaStickers() {
        try {
            clearStickers();
            StackPane currentStackPane = getCurrentStackPane();
            if (currentStackPane == null) return;

            if (isVerticalLayout) {
                addVerticalLayoutPandaStickers(currentStackPane);
            } else {
                addHorizontalLayoutPandaStickers(currentStackPane);
            }
            System.out.println("Added panda stickers to " + (isVerticalLayout ? "vertical" : "horizontal") + " layout");
        } catch (Exception e) {
            System.err.println("Error adding panda stickers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addRandomAxolotlStickers() {
        try {
            clearStickers();
            StackPane currentStackPane = getCurrentStackPane();
            if (currentStackPane == null) return;

            if (isVerticalLayout) {
                addVerticalLayoutAxolotlStickers(currentStackPane);
            } else {
                addHorizontalLayoutAxolotlStickers(currentStackPane);
            }
            System.out.println("Added axolotl stickers to " + (isVerticalLayout ? "vertical" : "horizontal") + " layout");
        } catch (Exception e) {
            System.err.println("Error adding axolotl stickers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addRandomCatStickers() {
        try {
            clearStickers();
            StackPane currentStackPane = getCurrentStackPane();
            if (currentStackPane == null) return;

            if (isVerticalLayout) {
                addVerticalLayoutCatStickers(currentStackPane);
            } else {
                addHorizontalLayoutCatStickers(currentStackPane);
            }
            System.out.println("Added cat stickers to " + (isVerticalLayout ? "vertical" : "horizontal") + " layout");
        } catch (Exception e) {
            System.err.println("Error adding cat stickers: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private StackPane getCurrentStackPane() {
        VBox currentBackground = isVerticalLayout ? photostripBackgroundVertical : photostripBackgroundHorizontal;
        if (currentBackground.getChildren().isEmpty()) return null;
        return (StackPane) currentBackground.getChildren().get(0);
    }

    private void addVerticalLayoutAxolotlStickers(StackPane container) {
        StickerPosition[] stickerPositions = {
                new StickerPosition("hi.png", -40, -140),
                new StickerPosition("eat.png", 40, -100),
                new StickerPosition("happy.png", -35, -60),
                new StickerPosition("laugh.png", 30, -10),
                new StickerPosition("love.png", -35, 30),
                new StickerPosition("read.png", 35, 30),
                new StickerPosition("angry.png", 35, 80),
                new StickerPosition("ok.png", -35, 120),
                new StickerPosition("sad.png", 35, 120),
                new StickerPosition("sleep.png", 0, -160),
        };

        for (StickerPosition pos : stickerPositions) {
            addFixedSticker(container, "axolotl-stickers/" + pos.filename, pos.x, pos.y);
        }
    }

    private void addVerticalLayoutCatStickers(StackPane container) {
        StickerPosition[] stickerPositions = {
                new StickerPosition("yes.png", -40, -140),
                new StickerPosition("eat.png", 40, -100),
                new StickerPosition("love.png", -35, -60),
                new StickerPosition("mocking.png", 30, -10),
                new StickerPosition("full.png", -35, 30),
                new StickerPosition("run.png", 35, 30),
                new StickerPosition("angry.png", 35, 80),
                new StickerPosition("no.png", -35, 120),
                new StickerPosition("sad.png", 35, 120),
                new StickerPosition("sing.png", 0, -160),
        };

        for (StickerPosition pos : stickerPositions) {
            addFixedSticker(container, "cat-stickers/" + pos.filename, pos.x, pos.y);
        }
    }

    private void addVerticalLayoutPandaStickers(StackPane container) {
        StickerPosition[] stickerPositions = {
                new StickerPosition("hello.png", -40, -140),
                new StickerPosition("eat.png", 40, -100),
                new StickerPosition("happy.png", -35, -60),
                new StickerPosition("mocking.png", 30, -10),
                new StickerPosition("love.png", -35, 30),
                new StickerPosition("full.png", 35, 30),
                new StickerPosition("angry.png", 35, 80),
                new StickerPosition("peace.png", -35, 120),
                new StickerPosition("fever.png", 35, 120),
                new StickerPosition("birthday.png", 0, -160),
        };

        for (StickerPosition pos : stickerPositions) {
            addFixedSticker(container, "panda-stickers/" + pos.filename, pos.x, pos.y);
        }
    }


    private void addHorizontalLayoutAxolotlStickers(StackPane container) {
        StickerPosition[] stickerPositions = {
                new StickerPosition("hi.png", -150, -60),
                new StickerPosition("eat.png", -50, -60),
                new StickerPosition("happy.png", -150, 60),
                new StickerPosition("laugh.png", 50, -60),
                new StickerPosition("love.png", -50, 60),
                new StickerPosition("read.png", 50, 60),
                new StickerPosition("angry.png", 150, 60),
                new StickerPosition("ok.png", 150, -10),
                new StickerPosition("sad.png", 150, -60),
                new StickerPosition("sleep.png", -150, -10),
        };

        for (StickerPosition pos : stickerPositions) {
            addFixedSticker(container, "axolotl-stickers/" + pos.filename, pos.x, pos.y);
        }
    }

    private void addHorizontalLayoutCatStickers(StackPane container) {
        StickerPosition[] stickerPositions = {
                new StickerPosition("yes.png", -150, -60),
                new StickerPosition("eat.png", -50, -60),
                new StickerPosition("love.png", -150, 60),
                new StickerPosition("mocking.png", 50, -60),
                new StickerPosition("full.png", -50, 60),
                new StickerPosition("run.png", 50, 60),
                new StickerPosition("angry.png", 150, 60),
                new StickerPosition("no.png", 150, -10),
                new StickerPosition("sad.png", 150, -60),
                new StickerPosition("sing.png", -150, -10),
        };

        for (StickerPosition pos : stickerPositions) {
            addFixedSticker(container, "cat-stickers/" + pos.filename, pos.x, pos.y);
        }
    }

    private void addHorizontalLayoutPandaStickers(StackPane container) {
        StickerPosition[] stickerPositions = {
                new StickerPosition("hello.png", -150, -60),
                new StickerPosition("eat.png", -50, -60),
                new StickerPosition("happy.png", -150, 60),
                new StickerPosition("mocking.png", 50, -60),
                new StickerPosition("love.png", -50, 60),
                new StickerPosition("full.png", 50, 60),
                new StickerPosition("angry.png", 150, 60),
                new StickerPosition("peace.png", 150, -10),
                new StickerPosition("fever.png", 150, -60),
                new StickerPosition("birthday.png", -150, -10),
        };

        for (StickerPosition pos : stickerPositions) {
            addFixedSticker(container, "panda-stickers/" + pos.filename, pos.x, pos.y);
        }
    }

    private void reapplyCurrentStickers() {
        switch (currentStickerType) {
            case "axolotl":
                addRandomAxolotlStickers();
                break;
            case "cat":
                addRandomCatStickers();
                break;
            case "panda":  // ADD THIS CASE
                addRandomPandaStickers();
                break;
            case "none":
            default:
                // No stickers to re-apply
                break;
        }
    }

    private void addFixedSticker(StackPane container, String stickerPath, double x, double y) {
        try {
            String fullPath = "/com/example/nostalgiaapp/Stickers/" + stickerPath;

            Image stickerImage = new Image(getClass().getResourceAsStream(fullPath));
            ImageView stickerView = new ImageView(stickerImage);

            double stickerSize = 25;
            stickerView.setFitWidth(stickerSize);
            stickerView.setFitHeight(stickerSize);
            stickerView.setPreserveRatio(true);

            stickerView.setTranslateX(x);
            stickerView.setTranslateY(y);

            container.getChildren().add(stickerView);
            stickerViews.add(stickerView);

            System.out.println("Added sticker: " + stickerPath + " at position (" + x + ", " + y + ")");

        } catch (Exception e) {
            System.err.println("Error loading sticker " + stickerPath + ": " + e.getMessage());
        }
    }

    private static class StickerPosition {
        String filename;
        double x, y;

        StickerPosition(String filename, double x, double y) {
            this.filename = filename;
            this.x = x;
            this.y = y;
        }
    }



    private void clearStickers() {
        if (!stickerViews.isEmpty()) {
            // Clear stickers from both layouts to ensure complete cleanup
            StackPane verticalStackPane = null;
            StackPane horizontalStackPane = null;

            if (!photostripBackgroundVertical.getChildren().isEmpty()) {
                verticalStackPane = (StackPane) photostripBackgroundVertical.getChildren().get(0);
            }
            if (!photostripBackgroundHorizontal.getChildren().isEmpty()) {
                horizontalStackPane = (StackPane) photostripBackgroundHorizontal.getChildren().get(0);
            }

            // Remove stickers from both layouts
            if (verticalStackPane != null) {
                verticalStackPane.getChildren().removeAll(stickerViews);
            }
            if (horizontalStackPane != null) {
                horizontalStackPane.getChildren().removeAll(stickerViews);
            }

            stickerViews.clear();
            System.out.println("Cleared all stickers from both layouts");
        }
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
                    imageView.setFitWidth(250);
                    imageView.setFitHeight(85);
                } else {
                    imageView.setFitWidth(140);
                    imageView.setFitHeight(100);
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
            // Save current sticker type before switching
            String previousStickerType = currentStickerType;

            isVerticalLayout = true;
            updateLayoutButtons();
            loadPhotosIntoPreview();

            // Switch preview display
            VBox leftSide = (VBox) ((HBox) this.getCenter()).getChildren().get(0);
            leftSide.getChildren().clear();
            leftSide.getChildren().add(photostripBackgroundVertical);

            // If there were stickers, re-add them to the new layout
            currentStickerType = previousStickerType;
            reapplyCurrentStickers();
        }
    }

    private void switchToHorizontalLayout() {
        if (isVerticalLayout) {
            // Save current sticker type before switching
            String previousStickerType = currentStickerType;

            isVerticalLayout = false;
            updateLayoutButtons();
            loadPhotosIntoPreview();

            // Switch preview display
            VBox leftSide = (VBox) ((HBox) this.getCenter()).getChildren().get(0);
            leftSide.getChildren().clear();
            leftSide.getChildren().add(photostripBackgroundHorizontal);

            // If there were stickers, re-add them to the new layout
            currentStickerType = previousStickerType;
            reapplyCurrentStickers();
        }
    }

    private void updateLayoutButtons() {
        verticalLayoutBtn.setStyle("-fx-background-color: " + (isVerticalLayout ? "#4CAF50" : "white") +
                "; -fx-border-color: #333; -fx-border-width: 2; -fx-background-radius: 5; -fx-cursor: hand;");
        horizontalLayoutBtn.setStyle("-fx-background-color: " + (!isVerticalLayout ? "#4CAF50" : "white") +
                "; -fx-border-color: #333; -fx-border-width: 2; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    // FIXED: Updated layout method to properly position date in horizontal layout
    private void updatePhotostripLayout() {
        if (isVerticalLayout) {
            photostripPreview.getChildren().clear();

            // Add photo frames vertically
            for (ImageView photo : photoImages) {
                photostripPreview.getChildren().add(photo);
            }

            // Add date if enabled (at the bottom for vertical layout)
            if (enableDate) {
                Text dateLabel = new Text(java.time.LocalDate.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                dateLabel.setStyle("-fx-font-size: 12px; -fx-fill: #333;");
                photostripPreview.getChildren().add(dateLabel);
            }
        } else {
            // FIXED: Clear and rebuild horizontal layout properly
            horizontalPhotostripPreview.getChildren().clear();

            // Create a VBox to hold photos and date separately
            VBox horizontalContainer = new VBox(5);
            horizontalContainer.setAlignment(Pos.CENTER);

            // Create HBox for photos
            HBox photoContainer = new HBox(5);
            photoContainer.setAlignment(Pos.CENTER);

            // Add photo frames horizontally
            for (ImageView photo : photoImages) {
                photoContainer.getChildren().add(photo);
            }

            horizontalContainer.getChildren().add(photoContainer);

            // Add date if enabled (at the center bottom for horizontal layout)
            if (enableDate) {
                Text dateLabel = new Text(java.time.LocalDate.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                dateLabel.setStyle("-fx-font-size: 12px; -fx-fill: #333;");
                horizontalContainer.getChildren().add(dateLabel);
            }

            horizontalPhotostripPreview.getChildren().add(horizontalContainer);
        }
    }

    private void updateBackgroundColors() {
        String bgColor = colorToHex(selectedBackgroundColor);

        if (isVerticalLayout) {
            photostripBackgroundVertical.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 15; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 2, 2);");
        } else {
            photostripBackgroundHorizontal.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 15; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 2, 2);");
        }
    }

    private void updatePhotostripColors() {
        String stripColor = colorToHex(selectedPhotostripColor);

        if (isVerticalLayout) {
            photostripPreview.setStyle("-fx-background-color: " + stripColor + "; -fx-background-radius: 10;");
        } else {
            horizontalPhotostripPreview.setStyle("-fx-background-color: " + stripColor + "; -fx-background-radius: 10;");
        }
    }

    // FIXED: Enhanced filter application with proper CSS effects
    private void applyFilter() {
        // Apply selected filter to all photos
        for (ImageView photo : photoImages) {
            ColorAdjust colorAdjust = null;

            switch (selectedFilter) {
                case "B&W":
                    colorAdjust = new ColorAdjust();
                    colorAdjust.setSaturation(-1.0); // Remove all saturation for black and white
                    break;
                case "Bright":
                    colorAdjust = new ColorAdjust();
                    colorAdjust.setBrightness(0.4); // Increase brightness
                    break;
                case "Sepia":
                    colorAdjust = new ColorAdjust();
                    colorAdjust.setHue(0.1); // Slight brown hue
                    colorAdjust.setSaturation(0.2); // Reduce saturation slightly
                    colorAdjust.setBrightness(0.1); // Slight brightness increase
                    break;
                case "Warm":
                    colorAdjust = new ColorAdjust();
                    colorAdjust.setHue(0.1); // Warmer hue (towards red/orange)
                    colorAdjust.setSaturation(0.2); // Increase saturation slightly
                    colorAdjust.setBrightness(-0.05); // Slightly darker
                    break;
                case "Cold":
                    colorAdjust = new ColorAdjust();
                    colorAdjust.setHue(-0.2); // Cooler hue (towards blue)
                    colorAdjust.setSaturation(-0.1); // Reduce saturation slightly
                    colorAdjust.setBrightness(0.05); // Slightly brighter
                    break;
                case "no-filter":
                default:
                    colorAdjust = null; // No filter - remove effect
                    break;
            }

            // Apply the filter effect
            photo.setEffect(colorAdjust);

            // Clear any CSS styles that might interfere
            photo.setStyle("");
        }

        // Debug output to verify filter application
        System.out.println("Applied filter: " + selectedFilter);
    }

    private void updateDateDisplay() {
        updatePhotostripLayout(); // Refresh the layout to show/hide date
    }

    private void handleContinue() {
        // This method is no longer used as continue functionality is handled in createActionButtons
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