#include<iostream>
#include<deque>
#include<vector>
#include<list>

//Iterator shorthands
class Node;
typedef std::list<Node*>::iterator list_it;
typedef std::vector<Node*>::iterator vector_it;

// === NODE ===
class Node {
private:
  std::list<Node*> adjacent_nodes;

  int in_degree;

public:
  int id;

  Node(int i) : id(i) {}

  bool no_exit_edges() {
    return adjacent_nodes.empty();
  }

  bool no_entry_edges() { // O(1)
    return in_degree == 0;
  }

  void addEdge(Node* n) {
    adjacent_nodes.push_back(n);
  }

  void remove_edge(list_it pos) { // O(1)
    adjacent_nodes.erase(pos);
  }

  void incrementInDegree() {
    in_degree++;
  }

  list_it begin() { return adjacent_nodes.begin(); }
  list_it end() { return adjacent_nodes.end(); }
};

// === GRAPH ===

class Graph {
private:
  std::vector<Node*> adjacency_list;

public:
  Graph(int size)  {
    adjacency_list.reserve(size);
    for(int i = 0; i < size; i++)
      adjacency_list.push_back(new Node(i+1));
  }

  void addEdge(int from, int to) {
    adjacency_list[from-1]->addEdge(adjacency_list[to-1]);
    adjacency_list[to-1]->incrementInDegree();
  }

  bool has_edges() { // O(V)
    for(vector_it it = adjacency_list.begin(); it != adjacency_list.end(); ++it) {
      if(!((*it)->no_exit_edges()))
        return true;
    }
    return false;
  }

  vector_it begin() { return adjacency_list.begin(); }

  vector_it end() { return adjacency_list.end(); }
};

// ==== TOPOLOGICAL SORT ====

std::list<Node*> topological_sort(Graph& g) {
  std::list<Node*> list;
  std::deque<Node*> queue;

  for(vector_it it = g.begin(); it != g.end(); ++it) { // O(V)
    if((*it)->no_entry_edges())
      queue.push_front(*it);
  }

  while(!queue.empty()) { // O( V + E) (MAYBE??)
    Node* n = queue.front();
    list.push_back(n);

    for(list_it iterator = n->begin(); iterator != n->end(); ++iterator) {
      n->remove_edge(iterator); // O(1)
      if((*iterator)->no_entry_edges()) // O(1)
        queue.push_front(*iterator);
    }
  }

  if(g.has_edges()) // O(V)
    throw -1; //The Graph has at least a cycle
  else
    return list;
}

int main() {

}
