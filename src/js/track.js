'use strict';

class Collidable extends THREE.Object3D {

	constructor() {
		super();

		this.dirty_radius = true;
		this.dirty_center = true;

		this.sphere_center = new THREE.Vector3();
		this.sphere_radius = 0;
	}

	update_center() {
		this.sphere_center.copy(this.getWorldPosition());
		this.dirty_center = false;
	}

	update_radius() {
		console.warn("update_radius() should be overridden!")
	}

	unit_vector_to(other_collidable) {
		return new THREE.Vector3(other_collidable.sphere_center.x - this.sphere_center.x, other_collidable.sphere_center.y - this.sphere_center.y, 0).normalize();
	}

	distanceSq_to(other_collidable) {
		return (this.sphere_center.x - other_collidable.sphere_center.x) ** 2 + (this.sphere_center.y - other_collidable.sphere_center.y) ** 2;
	}

	sphere_collides(other_collidable) {
		if (!(this.visible) || !(other_collidable.visible)) return false;
		let distance = this.distanceSq_to(other_collidable);

		return (this.sphere_radius + other_collidable.sphere_radius) ** 2 >= distance;
	}

	out_of_limits() {
		if (!(this.visible)) return false;
		return this.sphere_center.x < -table_half_length || this.sphere_center.x > table_half_length ||
			this.sphere_center.y < -table_half_length || this.sphere_center.y > table_half_length;
	}

	resolve_collision(other_node, delta) {
		console.warn("resolve_collision() should be overridden in", this)
	}

}

class VariablyAcceleratable extends Collidable {

	constructor() {
		super();

		this.up.set(0, 0, 1);

		this.drag = 0.4;

		this.acceleration = 0;
		this.speed = 0;
		this.angularVelocity = 0;
		this.front = new THREE.Vector3(0, 1, 0);
	}

	update(delta) {
		if (!(this.visible)) return;

		this.speed += this.acceleration * delta - this.speed * this.drag * delta;
		this.rotateOnAxis(this.up, Math.abs(this.speed) * this.angularVelocity * delta / 50);
		this.translateOnAxis(this.front, this.speed * delta);

		if (this.speed !== 0)
			this.dirty_center = true;
	}
}

class Track extends THREE.Object3D {

	constructor() {
		super();
		this.name = "Track";

		this.create_ground();
		this.add_cheerios();
		this.add_butters();
		this.add_oranges();
		this.add_candles();
		this.create_car();
	}

	create_ground() {
		let ground = ground_mesh.clone();
		ground.position.z = - table_height / 2;
		this.add(ground);
	}

	add_cheerios() {
		const cheerios = new THREE.Group();
		cheerios.name = "Cheerios";

		const number_of_cheerios = 64;

		const anglePerCheerio = (2 * Math.PI) / number_of_cheerios;
		const outerRadius = (table_length - cheerio_size * 4) / 2 * 8 / 9;
		const innerRadius = (table_length - cheerio_size * 4) / 2 * 4 / 7;

		for (let i = 0, alpha = 0; i < number_of_cheerios; i++, alpha += anglePerCheerio) {
			cheerios.add(new Cheerio(Math.cos(alpha) * outerRadius, Math.sin(alpha) * outerRadius, 0));
			cheerios.add(new Cheerio(Math.cos(alpha) * innerRadius, Math.sin(alpha) * innerRadius, 0));

		}

		this.add(cheerios);
	}

	add_butters() {
		this.add(new Butter(-49, 33, 0.8));
		this.add(new Butter(-67, -78, 0.8));
		this.add(new Butter(30, -27, 0.8));
		this.add(new Butter(32, 45, 0.8));
		this.add(new Butter(-14, 79, 0.8));
	}

	add_oranges() {
		this.add(new Orange());
		this.add(new Orange());
		this.add(new Orange());
	}

	add_candles() {
		let inner = false;

		const angle_per_candle = (2 * Math.PI) / number_of_candles;
		const outerRadius = (table_length - cheerio_size * 4) / 2 * 8 / 9 + 10;
		const innerRadius = (table_length - cheerio_size * 4) / 2 * 4 / 7 - 10;

		for (let i = 0, alpha = 0; i < number_of_candles; i++, alpha += angle_per_candle) {
			let candle = new Candle(Math.cos(alpha) * (inner ? innerRadius : outerRadius), Math.sin(alpha) * (inner ? innerRadius : outerRadius), 0);
			candles.push(candle);
			this.add(candle);
			inner = !inner;
		}
	}

	create_car() {
		this.car = new Car(3, 5, 0.9, 0.3);
		this.car.position.set(65, -65, 0);
		scene.add(this.car);
	}
}

class Cheerio extends VariablyAcceleratable {

	constructor(x, y, z) {
		super();

		this.drag = 0.8;

		let clone = cheerio_mesh.clone();
		this.add(clone);
		this.position.set(x, y, z);

		this.update_radius();
	}

	update_radius() {
		this.sphere_radius = cheerio_size * 1.5;
		this.dirty_radius = false;
	}

	resolve_collision(other_node, delta) {

		if ((other_node instanceof VariablyAcceleratable) && !(other_node instanceof Orange)) { //if we are colliding with orange, do nothing
			let new_direction = this.unit_vector_to(other_node).negate();
			let over_movement = this.sphere_radius + other_node.sphere_radius - Math.sqrt(this.distanceSq_to(other_node));

			this.front = new_direction;
			this.speed = Math.max(this.speed, Math.abs(other_node.speed));
			this.translateOnAxis(new_direction, over_movement); //We've overstepped, go back!
			if (over_movement > 0) {
				this.dirty_center = true;
			}
		}

	}
}

class Butter extends VariablyAcceleratable {

	constructor(x, y, z) {
		super();

		let clone = butter_mesh.clone();
		this.add(clone);
		this.position.set(x, y, z);

		this.update_radius();
	}

	update_radius() {
		this.sphere_radius = Math.sqrt(3.5 ** 2 + 2 ** 2);
		this.dirty_radius = false;
	}

	resolve_collision(other_node) {
		//do nothing
	}
}

class Orange extends VariablyAcceleratable {

	constructor() {
		super();

		this.drag = 0.1;
		this.acceleration = 5;

		this.inner_orange = orange_group.clone();
		this.add(this.inner_orange);

		this.reset_and_randomize();
		this.update_radius();
	}

	hide() {
		this.visible = false;

		setTimeout(function (object) {
			return function () {
				object.reset_and_randomize();
			}
		}(this), (Math.random() + 0.5) * 1000 * 3)
	}

	reset_and_randomize() {
		let x = (Math.random() * 2 - 1) * table_half_length;
		let y = (Math.random() * 2 - 1) * table_half_length;
		this.position.set(x, y, orange_radius);
		this.update_center();

		this.speed = Math.floor(Math.random() * 10 * current_level + current_level);
		this.rotateZ(Math.random() * 2 * Math.PI);

		this.visible = true;
	}

	update_radius() {
		this.sphere_radius = 6;
		this.dirty_radius = false;
	}

	update(delta) {
		super.update(delta);
		this.inner_orange.rotateX(-this.speed * delta / orange_radius);
	}

	resolve_collision(other_node, delta) {
		if (other_node instanceof Limits) {
			this.hide();
		}
	}
}

class Candle extends THREE.Object3D {

	constructor(x, y, z) {
		super();

		this.light = new THREE.PointLight(candle_light_color, 1, 100);
		this.light.position.z = 4 + (1 / 2);
		this.add(this.light);

		this.add(candle_group.clone());

		this.position.set(x, y, z)
	}
}

class Car extends VariablyAcceleratable {

	constructor(width, length, wheel_external_diameter, wheel_width) {
		super();

		this.width = width;
		this.length = length;
		this.part1_height = 1;
		this.part2_height = 0.5;

		this.axle_height = wheel_external_diameter / 2;
		this.wheel_radius = (wheel_external_diameter - wheel_width) / 2;
		this.wheel_tube = wheel_width / 2;
		this.total_axle_length = this.width + this.wheel_tube * 2;

		this.addBody();
		this.addWheels();

		this.update_radius();
	}

	update_radius() {
		let half_total_width = this.width / 2 + this.wheel_tube * 2;
		let half_total_height = this.length / 2;

		this.sphere_radius = Math.sqrt(half_total_width * half_total_width + half_total_height * half_total_height);

		this.dirty_radius = false;
	}

	addBody() {
		let body = car_body.clone();
		body.position.z = this.part1_height / 2 + this.axle_height;
		this.add(body);


		let top = car_top.clone();
		top.position.z = this.part2_height / 2 + this.part1_height + this.axle_height;
		this.add(top);

		addCloneAtPosition(this, car_headlights, 1, this.length / 2 + 0.25, this.part1_height / 2 + this.axle_height);
		addCloneAtPosition(this, car_headlights, -1, this.length / 2 + 0.25, this.part1_height / 2 + this.axle_height);

		this.headlights = [];
		for (let x = -1; x <= 1; x += 2) { //just 2 iterations
			let headlight = new THREE.SpotLight(0xffffff, 1, 32, Math.PI / 4, 0.1);
			headlight.position.set(x, this.length / 2 + 0.25, this.part1_height / 2 + this.axle_height);
			headlight.target.position.set(x, this.length / 2 + 0.25 + 50, this.part1_height / 2 + this.axle_height);
			this.add(headlight.target);
			this.add(headlight);

			this.headlights.push(headlight);
		}
	}

	addWheels() {
		const geometry = new THREE.TorusGeometry(this.wheel_radius, this.wheel_tube, 8, 10);
		const wheel = new THREE.Mesh(geometry, tires_material);
		wheel.rotateY(Math.PI / 2);

		addCloneAtPosition(this, wheel, this.total_axle_length / 2, this.width / 2, this.axle_height);
		addCloneAtPosition(this, wheel, -this.total_axle_length / 2, this.width / 2, this.axle_height);
		addCloneAtPosition(this, wheel, -this.total_axle_length / 2, -this.width / 2, this.axle_height);
		addCloneAtPosition(this, wheel, this.total_axle_length / 2, -this.width / 2, this.axle_height);
	}

	resolve_collision(other_node) {
		if (other_node instanceof Butter) {
			let over_movement = this.sphere_radius + other_node.sphere_radius - Math.sqrt(this.distanceSq_to(other_node));
			this.translateOnAxis(this.front.clone().negate(), Math.sign(this.speed) * over_movement); //We've overstepped, go back!
			if (over_movement > 0)
				this.dirty_center = true;

			this.speed = 0;
		} else if (other_node instanceof Orange) {
			// window.location.reload();
		}
	}
}