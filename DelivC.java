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

		ArrayList<Node> nodes = new ArrayList<>(g.getNodeList());

		//finding start and goal nodes
		Node startNode = getNodebyVAl("S", nodes);
		Node goalNode = getNodebyVAl("G", nodes);

		if(startNode == null || goalNode == null){
			System.out.println("Error: Start or Goal node not found!");
			output.println("Error: Start or Goal node not found!");
			output.close();
			return;
		}
		AStarSearch(startNode, goalNode, nodes);

		output.close();
	}

	/**
	 * Finds a node by its value field (looking for "S" or "G")
	 * @param val The value to search for
	 * @param nodeList List of all nodes
	 * @return The node with matching value, or null if not found
	 */
	private Node getNodebyVAl(String val, ArrayList<Node> nodeList){
		// loop
		for(Node node: nodeList){
			if (val.equals(node.getVal())) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Gets the actual distance from one node to another
	 * @param from Starting node
	 * @param to Destination node
	 * @return Actual distance, or 0 if no edge found
	 */

	private int getActualDistance(Node fromCity, Node toCity){
		ArrayList<Edge> outgoing = fromCity.getOutgoingEdges();
		for(Edge edge: outgoing){
			if(edge.getHead().equals(toCity)){
				return edge.getDist();
			}
		}
		return 0;
	}


	/**
	 * Gets the heuristic distance from one node to another
	 * @param from Starting node
	 * @param to Destination node
	 * @return Heuristic distance, or 0 if no edge found
	 */
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

	/**
     * Prints a line to BOTH console (System.out) AND output file
     */
	private void printlnBoth(String s) {
        System.out.println(s);
        output.println(s);
    }
	
	
    private void printQueueStatus(PriorityQueue<Path> queue) {
        // Convert queue to list (can't iterate queue directly)
        List<Path> pathsList = new ArrayList<>(queue);
        
        // Sort by f-value
        Collections.sort(pathsList);
        
        // Print each path with formatted columns
        for (Path path : pathsList) {
            String pathStr = path.getPathString();
            String output = String.format("%-20s %-8d %-8d %-8d", 
                pathStr, path.d, path.h, path.f);
            printlnBoth(output);
        }
    }

		/**
		 * Performs A* search from start to goal
		 * @param startNode The starting city
		 * @param goalNode The goal city
		 * @param allNodes List of all nodes in the graph
		 */

		private void AStarSearch(Node startNode, Node goalNode, ArrayList<Node> allNodes){
			PriorityQueue<Path> openQueue = new PriorityQueue<>();
			Set<Node> closedSet = new HashSet<>();
			Map<Node,Integer> bestDistTo = new HashMap<>();

			//Creating initial path
			ArrayList<Node> startPath = new ArrayList<>();
			startPath.add(startNode);

			//calculate heuristic from start to goal
			int startHeuristic = getHeuristic(startNode, goalNode);

			//creating first path and add to queue
			Path initialPath = new Path(startPath,0,startHeuristic);
			openQueue.add(initialPath);
			bestDistTo.put(startNode, 0);

			// printing output
			String header = "Shortest Path from " + startNode.getName() + 
			" (" + startNode.getAbbrev() + 
			") to " + goalNode.getName() + 
			" (" + goalNode.getAbbrev() + ")";
			printlnBoth(header);
			printlnBoth("");
			printlnBoth(String.format("%-20s %-8s %-8s %-8s","PATH", "DIST", "HEUR", "F-VALUE"));
			printlnBoth("");
			printQueueStatus(openQueue);
		
			//main loop
			while(!openQueue.isEmpty()){
				Path currentPath = openQueue.poll();
				Node lastNode = currentPath.getLastNode();

				if(lastNode.equals(goalNode)){
					printlnBoth("");
					String pathStr = currentPath.getPathString();
					String output = String.format("%-20s %-8d", pathStr, currentPath.d);
					printlnBoth(output);
					return;
				}

				if(closedSet.contains(lastNode)){
					continue;
				}

				closedSet.add(lastNode);

				//looking at neighbors
				ArrayList<Edge> outgoing = lastNode.getOutgoingEdges();
				for (Edge edge: outgoing){
					Node neighbor = edge.getHead();
					int edgeDist = edge.getDist();
					int distToNeighbor = currentPath.d + edgeDist;

					if (!bestDistTo.containsKey(neighbor) || distToNeighbor < bestDistTo.get(neighbor)) {
						bestDistTo.put(neighbor, distToNeighbor);
						ArrayList newPathNodes = new ArrayList<>(currentPath.nodes);
						newPathNodes.add(neighbor);

						// Get heuristic distance from neighbor to goal
						int heuristic = getHeuristic(neighbor, goalNode);
						Path newPath = new Path(newPathNodes, distToNeighbor, heuristic);
						openQueue.add(newPath);
					}
				}
				if (!openQueue.isEmpty()) {
						printlnBoth("");
						printQueueStatus(openQueue);
					}
				}
				
				// If we get here, no path was found
				printlnBoth("No path found from " + startNode.getAbbrev() + " to " + goalNode.getAbbrev());
	}
}
	
		





