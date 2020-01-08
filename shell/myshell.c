#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <errno.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <signal.h>


extern char **get_line();
extern void destroy();


void execute_command(char** args);
void launch_my_function(int *index, char **args);
void launch_process(char** args);
void redirection();

int numFunctions = 2;
char *myFunctions[] = {"cd","exit"};

int flag[2];
char*** individualArguments=NULL;
char** arg1=NULL;
char *fileName[2]={NULL,NULL};
int mainid;
int processesCount =0;

void signalCatcher()
{
    //refrenced: https://www.geeksforgeeks.org/write-a-c-program-that-doesnt-terminate-when-ctrlc-is-pressed/
    //for signal catching
	signal(SIGINT, signalCatcher);
    if(mainid!=0){
    	exit(0);
    }
}

//fileName[0] is readin file name, fileName[1] is readout file name

int main() {
	mainid =0;

	while(1){
	    signal(SIGINT, signalCatcher);
		processesCount=0;
		int tempIn =dup(0);
		int tempOut=dup(1);
		int pipeCount =0;
		flag[0]=0;
		flag[1]=0;
		int* p;
		int j;

		printf(">");
		individualArguments = (char***) get_line(&flag, &fileName, &pipeCount);
		//first command

		//check if any redirection
		redirection();

		//at least one pipe
		if(pipeCount>0){
			p=(int *)malloc((pipeCount+1)*2*sizeof(int));

			//create the pipes
			for(j=0;j<=pipeCount; j++){
				pipe(&p[2*j]);
			}

			close(1);
			dup(p[2*processesCount +1]);
			close(p[1]);
			processesCount++;
		}

		execute_command(individualArguments[0]);



		//commands in the middle where there the command has an input from another pipe and
		//outputs to another pipe
		for(;processesCount<pipeCount;processesCount++){

			//input to the pipe is the 1st fd in the previous pipe call
			dup2(p[2*(processesCount-1)],0);

			//output of the pipe is 2nd fd of the current pipe call
			dup2(p[2*(processesCount)+1],1);

			close(p[2*(processesCount-1)]);
			close(p[2*(processesCount)+1]);

			launch_process(individualArguments[processesCount]);

		}

		//last command
		if(pipeCount>0){
			//reset output to std out of terminal
			if(close(1)==-1){
				perror("error closing");
			}
			dup(tempOut);
			//check if any redirection to output
			//printf("%x",flag[1]);
			redirection();
			close(0);
			dup(p[2*(processesCount-1)]);

			close(p[2*(processesCount-1)]);
			close(p[(2*processesCount-1)+1]);

			//launch the first processes
			launch_process(individualArguments[processesCount]);

			free(p);
			}

		//reset std in and out
		dup2(tempOut,1);
		dup2(tempIn,0);
		close(tempOut);
		close(tempIn);


		//free variables
		free(individualArguments);

		if(flag[0]!=0){
			free(fileName[0]);
		}
		if(flag[1]!=0){
			free(fileName[1]);
		}
	}

}

void freeArgumentMem(char** args){
	//frees memory of the command line argument words ie (ls, -l) and the pointers to those word
	int i;
	for (i = 0; args[i] != NULL; i++) {
		free(args[i]);
	}
	free(args);

}

void execute_command(char** args){

	int index;
	//check if no input
	if(args[0]==NULL){
		return;
	}

	for(index=0;index<numFunctions;index++){
		if(strcmp(args[0],myFunctions[index])==0){
			//function called is one of my functions
			launch_my_function(&index, args);
			(void) freeArgumentMem(args);
			return;
		}
	}
	//else it must be a execvp command
	launch_process(args);
}

void launch_my_function(int *index, char **args){

	switch(*index){
		case 0:
			//cd function
			if(chdir(args[1])==-1){
				perror("error");
			}
			free(individualArguments);
			break;
		case 1:
			//exit function
			(void) freeArgumentMem(args);
			free(individualArguments);
			destroy();
			exit(0);
		default:
			perror("error in my_functions");
			exit(0);
	}

}

void launch_process(char** args){
	int pid;
	int wait_status =-1;
	int exec_status;


	pid=fork();

	if(pid<0){
		perror("error in forking");
	}else if(pid==0){
		//child
		exec_status=execvp(args[0], args);
		if(exec_status==-1){
			(void) freeArgumentMem(args);
			perror("not a valid command:");
			exit(1);
		}
		(void) freeArgumentMem(args);
	    exit(0);

	}else{

		//parent processes
		while(wait_status<0){
			waitpid(pid,&wait_status, 0);
		}
		//free memory
		(void) freeArgumentMem(args);

		if(wait_status>0){
			//perror("error terminating");
			return;
		}
	}
}

void redirection(){
	int q[2]={-1,-1};
	switch(flag[0]){

		case 0:
			//no redirection
			break;
		case 1:
			//input read only
			q[0]=open(fileName[0],O_RDONLY, 0666);
			if(q[0]==-1){
				//error opening file
				perror("error opening file");
				//TEMPORTATy
				return;
			}
			close(0);
			dup(q[0]);
			close(q[0]);
			break;
		default:
			break;
	}

	switch(flag[1]){
			case 2:

				//output write only
				q[1]=open(fileName[1],O_CREAT | O_WRONLY|O_TRUNC, 0666);
				if(q[1]==-1){
					//error opening file
					perror("error writting file");
					//TEMPORTATy
					return;
				}
				close(1);

				dup(q[1]);
				close(q[1]);

				break;

			case 3:
				//append to write file
				q[1]=open(fileName[1],O_APPEND|O_WRONLY,0666);
				if(q[1]==-1){
					//error opening file
					perror("error append-writting file");
					//TEMPORTATy
					return;
				}
				close(1);
				dup(q[1]);
				close(q[1]);
				break;
			default:
				return;
	}

	return;
}
