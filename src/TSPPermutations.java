
import edu.uci.ics.jung.graph.Graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;


class TSPPermutations extends TSPAlg {

	/**
	 *  A iterator over all permutations of cities (except
	 *  the first city which is "locked in").
	 */
	Iterator<List<City>> permutationItr;
	
	/**
	 *  The current permutation being examined by the algorithm.
	 */
	List<City> currentPermutation = null;
	
	/**
	 *  {@inheritDoc}
	 */
	public void reset(Graph<City, Flight> graph) {
		super.reset(graph);
		permutationItr = null;
		currentPermutation = null;
	}
	
	/**
	 *  {@inheritDoc}
	 */
	public void start() {
		super.start();
	}
	
	/**
	 *  {@inheritDoc}
	 */
	public void finish() {
		System.out.println("TSP Via Permutation:");
		if(visitOrder != null) {
			this.visitOrder.add(0, this.startingCity);
		}
		super.finish();
	}

	/**
	 *  Get the next permutation. Note that a permutation does
	 *  NOT include the starting city since this should be "locked
	 *  in place". If you include the starting city in the permuations
	 *  you end up examining "rotations" of trips you have already
	 *  looked at.
	 *  
	 *  @return the next permutation of cities (other than the starting city)
	 */
	public List<City> permutate() {


		if(permutationItr == null){
			Collection<City> listOfGraphs = graph.getVertices();
			Collection<City> k = new ArrayList<>();
			for (City curCity : listOfGraphs) {
				if (curCity != startingCity) {
					k.add(curCity);
				}
			}
			permutationItr = new PermutationIterator<>(k);

		}

		if(permutationItr.hasNext()){
			List<City> cur = permutationItr.next();
			return cur;
		}else{
			return null;
		}




	}
	
	/**
	 *  {@inheritDoc}
	 */
	public boolean setupNextStep() {
		//YOUR CODE HERE

		List<City> cur = permutate();
		if(cur == null){
			return false;
		}else{
			currentPermutation = cur;
			return true;
		}

	}
	
	/**
	 *  {@inheritDoc}
	 */
	public void doNextStep() {


		int cost = 0;

		//Start city addition cost (to first city in List):
		GraphEdge currEdge = graph.findEdge(startingCity, currentPermutation.get(0));
		currEdge.setColor(COLOR_ACTIVE_EDGE);
		cost = currEdge.cost;



		for(int i = 0; i < currentPermutation.size()-1; i++){
			currEdge = graph.findEdge(currentPermutation.get(i), currentPermutation.get(i+1));
			currEdge.setColor(COLOR_ACTIVE_EDGE);
			cost = cost + currEdge.cost;
		}

		//Final city addition cost (to starting city):
		currEdge = graph.findEdge(currentPermutation.get(currentPermutation.size()-1), startingCity);
		currEdge.setColor(COLOR_ACTIVE_EDGE);
		cost = cost + currEdge.cost;

		if(cost < visitOrderCost){
			visitOrderCost = cost;
			visitOrder = currentPermutation;
		}

		return;





	}
}
