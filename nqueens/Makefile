#------------------------------------------------------------------------------
# A Makefile that sets up Chessboard.java, which also sources Point.java
# Akul Gupta, ID:168664
#------------------------------------------------------------------------------
MAINCLASS  = ChessBoard
JAVASRC    = $(MAINCLASS).java Point.java
SOURCES    = README Makefile $(JAVASRC)
CLASSES    = $(MAINCLASS).class Point.class
JARFILE    = NQueens.jar

all: $(JARFILE)

$(JARFILE): $(CLASSES)
	echo Main-class: $(MAINCLASS) > Manifest
	jar cvfm $(JARFILE) Manifest $(CLASSES)
	rm Manifest

$(CLASSES): $(JAVASRC)
	javac -Xlint $(JAVASRC)

clean:
	rm $(CLASSES) $(JARFILE)

