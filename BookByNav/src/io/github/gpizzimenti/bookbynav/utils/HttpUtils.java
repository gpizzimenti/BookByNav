package io.github.gpizzimenti.bookbynav.utils;

/**
 *
 * @author Giuseppe Pizzimenti
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    
    
  /**
     * @param url*
     * @param file*
     * @param UA*
     * @return 
     * @throws java.lang.Exception***********************************************************************/
    
  public static boolean saveFile(URL url, File file, String UA) throws Exception{

    boolean isSucceed = true;

    try (CloseableHttpClient httpClient = HttpClients
                                                .custom()
                                                .setUserAgent(UA)
                                                .build()) {
    
        HttpGet httpGet = new HttpGet(url.toString());


        try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
             InputStream is = (httpResponse.getEntity()!=null ? httpResponse.getEntity().getContent() : null)) {

             FileUtils.copyInputStreamToFile(is, file);
     
        } catch (Exception e) {
            isSucceed = false;
        }
     
        httpGet.releaseConnection();
        
    } catch (Exception e) {
        isSucceed = false;
    }

    return isSucceed;
   }

    
  /**
     * @param url*
     * @param UA*
     * @param charset*
     * @param baseUri*
     * @return **********************************************************************/
  
  public static Document getDocument(URL url, String UA, String charset, String baseUri)  {
    
    Document doc= null;
    
    try (CloseableHttpClient httpClient = HttpClients
                                                .custom()
                                                .setUserAgent(UA)
                                                .build()) {
    
        HttpGet httpGet = new HttpGet(url.toString());

        try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
             InputStream is = (httpResponse.getEntity()!=null ? httpResponse.getEntity().getContent() : null)) {

             doc= Jsoup.parse(is, charset, baseUri);

        } catch (Exception e) {
            doc = null;
        }

        httpGet.releaseConnection();    
    } catch (Exception e) {
            doc = null;
    }
    
    return doc;
 }   
    
}
