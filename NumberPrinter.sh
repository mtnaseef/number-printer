#!/bin/sh
PROJECTDIR=`dirname $0`
JARFILE=$PROJECTDIR/target/number-printer-1.0-SNAPSHOT.jar

if [ ! -f "$JARFILE" ]; then
    echo $JARFILE not found. Building...
    # Build with standard tools in case mvn is not available.
    (cd $PROJECTDIR &&
    mkdir -p target/classes &&
    javac -sourcepath src/main/java -d target/classes src/main/java/com/example/exercise/numberprinter/NumberPrinter.java &&
    cp src/main/resources/number_translations.properties target/classes &&
    jar cfe target/number-printer-1.0-SNAPSHOT.jar com.example.exercise.numberprinter.NumberPrinter -C target/classes .
    )
fi

java -jar $JARFILE "$@"
