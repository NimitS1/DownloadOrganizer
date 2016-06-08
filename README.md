# DownloadOrganizer
A small utility to organize my downloads.

[![Build Status](https://travis-ci.org/NimitS1/DownloadOrganizer.svg?branch=master)](https://travis-ci.org/NimitS1/DownloadOrganizer)
<a href="https://scan.coverity.com/projects/nimits1-downloadorganizer">
  <img alt="Coverity Scan Build Status"
       src="https://scan.coverity.com/projects/9039/badge.svg"/>
</a>

### Building Download Organizer

1. Clone the repository to a new directory
2. Install maven
3. Go to the root folder of repository and run the following commands:

```bash
mvn clean
mvn package assembly:single
```
There should be two jars in  your target directory:
* DownloadOrganizer-0.0.1-SNAPSHOT.jar
* DownloadOrganizer-0.0.1-SNAPSHOT-jar-with-dependencies.jar

###Using Download Organizer
The jar file takes a yaml configuration file as an input.
The configuration file is used to mention the directories where files are downloaded by default and the desired location where files should be moved to.

**Sample YML file**
```yaml
downloadFolders:
 - C:\Users\snimit\Downloads\

fileTypeMap:
 txt: C:\Users\snimit\Downloads\documents
 doc: C:\Users\snimit\Downloads\documents
 docx: C:\Users\snimit\Downloads\documents
 ppt: C:\Users\snimit\Downloads\documents
 pptx: C:\Users\snimit\Downloads\documents
 zip: C:\Users\snimit\Downloads\zipped files
 rar: C:\Users\snimit\Downloads\zipped files
 jpg: C:\Users\snimit\Downloads\images
 png: C:\Users\snimit\Downloads\images
 mobi: C:\Users\snimit\Downloads\ebooks
 epub: C:\Users\snimit\Downloads\ebooks
 mp4: C:\Users\snimit\Downloads\media
 exe: C:\Users\snimit\Downloads\executables
 sh: C:\Users\snimit\Downloads\executables
 jar: C:\Users\snimit\Downloads\executables
 msi: C:\Users\snimit\Downloads\executables
 js: C:\Users\snimit\Downloads\scripts
 ```


The command to be executed:
``` bash
java -jar <location_of_target_directory>\DownloadOrganizer-0.0.1-SNAPSHOT-jar-with-dependencies.jar <Path_to_your_yml_file>
```
