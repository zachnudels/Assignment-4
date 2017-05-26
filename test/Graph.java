/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// package Graphs;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Map;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.ArrayDeque;

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
    public String     			name;   // Vertex name
    public List<Edge> 			adj;    // Adjacent vertices
    public double     			dist;   // Cost
    public Vertex     			prev;   // Previous vertex on shortest path
    public int        			scratch;// Extra variable used in algorithm
		public ArrayList<Route> routes;
		public boolean					victim=false;
		public boolean					unique =true;


    public Vertex( String nm )
      { name = nm; adj = new LinkedList<Edge>( ); reset( ); }

    public void reset( )
    //  { dist = Graph.INFINITY; prev = null; pos = null; scratch = 0; }
    { dist = Graph.INFINITY; prev = null; scratch = 0; }



   // public PairingHeap.Position<Path> pos;  // Used for dijkstra2 (Chapter 23)
}
// Each vertex has a Route
// Want a new route for every possible Route
// Every time there is more than one node, there is another Route
// If there's one node, it's the same Route
// Two nodes 	One more route for either of two nodes and for parent route
// Three nodes, then
class Route{
	// Attributes
	ArrayList<Vertex> stops;
	double cost;

	public void addStop(Vertex stop){
		stops.add(stop);
		cost=cost+stop.dist;
	}

	public void printRoute(){
		for (Vertex stop: stops){
			System.out.println(stops);
		}
	}

}

// Graph class: evaluate shortest paths.
//
// CONSTRUCTION: with no parameters.
//
// ******************PUBLIC OPERATIONS**********************
// void addEdge( String v, String w, double cvw )
//                              --> Add additional edge
// void printPath( String w )   --> Print path after alg is run
// void unweighted( String s )  --> Single-source unweighted
// void dijkstra( String s )    --> Single-source weighted
// void negative( String s )    --> Single-source negative weighted
// void acyclic( String s )     --> Single-source acyclic
// ******************ERRORS*********************************
// Some error checking is performed to make sure graph is ok,
// and to make sure graph satisfies properties needed by each
// algorithm.  Exceptions are thrown if errors are detected.

public class Graph
{
    public static final double INFINITY = Double.MAX_VALUE;
    public Map<String,Vertex> vertexMap = new HashMap<String,Vertex>( );
		public int								 size;

    /**
     * Add a new edge to the graph.
     */
    public void addEdge( String sourceName, String destName, double cost)
    {
        Vertex v = getVertex( sourceName );
        Vertex w = getVertex( destName );
				// w.victim=true;
        v.adj.add( new Edge( w, cost ) );
				this.size=vertexMap.size();
    }

		public void victim(String name){
			Vertex v = vertexMap.get( name );
			v.victim = true;
		}

		public boolean isVictim(String name){
			Vertex v = vertexMap.get(name);
				return v.victim;

		}



    /**
     * Driver routine to handle unreachables and print total cost.
     * It calls recursive routine to print shortest path to
     * destNode after a shortest path algorithm has run.
     */
    public void printPath( String destName )
    {
        Vertex w = vertexMap.get( destName );
        if( w == null )
            throw new NoSuchElementException( "Destination vertex not found" );
        else if( w.dist == INFINITY )
            System.out.println( destName + " is unreachable" );
        else
        {
            System.out.print( "(Cost is: " + w.dist + ") " );
            printPath( w );
            System.out.println( );
        }
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
    private void printPath( Vertex dest )
    {
        if( dest.prev != null )
        {
            printPath( dest.prev );
            System.out.print( " to " );
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
    public void dijkstra( String startName )
    {
        PriorityQueue<Path> pq = new PriorityQueue<Path>( );

        Vertex start = vertexMap.get( startName );
        if( start == null )
            throw new NoSuchElementException( "Start vertex not found" );

        clearAll( );
        pq.add( new Path( start, 0 ) ); start.dist=0;

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

                if( cvw < 0 )
                    throw new GraphException( "Graph has negative edges" );

                if(( w.dist > v.dist + cvw))
                {

                    w.dist = v.dist +cvw;
                    w.prev = v;
                    pq.add( new Path( w, w.dist ) );
                }
            }
        }
    }

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
                    if(  w.scratch++%2==0 ){
                        q.add( w );
											}
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

    /**
     * Process a request; return false if end of file.
     */
    public static boolean processRequest( Scanner in, Graph g )
    {
        try
        {
            System.out.print( "Enter start node:" );
            String startName = in.nextLine( );

            System.out.print( "Enter destination node:" );
            String destName = in.nextLine( );

            System.out.print( "Enter algorithm (u, d, n, a ): " );
            String alg = in.nextLine( );

            if( alg.equals( "u" ) )
                g.unweighted( startName );
            else if( alg.equals( "d" ) )
            {
                g.dijkstra( startName );
                g.printPath( destName );
            }
            else if( alg.equals( "n" ) )
                g.negative( startName );
            else if( alg.equals( "a" ) )
                g.acyclic( startName );

            g.printPath( destName );
        }
        catch( NoSuchElementException e )
          { return false; }
        catch( GraphException e )
          { System.err.println( e ); }
        return true;
    }

		public boolean doesContain(String start, String dest){
			Vertex w = vertexMap.get(start);
			System.out.println("Does contain starts");

			if(w.prev==null || w.prev.name.equals(start) ){
				System.out.println("Check");
					return false;}

			if (!w.prev.name.equals(dest)){
				System.out.println("Check2");
				doesContain(w.prev.name, dest);
			}
			if(w.prev.name.equals(dest))
				return true;

			System.out.println("check");
			return false;



		}
		public boolean checkMultiple(String startName, String victimName){
			return checkIfMultiple(startName, victimName) || checkIfMultiple(victimName, startName);
		}

		public boolean checkIfMultiple(String startName, String victimName)
		{
			boolean rtn = false;
			PriorityQueue<Path> pq = new PriorityQueue<Path>( );

			Vertex start = vertexMap.get( startName );
			if( start == null )
					throw new NoSuchElementException( "Start vertex not found" );

			clearAll( );
			pq.add( new Path( start, 0 ) ); start.dist=0;

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

							if( cvw < 0 )
									throw new GraphException( "Graph has negative edges" );
							if(w.name.equals(victimName)){
								if(w.dist==v.dist+cvw)
									w.unique=false;
								else if(w.dist>v.dist+cvw)
									w.unique=true;
							}

							if(( w.dist > v.dist + cvw))
							{

									w.dist = v.dist +cvw;
									w.prev = v;
									pq.add( new Path( w, w.dist ) );
							}


					}
			}
			if(getVertex(victimName).unique==false)
				rtn=true;
			return rtn;
	}


 		  // System.out.println(startName);
 	// 		boolean rtn = false;
 	// 		ArrayList<Double> paths = new ArrayList<Double>();
		//
 	// 			PriorityQueue<Path> pq = new PriorityQueue<Path>( );
		//
 	// 			Vertex hosp = vertexMap.get( startName );
 	// 			Vertex vic = vertexMap.get(victimName);
		//
 	// 			if( hosp == null )
 	// 					throw new NoSuchElementException( "Start vertex not found" );
		//
 	// 			clearAll( );
 	// 			pq.add( new Path( hosp, 0 ) ); hosp.dist = 0;
 	// 		 //  System.out.println("Check");
 	// 			int best = 0;
 	// 			while( !pq.isEmpty( ) )//&& nodesSeen < vertexMap.size( ) )
 	// 			{
 	// 					Path vrec = pq.remove( );
 	// 					Vertex v = vrec.dest;
 	// 					// System.out.println("Vert v: "+v.name);
 	// 				 //  if( v.scratch != 0 )  // already processed v
 	// 						 //  continue;
		//
 	// 				 //  v.scratch = 1;
 	// 				 //  nodesSeen++;
 	// 				 // System.out.println("Check");
 	// 					for( Edge e : v.adj )
 	// 					{
 	// 							Vertex w = e.dest;
 	// 							double cvw = e.cost;
		//
 	// 							if( cvw < 0 )
 	// 									throw new GraphException( "Graph has negative edges" );
		//
 	// 							if( w.dist > v.dist + cvw || w.dist ==0 || w.dist==(v.dist+cvw) )
 	// 							{
 	// 									w.dist = v.dist +cvw;
 	// 									w.prev = v;
 	// 									pq.add( new Path( w, w.dist ) );
 	// 							}
 	// 							if (w.name.equals(startName)){
 	// 								if (w.dist==(v.dist+cvw))
 	// 									w.unique=false;
 	// 								else //(w.dist>v.dist+cvw)
 	// 									w.unique=true;
 	// 								}
 	// 							// System.out.println("W: "+w.name);
 	// 							// System.out.println("w.prev: "+w.prev.name);
 	// 							// if (w.name.equals(startName) && !w.prev.prev.name.equals(w.name)){
 	// 							// 	// System.out.println("w.name is A");
 	// 							// 	 if(doesContain(startName, victimName)){
 	// 							// 	double y = v.dist+cvw;
 	// 							// 	if(y==bestPath){
 	// 							// 		best++;
 	// 							// 		System.out.println("W.path: "+w.name+w.prev.name+w.prev.prev.name+w.prev.prev.prev.name+w.prev.prev.prev.name+w.prev.prev.prev.prev.name+w.prev.prev.prev.prev.prev.name);
 	// 							// 	}
 	// 							//
 	// 							// 	}
 	// 						// }
 	// 					}
 	// 				}
		//
		//
 	// 			if (hosp.unique=false){
 	// 				System.out.println(best);
 	// 				rtn=true;
 	// 			}
 	// 		 //  System.out.println("CheckMultiple End "+rtn);
 	// 			return rtn;
 	// 		 // if (best>1){
 	// 		 // 	rtn=true;
 	// 		 // }
 	// 		 // return rtn;
 	// 	}




    /**
     * A main routine that:
     * 1. Reads a file containing edges (supplied as a command-line parameter);
     * 2. Forms the graph;
     * 3. Repeatedly prompts for two vertices and
     *    runs the shortest path algorithm.
     * The data file is a sequence of lines of the format
     *    source destination cost
     */
    public static void main( String [ ] args )
    {
        Graph g = new Graph();
				g.addEdge("A", "F", 5);
		    g.addEdge("A", "B", 3);
		    g.addEdge("B", "D", 4);
		    g.addEdge("D", "E", 5);
		    g.addEdge("F", "G", 4);
				g.addEdge("G","E",3);
				g.addEdge("E","A",4);
				g.dijkstra("A");
				System.out.println(g.getVertex("E").dist);


				// System.out.println(g.isVictim("C"));
				// System.out.println(g.isVictim("D"));
				if (g.checkMultiple("A", "E")){
				System.out.println("multiple solutions");
					g.dijkstra("A");
					Double path = g.getVertex("E").dist;
					g.dijkstra("E");
					path = path+g.getVertex("A").dist;
					System.out.println("multiple solutions cost "+path);
					return;
				}
    }
}
