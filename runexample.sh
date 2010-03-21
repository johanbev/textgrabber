#! /bin/bash

CLASS_PATH=$CLASS_PATH:build/:jars/apache-pdfbox-0.8.0-incubator-dev.jar:jars/fontbox-0.8.0-incubating.jar:jars/jdom.jar:jars/log4j-1.2.15.jar:jars/bcmail-jdk16-143.jar:jars/bcprov-jdk16-143.jar
java -cp $CLASS_PATH no/uio/ifi/nora/examples/SingleExtractor
