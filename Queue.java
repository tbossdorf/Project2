package Project2;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Queue {
    //queue stores objects from poll class
    private BlockingQueue<Poll> queue;

    public Queue(){
        //creates an empty queue ready to store poll objects
        queue = new LinkedBlockingQueue<>();
    }

    public void addPoll(Poll poll){
        try{
            queue.put(poll); //adds the poll object
        } catch (InterruptedException e){
            Thread.currentThread().interrupt(); //handles interruption
        }
    }

    public Poll poll(){
        return queue.poll(); //retreives and removes the head of the queue
    }

    public BlockingQueue<Poll> getQueue(){
        return queue; //returns the queue
    }
}


