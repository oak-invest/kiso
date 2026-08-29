(() => {
    const button = document.querySelector(".kiso-back-to-top");
    const visibleScrollOffset = 300;
    const desktopViewport = window.matchMedia("(min-width: 768px)");

    if (!button) {
        return;
    }

    function updateButtonVisibility() {
        if (!desktopViewport.matches) {
            button.classList.add("hidden");
            return;
        }
        if (window.scrollY > visibleScrollOffset) {
            button.classList.remove("hidden");
            return;
        }
        button.classList.add("hidden");
    }

    function scrollToTop() {
        if (window.matchMedia("(prefers-reduced-motion: reduce)").matches) {
            window.scrollTo(0, 0);
            return;
        }
        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    }

    button.addEventListener("click", scrollToTop);
    window.addEventListener("scroll", updateButtonVisibility, { passive: true });
    desktopViewport.addEventListener("change", updateButtonVisibility);
    updateButtonVisibility();
})();
