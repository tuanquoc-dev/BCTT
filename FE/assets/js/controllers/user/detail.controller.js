document.addEventListener("DOMContentLoaded", async () => {

    const params =
        new URLSearchParams(window.location.search);

    const slug = params.get("slug");

    if (!slug) return;

    // GET PRODUCT
    const product =
        await loadDetail(slug);

    // INIT COMMENT
    await DetailCommentController.init(
        product.id
    );

});

async function loadDetail(slug) {

    try {

        showLoading();

        const res =
            await UserModel.getProductDetail(slug);

        const product =
            res.data.data;

        DetailView.render(product);

        // RETURN PRODUCT
        return product;

    } catch (err) {


    } finally {

        hideLoading();
    }
}