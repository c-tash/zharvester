package ru.umeta.zharvester;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.IOException;

public class HttpClientSend {

    public static int dataSend(String location){
        CloseableHttpClient client;
        File file = new File(location);

        HttpPost post = new HttpPost("http://localhost:8080/");

        client = HttpClientBuilder.create().build();

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("file", new FileBody(file));

        HttpEntity entity = builder.build();
        post.setEntity(entity);
        try {
            HttpResponse response = client.execute(post);
            client.close();
        }
        catch (IOException e){
            System.out.println("Client error!!!!");
        }
        return 1;
    }
}
