import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


// Class DelivA does the work for deliverable DelivA of the Prog340

public class DelivA {

	File inputFile;
	File outputFile;
	PrintWriter output;
	Graph g;
	
	public DelivA( File in, Graph gr ) {
		inputFile = in;
		g = gr;
		
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
		
	// Get all nodes from the graph 
		ArrayList<Node> allNodes = g.getNodeList();

// Sort nodes by their values in ascending order (1, 2, 3, ...)
// Use Collections.sort with custom Comparator for Node objects
		Collections.sort(allNodes, new Comparator<Node>(){
			@Override
			public int compare(Node node1, Node node2){
				// Convert string values to integers for proper numeric comparison
				int valueInt1 = Integer.parseInt(node1.getVal());
				int valueInt2 = Integer.parseInt(node2.getVal());

				return Integer.compare(valueInt1, valueInt2);
			}
		});

		// Initialize path builder and total distance counter
		StringBuilder pathBuilder = new StringBuilder("Path ");
		int totalDistance = 0;
		
		// Build the Hamiltonian cycle path and calculate total distance
		for (int i = 0; i < allNodes.size(); i++) {
			Node currentNode = allNodes.get(i);
			// Add current node abbreviation to the path
			pathBuilder.append(currentNode.getAbbrev());
			pathBuilder.append(" ");

			// Determine the next node in the cycle
			Node nextNode;
			if(i == allNodes.size()-1){
				nextNode = allNodes.get(0);  // Return to starting node (complete the cycle)
			}
			else{
				nextNode = allNodes.get(i +1); // Move to next node in sequence
			}
			// Calculate distance between current and next node, add to total
			int distance = findDistanceBetweenNodes(currentNode,nextNode);
			totalDistance += distance;
		}
	// Complete the path string with return node and total distance, printing it .
		pathBuilder.append(allNodes.get(0).getAbbrev());
		String result = pathBuilder + " has distance " + totalDistance + ".";
		System.out.println(result);
		output.println(result);
		

		

		output.flush();
	}

	/**
	 * Finds the distance between two nodes by searching through outgoing edges
	 * @param currentNode The starting node
	 * @param nextNode The destination node
	 * @return The distance between the two nodes, or 0 if no edge found
	 */
	private int findDistanceBetweenNodes(Node currentNode,Node nextNode){
	ArrayList<Edge> outgoinEdges = currentNode.getOutgoingEdges();
	
	for (Edge edge : outgoinEdges){
		if(edge.getHead().equals(nextNode)){
			return edge.getDist();
		}

		
	}
	return 0;
	}
}
