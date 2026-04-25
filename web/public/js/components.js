async function loadComponent(id, file) {
  const res = await fetch(file);
  const html = await res.text();
  document.getElementById(id).innerHTML = html;
}

function setActiveNavLink() {
  const path = window.location.pathname;
  const currentPage = path === "/" || path === "" ? "index.html" : path.split("/").pop();

  document.querySelectorAll(".nav-link").forEach(link => {
    const linkPage = link.getAttribute("href").split("/").pop();
    if (linkPage === currentPage) {
      link.classList.add("underline", "underline-offset-4", "text-accent");
    }
  });
}

window.toggleMenu = function () {
  const menu = document.getElementById("mobileMenu");
  const btn = document.getElementById("menuBtn");
  const isOpen = !menu.classList.contains("hidden");

  menu.classList.toggle("hidden");
  menu.classList.toggle("flex");
  btn.textContent = isOpen ? "☰" : "✕";
};

document.addEventListener("DOMContentLoaded", async () => {
  await loadComponent("header", "/components/header.html");
  await loadComponent("footer", "/components/footer.html");

  setActiveNavLink();
});