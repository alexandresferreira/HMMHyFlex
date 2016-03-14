#run with parameters PROBLEM TIMELIMIT

# DEFINED PROBLEMS:
# BinPacking
# TSP
# VRP
# PersonnelScheduling
# MAXSAT
# FlowShop
 
#INSTANCE: an integer value representing the instance to be tested.


problem=$1
timeLimit=$2
acc=$3
runs=31

scaling[0]=0.1
scaling[1]=0.08
scaling[2]=0.05



# Set the number of instances of a problem.
if [ $problem == "BinPacking" ]; then
	instance[0]=7
	instance[1]=1
	instance[2]=9
	instance[3]=10
	instance[4]=11
elif [ $problem == "TSP" ]; then
	instance[0]=0
	instance[1]=8
	instance[2]=2
	instance[3]=7
	instance[4]=6
elif [ $problem == "VRP" ]; then
	instance[0]=6
	instance[1]=2
	instance[2]=5
	instance[3]=1
	instance[4]=9
elif [ $problem == "PersonnelScheduling" ]; then
	instance[0]=5
	instance[1]=9
	instance[2]=8
	instance[3]=10
	instance[4]=11
elif [ $problem == "MAXSAT" ]; then
	instance[0]=3
	instance[1]=5
	instance[2]=4
	instance[3]=10
	instance[4]=11
elif [ $problem == "FlowShop" ]; then
	instance[0]=1
	instance[1]=8
	instance[2]=3
	instance[3]=10
	instance[4]=11
fi

for c in ${scaling[@]}
do
	for i in ${instance[@]}
	do
    		for (( j = 0 ; j < $runs; j++ ))
    		do
          	java -Xms3058m -Xmx5848m -cp chesc.jar:build main.Main $problem $i $timeLimit $acc $c > resultados/$problem/$i/$problem'-'$i'-'$j'-ac-'$acc'-C-'$c.txt 
    		done
	done
done
