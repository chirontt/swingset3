package com.github.chirontt.graalvm.swingset3;

import java.awt.EventQueue;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import com.sun.swingset3.SwingSet3;

public class NativeSwingSet3 {

    public static void main(String[] args) {
        //temp fix for GraalVM's native image for Windows:
        //AWT/Swing in Windows requires the "java.home" property be set
        String javaHome = System.getProperty("java.home");
        if (javaHome == null) {
            //set the "java.home" system property to the directory
            //where the native image resides (along with other .dll files)
            try {
                URI executablePath = NativeSwingSet3.class.getProtectionDomain().getCodeSource().getLocation().toURI();
                System.setProperty("java.home", (new File(executablePath)).getParent());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            //temp fix for non-support of splash screen image in GraalVM's native image,
            //by showing the splash image manually
            try {
                String splashScreenImage =
                        NativeImageUtils.getManifestValue(NativeSwingSet3.class.getName(), "SplashScreen-Image")
                                        .orElseThrow();
                SplashScreen splashScreen = new SplashScreen(splashScreenImage);
                splashScreen.splash();
                //launch the SwingSet3 program
                SwingSet3.main(args);
                //close the splash image
                EventQueue.invokeLater(() -> splashScreen.dispose());
            }
            catch (NoSuchElementException e) {
                //no 'SplashScreen-Image' attribute in the manifest;
                //launch the SwingSet3 program without any splash screen image
                SwingSet3.main(args);
            }
        } else {
            //launch SwingSet3 in standard JVM
            SwingSet3.main(args);
        }
    }

}
