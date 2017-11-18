const life_spacing = 0.01;
const total_lives = 5;

let lives = total_lives;
let car_clones = [];



class MessageObject extends THREE.Object3D {

	constructor(path) {
		super();

		let texture = texture_loader.load(path);
		let message_material = new THREE.MeshBasicMaterial({map: texture});

		this.add(new THREE.Mesh(message_geometry, message_material));
	}

}

function reset_lives() {
	lives = total_lives;
	for(let cc of car_clones) {
		hud_scene.remove(cc);
	}
	car_clones = [];
	for (let i = 1; i <= total_lives; i++) {
		let car = track.car.clone();
		car.scale.set(life_spacing, life_spacing, life_spacing);
		car.position.set(-1 + i*life_spacing*7.5, 1 - life_spacing*7.5, 0);
		hud_scene.add(car);
		car_clones.push(car);
	}
}

function decrement_lives() {
	let cc = car_clones.pop();
	hud_scene.remove(cc);
	lives--;

	if (lives === 0) {
		hud_scene.add(new MessageObject('img/end.png'));
	}
}

function init_hud() {
	reset_lives();
}