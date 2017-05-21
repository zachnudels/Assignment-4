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
      public boolean    hospital;  //Hospital or not

      public Vertex( String nm )
        { name = nm; adj = new LinkedList<Edge>( ); reset( ); }

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


	/**
	 * Driver routine to handle unreachables and print total cost.
	 * It calls recursive routine to print shortest path to
	 * destNode after a shortest path algorithm has run.
	 */
	public void printPath( int dest, int source )
	{
			String destName = Integer.toString(dest);
			String sourceName = Integer.toString(source);
			dijkstra(source);
			Vertex w = vertexMap.get( destName );
			if( w == null )
					throw new NoSuchElementException( "Destination vertex not found" );
			else if( w.dist == INFINITY )
					System.out.println( destName + " is unreachable" );
			else
			{
					// System.out.print( "(Cost is: " + w.dist + ") " );
					printDestPath( w );
					// System.out.println( );
			}
			dijkstra(dest);
			Vertex v = vertexMap.get( sourceName );
			if( w == null )
					throw new NoSuchElementException( "Source vertex not found" );
			else if( v.dist == INFINITY )
					System.out.println( destName + " is unreachable" );
			else
			{
					// System.out.print( "(Cost is: " + v.dist + ") " );
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
			System.out.print(" ");
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
	public void dijkstra( int istartName )
	{
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

							if( cvw < 0 )
									throw new GraphException( "Graph has negative edges" );

							if( w.dist > v.dist + cvw )
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



  }
