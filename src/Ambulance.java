import java.util.*;
public class Ambulance{
  String type;
  Victim vic1;
  Victim vic2;
  Hospital hosp;
  int capacity;
  boolean full;
  Vertex node;
  Vertex nextNode;
  String name;
  int patients;
  int toVic;
  int toHosp;
  int timeToNext;
  boolean available=true;
  int currentTime;
  ArrayList<Vertex> nodesInPath = new ArrayList<Vertex>();
  boolean mark;
  boolean gotVic;

  public Ambulance(String type){
    this.type=type;
    if (type.equals("Public"))
      capacity=1;
    else
      capacity=2;
  }

  public void add(){
    patients++;
    if(patients==capacity)
      full=true;
  }

  public void startTimer(){
    available=false;
    node=null;
    if(nodesInPath.size()>0){
      timeToNext=nodesInPath.get(0).dist;
    }else{
      throw new GraphException("No nodes in new ambulance's path.");
    }

  }

  public void reset(){
    vic1=null;
    vic2=null;
    hosp=null;
    full=false;
    nextNode=null;
    patients=0;
    toVic=0;
    toHosp=0;
    timeToNext=0;
    available=true;
    currentTime=0;
    gotVic=false;
    nodesInPath.clear();
  }

  public void onwards(){
    nodesInPath.remove(0);
    available=false;
    if(!nodesInPath.isEmpty()){
      nextNode=nodesInPath.get(0);
      timeToNext=nodesInPath.get(0).dist;
      System.out.println(timeToNext);

    }
    currentTime=0;
  }

  public boolean equals(Ambulance other){
    return this.name.equals(other.name);
  }



}
