package io.github.gpizzimenti.bookbynav;

/**
 *
 * @author Giuseppe Pizzimenti
 */


import java.io.File;

import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.safety.Whitelist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.gpizzimenti.bookbynav.utils.DateUtils;
import io.github.gpizzimenti.bookbynav.utils.ExceptionUtils;
import io.github.gpizzimenti.bookbynav.utils.HttpUtils;


public class WebCrawler {
    
    private final Logger logger;
    private Configuration cfg;
    private int pageCounter;

    public WebCrawler(Configuration configuration) {
        this.logger = LoggerFactory.getLogger(WebCrawler.class.getSimpleName());
        this.cfg = configuration;
        this.pageCounter = 0;
        
    }
    
    public  LinkedList<Page> run() {

        LinkedList<Page> pages = new LinkedList<>();

        try {
                logger.info("{}",DateUtils.now() + " : STARTED");
                        
                if (cfg.isVerboseLog()) logger.info("{}",DateUtils.now() + " : connecting to " + cfg.getStartUrl());
                
                //Document startPage = Jsoup.connect(cfg.getStartUrl()).get();   
                //we do not use Jsoup.connect, so we can set our charset and user agent string
                Document startPage = HttpUtils.getDocument(new URL(cfg.getStartUrl()), cfg.getUserAgent() , cfg.getCharset(), (StringUtils.isNotBlank(cfg.getBaseUri()) ? cfg.getBaseUri() :  FilenameUtils.getFullPath(cfg.getStartUrl())));
                
                if (StringUtils.isBlank(cfg.getBaseUri())) 
                        cfg.setBaseUri(startPage.baseUri());
                
                if (cfg.isVerboseLog()) logger.info("{}",DateUtils.now() + " : searching for " + cfg.getNavigationSelector());

                Element navigation = startPage.selectFirst(cfg.getNavigationSelector());
                
                if (cfg.isVerboseLog()) {
                    if (navigation != null)
                        logger.info("{}",DateUtils.now() + " : processing data from " + cfg.getNavigationSelector());
                    else
                        logger.info("{}",DateUtils.now() + " : element not found for selector " + cfg.getNavigationSelector());
                }

                processList(navigation,pages);

                logger.info("{}",DateUtils.now() + " : COMPLETE");
                
        } 
        catch (Exception exc) {
          logger.error("{}",  ExceptionUtils.getCallerMethodAndError(exc));
        }
        
        return pages;        
    };    
    
    
    
    /****************************************************************************/ 
    
    private  void processList(Element container, LinkedList<Page> pages) {
        
        try {
            Elements lists = container.select(cfg.getUlSelector()); 
                
            for (Element list : lists) {
                for (Iterator<Element> pointsIter = list.select(cfg.getLiSelector()).iterator();  pointsIter.hasNext(); ) {
                    processPoint(pointsIter.next(),pages);
                }
                
                //if (pageCounter>0) break;
            } 
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
        }
        
    };
    
    /****************************************************************************/     
    
    private  void processPoint(Element li, LinkedList<Page> pages) {
        
        try {        
        
                    Element a = li.selectFirst("a");

                    Page page = new Page();

                    pageCounter = pageCounter+1;
                    
                    page.setPageNr(pageCounter);   
                    page.setUrl(a.absUrl("href"));
                    page.setTitle(a.ownText());
                    
                    URL url = new URL(page.getUrl());
                    page.setName(String.format("%04d", page.getPageNr()) + "_" + FilenameUtils.getBaseName(url.getPath()));
                    
                    processPage(page);

                    pages.add(page);
                    
                    //if there is not a selected menu selector ..let's see if this element contains a list and then process it recursively!
                    if (StringUtils.isBlank(cfg.getActiveMenuSelector())) {
                        processList(li, page.children);  
                    }             
                        
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
        }                        
    
    };
    
    /****************************************************************************/ 
    
    private  void processPage(Page page) {
        
        try {        
             if (cfg.isVerboseLog()) logger.info("{}",DateUtils.now() + " : processing page " + page.getName());
             
             //Document doc = Jsoup.connect(page.getUrl()).get();  
             //we do not use Jsoup.connect, so we can set our charset and user agent string
             Document doc = HttpUtils.getDocument(new URL(page.getUrl()), cfg.getUserAgent() , cfg.getCharset(), cfg.getBaseUri());
             Element selectedMenu = null;
             
             //if there is a selected menu which expands a submenu ..let's process it recursively! ..but before we process the html!
             if (StringUtils.isNotBlank(cfg.getActiveMenuSelector())) {

                Element navigation = doc.selectFirst(cfg.getNavigationSelector());
                selectedMenu = navigation.selectFirst(cfg.getActiveMenuSelector());
                
                if (selectedMenu != null) { 
                    if (selectedMenu.is("a")) selectedMenu = selectedMenu.parent(); //... and let's start the process with the <li>
                    if (!selectedMenu.is("li")) selectedMenu = null; 
                }
             }             
             
            Elements article =  doc.select(cfg.getArticleSelector());

            if (article != null && article.size()>0)
                doc = Jsoup.parseBodyFragment(article.html(),cfg.getBaseUri());             
             
            doc = processImages(
                     processLinks(
                        preserveClasses(
                            filterOutHtml(doc)
                        )
                     )
                   );    
             
            String html = restoreClasses(
                                            Jsoup.clean(doc.html(),
                                                        cfg.getBaseUri(),
                                                        Whitelist.relaxed()
                                                                .addTags("figure","figcaption")
                                                                .addAttributes(":all", "preserve-class","id")
                                                                .preserveRelativeLinks(true)
                                                        )
                                          );
    
            File file= new File(cfg.getFolderText(),page.getName() + ".html" ); 
            
            try (FileOutputStream fs = new FileOutputStream(file) ; Writer fw = new OutputStreamWriter(fs, cfg.getCharset())) { 
                fw.write(html);
            };

             //if there is a selected menu which expands a submenu ..let's process it recursively!
            if ((selectedMenu != null) && (selectedMenu.is("li"))) processList(selectedMenu, page.children);  

        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
        }                        
    
    };  
    

    /****************************************************************************/     
    
    private Document filterOutHtml(Document document) {        
        Document doc = document;
        
        try {
            
            for (String selector : cfg.getRemoveSelectors()) {
                Elements els = doc.select(selector);
                els.remove();
            }
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
        }    
        
        return doc;
    } 
    
    

    /****************************************************************************/     
    
    private Document preserveClasses(Document document) {        
        Document doc = document;
        
        if (cfg.getPreserveClasses() != null) {
            try {

                for (String selector : cfg.getPreserveClasses()) {
                    Elements els = doc.select(selector);
                    els.attr("preserve-class", selector);
                }
            } 
            catch (Exception exc) {
              logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
            }    
        }            
        return doc;
    }     
    
    /****************************************************************************/     
    
    private Document processLinks (Document document) {        
        Document doc = document;
        
        try {
            Elements links = doc.select("a[href]");
             
            for (Element link : links) {
                if (!link.attr("href").startsWith("#")) 
                    link.attr("href",link.absUrl("href"));
            }      
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
        }    
        
        return doc;
    }      
    
    /****************************************************************************/     
    
    private Document processImages (Document document) {        
        Document doc = document;
        
        try {
            Elements images = doc.select("img[src]");
             
            for (Element image : images) {             
                URL url = new URL(image.absUrl("src"));
                String name = String.format("%04d", pageCounter) + "_" + FilenameUtils.getName(url.getPath());
                File path = new File(cfg.getFolderImages());
                File img = new File(path,name);
                HttpUtils.saveFile(url, img , cfg.getUserAgent());
                //FileUtils.copyURLToFile(url , img);
                
                image.attr("src","..\\Images\\" + name);
            }      
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
        }    
        
        return doc;
    }    
    
    /****************************************************************************/     
    
    private String restoreClasses(String html) {        
        Document doc = Jsoup.parseBodyFragment(html,cfg.getBaseUri());
        
        try {
            Elements els = doc.select("[preserve-class]");
            
            for (Element el : els) {
                String val = el.attr("preserve-class");
                el.attr("class", val);
                el.removeAttr("preserve-class");
            }
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
        }    
        
        return doc.html();
    }     
        
    /****************************************************************************/      
    
}
