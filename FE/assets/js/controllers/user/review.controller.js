const ReviewController = {

    productId: null,

    async init(productId) {

        this.productId = productId;

        await this.loadStats();

        await this.loadReviews();

        this.bindFilterEvents();
    },

    async loadStats() {

        try {

            const avgRes =
                await UserModel.getAverageStar(
                    this.productId
                );

            const totalRes =
                await UserModel.getTotalReview(
                    this.productId
                );

            ReviewView.renderStats(
                avgRes.data.data || 0,
                totalRes.data.data || 0
            );

        } catch (e) {

            console.log(e);
        }
    },

    async loadReviews(star = null) {

        try {

            const res =
                await UserModel.getReviews(
                    this.productId,
                    star
                );

            ReviewView.renderReviews(
                res.data.data
            );

        } catch (e) {

            console.log(e);
        }
    },

    bindFilterEvents() {

        document
            .querySelectorAll(".review-filter")
            .forEach(btn => {

                btn.onclick = async () => {

                    document
                        .querySelectorAll(".review-filter")
                        .forEach(b =>
                            b.classList.remove("active")
                        );

                    btn.classList.add("active");

                    const star =
                        btn.dataset.star;

                    await this.loadReviews(star);
                };
            });
    }
};