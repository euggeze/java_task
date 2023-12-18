package com.example.tasksapi.services;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CreateFolderResult;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import java.io.InputStream;

public class DropBoxService {

    private final DbxClientV2 client;

    public DropBoxService(DbxClientV2 client) {
        this.client = client;
    }

    public InputStream downloadFile(String filePath) throws Exception{
        return client.files().download(filePath).getInputStream();
    }

    public FileMetadata uploadFile(String filePath, InputStream fileStream) throws Exception{
        return client.files().uploadBuilder(filePath).uploadAndFinish(fileStream);
    }

    public CreateFolderResult createFolder(String folderPath) throws Exception{
        return client.files().createFolderV2(folderPath);
    }

    public void deleteFile(String filePath) throws Exception{
        client.files().deleteV2(filePath);
    }

}