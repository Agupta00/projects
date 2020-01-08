-- This program was completed using pair programming by
-- (akul gupta, agupta60@ucsc) and
-- (himanshu gautam, hgautam@ucsc.edu).
-- I acknowledge that each partner in a programming pair should "drive"
-- roughly 50% of the time the pair is working together, and at most 25%
-- of an individual's effort for an assignment should be spent working
-- alone. Any work done by a solitary programmer must be reviewed by the
-- partner. The object is to work together, learning from each other, not
-- to divide the work into two pieces with each partner working on a
-- different piece.
-- We are both submitting the same program.

import Data.Char
import System.IO
import Debug.Trace
import Data.List
import System.Environment   
import qualified Control.Exception as Exc

-- maps labels line numbers and variables to values - uses float for line numbers for simplicity
type SymTable = [(String,Float)]

data Expr = Constant Float | Var String | Quote String| Plus Expr Expr | Minus Expr Expr | LT_ Expr Expr | List [Expr] | GT_ Expr Expr | Times Expr Expr |
 Divide Expr Expr | LE_ Expr Expr | GE_ Expr Expr | EE_ Expr Expr | NE_ Expr Expr | ExprError String deriving (Show) 

-- data Either = Left Float | Right String deriving (Show)

data Stmt =
     Let String Expr |
     Print Expr | 
     Label String Stmt |
     Input String | 
     Error String |
     If Expr String deriving (Show) 
--             string represents label name



debug = flip trace


-- dummy predicate that is supposed to check if a string is a label which is a string ending with ":"
isLabel :: String -> Bool
isLabel x = if (':' `elem` x) then True else False
isLabel _ = False -- there are no labels in this nano version of the tiny language

isString :: String -> Bool
isString x = if(head x == '\"' && last x == '\"') then True else False

--removes quotes from String
removeQuotes :: String -> String
removeQuotes x = if(head x == '\"' && last x == '\"') then (init $ tail x) else x 


-- takes a list of tokens as strings and returns the parsed expression
parseExpr :: [String] -> Expr
parseExpr (e1:",":e2) =
    let (e1',e2') = (parseExpr [e1] ,parseExpr e2) in List $e1':(e2':[]) --`debug` (show e1')

parseExpr (e1:"+":e2:[]) = Plus (parseExpr [e1]) (parseExpr [e2])
parseExpr (e1:"*":e2:[]) = Times (parseExpr [e1]) (parseExpr [e2])
parseExpr (e1:"/":e2:[]) = Divide (parseExpr [e1]) (parseExpr [e2])

parseExpr (e1:"-":e2:[]) = Minus (parseExpr [e1]) (parseExpr [e2])
parseExpr (e1:"<":e2) = LT_ (parseExpr [e1]) (parseExpr e2) 
parseExpr (e1:">":e2:[]) = GT_ (parseExpr [e1]) (parseExpr [e2])
parseExpr (e1:"<=":e2:[]) = LE_ (parseExpr [e1]) (parseExpr [e2])
parseExpr (e1:">=":e2:[]) = GE_ (parseExpr [e1]) (parseExpr [e2])
parseExpr (e1:"==":e2:[]) = EE_ (parseExpr [e1]) (parseExpr [e2])
parseExpr (e1:"!=":e2:[]) = NE_ (parseExpr [e1]) (parseExpr [e2])

parseExpr [x] = if (isString x) then Quote (removeQuotes x) else if (isAlpha (head x)) then (Var x) else (Constant (read x)) --`debug` (show x)
parseExpr x = ExprError (head x) --`debug` (show x)



-- takes the first token which should be a keyword and a list of the remaining tokens and returns the parsed Stmt
parseStmt :: String -> [String] -> Stmt
parseStmt "let" (v:"=":expr) = Let v (parseExpr expr)
parseStmt "print" expr = Print (parseExpr expr) --`debug` (show $Print (parseExpr expr)) 
parseStmt "if" rest = 
    let (expr, labelName) = (splitAt (head $elemIndices "goto" rest) rest) in If (parseExpr expr) (last labelName) --`debug`(show labelName)
parseStmt "input" expr = Input "_"
parseStmt x y = Error $ "invalid stmt" ++ x --`debug` (show y)



parseLabel :: String -> [String] -> Stmt 
parseLabel first rest = Label (init first) (parseLine rest) 


-- takes a list of tokens and returns the parsed statement - the statement may include a leading label
parseLine :: [String] -> Stmt
parseLine [] = Error "empty line"
parseLine (first:rest) =
      if (isLabel first) then parseLabel first rest --`debug` (show rest)
      else parseStmt first rest --`debug` (show rest)

-- takes a variable name and a ST and returns the value of that variable or zero if the variable is not in the ST
lookupVar :: String -> SymTable -> Float
lookupVar name [] = error $ "Undefined variable " ++ name 
lookupVar name ((id,v):rest) = if (id == name) then v --`debug` (show $id ++ "-at- lookingn for -" ++ name)
    else lookupVar name rest 




getFloat (Left x) = x
getString (Right x) = removeQuotes x --`debug` show x
getString (Left x) = show x


eval :: Expr ->SymTable -> Either Float String
eval (Var v) env = Left $ lookupVar v env
eval (Constant v) _ = Left v
eval (Plus e1 e2) env = Left $ (getFloat (eval e1 env)) + (getFloat (eval e2 env))
eval (Times e1 e2) env = Left $ (getFloat (eval e1 env)) * (getFloat (eval e2 env))
eval (Divide e1 e2) env = Left $ (getFloat (eval e1 env)) / (getFloat (eval e2 env))


eval (Minus e1 e2) env = Left $ (getFloat (eval e1 env)) - (getFloat (eval e2 env))
eval (LT_ e1 e2) env = if (getFloat (eval e1 env)) < (getFloat (eval e2 env)) then Left 1 
                        else Left 0 

eval (GT_ e1 e2) env = if (getFloat (eval e1 env)) > (getFloat (eval e2 env)) then Left 1 
                        else Left 0 
eval (LE_ e1 e2) env = if (getFloat (eval e1 env)) <= (getFloat (eval e2 env)) then Left 1 
                        else Left 0 
eval (GE_ e1 e2) env = if (getFloat (eval e1 env)) >= (getFloat (eval e2 env)) then Left 1 
                        else Left 0 
eval (EE_ e1 e2) env = if (getFloat (eval e1 env)) == (getFloat (eval e2 env)) then Left 1 
                        else Left 0 
eval (NE_ e1 e2) env = if (getFloat ((eval (EE_ e1 e2) env))==0) then Left 1 else Left 0

eval (Quote v) _ = Right v
eval (ExprError v) _ = Right v

eval x _ = error "can not eval" `debug` show x


-- given a statement, a ST, line number, input and previous output, return an updated ST, input, output, and line number
-- this starter version ignores the input and line number
-- Stmt, SymTable, progCounter, input, output, (SymTable', input', output', progCounter)
perform:: Stmt -> SymTable -> Float -> [String] ->String -> (SymTable, [String], String, Float)
perform (Print (List [])) env lineNum input output = error "can not print nothing.. stupid" --`debug` (show $ eval e env)

--DUID THIS QUICK REVIEW
perform (Print (List [e])) env lineNum input output = (env, input, output++(getString(eval e env)++"\n"), lineNum+1)--`debug` (show $ eval e env)
perform (Print (List (e:rest))) env lineNum input output = --`debug` (show $ eval e env)
    let (_, _, output', lineNum') = perform (Print (List rest)) env lineNum input (output++(getString (eval  e env))++ " ") in (env, input, output', lineNum') --`debug` (show e)
perform (Print e) env lineNum input output = (env, input, output++(getString (eval e env)++"\n"), lineNum+1)--`debug` (show $ eval e env)

perform (Let id e) env lineNum input output = ((id, getFloat (eval e env)):env, input, output, lineNum+1) --`debug` (show env)
perform (Input id) env lineNum input output = (env, input, output, lineNum+1) --`debug` (show env)

perform (Label id stmt) env lineNum input output = 
    let (env', input', output', _) = (perform stmt env lineNum input output) in ((id,lineNum):env', input', output', lineNum+1) --`debug` (show (id,lineNum))

perform (If e labelName) env lineNum input output = (env, input, output, if (getFloat (eval e env)== 0) then lineNum+1
    else lookupVar labelName env) 

perform (Error e) env lineNum input output = error $ "Error on line " ++ (show lineNum) ++"\n" ++ e 
-- (env, input, output++(getString (eval e env)++"\n"), lineNum+1) --`debug` (show $ eval e env)

-- go to lineNum = the value of labelName stored in the ST


-- given a list of Stmts, a ST, and current output, and lineNum perform all of the statements in the list and return the updated output String
run :: [Stmt] -> SymTable -> String -> Float -> String
-- run [] _ output lineNum = output
-- run (curr:rest) env output = 
--     let (env1, _, output1, _) = perform curr env 1 [] output in run rest env1 output1
-- whole is the whole Stmts list, index is the line at that index
run whole env output lineNum 
    | ((round lineNum) == (length whole)) = output 
    | otherwise = let (env1, _, output1, lineNum') = perform index env lineNum [] output in run whole env1 output1 lineNum' --`debug` (show lineNum)
    where index = whole!!(round lineNum)


performTest:: Stmt -> SymTable -> Float -> [String] ->String -> (SymTable, [String], String, Float)
performTest (Label id stmt) env lineNum input output = 
    let (env', input', output', _) = (performTest stmt env lineNum input output) in ((id,lineNum):env', input', output', lineNum+1) --`debug` (show (id,lineNum))

--do nothing for other Stmts
performTest _ env lineNum input output = (env, input, output, lineNum+1) --`debug` (show "here in nop in preformTest")

runTest :: [Stmt] -> SymTable ->Float -> (SymTable, Float)
runTest [] st lineNum = (st,lineNum)
runTest (curr:rest) env lineNum= 
    let (env1, _, _, _) = performTest curr env lineNum [] "" in runTest rest env1 (lineNum+1)

-- given list of list of tokens, a ST, return the list of parsed Stmts and ST storing mapping of labels to line numbers
parseTest :: [[String]] -> SymTable -> ([Stmt], SymTable)
parseTest []  st = ([], st)
parseTest x  st = let (stmt, (st', lineNum)) = (map parseLine x, runTest (map parseLine x) st 0) in (stmt, st')

-- needs completing for partial credit

--if there is an input stmt it will return the user input and name of that input variable 

--input St and list of lines returns io(St)
getInput :: SymTable -> [[String]] -> IO(SymTable)
getInput st (x:rest) = do

    if (not $ null x) then do
        if (head x == "input") then do
            value <- getLine
            let st' = (last x, read value :: Float)
            getInput (st':st) rest
        else
            getInput st rest
    else
        return(st)
getInput st y = return(st) --`debug` show y


main = do  
    args <- getArgs  
    if (length args == 1) then do
 
        pfile <- openFile (head args) ReadMode
        contents <- hGetContents pfile
        let tempLines = map words (lines contents)
        --create pre st which has later labels and inputs
        let (x,st') = parseTest tempLines []
        st <- getInput st' tempLines

        --check for any errors
        -- parseLineTest tempLines 0

        putStr (run (map parseLine (map words (lines contents))) st "" 0)
        hClose pfile
    else error "invalid command line argument"





















