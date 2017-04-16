var menu = [
  {
    name: "Bitoque de Vaca",
    id: 1,
    price: 7.5,
    categories: [
        "destaques",
        "pratos-principais-carne"
    ],
    nutrition: {
      "Energia": "658 kCal",
      "Lípidos": "29.5 g",
      "\tdos quais saturados": "7.5 g",
      "Colesterol": "296 mg",
      "Hidratos de Carbono": "49.5 g",
      "Proteína": "44 g",
      "Sódio": "429 mg"
    },
    options: {
      Batatas: 1,
      Arroz: 0,
      Cebola: 1
    }
  },
  {
    name: "Bacalhau com Espinafres",
    id: 2,
    price: 8.5,
    categories: [
        "pratos-principais-peixe"
    ],
    nutrition: {

    }
  },
  {
    name: "Sumo de Laranja",
    id: 3,
    price: 1.5,
    categories: [
        "bebidas"
    ],
    nutrition: {

    }
  },
  {
    name: "Limonada",
    id: 4,
    price: 1.5,
    categories: [
        "bebidas"
    ],
    nutrition: {

    },
    options: {
      "+ Açúcar": 0
    }
  },
  {
    name: "Couvert",
    id: 5,
    price: 3,
    categories: [
        "entradas"
    ],
    nutrition: {

    }
  },
  {
    name: "Sopa de Coentros",
    id: 6,
    price: 2,
    categories: [
        "sopas"
    ],
    nutrition: {

    }
  },
  {
    name: "Salada de Salmão Fumado",
    id: 7,
    price: 7.5,
    categories: [
        "saladas"
    ],
    nutrition: {

    }
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
