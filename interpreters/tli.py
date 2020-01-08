#! /usr/bin/env python3

# -- This program was completed using pair programming by
# -- (akul gupta, agupta60@ucsc) and
# -- (himanshu gautam, hgautam@ucsc.edu).
# -- I acknowledge that each partner in a programming pair should "drive"
# -- roughly 50% of the time the pair is working together, and at most 25%
# -- of an individual's effort for an assignment should be spent working
# -- alone. Any work done by a solitary programmer must be reviewed by the
# -- partner. The object is to work together, learning from each other, not
# -- to divide the work into two pieces with each partner working on a
# -- different piece.
# -- We are both submitting the same program.


import fileinput
import sys

# used to store a parsed TL expressions which are
# constant numbers, constant strings, variable names, and binary expressions
class Expr :
    def __init__(self,op1,operator,op2=None):
        self.op1 = op1
        self.operator = operator
        self.op2 = op2

    def __str__(self):
        if self.op2 == None:
            return self.operator + " " + self.op1
        else:
            # pdb.set_trace()
            # return self.op1 + " " + self.operator + " " +  self.op2
            return str(self.op1) + " " + self.operator + " " +  str(self.op2)

    # evaluate this expression given the environment of the symTable
    def eval(self, symTable,lineNum=0):

        if self.operator == "var":
            return symTable[self.op1]
        elif self.operator == "const":
            return float(self.op1)
        elif self.operator == "string":
            #removes quotes from the string
            self.op1=self.op1.replace('"','')
            return self.op1 
        elif self.operator == "+":
            #eval both expressions and sum result of each call
            return self.op1.eval(symTable) + self.op2.eval(symTable)
        elif self.operator == "-":
            #eval both expressions and sum result of each call
            return self.op1.eval(symTable) - self.op2.eval(symTable)    
        elif self.operator == "/":
            #eval both expressions and sum result of each call
            return self.op1.eval(symTable) / self.op2.eval(symTable)    
        elif self.operator == "*":
            #eval both expressions and sum result of each call
            return self.op1.eval(symTable) * self.op2.eval(symTable)    
        elif self.operator == "<":
            #eval both expressions and sum result of each call
            return int(self.op1.eval(symTable) < self.op2.eval(symTable))    
        elif self.operator == ">":
            #eval both expressions and sum result of each call
            return int(self.op1.eval(symTable) > self.op2.eval(symTable))    
        elif self.operator == "<=":
            #eval both expressions and sum result of each call
            return int(self.op1.eval(symTable) <= self.op2.eval(symTable))    
        elif self.operator == ">=":
            #eval both expressions and sum result of each call
            return int(self.op1.eval(symTable) >= self.op2.eval(symTable))    
        elif self.operator == "==":
            #eval both expressions and sum result of each call
            return int(self.op1.eval(symTable) == self.op2.eval(symTable))    
        elif self.operator == "!=":
            #eval both expressions and sum result of each call
            return int(self.op1.eval(symTable) != self.op2.eval(symTable))    
        else:
            print ("Syntax error on line "+  str(lineNum) + ".")
            sys.exit()

# used to store a parsed TL statement
class Stmt :
    def __init__(self,keyword,exprs):
        self.keyword = keyword
        self.exprs = exprs

    def __str__(self):
        others = ""
        others = others + " " + str(self.exprs)
        return self.keyword + others

    # perform/execute this statement given the environment of the symTable and returns the lineNum to go to on the next run
    def perform(self, symTable, lineNum):
        if(self.keyword=="let"):
            symTable[self.varName]=self.exprs.eval(symTable,lineNum)
            return (lineNum+1)

        elif(self.keyword=="input"):
            symTable[self.varName]=float(input())
            return (lineNum+1)

        elif(self.keyword=="if"):
            result = self.exprs.eval(symTable,lineNum)
            if(result==0): 
                return (lineNum+1)
            else: 
                # return the linenumber assosiated with that label name
                return int(symTable[self.label])
        elif(self.keyword=="label"):
            try:
                lineNum = self.stmt2.perform(symTable,lineNum)
                return(lineNum)
            except:
                print("Illegal goto label at line "+ str(lineNum)+".")
                sys.exit()
        elif(self.keyword=="print"):
            #if it is a print stmt then exprs will be a list

            for exp in self.exprs:
                # print(type_(self.exprs))
                try:
                    print(exp.eval(symTable, lineNum), end =" ")
                except:
                    print("Undefined variable " +exp.op1+ " at line "+ str(lineNum))
                    sys.exit()
            #adds the newline
            print()    
            return (lineNum+1)


    #builds the ST by filling it with labels
    def build(self, symTable, lineNum):
        # breakpoint()
        if(self.keyword=="label"):
            #adds labelName, linenum to ST
            symTable[self.label]=lineNum


    # print ("Doing: " + str(self))

def isFloat(str):
    try:
        float(str)
        return True
    except:
        return False

def type_(e1):

    if(isFloat(e1)):
        return "const"
    elif(e1.startswith("\"") and e1.endswith("\"")):
        return "string"
    elif(e1.isalnum()):
        return "var"
    else:
        print("error can not understand type_:", e1)

# e is the exprString that is passed in, iue 
def parseExpr(e):

    #that means there is one expr
    # print(e)

    if(not isinstance(e, list) ):
        return Expr(e,type_(e))
    elif(len(e)==1):
        return Expr(e[0],type_(e[0]))
    #else there are 2 expressions pressent
    elif(len(e)==3): 
        return Expr(parseExpr(e[0]),e[1],parseExpr(e[2]) )
    else:
        print("error can not parse this expr", e)

#helper function to for multiprint 
def parseString(e):
    #unword
    string = ' '.join(e)
    #split on commas
    commas = string.split(' , ')


    listExpr = []
    for expr in commas:

        #check if quotes
        if(expr[0].startswith("\"") and expr[-1].endswith("\"") ):
            listExpr.append(parseExpr(expr))
        else:
            #if there are no quotes then spaces indicate different parts of the expression which need to be broken into words for parsingExpr
            # tempString=''.join(expr)
            words=expr.split(' ')
            listExpr.append(parseExpr(words))

    return listExpr


def parseStmt(words,lineNum):
    first = words[0]
    rest = words[1:]
    # print(first+"_____")
    if(first=="let"):
        varName = rest[0]
        if(rest[1]!="="): print("error, expected '=' sign after stmt 'let' ")
        #everything after the let x = ....
        exprString = rest[2:]
        stmt =  Stmt(first, parseExpr(exprString))
        #add varName field to the let expr
        stmt.varName = varName
        return stmt
    elif(first=="if"):
        label = rest[-1]
        #removes "goto label"
        exprString = rest[:-2]
        stmt =  Stmt(first, parseExpr(exprString))
        #add label as class parameter
        stmt.label=label
        return stmt

    elif(":" in first):
        #remove : character at the end
        first = first[:-1]
        stmt =  Stmt("label", None)
        #create label attr and a second stmt
        stmt.label = first
        stmt.stmt2 = parseStmt(rest,lineNum)
        return stmt

    elif(first=="print"):
        stmt = Stmt(first, parseString(rest))
        return stmt

    elif(first=="input"):
        stmt = Stmt("input","")

        stmt.varName = rest[0]
        return stmt
    else:
        print ("Syntax error on line "+  str(lineNum) + ".")
        sys.exit()


#takes str line -> a Stmt
def parseLine(line):
    # line = line.strip()
    # words = line.split(' ')
    words=line.split()
    return words


def run(Stmts, ST, lineNum):
    #the current stmt being performed
    while(lineNum!=len(Stmts)+1):
        index = lineNum-1
        stmt = Stmts[index]
        lineNum=stmt.perform(ST, lineNum)



def main():
    if(len(sys.argv)!=2):
        print("invalid command line args")
    fileName = sys.argv[1]
    #empty dictionary
    lineNum=0
    listStmt=[]
    ST = {}
    file =  open(fileName, "r") 
    file = file.read().splitlines()

    #parses and builds internal representation
    for i,line in enumerate(file):   
        lineNum=i+1

        words = parseLine(line) 

        # print("the words are"+ str(words))
        stmt = parseStmt(words, lineNum)

        #add line number to statement
        stmt.lineNum=lineNum 

        listStmt.append(stmt)
        # breakpoint()
        stmt.build(ST,lineNum)
    # for i in listStmt:
    #     print(i)


    run(listStmt,ST,1)


        

main()