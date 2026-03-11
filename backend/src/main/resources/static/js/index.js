// index.js - AJAX pagination for loading classes on the home page

let offset = 0;
const limit = 6;

document.addEventListener("DOMContentLoaded", function () {
  loadClasses();

  const loadMoreBtn = document.getElementById("load-more-btn");
  if (loadMoreBtn) {
    loadMoreBtn.addEventListener("click", loadClasses);
  }
});

function loadClasses() {
  fetch(`/classes/load?offset=${offset}&limit=${limit}`)
    .then((response) => response.text())
    .then((html) => {
      // 1. If we get a totally empty response, hide the button
      if (html.trim() === "") {
        document.getElementById("load-more-btn").style.display = "none";
        return;
      }

      // 2. Insert the classes into the container
      const container = document.getElementById("classes-container");
      if (container) {
        container.insertAdjacentHTML("beforeend", html);
      }

      // 3. THE FIX: Look for the signal we just injected
      // If the server sent 'hasMore: false', this ID will now exist in the DOM
      if (document.getElementById("end-of-list-signal")) {
        document.getElementById("load-more-btn").style.display = "none";
      }

      // 4. Increment the offset for the next potential fetch
      offset += limit;
    });
}
