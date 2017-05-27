import java.util.*;
public class GraphGenerator{
  Graph g;
  Random rnd = new Random();


  public GraphGenerator(int n)  {
    g = new Graph();
    // 1. Add edges
    for (int i=0; i<n; i++){
      int dests = rnd.nextInt(n);
      for (int j=0; j<dests; j++){
        int end = random(j,n);
        double weight = (double) rnd.nextInt(n/10);
        g.addEdge(Integer.toString(i),Integer.toString(j),weight);
        }
      }
    // 2. Set 13 random nodes as hospitals
    ArrayList<Hospital> hospitals = genHosps();
    for (int k=0; k<13;k++){
      int nd = rnd.nextInt(n);
      g.getVertex1(nd).hosp=hospitals.get(k);
      hospitals.get(k).node=g.getVertex1(nd);
    }
    // 3. Set ambulances around the graph at random nodes

  }

  public ArrayList<Hospital> genHosps(){
    ArrayList<Hospital> hosies = new ArrayList<Hospital>();
    for (int i=0;i<2;i++){
      hosies.add(new Hospital("Life", i));
    }
    for (int j=0;j<4;j++){
      hosies.add(new Hospital("MediClinic",j));
    }
    for (int k=0;k<5;k++){
      hosies.add(new Hospital("NetCare",k));
    }
    for (int l=0;l<2;l++){
      hosies.add(new Hospital("Public",l));
    }
    return hosies;
  }

  public int random(int j, int n){
    int ran = rnd.nextInt(n);
    if(ran==j){
      random(j,n);
    }
    return ran;
    }

}
