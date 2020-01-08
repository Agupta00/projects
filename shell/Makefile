CFLAGS=CFLAGS=-Wall -Werror -Wextra -pedantic -O3 -Wshadow

# Akul Gupta, ID:1686664

FLAGS   = -Wall -Werror -Wextra -pedantic -O3 -Wshadow
SOURCES = lex.yy.c myshell.c
OBJECTS = myshell.o lex.yy.o
EXEBIN  = myshell

all: $(EXEBIN)

$(EXEBIN) : $(OBJECTS)
	cc $(FLAGS) $(OBJECTS) -o $(EXEBIN) -lfl

$(OBJECTS) : $(SOURCES)
	cc $(FLAGS) -c $(SOURCES) 
lex.yy.c : lex.l
	flex lex.l 
clean :
	rm -f $(EXEBIN) $(OBJECTS) lex.yy.c

check : $(EXEBIN)
	valgrind --leak-check=full ./$(EXEBIN) myshell.c out.txt
