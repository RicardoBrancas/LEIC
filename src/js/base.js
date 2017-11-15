'use strict';

let camera, camera1, camera2, camera3, scene, renderer;
let clock;

let current_level = 1;

let camera_needs_update = false;

let switch_candles = false;

let track;

// === MATERIALS AND SCENE LIGHT ===

const sunlight = new THREE.DirectionalLight();

sunlight.position.x = 50;
sunlight.position.y = 75;
sunlight.position.z = 100;

// === MATERIALS AND SCENE LIGHT END ===

// === SCENE INIT ===

function create_scene() {
	scene = new THREE.Scene();
	track = new Track();
	scene.add(track);
	scene.add(sunlight);
}

// === SCENE INIT END ===


// === EVENT LISTENERS ===

function onResize() {
	renderer.setSize(window.innerWidth, window.innerHeight);
	update_camera(camera);
}

function onKeyDown(e) {
	switch (e.keyCode) {
		case 65:
		case 97:
			foreach_material(material => material.wireframe = !material.wireframe);
			break;
		case 38: //up
			track.car.acceleration = 30;
			break;
		case 40: //down
			track.car.acceleration = -40;
			break;
		case 37: //left
			track.car.angularVelocity = 2;
			break;
		case 39:
			track.car.angularVelocity = -2;
			break;
		case 49:
			camera = camera1;
			camera_needs_update = true;
			break;
		case 50:
			camera = camera2;
			camera_needs_update = true;
			break;
		case 51:
			camera = camera3;
			camera_needs_update = true;
			break;
		case 67:
			switch_candles = true;
			break;
		case 71: //G
			switch_shading = true;
			break;
		case 76: //L
			switch_lighting_enabled = true;
			break;
		case 78: //N
			switch_day = true;
			break;
		case 72: //H
			switch_headlights = true;
			break;
	}
}

function onKeyUp(e) {
	switch (e.keyCode) {
		case 38:
		case 40:
			track.car.acceleration = 0;
			break;
		case 37:
		case 39:
			track.car.angularVelocity = 0;
			break;
	}
}

function onLevelIncrease() {
	current_level++;
}

// === EVENT LISTENERS END ===

function render() {
	renderer.render(scene, camera);
}

function animate() {
	const delta = clock.getDelta();

	if (camera_needs_update) {
		update_camera(camera);
		camera_needs_update = false;
	}

	if (switch_candles) {
		for (let candle of candles) {
			candle.visible = !candle.visible;
		}
		switch_candles = false;
	}

	if (switch_headlights) {
		for (let headlight of track.car.headlights) {
			headlight.visible = !headlight.visible;
		}
		switch_headlights = false;
	}

	if (switch_day) {
		sunlight.visible = !sunlight.visible;
		switch_day = false;
	}

	if (switch_lighting_enabled)
		lighting_enabled = !lighting_enabled;

	if (switch_shading)
		is_phong = !is_phong;

	scene.traverse(function (node) {

		if (switch_shading || switch_lighting_enabled) {
			if (node instanceof THREE.Mesh) {

				if (!lighting_enabled)
					node.material = basic_materials.get(node.material.name);

				else {
					if (is_phong)
						node.material = phong_materials.get(node.material.name);

					else
						node.material = lambert_materials.get(node.material.name);
				}
			}
		}

		if (node instanceof VariablyAcceleratable) {
			node.update(delta)
		}

		if (node instanceof Collidable) {

			if (node.dirty_center)
				node.update_center();

			if (node.dirty_radius)
				node.update_radius();

			if (node.out_of_limits())
				node.resolve_collision(limits, delta);

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

	switch_lighting_enabled = false;
	switch_shading = false;

	render();

	requestAnimationFrame(animate);

}

function init() {
	renderer = new THREE.WebGLRenderer({
		antialias: true
	});

	renderer.setSize(window.innerWidth, window.innerHeight);
	document.body.appendChild(renderer.domElement);

	create_scene();
	createCamera();

	clock = new THREE.Clock();

	window.addEventListener("resize", onResize);
	window.addEventListener("keydown", onKeyDown);
	window.addEventListener("keyup", onKeyUp);
	setInterval(onLevelIncrease, 10 * 1000);
}


function createCamera() {
	const aspectRatio = window.innerWidth / window.innerHeight;
	if (aspectRatio > 1)
		camera1 = new THREE.OrthographicCamera(-(table_length / 2) * aspectRatio, (table_length / 2) * aspectRatio, (table_length / 2), -(table_length / 2), 1, 200);
	else
		camera1 = new THREE.OrthographicCamera(-(table_length / 2), (table_length / 2), (table_length / 2) / aspectRatio, -(table_length / 2) / aspectRatio, 1, 200);

	camera1.position.x = 0;
	camera1.position.y = 0;
	camera1.position.z = 150;
	camera1.up.set(0, 1, 0);
	camera1.lookAt(scene.position);

	camera = camera1;

	camera2 = new THREE.PerspectiveCamera(90, aspectRatio, 1, 1000);
	camera2.position.x = 150;
	camera2.position.y = 0;
	camera2.position.z = 75;
	camera2.up.set(0, 0, 1);
	camera2.lookAt(scene.position);

	camera3 = new THREE.PerspectiveCamera(90, aspectRatio, 1, 1000);
	camera3.position.x = 0;
	camera3.position.y = -10;
	camera3.position.z = 7;
	camera3.up.set(0, 1, 0);
	camera3.lookAt(scene.position);
	track.car.add(camera3)
}
