// 代码生成时间: 2025-08-22 04:05:59
import io.vertx.core.Vertx;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.file.FileSystem;
import io.vertx.core.streams.ReadStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public class FileUnzipper extends AbstractVerticle {

    // 解压文件方法
    public void unzip(String zipFilePath, String targetDirectory) {
        FileSystem fileSystem = vertx.fileSystem();
        fileSystem.readFile(zipFilePath, res -> {
            if (res.succeeded()) {
                Buffer buffer = res.result();
                unzip(buffer, targetDirectory);
            } else {
                // 错误处理
                res.cause().printStackTrace();
                System.err.println("Failed to read zip file: " + zipFilePath);
            }
        });
    }

    // 解压缩Buffer中的zip文件到指定目录
    private void unzip(Buffer buffer, String targetDirectory) {
        ZipInputStream zis = new ZipInputStream(buffer.getBytes());
        ZipEntry zipEntry = zis.getNextEntry();
        try {
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                if (!zipEntry.isDirectory()) {
                    // 如果是文件，则解压
                    String filePath = targetDirectory + File.separator + fileName;
                    extractFile(zis, filePath);
                } else {
                    // 如果是文件夹，则创建文件夹
                    String folderPath = targetDirectory + File.separator + fileName;
                    new File(folderPath).mkdirs();
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to unzip file: " + targetDirectory);
        }
    }

    // 从ZIP输入流中提取文件
    private void extractFile(ZipInputStream zis, String filePath) throws IOException {
        int len;
        byte[] buffer = new byte[1024];
        // 创建文件
        Files.createDirectories(Paths.get(filePath).getParent());
        try (OutputStream fos = Files.newOutputStream(Paths.get(filePath), StandardOpenOption.CREATE);) {
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }
    }

    // Vert.x启动方法
    @Override
    public void start(Future<Void> startFuture) {
        try {
            String zipFilePath = "path/to/your/zipfile.zip";
            String targetDirectory = "path/to/your/target/directory";
            unzip(zipFilePath, targetDirectory);
            startFuture.complete();
        } catch (Exception e) {
            e.printStackTrace();
            startFuture.fail(e);
        }
    }

    // 主方法，用于运行Vert.x应用程序
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new FileUnzipper(), res -> {
            if (res.succeeded()) {
                System.out.println("FileUnzipper deployed successfully");
            } else {
                System.err.println("Failed to deploy FileUnzipper: " + res.cause().getMessage());
            }
        });
    }
}
