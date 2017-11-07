### Releasing a Driver Library

The library module is releasable using the Novoda bintrya release plugin.
When released the library will be availble on jcenter with the following syntax:

`compileOnly 'userorg:groupid:artifactid:version'`

The command to release is:

`$ ./gradlew clean build bintrayUpload -PbintrayUser=BINTRAY_USERNAME -PbintrayKey=BINTRAY_KEY -PdryRun=false`

replacing the username and key with your details found in your profile on Bintray.
