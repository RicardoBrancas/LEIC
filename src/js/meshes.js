'use strict';

const ground_mesh = new THREE.Mesh(ground_geometry, ground_material);
ground_mesh.name = "Ground";

let cheerio_mesh = new THREE.Mesh(cheerio_geometry, cheerio_material);
cheerio_mesh.name = "Cheerio";

let butter_mesh = new THREE.Mesh(butter_geometry, butter_material);
butter_mesh.name = "Butter";

let orange_mesh = new THREE.Mesh(orange_geometry, orange_material);
let leaf_mesh = new THREE.Mesh(leaf_geometry, leaf_material);
let stalk_mesh = new THREE.Mesh(stalk_geometry, leaf_material);

const orange_group = new THREE.Group();
orange_group.add(orange_mesh);
leaf_mesh.rotateY(Math.PI / 3);
leaf_mesh.position.x = 6.5;
leaf_mesh.position.z = 3;
orange_group.add(leaf_mesh);
stalk_mesh.rotateY(Math.PI / 3);
stalk_mesh.position.x = 5.5;
stalk_mesh.position.z = 3;
orange_group.add(stalk_mesh);

let candle_group = new THREE.Group();
let wax_mesh = new THREE.Mesh(wax_geometry, wax_material);
wax_mesh.position.z = 4 / 2;
wax_mesh.rotateX(-Math.PI / 2);
candle_group.add(wax_mesh);
let flame_mesh = new THREE.Mesh(flame_geometry, flame_material);
flame_mesh.rotateX(Math.PI / 2);
flame_mesh.position.z = 4 + (1 / 2);
candle_group.add(flame_mesh);

const car_body = new THREE.Mesh(car_body_geometry, car_material);
const car_top = new THREE.Mesh(car_top_geometry, car_top_material);
const car_headlights = new THREE.Mesh(car_headlights_geometry, headlights_material);