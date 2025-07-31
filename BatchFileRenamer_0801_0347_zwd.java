// 代码生成时间: 2025-08-01 03:47:23
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.FileSystemException;
import io.vertx.core.file.OpenOptions;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// BatchFileRenamer.java represents a Vert.x Verticle that can rename files in a directory.
public class BatchFileRenamer extends AbstractVerticle {

    private static final String DIRECTORY_CONFIG_KEY = "directory";
    private static final String PREFIX_CONFIG_KEY = "prefix";
    private static final String SUFFIX_CONFIG_KEY = "suffix";
    private static final String NUMBER_FORMAT_CONFIG_KEY = "numberFormat";

    @Override
    public void start(Future<Void> startFuture) {
        super.start(startFuture);
        JsonObject config = config();

        String directory = config.getString(DIRECTORY_CONFIG_KEY);
        String prefix = config.getString(PREFIX_CONFIG_KEY, "");
        String suffix = config.getString(SUFFIX_CONFIG_KEY, "");
        String numberFormat = config.getString(NUMBER_FORMAT_CONFIG_KEY, "%03d");

        FileSystem fs = vertx.fileSystem();

        fs.readDir(directory, true, ares -> {
            if (ares.succeeded()) {
                List<String> fileNames = ares.result().stream()
                    .map(Path::of)
                    .filter(path -> path.toFile().isFile())
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());

                renameFiles(directory, fileNames, prefix, suffix, numberFormat).setHandler(
                    renameRes -> {
                        if (renameRes.succeeded()) {
                            startFuture.complete();
                        } else {
                            startFuture.fail(renameRes.cause());
                        }
                    });
            } else {
                startFuture.fail(ares.cause());
            }
        });
    }

    // This method renames files in a directory based on the given prefix, suffix, and number format.
    private Future<Void> renameFiles(String directory, List<String> fileNames, String prefix, String suffix, String numberFormat) {
        Future<Void> future = Future.future();
        int count = 1;

        fileNames.forEach(fileName -> {
            try {
                Path oldPath = Paths.get(directory, fileName);
                String newFileName = String.format(numberFormat, count++) + (prefix.isEmpty() ? "" : "_" + prefix) + (suffix.isEmpty() ? "" : "." + suffix);
                Path newPath = Paths.get(directory, newFileName);

                Files.move(oldPath, newPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            } catch (Exception e) {
                future.fail(e);
            }
        });

        // If all files were renamed successfully, complete the future.
        future.complete();
        return future;
    }
}
