# SAT solver
SAT solver based on the Conflict-Driven Clause Learning (CDCL).

## How to use
there's a script file:

***./script.sh -help*** for help on usage.

For instance, execution of the help command generates the following:
```
Using script you need to substitute SAT_solver.jar with ./script.sh in order to use the script.

Two usage for this script: 
1. SAT_solver.jar <Strategy> <path_to_cnf>
2. SAT_solver.jar <Strategy> -F <path_to_folder_with_cnf>
Other flags:
-V to print the proofMap

Available strategies: FUIP, TWL
```
Beware that complex CNF problems may result in the generation of considerably large files.
For instance, enabling the "-V" flag for the Pigeon-7 problem led to the creation of a 500Mb file.

In case the script doesn't work, you can always use the JAR file in the project via:
```
java -jar SAT_solver.jar <parameters from above>
```