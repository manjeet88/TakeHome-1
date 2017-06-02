# TakeHome
Oracle Cloud Storage - Take Home Exercise

## Problem
The prime use-case of object store is back-up and archive which means that clients want to upload large amounts 
of data (think GB, TB). The goal is to optimize the upload and download of these objects by parallelization. 
Specifically,

* We want to provide a client side API which will chunk a given file into segments and upload/download them in parallel.
* Client should be oblivious to the fact and it should all happen under the covers (i.e. implementation detail). The client interface should be as simple as:
    * void putObject(ObjectKey key, Stream inputData)
    * Stream getObject(ObjectKey key)
* The non-functional requirements you should consider in your design are
    * Performance – It needs tobe fast using multiple threads but not overwhelm system resources at the same time.
    * Fault tolerant – It should handle failure modes like transient faults(think network timeouts), suspend/resume, abort/partial uploads etc.
    * Data integrity – The final state of the object after split should be consistent with the original source. Consider using MD5 or other checksum to guarantee integrity on upload and download.
    * Concurrency – There could be multiple writers trying to write/read the same object. Your design needs to handle that. Last write wins semantics is acceptable for this exercise.
* Bottom-line: Data integrity is paramount and should not be compromised in light of failures, parallelism and/or concurrency. Client should get back what he stored.
* This has to be a working app which uses the Oracle Cloud Storage and Java SDK.

## Implementation
So what this program does do is very simple, it chunks a provided file using a caller specified concurrence and chunk size.

This program creates separate objects stored (object size is based on the file size divided by the chunk size) in the Oracle Cloud and will re-assembly (TODO) them into the resulting file. Which isn't what the non-functional requirement:

    * Concurrency – There could be multiple writers trying to write/read the same object. Your design needs to handle that. Last write wins semantics is acceptable for this exercise.

But it would be easily adapted to meet this requirement. The design chooses to break the file into chunks, so that the data integrity will be consistent. The last write wins suggestion seems short sided.

### Clone

```
git clone https://github.com/paul-tinius/TakeHome.git
```
### Build

```
./gradlew clean build fatJar
```

### Run

```
export CLOUD_USERNAME=<username>
export CLOUD_PWD=<user's passwd>
export CLOUD_DOMAIN=<service-domain>
```

```
java -jar build/libs/take-home-all-0.0.1-SNAPSHOT.jar
```

### License
Copyright :copyright: 2017 Paul E. Tinius


This library is licensed under the Apache License, Version 2.0.

See http://www.apache.org/licenses/LICENSE-2.0.html or the LICENSE file in this repository for the full license text.
