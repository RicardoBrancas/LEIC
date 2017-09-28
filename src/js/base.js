'use strict';

var camera, scene, renderer;

// SCENE INIT
var carMaterial = new THREE.MeshBasicMaterial({
  color: 0xffffff,
  wireframe: true,
});

function createCar(x, y, z) {
  var car = new THREE.Object3D();
  addBody(car, x, y, z + 0.95);
  addWheel(car, x + 1.55, y + 1.5, z + 0.45);
  addWheel(car, x + 1.55, y - 1.5, z + 0.45);
  addWheel(car, x - 1.55, y + 1.5, z + 0.45);
  addWheel(car, x - 1.55, y - 1.5, z + 0.45);
  scene.add(car);
  car.position.x = x;
  car.position.y = y;
  car.position.z = z;
}

function addBody(parent, x, y, z) {
  var geometry = new THREE.BoxGeometry(3, 5, 1);
  var mesh = new THREE.Mesh(geometry, carMaterial);
  mesh.position.set(x, y, z);
  parent.add(mesh);
  geometry = new THREE.BoxGeometry(3, 3, 0.5);
  mesh = new THREE.Mesh(geometry, carMaterial);
  mesh.position.set(x, y, z + 0.75);
  parent.add(mesh);
}

function addWheel(parent, x, y, z) {
  var geometry = new THREE.TorusGeometry(0.30, 0.15, 8, 16);
  var torus = new THREE.Mesh(geometry, carMaterial);
  torus.rotateY(Math.PI / 2);
  torus.position.set(x, y, z);
  parent.add(torus);
}

function addOrange(parent, x, y, z) {

  var geometry = new THREE.SphereGeometry(6, 16, 16);
  var mesh = new THREE.Mesh(geometry, orangeMaterial);
  mesh.position.set(x, y, z);
  parent.add(mesh);
}

var orangeMaterial = new THREE.MeshBasicMaterial({
  color: 0xff8c00,
  wireframe: true,
});

function addAllOrange(parent) {
  addOrange(parent, -33, -29, 0.8);
  addOrange(parent, 67, -78, 0.8);
}

function addButter(parent, x, y, z) {

  var geometry = new THREE.BoxGeometry(7, 4, 1.2);
  var mesh = new THREE.Mesh(geometry, buttterMaterial);
  mesh.position.set(x, y, z);
  parent.add(mesh);
}

var buttterMaterial = new THREE.MeshBasicMaterial({
  color: 0xffffff,
  wireframe: true,
});

function addAllButter(parent) {
  addButter(parent, -49, 33, 0.8);
  addButter(parent, -67, -78, 0.8);
  addButter(parent, 30, -27, 0.8);
  addButter(parent, 32, 45, 0.8);
  addButter(parent, -14, 79, 0.8);
}

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

  addGround(track, x, y, z - 1);
  addCheerios(track);
  addAllButter(track);
  addAllOrange(track);
  scene.add(track);
  track.position.x = x;
  track.position.y = y;
  track.position.z = z;
}

function createScene() {
  scene = new THREE.Scene();
  scene.add(new THREE.AxisHelper(10));
  createTrack(0, 0, 0);
  createCar(40, -40, 0);
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
      scene.traverse(function(node) {
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
  camera.position.z = 200;
  camera.up.set(0, 1, 0);
  camera.lookAt(scene.position);
}
