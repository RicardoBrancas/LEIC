'use strict';

let basic_materials = new Map();
let phong_materials = new Map();
let lambert_materials = new Map();

function create_material(name, color, {emissive = false, shininess = 30, specular = 0x111111, transparent = false, opacity = 1} = {}) {
	let basic = new THREE.MeshBasicMaterial({color: color, transparent: transparent, opacity: opacity});
	basic.name = name;
	basic_materials.set(name, basic);

	let phong = new THREE.MeshPhongMaterial({
		color: color,
		emissive: emissive ? color : 0x0,
		shininess: shininess,
		specular: specular,
		transparent: transparent,
		opacity: opacity
	});
	phong.name = name;
	phong_materials.set(name, phong);

	let lambert = new THREE.MeshLambertMaterial({
		color: color,
		emissive: emissive ? color : 0x0,
		transparent: transparent,
		opacity: opacity
	});
	lambert.name = name;
	lambert_materials.set(name, lambert);

	return basic;
}

function foreach_material(func) {
	basic_materials.forEach(func);
	phong_materials.forEach(func);
	lambert_materials.forEach(func);
}

let car_material = create_material('car', 0xFF7518, {shininess: 100});
let car_top_material = create_material('car_top', 0xdcf5f7, {
	shininess: 200,
	specular: 0xffffff,
	transparent: true,
	opacity: 0.8
});

let tires_material = create_material('tires', 0x0303003, {shininess: 10, specular: 0x666666});
let ground_material = create_material('ground', 0x45525F, {shininess: 20});
let wax_material = create_material('wax', 0xefe6d3);
let flame_material = create_material('flame', 0xe71837, {emissive: true});
let cheerio_material = create_material('cheerio', 0xFFD700, {shininess: 1, specular: 0xDA9100});
let butter_material = create_material('butter', 0xFFFDD0);
let orange_material = create_material('orange', 0xff8c00, {shininess: 80});
let leaf_material = create_material('leaf', 0x05581c, {shininess: 100});
let headlights_material = create_material('car_headlights', 0xaaaaaa, {shininess: 76.8, emissive: true});