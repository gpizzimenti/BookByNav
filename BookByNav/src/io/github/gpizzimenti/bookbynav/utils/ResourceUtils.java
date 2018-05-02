package io.github.gpizzimenti.bookbynav.utils;

/**
 *
 * @author Giuseppe Pizzimenti
 */


import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;


public class ResourceUtils {

  private static final String resourcePath = "io/github/gpizzimenti/bookbynav/resources/";
    
  /**
     * @param resource
     * @return 
  **************************************************************************/ 

  public static String getTextResource(String resource) {    
    String res = null;
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    try (InputStream is = classLoader.getResourceAsStream(resourcePath + resource)) {   
            res = IOUtils.toString(is, StandardCharsets.UTF_8);
    } catch (Exception e) {
        res = null;
    }      
    
    return res;
  }
  
  /**
     * @param resource
     * @param file
     * @return 
  **************************************************************************/   
  
    public static boolean saveResource(String resource,File file) {    
    
    boolean success = false;
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    
    try (InputStream is = classLoader.getResourceAsStream(resourcePath + resource)) {   
            FileUtils.copyInputStreamToFile(is, file);
            success = true;
    } catch (Exception e) {
        success = false;
    }      
    
    return success;
  }
    
}
