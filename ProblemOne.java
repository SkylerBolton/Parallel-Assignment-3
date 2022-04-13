import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.HashSet;
import java.util.Random;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.CountDownLatch;

import javax.print.event.PrintEvent;

import java.util.concurrent.locks.ReentrantLock;

public class ProblemOne
{ 
    public static GiftList gList = new GiftList();
    public static ReentrantLock ArrayLock = new ReentrantLock();
    public static CountDownLatch cd = new CountDownLatch(4);
    
    public static ArrayList<Integer> bag = new ArrayList<>(500000);
    public static void main(String[] args)
    {
        long startTime = System.nanoTime();
        for (int i = 1; i <= 500000; i++)
        {
            bag.add(i);
        }

        Collections.shuffle(bag);

        Servant servant1 = new Servant(bag); 
		Thread servantThread1 = new Thread(servant1);
		servantThread1.start();
        
        Servant servant2 = new Servant(bag); 
		Thread servantThread2 = new Thread(servant2);
		servantThread2.start();
        
        Servant servant3 = new Servant(bag); 
		Thread servantThread3 = new Thread(servant3);
		servantThread3.start();

        Servant servant4 = new Servant(bag); 
		Thread servantThread4 = new Thread(servant4);
		servantThread4 .start();
        
        /*  This prints an ordered set for some reason??
        HashSet<Integer> bag = new HashSet<>(500);
        for (int i = 1; i < 501; i++)
        {
            bag.add(i);
        }

        for  (Integer i : bag)
        {
            System.out.println(i);
        }
        */
        try {
            cd.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endtime  = System.nanoTime();
        
        System.out.print("All thank-you notes written!\nDone in " + ((endtime - startTime)/1000000) + " milliseconds!");
        
        
    }
}



class GiftList
{
    Node head;

    public GiftList()
    {
        head = new Node(-2);
        Node nearTail = new Node(500001);
        Node tail = new Node(500001);
        head.next = new AtomicMarkableReference<Node>(nearTail, false);
        nearTail.next = new AtomicMarkableReference<Node>(tail, false);
    }

    public boolean add(int item) 
    {
        int key = item;
        while (true) 
        {
            Window window = Window.find(head, key);
            Node pred = window.pred, curr = window.curr;
            if (curr.key == key) 
            {
                return false;
            } 
            else 
            {
                Node node = new Node(item);
                node.next = new AtomicMarkableReference(curr, false);
                if (pred.next.compareAndSet(curr, node, false, false)) 
                {
                   return true;
                }
            }
        }
    }

    public boolean remove(int item) 
    {
        int key = item;
        boolean snip;
        while (true) 
        {
            Window window = Window.find(head, key);
            Node pred = window.pred, curr = window.curr;
            if (curr.key != key) 
            {
                return false;
            } 
            else 
            {
                Node succ = curr.next.getReference();
                snip = curr.next.compareAndSet(succ, succ, false, true);
                if (!snip)
                    continue;
                pred.next.compareAndSet(curr, succ, false, false);
                return true;
            }
        }
    }

    public int removeHead() 
    {
        Node pred = head;
        Node second = pred.next.getReference();
        Node third = second.next.getReference();

        if (second.next == null || third.next == null) 
        {
            return -1;
        }

        int n = third.key;

        head.next = new AtomicMarkableReference(third, false);
        
        return n;
    }

    public boolean contains(int item) 
    {
        boolean[] marked = {false};
        int key = item;
        Node curr = head;
        while (curr.key < key) 
        {
            curr = curr.next.getReference();
        }
        return (curr.key == key && !marked[0]);
    }
}

class Node 
{
    int key;
    public volatile AtomicMarkableReference<Node> next;
 
    Node(int key)
    {        
        this.key = key;
        this.next = null;
    }
}

class Servant implements Runnable
{
    Random rand = new Random();
    ArrayList<Integer> bag;
    public static AtomicInteger remaining = new AtomicInteger(500000);
    public static AtomicInteger counter = new AtomicInteger(0);
    int gift;
    int idx;
    int personal = 50000 / 4;
    public static boolean doneAddingDisplayed = false;

    public Servant(ArrayList<Integer> bag)
    {
        this.bag = bag;
    }

    @Override
    public void run() 
    {
        int choice;
        int alter = 0;
        boolean x; 
        Node thing;

        while(true)
        {

            // The servants will alternate between adding gifts and writing thank-you notes.
            if (alter == 1)
                alter = 0;
            else
                alter = 1;

            // There is a 1/3 chance the minotaur will ask to call contains().
            choice = rand.nextInt(3) + 1;

            // Add something to the chain of gifts
            if (alter == 1 && personal > 0)
            {
            
                
                if(!bag.isEmpty() && counter.get() < 500000)
                {
                    gift = bag.get(counter.get());
                    counter.getAndIncrement();
                    ProblemOne.gList.add(gift);

                }
            }
            
            // Write a thank you note and remove it
            if (alter == 0)
            {
                if(ProblemOne.gList.head.next != null)
                {
                    ProblemOne.gList.removeHead();
                    remaining.decrementAndGet();
                }
            }

            // Check if a random gift is in the list
            thing = ProblemOne.gList.head.next.getReference();;
            if (choice == 3 && thing.key != Integer.MAX_VALUE)
            {
                ProblemOne.gList.contains(rand.nextInt(500000));
            }
        
            if (counter.get() < 500000 && !doneAddingDisplayed)
            {
                doneAddingDisplayed = true;
                System.out.println("All presents have been added!");
                
            }
            if(remaining.get() < 1)
            {
                break;
            }
        }
        ProblemOne.cd.countDown();
    }
}

class Window 
{
    public Node pred, curr;
    Window(Node myPred, Node myCurr) 
    {
        pred = myPred; curr = myCurr;   
    }

    public static Window find(Node head, int key) 
    {
        Node pred = null, curr = null, succ = null;
        boolean[] marked = {false};
        boolean snip;
        retry: while (true) 
        {
            pred = head;
            curr = pred.next.getReference();
           
        
            while (true) 
            {
                succ = curr.next.get(marked);
                while (marked[0]) 
                {
                    snip = pred.next.compareAndSet(curr, succ, false, false);
                    if (!snip) continue retry;
                    curr = succ;
                    succ = curr.next.get(marked);
                }
                if (curr.key >= key)
                return new Window(pred, curr);
                pred = curr;
                curr = succ;
            }
        }
    }
}