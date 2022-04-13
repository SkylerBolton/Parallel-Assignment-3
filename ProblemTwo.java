import java.lang.Thread;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Arrays;
import java.util.*;
import java.util.TreeSet;

public class ProblemTwo 
{
    public static CountDownLatch cd;
    public static ReentrantLock lock = new ReentrantLock();
    public static int[][] tempArray = new int[8][60];
    
    public static void main(String[] args)
    {
        TreeSet<Integer> set = new TreeSet<Integer>();


        for (int k = 0; k < 12; k++)
        {

            cd = new CountDownLatch(8);
            System.out.println("Hour #" + (k+1));
            for (int j = 0; j < 8; j++)
            {
                Sensor s = new Sensor(j);
                Thread sThread = new Thread(s);
                sThread.start();
            }
    
            try {
                cd.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            for (int i = 0; i < 8; i++)
            {
                for (int j = 0; j < 60; j++)
                {
                    set.add(tempArray[i][j]);
                } 
                
            }
            Iterator iter = set.descendingIterator();
            System.out.println("\n5 highest temperatures this hour:");
            for(int i  = 0; i < 5; i++)
            {
                System.out.println(iter.next());
            } 

            iter = set.iterator();
            System.out.println("\n5 lowest temperatures this hour:");
            for(int i  = 0; i < 5; i++)
            {
                System.out.println(iter.next());
            } 
            
        
            printWidestRange();
            System.out.println("\n");
            set.clear();
            for(int i = 0; i < 8; i++)
                Arrays.fill(tempArray[i], 0);
        }
    }

    static int maxAbsDiff(int arr[], int n)
    {
        int min = arr[0];
        int max = arr[0];
        for (int i = 1; i < n; i++) 
        {
            min = Math.min(min, arr[i]);
            max = Math.max(max, arr[i]);
        }

        return (max - min);
    }

    static void printWidestRange()
    {
        ArrayList<Integer> list = new ArrayList<>(481);
        int arr[] = new int[10];
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 60; j++)
            {
                list.add(tempArray[i][j]);
            } 
        }
        int maxDiff = 0;
        int diff = 0;
        int minute = 0;
        for (int i = 0; i < 50; i++)
        {
            // between i and i+10, find greatest difference
            for (int j = 0; j < 10; j++)
            {
                arr[j] = list.get(i + j);;
            }
            diff = maxAbsDiff(arr, 10);
            if (diff > maxDiff)
            {
                maxDiff = diff;
                minute = i; 
            }
        }
        System.out.println("\nThe 10-minute interval of time when the largest temperature difference was observed this hour was minute #" + minute + ".");
        System.out.println("This difference was: " + maxDiff + " degrees");
    }
}

class Sensor implements Runnable 
{
    int i = 0;
    Random rn = new Random();
    int row;
    int temperature; 


    public Sensor (int row)
    {
        this.row = row;
    }

    @Override
    public void run()
    {
        for (i = 0; i < 60; i++)
        {
            temperature = rn.nextInt(171) - 100;
            ProblemTwo.tempArray[row][i] = temperature;
        }
        ProblemTwo.cd.countDown();
    }

}
