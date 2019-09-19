# computer-networks-project
A p2p application for sharing files.

## Description and Execution
This is a client-server application for sharing files on UDP.  
* First run an instance of server by command:    
```
java -jar computer-networks-project.jar p2p -serve -name <filename-to-serve> -path <path-to-file>
```
* now you can run instances of client(s) to download file by executing below command:  
```
java -jar computer-networks-project.jar p2p -receive <filename>
```

