#include<iostream>
#include<vector>
#include<list>
#include<limits>
#include<queue>

class comparator {
private:
  std::vector<int>* keys;
public:
  comparator(std::vector<int>* k) : keys(k) {}
  bool operator() (const int& lhs, const int&rhs) const {
    return (*keys)[lhs] > (*keys)[rhs];
  }
};

typedef std::vector< std::list<int> > Graph;
typedef std::vector< std::list<int> >::iterator graph_it;
typedef std::priority_queue<int, std::vector<int>, comparator> priority_q;

void prim(Graph* graph_ptr) {
  std::vector<int> key(graph_ptr->size());
  std::vector<int> pi(graph_ptr->size());

  for(unsigned int i = 0; i < graph_ptr->size(); i++) {
    key[i] = std::numeric_limits<int>::max();
    pi[i] = -1;
  }

  key[1] = 0; //Maybe use 0?

  priority_q Q = priority_q(comparator(&key));
  for(unsigned int i = 0; i < graph_ptr->size(); i++) {
    Q.push(i);
  }

  //Initialization finished
}

int main() {
  int cityN, airportN, roadN, city, cost, cityFrom, cityTo;

  std::cin >> cityN;

  Graph cities(cityN + 1);

  std::cin >> airportN;
  for(int i = 0; i < airportN; i++) {
    std::cin >> city >> cost;
    cities[0].push_back(city);
    cities[city].push_back(0);
  }

  std::cin >> roadN;
  for(int i = 0; i < roadN; i++) {
    std::cin >> cityFrom >> cityTo >> cost;
    cities[cityFrom].push_back(cityTo);
    cities[cityTo].push_back(cityFrom);
  }
}
