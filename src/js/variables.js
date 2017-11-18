'use strict';

const table_length = 200;
const table_half_length = table_length / 2;
const table_height = 2;
const table_slices = 30;
const orange_radius = 6;
const candle_light_color = 0xffffff;
const number_of_candles = 6;
const cheerio_size = 1;

const car_width = 3;
const car_length = 3;
const car_body_height = 1;
const car_top_height = 0.5;
const car_wheel_external_diameter = 0.9;
const car_wheel_width = 0.3;

const car_axle_height = car_wheel_external_diameter / 2;
const car_wheel_radius = (car_wheel_external_diameter - car_wheel_width) / 2;
const car_wheel_tube = car_wheel_width / 2;
const car_total_axle_length = car_width + car_wheel_tube * 2;

let candles = [];

class Limits {
}

const limits = new Limits(); //used for collisions

let switch_day = false;
let switch_lighting_enabled = false;
let switch_shading = false;

let switch_headlights = false;

let is_phong = true;
let lighting_enabled = false;

let was_reset = false;
let waiting_for_restart = false;

let scene, renderer, hud_scene;
let clock;

let current_level = 1;

let camera_needs_update = false;

let switch_candles = false;

let track;