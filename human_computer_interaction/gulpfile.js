/**
 * Created by Ricardo Brancas on 17/06/2016.
 */
const gulp = require('gulp');
const del = require('del');
const runSequence = require('run-sequence');
const watch = require('gulp-watch');
const plumber = require('gulp-plumber');
const changed = require('gulp-changed');
const filter = require('gulp-filter');
const sourcemaps = require('gulp-sourcemaps');
const bowerFiles = require('main-bower-files');
const wiredep = require('wiredep').stream;
const sass = require('gulp-sass');
const includeSources = require('gulp-include-source');

var src = "src/";
var srcAssets = src + "assets/";
var dest = "out/";

gulp.task('clean', () => {
    return del([dest + "**/*"]);
});

gulp.task('bower_js', () => {
    return gulp.src(bowerFiles({overrides: {
            fancybox: {
                main: [
                    './source/jquery.fancybox.js'
                ]
            }
        }}))
        .pipe(filter(['**/*.js']))
        .pipe(gulp.dest(dest + "js/"));
});

gulp.task('bower_css', () => {
    return gulp.src(bowerFiles({overrides: {
            fancybox: {
                main: [
                    './source/jquery.fancybox.css'
                ]
            }
        }}))
        .pipe(filter(['**/*.css']))
        .pipe(gulp.dest(dest + "css/"))
});

gulp.task('bower_fonts', () => {
    return gulp.src(bowerFiles({overrides: {
            'components-font-awesome': {
                main: [
                    'css/font-awesome.css',
                    'fonts/fontawesome-webfont.woff'
                ]
            }
        }}))
        .pipe(filter(['**/*.otf', '**/*.eot', '**/*.svg', '**/*.ttf', '**/*.woff', '**/*.woff2']))
        .pipe(gulp.dest(dest + "fonts/"));
});

gulp.task('bower', ['bower_js', 'bower_css', 'bower_fonts']);

gulp.task('assets', () => {
    return gulp.src([srcAssets + "**/*", srcAssets + "**/.*"], {base: srcAssets})
        .pipe(changed(dest))
        .pipe(gulp.dest(dest));
});

gulp.task('watch:assets', () => {
    return gulp.src([srcAssets + "**/*", srcAssets + "**/.*"], {base: srcAssets})
        .pipe(watch([srcAssets + "**/*", srcAssets + "**/.*"]))
        .pipe(changed(dest))
        .pipe(gulp.dest(dest));
});

gulp.task('sass:bt', () => {
    return gulp.src(src + "sass/bootstrap-table.scss")
        .pipe(wiredep())
        .pipe(sourcemaps.init())
        .pipe(sass({outputStyle: 'expanded'}).on('error', sass.logError))
        .pipe(sourcemaps.write('./'))
        .pipe(gulp.dest(dest + "css"));
});

gulp.task('sass:t', () => {
    return gulp.src(src + "sass/table.scss")
        .pipe(wiredep())
        .pipe(sourcemaps.init())
        .pipe(sass({outputStyle: 'expanded'}).on('error', sass.logError))
        .pipe(sourcemaps.write('./'))
        .pipe(gulp.dest(dest + "css"));
});


gulp.task('watch:sass', () => {
    var sassWatcher = gulp.watch(src + 'sass/**/*.scss', ['sass']);
    sassWatcher.on('change', function(event) {
        console.log('File ' + event.path + ' was ' + event.type + ', running tasks...');
    });
});

gulp.task('watch', ['watch:assets', 'watch:sass'])

gulp.task('sass', ['sass:bt', 'sass:t']);
gulp.task('build', ['bower', 'assets', 'sass']);
gulp.task('rebuild', (cb) => {
    runSequence('clean', 'build', cb);
});
gulp.task('default', ['build']);
