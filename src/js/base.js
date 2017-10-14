'use strict';

var camera, scene, renderer;
var clock;

var car;

var carMaterial, groundMaterial, orangeMaterial, butterMaterial, cheerioMaterial;

var tableLength = 200;
var tableHeight = 2;

var cheerioSize = 1;

// === HELPER FUNCTIONS ===

function addCloneAtPosition(parent, object, x, y, z) {
	var clone = object.clone();
	clone.position.set(x, y, z);
	parent.add(clone);
}

// === HELPER FUNCTIONS END ===


// === OBJECTS ===

function VariablyAcceleratableObject3D() {
	THREE.Object3D.call(this);

	this.type = 'VariablyAcceleratableObject3D';

	this.up.set(0, 0, 1);

	this.DRAG = 0.4;

	this.acceleration = 0;
	this.speed = 0;
	this.angularVelocity = 0;
	this.front = new THREE.Vector3(0, 1, 0);
}

VariablyAcceleratableObject3D.prototype = Object.assign(Object.create(THREE.Object3D.prototype), {
	constructor: VariablyAcceleratableObject3D,

	update: function (delta) {
		this.speed += this.acceleration * delta - this.speed * this.DRAG * delta;
		this.rotateOnAxis(this.up, this.speed * this.angularVelocity * delta / 50);
		this.translateOnAxis(this.front, this.speed * delta);
	}
});


function Car(width, length, wheel_external_diameter, wheel_width) {
	VariablyAcceleratableObject3D.call(this);
	this.type = 'Car';

	carMaterial = new THREE.MeshBasicMaterial({color: 0xffffff, wireframe: true});

	this.width = width;
	this.length = length;
	this.part1_height = 1;
	this.part2_height = 0.5;

	this.axle_height = wheel_external_diameter / 2;
	this.wheel_radius = (wheel_external_diameter - wheel_width) / 2;
	this.wheel_tube = wheel_width / 2;
	this.total_axle_length = this.width + this.wheel_tube*2;

	this.addBody();
	this.addWheels();
}

Car.prototype = Object.assign(Object.create(VariablyAcceleratableObject3D.prototype), {

	addBody: function() {
		var part1_geom = new THREE.BoxGeometry(this.width, this.length, this.part1_height);
		var part1 = new THREE.Mesh(part1_geom, carMaterial);
		var part2_geom = new THREE.BoxGeometry(this.width, this.width, this.part2_height);
		var part2 = new THREE.Mesh(part2_geom, carMaterial);

		part1.position.y = -this.width / 2; //move back so that car center is the front axel
		part1.position.z = this.part1_height/2 + this.axle_height;
		this.add(part1);

		part2.position.y = -this.width / 2; //move back so that car center is the front axel
		part2.position.z = this.part2_height/2 + this.part1_height + this.axle_height;
		this.add(part2);
	},

	addWheels: function() {
		var geometry = new THREE.TorusGeometry(this.wheel_radius, this.wheel_tube, 8, 10);
		var wheel = new THREE.Mesh(geometry, carMaterial);
		wheel.rotateY(Math.PI / 2);

		addCloneAtPosition(this, wheel,  this.total_axle_length / 2, 0,           this.axle_height);
		addCloneAtPosition(this, wheel, -this.total_axle_length / 2, 0,           this.axle_height);
		addCloneAtPosition(this, wheel, -this.total_axle_length / 2, -this.width, this.axle_height);
		addCloneAtPosition(this, wheel,  this.total_axle_length / 2, -this.width, this.axle_height);
	}

});

// === OBJECTS END ===


// === SCENE INIT ===

function createCar(x, y, z) {
	car = new Car(3, 5, 0.9, 0.3);
	car.position.set(x, y, z);
	scene.add(car);
}

function addGround(parent, x, y, z) {
	groundMaterial = new THREE.MeshBasicMaterial({color: 0x45525F, wireframe: true});
	var geometry = new THREE.BoxGeometry(tableLength, tableLength, tableHeight);
	var mesh = new THREE.Mesh(geometry, groundMaterial);
	mesh.name = "Ground";
	mesh.position.set(x, y, z - tableHeight / 2);
	parent.add(mesh);
}

function addCheerios(parent) {
	cheerioMaterial = new THREE.MeshBasicMaterial({color: 0xffff00, wireframe: true});
	var geometry = new THREE.TorusGeometry(cheerioSize, cheerioSize / 2, 8, 10);
	var baseCheerio = new THREE.Mesh(geometry, cheerioMaterial);

	var cheerios = new THREE.Group();
	cheerios.name = "Cheerios";

	var anglePerCheerio = (2 * Math.PI) / 64;
	var outerRadius = (tableLength - cheerioSize * 4) / 2;
	var innerRadius = (tableLength - cheerioSize * 4) / 2 * 2 / 3;

	for (var alpha = 0; alpha < 2 * Math.PI; alpha += anglePerCheerio) {
		addCloneAtPosition(cheerios, baseCheerio, Math.cos(alpha) * outerRadius, Math.sin(alpha) * outerRadius, 0);
		addCloneAtPosition(cheerios, baseCheerio, Math.cos(alpha) * innerRadius, Math.sin(alpha) * innerRadius, 0);
	}

	parent.add(cheerios);
}

function addButters(parent) {
	var geometry = new THREE.BoxGeometry(7, 4, 1.2);
	butterMaterial = new THREE.MeshBasicMaterial({color: 0xffffff, wireframe: true});
	var baseButter = new THREE.Mesh(geometry, butterMaterial);
	baseButter.name = "Butter";

	addCloneAtPosition(parent, baseButter, -49, 33, 0.8);
	addCloneAtPosition(parent, baseButter, -67, -78, 0.8);
	addCloneAtPosition(parent, baseButter, 30, -27, 0.8);
	addCloneAtPosition(parent, baseButter, 32, 45, 0.8);
	addCloneAtPosition(parent, baseButter, -14, 79, 0.8);
}

function addOranges(parent) {
	var geometry = new THREE.SphereGeometry(6, 16, 16);
	orangeMaterial = new THREE.MeshBasicMaterial({color: 0xff8c00, wireframe: true});
	var baseOrange = new THREE.Mesh(geometry, orangeMaterial);
	baseOrange.name = 'Orange';

	addCloneAtPosition(parent, baseOrange, -33, -29, 0.8);
	addCloneAtPosition(parent, baseOrange, 67, -78, 0.8);
}

function createTrack(x, y, z) {
	var track = new THREE.Object3D();
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
		var aspectRatio = window.innerWidth / window.innerHeight;
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
			cheerioMaterial.wireframe = !cheerioMaterial.wireframe;
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
	var delta = clock.getDelta();

	scene.traverse(function (node) {
		if (node instanceof VariablyAcceleratableObject3D) {
			node.update(delta)
		}
	});

	render();

	requestAnimationFrame(animate)
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
	var aspectRatio = window.innerWidth / window.innerHeight;
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
