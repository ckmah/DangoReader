// grab gulp packages
var gulp = require('gulp');
var plugins = require('gulp-load-plugins')();
var browserSync = require('browser-sync').create();
var runSequence = require('run-sequence');
var deploy = require('gulp-gh-pages');

// base folder paths
var bases = {
    source: 'source/',
    dist: 'dist/'
};

// subfolder paths
var paths = {
    fonts: ['fonts/**/*'],
    scripts: ['scripts/*.js'],
    styles: ['styles/**/*.less'],
    css: ['styles/**/*.css*'],
    html: ['*.html'],
    images: ['assets/*.{png,jpg,svg}'],
    videos: ['assets/*.mp4'],
    svgs: ['assets/*.svg'],
    libs: ['libs/**/*', '!libs/**/*.less', '!libs/**/_*'],
    misc: ['CNAME']
};

// create default task and add watch task to it
gulp.task('default', ['sequence']);

// configure jshint task
gulp.task('jshint', function() {
    return gulp.src(paths.scripts, {
            cwd: bases.source
        })
        .pipe(plugins.jshint())
        .pipe(plugins.jshint.reporter('jshint-stylish'));
});

// Delete the dist directory
gulp.task('clean', function() {
    return gulp.src(bases.dist)
        .pipe(plugins.clean());
});

// minify js and concatenate into one file
gulp.task('build-js', function() {
    return gulp.src(paths.scripts, {
            cwd: bases.source,
            base: bases.source + 'scripts/'
        }) // process original sources
        .pipe(plugins.sourcemaps.init())
        .pipe(plugins.concat('bundle.js'))
        .pipe(plugins.uglify())
        .pipe(plugins.sourcemaps.write())
        .pipe(gulp.dest(bases.dist + 'scripts/'))
        .pipe(browserSync.stream());
});

// compile less and autoprefix less
gulp.task('build-css', function() {
    return gulp.src(paths.styles, {
            cwd: bases.source,
            base: bases.source + 'styles/'
        }) // process original sources
        .pipe(plugins.less())
        .pipe(plugins.autoprefixer({
            browsers: ['last 2 versions'],
            cascade: false
        }))
        .pipe(gulp.dest(bases.dist + 'styles/'))
        .pipe(browserSync.stream());
});

// Imagemin images and svgs and ouput them in dist
gulp.task('imagemin', function() {
    gulp.src(paths.images, {
            cwd: bases.source,
            base: bases.source + 'assets/'
        })
        .pipe(plugins.imagemin())
        .pipe(gulp.dest(bases.dist + 'assets/'))
        .pipe(browserSync.stream());
});

// copy html
gulp.task('copy-html', function() {
    return gulp.src(paths.html, {
            cwd: bases.source,
            base: bases.source
        })
        .pipe(gulp.dest(bases.dist))
        .pipe(browserSync.stream());
});

// copy css
gulp.task('copy-css', function() {
    return gulp.src(paths.css, {
            cwd: bases.source,
            base: bases.source
        })
        .pipe(gulp.dest(bases.dist))
        .pipe(browserSync.stream());
});

// copy fonts
gulp.task('copy-fonts', function() {
    return gulp.src(paths.fonts, {
            cwd: bases.source,
            base: bases.source
        })
        .pipe(gulp.dest(bases.dist))
        .pipe(browserSync.stream());
});

// copy svgs
gulp.task('copy-svgs', function() {
    return gulp.src(paths.svgs, {
            cwd: bases.source,
            base: bases.source
        })
        .pipe(gulp.dest(bases.dist))
        .pipe(browserSync.stream());
});

// copy videos
gulp.task('copy-videos', function() {
    return gulp.src(paths.videos, {
            cwd: bases.source,
            base: bases.source
    })
    .pipe(gulp.dest(bases.dist))
    .pipe(browserSync.stream());
});

// copy libs files
gulp.task('copy-libs', function() {
    return gulp.src(paths.libs, {
            cwd: bases.source,
            base: bases.source
    })
    .pipe(gulp.dest(bases.dist))
    .pipe(browserSync.stream());
});

gulp.task('copy-misc', function() {
    return gulp.src(paths.misc, {
        cwd: bases.source,
        base: bases.source
    })
    .pipe(gulp.dest(bases.dist))
    .pipe(browserSync.stream());
    })

// build task
gulp.task('build', ['jshint', 'build-js', 'build-css', 'imagemin', 'copy-html', 'copy-fonts', 'copy-css', 'copy-svgs', 'copy-videos', 'copy-libs', 'copy-misc']);

// configure which files to watch and what tasks to use on file changes
gulp.task('serve', function(gulpCallback) {
    // static server
    browserSync.init({
        // server out of dist/
        server: bases.dist,
        open: false
    }, function callback() {
        // server is now up, watch files
        gulp.watch(bases.source + paths.scripts, ['jshint', 'build-js']);
        gulp.watch(bases.source + paths.styles, ['build-css']);
        gulp.watch(bases.source + paths.css, ['copy-css']);
        gulp.watch(bases.source + paths.images, ['imagemin']);
        gulp.watch(bases.source + paths.html, ['copy-html']);
        gulp.watch(bases.source + paths.fonts, ['copy-fonts']);
        gulp.watch(bases.source + paths.svgs, ['copy-svgs']);
        gulp.watch(bases.source + paths.videos, ['copy-videos']);
        gulp.watch(bases.source + paths.libs, ['copy-libs']);
        gulp.watch(bases.source + paths.misc, ['copy-misc']);
        gulpCallback();
    });
})

// run clean, build, and serve in sequence
gulp.task('sequence', function(callback) {
    runSequence('clean', 'build', 'serve', callback);
})

// deploy to gh-pages
gulp.task('deploy', function () {
    return gulp.src("dist/**/*")
        .pipe(deploy())
});
