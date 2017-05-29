import java.util.*;
public class GraphGenerator extends Graph{
  public static int INFINITY = Integer.MAX_VALUE;
  Random rnd = new Random();
  int size;
  int insurances;
  ArrayList<Hospital> hospitals;
  ArrayList<Ambulance> ambulances;


  public GraphGenerator(int n)  {
    this.size=n;
    // 1. Add edges
    for (int i=0; i<n; i++){
      int dests = rnd.nextInt(n);
      for (int j=0; j<dests; j++){
        int end = random(j,n);
        int weight = rnd.nextInt(n/2);
        addEdge(Integer.toString(i),Integer.toString(end),weight);
        }
      }
      // System.out.println(vertexMap.values());
    // 2. Set 13 random nodes as hospitals
    hospitals = genHosps();
    for (int k=0; k<13;k++){
      int nd = rnd.nextInt(n);
      getVertex1(nd).hosp=hospitals.get(k);
      getVertex1(nd).hospital=true;
      hospitals.get(k).node=getVertex1(nd);
      hospitals.get(k).name=Integer.toString(k);
    }
    // 3. Set ambulances around the graph at random nodes
    ambulances = genAmbus();
    for (int l =0;l<25;l++){
      int nd1 = rnd.nextInt(n);
      getVertex1(nd1).ambu=ambulances.get(l);
      getVertex1(nd1).ambulance=true;
      ambulances.get(l).node=getVertex1(nd1);
      ambulances.get(l).name=Integer.toString(l);

    }
  }



  public ArrayList<Hospital> genHosps(){
    ArrayList<Hospital> hosies = new ArrayList<Hospital>();
    for (int k=0;k<10;k++){
      hosies.add(new Hospital("Private"));
    }
    for (int l=0;l<3;l++){
      hosies.add(new Hospital("Public"));
    }
    // System.out.println(hosies.size());
    return hosies;
  }

  public ArrayList<Ambulance> genAmbus(){
    ArrayList<Ambulance> ambus = new ArrayList<Ambulance>();
    ambus.addAll(genAmbus("Private", 20));
    ambus.addAll(genAmbus("Public", 5));
    // System.out.println(ambus.size());
    return ambus;
  }

  public Collection<Ambulance> genAmbus(String type, int n){
    Collection<Ambulance> ambus = new ArrayList<Ambulance>();
    for(int i=0;i<n;i++){
      ambus.add(new Ambulance(type));
    }
    return ambus;
  }

  public int random(int j, int n){
    int ran = rnd.nextInt(n);
    if(ran==j){
      random(j,n);
    }
    return ran;
    }

    public int randomVic(int n){
      int ran = rnd.nextInt(n);
      Vertex v = vertexMap.get((Integer.toString(ran)));
      if(v==null){
        throw new GraphException("ran does not exist: "+ran+", Size is "+size+" Nodes: "+vertexMap.values());
      }
      if(v.victim || v.hospital){
        randomVic(n);
      }
      return ran;
      }

    public Victim addVictim(){
      int insurance=0;
      int num = randomVic(size); //Random number between 0 and size that is not a vertex that already holds a vic, ambu or hosp
      if(/*insurances!=0 &&*/ insurances%10==0){
        insurance = 0;//rnd.nextInt(3);
        insurances++;
      }else{
        insurance=3;
        insurances++;
      }

      return victim(num, insurance);
    }

    private Victim victim(int name, int insurance){
       String vertName = Integer.toString(name);
       (vertexMap.get(vertName)).victim = true;
       Victim v =new Victim(insurance);
       vertexMap.get(vertName).vic=v;
       vertexMap.get(vertName).victim=true;
       v.node= vertexMap.get(vertName);
       v.name=v.insurance+vertName;
      //  System.out.println(v+" "+name);
       return v;
    }

    public ArrayList<Victim> checkVictims(){
      ArrayList<Victim> vicNames = new ArrayList<Victim>();
      for(Vertex v: vertexMap.values()){
        if(v.victim==true && !v.vic.dead){
          vicNames.add(v.vic);
        }
      }
      return vicNames;
    }

    /**
     * Single-source weighted shortest-path algorithm. (Dijkstra)
     * using priority queues based on the binary heap
     */
    public boolean dijkstra( String startName)
    {
          // ArrayList<Vertex> path = new ArrayList<Vertex>();
          boolean check =false;
          PriorityQueue<Path> pq = new PriorityQueue<Path>( );

          Vertex start = vertexMap.get( startName );
          if( start == null )
                throw new NoSuchElementException( "Start vertex not found" );

          clearAll( );
          pq.add( new Path( start, 0 ) ); start.dist = 0;

          int nodesSeen = 0;
          while( !pq.isEmpty( ) && nodesSeen < vertexMap.size( ) )
          {
                Path vrec = pq.remove( );
                Vertex v = vrec.dest;
                if( v.scratch != 0 )  // already processed v
                      continue;

                if(v.hospital){
                  if(v.hosp.full)
                    continue;
                }

                v.scratch = 1;
                nodesSeen++;

                for( Edge e : v.adj )
                {
                      Vertex w = e.dest;
                      int cvw = e.cost;
                      int newCost = v.dist+cvw;
                      // System.out.println(w.name+": "+w.dist+" "+v.name+": "+newCost);
                      // System.out.println(w.name+".victim: "+w.victim+" and "+v.name+".victim: "+v.victim);

                      if( cvw < 0 )
                            throw new GraphException( "Graph has negative edges" );

                      if( w.dist > v.dist + cvw )
                      {
                            w.dist = v.dist +cvw;
                            w.prev = v;
                            pq.add( new Path( w, w.dist ) );
                      }
                      else if( w.dist == newCost){//} && w.victim==v.victim && w.victim!=-1){
                         check = true;
                         // System.out.print(check);
                      }
                }
          }

          return check;
    }

    public String printDaPath( Vertex dest, Vertex source)
   {

     if( dest!= source  )
     {
         return printDaPath(dest.prev,source)+" "+dest.name;
     }
     else{
       return dest.name;
     }
 }

    public ArrayList<Vertex> getPath(Vertex source, Vertex dest){
      dijkstra(source.name);
      ArrayList<Vertex> path = new ArrayList<Vertex>();
      if(dest.equals(source))
        return null;
      String rtn=printDaPath(dest,source);
      String[] chars = rtn.split(" ");
      for (String s: chars){
        if(!s.equals(" "))
          path.add(getVertex(s));
      }
      path.remove(0);
      return path;

    }


}
