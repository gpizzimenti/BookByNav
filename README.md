# BookByNav
A (very basic & rough) command-line utility to create an EPUB from online documentation, written in Java.

The structure of the EPUB is inferred from the navigation menu, assuming a UL/OL --> LI --> A basic structure

## Usage examples (as tested May 04,2018)

bookbynav.exe \-\-folder="C:\VueXDocs" \-\-startUrl="https://vuex.vuejs.org/en/" \-\-navigationSelector="nav[role='navigation']" \-\-articleSelector=".page-inner" \-\-liSelector="> li.chapter" \-\-removeSelectors="head,script,.search-results" \-\-verboseLog="yes" \-\-bookTitle="VueX Guide" \-\-bookName="VueX Guide"

bookbynav.exe \-\-folder="C:\LiferayDocs" \-\-startUrl="https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-0/introduction-to-liferay-development" \-\-navigationSelector=".kbarticle-navigation" \-\-articleSelector=".kb-article-container" \-\-activeMenuSelector=".kbarticle-selected" \-\-removeSelectors="head,script,#banner,#footer,.kb-article-tools,.kb-article-siblings,.taglib-social-bookmarks,.taglib-ratings" \-\-preserveClasses=".kb-elements" \-\-verboseLog="yes"  \-\-bookTitle="Liferay 7.0 Tutorial" \-\-bookName="LiferayTutorial70"

## Parameters

| NAME  |REQUIRED   |DESCRIPTION   |DEFAULT   |
| ------------ | :---: | ------------ | ------------ |
|   startUrl|  YES |the starting URL for the web crawler    |   |
|  navigationSelector |YES   |the pseudo-CSS rule selecting the container element  of the navigation, structured in a gerarchy of  OL/UL -> LI -> A )  |   |
|  folder |NO |the folder where the EPUB will be produced     | executable's location   |
|  bookName |NO   | the name of the EPUB   | startUrl's hostname  |
|  bookTitle | NO  | the descriptive title of the EPUB   |   startUrl's hostname |
|  articleSelector | NO  |  the pseudo-CSS rule selecting the container element of the main content | "BODY" |
| ulSelector  |   NO | the pseudo-CSS rule selecting the list element  | "> UL"  |
|  liSelector | NO   |  the pseudo-CSS rule selecting the children elements of the list  |   "> LI" |
|  removeSelectors |NO   | the comma-separated list of pseudo-CSS rules to filter out elements of the page   |   |
| preserveClasses  | NO  | the comma-separated list of class names that will be NOT stripped away  |   |
| activeMenuSelector  |NO   | the pseudo-CSS rule selecting the LI (or A child)  currently selected in the navigation; if not provided, the navigation  & structure of the documentation will be recursively inferred from the gerarchy of the lists (es: UL --> Li --> UL --> LI ....)  |   |
|  userAgent |  NO | the user agent string used for web crawling  |  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36" |
| charset  | NO  |  character set used to retrieve content & produce the EPUB | "UTF-8"  |
|  baseUri | NO  | force the base path used by the web crawler for relative links   | BASE path of the retrieved page   |
| verboseLog  | NO   |  if "yes", will log every operation|  "no"  |


## .EXE download & creation

The .EXE can be downloaded from [here][1], and it's been built with [Launch4j][2].

If you want to rebuild it, you must edit [launch4j.xml][3] & the *makeexe* Ant task in [build-impl.xml][4] with your local paths 

[1]: https://github.com/gpizzimenti/BookByNav/blob/master/BookByNav/exe/bookbynav.exe "bookbynav.exe - 13.5 Mb"
[2]: http://launch4j.sourceforge.net/ "Go to Launch4j homepage"
[3]: https://github.com/gpizzimenti/BookByNav/blob/master/BookByNav/exe/launch4j.xml "launch4j configuration "
[4]: https://github.com/gpizzimenti/BookByNav/blob/master/BookByNav/nbproject/build-impl.xml "ANT tasks"
