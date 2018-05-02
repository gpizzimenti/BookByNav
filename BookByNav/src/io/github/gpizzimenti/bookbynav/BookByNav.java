package io.github.gpizzimenti.bookbynav;

import java.io.File;
import java.net.URI;

import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.gpizzimenti.bookbynav.utils.CommandLineParser;
import io.github.gpizzimenti.bookbynav.utils.DateUtils;
import io.github.gpizzimenti.bookbynav.utils.ExceptionUtils;
import io.github.gpizzimenti.bookbynav.utils.ResourceUtils;


 /*
 * @author Giuseppe Pizzimenti
 */

public class BookByNav {

    /**
     * @param args the command line arguments
     */
    
    private final static Logger logger = LoggerFactory.getLogger(BookByNav.class.getSimpleName());    
    
    public static void main(String[] args) {
        
        Configuration cfg = new Configuration();
        CommandLineParser arguments = new CommandLineParser(args);
        
        if (loadParameters(arguments,cfg)) {
            
            WebCrawler crawler = new WebCrawler(cfg);
            BookBuilder builder = new BookBuilder(cfg);
            
            builder.run(crawler.run());  
           
            purgeElab(cfg);
        }
    }
    
    
    /****************************************************************************/ 
    
    private static boolean loadParameters(CommandLineParser parameters,Configuration cfg) {    
        
        boolean loaded = false;
        
        try {        

            if (parameters.containsKey("startUrl")) cfg.setStartUrl(parameters.getValue("startUrl"));  else throw new IllegalArgumentException("Missing required parameter: startUrl");
            if (parameters.containsKey("navigationSelector")) cfg.setNavigationSelector(parameters.getValue("navigationSelector"));  else throw new IllegalArgumentException("Missing required parameter: navigationSelector");
            
            if (parameters.containsKey("bookName")) cfg.setBookName(parameters.getValue("bookName"));  else  {  
                URI uri = new URI(cfg.getStartUrl());
                cfg.setBookName(uri.getHost().replace(".","_") );
            }
            
            if (parameters.containsKey("bookTitle")) cfg.setBookTitle(parameters.getValue("bookTitle"));  else  cfg.setBookTitle(cfg.getBookName());  
            
            if (parameters.containsKey("articleSelector")) cfg.setArticleSelector(parameters.getValue("articleSelector")); else cfg.setArticleSelector("body");  
            
            if (parameters.containsKey("ulSelector")) cfg.setUlSelector(parameters.getValue("ulSelector")); else cfg.setUlSelector("> ul");            
            if (parameters.containsKey("liSelector")) cfg.setLiSelector(parameters.getValue("liSelector")); else cfg.setLiSelector("> li");
            if (parameters.containsKey("removeSelectors")) cfg.setRemoveSelectors(parameters.getValue("removeSelectors").split(","));
            if (parameters.containsKey("preserveClasses")) cfg.setPreserveClasses(parameters.getValue("preserveClasses").split(","));            
            if (parameters.containsKey("activeMenuSelector")) cfg.setActiveMenuSelector(parameters.getValue("activeMenuSelector"));
            if (parameters.containsKey("userAgent")) cfg.setUserAgent(parameters.getValue("userAgent")); else cfg.setUserAgent(ResourceUtils.getTextResource("UA.txt"));
            if (parameters.containsKey("charset")) cfg.setCharset(parameters.getValue("charset")); else cfg.setCharset("UTF-8");                                    
            if (parameters.containsKey("baseUri")) cfg.setBaseUri(parameters.getValue("baseUri"));
            
            if (parameters.containsKey("verboseLog") && (parameters.getValue("verboseLog").equalsIgnoreCase("true") || parameters.getValue("verboseLog").equalsIgnoreCase("yes") || parameters.getValue("verboseLog").equals("1"))) cfg.setVerboseLog(true); else cfg.setVerboseLog(false); 
            
            if (parameters.containsKey("folder")) cfg.setFolderUser(parameters.getValue("folder"));  else cfg.setFolderUser(System.getProperty("user.dir"));                        

            loaded = createFolders(cfg); 
        } 
        catch (Exception exc) {
            logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
            loaded = false; 
        }   
        
        return loaded;
    };    
    
    
    /****************************************************************************/ 
    
    private static boolean createFolders(Configuration cfg) {    
        
        boolean created= false;
        
        try {        
            
            cfg.setFolderUser(cfg.getFolderUser());
            cfg.setFolderElab(cfg.getFolderUser() + "\\Elab");            
            cfg.setFolderBook(cfg.getFolderElab() + "\\Book");            
            cfg.setFolderImages(cfg.getFolderBook() + "\\OEBPS\\Images");            
            cfg.setFolderText(cfg.getFolderBook() + "\\OEBPS\\Text");                
            cfg.setFolderStyle(cfg.getFolderBook() + "\\OEBPS\\Styles");
            cfg.setFolderFonts(cfg.getFolderBook() + "\\OEBPS\\Fonts");

            File pathUser = new File(cfg.getFolderUser());
            if (pathUser.exists()) FileUtils.forceDelete(pathUser); ;
            FileUtils.forceMkdir(pathUser);
                        
            File pathElab = new File(cfg.getFolderElab());
            FileUtils.forceMkdir(pathElab);
            
            File pathBook = new File(cfg.getFolderBook());
            FileUtils.forceMkdir(pathBook);

            File pathMeta = new File(cfg.getFolderBook() + "\\META-INF");
            FileUtils.forceMkdir(pathMeta);                              
            
            File pathImages = new File(cfg.getFolderImages());
            FileUtils.forceMkdir(pathImages);                      
            
            File pathText = new File(cfg.getFolderText());
            FileUtils.forceMkdir(pathText);                  
            
            File pathStyles = new File(cfg.getFolderStyle());
            FileUtils.forceMkdir(pathStyles);                    

            File pathFonts = new File(cfg.getFolderFonts());
            FileUtils.forceMkdir(pathFonts);                    
            
            created = true; 
        } 
        catch (Exception exc) {
            logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
            created = false; 
        }   
        
        return created;
    };      
    
          
    /****************************************************************************/     
    
    private static void purgeElab(Configuration cfg) {   
        
        try {    
            
            if (cfg.isVerboseLog()) logger.info("{}",DateUtils.now() + " : purging downloaded files");
            
               System.gc();
               File folder = new File(cfg.getFolderElab());
               FileUtils.deleteDirectory(folder);
               
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
        }         
    }
    
}
