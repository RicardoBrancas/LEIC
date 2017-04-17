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
      "Energia": "455.6 kCal",
      "Lípidos": "17 g",
      "\tdos quais saturados": "2.3 g",
      "Colesterol": "99 mg",
      "Hidratos de Carbono": "28.5 g",
      "\tdos quais açucares": "3.2 g",
      "Proteína": "53.6 g",
      "Sódio": "648 mg"
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
        "destaques",
        "pratos-principais-peixe"
    ],
    nutrition: {
      "Energia": "658 kCal",
      "Lípidos": "29.5 g",
      "\tdos quais saturados": "7.5 g",
      "Colesterol": "296 mg",
      "Hidratos de Carbono": "49.5 g",
      "\tdos quais açucares": "8 g",
      "Proteína": "44 g",
      "Sódio": "429 mg"
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
      "Energia": "111 kCal",
      "Lípidos": "0.7 g",
      "\tdos quais saturados": "0 g",
      "Colesterol": "0 mg",
      "Hidratos de Carbono": "25.9 g",
      "\tdos quais açucares": "20 g",
      "Proteína": "1.3 g",
      "Sódio": "2 mg"
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
      "Energia": "12 kCal",
      "Lípidos": "0 g",
      "\tdos quais saturados": "0 g",
      "Colesterol": "0 mg",
      "Hidratos de Carbono": "4.9 g",
      "\tdos quais açucares": "1.1 g",
      "Proteína": "0.2 g",
      "Sódio": "0 mg"
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
      "Energia": "178 kCal",
      "Lípidos": "12.7 g",
      "\tdos quais saturados": "6.3 g",
      "Colesterol": "156 mg",
      "Hidratos de Carbono": "28.9 g",
      "\tdos quais açucares": "7 g",
      "Proteína": "15.3 g",
      "Sódio": "219 mg"
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
      "Energia": "105 kCal",
      "Lípidos": "5.9 g",
      "\tdos quais saturados": "0 g",
      "Colesterol": "0 mg",
      "Hidratos de Carbono": "19 g",
      "\tdos quais açucares": "4 g",
      "Proteína": "0 g",
      "Sódio": "57 mg"
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
      "Energia": "406 kCal",
      "Lípidos": "29.5 g",
      "\tdos quais saturados": "5.7 g",
      "Colesterol": "210 mg",
      "Hidratos de Carbono": "16.9 g",
      "\tdos quais açucares": "1.5 g",
      "Proteína": "27.3 g",
      "Sódio": "836 mg"
    }
  },
  {
    name: "Orelha de Coentrada",
    id: 8,
    price: 3,
    categories: [
        "entradas"
    ],
    nutrition: {
      "Energia": "176 kCal",
      "Lípidos": "17.5 g",
      "\tdos quais saturados": "9.7 g",
      "Colesterol": "174 mg",
      "Hidratos de Carbono": "9 g",
      "\tdos quais açucares": "0.5 g",
      "Proteína": "7.3 g",
      "Sódio": "346 mg"
    }
  },
  {
    name: "Canja de Galinha",
    id: 9,
    price: 3,
    categories: [
        "sopas"
    ],
    nutrition: {
      "Energia": "175 kCal",
      "Lípidos": "10.9 g",
      "\tdos quais saturados": "2 g",
      "Colesterol": "79 mg",
      "Hidratos de Carbono": "18 g",
      "\tdos quais açucares": "1.5 g",
      "Proteína": "0 g",
      "Sódio": "77 mg"
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
