# SwingSet3 + GraalVM native image

[![Github Actions Build Status](https://github.com/chirontt/swingset3/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/chirontt/swingset3/actions/workflows/gradle-build.yml)
[![Github Actions Build Status](https://github.com/chirontt/swingset3/actions/workflows/maven-build.yml/badge.svg)](https://github.com/chirontt/swingset3/actions/workflows/maven-build.yml)

This project aims to produce platform-specific, native executable of the `SwingSet3` application
using the [GraalVM native-image](https://www.graalvm.org/reference-manual/native-image) utility.

Gradle and Maven build scripts are provided for building the project. The GraalVM version used
should be 21.1+ which supports compiling Swing application to native image.

## Build pre-requisites

The [GraalVM native-image](https://www.graalvm.org/reference-manual/native-image) page
shows how to set up GraalVM and its native-image utility for common platforms.
[Gluon](https://gluonhq.com/) also provides some setup [details](https://docs.gluonhq.com/#_platforms)
for GraalVM native-image creation.

Actually, for native compiling of a *Swing-based application* like this project, an implementation of 
GraalVM called the [Liberica Native Image Kit](https://bell-sw.com/pages/downloads/native-image-kit/)
can produce a *working* native executable better than the stock
[GraalVM-CE](https://github.com/graalvm/graalvm-ce-builds) software could, for some reason.
And this Liberica NIK software is used in the GitHub Actions
[build scripts](https://github.com/chirontt/swingset3/tree/master/.github/workflows) of this project
to successfully build and generate the relevant *working* native images for Linux and Windows.

The GraalVM native-image utility will use the configuration files in
`graal-cfg/<platform>/META-INF/native-image` folder to assist in the native image generation.

## Gradle build tasks

To run SwingSet3 in standard JVM with Gradle, execute the `run` task:

	gradlew run

To produce an executable uber jar, execute the `uberJar` task:

	gradlew uberJar

and the uber jar can then be run with the `java -jar` command:

	java -jar build/libs/SwingSet3-1.0-SNAPSHOT-no-deps-with-sources.jar

To produce a native executable, execute the `nativeCompile` task:

	gradlew nativeCompile

The `nativeCompile` task would take a while to compile the application and link into an executable file.
The resulting `SwingSet3` executable file is:

	build/native/nativeCompile/SwingSet3

(or if building on a Windows machine, the executable file is:

	build\native\nativeCompile\SwingSet3.exe

)

which can then be run directly:

	./build/native/nativeCompile/SwingSet3

(or if building on a Windows machine:

	build\native\nativeCompile\SwingSet3.exe

)

## Maven build tasks

To compile and run SwingSet3 in standard JVM with Maven, execute the
`compile` then `exec:exec` tasks:

	mvnw compile
	mvnw exec:exec

To produce a native executable, execute the `package` task:

	mvnw package

The `package` task would take a while to compile the application and link into an executable file.
The resulting `SwingSet3` executable file is:

	target/SwingSet3

(or if building on a Windows machine, the executable file is:

	target\SwingSet3.exe

)

which can then be run directly:

	./target/SwingSet3

(or if building on a Windows machine:

	target\SwingSet3.exe

)

The above `package` task also produces an executable uber jar.
The uber jar can then be run with the `java -jar` command:

	java -jar target/swingset3-1.0-SNAPSHOT-no-deps-with-sources.jar

## Caveats on Native Image

### Linux native executable

On Linux platform, such as Ubuntu, the resultant native image consists of multiple files: the executable
`SwingSet3`, plus a set of `.so` library files. Here are their structure after a Gradle (or Maven)
native-image build, in the `build/native/nativeCompile/` (for Gradle) or `target/` directory (for Maven):

	build/native/nativeCompile/ (or target/)
	    libawt.so
	    libawt_headless.so
	    libawt_xawt.so
	    libfontmanager.so
	    libfreetype.so
	    libjavajpeg.so
	    libjava.so
	    libjsound.so
	    libjvm.so
	    liblcms.so
	    SwingSet3

Many `.so` library files in the above *nativeCompile/* or *target/* folder are not needed at runtime
(at least not by this `SwingSet3` application) so they can be manually removed, resulting in the
final list of native executable files for Linux:

	build/native/nativeCompile/ (or target/)
	    libawt.so
	    libawt_xawt.so
	    libfontmanager.so
	    libjavajpeg.so
	    libjava.so
	    libjvm.so
	    SwingSet3

All these files in the *nativeCompile/* or *target/* folder must stay together
in the same structure, with names unchanged after the build, so that the `SwingSet3` native
executable can be run successfully in Linux.

Also, running the `ldd` command on the executable `SwingSet3` file will show all the dynamic libraries (`.so`)
from system library paths required by the executable at runtime:

	$ ldd build/native/nativeCompile/SwingSet3 (or $ ldd target/SwingSet3)
	    linux-vdso.so.1 (0x00007fffdf7ca000)
	    libz.so.1 => /lib/x86_64-linux-gnu/libz.so.1 (0x000077d226d86000)
	    libc.so.6 => /lib/x86_64-linux-gnu/libc.so.6 (0x000077d220e00000)
	    /lib64/ld-linux-x86-64.so.2 (0x000077d226dbe000)

### Windows native executable

On Windows 11 platform, the resultant native image consists of multiple files: the executable
`SwingSet3.exe`, plus a set of `.dll` files, and some `font` config files from the JDK are also
included. Here are their structure after a Gradle (or Maven) native-image build:

	build\native\nativeCompile\ (or target\)
	    lib\
	        fontconfig.bfc
	        fontconfig.properties.src
	    awt.dll
	    fontmanager.dll
	    freetype.dll
	    java.dll
	    javaaccessbridge.dll
	    javajpeg.dll
	    jawt.dll
	    jsound.dll
	    jvm.dll
	    lcms.dll
	    SwingSet3.exe

Many `.dll` files in the above *nativeCompile\\* or *target\\* folder are not needed at runtime
(at least not by this `SwingSet3` application) so they can be manually removed, resulting in the
final build folder structure for Windows:

	build\native\nativeCompile\ (or target\)
	    lib\
	        fontconfig.bfc
	        fontconfig.properties.src
	    awt.dll
	    fontmanager.dll
	    java.dll
	    jvm.dll
	    SwingSet3.exe

All files in the *nativeCompile\\* or *target\\* folder (including the *lib\\* sub-folder) must stay together
in the same structure, with names unchanged after the build, so that the `SwingSet3.exe` native
executable can be run successfully in Windows.

