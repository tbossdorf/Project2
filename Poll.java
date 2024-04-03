public class Poll{
    private int questionNum;
    private int client; //is the id of client

    public Poll (int questionNUm, int client){
        this.questionNum = questionNUm;
        this.client = client;
    }

    public int getquestionNum(){
        return this.questionNum;
    }

    public int getID(){
        return this.client;
    }
}