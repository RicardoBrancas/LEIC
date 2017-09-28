'use strict';

var camera, scene, renderer;


// SCENE INIT

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

	addGround(track, 0, 0, -1);
	addCheerios(track);
	scene.add(track);
	track.position.x = x;
	track.position.y = y;
	track.position.z = z;
}

function createScene() {
	scene = new THREE.Scene();
	scene.add(new THREE.AxisHelper(10));
	createTrack(0, 0, 0);
}

// SCENE INIT END


// EVENT LISTENERS

function onResize() {
	renderer.setSize(window.innerWidth, window.innerHeight);

	if (window.innerHeight > 0 && window.innerWidth > 0) {
		camera.aspect = renderer.getSize().width / renderer.getSize().height;
		camera.updateProjectionMatrix();
	}

	render();
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
	}
	render();
}

// EVENT LISTENERS END

function render() {

	renderer.render(scene, camera);
}

function animate() {
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

	window.addEventListener("resize", onResize);
	window.addEventListener("keydown", onKeyDown);
}


function createCamera() {
	camera = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 1, 1000);

	camera.position.x = 0;
	camera.position.y = 0;
	camera.position.z = 150;
	camera.up.set(0, 1, 0);
	camera.lookAt(scene.position);
}
