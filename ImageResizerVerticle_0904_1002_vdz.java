// 代码生成时间: 2025-09-04 10:02:39
package com.example.imageresizer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageResizerVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ImageResizerVerticle.class);

    @Override
    public void start(Future<Void> startFuture) {
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer("resizeImages", this::handleResizeRequest, message -> {
            startFuture.complete();
        });
    }

    private void handleResizeRequest(Message<JsonObject> message) {
        JsonObject request = message.body();
        String[] imagePaths = request.getJsonArray("imagePaths").stream().map(Object::toString).toArray(String[]::new);
        int targetWidth = request.getInteger("targetWidth");
        int targetHeight = request.getInteger("targetHeight");

        for (String imagePath : imagePaths) {
            resizeImage(imagePath, targetWidth, targetHeight);
        }
    }

    private void resizeImage(String imagePath, int targetWidth, int targetHeight) {
        try {
            File imageFile = new File(imagePath);
            BufferedImage originalImage = ImageIO.read(imageFile);

            // Calculate aspect ratio to maintain the image proportions
            double aspectRatio = (double) originalImage.getWidth() / originalImage.getHeight();
            int resizedWidth = targetWidth;
            int resizedHeight = targetHeight;
            if (targetWidth < 0 && targetHeight < 0) {
                resizedWidth = (int) (originalImage.getWidth() * Math.abs(targetWidth));
                resizedHeight = (int) (originalImage.getHeight() * Math.abs(targetHeight));
            } else if (targetWidth < 0) {
                resizedWidth = (int) (originalImage.getWidth() * Math.abs(targetWidth));
                resizedHeight = (int) (resizedWidth / aspectRatio);
            } else if (targetHeight < 0) {
                resizedHeight = (int) (originalImage.getHeight() * Math.abs(targetHeight));
                resizedWidth = (int) (resizedHeight * aspectRatio);
            }

            BufferedImage resizedImage = new BufferedImage(resizedWidth, resizedHeight, originalImage.getType());
            resizedImage.getGraphics().drawImage(originalImage.getScaledInstance(resizedWidth, resizedHeight, BufferedImage.SCALE_SMOOTH), 0, 0, null);

            String resizedImagePath = imagePath.replace("." + imageFile.getName().split("\.")[1], "_resized." + imageFile.getName().split("\.")[1]);
            ImageIO.write(resizedImage, imageFile.getName().split("\.")[1], new File(resizedImagePath));

            logger.info("Resized image saved to: " + resizedImagePath);
        } catch (IOException e) {
            logger.error("Failed to resize image: " + imagePath, e);
        }
    }
}
