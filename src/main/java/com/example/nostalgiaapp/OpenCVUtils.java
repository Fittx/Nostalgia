    package com.example.nostalgiaapp;

    import javafx.scene.image.Image;
    import javafx.scene.image.PixelFormat;
    import javafx.scene.image.PixelWriter;
    import javafx.scene.image.WritableImage;
    import org.opencv.core.CvType;
    import org.opencv.core.Mat;
    import org.opencv.imgproc.Imgproc;

    import java.nio.ByteBuffer;

    public class OpenCVUtils {

        // FIXED: Optimized mat2Image conversion for better performance
        public static Image mat2Image(Mat mat) {
            try {
                if (mat == null || mat.empty()) {
                    return createErrorPlaceholder();
                }

                Mat processedMat = new Mat();

                // Handle different channel types and fix color conversion
                if (mat.channels() == 1) {
                    // Grayscale to RGB
                    Imgproc.cvtColor(mat, processedMat, Imgproc.COLOR_GRAY2RGB);
                } else if (mat.channels() == 3) {
                    // BGR to RGB (OpenCV uses BGR, JavaFX uses RGB)
                    Imgproc.cvtColor(mat, processedMat, Imgproc.COLOR_BGR2RGB);
                } else if (mat.channels() == 4) {
                    // BGRA to RGB
                    Imgproc.cvtColor(mat, processedMat, Imgproc.COLOR_BGRA2RGB);
                } else {
                    processedMat = mat.clone();
                }

                // FIXED: Flip horizontally to fix mirror effect
                Mat flippedMat = new Mat();
                org.opencv.core.Core.flip(processedMat, flippedMat, 1);

                int width = flippedMat.width();
                int height = flippedMat.height();
                int channels = flippedMat.channels();

                // Ensure we have 3 channels (RGB)
                if (channels != 3) {
                    Mat rgbMat = new Mat();
                    if (channels == 1) {
                        Imgproc.cvtColor(flippedMat, rgbMat, Imgproc.COLOR_GRAY2RGB);
                    } else if (channels == 4) {
                        Imgproc.cvtColor(flippedMat, rgbMat, Imgproc.COLOR_BGRA2RGB);
                    } else {
                        rgbMat = flippedMat.clone();
                    }
                    flippedMat.release();
                    flippedMat = rgbMat;
                    channels = 3;
                }

                // Get the image data
                byte[] buffer = new byte[width * height * channels];
                flippedMat.get(0, 0, buffer);

                // Create WritableImage
                WritableImage writableImage = new WritableImage(width, height);
                PixelWriter pixelWriter = writableImage.getPixelWriter();

                // FIXED: Use efficient bulk pixel setting for RGB data
                if (channels == 3) {
                    pixelWriter.setPixels(0, 0, width, height,
                            PixelFormat.getByteRgbInstance(), buffer, 0, width * 3);
                } else {
                    // Fallback for other formats
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            int index = (y * width + x) * channels;
                            int r = buffer[index] & 0xFF;
                            int g = channels > 1 ? buffer[index + 1] & 0xFF : r;
                            int b = channels > 2 ? buffer[index + 2] & 0xFF : r;

                            int rgb = (0xFF << 24) | (r << 16) | (g << 8) | b;
                            pixelWriter.setArgb(x, y, rgb);
                        }
                    }
                }

                // Clean up temporary matrices
                processedMat.release();
                flippedMat.release();

                return writableImage;

            } catch (Exception e) {
                System.err.println("Error converting Mat to Image: " + e.getMessage());
                return createErrorPlaceholder();
            }
        }

        // FIXED: Better error placeholder
        private static Image createErrorPlaceholder() {
            try {
                WritableImage errorImage = new WritableImage(400, 400);
                PixelWriter pw = errorImage.getPixelWriter();

                // Create a simple camera icon pattern
                int centerX = 200;
                int centerY = 200;
                int iconSize = 100;

                for (int y = 0; y < 400; y++) {
                    for (int x = 0; x < 400; x++) {
                        // Background
                        pw.setArgb(x, y, 0xFFFAF0DC);

                        // Simple camera icon outline
                        if (Math.abs(x - centerX) < iconSize && Math.abs(y - centerY) < iconSize/2) {
                            if (Math.abs(x - centerX) == iconSize-1 || Math.abs(y - centerY) == iconSize/2-1) {
                                pw.setArgb(x, y, 0xFF8B4513); // Brown outline
                            }
                        }

                        // Camera lens circle
                        double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                        if (distance < 30 && distance > 25) {
                            pw.setArgb(x, y, 0xFF8B4513); // Brown circle
                        }
                    }
                }
                return errorImage;
            } catch (Exception e) {
                // Ultimate fallback - solid color
                WritableImage solidImage = new WritableImage(400, 400);
                PixelWriter pw = solidImage.getPixelWriter();
                for (int y = 0; y < 400; y++) {
                    for (int x = 0; x < 400; x++) {
                        pw.setArgb(x, y, 0xFFFAF0DC);
                    }
                }
                return solidImage;
            }
        }

        // FIXED: Optimized square cropping
        public static Mat cropToSquare(Mat source) {
            if (source == null || source.empty()) {
                return new Mat();
            }

            int width = source.width();
            int height = source.height();
            int size = Math.min(width, height);

            int x = (width - size) / 2;
            int y = (height - size) / 2;

            org.opencv.core.Rect cropRect = new org.opencv.core.Rect(x, y, size, size);
            return new Mat(source, cropRect);
        }

        // FIXED: Safe memory management
        public static void releaseMatSafely(Mat mat) {
            if (mat != null && !mat.empty()) {
                try {
                    mat.release();
                } catch (Exception e) {
                    System.err.println("Error releasing Mat: " + e.getMessage());
                }
            }
        }

        // FIXED: Batch release for multiple Mats
        public static void releaseMatsSafely(Mat... mats) {
            for (Mat mat : mats) {
                releaseMatSafely(mat);
            }
        }

        // FIXED: Helper method to check if Mat is valid
        public static boolean isValidMat(Mat mat) {
            return mat != null && !mat.empty() && mat.rows() > 0 && mat.cols() > 0;
        }
    }