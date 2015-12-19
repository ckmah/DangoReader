// autoplay next video using
function nextVideo(index) {
    var nextIndex = (index + 1) % 3;
    $('#feature-buttons > a').eq(nextIndex).click();
}

// play/pause toggle
function playToggle(video) {
    if (video.paused) {
        video.play();
    } else {
        video.pause();
    }
}

function resetVideo(vid) {
    vid.className = '';
    vid.currentTime = 0;
    vid.pause();
}

$(document).ready(function() {

    $.material.init();
    $('[data-toggle="tooltip"]').tooltip();

    // init videos
    $('#video-container > video').each(function(index, video) {
        resetVideo(video);
        video.pause();
        video.onclick = function() {
            playToggle(video);
        };
        // video.addEventListener('click', playToggle(video));
        video.onended = function() {
            nextVideo(index);
        };
    });

    // button click switch to corresponding video
    $('#feature-buttons > a').click(function(event) {
        event.preventDefault();
        var featureIndex = $(this).index();
        $('#video-container > video').each(function(index, video) {
            if (index == featureIndex) {
                video.className = 'video-active';
                console.log(index);
                if (video.paused) {
                    video.play();
                }
            } else {
                resetVideo(video);
            }
        });
    });

    // initial video start
    $('#feature-buttons a')[0].click();


});
