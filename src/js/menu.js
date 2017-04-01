var menu = [
  {
    name: "Bitoque de Vaca",
    price: 7.5,
    categories: [
        "destaques",
        "pratos-principais-carne"
    ],
    options: {
      Batatas: 1,
      Arroz: 0,
      Cebola: 1
    }
  }
]

var categories = [];
let parse_menu = function() {
    for(item of menu) {
      for(cat of item.categories) {
        if(categories[cat] === undefined)
          categories[cat] = [];
        categories[cat].push(item);
      }
    }
}();
