import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Class DelivB does the work for deliverable DelivB of the Prog340

public class DelivB {

	File inputFile;
	File outputFile;
	PrintWriter output;
	Graph g;
	
	public DelivB( File in, Graph gr ) {
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
		
		// Get nodes in two orderings: row order (original) and column order (sorted by abbreviation)
        List<Node> row = getRowNodes();
        List<Node> col = getColNodesInHeader();
		
		//Extract keys and frequencies from nodes into arrays for algorithm
		KeysFreq kf = loadKeysAndFreq(col);

		//Run dynamic programming algorithm to compute optimal BST structure
        DPResult res = computeOptimalBST(kf);

		//Build edges based on optimal tree structure stored in root table
        makeEdges(col, res.root, 1, kf.freq.length - 1);

		// Print the adjacency matrix showing the tree structure
        printTable(row, col);
        
        
            output.close();
        
	}

	/**
	 * Gets nodes in their original order from the graph
	 * @return ArrayList of nodes in the order they appear in the input file
	 */
	private ArrayList<Node> getRowNodes(){
		return new ArrayList<>(g.getNodeList());
	}

	/**
	 * Gets nodes sorted alphabetically by abbreviation for column headers
	 * This ensures consistent ordering in the output matrix
	 * @return ArrayList of nodes sorted by abbreviation
	 */

	private ArrayList<Node> getColNodesInHeader(){
		ArrayList<Node> cols = new ArrayList<>(g.getNodeList());
		Collections.sort(cols, Comparator.comparing(Node:: getAbbrev));
		return cols;
	}

	/**
	 * Helper class to store keys (abbreviations) and frequencies together
	 */

	private static class KeysFreq{
		String[] key;
		int[] freq;

        public KeysFreq(String[] k, int[] f) {
			key = k;
			freq = f;
        }
	}

	/**
	 * Extracts keys and frequencies from node list into 1-indexed arrays
	 * This converts from object-oriented representation to array format needed by algorithm
	 * @param colNodes List of nodes sorted by abbreviation
	 * @return KeysFreq object containing parallel arrays of keys and frequencies
	 */

	private KeysFreq loadKeysAndFreq(List<Node> colNodes) {
			int n = colNodes.size();
			String[] key = new String[n + 1]; // 1-based
			int[] freq = new int[n + 1];      // 1-based
		
			for (int i = 1; i <= n; i++) {
				Node nd = colNodes.get(i - 1);
				key[i] = nd.getAbbrev();
				
				String s = nd.getVal();

				freq[i] = Integer.parseInt(s.trim());
			}
			return new KeysFreq(key, freq);
		}
	/**
	 * Helper class to store the results of dynamic programming computation
	 * dp[i][j] = minimum cost for optimal BST containing keys i through j
	 * root[i][j] = index of root node in optimal BST for keys i through j
	 */
		private class DPResult {
			int [][] dp;
			int[][] root;

			public DPResult(int[][]dp,int[][]root){
				this.dp =dp;
				this.root = root;
			}
		}
		
		/**
		 * Implements the Optimal Binary Search Tree algorithm using dynamic programming
		 * Time complexity: O(n^3) where n is the number of keys
		 * 
		 * @param kf KeysFreq object containing keys and their access frequencies
		 * @return DPResult containing cost and root tables
		 */
		private DPResult computeOptimalBST(KeysFreq kf){
			int n = kf.freq.length -1;
			int[][] dp = new int[n+2][n+2];
			int[][] root = new int[n+2][n+2];

			// prefix sums
			int[] pref = new int[n+1];
			pref[0] = 0;
			for (int i = 1; i <= n; i++) {
				pref[i] = pref[i-1] + kf.freq[i];
			}

			//Base Cases
			for (int i = 1; i <= n; i++) {
				dp[i][i] = kf.freq[i];
				root[i][i] = i;
			}
			for (int i = 1; i <= n+1; i++) {
				dp[i][i-1] = 0;
				}

			//loop
			for (int len = 2; len<= n; len++) {
				for (int i = 1; i+ len-1 <= n; i++) {
					int j = i + len -1;
					dp[i][j] = Integer.MAX_VALUE /4;
					int s = pref[j] - pref[i -1];
					for (int k = i; k<= j; k++) {
						int cost = dp[i][k-1]+dp[k+1][j] + s;
						if(cost < dp[i][j]){
							dp[i][j] = cost;
							root[i][j] = k;
						}
					}
						
					}
				}
				return new DPResult(dp,root);
			}
		
		/**
		 * Creates parent-child relationships by adding edges to the graph
		 * 
		 * @param col List of nodes sorted by abbreviation
		 * @param root Root selection table from DP 
		 * @param i Starting index of current subtree
		 * @param j Ending index of current subtree
		 */
		private void makeEdges(List<Node> col, int[][]root, int i, int j){
			if (i>j)
				return;

			int k = root[i][j];
			Node parent = col.get(k-1);

			//left child
			if(i <= k-1){
				int lk = root[i][k-1];
				Node leftChild = col.get(lk -1);
				addEdge(parent, leftChild);
				makeEdges(col, root, i, k-1);
			}

			// right child
			if( k+1 <= j){
				int rk = root[k+1][j];
				Node rightChild = col.get(rk -1);
				addEdge(parent, rightChild);
				makeEdges(col, root, k+1, j);
			}
		}

		/**
		 * Adds and update a directed edge from one node to another in the graph
		 * 
		 * @param frm The tail node (parent in the BST)
		 * @param to The head node (child in the BST)
		 */

		private void addEdge(Node frm , Node to){
			Edge e = new Edge(frm,to,1);
			g.addEdge(e);
			if(frm.getOutgoingEdges()!= null){
				frm.getOutgoingEdges().add(e);
			}

		}

		/**
		 * Prints the output table showing the optimal BST structure as an adjacency matrix
		 * @param row List of nodes in original input order
		 * @param col List of nodes sorted by abbreviation
		 */
		
		private void printTable(List<Node> row, List<Node> col) {
			// Print header
			StringBuilder header = new StringBuilder();
			header.append("~ val");
			for (Node c : col) {
				header.append(" ").append(c.getAbbrev());
			}
			printlnBoth(header.toString());
	
			// Build edge lookup set
			java.util.HashSet<String> edgeSet = new java.util.HashSet<>();
			for (Edge e : g.getEdgeList()) {
				String key = e.getTail().getAbbrev() + "->" + e.getHead().getAbbrev();
				edgeSet.add(key);
			}
	
			// Print each row
			for (Node r : row) {
				StringBuilder line = new StringBuilder();
				line.append(r.getName()).append(" ").append(r.getVal());
				
				for (Node c : col) {
					String key = r.getAbbrev() + "->" + c.getAbbrev();
					if (edgeSet.contains(key)) {
						line.append(" 1");
					} else {
						line.append(" ~");
					}
				}
				printlnBoth(line.toString());
			}
		}
	
		/**
		 * Helper method to print to both console and output file
		 * @param s String to print
		 */

		private void printlnBoth(String s) {
			System.out.println(s);
			output.println(s);
		}
	}


