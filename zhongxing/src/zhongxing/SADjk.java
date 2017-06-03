package zhongxing;

import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

public class SADjk {

	private int[] edgeTo;
	private double[] distTo;
	private IndexMinPQ<Double> pq;// 最小索引

	private EdgeWeightedGraph G; // 权重图
	private Bag<Edge>[] enemy;// 避免路径
	private Queue<Integer> minStepPath;// 最短步数路径
	private int[] minStepFruit;// 最短步数对应的必过点访问顺序
	private Queue<Integer> minCostPath;// 最短花费路径
	private int[] minCostFruit;// 最小花费对应的必过点访问顺序
	private int limitedStep; // 允许的最大步数
	private int[] MPVertex;//必过点
	private Bag<Edge> MPEdge;//必过边
	private Bag<Edge> FEdge;//禁止边
	private int start; // 起始节点
	private int end; // 终止节点
	private boolean isWeighted; // 是否加权重
	private Queue<Integer>[][] unweightedXToY;
	private Queue<Integer>[][] weightedXToY;
	

	// 初始话无权重图(所有边权重都为1)
	public void initEdgeGraph() {
		G = new EdgeWeightedGraph(new In(this.getClass().getClassLoader().getResource("zhongxing/case1.txt")), 1);
		isWeighted = false;
	}

	// 初始话权重图
	public void initEdgeWeightedGraph() {
		G = new EdgeWeightedGraph(new In(this.getClass().getClassLoader().getResource("zhongxing/case1.txt")));
		isWeighted = true;
	}
	
	// 初始化限制条件
	public void init(){
			In sc = new In(this.getClass().getClassLoader().getResource("zhongxing/limit1.txt"));
			//起点和终点
			String[] se = sc.readLine().split("\\s");
			start = Integer.parseInt(se[0]);
			end = Integer.parseInt(se[1]);
			sc.readLine();
			//步数限制
			limitedStep = Integer.parseInt(sc.readLine());
			sc.readLine();
			//必过点
			int MPVLength = Integer.parseInt(sc.readLine());
			MPVertex = new int[MPVLength];
			String[] mpv = sc.readLine().split("\\s");
			for (int i = 0; i < MPVLength; i++) {
				MPVertex[i] = Integer.parseInt(mpv[i]);
			}
			sc.readLine();
			//必过边
			int MPELength = Integer.parseInt(sc.readLine());
			MPEdge = new Bag<Edge>();
			for (int i = 0; i < MPELength; i++) {
				String[] edge = sc.readLine().split("\\s");
				int v = Integer.parseInt(edge[0]);
				int w = Integer.parseInt(edge[1]);
				double weight = Double.parseDouble(edge[2]);
				MPEdge.add(new Edge(v,w,weight));
			}
			sc.readLine();
			//障碍边
			int FELength = Integer.parseInt(sc.readLine());
			FEdge = new Bag<Edge>();
			for (int i = 0; i < FELength; i++) {
				String[] edge = sc.readLine().split("\\s");
				int v = Integer.parseInt(edge[0]);
				int w = Integer.parseInt(edge[1]);
				double weight = Double.parseDouble(edge[2]);
				FEdge.add(new Edge(v,w,weight));
			}
			sc.close();
			
	}

	// 初始化敌人所在的边(禁止通过的边)
	public void initEnemy() {
		enemy = (Bag<Edge>[]) new Bag[G.V()];
		for (int i = 0; i < enemy.length; i++) {
			enemy[i] = new Bag<Edge>();
		}
		Iterator<Edge> it = FEdge.iterator();
		while(it.hasNext()){
			Edge e = it.next();
			int v = e.either();
			int w = e.other(v);
			enemy[v].add(e);
			enemy[w].add(e);
		}
	}


	// 初始化必过点和必过边的顶点
	public int[] initFruit() {
		Queue<Integer> q = new Queue<Integer>();
		Iterator<Edge> it = MPEdge.iterator();
		while(it.hasNext()){
			Edge e = it.next();
			int v = e.either();
			int w = e.other(v);
			q.enqueue(v);
			q.enqueue(w);
		}
		for (int i = 0; i < MPVertex.length; i++) {
			if(!q.contains(MPVertex[i])){
				//必过点进两次
				q.enqueue(MPVertex[i]);
				q.enqueue(MPVertex[i]);
			}
		}
		int[] fruit = new int[q.size()];
		for (int i = 0; i < fruit.length; i++) {
			fruit[i] = q.dequeue();
		}
		
		return fruit;
	}

	//最短路径树索引Dijkstra
	public void dijkstraSP(int s, int e) {
		edgeTo = new int[G.V()];
		distTo = new double[G.V()];
		pq = new IndexMinPQ<Double>(G.V());
		for (int v = 0; v < G.V(); v++) {
			distTo[v] = Double.POSITIVE_INFINITY;
		}
		distTo[s] = 0.0;
		pq.insert(s, 0.0);
		while (pq.minIndex() != e) {
			relax(pq.delMin());
		}
	}

	//松弛操作
	private void relax(int v) {
		for (Edge e : G.adj(v)) {
			//跳过障碍边
			if (enemy[v].contains(e))
				continue;
			int w = e.other(v);
			if (distTo[w] > distTo[v] + e.weight()) {
				distTo[w] = distTo[v] + e.weight();
				edgeTo[w] = v;
				if (pq.contains(w))
					pq.changeKey(w, distTo[w]);
				else
					pq.insert(w, distTo[w]);
			}
		}
	}

	//s到v的距离
	public double distTo(int v) {
		return distTo[v];
	}
	
	//s到v是否可达
	public boolean hasPathTo(int v) {
		return distTo[v] < Double.POSITIVE_INFINITY;
	}

	// 返回s到v的路径
	public Iterable<Integer> pathTo(int s, int v) {
		if (!hasPathTo(v))
			return null;
		Stack<Integer> path = new Stack<Integer>();
		for (int x = v; x != s; x = edgeTo[x]) {
			path.push(x);
		}
		path.push(s);
		return path;
	}

	// 返回s到v的步数
	public int stepTo(int s, int v) {
		if (!hasPathTo(v))
			return Integer.MAX_VALUE;
		int step = 0;
		for (int x = v; x != s; x = edgeTo[x]) {
			step++;
		}
		step++;
		return step;
	}


	// 返回某必过点访问顺序下的最小花费
	public double getCost(Queue<Integer> path) {
		// 初始化花费(设置为必过边的权重和)
		double cost = 0.0;
		Iterator<Integer> it = path.iterator();
		int from = it.next();
		int to;
		while (it.hasNext()) {
			to = it.next();
			cost += G.findEdge(from, to).weight();
			from = to;
		}

		return cost;
	}

	// 复制路径
	public Queue<Integer> copyPath(Queue<Integer> paths) {
		Queue<Integer> temp = new Queue<Integer>();
		Iterator<Integer> it = paths.iterator();
		while (it.hasNext()) {
			temp.enqueue(it.next());
		}
		return temp;
	}

	// 复制最优必过点访问顺序
	public int[] copyFruit(int[] fruit) {
		int[] minFruit = new int[fruit.length];
		for (int i = 0; i < fruit.length; i++) {
			minFruit[i] = fruit[i];
		}
		return minFruit;
	}

	// 从路径中获取必过点访问顺序
	public int[] getFruit(Queue<Integer> path, int[] fruit) {
		int[] minFruit = new int[fruit.length];
		Iterator<Integer> it = path.iterator();
		for (int i = 0; i < minFruit.length;) {
			int one = it.next();
			while (it.hasNext()) {
				int two = it.next();
				if (!contains(minFruit,one,two) && contains(fruit, one, two)) {
					minFruit[i] = one;
					minFruit[i+1] = two;
					i +=2 ;
					
				}
				if(!contains(minFruit,one) && contains(fruit, one)){
					minFruit[i] = one;
					minFruit[i+1] = one;
					i +=2 ;
				}
				one = two;
			}
		}
		return minFruit;
	}

	//必过点数组中是否包含某顶点
	public boolean contains(int[] fruit, int vertex) {
		for (int i = 0; i < fruit.length-1; i+=2) {
			if (fruit[i] == vertex && fruit[i+1] == vertex) {
				return true;
			}
		}
		return false;
	}
	//必过点数组中是否包含某必过边
	public boolean contains(int[] fruit, int one, int two) {
		for (int i = 0; i < fruit.length-1; i+=2) {
			if (fruit[i] == one && fruit[i+1]==two) {
				return true;
			}
			if(fruit[i] == two && fruit[i+1]==one){
				return true;
			}
		}
		return false;
	}
	

	// 获取新的节点位置
	public int[] getNewFruit(int[] oldPath) {
		int length = oldPath.length;
		int[] newPath = new int[length];
		int o = new Random().nextInt(length/2);
		int p = new Random().nextInt(length/2);
		if (o == p)
			p = new Random().nextInt(length/2);
		int q = new Random().nextInt(length/2);
		if (q == o || q == p)
			q = new Random().nextInt(length/2);
		int temp = p;
		p = p > o ? p : o;
		o = p == temp ? o : temp;
		temp = q;
		q = q > p ? q : p;
		p = q == temp ? p : temp;
		temp = p;
		p = p > o ? p : o;
		o = p == temp ? o : temp;
		// System.out.println("o:"+o+" p:"+p+" q:"+q);
		int oo = 2*o;
		int qq = 2*(q+1);
		int[] x = new int[length + 2*( p - o + 1 )];
		for (int i = 0; i < x.length; i++) {
			if (i < 2*(q+1))
				x[i] = oldPath[i];
			else if (i < 2*(q + p - o + 2)) {
				x[i] = oldPath[oo++];
			} else {
				x[i] = oldPath[qq++];
			}
		}
		for (int i = 0; i < length; i++) {
			if (i < 2 * o) {
				newPath[i] = x[i];
			} else {
				newPath[i] = x[i + 2*(p - o + 1)];
			}
		}
		for (int i = 0; i < length/2; i++) {
			if(Math.random()>=0.5){
				int t = newPath[2*i];
				newPath[2*i]= newPath[2*i+1];
				newPath[2*i+1] = t;
			}
		}
		return newPath;
	}

	//任意两必过点之间的路径
	public Queue<Integer>[][] xToy(int[] fruit){
		Queue<Integer>[][] xToy = (Queue<Integer>[][])new Queue[G.V()][G.V()];
		for (int i = 0; i < xToy.length; i++) {
			for (int j = 0; j < xToy[i].length; j++) {
				xToy[i][j] = new Queue<Integer>();
			}
		}
		
		//初始化必过边之间路径
		Iterator<Edge> it = MPEdge.iterator();
		while(it.hasNext()){
			Edge e = it.next();
			int v = e.either();
			int w = e.other(v);
			xToy[v][w].enqueue(v);
			xToy[v][w].enqueue(w);
			xToy[w][v].enqueue(w);
			xToy[w][v].enqueue(v);
		}
		
		
		//初始化起点到任意必过点之间路径
		for (int i = 0; i < fruit.length; i++) {
			if(xToy[start][fruit[i]].isEmpty()){
				dijkstraSP(start,fruit[i]);
				Iterator<Integer> it1 = pathTo(start,fruit[i]).iterator();
				while (it1.hasNext()) {
					int vertex = it1.next();
					xToy[start][fruit[i]].enqueue(vertex);
				}
			}
		}
		
		//初始化任意必过点到终点之间路径
		for (int i = 0; i < fruit.length; i++) {
			if(xToy[fruit[i]][end].isEmpty()){
				dijkstraSP(fruit[i],end);
				Iterator<Integer> it1 = pathTo(fruit[i],end).iterator();
				while (it1.hasNext()) {
					int vertex = it1.next();
					xToy[fruit[i]][end].enqueue(vertex);
				}
			}
		}
		
		//初始化任意两必过点之间路径
		for (int i = 0; i < fruit.length; i++) {
			for (int j = 0; j < fruit.length; j++) {
				if(xToy[fruit[i]][fruit[j]].isEmpty()){
					dijkstraSP(fruit[i],fruit[j]);
					Iterator<Integer> it1 = pathTo(fruit[i],fruit[j]).iterator();
					while (it1.hasNext()) {
						int vertex = it1.next();
						xToy[fruit[i]][fruit[j]].enqueue(vertex);
					}
				}
			}
		}
	
		return xToy;
	} 
	
	public Queue<Integer> getPaths(int[] fruit, Queue<Integer>[][] xToy){	
		Queue<Integer> path = new Queue<Integer>();
		path.enqueue(start);
		for (int i = 0; i < fruit.length + 1; i++) {
			Iterator<Integer> it = xToy[i == 0 ? start : fruit[i - 1]][i == fruit.length ? end : fruit[i]].iterator();
			it.next();
			while (it.hasNext()) {
				path.enqueue(it.next());
			}
		}
		return path;
	}
	
	
	// 退火算法寻找经过必过点的最少步数的路径
	public int[] anneal1() {

		int[] fruit = initFruit(); // 节点位置部署
		unweightedXToY = xToy(fruit);
		int[] minFruit = copyFruit(fruit);
		Queue<Integer> paths = getPaths(fruit,unweightedXToY);
		minStepPath = copyPath(paths);
		minFruit = getFruit(paths,fruit);
		int step = paths.size();
		int minStep = step;
		double temperature = 0.01;
		if(fruit.length/2<10){
			temperature = 0.01;
		}else if(fruit.length/2 < 50){
			temperature = 1;
		}else{
			temperature = 100;
		}
		int detaStep = 0;
		double coolingRate = 0.9999;
		double absoluteTemperature = 0.00001;

		int[] nextFruit;
		Random random = new Random();
		while (temperature > absoluteTemperature) {
			nextFruit = getNewFruit(fruit);
			Queue<Integer> newPaths = getPaths(nextFruit,unweightedXToY);
			detaStep = newPaths.size() - step;

			if ((detaStep < 0) || (Math.exp(-detaStep / temperature) > random.nextDouble())) {
				fruit = copyFruit(nextFruit);
				step = detaStep + step;

				if (step < minStep) {
					minFruit = getFruit(newPaths, nextFruit);
//					minFruit = copyFruit(nextFruit);
					minStepPath = copyPath(newPaths);
				}
			}

			temperature *= coolingRate;
		}
		return minFruit;
	}
	
	// 退火算法寻找经过必过点的步数与花费的统一
	public int[] anneal2() {
		int[] fruit = initFruit(); // 节点位置部署
		weightedXToY = xToy(fruit);
		int[] minFruit = copyFruit(fruit);
		Queue<Integer> paths = getPaths(fruit,weightedXToY);
		minCostPath = copyPath(paths);
		minFruit = getFruit(paths,fruit);
		int step = paths.size();
		double cost = getCost(paths);
		double value = cost*(step/(cost/step+1))+step*(cost/(cost/step+1));
		double minValue = value;
		double temperature = 0.01;
		if(fruit.length/2<10){
			temperature = 0.01;
		}else if(fruit.length/2 < 50){
			temperature = 1;
		}else{
			temperature = 100;
		}
		double deta = 0.0;
		double coolingRate = 0.9999;
		double absoluteTemperature = 0.00001;

		int[] nextFruit;
		Random random = new Random();
		while (temperature > absoluteTemperature) {
			nextFruit = getNewFruit(fruit);
			Queue<Integer> newPaths = getPaths(nextFruit,weightedXToY);
			double detaCost = getCost(newPaths) - cost;
			int detaStep = newPaths.size()-step;
			double ratio = Math.abs(detaCost/detaStep);
			int x = newPaths.size()-limitedStep;
			double y = Math.exp(x)-1;
			double percent1 = 1/(y+1);
			double percent2 = y/(y+1); 
			deta = detaCost*(Math.abs(detaStep/(ratio+1)))*percent1+detaStep*(Math.abs(detaCost/(ratio+1)))*percent2;

			if ((deta < 0) || (Math.exp(-deta / temperature) > random.nextDouble())) {
				fruit = copyFruit(nextFruit);
				cost = cost +detaCost;
				step = step + detaStep;
				value = value + deta;
				if (value < minValue) {
					minFruit = getFruit(newPaths, nextFruit);
					minCostPath = copyPath(newPaths);
				}
			}

			temperature *= coolingRate;
		}
		return minFruit;
	}

	// 返回经过必过点的最少步数
	public int minStep() {
		initEdgeGraph();
		init();
		initEnemy();
		minStepFruit = anneal1();
		return minStepPath.size();
	}

	// 返回经过必过点的最小花费(考虑步数)
	public double minCost() {
		initEdgeWeightedGraph();
		init();
		initEnemy();
		minCostFruit = anneal2();
		return getCost(minCostPath);
	}

	// 最小花费对应必过点访问顺序的最短路径
	public boolean judge() {
		Queue<Integer> path = getPaths(minCostFruit,unweightedXToY);
		if (path.size() <= limitedStep) {
			minStepFruit = copyFruit(minCostFruit);
			minStepPath = copyPath(path);
			return true;
		} else {
			minCostFruit = copyFruit(minStepFruit);
			initEdgeWeightedGraph();
			minCostPath = getPaths(minCostFruit,weightedXToY);
			return false;
		}

	}

	// 改变最小花费路径以满足步数限制
	public Queue<Integer> changePath() {
		boolean flag = judge();
		if (!isWeighted) {
			initEdgeWeightedGraph();
		}	
		int length = minCostFruit.length + 1;
		// 最短路径对应各必过点间的花费及步数
		double cost1[] = new double[length];
		int step1[] = new int[length];
		Queue<Integer>[] minStepQueue = (Queue<Integer>[]) new Queue[length];
		Iterator<Integer> it1 = minStepPath.iterator();
		int vertex = -1;
		for (int i = 0; i < length; i++) {
			Queue<Integer> shortPath = new Queue<Integer>();
			if (vertex != -1) {
				shortPath.enqueue(vertex);
			}
			vertex = it1.next();
			int step = 0;
			if (i < length - 1) {
				while (vertex != minCostFruit[i] && it1.hasNext()) {
					shortPath.enqueue(vertex);
					step++;
					vertex = it1.next();
				}
				shortPath.enqueue(vertex);
				minStepQueue[i] = copyPath(shortPath);
				step++;
			} else {
				while (vertex != end) {
					shortPath.enqueue(vertex);
					step++;
					vertex = it1.next();
				}
				shortPath.enqueue(vertex);
				minStepQueue[i] = copyPath(shortPath);
				step++;
			}
			step1[i] = step;
			cost1[i] = getCost(shortPath);
			if(i < length-2){
				if(minCostFruit[i]== minCostFruit[i+1]){
					minStepQueue[i+1] = new Queue<Integer>();
					minStepQueue[i+1].enqueue(minCostFruit[i]);
					step1[i+1] = 0;
					cost1[i+1] = 0;
					i++;
				}
			}
		}
		// 最小化费路径对应各必过点间的花费及步数
		double cost2[] = new double[length];
		int step2[] = new int[length];
		Iterator<Integer> it2 = minCostPath.iterator();
		Queue<Integer>[] minCostQueue = (Queue<Integer>[]) new Queue[length];
		vertex = -1;
		for (int i = 0; i < length; i++) {
			Queue<Integer> shortPath = new Queue<Integer>();
			if (vertex != -1) {
				shortPath.enqueue(vertex);
			}
			vertex = it2.next();
			int step = 0;
			if (i < length - 1) {
				while (vertex != minCostFruit[i]) {
					shortPath.enqueue(vertex);
					step++;
					vertex = it2.next();
				}
				shortPath.enqueue(vertex);
				minCostQueue[i] = copyPath(shortPath);
				step++;
			} else {
				while (vertex != end) {
					shortPath.enqueue(vertex);
					step++;
					vertex = it2.next();
				}
				shortPath.enqueue(vertex);
				minCostQueue[i] = copyPath(shortPath);
				step++;
			}
			step2[i] = step;
			cost2[i] = getCost(shortPath);
			if(i < length-2){
				if(minCostFruit[i]== minCostFruit[i+1]){
					minCostQueue[i+1] = new Queue<Integer>();
					minCostQueue[i+1].enqueue(minCostFruit[i]);
					step2[i+1] = 0;
					cost2[i+1] = 0;
					i++;
				}
			}
		}

/*		System.out.println("最小花费路径各必过点间的步数: ");
		for (int i = 0; i < length; i++) {
			System.out.print(step2[i] + " ");
		}
		System.out.println();
		System.out.println("最小花费路径各必过点间的花费: ");
		for (int i = 0; i < length; i++) {
			System.out.print(cost2[i] + " ");
		}
		System.out.println();*/

		int detaStep = minCostPath.size() - limitedStep;
		if (detaStep <= 0) {
			return minCostPath;
		}

		int[] perStep = new int[length];
		double[] perCost = new double[length];
		for (int i = 0; i < length; i++) {
			perStep[i] = step2[i] - step1[i];
			if (perStep[i] == 0) {
				perCost[i] = Double.POSITIVE_INFINITY;
			} else {
				perCost[i] = (cost1[i] - cost2[i]) / perStep[i];
			}
		}

//		Queue<Integer> tempPath = copyPath(minStepPath);
//		Queue<Integer> path = stepCut11(perStep, perCost, detaStep, minCostQueue, minStepQueue, tempPath);
		Bag<Integer> change = stepCut(perStep, perCost, detaStep);
		Iterator<Integer> it = change.iterator();
		while(it.hasNext()){
			int index = it.next();
			minCostQueue[index] = copyPath(minStepQueue[index]);
		}
		Queue<Integer> path = getResult(minCostQueue);
		return path;

	}

	// 从最小花费路径中删除节点
	public Bag<Integer> stepCut(int[] perStep, double[] perCost, int detaStep) {
		
		int index = selectMin(perCost);
		Bag<Integer> change = new Bag<Integer>();
		Stack<Integer> stack = new Stack<Integer>();
		while (detaStep > 0 && perStep[index] <= detaStep) {
			detaStep -= perStep[index];
			stack.push(index);
			change.add(index);
			perCost[index] = Double.POSITIVE_INFINITY;
			index = selectMin(perCost);
		}
		// 多余步数为0
		if (detaStep == 0) {
			return change;
		}
		// 找到perStep[index]>detaStep
		else {
			change.add(index);
			int deta = perStep[index] - detaStep;
			while(deta>0 && !stack.isEmpty()){
				int x = stack.pop();
				if(perStep[x] <= deta){
					change.detele(x);
					deta -= perStep[x];
				}
			}
			return change;
		}
	}

	public Queue<Integer> getResult(Queue<Integer>[] minCostQueue) {
		Queue<Integer> path = new Queue<Integer>();
		for (int i = 0; i < minCostQueue.length; i++) {
			Iterator<Integer> it = minCostQueue[i].iterator();
			if (i == 0) {
				while (it.hasNext()) {
					path.enqueue(it.next());
				}
			} else {
				it.next();
				while (it.hasNext()) {
					path.enqueue(it.next());
				}
			}
		}
		return path;
	}

	// 找出数组中最小值
	public int selectMin(double[] perCost) {
		double min = Double.POSITIVE_INFINITY;
		int index = -1;
		for (int i = 0; i < perCost.length; i++) {
			if (perCost[i] < min) {
				index = i;
				min = perCost[i];
			}
		}
		return index;
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		SADjk djk = new SADjk();
		int minStep = djk.minStep();
		double minCost = djk.minCost();
		if (minStep > djk.limitedStep) {
			Queue<Integer> path = djk.minCostPath;
			double cost = djk.getCost(path);
			System.out.println("不能满足步数限制,返回忽略步数限制的最小花费路径: ");
			Iterator<Integer> it = path.iterator();
			while (it.hasNext()) {
				System.out.print(it.next() + " ");
			}
			System.out.println();
			System.out.println("最小花费:"+cost);
		}else{
			Queue<Integer> path = djk.changePath();
			double cost = djk.getCost(path);
			System.out.println("满足所有限制条件的最小花费路径: ");
			Iterator<Integer> it = path.iterator();
			while (it.hasNext()) {
				System.out.print(it.next() + " ");
			}
			System.out.println();
			System.out.println("最小花费: "+cost);
		}
		System.out.println("总时间: "+(System.currentTimeMillis()-start)+" ms");

		System.out.println("输入 q 退出");
		Scanner sc = new Scanner(System.in);
		while(sc.hasNext()){
			if(sc.next().equals("q")){
				break;
			}
		}
		sc.close();
	}

}
