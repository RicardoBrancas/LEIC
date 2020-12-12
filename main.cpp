#include<iostream>
#include<vector>
#include<queue>

struct Edge {
	int from, to, cost;

	Edge(int f, int t, int c) : from(f), to(t), cost(c) {}

	inline bool isAirline() const { return to == 0 || from == 0; }

	friend inline bool operator>(const Edge& l, const Edge& r) {
		if( l.cost != r.cost)
			return l.cost > r.cost;
		else return l.isAirline();
	}
};

//Despite the weird c++ semantics, using a greater comparator creates a min-heap
typedef std::priority_queue<Edge, std::vector<Edge>, std::greater<Edge> > edge_pq;

class Set {
	int label, rank;
	Set* parent;

	void Link(Set* x, Set* y) {
		if(x->rank > y->rank)
			y->parent = x;
		else {
			x->parent = y;
			if(x->rank == y->rank)
				y->rank++;
		}
	}

public:
	Set(int l) : label(l), rank(0), parent(this) {}

	Set* FindSet() {
		if(label != parent->label)
			parent = parent->FindSet();
		return parent;
	}

	void Union(Set* s) {
		Link(FindSet(), s->FindSet());
	}

	friend inline bool operator!=(const Set& l, const Set& r) {
		return l.label != r.label;
	}
};

/* Consideremos R = numero de estradas
				A = numero de linhas aereas

	Complexidade: O( R logV + (V+A-1) logV ) = O( (V+R+A-1) logV)
				= O( (V + E) logV )
	Para grafos ligados (E >= V-1):
				= O( E logV )
*/
void kruskals(unsigned V, edge_pq* roads_pq, edge_pq* airlines_pq) {
	std::vector<Set*> forest;
	for(unsigned i = 0; i < V; i++) {
		forest.push_back(new Set(i));
	}

	//First we consider just the roads: O(R logV)
	int jr_totalCost = 0;
	unsigned jr_roadN = 0;
	while(!roads_pq->empty()) {
		Edge e = roads_pq->top();
		roads_pq->pop();

		if(*forest[e.from]->FindSet() != *forest[e.to]->FindSet()) {
			jr_totalCost += e.cost;
			jr_roadN++;
			airlines_pq->push(e); //Vamos adicionando a estradas às linhas aereas
			forest[e.from]->Union(forest[e.to]);
		}
	}

	//No fim ficamos, no maximo, com (V - 1) + A arcos em airline_pq
	//Consideramos agora os arcos "todos" O( (V+A-1) logV)
	std::vector<Edge> edges;

	for(unsigned i = 0; i < V; i++) {
		delete forest[i];
		forest[i] = new Set(i);
	}

	unsigned roadN = 0, airlineN = 0;
	while(!airlines_pq->empty()) {
		Edge e = airlines_pq->top();
		airlines_pq->pop();

		if(*forest[e.from]->FindSet() != *forest[e.to]->FindSet()) {
			if(e.isAirline())
				airlineN++;
			else
				roadN++;
			edges.push_back(e);
			forest[e.from]->Union(forest[e.to]);
		}
	}

	Set* set = forest[1]->FindSet();
	for(unsigned i = 0; i < V; i++) {
		if(i != 0 && set != forest[i]->FindSet()) { //only the first vertex is allowed
			std::cout << "Insuficiente" << std::endl;
			return;
		}
	}
	for(unsigned i = 0; i < V; i++) {
		delete forest[i];
	}

	int totalCost = 0;
	for(unsigned i = 0; i < edges.size(); i++) {
		if(airlineN > 1 || !edges[i].isAirline())
			totalCost += edges[i].cost;
	}

	if(jr_roadN == V-2 && jr_totalCost <= totalCost) {
		std::cout << jr_totalCost << std::endl << 0 << " " << jr_roadN << std::endl;
	} else {
		std::cout << totalCost << std::endl << airlineN << " " << roadN << std::endl;

	}
}

int main() {
	int cityN, airportN, roadN, city, cost, cityFrom, cityTo;

	std::cin >> cityN;

	edge_pq roads_pq;
	edge_pq airlines_pq;

	std::cin >> airportN;
	for(int i = 0; i < airportN; i++) {
		std::cin >> city >> cost;
		airlines_pq.push(Edge(0, city, cost));
	}

	std::cin >> roadN;
	for(int i = 0; i < roadN; i++) {
		std::cin >> cityFrom >> cityTo >> cost;
		roads_pq.push(Edge(cityFrom, cityTo, cost));
	}

	kruskals(cityN+1, &roads_pq, &airlines_pq);
}
