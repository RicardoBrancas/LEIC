'use strict';

const ground_geometry = new THREE.BoxGeometry(table_length, table_length, table_height, table_slices, table_slices);
const cheerio_geometry = new THREE.TorusGeometry(cheerio_size, cheerio_size / 2, 8, 10);
const butter_geometry = new THREE.BoxGeometry(7, 4, 1.2);
const orange_geometry = new THREE.SphereGeometry(orange_radius, 16, 16);
const leaf_geometry = new THREE.BoxGeometry(3, 3, 0.1);
const stalk_geometry = new THREE.BoxGeometry(0.5, 0.5, 2);
let wax_geometry = new THREE.CylinderGeometry(0.6, 0.4, 4, 8);
let flame_geometry = new THREE.ConeGeometry(0.3, 1, 3);

let car_body_geometry = new THREE.Geometry();
car_body_geometry.vertices.push(
	new THREE.Vector3(-1.5, 2.5, -0.5),
	new THREE.Vector3(1.5, 2.5, -0.5),
	new THREE.Vector3(1.5, -2.5, -0.5),
	new THREE.Vector3(-1.5, -2.5, -0.5),
	new THREE.Vector3(-1.5, 2.5, 0.5),
	new THREE.Vector3(1.5, 2.5, 0.5),
	new THREE.Vector3(1.5, -2.5, 0.5),
	new THREE.Vector3(-1.5, -2.5, 0.5),
	new THREE.Vector3(1.5, 2.75, 0.25),
	new THREE.Vector3(1.5, 2.75, -0.25),
	new THREE.Vector3(-1.5, 2.75, -0.25),
	new THREE.Vector3(-1.5, 2.75, 0.25),
	new THREE.Vector3(1.5, -2.75, 0.25),
	new THREE.Vector3(1.5, -2.75, -0.25),
	new THREE.Vector3(-1.5, -2.75, -0.25),
	new THREE.Vector3(-1.5, -2.75, 0.25)
);
car_body_geometry.faces.push(
	new THREE.Face3(0, 1, 2),
	new THREE.Face3(2, 3, 0),
	new THREE.Face3(4, 7, 6),
	new THREE.Face3(6, 5, 4),
	new THREE.Face3(4, 0, 3),
	new THREE.Face3(3, 7, 4),
	new THREE.Face3(1, 5, 6),
	new THREE.Face3(6, 2, 1),
	new THREE.Face3(0, 4, 5),
	new THREE.Face3(5, 1, 0),
	new THREE.Face3(2, 6, 7),
	new THREE.Face3(7, 3, 2),
	new THREE.Face3(8, 9, 10),
	new THREE.Face3(10, 11, 8),
	new THREE.Face3(4, 5, 8),
	new THREE.Face3(8, 11, 4),
	new THREE.Face3(5, 1, 9),
	new THREE.Face3(9, 8, 5),
	new THREE.Face3(0, 4, 11),
	new THREE.Face3(11, 10, 0),
	new THREE.Face3(1, 0, 10),
	new THREE.Face3(10, 9, 1),
	new THREE.Face3(15, 14, 13),
	new THREE.Face3(13, 12, 15),
	new THREE.Face3(12, 6, 7),
	new THREE.Face3(7, 15, 12),
	new THREE.Face3(3, 2, 13),
	new THREE.Face3(13, 14, 3),
	new THREE.Face3(15, 7, 3),
	new THREE.Face3(3, 14, 15),
	new THREE.Face3(6, 12, 13),
	new THREE.Face3(13, 2, 6),
);
car_body_geometry.computeFaceNormals();
car_body_geometry.computeVertexNormals();

let car_top_geometry = new THREE.Geometry();
car_top_geometry.vertices.push(
	new THREE.Vector3(-1.5, 1.5, -0.25),
	new THREE.Vector3(1.5, 1.5, -0.25),
	new THREE.Vector3(1.5, -1.5, -0.25),
	new THREE.Vector3(-1.5, -1.5, -0.25),
	new THREE.Vector3(-1.25, 1.25, 0.25),
	new THREE.Vector3(1.25, 1.25, 0.25),
	new THREE.Vector3(1.25, -1.25, 0.25),
	new THREE.Vector3(-1.25, -1.25, 0.25)
);
car_top_geometry.faces.push(
	new THREE.Face3(0, 1, 2),
	new THREE.Face3(2, 3, 0),
	new THREE.Face3(5, 4, 7),
	new THREE.Face3(7, 6, 5),
	new THREE.Face3(1, 5, 6),
	new THREE.Face3(6, 2, 1),
	new THREE.Face3(2, 6, 7),
	new THREE.Face3(7, 3, 2),
	new THREE.Face3(4, 0, 3),
	new THREE.Face3(3, 7, 4),
	new THREE.Face3(4, 5, 1),
	new THREE.Face3(1, 0, 4),
);
car_top_geometry.computeFaceNormals();
car_top_geometry.computeFlatVertexNormals();

let car_headlights_geometry = new THREE.Geometry();
for(let y = 0.1; y <= 0.2; y += 0.1) {
	for(let i = 0; i < 6; i++) {
		car_headlights_geometry.vertices.push(new THREE.Vector3(Math.cos(Math.PI / 3 * i) * 0.1, y, Math.sin(Math.PI / 3 * i) * 0.15));
	}
}
for(let i = 0; i < 6; i+=1) { //sides
	if(i+6+1 < 12) { //last one must be flipped
		car_headlights_geometry.faces.push(new THREE.Face3(i+1, i, i+6+1));
	} else {
		car_headlights_geometry.faces.push(new THREE.Face3(i+6, i+1, 0));
	}
	car_headlights_geometry.faces.push(new THREE.Face3((i+6+1) % 12, i, i+6));
}
for(let i = 1; i < 5; i++) { //bases
	car_headlights_geometry.faces.push(new THREE.Face3(i, i+1, 0));
	car_headlights_geometry.faces.push(new THREE.Face3(6+i+1, 6+i, 6));
}
car_headlights_geometry.computeFaceNormals();
car_headlights_geometry.computeVertexNormals();