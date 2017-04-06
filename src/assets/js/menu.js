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
  },
  {
    name: "Bacalhau com Espinafres",
    price: 8.5,
    categories: [
        "pratos-principais-peixe"
    ]
  },
  {
    name: "Sumo de Laranja",
    price: 1.5,
    categories: [
        "bebidas"
    ]
  },
  {
    name: "Limonada",
    price: 1.5,
    categories: [
        "bebidas"
    ],
    options: {
      "+ Açúcar": 0
    }
  },
  {
    name: "Couvert",
    price: 3,
    categories: [
        "entradas"
    ]
  },
  {
    name: "Sopa de Coentros",
    price: 2,
    categories: [
        "sopas"
    ]
  },
  {
    name: "Salada de Salmão Fumado",
    price: 7.5,
    categories: [
        "saladas"
    ]
  },
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
