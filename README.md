language-detection
==================

Forked from Norconex's [mavenised version](https://github.com/Norconex/language-detection) of Shuyo's ["language-detection" project](https://github.com/shuyo/language-detection/blob/wiki/ProjectHome.md) 

#### Changes:

##### v2.0.0 (2021-02-01)
 - Modified to allow multiple language profiles to be configured within the same JVM.
 - Moved to v2.0.0 as `DetectorFactory` API is not back-compat

##### v1.3.0 (2014-11-25):
- Fork of release 2014-03-03 from original language-detection project
  (from the more up-to-date "master" branch). Original project hosted at:
  https://code.google.com/p/language-detection/
- Language profile files are now in classpath, bundled with the distributed jar.
- All generated files were removed from the project.
- Mavenized (changing directory structure, adding pom.xml, etc).
- Test classes moved to /src/main/test/.
- Added DetectorFactory#loadProfile(InputStream)
- Minor tweaks.

#### Maven Dependency:

```xml
<dependency>
  <groupId>uk.ac.susx.tag</groupId>
  <artifactId>langdetect</artifactId>
  <version>2.0.1</version>
</dependency>
```
