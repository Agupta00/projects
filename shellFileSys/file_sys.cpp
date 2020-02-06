// $Id: file_sys.cpp,v 1.7 2019-07-09 14:05:44-07 - - $

#include <iostream>
#include <stdexcept>
#include <unordered_map>
#include <list>
#include <sstream> 
#include <deque>

using namespace std;

#include "debug.h"
#include "file_sys.h"
#include "util.h" // needed for `complain()`

int inode::next_inode_nr {1};

struct file_type_hash {
   size_t operator() (file_type type) const {
      return static_cast<size_t> (type);
   }
};

ostream& operator<< (ostream& out, file_type type) {
   static unordered_map<file_type,string,file_type_hash> hash {
      {file_type::PLAIN_TYPE, "PLAIN_TYPE"},
      {file_type::DIRECTORY_TYPE, "DIRECTORY_TYPE"},
   };
   return out << hash[type];
}

inode_state::inode_state() {
   inode_ptr rootNode = make_shared<inode>(file_type::DIRECTORY_TYPE);
   directory* dir = static_cast<directory*>(rootNode->contents.get());
   dir->dirents["."]=rootNode;
   root = rootNode;
   cwd = rootNode;

   DEBUGF ('i', "root = " << root << ", cwd = " << cwd
          << ", prompt = \"" << prompt() << "\"");
}

//common helper function to get a pointer to a directory 
//or return nullptr if it is invalid
//isDirectory = "" if searching for file else "/"
inode_ptr inode_state::getDir_helper(const inode_ptr& state,
                                     const    string& dirName,
                                     const      bool  isDirectory){

  
   //get the pointer to directory
    base_file* basePtr = state->contents.get();

    const directory& dir = static_cast<directory&>(*basePtr);
    const map<string,inode_ptr>& dirents= dir.dirents;

    for(auto elem: dirents){
      string str = elem.first;
      //todo change to dynamic type checking to check if directory
      //if(str==(dirName+"/") || ((str=="."||str=="..")&&str==dirName))
      // return elem.second;

      if(isDirectory){
        if(str==(dirName+"/")|| ((str=="."||str=="..")&&str==dirName)){
          return elem.second;
        }   
      }else{
        if (str==dirName)  return elem.second;

      }

      
    }
    //not found means error
    return nullptr;

}

inode_ptr
inode_state::getDir(const string& dirName, const bool isDirectory){
  inode_ptr state;
  //search from root or from cwd
  (dirName[0]=='/')? state=root:state=cwd;

  //split dirname by / character
  deque<string> path;
  istringstream s(dirName);
  string token;

  while(getline(s, token, '/')){
    if(token!="") path.push_back(token);
  } 

  while(!path.empty()){
    string temp=path.front();
    DEBUGF('z',":  "<<temp<<endl);  
    state=getDir_helper(state, path.front(), isDirectory);
    path.pop_front();
    //check for directory does not exist error
    if(!state) break;
  }

  return state;
}


// inode_ptr inode_state::getFile(const string& fileName){

   
//      //get the pointer to directory
//       base_file* basePtr = cwd->contents.get();

//       const directory& dir = static_cast<directory&>(*basePtr);
//       const map<string,inode_ptr>& dirents= dir.dirents;

//       for(auto elem: dirents){
//         string str = elem.first;
//         //todo change to dynamic type checking to check if directory
//         if (str==fileName)  return elem.second;
//       }
//       //not found means error
//       return nullptr;
// }

void inode_state::cat(const string& fileName){
  inode_ptr target = getDir(fileName, false);
   if (!target) {
       complain() << "cat: " << fileName;
       if (getDir(fileName, true)) {
           complain() << ": Is a directory" << endl;
       } else {
           complain() << ": No such file or directory" << endl;
       }
       return;
   }
   shared_ptr<plain_file> plain
       = dynamic_pointer_cast<plain_file>(target->contents);
  const wordvec& data = plain->readfile();
  
  for (string elem: data) cout<<elem<<" ";
  cout<<endl;
}

void inode_state::pwd(inode_ptr state){
  list<string> path;
  if (state==nullptr) state = cwd;
  string name;

  while(state!=root){

    base_file* basePtr = state->contents.get();

    directory& dir = static_cast<directory&>(*basePtr);
    map<string,inode_ptr> dirents_= dir.dirents;
    inode_ptr parent = dirents_[".."];

    //search for current state name in parents dirents
    directory& dir1
        = static_cast<directory&>(*(parent->contents.get()));
    map<string,inode_ptr> dirents1_= dir1.dirents;
    for(auto elem:dirents1_){
        if (elem.second==state) {
          name =elem.first;
          state=parent;
          break;
      }  
    }
    path.push_front(name);
  }
    //formating
    if (path.size()!=0) path.back().pop_back();
    path.push_front("/");

    for(string elem : path) cout<<elem;

}

void inode_state::ls(const inode_ptr& target){
  //pwd
    DEBUGF('h', target.use_count());

  pwd(target);
  cout<<':'<<endl;

  //get the pointer to directory
  base_file* basePtr = target->contents.get();

  const directory& dir = static_cast<directory&>(*basePtr);
        DEBUGF('h', "size"<<dir.dirents.size());

  const map<string,inode_ptr> dirents_= dir.dirents;
        DEBUGF('h', target.use_count());

  for(auto elem:dirents_){
    DEBUGF('h', elem.first);
    inode& node = *elem.second;
    DEBUGF('h', "node number"<<node.contents.get()->size());
    ios state(nullptr);
    state.copyfmt(cout);
    cout.width(5);
    cout<<node.get_inode_nr();
    cout.width(1);
    cout<<' ';
    cout.width(5);
    cout<<node.contents.get()->size();
    cout.width(1);
    cout<<' ';
    cout.copyfmt(state); //restore widthless state of cout
    cout<<elem.first<<endl;
    DEBUGF('h', "last");
  }
}

void inode_state::ls(const string& dirname){
  inode_ptr target = getDir(dirname);
  if (dirname=="") target=cwd;
  if(target==nullptr) {
    complain() <<"error not a directory\n";
    return;
  }

  ls(target);
}

void inode_state::lsr(const inode_ptr& target){

  //print the current directory
  ls(target);

  //get the pointer to directory
  base_file* basePtr = target->contents.get();

  const directory& dir = static_cast<directory&>(*basePtr);
  const map<string,inode_ptr> dirents_= dir.dirents;
  for(auto elem:dirents_){
    string name = elem.first;
    directory* isDir
        = dynamic_cast<directory*>(elem.second->contents.get());

    //todo:
    //pass ptr instead of string & add helper function lsr_helper(ptr)
    if(isDir!=nullptr && (name!=".")&&(name!="..")) lsr(elem.second);
  }

}

//helper function of the main lsr(ptr) function
void inode_state::lsr(const string& dirname){

  //recursive calls to all directories in this directory
  inode_ptr target;
  if (dirname==""){ 
    target=cwd;
  }else{
    target = getDir(dirname);
  }

  lsr(target);
}

void inode_state::mkdir(const string& dirname) {
    if (getDir(dirname, true) || getDir(dirname, false)) {
        complain() << "mkdir: cannot create directory '" << dirname
            << "': File exists" << endl;
        return;
    }
//get the pointer to directory
  base_file* basePtr = cwd->contents.get();

  //debug stuff
  auto ptr = basePtr->mkdir(dirname);
  auto dir = static_cast<directory*>(ptr->contents.get());
}

void inode_state::cd(const string& dirname) {
  inode_ptr target = getDir(dirname);
  if (dirname=="") target=root;
  if(target==nullptr) {
    complain() <<"error not a directory\n";
    return;
  }
  cwd=target;
}


void inode_state::make(const string& filename, const wordvec& words) {
  //get the pointer to directory
    if (getDir(filename, true) || getDir(filename, false)) {
        complain() << "make: cannot create file '" << filename
            << "': File exists" << endl;
        return;
    }
  base_file* basePtr = cwd->contents.get();
  basePtr->mkfile(filename,words);
}

const string& inode_state::prompt() const { return prompt_; }

ostream& operator<< (ostream& out, const inode_state& state) {
   out << "inode_state: root = " << state.root
       << ", cwd = " << state.cwd;
   return out;
}

void inode_state::remove(string& filename, bool isRecursive){

  //thing to delete is the name after the last / 
  int pos = filename.rfind("/");
  string parentName;
  string lastPath;

  //not / then need to format to make 
  if (pos==-1) {
    parentName = "";
    lastPath = filename;
  }else{
    parentName = filename.substr(0,pos);
    pos = max(0,pos);
    lastPath = filename.substr(pos+1);  
  }
  DEBUGF('m', parentName);
  DEBUGF('m', lastPath);

  //can either be a directory or file
  inode_ptr ptrDirectory = getDir(filename, true);
  inode_ptr ptrFile = getDir(filename, false);



  if(filename=="/"){
    //root, so do nothing and return
    return;
  }

  auto removeFromParentDir = [&](){
  //remove file/dir from parent directory
    inode_ptr parentPtr = getDir(parentName);

    directory* parentdir
        = static_cast<directory*>(parentPtr->contents.get());
    parentdir->dirents[lastPath+"/"].reset();
    if (ptrFile != nullptr) {
        parentdir->dirents.erase(lastPath);
    } else {
        parentdir->dirents.erase(lastPath+"/");
    }
  };

  if (ptrDirectory==nullptr && ptrFile == nullptr){
      complain() <<"error path does not exist\n";
      return;
  }

  //not recursive means that the second condition has to be true
  if(!isRecursive&&ptrDirectory!=nullptr){
    directory* dir
        = static_cast<directory*>(ptrDirectory->contents.get());
    if(dir->size()!=2){
      complain() <<"error, directory not empty\n";
      return;
    }
  } 
  removeFromParentDir();
  
}

inode::inode(file_type type, inode_ptr parent=nullptr):
   inode_nr (next_inode_nr++) {
   switch (type) {
      case file_type::PLAIN_TYPE:
           contents = make_shared<plain_file>();
           break;
      case file_type::DIRECTORY_TYPE:
      //if nullptr then it is a rootnode
      //and must be initiialized to point to itself
           if (parent==nullptr) parent=shared_ptr<inode>(this);
      //pass in shared_ptr to parent and this inode
           contents = make_shared<directory>(parent);
           break;
   }
   DEBUGF ('i', "inode " << inode_nr << ", type = " << type);
}

int inode::get_inode_nr() const {
   DEBUGF ('i', "inode = " << inode_nr);
   return inode_nr;
}


file_error::file_error (const string& what):
            runtime_error (what) {
}

const wordvec& base_file::readfile() const {
   throw file_error ("is a " + error_file_type());
}

void base_file::writefile (const wordvec&) {
   throw file_error ("is a " + error_file_type());
}

void base_file::remove (const string&) {
  //file will inherit and use this
  // contents.reset();

   // throw file_error ("is a " + error_file_type());
}

inode_ptr base_file::mkdir (const string&) {
   throw file_error ("is a " + error_file_type());
}

inode_ptr base_file::mkfile (const string&, const wordvec& words) {

   throw file_error ("is a " + error_file_type());
}


size_t plain_file::size() const {
   size_t size {0};
   for (string word: data){
    size+=word.length();
    size+=1;
   }
   if (size!=0) size-=1;
   DEBUGF ('i', "size = " << size);
   return size;
}

const wordvec& plain_file::readfile() const {
   DEBUGF ('i', data);
   return data;
}


void plain_file::writefile (const wordvec& words) {
  data=words;
  DEBUGF ('i', words);
}

directory::directory(inode_ptr parent){
    dirents[".."]=parent;
    // dirents["."]=self;
}

size_t directory::size() const {
   size_t size =dirents.size();
   DEBUGF ('i', "size = " << size);
   return size;
}




inode_ptr directory::mkdir (const string& dirname) {
    inode_ptr parentPtr=dirents["."];
    dirents[dirname +"/"]
        = make_shared<inode>(file_type::DIRECTORY_TYPE, parentPtr);
    auto temp = static_cast<directory*>(dirents[dirname +"/"]
        ->contents.get());

    temp->dirents["."]=dirents[dirname +"/"];

    DEBUGF ('m', "use count of ."<<dirents[dirname +"/"].use_count());
    DEBUGF ('m', "use count of ."<<temp->dirents["."].use_count());
    DEBUGF ('m', "created directory "<<dirents[dirname +"/"]);

   return parentPtr;
}

inode_ptr directory::mkfile
(const string& filename, const wordvec& words) {
    inode_ptr nodePtr = make_shared<inode>(file_type::PLAIN_TYPE);
    //write words to the file
    nodePtr->contents->writefile(words);

    dirents[filename]=nodePtr;
    return nullptr;
}






