# SwingSet3 + GraalVM native image

This project aims to produce platform-specific, native executable of the `SwingSet3` application
using the [GraalVM native-image](https://www.graalvm.org/reference-manual/native-image) utility.

Gradle and Maven build scripts are provided for building the project. The GraalVM version used
should be 21.1+ which supports compiling Swing application to native image.

## Build pre-requisites

The [GraalVM native-image](https://www.graalvm.org/reference-manual/native-image) page
shows how to set up GraalVM and its native-image utility for common platforms.
[Gluon](https://gluonhq.com/) also provides some setup [details](https://docs.gluonhq.com/#_platforms)
for GraalVM native-image creation.

The GraalVM native-image utility will use the configuration files in
`graal-cfg/<platform>/META-INF/native-image` folder to assist in the native image generation.

## Gradle build tasks

To run SwingSet3 in standard JVM with Gradle, execute the `run` task:

	gradlew run

To produce an executable uber jar, execute the `uberJar` task:

	gradlew uberJar

and the uber jar can then be run with the `java -jar` command:

	java -jar build/libs/SwingSet3-0.0.1-SNAPSHOT-no-deps-with-sources.jar

To produce a native executable, execute the `nativeImage` task:

	gradlew nativeImage

The `nativeImage` task would take a while to compile the application and link into an executable file.
The resulting `SwingSet3` executable file is:

	build/native-image-linux/SwingSet3

(or if building on a Windows machine, the executable file is:

	build\native-image-windows\SwingSet3.exe

)

which can then be run directly:

	./build/native-image-linux/SwingSet3

(or if building on a Windows machine:

	build\native-image-windows\SwingSet3.exe

)

## Maven build tasks

To compile and run SwingSet3 in standard JVM with Maven, execute the
`compile` then `exec:exec` tasks:

	mvnw compile
	mvnw exec:exec

To produce a native executable, execute the `package` task for specific platform
profile (e.g. for Linux):

	mvnw package -Pnative-linux

or if building on a Windows machine:

	mvnw package -Pnative-windows

The `package` task would take a while to compile the application and link into an executable file.
The resulting `SwingSet3` executable file is:

	target/native-image-linux/SwingSet3

(or if building on a Windows machine, the executable file is:

	target\native-image-windows\SwingSet3.exe

)

which can then be run directly:

	./target/native-image-linux/SwingSet3

(or if building on a Windows machine:

	target\native-image-windows\SwingSet3.exe

)

The above `package` task also produces an executable uber jar.
The uber jar can then be run with the `java -jar` command:

	java -jar target/swingset3-0.0.1-SNAPSHOT-no-deps-with-sources.jar

## Compressed executable

The resulting `SwingSet3` native executable, whether produced by Gradle or Maven build script,
can be further reduced in size via compression, using the [UPX](https://upx.github.io) utility,
as described [here](https://medium.com/graalvm/compressed-graalvm-native-images-4d233766a214).

As an example, the resulting `SwingSet3.exe` native application file produced in Windows is
normally 71MB in size, but is compressed to 21MB with the UPX command: `upx --best SwingSet3.exe`

## Caveats on Native Image

On Linux platform, such as Ubuntu, the resultant native executable is a single file, `SwingSet3`,
of about 83MB in size. Running the `ldd` command on it will show all the dynamic libraries (`.so`)
from standard library paths required by the executable at runtime:

	$ ldd build/native-image-linux/SwingSet3
	    linux-vdso.so.1 (0x00007ffd23575000)
	    libX11.so.6 => /lib/x86_64-linux-gnu/libX11.so.6 (0x00007fd5d2fd8000)
	    libXrender.so.1 => /lib/x86_64-linux-gnu/libXrender.so.1 (0x00007fd5d2dce000)
	    libXext.so.6 => /lib/x86_64-linux-gnu/libXext.so.6 (0x00007fd5d2db9000)
	    libXi.so.6 => /lib/x86_64-linux-gnu/libXi.so.6 (0x00007fd5d2da7000)
	    libstdc++.so.6 => /lib/x86_64-linux-gnu/libstdc++.so.6 (0x00007fd5d2bc5000)
	    libm.so.6 => /lib/x86_64-linux-gnu/libm.so.6 (0x00007fd5d2a76000)
	    libfreetype.so.6 => /lib/x86_64-linux-gnu/libfreetype.so.6 (0x00007fd5d29b5000)
	    libz.so.1 => /lib/x86_64-linux-gnu/libz.so.1 (0x00007fd5d2999000)
	    libpthread.so.0 => /lib/x86_64-linux-gnu/libpthread.so.0 (0x00007fd5d2976000)
	    libdl.so.2 => /lib/x86_64-linux-gnu/libdl.so.2 (0x00007fd5d2970000)
	    libgcc_s.so.1 => /lib/x86_64-linux-gnu/libgcc_s.so.1 (0x00007fd5d2955000)
	    libc.so.6 => /lib/x86_64-linux-gnu/libc.so.6 (0x00007fd5d2763000)
	    libxcb.so.1 => /lib/x86_64-linux-gnu/libxcb.so.1 (0x00007fd5d2737000)
	    /lib64/ld-linux-x86-64.so.2 (0x00007fd5d810b000)
	    libpng16.so.16 => /lib/x86_64-linux-gnu/libpng16.so.16 (0x00007fd5d26ff000)
	    libXau.so.6 => /lib/x86_64-linux-gnu/libXau.so.6 (0x00007fd5d26f9000)
	    libXdmcp.so.6 => /lib/x86_64-linux-gnu/libXdmcp.so.6 (0x00007fd5d26f1000)
	    libbsd.so.0 => /lib/x86_64-linux-gnu/libbsd.so.0 (0x00007fd5d26d7000)

On Windows 10 platform, the resultant native image consists of multiple files: the executable `SwingSet3.exe`,
plus a set of `.dll` files, and some `font` config files from the JDK are also included.
Here are their structure after a Gradle (or Maven) native-image build:

	build\ (or target\)
	    native-image-windows\
	        lib\
	            fontconfig.bfc
	            fontconfig.properties.src
	        awt.dll
	        fontmanager.dll
	        freetype.dll
	        harfbuzz.dll
	        java.dll
	        javaaccessbridge.dll
	        javajpeg.dll
	        jawt.dll
	        jsound.dll
	        jvm.dll
	        lcms.dll
	        sunmscapi.dll
	        SwingSet3.exe

All files in the *native-image-windows* folder (including the *lib* sub-folder) must stay together
in the same structure, with names unchanged after the build, so that the `SwingSet3.exe` native executable
can be run successfully in Windows.

