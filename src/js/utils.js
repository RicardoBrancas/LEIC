'use strict';

function addCloneAtPosition(parent, object, x, y, z) {
	const clone = object.clone();
	clone.position.set(x, y, z);
	parent.add(clone);
}