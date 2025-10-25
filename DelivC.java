import java.io.File;
import java.io.PrintWriter;
import java.util.*;

// Class DelivC does the work for deliverable DelivC of the Prog340

public class DelivC {

	File inputFile;
	File outputFile;
	PrintWriter output;
	Graph g;
	Graph gh;
	
	public DelivC( File in, Graph gr, Graph grh ) {
		inputFile = in;
		g = gr;
		gh = grh;
		
		// Get output file name.
		String inputFileName = inputFile.toString();
		String baseFileName = inputFileName.substring( 0, inputFileName.length()-4 ); // Strip off ".txt"
		String outputFileName = baseFileName.concat( "_out.txt" );
		outputFile = new File( outputFileName );
		if ( outputFile.exists() ) {    // For retests
			outputFile.delete();
		}
		
		try {
			output = new PrintWriter(outputFile);			
		}
		catch (Exception x ) { 
			System.err.format("Exception: %s%n", x);
			System.exit(0);
		}
		
	}

	private Node getNodebyVAl(String targetVal, ArrayList<Node> nodes){
		// loop
		for(Node node: nodes){
			String nodeVal = node.getVal();

			if(nodeVal != null && nodeVal.equals(targetVal)){
				return node;
			}
		}
		return null;
	}

	private int getActualDistance(Node fromCity, Node toCity){
		ArrayList<Edge> outgoing = fromCity.getOutgoingEdges();

		for(Edge edge: outgoing){
			if(edge.getHead().equals(toCity)){
				return edge.getDist();
			}
		}
		return 0;
	}

	private int getHeuristic(Node fromCity, Node goalCity){
		ArrayList<Edge> outgoing = fromCity.getOutgoingEdges();

		for(Edge edge: outgoing){
			if(edge.getHead().equals(goalCity)){
				return edge.getDist();
			}
		}
		return 0;
	}

	/**
	 * Represents one possible path from start to some city
	 * Stores the sequence of cities visited and costs
	 */
	private class Path implements Comparable<Path>{
		ArrayList<Node> nodes;
		int d;
		int h;
		int f;

		/**
		* Constructor
		* @param pathNodes - List of nodes in this path
		* @param actualDist - Real distance we've traveled
		* @param heuristic - Estimated distance to goal
		*/

		public Path(ArrayList<Node> pathNodes, int actualDist, int heuristic){
			this.nodes = new ArrayList<>(pathNodes);
			this.d = actualDist;
			this.h = heuristic;
			this.f = actualDist + heuristic;

		}
		/**
    	 * Compare paths by f-value
    	 * Lower f-value = higher priority (explored first)
    	 */
		@Override
		public int compareTo(Path other){
			return Integer.compare(this.f, other.f);
		}

		/**
    	 * Get last node in path (current city)
    	 */
		public Node getLastNode(){
			return nodes.get(nodes.size()-1);
		}

		/**
    	 * Converting path to string
		*/
		public String getPathString(){
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < nodes.size(); i++) {
				if(i > 0) sb.append("-");
				sb.append(nodes.get(i).getAbbrev());
			}
			return sb.toString();
		}
	}

		// A* Search Algorithm

		private void AStarSearch(Node StartNode, Node goalNode, ArrayList<Node> allNodes){
			
		}

}


