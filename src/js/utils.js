'use strict';

function addCloneAtPosition(parent, object, x, y, z) {
	const clone = object.clone();
	clone.position.set(x, y, z);
	parent.add(clone);
}

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