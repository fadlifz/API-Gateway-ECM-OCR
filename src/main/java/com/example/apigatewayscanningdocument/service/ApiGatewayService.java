package com.example.apigatewayscanningdocument.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.apigatewayscanningdocument.request.AllRequest;
import com.example.apigatewayscanningdocument.response.EcmResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import org.springframework.http.HttpEntity;
import java.awt.image.BufferedImage;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class ApiGatewayService implements Serializable {

    public static final DateTimeFormatter yyMMddHHmmssSSFormat = DateTimeFormatter.ofPattern("yyMMddHHmmssSS");

    public String callApiOcr(AllRequest fileRequest) {
        try {
            String apiUrl = "http://localhost:8081/extract-text";
            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<AllRequest> fileRequestHttpEntity = new HttpEntity<>(fileRequest);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, fileRequestHttpEntity,
                    String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("extractedText").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public EcmResponse callApiEcm(AllRequest allRequest) {

        try {
            String[] strings = allRequest.getFile().split(",");
            String extension;
            switch (strings[0]) {// check image's extension
                case "data:image/jpeg;base64":
                    extension = "jpeg";
                    break;
                case "data:image/png;base64":
                    extension = "png";
                    break;
                default:// should write cases for more images types
                    extension = "jpg";
                    break;
            }
            // convert base64 string to binary data
            BufferedImage image = null;
            byte[] data = DatatypeConverter.parseBase64Binary(strings[1]);
            ByteArrayInputStream bis = new ByteArrayInputStream(data);

            image = ImageIO.read(bis);
            bis.close();

            // write the image to a pdf file
            File outputImageFile = new File(
                    "image_" + yyMMddHHmmssSSFormat.format(LocalDateTime.now()) + "." + extension);
            ImageIO.write(image, extension, outputImageFile);
            String outputPdfFile = "output_" + yyMMddHHmmssSSFormat.format(LocalDateTime.now()) + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(new File(outputPdfFile)));
            document.open();
            document.newPage();
            Image readImage = Image.getInstance(new File(outputImageFile.getName()).getAbsolutePath());
            readImage.setAbsolutePosition(0, 0);
            readImage.setBorderWidth(0);
            readImage.scaleAbsolute(PageSize.A4);
            document.add(readImage);
            document.close();
            outputImageFile.delete();

            String apiUrl = "http://localhost:8082/saveToEcm";
            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<AllRequest> fileRequestHttpEntity = new HttpEntity<>(allRequest);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, fileRequestHttpEntity,
                    String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return EcmResponse.builder()
                    .txtFile(jsonNode.get("result").get("txtFile").asText())
                    .pdfFile("Successfully generated pdf file " + outputPdfFile)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
