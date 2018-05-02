package io.github.gpizzimenti.bookbynav;

/**
 *
 * @author Giuseppe Pizzimenti
 */

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.ListIterator;
import java.util.Collections;
import java.util.UUID;

import java.io.File;

import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Entities;
import org.jsoup.nodes.XmlDeclaration;
import org.jsoup.parser.Parser;

import eu.medsea.mimeutil.MimeUtil;

import io.github.gpizzimenti.bookbynav.utils.DateUtils;
import io.github.gpizzimenti.bookbynav.utils.ExceptionUtils;
import io.github.gpizzimenti.bookbynav.utils.ResourceUtils;
import io.github.gpizzimenti.bookbynav.utils.ZipUtils;



/**
 *
 * @author Giuseppe Pizzimenti
 */
public class BookBuilder {
    
    private final  Logger logger;
    private Configuration cfg;
    private int innerDepth;
    private int depth;
    private final UUID uuid;
    

    public BookBuilder(Configuration configuration) {
        this.logger = LoggerFactory.getLogger(BookBuilder.class.getSimpleName());
        this.cfg = configuration;        
        this.uuid = UUID.randomUUID();
        this.innerDepth = 1;
        this.depth = 1;
    }

    /**
     * @param pages
     *************************************************************************/ 
    
    public void run(LinkedList<Page> pages) {
        
        try {
                logger.info("{}",DateUtils.now() + " : STARTED");        

                HashMap<String,Page>  pagesMap = flattenPages(pages);
                processPages(pagesMap);
                
                buildBook(pages,pagesMap);
    
                logger.info("{}",DateUtils.now() + " : COMPLETE");
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
        }
        
    };       
    
    /****************************************************************************/     
    
    
    private HashMap<String,Page>  flattenPages (LinkedList<Page> pages) {    
        
        HashMap<String,Page> pMap = new HashMap<>();
        
        try {
        
            for (Page page : pages) {
                pMap.put(page.getUrl().split("#")[0].trim(),page);

                if (page.children.size()>0)
                     pMap.putAll(flattenPages(page.children));
            }         
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
        }
        return pMap;        
    }        
  
    /****************************************************************************/     
    
    private void processPages (HashMap<String,Page> pages) {        

        try {
            
            for (String key : pages.keySet()) {
               Page page =  pages.get(key);
               
               if (cfg.isVerboseLog()) logger.info("{}",DateUtils.now() + " : processing page " + page.getName());
               
               File input = new File(cfg.getFolderText(),page.getName()+".html");
               Document doc = Jsoup.parse(input, cfg.getCharset(),cfg.getBaseUri());               
               
               buildPage(page,localizeLinks(doc,pages));
            }
            
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
        }        
    }

    /****************************************************************************/     
    
    private Document localizeLinks (Document document, HashMap<String,Page> pages) {        
         
        Document doc = document;
        
        try {
            Elements links = doc.select("a[href]");
             
            for (Element link : links) {
                
             if (!link.attr("href").startsWith("#")) {  
                 
                String href= link.absUrl("href");                
                String hash = "";                
                
                if (href.contains("#")) {
                    hash = href.substring(href.indexOf("#"));
                    href = href.split("#")[0].trim();
                }
                
                if (pages.containsKey(href)) {
                    Page page = pages.get(href);
                    link.attr("href",page.getName() + ".xhtml" + hash);
                }
                
             }   
            }     
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
        }    
        
        return doc;
        
    }    
    
    /****************************************************************************/     
    
    private Document prepareDoc(String xml) {      
        Document doc = null;
        
        try {                
            doc = Jsoup.parse(xml, "UTF-8", Parser.xmlParser());

            doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);    
            doc.outputSettings().charset(cfg.getCharset());
            doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);

            XmlDeclaration decl = (XmlDeclaration) doc.childNode(0);
            decl.attr("encoding",cfg.getCharset()); 
            
            } 
            catch (Exception exc) {
              logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
            }         
        
        return doc;
    }      
    
    /****************************************************************************/     
    
    private void buildPage(Page page,Document document) {    
        
        try {        
            String pageTemplate = ResourceUtils.getTextResource("page.xhtml");
            
            if (pageTemplate != null) {
                 Document doc = prepareDoc(pageTemplate);
                 
                 doc.select("title").html(page.getTitle());
                 doc.select("body").html(document.html());
                 doc.select("head").prepend("<meta charset=\"" + cfg.getCharset()+ "\">");
                 
                 File file = new File(cfg.getFolderText(),page.getName()+".xhtml");
                 FileUtils.writeStringToFile(file, doc.html(), cfg.getCharset());
                 
                 file = new File(cfg.getFolderText(),page.getName()+".html");
                 FileUtils.deleteQuietly(file);
                 
            } else {
                logger.error("{}","Missing or unavailable resource: page.xhtml");
            }
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
        }            
    }
    
    /****************************************************************************/     
    
    private void buildBook(LinkedList<Page> pages, HashMap<String,Page>  pagesMap) {    
        try {        
            
            logger.info("{}",DateUtils.now() + " : building book " + cfg.getBookName() + ".epub");

               if (buildCover()) 
                     if (buildOPF(pagesMap)) 
                         if (buildTOC(pages)) 
                            if (linkResources())          
                                if (zipIt()) {    
                                    logger.info("{}",DateUtils.now() + " : book successfully created at " + cfg.getFolderUser() + "\\" + cfg.getBookName() + ".epub");
                                }
                 
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
        }            
    }
    
    /****************************************************************************/     
    
    private boolean buildCover() {   
        
        boolean success = false;
        
        try {        
            
            if (cfg.isVerboseLog()) logger.info("{}",DateUtils.now() + " : adding cover");
            
            File cover = new File(cfg.getFolderImages(),"cover.png");
            
            if (ResourceUtils.saveResource("cover.png", cover)) {

                String coverTemplate = ResourceUtils.getTextResource("cover.xhtml");

                if (coverTemplate != null) {
                     Document doc = prepareDoc(coverTemplate);

                     doc.select("head").prepend("<meta charset=\"" + cfg.getCharset()+ "\">");

                     //TODO: add book title to cover image

                    File file = new File(cfg.getFolderText(),"cover.xhtml");
                    FileUtils.writeStringToFile(file, doc.html(), cfg.getCharset());
     
                    success = true;
            
                } else {
                    logger.error("{}","Missing or Unavailable resource: cover.xhtml");
                    success = false;
                }       
            } else {
                logger.error("{}","Missing or Unavailable resource: cover.png");
                success = false;
            }              
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
          success = false;
        } 
        
        return success;
    }    
    
    /****************************************************************************/     

    private boolean buildOPF(HashMap<String,Page>  pagesMap) {   
        
        boolean success = false;
        
        try {        
            
            if (cfg.isVerboseLog()) logger.info("{}",DateUtils.now() + " : adding OPF");
            
            String opfTemplate = ResourceUtils.getTextResource("content.opf");
            
            if (opfTemplate != null) {
                 Document doc = prepareDoc(opfTemplate);
                 
                 doc.select("#booktitle").html(cfg.getBookTitle());
                 doc.select("#bookid").html(this.uuid.toString());
                 
                 Element manifest = doc.selectFirst("manifest");
                 Element spine = doc.selectFirst("spine");
                 
                 LinkedList<Page> pagesList = new LinkedList<>(pagesMap.values());
                 
                 Collections.sort(pagesList, (Page p1, Page p2) -> {
                     return p1.getPageNr() < p2.getPageNr() ? -1 : (p1.getPageNr() > p2.getPageNr()) ? 1 : 0;
                 });
                 
                 for (Page page : pagesList) {
                     
                     Element item = new Element("item");
                             item.attr("id","page-" + String.format("%04d", page.getPageNr()));
                             item.attr("media-type","application/xhtml+xml");
                             item.attr("href","Text/" +page.getName()+".xhtml");
                             
                     item.appendTo(manifest);
                     
                     Element itemref = new Element("itemref");
                             itemref.attr("idref","page-" + String.format("%04d", page.getPageNr()));
                             
                     itemref.appendTo(spine);
                 };
                 
                MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector"); 
                
                ListIterator it = (ListIterator)FileUtils.iterateFiles(new File(cfg.getFolderImages()), null, true);
                while(it.hasNext()){
                    
                     File img = (File)it.next();
                     Collection<?> mimeTypes = MimeUtil.getMimeTypes(img);
                     
                     String mime = (mimeTypes.size()> 0) ?  mimeTypes.toString() : "image/jpg";
                     
                     Element item = new Element("item");
                             item.attr("id","image-" + String.format("%04d", it.nextIndex()));
                             item.attr("media-type",mime);
                             item.attr("href","Images/" + img.getName());     
                             
                     item.appendTo(manifest);         
                }                 
                 
                 File file = new File(cfg.getFolderBook() + "\\OEBPS","content.opf");
                 FileUtils.writeStringToFile(file, doc.html(), cfg.getCharset());
                
                 success = true;
                 
            } else {
                logger.error("{}","Missing or Unavailable resource: content.opf");
                success = false;
            }            
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
          success = false;
        } 
        
        return success;
    }     
          
    
    
    /****************************************************************************/     
    
    private boolean buildTOC(LinkedList<Page> pages) {   
        boolean success = false;
        
        try {        
            if (cfg.isVerboseLog()) logger.info("{}",DateUtils.now() + " : adding TOC");
            
            String ncxTemplate = ResourceUtils.getTextResource("toc.ncx");
            
            if (ncxTemplate !=null) {
                 Document ncx = prepareDoc(ncxTemplate);
                                 
                 ncx.select("doctitle text").html(cfg.getBookTitle());
                 Element navmap = ncx.selectFirst("navmap");
                         
                 addPagesToTOC(pages,navmap);

                 ncx.select("meta[name=dtb:depth]").attr("content",Integer.toString(this.depth));
                 ncx.select("meta[name=dtb:uid]").attr("content","urn:uuid:" + this.uuid.toString());
                 
                 File file = new File(cfg.getFolderBook() + "\\OEBPS","toc.ncx");
                 FileUtils.writeStringToFile(file, ncx.html(), cfg.getCharset());
                 
               success = true;
               
            } else {
                if (ncxTemplate ==null) logger.error("{}","Missing or Unavailable resource: toc.ncx");
                success = false;
            }                   
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
          success = false;
        } 
        
        return success;
    }                
    
    /****************************************************************************/     
    
    private void addPagesToTOC(LinkedList<Page> pages,Element nav) {   

        try {            
            
                 for (Page page : pages) {
                     
                        Element navPoint = new Element("navPoint");
                                navPoint.attr("id","page-" + String.format("%04d", page.getPageNr())); 
                                navPoint.attr("playOrder",Integer.toString(page.getPageNr()));
                        
                        Element navLabel = new Element("navLabel");                                
                        Element text = new Element("text");                                
                                text.html(page.getTitle());
                        Element content = new Element("content");                                
                                content.attr("src","Text/" + page.getName() + ".xhtml");
                        text.appendTo(navLabel);
                        navLabel.appendTo(navPoint);
                        content.appendTo(navPoint);
                        
                       
                        if (page.children.size()>0) {
                            innerDepth += 1;
                            if (innerDepth > depth) depth = innerDepth;
                            addPagesToTOC(page.children,navPoint);   
                        } else 
                            innerDepth = 1;
                        
                     navPoint.appendTo(nav);
                }                              
                        
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));

        }                  
    }                
    
    /****************************************************************************/     
    
    private boolean linkResources() {   
        
        boolean success = false;
        
        try {        
            
            if (cfg.isVerboseLog()) logger.info("{}",DateUtils.now() + " : adding resources");
            
            if (!ResourceUtils.saveResource("mimetype",  new File(cfg.getFolderBook() ,"mimetype"))) {
                logger.error("{}","Missing or Unavailable resource: mimetype");
                return false;
            }            

            if (!ResourceUtils.saveResource("container.xml",  new File(cfg.getFolderBook() + "\\META-INF","container.xml"))) {
                logger.error("{}","Missing or Unavailable resource: container.xml");
                return false;
            }
            
            if (!ResourceUtils.saveResource("style.css", new File(cfg.getFolderStyle(),"style.css"))) {
                logger.error("{}","Missing or Unavailable resource: style.css");
                return false;
            }
            
            if (!ResourceUtils.saveResource("FiraMono-Regular.ttf", new File(cfg.getFolderFonts(),"FiraMono-Regular.ttf"))) {
                logger.error("{}","Missing or Unavailable resource: FiraMono-Regular.ttf");
                return false;
            } 

            success = true;
        
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
          success = false;
        } 
        
        return success;
    }    
        
    
    /****************************************************************************/     
    
    private boolean zipIt() {       
        
        boolean success = false;
        
        try {       
            if (cfg.isVerboseLog()) logger.info("{}",DateUtils.now() + " : zipping");
            
            ZipUtils zip = new ZipUtils();
            File[] folderToZip  =   new File(cfg.getFolderBook()).listFiles() ; 
            zip.zip(Arrays.asList(folderToZip) , cfg.getFolderUser() + "\\" + cfg.getBookName() + ".epub");            
            
            success = true;
        } 
        catch (Exception exc) {
          logger.error("{}",ExceptionUtils.getCallerMethodAndError(exc));
          success = false;
        }         
        return success;
    }     
                          
          
}
