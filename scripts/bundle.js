function githubIcon(e){function n(e){var n="";n+=e.avatar_url,document.getElementById("github-icon").innerHTML=n}var t=new XMLHttpRequest,o="https://api.github.com/users/"+e;t.onreadystatechange=function(){if(4==t.readyState&&200==t.status){var e=JSON.parse(t.responseText);n(e)}},t.open("GET",o,!0),t.send()}$(document).ready(function(){$("#phone-screenshot > div:gt(0)").hide(),setInterval(function(){$("#phone-screenshot > div:first").fadeOut(500).next().fadeIn(500).end().appendTo("#phone-screenshot")},4e3)});
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbImdpdGh1Yi1pY29uLmpzIiwic2NyaXB0cy5qcyJdLCJuYW1lcyI6WyJnaXRodWJJY29uIiwidXNlcm5hbWUiLCJnZXRQaWN0dXJlIiwic2l0ZSIsIm91dCIsImF2YXRhcl91cmwiLCJkb2N1bWVudCIsImdldEVsZW1lbnRCeUlkIiwiaW5uZXJIVE1MIiwieG1saHR0cCIsIlhNTEh0dHBSZXF1ZXN0IiwidXJsIiwib25yZWFkeXN0YXRlY2hhbmdlIiwicmVhZHlTdGF0ZSIsInN0YXR1cyIsInVzZXJQYWdlIiwiSlNPTiIsInBhcnNlIiwicmVzcG9uc2VUZXh0Iiwib3BlbiIsInNlbmQiLCIkIiwicmVhZHkiLCJoaWRlIiwic2V0SW50ZXJ2YWwiLCJmYWRlT3V0IiwibmV4dCIsImZhZGVJbiIsImVuZCIsImFwcGVuZFRvIl0sIm1hcHBpbmdzIjoiQUFBQSxRQUFBQSxZQUFBQyxHQWdCQSxRQUFBQyxHQUFBQyxHQUNBLEdBQUFDLEdBQUEsRUFDQUEsSUFBQUQsRUFBQUUsV0FDQUMsU0FBQUMsZUFBQSxlQUFBQyxVQUFBSixFQWpCQSxHQUFBSyxHQUFBLEdBQUFDLGdCQUNBQyxFQUFBLGdDQUFBVixDQUdBUSxHQUFBRyxtQkFBQSxXQUNBLEdBQUEsR0FBQUgsRUFBQUksWUFBQSxLQUFBSixFQUFBSyxPQUFBLENBQ0EsR0FBQUMsR0FBQUMsS0FBQUMsTUFBQVIsRUFBQVMsYUFDQWhCLEdBQUFhLEtBSUFOLEVBQUFVLEtBQUEsTUFBQVIsR0FBQSxHQUNBRixFQUFBVyxPQ2JBQyxFQUFBZixVQUFBZ0IsTUFBQSxXQUNBRCxFQUFBLGlDQUFBRSxPQUVBQyxZQUFBLFdBQ0FILEVBQUEsaUNBQ0FJLFFBQUEsS0FDQUMsT0FDQUMsT0FBQSxLQUNBQyxNQUNBQyxTQUFBLHNCQUNBIiwiZmlsZSI6ImJ1bmRsZS5qcyIsInNvdXJjZXNDb250ZW50IjpbImZ1bmN0aW9uIGdpdGh1Ykljb24odXNlcm5hbWUpIHtcblx0Ly8gbmV3IGh0dHAgcmVxdWVzdCB0byBnaXRodWIgcGFnZVxuXHR2YXIgeG1saHR0cCA9IG5ldyBYTUxIdHRwUmVxdWVzdCgpO1xuXHR2YXIgdXJsID0gXCJodHRwczovL2FwaS5naXRodWIuY29tL3VzZXJzL1wiICsgdXNlcm5hbWU7XG5cblx0Ly8gY2hlY2tzIGZvciB2YWxpZCBzaXRlIGJlZm9yZSBwYXJzaW5nXG5cdHhtbGh0dHAub25yZWFkeXN0YXRlY2hhbmdlID0gZnVuY3Rpb24oKSB7XG5cdCAgICBpZiAoeG1saHR0cC5yZWFkeVN0YXRlID09IDQgJiYgeG1saHR0cC5zdGF0dXMgPT0gMjAwKSB7XG5cdCAgICAgICAgdmFyIHVzZXJQYWdlID0gSlNPTi5wYXJzZSh4bWxodHRwLnJlc3BvbnNlVGV4dCk7XG5cdCAgICAgICAgZ2V0UGljdHVyZSh1c2VyUGFnZSk7XG5cdCAgICB9XG5cdH07XG5cblx0eG1saHR0cC5vcGVuKFwiR0VUXCIsIHVybCwgdHJ1ZSk7XG5cdHhtbGh0dHAuc2VuZCgpO1xuXG5cdGZ1bmN0aW9uIGdldFBpY3R1cmUoc2l0ZSkge1xuXHRcdHZhciBvdXQgPSBcIlwiO1xuXHRcdG91dCArPSBzaXRlLmF2YXRhcl91cmw7XG5cdFx0ZG9jdW1lbnQuZ2V0RWxlbWVudEJ5SWQoXCJnaXRodWItaWNvblwiKS5pbm5lckhUTUwgPSBvdXQ7XG5cdH1cbn1cbiIsIi8vIHNpbXBsZSBwaG9uZSBzbGlkZXNob3dcbiQoZG9jdW1lbnQpLnJlYWR5KGZ1bmN0aW9uKCkge1xuICAgICQoXCIjcGhvbmUtc2NyZWVuc2hvdCA+IGRpdjpndCgwKVwiKS5oaWRlKCk7XG5cbiAgICBzZXRJbnRlcnZhbChmdW5jdGlvbigpIHtcbiAgICAgICAgJCgnI3Bob25lLXNjcmVlbnNob3QgPiBkaXY6Zmlyc3QnKVxuICAgICAgICAgICAgLmZhZGVPdXQoNTAwKVxuICAgICAgICAgICAgLm5leHQoKVxuICAgICAgICAgICAgLmZhZGVJbig1MDApXG4gICAgICAgICAgICAuZW5kKClcbiAgICAgICAgICAgIC5hcHBlbmRUbygnI3Bob25lLXNjcmVlbnNob3QnKTtcbiAgICB9LCA0MDAwKTtcbn0pO1xuXG5cbi8vIC8vIGF1dG9wbGF5IG5leHQgdmlkZW8gdXNpbmdcbi8vIGZ1bmN0aW9uIG5leHRWaWRlbyhpbmRleCkge1xuLy8gICAgIHZhciBuZXh0SW5kZXggPSAoaW5kZXggKyAxKSAlIDM7XG4vLyAgICAgJCgnI2ZlYXR1cmUtYnV0dG9ucyA+IGEnKS5lcShuZXh0SW5kZXgpLmNsaWNrKCk7XG4vLyB9XG5cbi8vIC8vIHBsYXkvcGF1c2UgdG9nZ2xlXG4vLyBmdW5jdGlvbiBwbGF5VG9nZ2xlKHZpZGVvKSB7XG4vLyAgICAgaWYgKHZpZGVvLnBhdXNlZCkge1xuLy8gICAgICAgICB2aWRlby5wbGF5KCk7XG4vLyAgICAgfSBlbHNlIHtcbi8vICAgICAgICAgdmlkZW8ucGF1c2UoKTtcbi8vICAgICB9XG4vLyB9XG5cbi8vIGZ1bmN0aW9uIHJlc2V0VmlkZW8odmlkKSB7XG4vLyAgICAgdmlkLmNsYXNzTmFtZSA9ICcnO1xuLy8gICAgIHZpZC5jdXJyZW50VGltZSA9IDA7XG4vLyAgICAgdmlkLnBhdXNlKCk7XG4vLyB9XG5cbi8vICQoZG9jdW1lbnQpLnJlYWR5KGZ1bmN0aW9uKCkge1xuLy8gICAgICQoJ1tkYXRhLXRvZ2dsZT1cInRvb2x0aXBcIl0nKS50b29sdGlwKCk7XG5cbi8vICAgICAvLyBpbml0IHZpZGVvc1xuLy8gICAgICQoJyN2aWRlby1jb250YWluZXIgPiB2aWRlbycpLmVhY2goZnVuY3Rpb24oaW5kZXgsIHZpZGVvKSB7XG4vLyAgICAgICAgIHJlc2V0VmlkZW8odmlkZW8pO1xuLy8gICAgICAgICB2aWRlby5wYXVzZSgpO1xuLy8gICAgICAgICB2aWRlby5vbmNsaWNrID0gZnVuY3Rpb24oKSB7XG4vLyAgICAgICAgICAgICBwbGF5VG9nZ2xlKHZpZGVvKTtcbi8vICAgICAgICAgfTtcbi8vICAgICAgICAgLy8gdmlkZW8uYWRkRXZlbnRMaXN0ZW5lcignY2xpY2snLCBwbGF5VG9nZ2xlKHZpZGVvKSk7XG4vLyAgICAgICAgIHZpZGVvLm9uZW5kZWQgPSBmdW5jdGlvbigpIHtcbi8vICAgICAgICAgICAgIG5leHRWaWRlbyhpbmRleCk7XG4vLyAgICAgICAgIH07XG4vLyAgICAgfSk7XG5cbi8vICAgICAvLyBidXR0b24gY2xpY2sgc3dpdGNoIHRvIGNvcnJlc3BvbmRpbmcgdmlkZW9cbi8vICAgICAkKCcjZmVhdHVyZS1idXR0b25zID4gYScpLmNsaWNrKGZ1bmN0aW9uKGV2ZW50KSB7XG4vLyAgICAgICAgIGV2ZW50LnByZXZlbnREZWZhdWx0KCk7XG4vLyAgICAgICAgIHZhciBmZWF0dXJlSW5kZXggPSAkKHRoaXMpLmluZGV4KCk7XG4vLyAgICAgICAgICQoJyN2aWRlby1jb250YWluZXIgPiB2aWRlbycpLmVhY2goZnVuY3Rpb24oaW5kZXgsIHZpZGVvKSB7XG4vLyAgICAgICAgICAgICBpZiAoaW5kZXggPT0gZmVhdHVyZUluZGV4KSB7XG4vLyAgICAgICAgICAgICAgICAgdmlkZW8uY2xhc3NOYW1lID0gJ3ZpZGVvLWFjdGl2ZSc7XG4vLyAgICAgICAgICAgICAgICAgY29uc29sZS5sb2coaW5kZXgpO1xuLy8gICAgICAgICAgICAgICAgIGlmICh2aWRlby5wYXVzZWQpIHtcbi8vICAgICAgICAgICAgICAgICAgICAgdmlkZW8ucGxheSgpO1xuLy8gICAgICAgICAgICAgICAgIH1cbi8vICAgICAgICAgICAgIH0gZWxzZSB7XG4vLyAgICAgICAgICAgICAgICAgcmVzZXRWaWRlbyh2aWRlbyk7XG4vLyAgICAgICAgICAgICB9XG4vLyAgICAgICAgIH0pO1xuLy8gICAgIH0pO1xuXG4vLyAgICAgLy8gaW5pdGlhbCB2aWRlbyBzdGFydFxuLy8gICAgICQoJyNmZWF0dXJlLWJ1dHRvbnMgYScpWzBdLmNsaWNrKCk7XG5cblxuLy8gfSk7XG4iXSwic291cmNlUm9vdCI6Ii9zb3VyY2UvIn0=