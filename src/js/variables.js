'use strict';

const table_length = 200;
const table_half_length = table_length / 2;
const table_height = 2;
const table_slices = 30;
const orange_radius = 6;
const candle_light_color = 0xffffff;
const number_of_candles = 6;
const cheerio_size = 1;

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