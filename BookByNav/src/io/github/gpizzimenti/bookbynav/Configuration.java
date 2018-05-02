package io.github.gpizzimenti.bookbynav;

/**
 *
 * @author Giuseppe Pizzimenti
 */

public class Configuration {

    private String bookName;
    private String bookTitle;    
    
    private String startUrl;
    
    private String folderUser;
    private String folderElab;
    private String folderBook;
    private String folderImages;
    private String folderText;
    private String folderStyle;
    private String folderFonts;
    
    private String navigationSelector;
    private String articleSelector;    
    private String ulSelector;        
    private String liSelector;            
    private String activeMenuSelector;    
    private String[] removeSelectors;       
    private String[] preserveClasses;       
    
    private String UserAgent;
    
    private String charset;
    private String baseUri;    
    
    private boolean verboseLog;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getStartUrl() {
        return startUrl;
    }

    public void setStartUrl(String startUrl) {
        this.startUrl = startUrl;
    }

    public String getFolderUser() {
        return folderUser;
    }

    public void setFolderUser(String folderUser) {
        this.folderUser = folderUser;
    }

    public String getFolderElab() {
        return folderElab;
    }

    public void setFolderElab(String folderElab) {
        this.folderElab = folderElab;
    }

    public String getFolderBook() {
        return folderBook;
    }

    public void setFolderBook(String folderBook) {
        this.folderBook = folderBook;
    }

    public String getFolderImages() {
        return folderImages;
    }

    public void setFolderImages(String folderImages) {
        this.folderImages = folderImages;
    }

    public String getFolderText() {
        return folderText;
    }

    public void setFolderText(String folderText) {
        this.folderText = folderText;
    }

    public String getFolderStyle() {
        return folderStyle;
    }

    public void setFolderStyle(String folderStyle) {
        this.folderStyle = folderStyle;
    }

    public String getNavigationSelector() {
        return navigationSelector;
    }

    public void setNavigationSelector(String navigationSelector) {
        this.navigationSelector = navigationSelector;
    }

    public String getFolderFonts() {
        return folderFonts;
    }

    public void setFolderFonts(String folderFonts) {
        this.folderFonts = folderFonts;
    }

    public String getArticleSelector() {
        return articleSelector;
    }

    public void setArticleSelector(String articleSelector) {
        this.articleSelector = articleSelector;
    }

    public String getUlSelector() {
        return ulSelector;
    }

    public void setUlSelector(String ulSelector) {
        this.ulSelector = ulSelector;
    }

    public String getLiSelector() {
        return liSelector;
    }

    public void setLiSelector(String liSelector) {
        this.liSelector = liSelector;
    }

    public String getActiveMenuSelector() {
        return activeMenuSelector;
    }

    public void setActiveMenuSelector(String activeMenuSelector) {
        this.activeMenuSelector = activeMenuSelector;
    }

    public String[] getRemoveSelectors() {
        return removeSelectors;
    }

    public void setRemoveSelectors(String[] removeSelectors) {
        this.removeSelectors = removeSelectors;
    }

    public String[] getPreserveClasses() {
        return preserveClasses;
    }

    public void setPreserveClasses(String[] preserveClasses) {
        this.preserveClasses = preserveClasses;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getUserAgent() {
        return UserAgent;
    }

    public void setUserAgent(String UserAgent) {
        this.UserAgent = UserAgent;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public boolean isVerboseLog() {
        return verboseLog;
    }

    public void setVerboseLog(boolean verboseLog) {
        this.verboseLog = verboseLog;
    }


    
}
