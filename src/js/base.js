'use strict';

let camera, scene, renderer;
let clock;

let car;

let carMaterial, groundMaterial, orangeMaterial, butterMaterial;

const tableLength = 200;
const tableHeight = 2;

const cheerioSize = 1;

// === HELPER FUNCTIONS ===

function addCloneAtPosition(parent, object, x, y, z) {
	const clone = object.clone();
	clone.position.set(x, y, z);
	parent.add(clone);
}

// === HELPER FUNCTIONS END ===


// === OBJECTS ===

class Collidable extends THREE.Object3D {

	constructor() {
		super();

		this.dirty_radius = true;
		this.dirty_center = true;

		this.sphere_center = new THREE.Vector3();
		this.sphere_radius = 0;
	}

	update_center() {
		this.sphere_center.copy(this.getWorldPosition());

		this.dirty_center = false;
	}

	update_radius() {
		console.warn("update_radius() should be overridden!")
	}

	unit_vector_to(other_collidable) {
		return new THREE.Vector3(other_collidable.sphere_center.x - this.sphere_center.x, other_collidable.sphere_center.y - this.sphere_center.y, 0).normalize();
	}

	distanceSq_to(other_collidable) {
		return (this.sphere_center.x - other_collidable.sphere_center.x) ** 2 + (this.sphere_center.y - other_collidable.sphere_center.y) ** 2;
	}

	sphere_collides(other_collidable) {
		let distance = this.distanceSq_to(other_collidable);

		return (this.sphere_radius + other_collidable.sphere_radius) ** 2 >= distance;
	}

	resolve_collision(other_node, delta) {
		console.warn("resolve_collision() should be overridden in", this)
	}

}

class VariablyAcceleratable extends Collidable {

	constructor() {
		super();

		this.up.set(0, 0, 1);

		this.drag = 0.4;

		this.acceleration = 0;
		this.speed = 0;
		this.angularVelocity = 0;
		this.front = new THREE.Vector3(0, 1, 0);
	}

	update(delta) {
		this.speed += this.acceleration * delta - this.speed * this.drag * delta;
		this.rotateOnAxis(this.up, Math.abs(this.speed) * this.angularVelocity * delta / 50);
		this.translateOnAxis(this.front, this.speed * delta);

		if(this.speed !== 0)
			this.dirty_center = true;
	}
}

const cheerio_material = new THREE.MeshBasicMaterial({color: 0xFFD700});
const cheerio_geometry = new THREE.TorusGeometry(cheerioSize, cheerioSize / 2, 8, 10);
const cheerio_mesh = new THREE.Mesh(cheerio_geometry, cheerio_material);

class Cheerio extends VariablyAcceleratable {

	constructor(x, y, z) {
		super();

		this.drag = 1;

		let clone = cheerio_mesh.clone();
		this.add(clone);
		this.position.set(x, y, z);

		this.update_radius();
	}


	update_radius() {
		this.sphere_radius = cheerioSize * 1.5;
		this.dirty_radius = false;
	}


	resolve_collision(other_node, delta) {

		if (other_node instanceof VariablyAcceleratable) {
			let new_direction = this.unit_vector_to(other_node).negate();
			let over_movement = this.sphere_radius + other_node.sphere_radius - Math.sqrt(this.distanceSq_to(other_node));

			this.translateOnAxis(new_direction, over_movement); //We've overstepped, go back!
			if (over_movement > 0) {
				this.dirty_center = true;
			}

			this.front = new_direction;
			this.speed = Math.abs(other_node.speed);
		}

	}
}

const butter_material = new THREE.MeshBasicMaterial({color: 0xFFFFE0});
const butter_geometry = new THREE.BoxGeometry(7, 4, 1.2);
const butter_mesh = new THREE.Mesh(butter_geometry, butter_material);

class Butter extends VariablyAcceleratable {

	constructor(x, y, z) {
		super();

		let clone = butter_mesh.clone();
		this.add(clone);
		this.position.set(x, y, z);

		this.update_radius();
	}


	update_radius() {
		this.sphere_radius = Math.sqrt(3.5 ** 2 + 2 ** 2);

		this.dirty_radius = false;
	}


	resolve_collision(other_node) {
		//do nothing
	}
}

class Car extends VariablyAcceleratable {

	constructor(width, length, wheel_external_diameter, wheel_width) {
		super();

		carMaterial = new THREE.MeshBasicMaterial({color: 0xffffff});

		this.mass = 3;

		this.width = width;
		this.length = length;
		this.part1_height = 1;
		this.part2_height = 0.5;

		this.axle_height = wheel_external_diameter / 2;
		this.wheel_radius = (wheel_external_diameter - wheel_width) / 2;
		this.wheel_tube = wheel_width / 2;
		this.total_axle_length = this.width + this.wheel_tube * 2;

		this.addBody();
		this.addWheels();

		this.update_radius();
	}


	update_radius() {
		let half_total_width = this.width / 2 + this.wheel_tube * 2;
		let half_total_height = this.width + (this.length - this.width) / 2;

		this.sphere_radius = Math.sqrt(half_total_width * half_total_width + half_total_height * half_total_height);

		this.dirty_radius = false;
	}

	addBody() {
		const part1_geom = new THREE.BoxGeometry(this.width, this.length, this.part1_height);
		const part1 = new THREE.Mesh(part1_geom, carMaterial);
		const part2_geom = new THREE.BoxGeometry(this.width, this.width, this.part2_height);
		const part2 = new THREE.Mesh(part2_geom, carMaterial);

		part1.position.y = -this.width / 2; //move back so that car center is the front axel
		part1.position.z = this.part1_height / 2 + this.axle_height;
		this.add(part1);

		part2.position.y = -this.width / 2; //move back so that car center is the front axel
		part2.position.z = this.part2_height / 2 + this.part1_height + this.axle_height;
		this.add(part2);
	}

	addWheels() {
		const geometry = new THREE.TorusGeometry(this.wheel_radius, this.wheel_tube, 8, 10);
		const wheel = new THREE.Mesh(geometry, carMaterial);
		wheel.rotateY(Math.PI / 2);

		addCloneAtPosition(this, wheel, this.total_axle_length / 2, 0, this.axle_height);
		addCloneAtPosition(this, wheel, -this.total_axle_length / 2, 0, this.axle_height);
		addCloneAtPosition(this, wheel, -this.total_axle_length / 2, -this.width, this.axle_height);
		addCloneAtPosition(this, wheel, this.total_axle_length / 2, -this.width, this.axle_height);
	}


	resolve_collision(other_node) {
		if (other_node instanceof Butter) {
			let new_direction = this.unit_vector_to(other_node).negate();
			let over_movement = this.sphere_radius + other_node.sphere_radius - Math.sqrt(this.distanceSq_to(other_node));
			this.translateOnAxis(this.front.clone().negate(), over_movement); //We've overstepped, go back!
			if (over_movement > 0)
				this.dirty_center = true;

			this.speed = 0;
		}
	}
}

// === OBJECTS END ===


// === SCENE INIT ===

function createCar(x, y, z) {
	car = new Car(3, 5, 0.9, 0.3);
	car.position.set(x, y, z);
	scene.add(car);
}

function addGround(parent, x, y, z) {
	groundMaterial = new THREE.MeshBasicMaterial({color: 0x45525F});
	const geometry = new THREE.BoxGeometry(tableLength, tableLength, tableHeight);
	const mesh = new THREE.Mesh(geometry, groundMaterial);
	mesh.name = "Ground";
	mesh.position.set(x, y, z - tableHeight / 2);
	parent.add(mesh);
}

function addCheerios(parent) {
	const cheerios = new THREE.Group();
	cheerios.name = "Cheerios";

	const number_of_cheerios = 64;

	const anglePerCheerio = (2 * Math.PI) / number_of_cheerios;
	const outerRadius = (tableLength - cheerioSize * 4) / 2;
	const innerRadius = (tableLength - cheerioSize * 4) / 2 * 2 / 3;

	for (let i = 0, alpha = 0; i < number_of_cheerios; i++, alpha += anglePerCheerio) {
		cheerios.add(new Cheerio(Math.cos(alpha) * outerRadius, Math.sin(alpha) * outerRadius, 0));
		cheerios.add(new Cheerio(Math.cos(alpha) * innerRadius, Math.sin(alpha) * innerRadius, 0));
	}

	parent.add(cheerios);
}

function addButters(parent) {
	parent.add(new Butter(-49, 33, 0.8));
	parent.add(new Butter(-67, -78, 0.8));
	parent.add(new Butter(30, -27, 0.8));
	parent.add(new Butter(32, 45, 0.8));
	parent.add(new Butter(-14, 79, 0.8));
}

function addOranges(parent) {
	const geometry = new THREE.SphereGeometry(6, 16, 16);
	orangeMaterial = new THREE.MeshBasicMaterial({color: 0xff8c00});
	const baseOrange = new THREE.Mesh(geometry, orangeMaterial);
	baseOrange.name = 'Orange';

	addCloneAtPosition(parent, baseOrange, -33, -29, 0.8);
	addCloneAtPosition(parent, baseOrange, 67, -78, 0.8);
}

function createTrack(x, y, z) {
	const track = new THREE.Object3D();
	track.name = "Track";
	addGround(track, x, y, z);
	addCheerios(track);
	addButters(track);
	addOranges(track);
	track.position.set(x, y, z);
	scene.add(track);
}

function createScene() {
	scene = new THREE.Scene();
	scene.add(new THREE.AxisHelper(10));
	createTrack(0, 0, 0);
	createCar(65, -65, 0);
}

// === SCENE INIT END ===


// === EVENT LISTENERS ===

function onResize() {
	renderer.setSize(window.innerWidth, window.innerHeight);

	if (window.innerHeight > 0 && window.innerWidth > 0) {
		const aspectRatio = window.innerWidth / window.innerHeight;
		if (aspectRatio > 1) {
			camera.left = -(tableLength / 2) * aspectRatio;
			camera.right = (tableLength / 2) * aspectRatio;
			camera.top = (tableLength / 2);
			camera.bottom = -(tableLength / 2);
		} else {
			camera.left = -(tableLength / 2);
			camera.right = (tableLength / 2);
			camera.top = (tableLength / 2) / aspectRatio;
			camera.bottom = -(tableLength / 2) / aspectRatio;
		}
		camera.updateProjectionMatrix();
	}
}

function onKeyDown(e) {
	switch (e.keyCode) {
		case 65:
		case 97:
			groundMaterial.wireframe = !groundMaterial.wireframe;
			carMaterial.wireframe = !carMaterial.wireframe;
			orangeMaterial.wireframe = !orangeMaterial.wireframe;
			butterMaterial.wireframe = !butterMaterial.wireframe;
			cheerio_material.wireframe = !cheerio_material.wireframe;
			break;
		case 38: //up
			car.acceleration = 20;
			break;
		case 40: //down
			car.acceleration = -10;
			break;
		case 37: //left
			car.angularVelocity = 1;
			break;
		case 39:
			car.angularVelocity = -1;
			break;
	}
}

function onKeyUp(e) {
	switch (e.keyCode) {
		case 38:
		case 40:
			car.acceleration = 0;
			break;
		case 37:
		case 39:
			car.angularVelocity = 0;
			break;
	}
}

// === EVENT LISTENERS END ===

function render() {
	renderer.render(scene, camera);
}

function animate() {
	const delta = clock.getDelta();

	scene.traverse(function (node) {

		if (node instanceof VariablyAcceleratable) {
			node.update(delta)
		}

		if (node instanceof Collidable) {

			if (node.dirty_center)
				node.update_center();

			if (node.dirty_radius)
				node.update_radius();

			scene.traverse(function (other_node) {
				if (node !== other_node) {
					if (other_node instanceof Collidable) {
						if (node.sphere_collides(other_node)) {
							node.resolve_collision(other_node, delta);
						}
					}
				}
			})
		}


	});

	render();

	requestAnimationFrame(animate);

}

function init() {
	renderer = new THREE.WebGLRenderer({
		antialias: true
	});

	renderer.setSize(window.innerWidth, window.innerHeight);
	document.body.appendChild(renderer.domElement);

	createScene();
	createCamera();

	clock = new THREE.Clock();

	window.addEventListener("resize", onResize);
	window.addEventListener("keydown", onKeyDown);
	window.addEventListener("keyup", onKeyUp);
}


function createCamera() {
	const aspectRatio = window.innerWidth / window.innerHeight;
	if (aspectRatio > 1)
		camera = new THREE.OrthographicCamera(-(tableLength / 2) * aspectRatio, (tableLength / 2) * aspectRatio, (tableLength / 2), -(tableLength / 2), 1, 200);
	else
		camera = new THREE.OrthographicCamera(-(tableLength / 2), (tableLength / 2), (tableLength / 2) / aspectRatio, -(tableLength / 2) / aspectRatio, 1, 200);

	camera.position.x = 0;
	camera.position.y = 0;
	camera.position.z = 150;
	camera.up.set(0, 1, 0);
	camera.lookAt(scene.position);
}
