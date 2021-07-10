package com.github.chirontt.graalvm.text.html;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Specialized HTMLEditorKit to handle the view creation of in-line IMG tag
 * whose SRC attribute pointing to classpath resource in GraalVM's native image.
 * All classpath resources in the native image are of the "resource" URL protocol
 * which need a specialized class to load the in-line IMG of that URL protocol
 * in the HTML page.
 */
public class ResourceHTMLEditorKit extends HTMLEditorKit {

    private static final long serialVersionUID = 1L;

    /** Shared factory for creating HTML Views. */
    private static final ViewFactory defaultFactory = new HTMLFactory();

    public ResourceHTMLEditorKit() {
    }

    @Override
    public ViewFactory getViewFactory() {
        return defaultFactory;
    }

    public static class HTMLFactory extends HTMLEditorKit.HTMLFactory {
    	
        @Override
        public View create(Element elem) {
            AttributeSet attrs = elem.getAttributes();
            Object elementName = attrs.getAttribute(AbstractDocument.ElementNameAttribute);
            Object o = (elementName != null) ? null : attrs.getAttribute(StyleConstants.NameAttribute);
            if (o instanceof HTML.Tag) {
                HTML.Tag kind = (HTML.Tag) o;
                if (kind == HTML.Tag.IMG) {
                    return new ResourceImageView(elem);
                }
            }
            return super.create(elem);
        }

    }

}
