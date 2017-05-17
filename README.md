# TakeHome
Oracle Cloud Storage - Take Home Exercise

## Overview
This is a exercise where a supplied file, based on the chuck size, is broken into smaller peices so they can be uploaded
in parallel, allowing for faster transfer rates.

This exercise has a default chunk size of 1M, but can easily be any size ( smaller or larger ). When transferring large
files it better to transfer them in large chunks and in parallel.

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
