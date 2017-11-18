'use strict';

let camera, hud_camera;
let camera1, camera2, camera3;

function update_camera(camera) {
	if (window.innerWidth > 0 && window.innerHeight > 0) {
		let aspectRatio = window.innerWidth / window.innerHeight;

		if (camera instanceof THREE.OrthographicCamera) {
			if (aspectRatio > 1) {
				camera.left = -(table_length / 2) * aspectRatio;
				camera.right = (table_length / 2) * aspectRatio;
				camera.top = (table_length / 2);
				camera.bottom = -(table_length / 2);
			} else {
				camera.left = -(table_length / 2);
				camera.right = (table_length / 2);
				camera.top = (table_length / 2) / aspectRatio;
				camera.bottom = -(table_length / 2) / aspectRatio;
			}
		} else if (camera instanceof THREE.PerspectiveCamera) {
			camera.aspect = aspectRatio;
		}

		camera.updateProjectionMatrix();
	}
}

function update_hud() {
	let aspectRatio = window.innerWidth / window.innerHeight;

	hud_camera.left = -aspectRatio;
	hud_camera.right = aspectRatio;
	hud_camera.updateProjectionMatrix();
}

function reposition_camera3(old_track, new_track) {
	old_track.car.remove(camera3);
	new_track.car.add(camera3);
	update_camera(camera3);
}

function create_cameras() {
	const aspectRatio = window.innerWidth / window.innerHeight;

	if (aspectRatio > 1)
		camera1 = new THREE.OrthographicCamera(-(table_length / 2) * aspectRatio, (table_length / 2) * aspectRatio, (table_length / 2), -(table_length / 2), 1, 200);
	else
		camera1 = new THREE.OrthographicCamera(-(table_length / 2), (table_length / 2), (table_length / 2) / aspectRatio, -(table_length / 2) / aspectRatio, 1, 200);
	camera1.position.z = 150;
	camera1.up.set(0, 1, 0);
	camera1.lookAt(scene.position);

	camera2 = new THREE.PerspectiveCamera(90, aspectRatio, 1, 1000);
	camera2.position.x = 150;
	camera2.position.z = 75;
	camera2.up.set(0, 0, 1);
	camera2.lookAt(scene.position);

	camera3 = new THREE.PerspectiveCamera(90, aspectRatio, 1, 1000);
	camera3.position.y = -10;
	camera3.position.z = 7;
	camera3.up.set(0, 1, 0);
	camera3.lookAt(scene.position);
	track.car.add(camera3);

	camera = camera1;

	hud_camera = new THREE.OrthographicCamera(-aspectRatio, aspectRatio, 1, -1, 0, 600);
	hud_camera.position.z = 300;
	hud_camera.lookAt(hud_scene.position);
	hud_camera.up.set(0, 1, 0);
	hud_camera.updateProjectionMatrix();
}