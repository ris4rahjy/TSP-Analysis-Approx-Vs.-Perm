
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.algorithms.shortestpath.MinimumSpanningForest;

import java.awt.Color;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


class TSPApprox extends TSPAlg {

	/**
	 *  The step number for doing a MST.
	 */
	private static final int STEP_MST = 1;
	
	/**
	 *  The step number for starting the DFT.
	 */
	private static final int STEP_DFT = 2;
	
	/**
	 *  The step number for finishing.
	 *  NOTE: this one gets changed!
	 */
	private int stepDone = 3;
	
	/**
	 *  The current step.
	 */
	private int step = 0;
	
	/**
	 *  The MST found with Prim's algorithm.
	 */
	Forest<City,Flight> tree = null;
	
	/**
	 *  {@inheritDoc}
	 */
	public void reset(Graph<City, Flight> graph) {
		super.reset(graph);
		
		//reset steps
		step = 0;
	}
	
	/**
	 *  {@inheritDoc}
	 */
	public void start() {
		super.start();
		
		//calculate how many steps of the DFT
		//will be displayed.
		stepDone = STEP_DFT + graph.getVertexCount();
	}
	
	/**
	 *  {@inheritDoc}
	 */
	public void finish() {
		System.out.println("TSP Via Approximation:");
		super.finish();
	}
	
	/**
	 *  {@inheritDoc}
	 */
	public boolean setupNextStep() {
		step++;
		return (step != stepDone);
	}
	
	/**
	 *  Helper method to color all edges of a tree.
	 *  
	 *  @param tree the tree to highlight on the graph
	 *  @param c the color to use
	 */
	protected void colorAllEdges(Forest<City,Flight> tree, Color c) {
		for(City c1: tree.getVertices()) {
			for(City c2: tree.getVertices()) {
				if(tree.isNeighbor(c1, c2)) {
					Flight f = graph.findEdge(c1, c2);
					f.setColor(c);
				}
			}
		}
	}
	
	/**
	 *  Helper method to print out one step of the DFT at a time
	 *  to make the simulation more interesting.
	 */
	protected void stepDisplayDFT() {
		colorAllEdges(COLOR_DONE_EDGE_2);
		colorAllEdges(tree, COLOR_ACTIVE_EDGE);
		
		for(int i = 0; i < step-STEP_DFT; i++) {
			Flight f = null;
			
			if(i == stepDone-1) {
				f = graph.findEdge(visitOrder.get(i), this.startingCity);
			}
			else {
				f = graph.findEdge(visitOrder.get(i), visitOrder.get(i+1));
			}
			
			f.setColor(COLOR_DONE_EDGE_1);
		}
	}
	

	/**
	 *  {@inheritDoc}`
	 */
	public void doNextStep() {


		if(step == STEP_MST){
			DelegateForest<City, Flight> dForest = new DelegateForest<>();
			HashMap<Flight, Double> hash = new HashMap<>();
			Collection<Flight> flights = graph.getEdges();
			colorAllEdges(COLOR_ACTIVE_EDGE);
			for(Flight currentFlight : flights){
				hash.put(currentFlight, (double) currentFlight.cost);
			}
			MinimumSpanningForest<City, Flight> primsMST = new MinimumSpanningForest<City, Flight>(graph, dForest, startingCity, hash);
			tree = primsMST.getForest();
			return;
		}

		if(step == STEP_DFT){
			List<City> visitingOrder = new ArrayList<>();
			visitingOrder.add(startingCity);
			depthFirstTraversal(tree, startingCity, visitingOrder);
			visitOrder = visitingOrder;
			GraphEdge currEdge;
			int sizeOfList = visitingOrder.size()-1;
			visitOrderCost = 0;


			for(int i = 0; i < visitingOrder.size()-1; i++){
				currEdge = graph.findEdge(visitingOrder.get(i), visitingOrder.get(i+1));
				visitOrderCost = visitOrderCost + currEdge.cost;
			}

			currEdge = graph.findEdge(visitingOrder.get(sizeOfList), startingCity);

			visitOrderCost = visitOrderCost + currEdge.cost;

		}


		if(step >= STEP_DFT) {
			stepDisplayDFT();
		}

		return;

	}
	
	static void depthFirstTraversal(Forest<City,Flight> treeToWalk, City currNode, List<City> dftVisitOrder) {

		//Base Case:
		if(currNode == null){
			return;
		}

		Collection<City> colOfCities = treeToWalk.getChildren(currNode);

		for(City t : colOfCities){
			if(!dftVisitOrder.contains(t)){
				dftVisitOrder.add(t);
				depthFirstTraversal(treeToWalk, t, dftVisitOrder);
			}
		}





	}
}
