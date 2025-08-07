// 代码生成时间: 2025-08-07 14:05:40
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;

public class BatchFileRenamer extends AbstractVerticle {

    private static final String FILE_RENAMER_CONFIG = "fileRenamerConfig.json";

    @Override
    public void start(Future<Void> startFuture) {
        vertx.executeBlocking(promise -> {
            try {
                // Load the configuration for file renaming
                JsonObject config = readConfig(FILE_RENAMER_CONFIG);
                if (config == null) {
                    throw new IllegalArgumentException("Configuration file not found or invalid");
                }

                // Get the list of files to rename and their new names
                JsonArray filesToRename = config.getJsonArray("files");
                if (filesToRename == null || filesToRename.isEmpty()) {
                    throw new IllegalArgumentException("No files to rename in the configuration");
                }

                // Perform the renaming operation
                boolean allRenamedSuccessfully = renameFiles(filesToRename);
                if (allRenamedSuccessfully) {
                    promise.complete();
                } else {
                    promise.fail(new Exception("Failed to rename one or more files"));
                }
            } catch (Exception e) {
                promise.fail(e);
            }
        }, res -> {
            if (res.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(res.cause());
            }
        });
    }

    private JsonObject readConfig(String configFilePath) {
        // Read the configuration file
        String configContent = vertx.fileSystem().readFileBlocking(configFilePath);
        try {
            return new JsonObject(configContent);
        } catch (Exception e) {
            vertx.logger().error("Failed to read configuration file", e);
            return null;
        }
    }

    private boolean renameFiles(JsonArray filesToRename) throws IOException {
        boolean allRenamed = true;
        for (int i = 0; i < filesToRename.size(); i++) {
            JsonObject fileEntry = filesToRename.getJsonObject(i);
            String oldPath = fileEntry.getString("oldPath");
            String newPath = fileEntry.getString("newPath");
            File oldFile = new File(oldPath);
            File newFile = new File(newPath);

            if (!oldFile.exists() || newFile.exists()) {
                allRenamed = false;
                continue;
            }

            // Use Java NIO to rename the file
            Files.move(Paths.get(oldPath), Paths.get(newPath), StandardCopyOption.REPLACE_EXISTING);
        }
        return allRenamed;
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(BatchFileRenamer.class.getName(), res -> {
            if (res.succeeded()) {
                System.out.println("Batch file renamer started successfully");
            } else {
                System.err.println("Failed to start batch file renamer");
                res.cause().printStackTrace();
            }
        });
    }
}
