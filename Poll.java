public class Poll{
    
    private int questionNum; //number being polled
    private int clientID; //is the id of client

    public Poll (int questionNum, int clientID){ //initializes a poll object
        this.questionNum = questionNum;
        this.clientID = clientID;
    }

    //allows access to questionNum outsdie of the poll class
    public int getquestionNum(){
        return this.questionNum;
    }
    //allows access to clientID outsdie of the poll class
    public int getID(){
        return this.clientID;
    }
}