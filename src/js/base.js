let camera, camera1, camera2, camera3, scene, renderer;
let clock;

let car;

const table_length = 200;
const table_half_length = table_length / 2;
const tableHeight = 2;

let current_level = 1;

let camera_needs_update = false;

let candles = [];
let switch_candles = false;

class Limits {}

const limits = new Limits(); //used for collisions

const cheerioSize = 1;

// === HELPER FUNCTIONS ===

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

// === HELPER FUNCTIONS END ===


// === MATERIALS AND SCENE LIGHT ===

const sunLight = new THREE.DirectionalLight(defaultStatus, 1.5);

sunLight.position.x = 1;
sunLight.position.z = 3;

let switchDay = false;
let switchBasic = false;

let basicMaterials = {};
let phongMaterials = {};

function create_material(name, color, emissive = false) {
  let basic = new THREE.MeshBasicMaterial({
    color: color
  });
  basic.name = name;
  basicMaterials[name] = basic;

  let phong = new THREE.MeshPhongMaterial({
    color: color,
    emissive: emissive ? color : 0x000000
  });
  phong.name = name;
  phongMaterials[name] = phong;

  return basic;
}

let car_material = create_material('car', 0xffffff);
let ground_material = create_material('ground', 0x45525F);
let wax_material = create_material('wax', 0xefe6d3);
let flame_material = create_material('flame', 0xe71837, emissive = true);
let cheerio_material = create_material('cheerio', 0xFFD700);
let butter_material = create_material('butter', 0xFFFFE0);
let orange_material = create_material('orange', 0xff8c00);
let leaf_material = create_material('leaf', 0x05581c);


// === MATERIALS AND SCENE LIGHT END ===


// === OBJECTS ===

const candle_light_color = 0xffffff;

let candle_group = new THREE.Group();
let wax_geometry = new THREE.CylinderGeometry(0.6, 0.4, 4, 8);
let wax_mesh = new THREE.Mesh(wax_geometry, wax_material);
wax_mesh.position.z = 4 / 2;
wax_mesh.rotateX(-Math.PI / 2);
candle_group.add(wax_mesh);
let flame_geometry = new THREE.ConeGeometry(0.3, 1, 3);
let flame_mesh = new THREE.Mesh(flame_geometry, flame_material);
flame_mesh.rotateX(Math.PI / 2);
flame_mesh.position.z = 4 + (1 / 2);
candle_group.add(flame_mesh);

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

const cheerio_geometry = new THREE.TorusGeometry(cheerioSize, cheerioSize / 2, 8, 10);
let cheerio_mesh = new THREE.Mesh(cheerio_geometry, cheerio_material);

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
    this.sphere_radius = cheerioSize * 1.5;
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

const butter_geometry = new THREE.BoxGeometry(7, 4, 1.2);
let butter_mesh = new THREE.Mesh(butter_geometry, butter_material);

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

const orange_radius = 6;

const orange_geometry = new THREE.SphereGeometry(orange_radius, 16, 16);
let orange_mesh = new THREE.Mesh(orange_geometry, orange_material);

const leaf_geometry = new THREE.BoxGeometry(3, 3, 0.1);
let leaf_mesh = new THREE.Mesh(leaf_geometry, leaf_material);
const stalk_geometry = new THREE.BoxGeometry(0.5, 0.5, 2);
let stalk_mesh = new THREE.Mesh(stalk_geometry, leaf_material);

const orange_group = new THREE.Group();
orange_group.add(orange_mesh);
leaf_mesh.rotateY(Math.PI / 3);
leaf_mesh.position.x = 6.5;
leaf_mesh.position.z = 3;
orange_group.add(leaf_mesh);
stalk_mesh.rotateY(Math.PI / 3);
stalk_mesh.position.x = 5.5;
stalk_mesh.position.z = 3;
orange_group.add(stalk_mesh);

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

    setTimeout(function(object) {
      return function() {
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
    let part1_geom = new THREE.Geometry();

    part1_geom.vertices.push(
      new THREE.Vector3(-1.5, 2.5, -0.5),
      new THREE.Vector3(1.5, 2.5, -0.5),
      new THREE.Vector3(1.5, -2.5, -0.5),
      new THREE.Vector3(-1.5, -2.5, -0.5),
      new THREE.Vector3(-1.5, 2.5, 0.5),
      new THREE.Vector3(1.5, 2.5, 0.5),
      new THREE.Vector3(1.5, -2.5, 0.5),
      new THREE.Vector3(-1.5, -2.5, 0.5),
      new THREE.Vector3(1.5, 2.75, 0.25),
      new THREE.Vector3(1.5, 2.75, -0.25),
      new THREE.Vector3(-1.5, 2.75, -0.25),
      new THREE.Vector3(-1.5, 2.75, 0.25),
      new THREE.Vector3(1.5, -2.75, 0.25),
      new THREE.Vector3(1.5, -2.75, -0.25),
      new THREE.Vector3(-1.5, -2.75, -0.25),
      new THREE.Vector3(-1.5, -2.75, 0.25)
    );

    part1_geom.faces.push(
      new THREE.Face3(0, 1, 2),
      new THREE.Face3(2, 3, 0),
      new THREE.Face3(4, 7, 6),
      new THREE.Face3(6, 5, 4),
      new THREE.Face3(4, 0, 3),
      new THREE.Face3(3, 7, 4),
      new THREE.Face3(1, 5, 6),
      new THREE.Face3(6, 2, 1),
      new THREE.Face3(0, 4, 5),
      new THREE.Face3(5, 1, 0),
      new THREE.Face3(2, 6, 7),
      new THREE.Face3(7, 3, 2),
      new THREE.Face3(8, 9, 10),
      new THREE.Face3(10, 11, 8),
      new THREE.Face3(4, 5, 8),
      new THREE.Face3(8, 11, 4),
      new THREE.Face3(5, 1, 9),
      new THREE.Face3(9, 8, 5),
      new THREE.Face3(0, 4, 11),
      new THREE.Face3(11, 10, 0),
      new THREE.Face3(1, 0, 10),
      new THREE.Face3(10, 9, 1),
      new THREE.Face3(15, 14, 13),
      new THREE.Face3(13, 12, 15),
      new THREE.Face3(12, 6, 7),
      new THREE.Face3(7, 15, 12),
      new THREE.Face3(3, 2, 13),
      new THREE.Face3(13, 14, 3),
      new THREE.Face3(15, 7, 3),
      new THREE.Face3(3, 14, 15),
      new THREE.Face3(6, 12, 13),
      new THREE.Face3(13, 2, 6),
    );

    part1_geom.computeFaceNormals();
    const part1 = new THREE.Mesh(part1_geom, car_material);
    part1.position.z = this.part1_height / 2 + this.axle_height;
    this.add(part1);

    let part2_geom = new THREE.Geometry();
    part2_geom.vertices.push(
			new THREE.Vector3(-1.5, 1.5, -0.25),
			new THREE.Vector3(1.5, 1.5, -0.25),
			new THREE.Vector3(1.5, -1.5, -0.25),
			new THREE.Vector3(-1.5, -1.5, -0.25),
			new THREE.Vector3(-1.5, 1.25, 0.25),
			new THREE.Vector3(1.5, 1.25, 0.25),
			new THREE.Vector3(1.5, -1.25, 0.25),
			new THREE.Vector3(-1.5, -1.25, 0.25)
		);

		part2_geom.faces.push(
      new THREE.Face3(0, 1, 2),
      new THREE.Face3(2, 3, 0),
			new THREE.Face3(5, 4, 7),
      new THREE.Face3(7, 6, 5),
			new THREE.Face3(1, 5, 6),
      new THREE.Face3(6, 2, 1),
			new THREE.Face3(2, 6, 7),
      new THREE.Face3(7, 3, 2),
			new THREE.Face3(4, 0, 3),
      new THREE.Face3(3, 7, 4),
			new THREE.Face3(4, 5, 1),
      new THREE.Face3(1, 0, 4),
    );
		part2_geom.computeFaceNormals();
    const part2 = new THREE.Mesh(part2_geom, car_material);
    part2.position.z = this.part2_height / 2 + this.part1_height + this.axle_height;
    this.add(part2);
  }

  addWheels() {
    const geometry = new THREE.TorusGeometry(this.wheel_radius, this.wheel_tube, 8, 10);
    const wheel = new THREE.Mesh(geometry, car_material);
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
      window.location.reload();
    }
  }
}

// === OBJECTS END ===

// === SCENE INIT ===

function createCar(x, y, z) {
  car = new Car(3, 5, 0.9, 0.3);
  car.position.set(x, y, z);
  scene.add(car);
}

function addGround(parent, x, y, z) {
  const geometry = new THREE.BoxGeometry(table_length, table_length, tableHeight, 20, 20);
  geometry.computeFaceNormals();
  const mesh = new THREE.Mesh(geometry, ground_material);
  mesh.name = "Ground";
  mesh.position.set(x, y, z - tableHeight / 2);
  parent.add(mesh);
}

function addCheerios(parent) {
  const cheerios = new THREE.Group();
  cheerios.name = "Cheerios";

  const number_of_cheerios = 64;

  const anglePerCheerio = (2 * Math.PI) / number_of_cheerios;
  const outerRadius = (table_length - cheerioSize * 4) / 2 * 8 / 9;
  const innerRadius = (table_length - cheerioSize * 4) / 2 * 4 / 7;

  for (let i = 0, alpha = 0; i < number_of_cheerios; i++, alpha += anglePerCheerio) {
    cheerios.add(new Cheerio(Math.cos(alpha) * outerRadius, Math.sin(alpha) * outerRadius, 0));
    cheerios.add(new Cheerio(Math.cos(alpha) * innerRadius, Math.sin(alpha) * innerRadius, 0));

  }

  parent.add(cheerios);
}

function addButters(parent) {
  parent.add(new Butter(-49, 33, 0.8));
  parent.add(new Butter(-67, -78, 0.8));
  parent.add(new Butter(30, -27, 0.8));
  parent.add(new Butter(32, 45, 0.8));
  parent.add(new Butter(-14, 79, 0.8));
}

function addOranges(parent) {
  parent.add(new Orange());
  parent.add(new Orange());
  parent.add(new Orange());

}

function addCandles(parent, candles_n) {
  let inner = false;

  const angle_per_candle = (2 * Math.PI) / candles_n;
  const outerRadius = (table_length - cheerioSize * 4) / 2 * 8 / 9 + 10;
  const innerRadius = (table_length - cheerioSize * 4) / 2 * 4 / 7 - 10;

  for (let i = 0, alpha = 0; i < candles_n; i++, alpha += angle_per_candle) {
    let candle = new Candle(Math.cos(alpha) * (inner ? innerRadius : outerRadius), Math.sin(alpha) * (inner ? innerRadius : outerRadius), 0);
    candles.push(candle);
    parent.add(candle);
    inner = !inner;
  }
}

function createTrack(x, y, z) {
  const track = new THREE.Object3D();
  track.name = "Track";
  addGround(track, x, y, z);
  addCheerios(track);
  addButters(track);
  addOranges(track);
  addCandles(track, 6);
  track.position.set(x, y, z);
  scene.add(track);
}

function createScene() {
  scene = new THREE.Scene();
  createTrack(0, 0, 0);
  scene.add(sunLight);
  createCar(65, -65, 0);
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
      ground_material.wireframe = !ground_material.wireframe;
      car_material.wireframe = !car_material.wireframe;
      orange_material.wireframe = !orange_material.wireframe;
      leaf_material.wireframe = !leaf_material.wireframe;
      butter_material.wireframe = !butter_material.wireframe;
      cheerio_material.wireframe = !cheerio_material.wireframe;
      break;
    case 38: //up
      car.acceleration = 30;
      break;
    case 40: //down
      car.acceleration = -40;
      break;
    case 37: //left
      car.angularVelocity = 2;
      break;
    case 39:
      car.angularVelocity = -2;
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
      break;
    case 76: //L
      switchBasic = true;
      break;
    case 78: //N
      switchDay = true;
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

  if (switchDay) {
    sunLight.visible = !sunLight.visible;
    switchDay = false;
  }

  scene.traverse(function(node) {

    if (switchBasic) {
      if (node instanceof THREE.Mesh) {
        if (node.material instanceof THREE.MeshBasicMaterial)
          node.material = phongMaterials[node.material.name];

        else if (node.material instanceof THREE.MeshPhongMaterial)
          node.material = basicMaterials[node.material.name];
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

      scene.traverse(function(other_node) {
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

  switchBasic = false;

  render();

  requestAnimationFrame(animate);

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
  camera3.position.x = 10;
  camera3.position.y = 3;
  camera3.position.z = 5;
  camera3.up.set(0, 0, 1);
  camera3.lookAt(scene.position);
  car.add(camera3)
}
