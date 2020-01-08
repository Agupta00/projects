// -- This program was completed using pair programming by
// -- (akul gupta, agupta60@ucsc) and
// -- (himanshu gautam, hgautam@ucsc.edu).
// -- I acknowledge that each partner in a programming pair should "drive"
// -- roughly 50% of the time the pair is working together, and at most 25%
// -- of an individual's effort for an assignment should be spent working
// -- alone. Any work done by a solitary programmer must be reviewed by the
// -- partner. The object is to work together, learning from each other, not
// -- to divide the work into two pieces with each partner working on a
// -- different piece.
// -- We are both submitting the same program.

//source: https://gist.github.com/YordanGeorgiev/c79e8875991c63a90a1e28ba5fe19435
//https://stackoverflow.com/questions/19704787/scala-check-if-string-contains-no-special-chars
import scala.collection.mutable.Map
import scala.io.Source
import scala.util.Try
import scala.io.StdIn




abstract class Expr
case class Var(name: String) extends Expr
case class Str(name: String) extends Expr
case class Constant(num: Double) extends Expr
case class BinOp(operator: String, left: Expr, right: Expr) extends Expr
// case class ListExpr(list: List[Expr]) extends Expr

abstract class Stmt
case class Let(variable: String, expr: Expr) extends Stmt
case class If(expr: Expr, label: String) extends Stmt
case class Label(label: String, stmt: Stmt) extends Stmt
case class Input(variable: String) extends Stmt
case class Print(exprList: List[Expr]) extends Stmt
case class Tmp() extends Stmt

object TLI {
	val ordinary=(('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')).toSet
	def isOrdinary(s:String)=s.forall(ordinary.contains(_))
	def isFloat(aString: String): Boolean = {Try(aString.toFloat).isSuccess}
	def isLabel(s:String)={
		if(s.last==':') {true} else {false}
	}
	def toInt(a: Boolean) = if(a) 1 else 0

    def eval(expr: Expr, symTab: Map[String, Double], lineNum: Double): Double = expr match {
        case BinOp("+",e1,e2) => eval(e1,symTab, lineNum) + eval(e2,symTab, lineNum) 
        case BinOp("+",e1,e2) => eval(e1,symTab, lineNum) + eval(e2,symTab, lineNum) 
		case BinOp("-",e1,e2) => eval(e1,symTab, lineNum) - eval(e2,symTab, lineNum) 
		case BinOp("*",e1,e2) => eval(e1,symTab, lineNum) * eval(e2,symTab, lineNum) 
		case BinOp("/",e1,e2) => eval(e1,symTab, lineNum) / eval(e2,symTab, lineNum) 
		case BinOp("<",e1,e2) => toInt(eval(e1,symTab, lineNum) < eval(e2,symTab, lineNum))
		case BinOp(">",e1,e2) => toInt(eval(e1,symTab, lineNum) > eval(e2,symTab, lineNum))
		case BinOp("<=",e1,e2) => toInt(eval(e1,symTab, lineNum) <= eval(e2,symTab, lineNum))
		case BinOp(">=",e1,e2) => toInt(eval(e1,symTab, lineNum) >= eval(e2,symTab, lineNum))
		case BinOp("==",e1,e2) => toInt(eval(e1,symTab, lineNum) == eval(e2,symTab, lineNum))
		case BinOp("!=",e1,e2) => toInt(eval(e1,symTab, lineNum) != eval(e2,symTab, lineNum))

        case Var(name) => symTab(name)
        case Constant(num) => num
		case _ => 
			println("Syntax error on line "+  lineNum.toInt.toString + ".")
			System.exit(1)
			0
    }
    
    def perform(stmt: Stmt, symTab: Map[String, Double], lineNum: Double): Double = stmt match {
   		case Let(name, exp)=>
   			symTab(name)=eval(exp, symTab,lineNum)
   			lineNum + 1
   		case If(expr, label)=>
   			var result = eval(expr, symTab,lineNum)
   			if(result==0){
   				lineNum+1
   			}else{
   				//return the linenum of the label
	   			try{
					symTab(label)
	   			}
	   			catch{
					case e : Throwable=>
	   				println("Illegal goto label at line "+lineNum.toString+".")
	   				System.exit(1)
	   				0
	   			}
   			}

   		case Input(name)=>
   			var read = StdIn.readLine()
   			if(isFloat(read)){
   				symTab(name)=read.toDouble
   				lineNum+1
   			}else{
   				//input has to be Double
   				println( "Illegal or missing input")
   				System.exit(1)
   				0
   			}
   			
   		case Print(list)=>
   			for(exp<-list){
   				try{
	   				exp match{
	   					//if string print string
	   					case Str(name)=> print(name + " ")
	   					//else eval and print number
	   					// case Var(name) => print(eval(exp, symTab) + " ")
						case _ => 
							print(eval(exp, symTab,lineNum) + " ")
   					}	
   				}catch{
					case e : Throwable=>
						exp match{
	   					//if string print string
	   					case Var(name)=> println(name)
	   						println("Undefined variable " +name+ " at line "+ lineNum.toString)
							System.exit(1)
							0	
   					}		
	   			}
   			}

   			println()
   			lineNum + 1

		//todo remove useless try catch
   		case Label(label,stmt)=>
   			try{
   				var lineNum_ = perform(stmt,symTab, lineNum)
   				lineNum_ 
   			}
   			catch{
				case e : Throwable=>
   				println("Illegal goto label at line "+lineNum.toString+".")
   				System.exit(1)
   				0
   			}
   		case _=>
   			println("error in perform")
   			0

    }	

    def build(stmt: Stmt, symTab: Map[String, Double], lineNum: Double): Double = stmt match {
    	case Label(name, exp)=>
   			symTab(name)=lineNum
   			lineNum+1
   		case _=>
   			0
    }

    //helper for parseExpr
    def type_(str: String):Expr = {
    	if(isFloat(str)){

    		var y = Constant(str.toFloat)
    		y
    	} else if (str.startsWith("\"")&&str.endsWith("\"")) {
    		//it is a string so remove the quotes
    		var name = str.substring(1, str.length()-1)
    		Str(name)
    	} else if(isOrdinary(str)){
    		Var(str)
    	} else {
    		println("error in type_"+str)
    		Var("null")
    	}
    }

    //e is the exprList that is passed in
    def parseExpr(expr: List[String]):Expr = {

    	expr match{
    		//one word in list
    		case x :: Nil => 
    			type_(x)

    		//3 words in list
    		case left :: op :: right :: Nil =>
    			var y = BinOp(op,type_(left), type_(right) )
    			y
    		case _ =>
    			Var("null")
    		
    	}

    }

   	//helper function to for multiprint 
    def parseString(list: List[String]):List[Expr] = {

    	//unword
    	var string = list.mkString(" ")
    	var commas = string.split(",").map(_.trim).toList
    	var listExpr = List[Expr]()

    	for(expr <- commas){
    		if( expr.startsWith("\"") && expr.endsWith("\"") ){
    			listExpr = listExpr:+ parseExpr(List(expr))
    		}else{
    			var words = expr.split(" ").toList
    			listExpr = listExpr:+ parseExpr(words)
    		}
    	}
    	listExpr
    }



    def parseStmt(in: List[String], lineNum:Int):Stmt = {
    	var first = in.head
    	var rest = in.tail

    	first match {
    		case "let" =>
    			var varName = rest(0)
    			var exprString = rest.drop(2)
    			Let(varName,parseExpr(exprString))
    		case "print" =>
    			//todo split string by ','gm
    			// var strings = rest.split(',')
    			var stmt = Print(parseString(rest))
    			stmt
    		case "if" =>
    			var label = rest.last
    			//removes the goto label
    			var exprString = rest.init.init
    			var stmt = If(parseExpr(exprString),label)
    			stmt
    		case "input" =>
    			Input(rest.head)
    		case _ if(isLabel(first)) =>
    			var stmt = parseStmt(rest, lineNum)
    			Label(first.init, stmt)
    		case _=>
    			println("Syntax error on line "+ lineNum.toString + ".")
    			System.exit(1)
    			Tmp()
    	}



    }

    def run(listStmt:List[Stmt], st:Map[String, Double]) = {
    	var lineNum = 1
    	while(lineNum!=listStmt.length+1){
			var index = lineNum - 1
			var stmt = listStmt(index)
			lineNum=perform(stmt,st,lineNum).toInt
    	}

    }

    // def parseLine(s: String):Array[String] {
    // 	lines = s.split
    // }args: Array[String]
    def main(args: Array[String]):Unit= {
    	if (args.length !=1) {
	        println("error no file provided to read")
	        System.exit(1)
	    }
		var listStmt=List[Stmt]()
	    var st = Map[String, Double]()
	    var lineNum = 1
	    val lines = Source.fromFile(args(0)).getLines().filter(!_.isEmpty())

	    for (line <- lines) {

		    val words = line.trim().split("\\s+").toList
		    var stmt = parseStmt(words,lineNum)
		    listStmt = listStmt:+stmt
		    build(stmt,st,lineNum)
		    lineNum+=1
	    }	
	    run(listStmt,st)

    }
}