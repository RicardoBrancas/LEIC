'use strict';

let camera, camera1, camera2, camera3, scene, renderer;
let clock;

let car;

let carMaterial, groundMaterial;

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

		if (this.speed !== 0)
			this.dirty_center = true;
	}
}

const cheerio_material = new THREE.MeshBasicMaterial({
	color: 0xFFD700
});
const cheerio_geometry = new THREE.TorusGeometry(cheerioSize, cheerioSize / 2, 8, 10);
const cheerio_mesh = new THREE.Mesh(cheerio_geometry, cheerio_material);

class Cheerio extends VariablyAcceleratable {

	constructor(x, y, z) {
		super();

		this.drag = 0.8;

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

		if (other_node instanceof Car) {
			let new_direction = this.unit_vector_to(other_node).negate();
			let over_movement = this.sphere_radius + other_node.sphere_radius - Math.sqrt(this.distanceSq_to(other_node));

			this.translateOnAxis(new_direction, over_movement); //We've overstepped, go back!
			if (over_movement > 0) {
				this.dirty_center = true;
			}

			this.front = new_direction;
			this.speed = Math.max(this.speed, Math.abs(other_node.speed));
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

const orange_geometry = new THREE.SphereGeometry(6, 16, 16);
const orange_material = new THREE.MeshBasicMaterial({color: 0xff8c00});
const leaf_material = new THREE.MeshBasicMaterial({color: 0x05581c})
const orange_mesh = new THREE.Mesh(orange_geometry, orange_material);

class Orange extends VariablyAcceleratable {

	constructor(x, y, z) {
		super();

        this.addLeaf();
		let clone = orange_mesh.clone();
		this.add(clone);
		this.position.set(x, y, z);
        this.drag = 0.1;
        this.speed = Math.floor(Math.random() * 10);
        this.acceleration = 5;
        this.front.applyAxisAngle(this.up, Math.random() * 2 * Math.PI);

		this.update_radius();
	}

	addLeaf() {
		const leaf_geom = new THREE.BoxGeometry(3, 3, 0.1);
		const leaf = new THREE.Mesh(leaf_geom, leaf_material);
		const stalk_geom = new THREE.BoxGeometry(0.5, 0.5, 2);
		const stalk = new THREE.Mesh(stalk_geom, leaf_material);

		leaf.rotateY(Math.PI / 3);
		leaf.position.x = 6.5;
		leaf.position.z = 3;
        this.add(leaf);
        stalk.rotateY(Math.PI / 3);
        stalk.position.x = 5.5;
		stalk.position.z = 3;
        this.add(stalk);
	}

    update_radius() {
        this.sphere_radius = 6;
        this.dirty_radius = false;
    }

    update(delta) {
		super.update(delta);
        let pos = this.getWorldPosition();

        if (Math.abs(pos.getComponent(0)) > 105 || (Math.abs(pos.getComponent(1)) > 105)) {
            this.position.set(150, 150, 5);
            this.visible = false;
            this.speed = 0;
            this. acceleration = 0;
        }
	}

	resolve_collision(other_node, delta) {
		//do nothing
	}
}

class Car extends VariablyAcceleratable {

	constructor(width, length, wheel_external_diameter, wheel_width) {
		super();

		carMaterial = new THREE.MeshBasicMaterial({
			color: 0xffffff
		});

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
		let half_total_height = this.length / 2;

		this.sphere_radius = Math.sqrt(half_total_width * half_total_width + half_total_height * half_total_height);

		this.dirty_radius = false;
	}

	addBody() {
		const part1_geom = new THREE.BoxGeometry(this.width, this.length, this.part1_height);
		const part1 = new THREE.Mesh(part1_geom, carMaterial);
		const part2_geom = new THREE.BoxGeometry(this.width, this.width, this.part2_height);
		const part2 = new THREE.Mesh(part2_geom, carMaterial);

		part1.position.z = this.part1_height / 2 + this.axle_height;
		this.add(part1);

		part2.position.z = this.part2_height / 2 + this.part1_height + this.axle_height;
		this.add(part2);
	}

	addWheels() {
		const geometry = new THREE.TorusGeometry(this.wheel_radius, this.wheel_tube, 8, 10);
		const wheel = new THREE.Mesh(geometry, carMaterial);
		wheel.rotateY(Math.PI / 2);

		addCloneAtPosition(this, wheel, this.total_axle_length / 2, this.width / 2, this.axle_height);
		addCloneAtPosition(this, wheel, -this.total_axle_length / 2, this.width / 2, this.axle_height);
		addCloneAtPosition(this, wheel, -this.total_axle_length / 2, -this.width / 2, this.axle_height);
		addCloneAtPosition(this, wheel, this.total_axle_length / 2, -this.width / 2, this.axle_height);
	}


	resolve_collision(other_node) {
		if (other_node instanceof Butter) {
			let over_movement = this.sphere_radius + other_node.sphere_radius - Math.sqrt(this.distanceSq_to(other_node));
			this.translateOnAxis(this.front.clone().negate(), over_movement); //We've overstepped, go back!
			if (over_movement > 0)
				this.dirty_center = true;

			this.speed = 0;
		} else if (other_node instanceof Orange) {
			window.location.reload();
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
	groundMaterial = new THREE.MeshBasicMaterial({
		color: 0x45525F
	});
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
	parent.add(new Orange(-33, -29, 5));
    parent.add(new Orange(-33, 69, 5));

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
			camera.aspect = aspectRatio;
		} else {
			camera.left = -(tableLength / 2);
			camera.right = (tableLength / 2);
			camera.top = (tableLength / 2) / aspectRatio;
			camera.bottom = -(tableLength / 2) / aspectRatio;
			camera.aspect = aspectRatio;
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
			orange_material.wireframe = !orange_material.wireframe;
			leaf_material.wireframe = !leaf_material.wireframe;
			butter_material.wireframe = !butter_material.wireframe;
			cheerio_material.wireframe = !cheerio_material.wireframe;
			break;
		case 38: //up
			car.acceleration = 20;
			break;
		case 40: //down
			car.acceleration = -40;
			break;
		case 37: //left
			car.angularVelocity = 1;
			break;
		case 39:
			car.angularVelocity = -1;
			break;
		case 49:
			camera = camera1;
			break;
		case 50:
			camera = camera2;
			break;
		case 51:
			camera = camera3;
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
		if (node instanceof Orange) {
			if (!node.visible) {
                node.visible = true;
                setTimeout(function () {
                    node.position.set(Math.random() * 180 - 90, Math.random() * 180 - 90, 5);
                    node.speed = Math.floor(Math.random() * 10);
                    node.acceleration = 5;
                    node.front.applyAxisAngle(node.up, Math.random() * 2 * Math.PI);
                }, 0)
				clearTimeout();
            }
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
		camera1 = new THREE.OrthographicCamera(-(tableLength / 2) * aspectRatio, (tableLength / 2) * aspectRatio, (tableLength / 2), -(tableLength / 2), 1, 200);
	else
		camera1 = new THREE.OrthographicCamera(-(tableLength / 2), (tableLength / 2), (tableLength / 2) / aspectRatio, -(tableLength / 2) / aspectRatio, 1, 200);

	camera1.position.x = 0;
	camera1.position.y = 0;
	camera1.position.z = 150;
	camera1.up.set(0, 1, 0);
	camera1.lookAt(scene.position);

	camera = camera1;

	camera2 = new THREE.PerspectiveCamera(90, aspectRatio, 1, 1000);
	camera2.position.x = 0;
	camera2.position.y = 0;
	camera2.position.z = 100;
	camera2.up.set(0, 1, 0);
	camera2.lookAt(scene.position);

	camera3 = new THREE.PerspectiveCamera(90, aspectRatio, 1, 1000);
	camera3.position.x = 0;
	camera3.position.y = -13;
	camera3.position.z = 12;
	camera3.up.set(0, 1, 0);
	camera3.lookAt(scene.position);
	car.add(camera3)
}
