package com.example.apigatewayscanningdocument.service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.web.multipart.MultipartFile;

public class FileManagementService {

    public File mergePdfFiles(List<MultipartFile> files) throws IOException {
        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        String outputFilePath = "mergedFile.pdf";
        pdfMerger.setDestinationFileName(outputFilePath);

        for (MultipartFile file : files) {
            pdfMerger.addSource(file.getInputStream());
        }

        pdfMerger.mergeDocuments(null);
        return new File(outputFilePath);
    }

    public File mergeImageFiles(List<MultipartFile> files) throws IOException {
        List<BufferedImage> images = new ArrayList<>();
        int totalHeight = 0;
        int maxWidth = 0;

        for (MultipartFile file : files) {
            BufferedImage img = ImageIO.read(file.getInputStream());
            images.add(img);
            totalHeight += img.getHeight();
            if (img.getWidth() > maxWidth) {
                maxWidth = img.getWidth();
            }
        }

        BufferedImage mergedImage = new BufferedImage(maxWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = mergedImage.createGraphics();

        int currentHeight = 0;
        for (BufferedImage img : images) {
            g2d.drawImage(img, 0, currentHeight, null);
            currentHeight += img.getHeight();
        }
        g2d.dispose();

        String outputFilePath = "mergedFile.png"; // Default to PNG
        File mergedFile = new File(outputFilePath);
        ImageIO.write(mergedImage, "png", mergedFile);

        return mergedFile;
    }

    public File mergeDocFiles(List<MultipartFile> files) throws IOException {
        String outputFilePath = "mergedFile.docx";
        if (getFileExtension(files.get(0)).equalsIgnoreCase("docx")) {
            XWPFDocument mergedDoc = new XWPFDocument();
            for (MultipartFile file : files) {
                try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
                    for (XWPFParagraph p : doc.getParagraphs()) {
                        XWPFParagraph newPara = mergedDoc.createParagraph();
                        copyParagraph(p, newPara);
                    }
                }
            }
            try (FileOutputStream out = new FileOutputStream(outputFilePath)) {
                mergedDoc.write(out);
            }
        } else {
            HWPFDocument mergedDoc = null;
            for (MultipartFile file : files) {
                try (HWPFDocument doc = new HWPFDocument(file.getInputStream())) {
                    if (mergedDoc == null) {
                        mergedDoc = doc;
                    } else {
                        Range range = mergedDoc.getRange();
                        range.insertAfter(doc.getDocumentText());
                    }
                }
            }
            if (mergedDoc != null) {
                try (FileOutputStream out = new FileOutputStream(outputFilePath)) {
                    mergedDoc.write(out);
                }
            }
        }
        return new File(outputFilePath);
    }

    public void copyParagraph(XWPFParagraph source, XWPFParagraph target) {
        target.setAlignment(source.getAlignment());
        target.setStyle(source.getStyle());
        for (XWPFRun run : source.getRuns()) {
            XWPFRun newRun = target.createRun();
            newRun.setText(run.getText(0));
            newRun.setBold(run.isBold());
            newRun.setItalic(run.isItalic());
            newRun.setUnderline(run.getUnderline());
            newRun.setColor(run.getColor());
            newRun.setFontFamily(run.getFontFamily());
            newRun.setFontSize(12);
        }
    }
//buat fungsi untuk menghitung total file yang diupload, returnnya adalah string

    public String countTotalfile(List<MultipartFile> files){
        
        for(MultipartFile file : files){
            //hitung total file yang diupload, untuk 
            // mengecek gunakan fungsi getFileExtension di bawah
            // jika file pertama adalah pdf dan kedua pdf maka
        }

        return "";
    }

    public String getFileExtension(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name != null && name.lastIndexOf(".") != -1) {
            return name.substring(name.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    public File mergeFiles(List<MultipartFile> files) throws IOException {
        
        if (files.isEmpty()) {
            throw new IllegalArgumentException("At least one file is required.");
        }

        List<BufferedImage> images = new ArrayList<>();
        int totalHeight = 0;
        int maxWidth = 0;
        String outputFormat = null;

        // Read all images and calculate total height and maximum width
        for (MultipartFile file : files) {
            BufferedImage img = ImageIO.read(file.getInputStream());
            images.add(img);
            totalHeight += img.getHeight();
            if (img.getWidth() > maxWidth) {
                maxWidth = img.getWidth();
            }
            // Detect file format based on extension
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                outputFormat = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            }
        }

        // Create merged image with maximum width and total height
        BufferedImage mergedImage = new BufferedImage(maxWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = mergedImage.createGraphics();

        int currentHeight = 0;
        for (BufferedImage img : images) {
            g2d.drawImage(img, 0, currentHeight, null);
            currentHeight += img.getHeight();
        }
        g2d.dispose();

        // Generate file path with detected output format or default to PNG
        String outputFilePath = "mergedFile";
        if (outputFormat != null && !outputFormat.isEmpty()) {
            outputFilePath += "." + outputFormat;
        } else {
            outputFilePath += ".png"; // Default to PNG if format cannot be detected
        }

        // Write merged image to file with detected or default output format
        File mergedFile = new File(outputFilePath);
        ImageIO.write(mergedImage, outputFormat != null ? outputFormat : "png", mergedFile);

        return mergedFile;
    }

    public File manageSingleFile(List<MultipartFile> uploadedFile) throws IOException {
        File convFile = null;
        
        for(MultipartFile file : uploadedFile){
            convFile = new File(file.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());   
            fos.close();
        }

        return convFile;

    }

}
