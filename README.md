# Parallel-Assignment-3

Problem One

Running: 

  javac ProblemOne.java

  java ProblemOne

Sample Output:

  All presents have been added!

  All thank-you notes written! All Done!

Approach:
To represent the bag, I created an integer ArrayList of size 500000. I then filled the array with integers 1 - 500000, then called Collections.shuffle(bag) to make the order of gifts random. My chain was represented with a non-blocking linked list based on the textbook's implementation in chapter 9. Each servant alternates between adding a gift to the chain and removing one from the chain. In addition, there is a 30% chance each loop the minotaur will look at the chain for a certain gift (using contains()).

Adding gifts: The servants share an atomic counter variable. Each servant will get the value from the bag at the index counter, then increment counter. (Randomness is simulated since the order in the bag is already randomized). Once the counter is more than 500000, every element has been added to the chain.

Removing gifts: the servants will always remove gifts from the head (ignoring the sentinel node) They will stop when no other nodes exist besides the head, the tail, and the second tail (added to deal with edge cases).

Efficiency/correctness: 
The program finishes (adds and removes all presents from the chain) in less than a second. The program will not end until every gift has been both added and removed. It will not stop running until 
1. The bag is empty.
2. 500000 elements have been removed from the chain.

# Parallel-Assignment-3

Problem Two

Running: 

  javac ProblemTwo.java

  java ProblemTwo

Sample Output:

  see TwoOutput.txt

Approach:
I simulated the temperature readings with two loops: an outer loop in main representing hours. Each hour, 8 sensor threads are created. The sensor threads fill a 2d array of size [8][60] with all of the temperature readings taken that hour. One this array is returned, I place it all in a TreeSet to order it, and then iterate through the back and front of the treeset to print the highest/lowest values that hour. Using a treeset also avoids duplicate values.

I then print the 10-minute timeframe in which the greatest temp difference is found. I print the first minute of the 10-minute window, and the difference.


Efficiency/correctness:
Runs in a couple seconds. Output is often similar just due to probability, but is not always the same, so it is random. 
