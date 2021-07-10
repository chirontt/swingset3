package com.github.chirontt.graalvm.text.html;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.ImageView;

/**
 * Specialized ImageView class to handle loading of image from classpath resource
 * in GraalVM's native image.
 * All classpath resources in the native image are of the "resource" URL protocol
 * that the standard ImageView knows nothing about, so it would
 * fail to load images from those URLs.
 */
public class ResourceImageView extends ImageView {

    public ResourceImageView(Element elem) {
		super(elem);
	}

    @Override
    public URL getImageURL() {
        String src = (String)getElement().getAttributes().
                             getAttribute(HTML.Attribute.SRC);
        if (src == null) {
            return null;
        }

        URL reference = ((HTMLDocument)getDocument()).getBase();
        try {
            URL u = new URL(reference, src);
            if ("resource".equals(u.getProtocol())) {
                //image comes from classpath resource in GraalVM's native image
            	u = getClass().getResource("/" + u.getPath());
            }
            return u;
        } catch (MalformedURLException e) {
            return null;
        }
    }

}
