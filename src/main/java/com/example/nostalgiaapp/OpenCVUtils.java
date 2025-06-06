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

    public static Image mat2Image(Mat mat) {
        try {
            // Handle different channel types
            Mat processedMat = new Mat();

            if (mat.channels() == 1) {
                // Grayscale to BGR
                Imgproc.cvtColor(mat, processedMat, Imgproc.COLOR_GRAY2BGR);
            } else if (mat.channels() == 3) {
                // BGR to RGB (OpenCV uses BGR, JavaFX uses RGB)
                Imgproc.cvtColor(mat, processedMat, Imgproc.COLOR_BGR2RGB);
            } else if (mat.channels() == 4) {
                // BGRA to RGB
                Imgproc.cvtColor(mat, processedMat, Imgproc.COLOR_BGRA2RGB);
            } else {
                processedMat = mat.clone();
            }

            int width = processedMat.width();
            int height = processedMat.height();
            int channels = processedMat.channels();

            // Ensure we have 3 channels (RGB)
            if (channels != 3) {
                Mat rgbMat = new Mat();
                if (channels == 1) {
                    Imgproc.cvtColor(processedMat, rgbMat, Imgproc.COLOR_GRAY2RGB);
                } else if (channels == 4) {
                    Imgproc.cvtColor(processedMat, rgbMat, Imgproc.COLOR_BGRA2RGB);
                } else {
                    rgbMat = processedMat.clone();
                }
                processedMat = rgbMat;
                channels = 3;
            }

            // Get the image data
            byte[] buffer = new byte[width * height * channels];
            processedMat.get(0, 0, buffer);

            // Create WritableImage
            WritableImage writableImage = new WritableImage(width, height);
            PixelWriter pixelWriter = writableImage.getPixelWriter();

            // Convert byte array to RGB format for JavaFX
            if (channels == 3) {
                pixelWriter.setPixels(0, 0, width, height,
                        PixelFormat.getByteRgbInstance(), buffer, 0, width * 3);
            } else {
                // Fallback: convert pixel by pixel
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

            return writableImage;

        } catch (Exception e) {
            System.err.println("Error converting Mat to Image: " + e.getMessage());
            e.printStackTrace();

            // Return a placeholder image on error
            WritableImage errorImage = new WritableImage(640, 480);
            PixelWriter pw = errorImage.getPixelWriter();
            for (int y = 0; y < 480; y++) {
                for (int x = 0; x < 640; x++) {
                    pw.setArgb(x, y, 0xFF808080); // Gray color
                }
            }
            return errorImage;
        }
    }
}