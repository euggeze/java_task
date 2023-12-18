package com.example.tasksapi.services;

import com.dropbox.core.v2.DbxClientV2;
import com.example.tasksapi.configuration.DropboxConfiguration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class FileWorker {

    private DropBoxService dropBoxService;
    private static String folderForImages = "/images/";

    FileWorker(){
        DbxClientV2 client = new DropboxConfiguration().dropboxClient();
        dropBoxService = new DropBoxService(client);
    }
    public void createFolderDropbox(String folderPath){
        try{
            dropBoxService.createFolder(folderForImages+folderPath);
        }
        catch (Exception ex){
            String st = ex.getMessage();
        }
    }

    public void saveFile(BufferedImage file, String path){
        try{
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(file, "png", outputStream);
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            dropBoxService.uploadFile(folderForImages + path, inputStream);
        }
        catch (Exception ex){
            String st = ex.getMessage();
        }
    }
}
