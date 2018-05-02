package io.github.gpizzimenti.bookbynav.utils;

/**
 *
 * @author Giuseppe Pizzimenti
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    

  /**
     * @return 
  **************************************************************************/ 

  public static String now() {    
      
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); 
        Date now = new Date();
        
        return sdf.format(now);       
  }
  
  /****************************************************************************/ 
    
}
