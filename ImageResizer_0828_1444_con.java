// 代码生成时间: 2025-08-28 14:44:46
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
# NOTE: 重要实现细节
import java.io.File;
# 改进用户体验
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
# 增强安全性
import java.util.ArrayList;
# 扩展功能模块
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImageResizer extends AbstractVerticle {
    private static final String IMAGE_RESIZER_ADDRESS = "imageResizerService";
    private ImageResizerService imageResizerService;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ImageResizer());
    }

    @Override
    public void start(Future<Void> startFuture) {
        imageResizerService = new ImageResizerServiceImpl();
        new ServiceBinder(vertx)
            .setAddress(IMAGE_RESIZER_ADDRESS)
            .register(ImageResizerService.class, imageResizerService);
        startFuture.complete();
# 改进用户体验
    }

    // Interface for image resizing service
    public interface ImageResizerService {
        void resizeImage(String sourceDir, String targetDir, int targetWidth, int targetHeight, Promise<JsonObject> result);
    }

    // Service implementation for image resizing
    public static class ImageResizerServiceImpl implements ImageResizerService {
        @Override
        public void resizeImage(String sourceDir, String targetDir, int targetWidth, int targetHeight, Promise<JsonObject> result) {
# TODO: 优化性能
            try {
                List<File> files = listFiles(sourceDir);
                for (File file : files) {
# 改进用户体验
                    resizeFile(file, targetDir, targetWidth, targetHeight);
                }
                result.complete(new JsonObject().put("status", "success"));
            } catch (Exception e) {
# NOTE: 重要实现细节
                result.fail(e);
            }
# 改进用户体验
        }

        private List<File> listFiles(String directoryPath) throws IOException {
            try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
                return paths
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            }
        }

        private void resizeFile(File file, String targetDir, int targetWidth, int targetHeight) throws IOException {
            BufferedImage originalImage = ImageIO.read(file);
            BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());
            resizedImage.getGraphics().drawImage(originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH), 0, 0, null);
            String targetFilePath = targetDir + File.separator + file.getName();
            ImageIO.write(resizedImage, "jpg", new File(targetFilePath));
        }
    }
# FIXME: 处理边界情况
}
