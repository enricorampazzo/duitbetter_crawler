# DU it better crawler: a recursive crawler without recursion

This is the crawler that I have created to scrape the DU website, however the only part that is
customized to the du website is where I extract the relevant text from the webpages using specific
css classes, the rest of the rules could be moved to a configuration file to make this software
general-purpose

## How it works

Pages have links, some have links to each other: to avoid the stack overflow issues that could arise 
arise from finding the links and parsing the linked pages akin to what it is done when scanning
a directory tree (which is a tree, whereas a website is a graph), this crawler uses a two-steps 
approach:

Given the start URL

1) The url is scraped to find all the links and the page content. For each link a node in the graph
database is created and linked with the starting page
2) The graph database is queried to find all the nodes that refers to pages that have not been scraped yet 
and one of them is scraped, repeating step one for the new page

This process continues until all the webpage nodes have been scraped.

To scrape the pages I use Chrome via Selenium: this is because du webpages are built dynamically 
using Javascript, so you can only get the page content and the hyperlinks if you execute
the JS code in the page.
Because the pages never stop loading, I had to put a page load timeout: this makes the whole
process much longer than it could be, but I see no alternative

Once the HTML source page is captured and stored in the database, first I remove "useless" tags
such as `script`, `link`, `iframe` etc, then I extract the relevant text using CSS classes.
To make this more general-purpose I could define a Java interface that can be implemented to 
scape specific pages