package zhongxing;

import java.util.Iterator;


public class EdgeWeightedGraph {

	private int V;
	private int E;
	private Bag<Edge>[] adj;

	public EdgeWeightedGraph(int V) {
		if (V < 0)
			throw new IllegalArgumentException("Number of vertices must be nonnegative");
		this.V = V;
		this.E = 0;
		adj = (Bag<Edge>[]) new Bag[V];
		for (int v = 0; v < V; v++) {
			adj[v] = new Bag<Edge>();
		}
	}

	public EdgeWeightedGraph(In in) {
		this(in.readInt());
		int E = in.readInt();
		if (E < 0)
			throw new IllegalArgumentException("Number of edges must be nonnegative");
		for (int i = 0; i < E; i++) {
			int v = in.readInt();
			int w = in.readInt();
			double weight = in.readDouble();
			Edge e = new Edge(v, w, weight);
			addEdge(e);
		}
	}
	
	public EdgeWeightedGraph(In in,double value) {
		this(in.readInt());
		int E = in.readInt();
		if (E < 0)
			throw new IllegalArgumentException("Number of edges must be nonnegative");
		for (int i = 0; i < E; i++) {
			int v = in.readInt();
			int w = in.readInt();
			double weight = in.readDouble();
			Edge e = new Edge(v, w, value);
			addEdge(e);
		}
	}

	public int V() {
		return V;
	}

	public int E() {
		return E;
	}

	public void addEdge(Edge e) {
		int v = e.either();
		int w = e.other(v);
		adj[v].add(e);
		adj[w].add(e);
		E++;
	}

	public void deleteEdge(int v, int x) {
		Bag<Edge> bag = adj[v];
		Iterator<Edge> it = bag.iterator();
		while (it.hasNext()) {
			Edge e = it.next();
			int w  = e.other(v);
			if (w == x) {
				bag.detele(e);
				adj[v] = bag;
				E--;
				break;
			}
		}
	}
	
	public void editEdge(int v,int w,double weight){
		Bag<Edge> bag = adj[v];
		Iterator<Edge> it = bag.iterator();
		while (it.hasNext()) {
			Edge e = it.next();
			int x  = e.other(v);
			if (x == w) {
				e.editWeight(weight);
			}
		}
	}
	
	public Edge findEdge(int v,int w){
		Bag<Edge> bag = adj[v];
		Iterator<Edge> it = bag.iterator();
		while (it.hasNext()) {
			Edge e = it.next();
			int x  = e.other(v);
			if (x == w) {
				return e;
			}
		}
		return null;
	}

	public Iterable<Edge> adj(int v) {
		return adj[v];
	}

	public Iterable<Edge> edges() {
		Bag<Edge> list = new Bag<Edge>();
		for (int v = 0; v < V; v++) {
			int selfLoops = 0;
			for (Edge e : adj(v)) {
				if (e.other(v) > v) {
					list.add(e);
				} else if (e.other(v) == v) {
					if (selfLoops % 2 == 0)
						list.add(e);
					selfLoops++;
				}
			}
		}
		return list;
	}
		
}