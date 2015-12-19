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



$(document).ready(function() {

    $.material.init();
    $('[data-toggle="tooltip"]').tooltip();

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
        console.log("playtoggled");
    }

    // reset to video start
    function resetVideo(vid) {
        vid.className = '';
        vid.currentTime = 0;
        vid.pause();
        playButton.reset();
    }

    // play/pause button
    var playButton = {
        el: document.querySelector(".js-play-button"),
        iconEls: {
            playing: document.querySelector(".js-play-button #pause-icon"),
            paused:  document.querySelector(".js-play-button #play-icon")
        },
        state: "playing",
        nextState: {
            playing: "paused",
            paused:  "playing"
        },
        animationDuration: 200,
        // init injecting icons
        init: function () {
            this.replaceUseEl();
        },
        // inject icons
        replaceUseEl: function () {
            d3.select(this.el.querySelector("use")).remove();
            d3.select(this.el.querySelector("svg")).append("path")
                .attr("class", "js-icon")
                .attr("d", this.stateIconPath());
        },
        // reset to playing state
        reset: function() {
            this.state = "paused";
            this.toggle();
        },
        // toggle state and transition icons
        toggle: function () {
            this.goToNextState();

            d3.select(this.el.querySelector(".js-icon")).transition()
                .duration(this.animationDuration)
                .attr("d", this.stateIconPath());
        },
        // toggle state
        goToNextState: function () {
            this.state = this.nextState[this.state];
        },
        stateIconPath: function () {
            return this.iconEls[this.state].getAttribute("d");
        }
    };

    // init videos
    $('#video-container > video').each(function(index, video) {
        resetVideo(video);
        video.pause();
        video.onclick = function() {
            playToggle(video);
            playButton.toggle();
        };
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

    playButton.init();
});

//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbImdpdGh1Yi1pY29uLmpzIiwic2NyaXB0cy5qcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FDdEJBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EiLCJmaWxlIjoiYnVuZGxlLmpzIiwic291cmNlc0NvbnRlbnQiOlsiZnVuY3Rpb24gZ2l0aHViSWNvbih1c2VybmFtZSkge1xyXG5cdC8vIG5ldyBodHRwIHJlcXVlc3QgdG8gZ2l0aHViIHBhZ2VcclxuXHR2YXIgeG1saHR0cCA9IG5ldyBYTUxIdHRwUmVxdWVzdCgpO1xyXG5cdHZhciB1cmwgPSBcImh0dHBzOi8vYXBpLmdpdGh1Yi5jb20vdXNlcnMvXCIgKyB1c2VybmFtZTtcclxuXHJcblx0Ly8gY2hlY2tzIGZvciB2YWxpZCBzaXRlIGJlZm9yZSBwYXJzaW5nXHJcblx0eG1saHR0cC5vbnJlYWR5c3RhdGVjaGFuZ2UgPSBmdW5jdGlvbigpIHtcclxuXHQgICAgaWYgKHhtbGh0dHAucmVhZHlTdGF0ZSA9PSA0ICYmIHhtbGh0dHAuc3RhdHVzID09IDIwMCkge1xyXG5cdCAgICAgICAgdmFyIHVzZXJQYWdlID0gSlNPTi5wYXJzZSh4bWxodHRwLnJlc3BvbnNlVGV4dCk7XHJcblx0ICAgICAgICBnZXRQaWN0dXJlKHVzZXJQYWdlKTtcclxuXHQgICAgfVxyXG5cdH07XHJcblxyXG5cdHhtbGh0dHAub3BlbihcIkdFVFwiLCB1cmwsIHRydWUpO1xyXG5cdHhtbGh0dHAuc2VuZCgpO1xyXG5cclxuXHRmdW5jdGlvbiBnZXRQaWN0dXJlKHNpdGUpIHtcclxuXHRcdHZhciBvdXQgPSBcIlwiO1xyXG5cdFx0b3V0ICs9IHNpdGUuYXZhdGFyX3VybDtcclxuXHRcdGRvY3VtZW50LmdldEVsZW1lbnRCeUlkKFwiZ2l0aHViLWljb25cIikuaW5uZXJIVE1MID0gb3V0O1xyXG5cdH1cclxufVxyXG4iLCJcclxuXHJcbiQoZG9jdW1lbnQpLnJlYWR5KGZ1bmN0aW9uKCkge1xyXG5cclxuICAgICQubWF0ZXJpYWwuaW5pdCgpO1xyXG4gICAgJCgnW2RhdGEtdG9nZ2xlPVwidG9vbHRpcFwiXScpLnRvb2x0aXAoKTtcclxuXHJcbiAgICAvLyBhdXRvcGxheSBuZXh0IHZpZGVvIHVzaW5nXHJcbiAgICBmdW5jdGlvbiBuZXh0VmlkZW8oaW5kZXgpIHtcclxuICAgICAgICB2YXIgbmV4dEluZGV4ID0gKGluZGV4ICsgMSkgJSAzO1xyXG4gICAgICAgICQoJyNmZWF0dXJlLWJ1dHRvbnMgPiBhJykuZXEobmV4dEluZGV4KS5jbGljaygpO1xyXG4gICAgfVxyXG5cclxuICAgIC8vIHBsYXkvcGF1c2UgdG9nZ2xlXHJcbiAgICBmdW5jdGlvbiBwbGF5VG9nZ2xlKHZpZGVvKSB7XHJcbiAgICAgICAgaWYgKHZpZGVvLnBhdXNlZCkge1xyXG4gICAgICAgICAgICB2aWRlby5wbGF5KCk7XHJcbiAgICAgICAgfSBlbHNlIHtcclxuICAgICAgICAgICAgdmlkZW8ucGF1c2UoKTtcclxuICAgICAgICB9XHJcbiAgICAgICAgY29uc29sZS5sb2coXCJwbGF5dG9nZ2xlZFwiKTtcclxuICAgIH1cclxuXHJcbiAgICAvLyByZXNldCB0byB2aWRlbyBzdGFydFxyXG4gICAgZnVuY3Rpb24gcmVzZXRWaWRlbyh2aWQpIHtcclxuICAgICAgICB2aWQuY2xhc3NOYW1lID0gJyc7XHJcbiAgICAgICAgdmlkLmN1cnJlbnRUaW1lID0gMDtcclxuICAgICAgICB2aWQucGF1c2UoKTtcclxuICAgICAgICBwbGF5QnV0dG9uLnJlc2V0KCk7XHJcbiAgICB9XHJcblxyXG4gICAgLy8gcGxheS9wYXVzZSBidXR0b25cclxuICAgIHZhciBwbGF5QnV0dG9uID0ge1xyXG4gICAgICAgIGVsOiBkb2N1bWVudC5xdWVyeVNlbGVjdG9yKFwiLmpzLXBsYXktYnV0dG9uXCIpLFxyXG4gICAgICAgIGljb25FbHM6IHtcclxuICAgICAgICAgICAgcGxheWluZzogZG9jdW1lbnQucXVlcnlTZWxlY3RvcihcIi5qcy1wbGF5LWJ1dHRvbiAjcGF1c2UtaWNvblwiKSxcclxuICAgICAgICAgICAgcGF1c2VkOiAgZG9jdW1lbnQucXVlcnlTZWxlY3RvcihcIi5qcy1wbGF5LWJ1dHRvbiAjcGxheS1pY29uXCIpXHJcbiAgICAgICAgfSxcclxuICAgICAgICBzdGF0ZTogXCJwbGF5aW5nXCIsXHJcbiAgICAgICAgbmV4dFN0YXRlOiB7XHJcbiAgICAgICAgICAgIHBsYXlpbmc6IFwicGF1c2VkXCIsXHJcbiAgICAgICAgICAgIHBhdXNlZDogIFwicGxheWluZ1wiXHJcbiAgICAgICAgfSxcclxuICAgICAgICBhbmltYXRpb25EdXJhdGlvbjogMjAwLFxyXG4gICAgICAgIC8vIGluaXQgaW5qZWN0aW5nIGljb25zXHJcbiAgICAgICAgaW5pdDogZnVuY3Rpb24gKCkge1xyXG4gICAgICAgICAgICB0aGlzLnJlcGxhY2VVc2VFbCgpO1xyXG4gICAgICAgIH0sXHJcbiAgICAgICAgLy8gaW5qZWN0IGljb25zXHJcbiAgICAgICAgcmVwbGFjZVVzZUVsOiBmdW5jdGlvbiAoKSB7XHJcbiAgICAgICAgICAgIGQzLnNlbGVjdCh0aGlzLmVsLnF1ZXJ5U2VsZWN0b3IoXCJ1c2VcIikpLnJlbW92ZSgpO1xyXG4gICAgICAgICAgICBkMy5zZWxlY3QodGhpcy5lbC5xdWVyeVNlbGVjdG9yKFwic3ZnXCIpKS5hcHBlbmQoXCJwYXRoXCIpXHJcbiAgICAgICAgICAgICAgICAuYXR0cihcImNsYXNzXCIsIFwianMtaWNvblwiKVxyXG4gICAgICAgICAgICAgICAgLmF0dHIoXCJkXCIsIHRoaXMuc3RhdGVJY29uUGF0aCgpKTtcclxuICAgICAgICB9LFxyXG4gICAgICAgIC8vIHJlc2V0IHRvIHBsYXlpbmcgc3RhdGVcclxuICAgICAgICByZXNldDogZnVuY3Rpb24oKSB7XHJcbiAgICAgICAgICAgIHRoaXMuc3RhdGUgPSBcInBhdXNlZFwiO1xyXG4gICAgICAgICAgICB0aGlzLnRvZ2dsZSgpO1xyXG4gICAgICAgIH0sXHJcbiAgICAgICAgLy8gdG9nZ2xlIHN0YXRlIGFuZCB0cmFuc2l0aW9uIGljb25zXHJcbiAgICAgICAgdG9nZ2xlOiBmdW5jdGlvbiAoKSB7XHJcbiAgICAgICAgICAgIHRoaXMuZ29Ub05leHRTdGF0ZSgpO1xyXG5cclxuICAgICAgICAgICAgZDMuc2VsZWN0KHRoaXMuZWwucXVlcnlTZWxlY3RvcihcIi5qcy1pY29uXCIpKS50cmFuc2l0aW9uKClcclxuICAgICAgICAgICAgICAgIC5kdXJhdGlvbih0aGlzLmFuaW1hdGlvbkR1cmF0aW9uKVxyXG4gICAgICAgICAgICAgICAgLmF0dHIoXCJkXCIsIHRoaXMuc3RhdGVJY29uUGF0aCgpKTtcclxuICAgICAgICB9LFxyXG4gICAgICAgIC8vIHRvZ2dsZSBzdGF0ZVxyXG4gICAgICAgIGdvVG9OZXh0U3RhdGU6IGZ1bmN0aW9uICgpIHtcclxuICAgICAgICAgICAgdGhpcy5zdGF0ZSA9IHRoaXMubmV4dFN0YXRlW3RoaXMuc3RhdGVdO1xyXG4gICAgICAgIH0sXHJcbiAgICAgICAgc3RhdGVJY29uUGF0aDogZnVuY3Rpb24gKCkge1xyXG4gICAgICAgICAgICByZXR1cm4gdGhpcy5pY29uRWxzW3RoaXMuc3RhdGVdLmdldEF0dHJpYnV0ZShcImRcIik7XHJcbiAgICAgICAgfVxyXG4gICAgfTtcclxuXHJcbiAgICAvLyBpbml0IHZpZGVvc1xyXG4gICAgJCgnI3ZpZGVvLWNvbnRhaW5lciA+IHZpZGVvJykuZWFjaChmdW5jdGlvbihpbmRleCwgdmlkZW8pIHtcclxuICAgICAgICByZXNldFZpZGVvKHZpZGVvKTtcclxuICAgICAgICB2aWRlby5wYXVzZSgpO1xyXG4gICAgICAgIHZpZGVvLm9uY2xpY2sgPSBmdW5jdGlvbigpIHtcclxuICAgICAgICAgICAgcGxheVRvZ2dsZSh2aWRlbyk7XHJcbiAgICAgICAgICAgIHBsYXlCdXR0b24udG9nZ2xlKCk7XHJcbiAgICAgICAgfTtcclxuICAgICAgICB2aWRlby5vbmVuZGVkID0gZnVuY3Rpb24oKSB7XHJcbiAgICAgICAgICAgIG5leHRWaWRlbyhpbmRleCk7XHJcbiAgICAgICAgfTtcclxuICAgIH0pO1xyXG5cclxuICAgIC8vIGJ1dHRvbiBjbGljayBzd2l0Y2ggdG8gY29ycmVzcG9uZGluZyB2aWRlb1xyXG4gICAgJCgnI2ZlYXR1cmUtYnV0dG9ucyA+IGEnKS5jbGljayhmdW5jdGlvbihldmVudCkge1xyXG4gICAgICAgIGV2ZW50LnByZXZlbnREZWZhdWx0KCk7XHJcbiAgICAgICAgdmFyIGZlYXR1cmVJbmRleCA9ICQodGhpcykuaW5kZXgoKTtcclxuICAgICAgICAkKCcjdmlkZW8tY29udGFpbmVyID4gdmlkZW8nKS5lYWNoKGZ1bmN0aW9uKGluZGV4LCB2aWRlbykge1xyXG4gICAgICAgICAgICBpZiAoaW5kZXggPT0gZmVhdHVyZUluZGV4KSB7XHJcbiAgICAgICAgICAgICAgICB2aWRlby5jbGFzc05hbWUgPSAndmlkZW8tYWN0aXZlJztcclxuICAgICAgICAgICAgICAgIGNvbnNvbGUubG9nKGluZGV4KTtcclxuICAgICAgICAgICAgICAgIGlmICh2aWRlby5wYXVzZWQpIHtcclxuICAgICAgICAgICAgICAgICAgICB2aWRlby5wbGF5KCk7XHJcbiAgICAgICAgICAgICAgICB9XHJcbiAgICAgICAgICAgIH0gZWxzZSB7XHJcbiAgICAgICAgICAgICAgICByZXNldFZpZGVvKHZpZGVvKTtcclxuICAgICAgICAgICAgfVxyXG4gICAgICAgIH0pO1xyXG4gICAgfSk7XHJcblxyXG4gICAgLy8gaW5pdGlhbCB2aWRlbyBzdGFydFxyXG4gICAgJCgnI2ZlYXR1cmUtYnV0dG9ucyBhJylbMF0uY2xpY2soKTtcclxuXHJcbiAgICBwbGF5QnV0dG9uLmluaXQoKTtcclxufSk7XHJcbiJdLCJzb3VyY2VSb290IjoiL3NvdXJjZS8ifQ==