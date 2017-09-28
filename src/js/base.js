'use strict';

var camera, scene, renderer;
var clock;

var car;

// OBJECTS

function VariablyAcceleratableObject3D() {

	THREE.Object3D.call(this);

	this.type = 'VariablyAcceleratableObject3D';

	this.up.set(0, 0, 1);

	this.acceleration = 0;
	this.scalarVelocity = 0;
	this.angularVelocity = 0;
	this.angle = 0;
	this.baseVelocityDirection = new THREE.Vector3(0, -1, 0);

}

VariablyAcceleratableObject3D.prototype = Object.assign(Object.create(THREE.Object3D.prototype), {
	constructor: VariablyAcceleratableObject3D,

	update: function (delta) {
		this.scalarVelocity += this.acceleration * delta;
		this.angle += this.angularVelocity * delta;
		this.quaternion.setFromAxisAngle(this.up, this.angle);
		var dv = this.baseVelocityDirection.clone().applyQuaternion(this.quaternion);
		dv.multiplyScalar(-this.scalarVelocity * delta);
		this.position.add(dv);
	}
});

// OBJECTS


// SCENE INIT

var carMaterial = new THREE.MeshBasicMaterial({
	color: 0xffffff,
	wireframe: true,
});

function createCar(x, y, z) {
	car = new VariablyAcceleratableObject3D();
	addBody(car, 0, 0, 0.95);
	addWheel(car, 1.55, 1.5, 0.45);
	addWheel(car, -1.55, -1.5, 0.45);
	addWheel(car, -1.55, 1.5, 0.45);
	addWheel(car, 1.55, -1.5, 0.45);
	scene.add(car);
	car.position.x = x;
	car.position.y = y;
	car.position.z = z;
}

function addBody(parent, x, y, z) {
	var geometry = new THREE.BoxGeometry(3, 5, 1);
	var mesh = new THREE.Mesh(geometry, carMaterial);
	mesh.position.set(x, y, z);
	parent.add(mesh);
	geometry = new THREE.BoxGeometry(3, 3, 0.5);
	mesh = new THREE.Mesh(geometry, carMaterial);
	mesh.position.set(x, y, z + 0.75);
	parent.add(mesh);
}

function addWheel(parent, x, y, z) {
	var geometry = new THREE.TorusGeometry(0.30, 0.15, 8, 16);
	var torus = new THREE.Mesh(geometry, carMaterial);
	torus.rotateY(Math.PI / 2);
	torus.position.set(x, y, z);
	parent.add(torus);
}

function addOrange(parent, x, y, z) {

	var geometry = new THREE.SphereGeometry(6, 16, 16);
	var mesh = new THREE.Mesh(geometry, orangeMaterial);
	mesh.position.set(x, y, z);
	parent.add(mesh);
}

var orangeMaterial = new THREE.MeshBasicMaterial({
	color: 0xff8c00,
	wireframe: true
});

function addAllOrange(parent) {
	addOrange(parent, -33, -29, 0.8);
	addOrange(parent, 67, -78, 0.8);
}

function addButter(parent, x, y, z) {

	var geometry = new THREE.BoxGeometry(7, 4, 1.2);
	var mesh = new THREE.Mesh(geometry, buttterMaterial);
	mesh.position.set(x, y, z);
	parent.add(mesh);
}

var buttterMaterial = new THREE.MeshBasicMaterial({
	color: 0xffffff,
	wireframe: true
});

function addAllButter(parent) {
	addButter(parent, -49, 33, 0.8);
	addButter(parent, -67, -78, 0.8);
	addButter(parent, 30, -27, 0.8);
	addButter(parent, 32, 45, 0.8);
	addButter(parent, -14, 79, 0.8);
}

function addGround(parent, x, y, z) {
	var material = new THREE.MeshBasicMaterial({
		color: 0x00fff0,
		wireframe: true
	});

	var geometry = new THREE.BoxGeometry(200, 200, 200);
	var mesh = new THREE.Mesh(geometry, material);
	mesh.scale.set(1, 1, 0.01);
	mesh.position.set(x, y, z);
	parent.add(mesh);
}

var cheerioMaterial = new THREE.MeshBasicMaterial({
	color: 0xffff00,
	wireframe: true
});

function addCheerio(parent, x, y, z) {
	var geometry = new THREE.TorusGeometry(2, 1, 8, 16);
	var torus = new THREE.Mesh(geometry, cheerioMaterial);
	torus.position.set(x, y, z);
	parent.add(torus);
}

function addCheerios(parent) {
	for (var i = -96; i < 100; i = i + 8) {
		addCheerio(parent, -96, i, 1);
		addCheerio(parent, 96, i, 1);
		addCheerio(parent, i, -96, 1);
		addCheerio(parent, i, 96, 1);
	}

	for (var i = -64; i <= 64; i = i + 8) {
		addCheerio(parent, -64, i, 1);
		addCheerio(parent, 64, i, 1);
		addCheerio(parent, i, -64, 1);
		addCheerio(parent, i, 64, 1);
	}
}

function createTrack(x, y, z) {
	var track = new THREE.Object3D();

	addGround(track, x, y, z - 1);
	addCheerios(track);
	addAllButter(track);
	addAllOrange(track);
	scene.add(track);
	track.position.x = x;
	track.position.y = y;
	track.position.z = z;
}

function createScene() {
	scene = new THREE.Scene();
	scene.add(new THREE.AxisHelper(10));
	createTrack(0, 0, 0);
	createCar(74, -70, 0);
}

// SCENE INIT END


// EVENT LISTENERS

function onResize() {
	renderer.setSize(window.innerWidth, window.innerHeight);

	if (window.innerHeight > 0 && window.innerWidth > 0) {
		var aspectRatio = window.innerWidth / window.innerHeight;
		if (aspectRatio > 1) {
			camera.left = -100 * aspectRatio;
			camera.right = 100 * aspectRatio;
			camera.top = 100;
			camera.bottom = -100;
		} else {
			camera.left = -100;
			camera.right = 100;
			camera.top = 100 / aspectRatio;
			camera.bottom = -100 / aspectRatio;
		}
		camera.updateProjectionMatrix();
	}
}

function onKeyDown(e) {
	switch (e.keyCode) {
		case 65:
		case 97:
			scene.traverse(function (node) {
				if (node instanceof THREE.Mesh) {
					node.material.wireframe = !node.material.wireframe;
				}
			});
			break;
		case 38: //up
			car.acceleration = 2;
			break;
		case 40: //down
			car.acceleration = -2;
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

// EVENT LISTENERS END

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
		camera = new THREE.OrthographicCamera(-100 * aspectRatio, 100 * aspectRatio, 100, -100, 1, 200);
	else
		camera = new THREE.OrthographicCamera(-100, 100, 100 / aspectRatio, -100 / aspectRatio, 1, 200);

	camera.position.x = 0;
	camera.position.y = 0;
	camera.position.z = 150;
	// camera.up.set(0, 1, 0);
	camera.lookAt(scene.position);
}
