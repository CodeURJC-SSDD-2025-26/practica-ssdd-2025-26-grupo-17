// Select tabs and forms
const tabs = document.querySelectorAll(".tab");
const forms = document.querySelectorAll(".form");

document.addEventListener("DOMContentLoaded", () => {
  const params = new URLSearchParams(window.location.search);
  const mode = params.get("mode");

  if (!mode) return;

  const targetTab = document.querySelector(`.tab[data-target="${mode}"]`);
  const targetForm = document.getElementById(mode);

  if (!targetTab || !targetForm) return;

 
  document
    .querySelectorAll(".tab")
    .forEach((tab) => tab.classList.remove("active"));
  document
    .querySelectorAll(".form")
    .forEach((form) => form.classList.remove("active"));

  
  targetTab.classList.add("active");
  targetForm.classList.add("active");
});

tabs.forEach((tab) => {
  tab.addEventListener("click", () => {
    const targetId = tab.getAttribute("data-target");

    // Remove "active" class from all tabs
    tabs.forEach((t) => t.classList.remove("active"));
    // Active clicked tab
    tab.classList.add("active");

    // Hide all forms
    forms.forEach((form) => {
      form.classList.remove("active");
    });

    // Show the corresponding form
    const targetForm = document.getElementById(targetId);
    if (targetForm) {
      targetForm.classList.add("active");
    }
  });
});

document.addEventListener("DOMContentLoaded", () => {
  const registerForm = document.getElementById("register");
  if (!registerForm) return;

  const email = registerForm.querySelector("#reg-email");
  const confirm = registerForm.querySelector("#reg-email2");
  const submit = registerForm.querySelector('button[type="submit"]');

  // Create or reuse an error element next to the confirm input
  let error = registerForm.querySelector("#reg-error");
  if (!error) {
    error = document.createElement("span");
    error.id = "reg-error";
    error.style.color = "red";
    error.style.fontSize = "0.8rem"; 
    const confirmGroup = confirm.closest(".form-group") || confirm.parentNode;
    confirmGroup.appendChild(error);
  }

  function check() {
    if (email.value && confirm.value && email.value !== confirm.value) {
      confirm.setCustomValidity("Los correos no coinciden");
      error.textContent = "Los correos no coinciden";
      submit.disabled = true;
    } else {
      confirm.setCustomValidity("");
      error.textContent = "";
      submit.disabled = false;
    }
  }

  email.addEventListener("input", check);
  confirm.addEventListener("input", check);

 
  registerForm.addEventListener("submit", (e) => {
    e.preventDefault(); 

    if (email.value !== confirm.value) {
      confirm.reportValidity();
    } else {
    
      window.location.href = "payment.html";
    }
  });
});
