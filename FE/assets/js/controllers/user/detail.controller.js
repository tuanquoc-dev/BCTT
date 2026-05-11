document.addEventListener("DOMContentLoaded", async () => {

    const params =
        new URLSearchParams(window.location.search);

    const slug = params.get("slug");

    if (!slug) return;

    await loadDetail(slug);

});

async function loadDetail(slug) {

    try {

        showLoading();

        const res =
            await UserModel.getProductDetail(slug);

        DetailView.render(
            res.data.data
        );

    } catch (err) {

        console.log(err);

    } finally {

        hideLoading();
    }
}