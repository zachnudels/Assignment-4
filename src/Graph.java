import java.util.*;
import java.io.*;

// Used to signal violations of preconditions for
// various shortest path algorithms.
class GraphException extends RuntimeException
{
    /**
    *
    */
   private static final long serialVersionUID = 1L;

   public GraphException( String name )
    {
        super( name );
    }
}

  // Represents an edge in the graph.
  class Edge
  {
      public Vertex     dest;   // Second vertex in Edge
      public double     cost;   // Edge cost

      public Edge( Vertex d, double c )
      {
          dest = d;
          cost = c;
      }
  }

  // Represents an entry in the priority queue for Dijkstra's algorithm.
  class Path implements Comparable<Path>
  {
      public Vertex     dest;   // w
      public double     cost;   // d(w)

      public Path( Vertex d, double c )
      {
          dest = d;
          cost = c;
      }

      public int compareTo( Path rhs )
      {
          double otherCost = rhs.cost;

          return cost < otherCost ? -1 : cost > otherCost ? 1 : 0;
      }
  }

  // Represents a vertex in the graph.
  class Vertex
  {
      public String     name;   // Vertex name
      public List<Edge> adj;    // Adjacent vertices
      public double     dist;   // Cost
      public Vertex     prev;   // Previous vertex on shortest path
      public int        scratch;// Extra variable used in algorithm
      public boolean          victim;  //Victim #
    	public boolean       hospital; //Hospital or not
			public boolean unique;

      public Vertex( String nm )
        { name = nm; adj = new LinkedList<Edge>( ); reset( ); victim=false;}

      public void reset( )
      //  { dist = Graph.INFINITY; prev = null; pos = null; scratch = 0; }
      { dist = Graph.INFINITY; prev = null; scratch = 0; }

     public String toString(){
          return this.name;
       }
  }

public class Graph{

  public static final double INFINITY = Double.MAX_VALUE;
  public Map<String,Vertex> vertexMap = new HashMap<String,Vertex>( );

  /**
   * Add a new edge to the graph.
   */
  public void addEdge( String sourceName, String destName, double cost )
  {
      Vertex v = getVertex( sourceName );
      Vertex w = getVertex( destName );
      v.adj.add( new Edge( w, cost ) );
  }

   public void victim(int name){
      String vertName = Integer.toString(name);
      (vertexMap.get(vertName)).victim = true;
   }


	 public boolean isVictim(String name){
		 Vertex v = vertexMap.get(name);
			 return v.victim;

	 }
	 public boolean doesContain(String start, String dest){
		 Vertex w = vertexMap.get(start);
		//  System.out.println("Does contain starts");

		 if(w.prev==null || w.prev.name.equals(start) ){
			 System.out.println("Check");
				 return false;}

		 if (!w.prev.name.equals(dest) && !w.prev.name.equals(start)){
			 System.out.println(w.prev.name);
			 doesContain(w.prev.name, dest);
		 }
		 if(w.prev.name.equals(dest))
			 return true;

		 System.out.println("check");
		 return false;



	 }

	 public boolean checkMultiple(String startName, String victimName, double bestPath)
		{
		 //  System.out.println(startName);
			boolean rtn = false;
			ArrayList<Double> paths = new ArrayList<Double>();

				PriorityQueue<Path> pq = new PriorityQueue<Path>( );

				Vertex hosp = vertexMap.get( startName );
				Vertex vic = vertexMap.get(victimName);

				if( hosp == null )
						throw new NoSuchElementException( "Start vertex not found" );

				clearAll( );
				pq.add( new Path( hosp, 0 ) ); hosp.dist = 0;
			 //  System.out.println("Check");
				int best = 0;
				while( !pq.isEmpty( ) )//&& nodesSeen < vertexMap.size( ) )
				{
						Path vrec = pq.remove( );
						Vertex v = vrec.dest;
					 //  if( v.scratch != 0 )  // already processed v
							 //  continue;

					 //  v.scratch = 1;
					 //  nodesSeen++;
					 // System.out.println("Check");
						for( Edge e : v.adj )
						{
								Vertex w = e.dest;
								double cvw = e.cost;

								if( cvw < 0 )
										throw new GraphException( "Graph has negative edges" );

								if( w.dist > v.dist + cvw || w.dist ==0 || w.dist==(v.dist+cvw) )
								{
										w.dist = v.dist +cvw;
										w.prev = v;
										pq.add( new Path( w, w.dist ) );
								}
								// System.out.println(w.name);
								// System.out.println(w.prev.name);
								if (w.name.equals(startName)){
									// System.out.println("w.name is A");
									 if(doesContain(startName, victimName)){
									double y = v.dist+cvw;
									paths.add(y);

									}
							} }
					}

					for(Double d: paths){
						if (d==bestPath)
						 best++;
					}

				if (best>1){
					System.out.println(best);
					rtn=true;
				}
			 //  System.out.println("CheckMultiple End "+rtn);
				return rtn;
			 // if (best>1){
			 // 	rtn=true;
			 // }
			 // return rtn;
		}

	//  public boolean checkMultiple(String startName, String victimName, double bestPath)
	// 	 {
	// 		//  System.out.println(startName);
	// 		 boolean rtn = false;
	// 		 ArrayList<Double> vicPaths = new ArrayList<Double>();
	// 		 ArrayList<Double> hosPaths = new ArrayList<Double>();
	// 			 PriorityQueue<Path> pq = new PriorityQueue<Path>( );
	// 			 PriorityQueue<Path> pq1 = new PriorityQueue<Path>();
	 //
	// 			 Vertex hosp = vertexMap.get( startName );
	// 			 Vertex vic = vertexMap.get(victimName);
	 //
	// 			 if( hosp == null )
	// 					 throw new NoSuchElementException( "Start vertex not found" );
	 //
	// 			 clearAll( );
	// 			 pq.add( new Path( hosp, 0 ) ); hosp.dist = 0;
	// 			//  System.out.println("Check");
	// 			 int best = 0;
	// 			 while( !pq.isEmpty( ) )//&& nodesSeen < vertexMap.size( ) )
	// 			 {
	// 					 Path vrec = pq.remove( );
	// 					 Vertex v = vrec.dest;
	// 					//  if( v.scratch != 0 )  // already processed v
	// 							//  continue;
	 //
	// 					//  v.scratch = 1;
	// 					//  nodesSeen++;
	// 					// System.out.println("Check");
	// 					 for( Edge e : v.adj )
	// 					 {
	// 							 Vertex w = e.dest;
	// 							 double cvw = e.cost;
	 //
	// 							 if( cvw < 0 )
	// 									 throw new GraphException( "Graph has negative edges" );
	 //
	// 							//  if( w.dist > v.dist + cvw )
	// 							//  {
	// 							// 		 w.dist = v.dist +cvw;
	// 							// 		 w.prev = v;
	// 							// 		 pq.add( new Path( w, w.dist ) );
	// 							//  }
	// 							pq.add( new Path( w, w.dist ) );
	// 							 if (w.name.equals(victimName)){
	// 								 double y = v.dist+cvw;
	// 								 vicPaths.add(y);
	// 								 pq.add( new Path( w, w.dist ) );
	// 								//  if (w.dist==bestPath)
 // 								// 		best++;
 // 								// 	 if (w.dist == v.dist+cvw){
 // 								// 		 rtn = true;
 // 								// 		//  System.out.println("true");
 // 								// 	 }
 // 								// 	 if ((w.dist < v.dist+cvw) || (w.dist>v.dist+cvw))
 // 								// 	 	rtn=false;
	 //
 // 								 }
 // 						 }
 // 				 }
	 //
	// 			clearAll( );
	// 			pq1.add( new Path( vic, 0 ) ); vic.dist = 0;
	 //
	// 			int best1 = 0;
	// 			while( !pq1.isEmpty( ) )//&& nodesSeen < vertexMap.size( ) )
	// 			{
	// 					Path vrec1 = pq1.remove( );
	// 					Vertex v1 = vrec1.dest;
	// 				 //  if( v.scratch != 0 )  // already processed v
	// 						 //  continue;
	 //
	// 				 //  v.scratch = 1;
	// 				 //  nodesSeen++;
	 //
	// 					for( Edge e1 : v1.adj )
	// 					{
	// 							Vertex w1 = e1.dest;
	// 							double cvw1 = e1.cost;
	 //
	// 							if( cvw1 < 0 )
	// 									throw new GraphException( "Graph has negative edges" );
	 //
	// 							// if( w1.dist > v1.dist + cvw1 )
	// 							// {
	// 							// 		w1.dist = v1.dist +cvw1;
	// 							// 		w1.prev = v1;
	// 							// 		pq1.add( new Path( w1, w1.dist ) );
	// 							// }
	// 							pq1.add( new Path( w1, w1.dist ) );
	// 							if (w1.name.equals(startName)){
	// 								double x = v1.dist+cvw1;
	// 								hosPaths.add(x);
	// 						 }
	// 				 }
	// 		 }
	// 		//  System.out.println(hosPaths.size());
	// 		 for (int i = 0; i<hosPaths.size();i++){
	 //
	// 			 for (int j = 0; j< vicPaths.size();j++){
	// 				 double pathCheck = hosPaths.get(i)+vicPaths.get(j);
	// 				//  System.out.println(hosPaths.get(i)+vicPaths.get(j));
	// 				 if (pathCheck==bestPath){
	// 					//  System.out.println(pathCheck);
	// 					 best++;
	// 				 }
	// 			 }
	// 		 }
	 //
	 //
	 //
	 //
	 //
	 //
	 //
	 //
	// 			 if (best>1){
	// 				//  System.out.println(best);
	// 				 rtn=true;
	// 			 }
	// 			//  System.out.println("CheckMultiple End "+rtn);
	// 			 return rtn;
	// 			// if (best>1){
	// 			// 	rtn=true;
	// 			// }
	// 			// return rtn;
	// 	 }


   /**
    * Driver routine to handle unreachables and print total cost.
    * It calls recursive routine to print shortest path to
    * destNode after a shortest path algorithm has run.
    */
   public void printPath( int dest, int source , double bestPath)
   {
		//  System.out.println(bestPath);
         if(dest==source){
            System.out.println(dest);
            return;
         }
         String destName = Integer.toString(dest);
         String sourceName = Integer.toString(source);
				 Vertex w = vertexMap.get( destName );
				 Vertex v = vertexMap.get( sourceName );

         if (checkMultiple(sourceName, destName, bestPath)){
				 System.out.println("multiple solutions");
					 dijkstra(source);
					 Double path = w.dist;
					 dijkstra(dest);
					 path = path+v.dist;
					 System.out.println("multiple solutions cost "+bestPath);
					 return;
				 }
				 dijkstra(source);
         if( w == null )
               throw new NoSuchElementException( "Destination vertex not found" );
         else if( w.dist == INFINITY ){
               System.out.println( destName + " is unreachable" );
							 return;
         }else
         {
               // System.out.print( "(Cost is: " + w.dist + ") " );
               printDestPath( w );
               // System.out.println( );
         }

         dijkstra(dest);

         if( w == null )
               throw new NoSuchElementException( "Source vertex not found" );
         else if( v.dist == INFINITY ){
               System.out.println( destName + " is unreachable" );
							 return;
         }else
         {
              //  System.out.print( "Check" );
							 System.out.print(" ");
               printSourcePath( v );

         }
         System.out.println( );

   }

   public Vertex getVertex1(int vertex){
      return getVertex(Integer.toString(vertex));
   }

  /**
   * If vertexName is not present, add it to vertexMap.
   * In either case, return the Vertex.
   */
  public Vertex getVertex( String vertexName )
  {
      Vertex v = vertexMap.get( vertexName );
      if( v == null )
      {
          v = new Vertex( vertexName );
          vertexMap.put( vertexName, v );
      }
      return v;
  }

  /**
   * Recursive routine to print shortest path to dest
   * after running shortest path algorithm. The path
   * is known to exist.
   */
	 private void printDestPath( Vertex dest )
  {

      if( dest.prev != null )
      {
          printDestPath( dest.prev );
          System.out.print( " " );
      }
      System.out.print( dest.name );
  }

   /**
   * Recursive routine to print shortest path to dest
   * after running shortest path algorithm. The path
   * is known to exist.
   */
	 private void printSourcePath( Vertex dest )
   {

       if( dest.prev.prev != null )
       {
           printSourcePath( dest.prev );
           System.out.print( " " );
       }
       System.out.print( dest.name );
   }

  /**
   * Initializes the vertex output info prior to running
   * any shortest path algorithm.
   */
  private void clearAll( )
  {
      for( Vertex v : vertexMap.values( ) )
          v.reset( );
  }

   public void printNodes(){
      for(Vertex v: vertexMap.values()){
         if(v!=null)
            System.out.println(v);
      }
   }

  /**
   * Single-source unweighted shortest-path algorithm.
   */
  public void unweighted( String startName )
  {
      clearAll( );

      Vertex start = vertexMap.get( startName );
      if( start == null )
          throw new NoSuchElementException( "Start vertex not found" );

      Queue<Vertex> q = new LinkedList<Vertex>( );
      q.add( start ); start.dist = 0;

      while( !q.isEmpty( ) )
      {
          Vertex v = q.remove( );

          for( Edge e : v.adj )
          {
              Vertex w = e.dest;
              if( w.dist == INFINITY )
              {
                  w.dist = v.dist + 1;
                  w.prev = v;
                  q.add( w );
              }
          }
      }
  }

   /**
    * Single-source weighted shortest-path algorithm. (Dijkstra)
    * using priority queues based on the binary heap
    */
   public boolean dijkstra( int istartName )
   {
         boolean check =false;
         String startName = Integer.toString(istartName);
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

               v.scratch = 1;
               nodesSeen++;

               for( Edge e : v.adj )
               {
                     Vertex w = e.dest;
                     double cvw = e.cost;
                     double newCost = v.dist+cvw;
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

   // public boolean findMultiple(int start, int dest){
   //    dijkstra(start);
   //    String startName = Integer.parseInt(start);
   //    Vertex start = vertexMap.get( startName );
   //
   //
   // }

  /**
   * Single-source negative-weighted shortest-path algorithm.
   * Bellman-Ford Algorithm
   */
  public void negative( String startName )
  {
      clearAll( );

      Vertex start = vertexMap.get( startName );
      if( start == null )
          throw new NoSuchElementException( "Start vertex not found" );

      Queue<Vertex> q = new LinkedList<Vertex>( );
      q.add( start ); start.dist = 0; start.scratch++;

      while( !q.isEmpty( ) )
      {
          Vertex v = q.remove( );
          if( v.scratch++ > 2 * vertexMap.size( ) )
              throw new GraphException( "Negative cycle detected" );

          for( Edge e : v.adj )
          {
              Vertex w = e.dest;
              double cvw = e.cost;

              if( w.dist > v.dist + cvw )
              {
                  w.dist = v.dist + cvw;
                  w.prev = v;
                    // Enqueue only if not already on the queue
                  if( w.scratch++ % 2 == 0 )
                      q.add( w );
                  else
                      w.scratch--;  // undo the enqueue increment
              }
          }
      }
  }

  /**
   * Single-source negative-weighted acyclic-graph shortest-path algorithm.
   */
  public void acyclic( String startName )
  {
      Vertex start = vertexMap.get( startName );
      if( start == null )
          throw new NoSuchElementException( "Start vertex not found" );

      clearAll( );
      Queue<Vertex> q = new LinkedList<Vertex>( );
      start.dist = 0;

        // Compute the indegrees
  Collection<Vertex> vertexSet = vertexMap.values( );
      for( Vertex v : vertexSet )
          for( Edge e : v.adj )
              e.dest.scratch++;

        // Enqueue vertices of indegree zero
      for( Vertex v : vertexSet )
          if( v.scratch == 0 )
              q.add( v );

      int iterations;
      for( iterations = 0; !q.isEmpty( ); iterations++ )
      {
          Vertex v = q.remove( );

          for( Edge e : v.adj )
          {
              Vertex w = e.dest;
              double cvw = e.cost;

              if( --w.scratch == 0 )
                  q.add( w );

              if( v.dist == INFINITY )
                  continue;

              if( w.dist > v.dist + cvw )
              {
                  w.dist = v.dist + cvw;
                  w.prev = v;
              }
          }
      }

      if( iterations != vertexMap.size( ) )
          throw new GraphException( "Graph has a cycle!" );
  }

	// public static void main( String [ ] args )
	// {
	// 		Graph g = new Graph();
	// 		g.addEdge("1", "4", 4);
	// 		g.addEdge("4", "3", 3);
	// 		g.addEdge("3", "1", 5);
	// 		g.addEdge("2", "3", 4);
	// 		g.addEdge("1", "2", 3);
	// 		g.victim(3);
	// 		g.dijkstra(1);
	// 		double paths = g.getVertex1(3).dist;
	// 		g.dijkstra(3);
	// 		paths = paths +g.getVertex1(1).dist;
	// 		// System.out.println(paths);
	// 		// System.out.println(g.isVictim("C"));
	// 		// System.out.println(g.isVictim("D"));
	// 		System.out.println(g.checkMultiple("1","3",paths));
	// }




  }
