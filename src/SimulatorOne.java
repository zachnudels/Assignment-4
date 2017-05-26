import java.util.*;
public class SimulatorOne{

  public static void main (String[] args){

    Graph g = new Graph( );

    // Read input into arraylist
    Scanner scan = new Scanner(System.in);
    ArrayList<String> lines = new ArrayList<String>();
    while(scan.hasNextLine()){
      String t = (scan.nextLine());
      lines.add(t);

    }

    // Initialise nodes
    ArrayList<Vertex> verts = new ArrayList<Vertex>();
    for (int i=0; i<Integer.parseInt(lines.get(0));i++){
      verts.add(new Vertex(Integer.toString(i)));
    }

    // Add destination nodes to each node
    for(int j=0 ;j<verts.size();j++){
      // System.out.println();
      // System.out.println(j);
      int cut = 1;
      if (j>9)
        cut=2;
      String text = (lines.get(j+1)).substring(cut)+" ";
      while(!text.equals(" ")){
        // slice ' ' off
        // System.out.println(text);
          text=text.substring(1);
        // System.out.println(text);
        // save first int - i of node
        // System.out.println(text);
        int vertI = Integer.parseInt(text.substring(0,text.indexOf(' ')));
        // slice i off
        text = text.substring((text.indexOf(' '))+1);
        // save second int - weight of node(i)
        double weight = Double.parseDouble(text.substring(0, text.indexOf(' ')));
        // slice weight off
        text = text.substring(text.indexOf(' '));
        // add dest to node(j)
        g.addEdge(Integer.toString(j),Integer.toString(vertI),weight);
      }
    }

    // add hospitals - # of hospitals will always be on lines(#ofNodes+1) and nodes of hospitals will be on lines(#ofNodes+2)
    String hosps = " "+lines.get((verts.size()+2))+" ";
    int[] hospitals = new int[verts.size()];
    while(!hosps.equals(" ")){
      // slice ' ' off
      hosps = hosps.substring(1);
      // save index of hosp
        int hospsI = Integer.parseInt(hosps.substring(0,hosps.indexOf(' ')));
      // slice off index
      hosps=hosps.substring(hosps.indexOf(' '));
      verts.get(hospsI).hospital=true;
      hospitals[hospsI]=1;
    }

/*****************************************************************************
*Main method to find and print routes
*****************************************************************************/


// Find Victims

    String vics = " "+lines.get(verts.size()+4)+ " ";
    // System.out.println(vics);
    // int[] costs = new int[hospitals.size()];
    while(!vics.equals(" ")){
      // slice ' ' off
      vics = vics.substring(1);
      // save index of vic
      int vicsI = Integer.parseInt(vics.substring(0,vics.indexOf(' ')));
      // slice off index
      vics=vics.substring(vics.indexOf(' '));
      g.victim(vicsI);

      System.out.println("victim "+vicsI);
      // System.out.println(verts.get(vicsI)+".victim: "+verts.get(vicsI).victim);


// Find costs of shortest round paths from each hospital to victim vicsI
      double[] paths = new double[hospitals.length];
      for (int k =0; k<hospitals.length;k++){
        if (hospitals[k]==1){
          g.dijkstra(k);
          paths[k]=(g.getVertex1(vicsI)).dist;
          g.dijkstra(vicsI);
          paths[k]=paths[k]+(g.getVertex1(k).dist);
        }
      }

// Determine if more than one shortest path
      ArrayList<Integer> bestPaths = new ArrayList<Integer>();

      // Save best path
      double bestPath = g.INFINITY;
      for (int l = 0; l<hospitals.length;l++){
        if (hospitals[l]==1){
          if(paths[l]<bestPath)
            bestPath=paths[l];
        }
      }

      // If any path has the same cost as best path, save to arraylist
      for (int m = 0 ; m<hospitals.length; m++){
        if(hospitals[m]==1){
          if(paths[m]==bestPath)
            bestPaths.add(m);
        }
      }

// Determine if no shortest path
      if(bestPaths.isEmpty())
        System.out.println("cannot be helped");

// Print all paths in bestpaths arraylist
      for(Integer spath: bestPaths){
        System.out.println("hospital "+ spath);
        g.printPath(vicsI,spath, bestPath);
      }

    }


  }

}
