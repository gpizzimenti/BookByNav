package io.github.gpizzimenti.bookbynav.utils;

/**
 *
 * @author Giuseppe Pizzimenti
 */

public class ExceptionUtils {
    
  /****************************************************************************/

  public static String getCaller(Throwable t) {

    StackTraceElement caller = null; 
    String msg = "";
    String pck = "io.github.gpizzimenti.";

    for (StackTraceElement st : t.getStackTrace()){
            if (st.getClassName().startsWith(pck)) {
                caller=st;
                break;
            }
    }
    
    if (caller == null)  caller = t.getStackTrace()[0];
            
    msg=caller.getMethodName()+" ("+caller.getLineNumber()+")";                            
    
    return msg;

 }


 /**
     * @param t 
     * @return 
 *************************************************************************/ 

 public static String getCallerMethodAndError(Throwable t) {
        
        return DateUtils.now() + " : " + getCaller(t) + " : "+ t.toString();
 } 
 
 /*************************************************************************/ 
 
}
