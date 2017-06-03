package zhongxing;

public class Edge implements Comparable<Edge> {

	private final int v;
	private final int w;
	private double weight;

	public Edge(int v, int w, double weight) {
		this.v = v;
		this.w = w;
		this.weight = weight;
	}

	public double weight() {
		return weight;
	}

	public int either() {
		return v;
	}

	public int other(int vertex) {
		if (vertex == v)
			return w;
		else if (vertex == w)
			return v;
		else
			throw new IllegalArgumentException("Illegal endpoint");
	}

	public int compareTo(Edge that) {
		return Double.compare(this.weight, that.weight);
	}
	
	public void editWeight(double weight){
		this.weight = weight;
	}


	public boolean sameAS(Object that){
		Edge edge = (Edge) that;
		return (this.v == edge.v && this.w == edge.w)
				|| (this.v == edge.w && this.w == edge.v);
	}
	
	
	public String toString() {
		return String.format("%d-%d %.5f", v, w, weight);
	}

}
