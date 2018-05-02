package io.github.gpizzimenti.bookbynav;

/**
 *
 * @author Giuseppe Pizzimenti
 */

import java.util.LinkedList;

public class Page {

    private int pageNr;
    private String url;
    private String name;
    private String title;
    protected LinkedList<Page> children = new LinkedList<>();
            
    public Page() {
    }

    public Page(int pageNr, String name, String url, String title, LinkedList<Page> children) {
        this.pageNr = pageNr;
        this.url = url;
        this.title = title;
        this.children = (children == null ? new LinkedList<>() : children);
    }

    public int getPageNr() {
        return pageNr;
    }

    public void setPageNr(int pageNr) {
        this.pageNr = pageNr;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
