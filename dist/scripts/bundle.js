function githubIcon(username) {
	// new http request to github page
	var xmlhttp = new XMLHttpRequest();
	var url = "https://api.github.com/users/" + username;

	// checks for valid site before parsing
	xmlhttp.onreadystatechange = function() {
	    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
	        var userPage = JSON.parse(xmlhttp.responseText);
	        getPicture(userPage);
	    }
	};

	xmlhttp.open("GET", url, true);
	xmlhttp.send();

	function getPicture(site) {
		var out = "";
		out += site.avatar_url;
		document.getElementById("github-icon").innerHTML = out;
	}
}

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

//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbImdpdGh1Yi1pY29uLmpzIiwic2NyaXB0cy5qcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FDdEJBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EiLCJmaWxlIjoiYnVuZGxlLmpzIiwic291cmNlc0NvbnRlbnQiOlsiZnVuY3Rpb24gZ2l0aHViSWNvbih1c2VybmFtZSkge1xyXG5cdC8vIG5ldyBodHRwIHJlcXVlc3QgdG8gZ2l0aHViIHBhZ2VcclxuXHR2YXIgeG1saHR0cCA9IG5ldyBYTUxIdHRwUmVxdWVzdCgpO1xyXG5cdHZhciB1cmwgPSBcImh0dHBzOi8vYXBpLmdpdGh1Yi5jb20vdXNlcnMvXCIgKyB1c2VybmFtZTtcclxuXHJcblx0Ly8gY2hlY2tzIGZvciB2YWxpZCBzaXRlIGJlZm9yZSBwYXJzaW5nXHJcblx0eG1saHR0cC5vbnJlYWR5c3RhdGVjaGFuZ2UgPSBmdW5jdGlvbigpIHtcclxuXHQgICAgaWYgKHhtbGh0dHAucmVhZHlTdGF0ZSA9PSA0ICYmIHhtbGh0dHAuc3RhdHVzID09IDIwMCkge1xyXG5cdCAgICAgICAgdmFyIHVzZXJQYWdlID0gSlNPTi5wYXJzZSh4bWxodHRwLnJlc3BvbnNlVGV4dCk7XHJcblx0ICAgICAgICBnZXRQaWN0dXJlKHVzZXJQYWdlKTtcclxuXHQgICAgfVxyXG5cdH07XHJcblxyXG5cdHhtbGh0dHAub3BlbihcIkdFVFwiLCB1cmwsIHRydWUpO1xyXG5cdHhtbGh0dHAuc2VuZCgpO1xyXG5cclxuXHRmdW5jdGlvbiBnZXRQaWN0dXJlKHNpdGUpIHtcclxuXHRcdHZhciBvdXQgPSBcIlwiO1xyXG5cdFx0b3V0ICs9IHNpdGUuYXZhdGFyX3VybDtcclxuXHRcdGRvY3VtZW50LmdldEVsZW1lbnRCeUlkKFwiZ2l0aHViLWljb25cIikuaW5uZXJIVE1MID0gb3V0O1xyXG5cdH1cclxufVxyXG4iLCIvLyBhdXRvcGxheSBuZXh0IHZpZGVvIHVzaW5nXHJcbmZ1bmN0aW9uIG5leHRWaWRlbyhpbmRleCkge1xyXG4gICAgdmFyIG5leHRJbmRleCA9IChpbmRleCArIDEpICUgMztcclxuICAgICQoJyNmZWF0dXJlLWJ1dHRvbnMgPiBhJykuZXEobmV4dEluZGV4KS5jbGljaygpO1xyXG59XHJcblxyXG4vLyBwbGF5L3BhdXNlIHRvZ2dsZVxyXG5mdW5jdGlvbiBwbGF5VG9nZ2xlKHZpZGVvKSB7XHJcbiAgICBpZiAodmlkZW8ucGF1c2VkKSB7XHJcbiAgICAgICAgdmlkZW8ucGxheSgpO1xyXG4gICAgfSBlbHNlIHtcclxuICAgICAgICB2aWRlby5wYXVzZSgpO1xyXG4gICAgfVxyXG59XHJcblxyXG5mdW5jdGlvbiByZXNldFZpZGVvKHZpZCkge1xyXG4gICAgdmlkLmNsYXNzTmFtZSA9ICcnO1xyXG4gICAgdmlkLmN1cnJlbnRUaW1lID0gMDtcclxuICAgIHZpZC5wYXVzZSgpO1xyXG59XHJcblxyXG4kKGRvY3VtZW50KS5yZWFkeShmdW5jdGlvbigpIHtcclxuXHJcbiAgICAkLm1hdGVyaWFsLmluaXQoKTtcclxuICAgICQoJ1tkYXRhLXRvZ2dsZT1cInRvb2x0aXBcIl0nKS50b29sdGlwKCk7XHJcblxyXG4gICAgLy8gaW5pdCB2aWRlb3NcclxuICAgICQoJyN2aWRlby1jb250YWluZXIgPiB2aWRlbycpLmVhY2goZnVuY3Rpb24oaW5kZXgsIHZpZGVvKSB7XHJcbiAgICAgICAgcmVzZXRWaWRlbyh2aWRlbyk7XHJcbiAgICAgICAgdmlkZW8ucGF1c2UoKTtcclxuICAgICAgICB2aWRlby5vbmNsaWNrID0gZnVuY3Rpb24oKSB7XHJcbiAgICAgICAgICAgIHBsYXlUb2dnbGUodmlkZW8pO1xyXG4gICAgICAgIH07XHJcbiAgICAgICAgLy8gdmlkZW8uYWRkRXZlbnRMaXN0ZW5lcignY2xpY2snLCBwbGF5VG9nZ2xlKHZpZGVvKSk7XHJcbiAgICAgICAgdmlkZW8ub25lbmRlZCA9IGZ1bmN0aW9uKCkge1xyXG4gICAgICAgICAgICBuZXh0VmlkZW8oaW5kZXgpO1xyXG4gICAgICAgIH07XHJcbiAgICB9KTtcclxuXHJcbiAgICAvLyBidXR0b24gY2xpY2sgc3dpdGNoIHRvIGNvcnJlc3BvbmRpbmcgdmlkZW9cclxuICAgICQoJyNmZWF0dXJlLWJ1dHRvbnMgPiBhJykuY2xpY2soZnVuY3Rpb24oZXZlbnQpIHtcclxuICAgICAgICBldmVudC5wcmV2ZW50RGVmYXVsdCgpO1xyXG4gICAgICAgIHZhciBmZWF0dXJlSW5kZXggPSAkKHRoaXMpLmluZGV4KCk7XHJcbiAgICAgICAgJCgnI3ZpZGVvLWNvbnRhaW5lciA+IHZpZGVvJykuZWFjaChmdW5jdGlvbihpbmRleCwgdmlkZW8pIHtcclxuICAgICAgICAgICAgaWYgKGluZGV4ID09IGZlYXR1cmVJbmRleCkge1xyXG4gICAgICAgICAgICAgICAgdmlkZW8uY2xhc3NOYW1lID0gJ3ZpZGVvLWFjdGl2ZSc7XHJcbiAgICAgICAgICAgICAgICBjb25zb2xlLmxvZyhpbmRleCk7XHJcbiAgICAgICAgICAgICAgICBpZiAodmlkZW8ucGF1c2VkKSB7XHJcbiAgICAgICAgICAgICAgICAgICAgdmlkZW8ucGxheSgpO1xyXG4gICAgICAgICAgICAgICAgfVxyXG4gICAgICAgICAgICB9IGVsc2Uge1xyXG4gICAgICAgICAgICAgICAgcmVzZXRWaWRlbyh2aWRlbyk7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICB9KTtcclxuICAgIH0pO1xyXG5cclxuICAgIC8vIGluaXRpYWwgdmlkZW8gc3RhcnRcclxuICAgICQoJyNmZWF0dXJlLWJ1dHRvbnMgYScpWzBdLmNsaWNrKCk7XHJcblxyXG5cclxufSk7XHJcbiJdLCJzb3VyY2VSb290IjoiL3NvdXJjZS8ifQ==