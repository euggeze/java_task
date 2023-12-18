package com.example.tasksapi.services;

import com.example.tasksapi.models.Image;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PDFConverter {

    private static List<String> filePaths;

    public PDFConverter() {
        filePaths = new ArrayList<>();
    }

    public List<Image> getImageObjects(){
        List<Image> imageList = new ArrayList<>();
        for (String filePath: filePaths){
            imageList.add(new Image(filePath));
        }
        return imageList;
    }
    public void convertPDFToImage(MultipartFile pdfFile, Long id) throws IOException {
            PDDocument document = Loader.loadPDF(pdfFile.getBytes());
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            FileWorker fileWorker = new FileWorker();
            fileWorker.createFolderDropbox(id.toString());

            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300);
                String imagePath = id.toString() + "/" + pdfFile.getOriginalFilename() + "page_" + (page + 1) + ".png";
                fileWorker.saveFile(bim, imagePath);
                filePaths.add(imagePath);
            }

            document.close();

    }
}