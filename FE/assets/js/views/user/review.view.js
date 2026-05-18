const ReviewView = {

    renderStats(avg, total) {

        document.getElementById("avgStar")
            .innerText = avg.toFixed(1);

        document.getElementById("totalReview")
            .innerText = total;
    },

    renderReviews(reviews) {

        const container =
            document.getElementById(
                "reviewContainer"
            );

        if (!reviews.length) {

            container.innerHTML = `

                <div class="text-center text-muted py-5">

                    Chưa có đánh giá nào

                </div>

            `;

            return;
        }

        container.innerHTML = reviews.map(r => `

            <div class="border-bottom py-4">

                <div class="d-flex gap-3">

                    <img
                        src="${r.avatar || 'https://i.pravatar.cc/100'}"
                        width="50"
                        height="50"
                        class="rounded-circle object-fit-cover"
                    >

                    <div class="flex-grow-1">

                        <div class="fw-bold">
                            ${r.username}
                        </div>

                        <div class="text-warning mb-2">

                            ${
            '<i class="fa-solid fa-star"></i>'
                .repeat(r.star)
        }

                        </div>

                        <div class="text-secondary">

                            ${r.content || ""}

                        </div>

                        <div class="text-muted small mt-2">

                           ${new Date(r.createdAt)
            .toLocaleString("vi-VN")}

                        </div>

                    </div>

                </div>

            </div>

        `).join("");
    }
};