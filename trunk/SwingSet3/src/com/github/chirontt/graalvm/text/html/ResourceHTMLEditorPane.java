package com.github.chirontt.graalvm.text.html;

import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLDocument;

/**
 * Specialized JEditorPane that knows how to load and parse HTML pages
 * from the classpath of a GraalVM's native image.
 * All classpath resources in native image are of the "resource" URL protocol
 * that the standard JEditorPane knows nothing about, so it would
 * load those HTML resources as just plain text by default.
 * This class will set a specialized HTMLEditorKit to the pane, to parse
 * the HTML pages properly.
 */
public class ResourceHTMLEditorPane extends JEditorPane {

    private static final long serialVersionUID = 1L;

    public ResourceHTMLEditorPane() {
        super();
        setEditorKit(new ResourceHTMLEditorKit());
    }

    public ResourceHTMLEditorPane(URL initialPage) throws IOException {
        this();
        setPage(initialPage);
    }

    public ResourceHTMLEditorPane(String url) throws IOException {
        this();
        setPage(url);
    }

    @Override
    public void setPage(URL page) throws IOException {
        if ("resource".equals(page.getProtocol())) {
            //page comes from classpath resource in GraalVM's native image
            page = getClass().getResource(page.getPath());
            super.setPage(page);
            HTMLDocument doc = (HTMLDocument) getDocument();
            if (doc.getBase() == null) {
                String urlForm = page.toExternalForm();
                String base = urlForm.substring(0, urlForm.lastIndexOf("/"));
                doc.setBase(new URL(base));
            }
        } else {
            super.setPage(page);
        }
    }

}
