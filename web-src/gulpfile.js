/**
 * Created by Gabor Molnar
 * 2016.01.23.
 */


/**
 * Gulp Plugins
 *
 */
var gulp = require('gulp');
var sass = require('gulp-sass');
var pug = require('gulp-pug');
var prettify = require('gulp-prettify');
var autoprefixer = require('gulp-autoprefixer');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var rimraf = require('gulp-rimraf');
var connect = require('gulp-connect');
var imagemin = require('gulp-imagemin');
var webpack = require("webpack-stream");
var sourcemaps = require('gulp-sourcemaps');
var rename = require('gulp-rename');
var cleanCss = require('gulp-clean-css');
var googleWebFonts = require('gulp-google-webfonts');
var modifyCssUrls = require('gulp-modify-css-urls');
var ghPages = require('gulp-gh-pages');

/**
 *  Gulp config
 */

var path = require('./gulp.config').path;
var fileName = require('./gulp.config').fileName;
var webpackConfig = require('./webpack.config.js');

/**
 * Gulp Task
 *
 * Compile app.scss to app.css
 */
gulp.task('scss', function() {
    return gulp.src(path.scss.src)
        .pipe(sass().on('error', sass.logError))
        .pipe(sourcemaps.init())
        .pipe(autoprefixer(['last 2 version', 'ie 10']))
        .pipe(cleanCss())
        .pipe(rename(fileName.scss))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest(path.scss.dest))
        .pipe(connect.reload());
});


/**
 * Gulp Task
 *
 * Download and create css for google fonts
 *
 */

gulp.task('fonts-clear',function () {
   return gulp.src(path.googleFonts.rm, {read: false})
       .pipe(rimraf());
});

gulp.task('google-fonts',['fonts-clear'], function () {
    var config = require('./gulp.config').googleFontsConfig;
    return gulp.src(path.googleFonts.src)
        .pipe(googleWebFonts(config))
        .pipe(gulp.dest(path.googleFonts.dest));
});

gulp.task('fonts-url-fix',['google-fonts'],function () {
    return gulp.src('./src/scss/base/_fonts.scss')
        .pipe(modifyCssUrls({
            modify: function (url, filePath) {
                return url;
            },
            prepend: '../'
        }))
        .pipe(gulp.dest('./src/scss/base/'));
});

gulp.task('fonts',['fonts-url-fix']);

/**
 * Gulp Task
 *
 * Get all the dependency js and concat them in vendor.js
 */
gulp.task('vendor', function (callback) {
    return gulp.src([
            // JQuery and plugins
            './bower_components/jquery/dist/jquery.js',

            // jQuery UI components
            // './bower_components/jquery.ui/ui/datepicker.js',
            // './bower_components/jquery.ui/ui/core.js',
            // './bower_components/jquery.ui/ui/widget.js',
            // './bower_components/jquery.ui/ui/mouse.js',
            // './bower_components/jquery.ui/ui/position.js',
            // './bower_components/jquery.ui/ui/menu.js',
            // './bower_components/jquery.ui/ui/autocomplete.js',
            // './bower_components/jquery.ui/ui/slider.js',

            // Tether
            './bower_components/tether/dist/js/tether.js',

            // Bootstrap components
            './bower_components/bootstrap/js/dist/alert.js',
            './bower_components/bootstrap/js/dist/collapse.js',
            './bower_components/bootstrap/js/dist/dropdown.js',
            './bower_components/bootstrap/js/dist/transition.js',
            './bower_components/bootstrap/js/dist/modal.js',
            './bower_components/bootstrap/js/dist/tab.js',
            './bower_components/bootstrap/js/dist/tooltip.js',
            './bower_components/bootstrap/js/dist/popover.js',
            './bower_components/bootstrap/js/dist/scrollspy.js',
            './bower_components/bootstrap/js/dist/dropdown.js',
            './bower_components/bootstrap/js/dist/util.js',

        ])
        .pipe(concat({path: 'vendor.js'}))
        .pipe(sourcemaps.init())
        .pipe(uglify())
        .pipe(rename(fileName.vendor))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest(path.vendor.dest));
});

/**
 * Gulp Task
 *
 * Generate app.js
 */
gulp.task('webpack', function () {
    return gulp.src(path.webpack.src)
        .pipe(webpack(webpackConfig))
        .pipe(sourcemaps.init())
        .pipe(uglify())
        .pipe(rename(fileName.webpack))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest(path.webpack.dest));
});

/**
 * Gulp Tasks
 *
 * Remove images from dist
 * Optimize images and copy them to dist
 */
gulp.task('images-optimize', function () {
    return gulp.src(path.image.src)
        .pipe(imagemin())
        .pipe(gulp.dest(path.image.dest));
});

gulp.task('images-remove', function () {
    return gulp.src(path.image.rm, {read: false})
        .pipe(rimraf());
});

gulp.task('images', ['images-remove', 'images-optimize']);

/**
 * Gulp Task
 *
 * Compile pug to html
 */
gulp.task('pug', function () {
    gulp.src([path.pug.src])
        .pipe(pug({
            pretty: false
        }))
        .pipe(prettify({
            unformatted: ['pre', 'code'],
            indent_inner_html: true,
            indegulnt_size: 2,
            brace_style: 'expand'
        }))
        .pipe(gulp.dest(path.pug.dest))
        .pipe(connect.reload());
});

/**
 * Gulp Server
 *
 * Start a server for testing your app.
 */
gulp.task('webserver', function() {
    connect.server({
        root: path.webserver.root,
        livereload: true,
        port: 8001
    });
});

/**
 * Gulp deploy
 */

gulp.task('deploy', function() {
    return gulp.src(path.deploy.dest)
        .pipe(ghPages());
});

/**
 *  Gulp watch
 *
 *  Watch all gulp tasks.
 */
gulp.task('watch', function () {
    gulp.watch(path.watch.webpack, ['webpack']);
    gulp.watch(path.watch.pug, ['pug']);
    gulp.watch(path.watch.scss, ['scss']);
    gulp.watch(path.watch.images, ['images']);
});

/**
 * Gulp Init
 *
 * Initialize all tasks and watchers.
 */
gulp.task('init', ['pug', 'scss', 'vendor', 'webpack', 'images','watch', 'webserver']);