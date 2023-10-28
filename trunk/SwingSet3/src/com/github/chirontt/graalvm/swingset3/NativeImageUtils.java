package com.github.chirontt.graalvm.swingset3;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class NativeImageUtils {

    private NativeImageUtils() {
    }

    public static Optional<String> getManifestValue(String mainClass, String attributeName) {
        if (mainClass == null || mainClass.isEmpty()) return Optional.empty();
        if (attributeName == null || attributeName.isEmpty()) return Optional.empty();

        Package currentPackage = NativeImageUtils.class.getPackage();
        switch (attributeName.toLowerCase()) {
            case "implementation-title":
                return Optional.ofNullable(currentPackage.getImplementationTitle());
            case "implementation-vendor":
                return Optional.ofNullable(currentPackage.getImplementationVendor());
            case "implementation-version":
                return Optional.ofNullable(currentPackage.getImplementationVersion());
            case "specification-title":
                return Optional.ofNullable(currentPackage.getSpecificationTitle());
            case "specification-vendor":
                return Optional.ofNullable(currentPackage.getSpecificationVendor());
            case "specification-version":
                return Optional.ofNullable(currentPackage.getSpecificationVersion());
            default:
                ClassLoader loader = NativeImageUtils.class.getClassLoader();
                URL url = loader.getResource(mainClass.replace(".", "/") + ".class");
                if (url != null) {
                    String classURL = url.toString();
                    if (classURL.startsWith("jar:")) { //running in standard JVM
                        try (InputStream stream = new URL(classURL.substring(0, classURL.lastIndexOf('!') + 1) + '/' + JarFile.MANIFEST_NAME).openStream()) {
                            return Optional.ofNullable(new Manifest(stream).getMainAttributes().getValue(attributeName));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (classURL.startsWith("resource:")) { //running with GraalVM's generated native image
                        try {
                            //find a MANIFEST.MF resource containing the specified Main-Class entry
                            for (Enumeration<URL> e = loader.getResources(JarFile.MANIFEST_NAME); e.hasMoreElements(); ) {
                                url = e.nextElement();
                                try (InputStream stream = url.openStream()) {
                                    Attributes attributes = new Manifest(stream).getMainAttributes();
                                    //does this manifest resource contain the specified Main-Class entry?
                                    if (mainClass.equals(attributes.getValue("Main-Class"))) {
                                        return Optional.ofNullable(attributes.getValue(attributeName));
                                    }
                                }
                            }
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                }

                return Optional.empty();
        }
    }

}
