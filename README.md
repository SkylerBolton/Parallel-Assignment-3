# Parallel-Assignment-3

Problem One

Running: 

  javac ProblemOne.java

  java ProblemOne

Sample Output:

  All presents have been added!

  All thank-you notes written! All Done!

Approach:
To represent the bag, I created an integer ArrayList of size 500000. I then filled the array with integers 1 - 500000, then called Collections.shuffle(bag) to make the order of gifts random. My chain was represented with a non-blocking linked list based on the textbook's implementation in chapter 9. ach servant alternates between adding a gift to the chain and removing one from the chain. In addition, there is a 30% chance each loop the minotaur will look at the chain for a certain gift (using contains()).

Adding gifts: The servants share an atomic counter variable. Each servant will get the value from the bag at the index counter, then increment counter. (Randomness is simulated since the order in the bag is already randomized). Once the counter is more than 500000, every element has been added to the chain.

Removing gifts: the servants will always remove gifts from the head (ignoring the sentinel node) They will stop when no other nodes exist besides the head, the tail, and the second tail (added to deal with edge cases).

Efficiency: 
The program finishes (adds and removes all presents from the chain) in less than a second. The program will not end until every gift has been both added and removed.
