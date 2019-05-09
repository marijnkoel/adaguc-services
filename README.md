# adaguc-services

[![Build Status](https://api.travis-ci.org/KNMI/adaguc-services.svg?branch=master)](https://travis-ci.org/KNMI/adaguc-services)
[![Download](https://jitpack.io/v/KNMI/adaguc-services.svg)](https://jitpack.io/#KNMI/adaguc-services)


Services for adaguc-server and pywps

For setting up development environment:

1) Download and install spring tool suite (https://spring.io/tools/sts/all)
2) Download lombok.jar (https://projectlombok.org/download.html)
3) Install lombok into spring tool suite with java -jar lombok.jar
3) Start STS and import this project as existing project
4) Press alt F5 to update Maven
5) In STS, select Run as java application
6) Select AdagucServicesApplication
7) To adjust the server port, set in Run Configuration the argument like this: --server.port=8090
8) Copy pre-commit to ./git/hooks to enable automatic unit testing on new commits.

For creating a new package:

1) Adjust the version in pom.xml: 0.<sprint number>.version (this is named ${VERSION} from now on)
2) Type mvn package
3) in directory target the file ./target/demo-${VERSION}-SNAPSHOT.jar is created.
4) You can for example start this with java -jar demo-${VERSION}-SNAPSHOT.jar


# Versions

1.1.0 - Uses spring boot 2.0

# Docker

To build adaguc-services with docker do:
```
docker build -t adaguc-services .
```

To run the docker container with your own configuration file do:

You need to specify the configuration file usin the ADAGUC_SERVICES_CONFIG environment variable.
```
mkdir ./myconfig
cp ./Docker/adaguc-services-config.xml ./myconfig
```

 Extend with the following
 ```
  <esgfsearch>
    <enabled>true</enabled>
    <cachelocation>/tmp/esgfsearch</cachelocation>
    <searchurl>https://esg-dn1.nsc.liu.se/esg-search/search?</searchurl>
  </esgfsearch>
  ```
```
docker run -it -p 8080:8080 -e EXTERNALADDRESS="localhost" -e ADAGUC_SERVICES_CONFIG="/config/adaguc-services-config.xml" -v `pwd`/myconfig:/config adaguc-services
```

http://localhost:8080/adaguc-services/esgfsearch/search?service=search&request=getfacets&query=clear&facet=project