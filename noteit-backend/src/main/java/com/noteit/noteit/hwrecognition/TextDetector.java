package com.noteit.noteit.hwrecognition;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class TextDetector {

    private static String TEMP_PATH = "src/main/java/com/noteit/noteit/hwrecognition/temp";

    /**
     * Detects given handwritten input from image at filePath using Google Vision API
     *
     * @param filePath path to the image
     * @return path to the txt file that contains the processed input from the image
     * @throws IOException
     */
    public static String detectDocumentText(String filePath) throws IOException {
        String[] pathComps = filePath.split("/");
        String fullFilename = pathComps[pathComps.length - 1];
        String[] fileComps = fullFilename.split("\\.");
        String tempFilename = fileComps[0];
        String tempPath = TEMP_PATH + "/files/" + tempFilename + ".txt";

        List<AnnotateImageRequest> requests = new ArrayList<>();

        FileInputStream fileInputStream = new FileInputStream(filePath);
        ByteString imgBytes = ByteString.readFrom(fileInputStream);
        fileInputStream.close();

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();
            client.close();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.format("Error: %s%n", res.getError().getMessage());
                    return null;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                TextAnnotation annotation = res.getFullTextAnnotation();
                for (Page page : annotation.getPagesList()) {
                    String pageText = "";
                    for (Block block : page.getBlocksList()) {
                        String blockText = "";
                        for (Paragraph para : block.getParagraphsList()) {
                            String paraText = "";
                            for (Word word : para.getWordsList()) {
                                String wordText = "";
                                for (Symbol symbol : word.getSymbolsList()) {
                                    wordText = wordText + symbol.getText();
                                    System.out.format(
                                            "Symbol text: %s (confidence: %f)%n",
                                            symbol.getText(), symbol.getConfidence());
                                }
                                System.out.format(
                                        "Word text: %s (confidence: %f)%n%n", wordText, word.getConfidence());
                                paraText = String.format("%s %s", paraText, wordText);
                            }
                            // Output Example using Paragraph:
                            System.out.println("%nParagraph: %n" + paraText);
                            System.out.format("Paragraph Confidence: %f%n", para.getConfidence());
                            blockText = blockText + paraText;
                        }
                        pageText = pageText + blockText;
                    }
                }
                System.out.println("%nComplete annotation:");
                System.out.println(annotation.getText());

                deleteFromTemp("images/" + fullFilename); //delete processed image from temp folder
                File tempFile = new File(tempPath);
                if (tempFile.createNewFile()) {
                    FileWriter writer = new FileWriter(tempPath);
                    writer.write(annotation.getText());
                    writer.close();
                }

                return tempPath;
            }
        }
        return null;
    }

    /**
     * Deletes given file from temp folder
     *
     * @param filename name of the file that is deleted
     * @return true if the file was successfully deleted
     * false if the file could not be deleted
     * @throws IOException
     */
    public static boolean deleteFromTemp(String filename) throws IOException {
        String path = TEMP_PATH + "/" + filename;
        File file = new File(path);
        return Files.deleteIfExists(file.toPath());
    }
}