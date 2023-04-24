# Use plugin native image builder.
```xml
           <plugin>
                <groupId>org.graalvm.buildtools</groupId>
                <artifactId>native-maven-plugin</artifactId>
                <version>${graal.plugin.version}</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>build</goal>
                        </goals>
                        <phase>package</phase>
                    </execution>
                </executions>
                <configuration>
                    <imageName>${project.name}</imageName>
                    <mainClass>org.example.Main</mainClass>
                    <buildArgs>-H:+PrintClassInitialization -H:+ReportExceptionStackTraces</buildArgs>
                </configuration>
            </plugin>
```

# Add the build args mentioning what needs to initialized at build time and what needs to be initialized at run time.
```shell
Args =\
--enable-http \
--enable-https \
--allow-incomplete-classpath \
-H:EnableURLProtocols=http,https \
--report-unsupported-elements-at-runtime \
-H:ReflectionConfigurationResources=${.}/reflect-config.json \
-H:JNIConfigurationResources=${.}/jni-config.json \
-H:ResourceConfigurationResources=${.}/resource-config.json \
--initialize-at-run-time=io.netty.handler.codec.compression.ZstdOptions,io.netty.handler.ssl.BouncyCastleAlpnSslUtils \
--initialize-at-build-time=io.netty.util.ResourceLeakDetector,org.slf4j.LoggerFactory \
--trace-object-instantiation=org.slf4j.LoggerFactory \
--trace-class-initialization=org.slf4j.LoggerFactory
```
Name of this file and folder should be like
```shell
resources/META-INF/native-image/{group-id}/{artifact-id}/native-image.propertis
```

# If some of the dependecies failing to initialize we can generate the reflection details for the native images by running the application using below options
```shell
java -agentlib:native-image-agent=config-output-dir=<DIRECTORY_YOU_WANT_THE_FILES_TO_BE_GERERATED_AT> -jar <JAR_FILE>.jar
```
then use these files to build the native image as shown below
```shell
native-image -jar <JAR_FILE>.jar <NATIVE_IMAGE_NAME> -H:ConfigurationFileDirectories=<DIRECTORY_WHERE_YOUR_JSON_FILES_ARE_AT>
```


# Never use Epsilon GC
The Epsilon GC (available with GraalVM 21.2 or later) is a no-op garbage collector that does not do any garbage collection and therefore never frees any allocated memory. The primary use case for this GC are very short running applications that only allocate a small amount of memory. To enable the Epsilon GC, specify the option --gc=epsilon at image build time.
