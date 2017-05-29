public class Victim{
  Vertex node;
  String insurance;
  String name;
  int time;
  Ambulance theirAmb;
  boolean saved;
  boolean inAmbu;
  boolean dead;


  public Victim(int ins){
    if(ins==0)
      insurance="Private";
    else if (ins==3)
      insurance="Public";
  }

  public String toString(){
    return ("Name: "+name+", Time: "+time);
  }
}
