package io.github.gpizzimenti.bookbynav.utils;

/**
 *
 * @author Giuseppe Pizzimenti
 */

import java.io.File;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HttpUtils {
    
    public static boolean saveFile(URL url, File file, String UA) {

    boolean isSucceed = true;

    CloseableHttpClient httpClient = HttpClients
                                        .custom()
                                        .setUserAgent(UA)
                                        .build();

    HttpGet httpGet = new HttpGet(url.toString());

    try {
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();

        if (entity != null) {
            FileUtils.copyInputStreamToFile(entity.getContent(), file);
        }

    } catch (Exception e) {
        isSucceed = false;
    }

    httpGet.releaseConnection();

    return isSucceed;
 }
    
public static Document getDocument(URL url, String UA, String charset, String baseUri) {
    
    Document doc= null;
    
    CloseableHttpClient httpClient = HttpClients
                                    .custom()
                                    .setUserAgent(UA)
                                    .build();
    
    HttpGet httpGet = new HttpGet(url.toString());
    
    try {
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();

        if (entity != null) {
            doc= Jsoup.parse(entity.getContent(), charset, baseUri);
        }

    } catch (Exception e) {
        doc = null;
    }

    httpGet.releaseConnection();    
    
    return doc;
}   
    
}
