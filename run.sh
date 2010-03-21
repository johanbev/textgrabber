#! /bin/bash
# This (skeleton) script provides an example of how to invoke DirTreeWalker.
# The variables INPUT_DIR OUTPUT_DIR and ERRORS must be specified!

INPUT_DIR=/logon/scratch/nora/pdf


OUTPUT_DIR=/logon/scratch/nora/pdf-out

ERRORS=""



CLASS_PATH=$CLASS_PATH:build/:jars/nora-pdfbox.jar:jars/fontbox-0.8.0-incubating.jar:jars/jdom.jar:jars/log4j-1.2.15.jar:jars/bcmail-jdk16-143.jar:jars/bcprov-jdk16-143.jar
CLASS_PATH=$CLASS_PATH:jars/icu4j-4_0_1-jar.

java -cp $CLASS_PATH no/uio/ifi/nora/dirtreewalker/DirTreeWalker $INPUT_DIR $OUTPUT_DIR $ERRORS
