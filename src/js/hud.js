const life_spacing = 3;
const total_lives = 5;

let lives = total_lives;
let car_clones = [];

let texture_loader = new THREE.TextureLoader();

class MessageObject extends THREE.Object3D {

	constructor(path) {
		super();

		let texture = texture_loader.load(path);

		let message_geometry = new THREE.CubeGeometry(640, 800);
		message_geometry.rotateZ(Math.PI);
		let message_material = new THREE.MeshBasicMaterial({color: 0xffffff, doubleSided: true});
		message_material.map = texture;

		this.add(new THREE.Mesh(message_geometry, message_material));
		this.add(new THREE.AxisHelper(10));
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
		car.position.set(window.innerWidth/-2 + i * life_spacing * 10, window.innerHeight/-2 + 30, 0);
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