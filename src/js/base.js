'use strict';

function onResize() {
	renderer.setSize(window.innerWidth, window.innerHeight);
	update_camera(camera);
	update_hud();
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
		case 82: //R
			if(waiting_for_restart)
				window.location.reload();
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
	renderer.clear();
	renderer.render(scene, camera);
	// renderer.clearDepth();
	renderer.render(hud_scene, hud_camera);
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
		track.sunlight.visible = !track.sunlight.visible;
		switch_day = false;
	}

	if (switch_lighting_enabled)
		lighting_enabled = !lighting_enabled;

	if (switch_shading)
		is_phong = !is_phong;

	scene.traverse(function (node) {

		if (switch_shading || switch_lighting_enabled || was_reset) {
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
	renderer.autoClear = false;

	renderer.setSize(window.innerWidth, window.innerHeight);
	document.body.appendChild(renderer.domElement);

	scene = new THREE.Scene();
	hud_scene = new THREE.Scene();
	track = new Track();
	scene.add(track);

	create_cameras();

	init_hud();

	clock = new THREE.Clock();

	window.addEventListener("resize", onResize);
	window.addEventListener("keydown", onKeyDown);
	window.addEventListener("keyup", onKeyUp);
	setInterval(onLevelIncrease, 10 * 1000);
}

function reset() {
	let new_track = new Track();
	reposition_camera3(track, new_track);
	scene.remove(track);
	track = new_track;
	scene.add(new_track);
	was_reset = true;
}
