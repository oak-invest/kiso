(() => {
    const button = document.querySelector(".kiso-back-to-top");
    const visibleScrollOffset = 300;

    if (!button) {
        return;
    }

    function updateButtonVisibility() {
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
    updateButtonVisibility();
})();
