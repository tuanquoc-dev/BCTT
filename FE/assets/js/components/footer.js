const Footer = {

    load: async () => {
        try {
            const basePath = window.location.pathname.includes("/pages/")
                ? "../../"
                : "./";

            const res = await fetch(basePath + "components/footer.html");
            const html = await res.text();

            const footerEl = document.getElementById("footer");
            if (footerEl) {
                footerEl.innerHTML = html;
            }

        } catch (err) {
            console.error("Load footer failed", err);
        }
    }

};