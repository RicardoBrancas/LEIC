#include<iostream>
#include<vector>
#include<list>

typedef std::vector< std::list<int> > Graph;

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
