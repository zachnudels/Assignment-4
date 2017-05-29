import java.util.*;
public class Trip implements Comparable<Trip>{
  Hospital hosp;
  Victim vic;
  Ambulance amb;
  int costToVic;
  int costToHosp;
  int totalCost;
  ArrayList<Vertex> path;
  // int timeSoFar;

  public Trip(Victim v, int cost, Ambulance amb){
    this.vic=v;
    this.costToVic=cost;
    this.amb=amb;
  }

  public Trip(Victim v, int cost, Hospital hosp){
    this.vic=v;
    this.costToHosp=cost;
    this.hosp=hosp;
  }

  public int compareTo(Trip other){
    int otherCost=other.totalCost;
    return totalCost < otherCost ? -1 : totalCost > otherCost ? 1 : 0;
  }




}
