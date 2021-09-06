package com.friskysoft.framework.utils;

import com.friskysoft.framework.Browser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GifRecorder {

    private static final Logger LOGGER = LoggerFactory.getLogger(GifRecorder.class);
    //private static final String tmpDir = System.getProperty("java.io.tmpdir") + "/GifRecorder";
    private static final String tmpDir = "build/tmp/GifRecorder";

    private static final ThreadLocal<GifWorker> worker = new ThreadLocal<>();
    private static int interval = 50;
    private static long timeout = 10 * 60 * 1000; // 10 min default

    /**
     * @param intervalMillis Interval between screenshots
     */
    public static void setInterval(int intervalMillis) {
        GifRecorder.interval = intervalMillis;
    }

    /**
     * @param timeoutMillis Maximum time a recorder should run before force stopping
     */
    public static void setTimeout(long timeoutMillis) {
        GifRecorder.timeout = timeoutMillis;
    }

    private static GifWorker localWorker() {
        GifWorker localWorker = worker.get();
        if (localWorker == null) {
            localWorker = new GifWorker(Browser.driver(), interval);
            worker.set(localWorker);
        }
        return localWorker;
    }

    public static void start() {
        reset();
        localWorker().startRecorder();
    }

    public static void reset() {
        if (worker.get() != null) {
            try {
                worker.get().stopRecorder();
                worker.get().clearTmpFiles();
            } catch (Throwable ex) {
                LOGGER.warn("Error in GifRecorder reset: " + ex.getMessage());
            }
            worker.set(null);
        }
    }

    public static File stop(boolean save) {
        String defaultSaveLocation = Browser.getDefaultVideoDir() + "/" + Browser.getDefaultVideoFileName() + ".gif";
        return stop(save, defaultSaveLocation);
    }

    public static File stop(boolean save, String filepath) {
        File gif;
        try {
            localWorker().stopRecorder();
            if (save) {
                gif = localWorker().saveGif(filepath);
            } else {
                gif = null;
            }
        } catch (Exception ex) {
            LOGGER.warn("Could not save gif recording. Error: " + ex.getMessage());
            gif = null;
        }
        localWorker().clearTmpFiles();
        return gif;
    }

    public static File stopAndSave(String filename) {
        if (!filename.trim().endsWith(".gif")) {
            filename = filename.trim() + ".gif";
        }
        return stop(true, Browser.getDefaultVideoDir() + "/" + filename);
    }

    public static void stopAndDiscard() {
        stop(false);
    }

    public static class GifWorker {

        private final List<String> frames;
        private final WebDriver driver;
        private final int interval;
        private final AtomicInteger counter;
        private Thread recorder;
        private String tmpFolder;
        byte[] lastFrame;
        private long startTime;

        public GifWorker(WebDriver driver, int interval) {
            this.driver = driver;
            this.interval = interval;
            this.frames = new LinkedList<>();
            this.tmpFolder = GifRecorder.tmpDir + "/" + System.currentTimeMillis() + RandomStringUtils.randomAlphanumeric(5);
            this.counter = new AtomicInteger();
        }

        public void setTmpFolder(String tmpFolder) {
            this.tmpFolder = tmpFolder;
        }

        public void startRecorder() {
            if (recorder != null) {
                recorder.stop();
            }
            frames.clear();
            recorder = new Thread(() -> {
                while (System.currentTimeMillis() - startTime <= timeout) {
                    captureFrame(new File(tmpFolder + "/frame_" + counter.getAndIncrement() + ".png"));
                    Browser.sleep(this.interval);
                }
            });
            recorder.start();
            startTime = System.currentTimeMillis();
        }

        public void stopRecorder() {
            if (recorder != null) {
                recorder.stop();
            }
        }

        private void captureFrame(File tmpFile) {
            try {
                if (driver instanceof RemoteWebDriver && ((RemoteWebDriver) driver).getSessionId() != null) {
                    byte[] frame = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    boolean changeDetected;
                    if (!frames.isEmpty()) {
                        changeDetected = false;
                        for (int x = 0; x < frame.length && x < lastFrame.length; x = x + 2) {
                            if (Math.abs(frame[x] - lastFrame[x]) > 1) {
                                changeDetected = true;
                                break;
                            }
                        }
                    } else {
                        changeDetected = true;
                    }
                    if (changeDetected) {
                        lastFrame = frame;
                        FileUtils.writeByteArrayToFile(tmpFile, frame);
                        frames.add(tmpFile.getAbsolutePath());
                    } else {
                        LOGGER.trace("Skipping GIF frame - No change detected");
                    }
                }
            } catch (Throwable e) {
                String error = "GIF captureFrame error";
                if (e.getMessage() != null) {
                    error = error + ". " + e.getMessage().split("\n")[0];
                }
                LOGGER.debug(error);
            }
        }

        public File saveGif(String filepath) {
            if (frames.isEmpty()) {
                return null;
            }

            try {
                BufferedImage firstImage = ImageIO.read(new File(frames.get(0)));

                if (!filepath.trim().endsWith(".gif")) {
                    filepath = filepath.trim() + ".gif";
                }
                File file = new File(filepath);

                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }

                ImageOutputStream output = new FileImageOutputStream(file);

                GifCreator gif = new GifCreator(output, firstImage.getType(), 500, true);

                for (int i = 1; i < frames.size(); i++) {
                    BufferedImage nextImage = ImageIO.read(new File(frames.get(i)));
                    gif.writeToSequence(nextImage);
                }

                gif.close();
                output.close();

                LOGGER.info("GIF file created at: " + file.getAbsolutePath());

                for (String frameFile : frames) {
                    FileUtils.forceDelete(new File(frameFile));
                }
                frames.clear();

                return file;

            } catch (Throwable ex) {
                LOGGER.warn("GIF could not be saved. Error: " + ex.getMessage());
            }
            clearTmpFiles();
            return null;
        }

        public void clearTmpFiles() {
            try {
                FileUtils.deleteDirectory(new File(tmpFolder));
            } catch (Throwable ex) {
                LOGGER.debug("Error while deleting temporary image files: " + ex.getMessage());
            }
        }
    }

    public static class GifCreator {

        private final ImageWriter writer;
        private final ImageWriteParam params;
        private final IIOMetadata metadata;

        public GifCreator(ImageOutputStream out, int imageType, int delay, boolean loop) throws IOException {
            writer = ImageIO.getImageWritersBySuffix("gif").next();
            params = writer.getDefaultWriteParam();

            ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType);
            metadata = writer.getDefaultImageMetadata(imageTypeSpecifier, params);

            configureRootMetadata(delay, loop);

            writer.setOutput(out);
            writer.prepareWriteSequence(null);
        }

        private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
            int nNodes = rootNode.getLength();
            for (int i = 0; i < nNodes; i++) {
                if (rootNode.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
                    return (IIOMetadataNode) rootNode.item(i);
                }
            }
            IIOMetadataNode node = new IIOMetadataNode(nodeName);
            rootNode.appendChild(node);
            return (node);
        }

        private void configureRootMetadata(int delay, boolean loop) throws IIOInvalidTreeException {
            String metaFormatName = metadata.getNativeMetadataFormatName();
            IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);

            IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
            graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
            graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
            graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
            graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(delay / 10));
            graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");

            IIOMetadataNode appExtensionsNode = getNode(root, "ApplicationExtensions");
            IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");
            child.setAttribute("applicationID", "NETSCAPE");
            child.setAttribute("authenticationCode", "2.0");

            int loopContinuously = loop ? 0 : 1;
            child.setUserObject(new byte[] {0x1, (byte) (loopContinuously & 0xFF), (byte) ((loopContinuously >> 8) & 0xFF)});
            appExtensionsNode.appendChild(child);
            metadata.setFromTree(metaFormatName, root);
        }

        public void writeToSequence(RenderedImage img) throws IOException {
            writer.writeToSequence(new IIOImage(img, null, metadata), params);
        }

        public void close() throws IOException {
            writer.endWriteSequence();
        }
    }

}
