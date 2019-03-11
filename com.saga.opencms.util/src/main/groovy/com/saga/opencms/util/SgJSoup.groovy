package com.saga.opencms.util

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class SgJSoup {

    /**
     * Conexion HTTP con la URL dada y extraccion del fragmento HTML con el selector dado.
     * @param url http://es.wikipedia.org/wiki/Wikipedia:Portada
     * @param selector #main-tfa
     * @return HMTL
     */
    String getHTMLfromURL(String url, String selector) {
        Document doc = Jsoup.connect(url).get();
        Elements newsHeadlines = doc.select(selector);
        String html = newsHeadlines.toString().trim()
                .replaceAll("\"", "\\\\\"")
                .replaceAll("\\n", "");
        return html
    }
}

