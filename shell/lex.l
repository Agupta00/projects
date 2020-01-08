%{
# define YY_DECL char ***yylex(int *flag, char **fileName, int* _pipeCount)
char **_args;
char*** individualArgument;


%}

%x ioFile
%option noinput
%option nounput


WORD [a-zA-Z0-9\/\.-]+
SPECIAL [()><|&;*]
%%
	int _numargs = 10;
	int _argcount = 0;
	int numPipes=10;
	int pipeCount = 0;
	int fileCount =0;
	_args= (char **)malloc(_numargs*sizeof(char**));
    _argcount = 0; _args[0] = NULL;
	individualArgument =(char ***) malloc(numPipes*sizeof(char***));

  
">>"+" " {
   flag[1]=3; fileCount=1; BEGIN(ioFile);
  }
  
">"+" " {
   flag[1]=2; fileCount=1 ;BEGIN(ioFile);

  }  

\<+" " {
   flag[0]=1; fileCount=0; BEGIN(ioFile);

  }

<ioFile>{WORD} {
     *(fileName+fileCount) = (char *)strdup(yytext);
     //printf("_%s\n",*(fileName+fileCount));
     BEGIN(INITIAL);
   }    

"|" {
	if((pipeCount-1) == numPipes){
		//reallocate memory if reached end of space in individualArgument
		individualArgument= (char ***)realloc(individualArgument,numPipes*2*sizeof(char***));
		numPipes = numPipes*2;
	}
	_args[_argcount] = NULL;
	individualArgument[pipeCount++]= _args;
	//create new _args and reset num args count
	_args= (char **)malloc(_numargs*sizeof(char**));
	_argcount = 0;
	_args[0] = NULL;
	_numargs=10;
   }       

[;]     {    
		//tells shell that on its next call to yylex there will already be text to parse
		*_pipeCount=pipeCount;
		_args[_argcount] = NULL;
		individualArgument[pipeCount]= _args; 
		//printf("%x\n",_args[2] ); 
		return individualArgument; 
	}
	
{WORD}|{SPECIAL} {

    _args[_argcount++] = (char *)strdup(yytext);
    //printf("%d:%s\n",_argcount,_args[_argcount-1]);
    if(_argcount == _numargs){
		//reallocate memory if reached end of space in _args
		_args= (char **)realloc(_args,_numargs*2*sizeof(char**));
		_numargs=_numargs*2;
	}

  }     

       


\n     {    
		*_pipeCount=pipeCount;
		_args[_argcount] = NULL;
		individualArgument[pipeCount]= _args; 
		//printf("%x\n",_args[2] ); 
		return individualArgument; 

	}
[ \t]+ 
.   
%%

char ***get_line(int *flag, char **fileName, int* pipeCount) { return yylex(flag, fileName, pipeCount); } 
void destroy(){yylex_destroy();}


