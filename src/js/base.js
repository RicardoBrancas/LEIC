'use strict';

var camera, scene, renderer;
var material;
var clock;

var car;

var tableLength = 200;
var tableHeight = 2;

var cheerioSize = 1;

// HELPER FUNCTIONS

function addCloneAtPosition(parent, object, x, y, z) {
	var clone = object.clone();
	clone.position.set(x, y, z);
	parent.add(clone);
}

// END HELPER FUNCTIONS


// OBJECTS

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
		this.rotateOnAxis(this.up, this.angularVelocity * delta);
		this.translateOnAxis(this.front, this.speed * delta);
	}
});

// OBJECTS


// SCENE INIT

function createCar(x, y, z) {
	material = new THREE.MeshBasicMaterial({color: 0xffffff, wireframe: true});

	car = new VariablyAcceleratableObject3D();
	addBody(car, 0, 0, 0.95);
	addWheel(car, 1.55, 1.5, 0.45);
	addWheel(car, -1.55, -1.5, 0.45);
	addWheel(car, -1.55, 1.5, 0.45);
	addWheel(car, 1.55, -1.5, 0.45);
	car.position.set(x, y, z);
	scene.add(car);
}

function addBody(parent, x, y, z) {
	var geometry = new THREE.BoxGeometry(3, 5, 1);
	var mesh = new THREE.Mesh(geometry, material);
	mesh.position.set(x, y, z);
	parent.add(mesh);
	geometry = new THREE.BoxGeometry(3, 3, 0.5);
	mesh = new THREE.Mesh(geometry, material);
	mesh.position.set(x, y, z + 0.75);
	parent.add(mesh);
}

function addWheel(parent, x, y, z) {
	var geometry = new THREE.TorusGeometry(0.30, 0.15, 8, 16);
	var torus = new THREE.Mesh(geometry, material);
	torus.rotateY(Math.PI / 2);
	torus.position.set(x, y, z);
	parent.add(torus);
}

function addGround(parent, x, y, z) {
	material = new THREE.MeshBasicMaterial({color: 0x00fff0, wireframe: true});
	var geometry = new THREE.BoxGeometry(tableLength, tableLength, tableHeight);
	var mesh = new THREE.Mesh(geometry, material);
	mesh.name = "Ground";
	mesh.position.set(x, y, z - tableHeight / 2);
	parent.add(mesh);
}

function addCheerios(parent) {
	material = new THREE.MeshBasicMaterial({color: 0xffff00, wireframe: true});
	var geometry = new THREE.TorusGeometry(cheerioSize, cheerioSize / 2, 8, 16);
	var baseCheerio = new THREE.Mesh(geometry, material);

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
	material = new THREE.MeshBasicMaterial({color: 0xffffff, wireframe: true});
	var baseButter = new THREE.Mesh(geometry, material);
	baseButter.name = "Butter";

	addCloneAtPosition(parent, baseButter, -49, 33, 0.8);
	addCloneAtPosition(parent, baseButter, -67, -78, 0.8);
	addCloneAtPosition(parent, baseButter, 30, -27, 0.8);
	addCloneAtPosition(parent, baseButter, 32, 45, 0.8);
	addCloneAtPosition(parent, baseButter, -14, 79, 0.8);
}

function addOranges(parent) {
	var geometry = new THREE.SphereGeometry(6, 16, 16);
	material = new THREE.MeshBasicMaterial({color: 0xff8c00, wireframe: true});
	var baseOrange = new THREE.Mesh(geometry, material);
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

	//TODO DEBUG
	(function printGraph(obj) {
		console.group(obj.name + ' <%o> ', obj);

		obj.children.forEach(printGraph);

		console.groupEnd();
	}(scene));
}

// SCENE INIT END


// EVENT LISTENERS

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
			scene.traverse(function (node) {
				if (node instanceof THREE.Mesh) {
					node.material.wireframe = !node.material.wireframe;
				}
			});
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
		camera = new THREE.OrthographicCamera(-(tableLength / 2) * aspectRatio, (tableLength / 2) * aspectRatio, (tableLength / 2), -(tableLength / 2), 1, 200);
	else
		camera = new THREE.OrthographicCamera(-(tableLength / 2), (tableLength / 2), (tableLength / 2) / aspectRatio, -(tableLength / 2) / aspectRatio, 1, 200);

	camera.position.x = 0;
	camera.position.y = 0;
	camera.position.z = 150;
	camera.up.set(0, 1, 0);
	camera.lookAt(scene.position);
}
