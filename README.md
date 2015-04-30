# Mutual-Exclusion-on-Distributed-System
Implemented Ricarta Agrawala mutual exclusion on a distributed systems

-> Compile all java Files:
MainRA.java
RicartaAgrawala.java

-> Note down IP addresses of all 5 machines
-> To properly run the code must be run on all 5 machines

On 1st Machine
Run as : java MainRA 1
where 1 = node number (this DOES NOT CHANGE)


On 2nd Machine
Run as : java MainRA 2 129.21.30.38
where 2 = node number (this DOES NOT CHANGE)                
129.21.30.38 - IP address of 1st Machine

On 3rd Machine
Run as : java MainRA 3 129.21.30.38 129.21.37.28
129.21.30.38 - IP address of 1st Machine
129.21.37.28 - IP address of 2nd Machine

On 4th Machine
Run as : java MainRA 4 129.21.30.38 129.21.37.28 129.21.37.31
129.21.30.38 - IP address of 1st Machine
129.21.37.28 - IP address of 2nd Machine
129.21.37.31 - IP address of 3rd Machine

On 5th Machine
Run as : java MainRA 5 129.21.30.38 129.21.37.28 129.21.37.31 129.21.37.30
129.21.30.38 - IP address of 1st Machine
129.21.37.28 - IP address of 2nd Machine
129.21.37.31 - IP address of 3rd Machine
129.21.37.30 - IP address of 4th Machine


TEST CASE:
On Node 2
Enter command 1. REQUEST (To enter CS) 2.STOP
REQUEST

Output : Open CS.txt on 1st Machine to read access order

On Node 4
Enter command 1. REQUEST (To enter CS) 2.STOP
REQUEST

Output : Open CS.txt on 1st Machine to read access order
