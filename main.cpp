#include<iostream>
#include<deque>
#include<vector>
#include<list>

typedef std::vector< std::pair<int, std::list<int> > > Graph;
typedef std::vector< std::pair<int, std::list<int> > >::iterator vector_it;

std::list<int> topological_sort(Graph* album_ptr) {
  std::list<int> list;
  std::deque<int> queue;

  for(vector_it it = album_ptr->begin(); it != album_ptr->end(); ++it) {
    if((*it).first == 0)
      queue.push_front(it - album_ptr->begin()); //TODO
  }

  while(!queue.empty()) { // O( V + E) (MAYBE??)
    int n = queue.front();
    queue.pop_front();
    list.push_back(n);

    std::list<int>::iterator it = (*album_ptr)[n].second.begin();
    while (it != (*album_ptr)[n].second.end()) {
      int m = *it;

      (*album_ptr)[m].first--;
      (*album_ptr)[n].second.erase(it++);

      if((*album_ptr)[m].first == 0) // O(1)
        queue.push_front(m);

    }
  }

  for(vector_it it = album_ptr->begin(); it != album_ptr->end(); ++it) {
    if((*it).first != 0)
      throw -1;
  }

  return list;
}

int main() {

  int nNodes, nEdges;

  std::cin >> nNodes >> nEdges;

  std::vector< std::pair<int, std::list<int> > > album(nNodes);

  int before, after;
  while(true) {
    std::cin >> before >> after;
    if(std::cin.eof())
      break;
    album[before-1].second.push_back(after-1);
    album[after-1].first++;
  }

  try {
    std::list<int> list = topological_sort(&album);

    for(std::list<int>::iterator it = list.begin(); it != list.end(); ++it) {
      std::cout << *it+1 << " ";
    }
  } catch (int i) {
    if (i == -1)
      std::cout << "Incoerente";
  }

  std::cout << std::endl;







}
