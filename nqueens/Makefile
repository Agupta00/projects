#------------------------------------------------------------------------------
# A Makefile that sets up Driver.java, which also sources Pair.java
# Akul Gupta, ID:168664
#------------------------------------------------------------------------------
MAINCLASS  = Driver
JAVASRC    = Driver.java Pair.java
SOURCES    = README Makefile $(JAVASRC)
CLASSES    = Driver.class Pair.class
JARFILE    = Bard.jar

all: $(JARFILE)

$(JARFILE): $(CLASSES)
	echo Main-class: $(MAINCLASS) > Manifest
	jar cvfm $(JARFILE) Manifest $(CLASSES)
	rm Manifest

$(CLASSES): $(JAVASRC)
	javac -Xlint $(JAVASRC)

clean:
	rm $(CLASSES) $(JARFILE)

