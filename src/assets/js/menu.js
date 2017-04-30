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
      "Energia": [455.6, "kCal"],
      "Lípidos": [17, "g"],
      "\tdos quais saturados": [2.3, "g"],
      "Colesterol": [99, "mg"],
      "Hidratos de Carbono": [28.5, "g"],
      "\tdos quais açucares": [3.2, "g"],
      "Proteína": [53.6, "g"],
      "Sódio": [648, "mg"]
    },
    options: {
      Batatas: {
		  options: {
			  "Não": {},
			  "Sim": {
				  "Energia": 455.6,
			      "Lípidos": 17,
			      "\tdos quais saturados": 2.3,
			      "Colesterol": 99,
			      "Hidratos de Carbono": 28.5,
			      "\tdos quais açucares": 3.2,
			      "Proteína": 53.6,
			      "Sódio": 648
			  }
		  },
		  default: "Sim"
	  },
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
    price: 8.5,
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
  {
    name: "Salada de Frango",
    id: 10,
    price: 7.5,
    categories: [
        "saladas"
    ],
    nutrition: {
      "Energia": "386 kCal",
      "Lípidos": "18.5 g",
      "\tdos quais saturados": "5.7 g",
      "Colesterol": "181 mg",
      "Hidratos de Carbono": "18.9 g",
      "\tdos quais açucares": "1.5 g",
      "Proteína": "24.3 g",
      "Sódio": "336 mg"
    }
  },
  {
    name: "Esparguete à Carbonara",
    id: 11,
    price: 9,
    categories: [
        "destaques",
        "pratos-principais-carne"
    ],
    nutrition: {
      "Energia": "646 kCal",
      "Lípidos": "27.2 g",
      "\tdos quais saturados": "6.7 g",
      "Colesterol": "234 mg",
      "Hidratos de Carbono": "38.9 g",
      "\tdos quais açucares": "6.5 g",
      "Proteína": "29.3 g",
      "Sódio": "476 mg"
    }
  },
  {
    name: "Bife de Atum de Cebolada com Batatas",
    id: 12,
    price: 8.5,
    categories: [
        "pratos-principais-peixe"
    ],
    nutrition: {
      "Energia": "624 kCal",
      "Lípidos": "21.4 g",
      "\tdos quais saturados": "6.1 g",
      "Colesterol": "279 mg",
      "Hidratos de Carbono": "41.7 g",
      "\tdos quais açucares": "7.5 g",
      "Proteína": "32.3 g",
      "Sódio": "376 mg"
    }
  },
  {
    name: "Risoto de Cenoura",
    id: 13,
    price: 6.5,
    categories: [
        "pratos-principais-vegetariano"
    ],
    nutrition: {
      "Energia": "397 kCal",
      "Lípidos": "16.1 g",
      "\tdos quais saturados": "3.4 g",
      "Colesterol": "173 mg",
      "Hidratos de Carbono": "45.9 g",
      "\tdos quais açucares": "7.8 g",
      "Proteína": "13.7 g",
      "Sódio": "217 mg"
    }
  },
  {
    name: "Ratatouille",
    id: 14,
    price: 10.5,
    categories: [
      "destaques",
        "pratos-principais-vegetariano"
    ],
    nutrition: {
      "Energia": "478 kCal",
      "Lípidos": "23.7 g",
      "\tdos quais saturados": "8.4 g",
      "Colesterol": "239 mg",
      "Hidratos de Carbono": "41.3 g",
      "\tdos quais açucares": "6.9 g",
      "Proteína": "14.6 g",
      "Sódio": "239 mg"
    }
  },
  {
    name: "Bolo de Bolacha",
    id: 15,
    price: 2.5,
    categories: [
        "sobremesas"
    ],
    nutrition: {
      "Energia": "282 kCal",
      "Lípidos": "20.1 g",
      "\tdos quais saturados": "9.2 g",
      "Colesterol": "267 mg",
      "Hidratos de Carbono": "26.9 g",
      "\tdos quais açucares": "18.8 g",
      "Proteína": "4.7 g",
      "Sódio": "12 mg"
    }
  },
  {
  name: "Mousse de Chocolate",
  id: 16,
  price: 2.5,
  categories: [
      "sobremesas"
  ],
  nutrition: {
    "Energia": "296 kCal",
    "Lípidos": "22.7 g",
    "\tdos quais saturados": "11.3 g",
    "Colesterol": "246 mg",
    "Hidratos de Carbono": "24.6 g",
    "\tdos quais açucares": "19.3 g",
    "Proteína": "3.8 g",
    "Sódio": "8 mg"
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
