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

        const product = res.data.data;

        DetailView.render(
            res.data.data
        );

        await ReviewController.init(product.id);
    } catch (err) {

        console.log(err);

    } finally {

        hideLoading();
    }
}