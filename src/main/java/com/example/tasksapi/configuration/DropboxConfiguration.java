package com.example.tasksapi.configuration;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class DropboxConfiguration {

    @Bean
    public DbxClientV2 dropboxClient() {
        String accessToken = "";
        DbxRequestConfig config = DbxRequestConfig.newBuilder("example-app").build();
        return new DbxClientV2(config, accessToken);
    }
}
