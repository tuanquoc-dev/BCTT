const BannerView = {

    renderTable: (banners) => {

        const tbody = document.getElementById("bannerTableBody");

        if (!banners.length) {

            tbody.innerHTML = `
                <tr>
                    <td colspan="9">
                        Không có banner
                    </td>
                </tr>
            `;

            return;
        }

        tbody.innerHTML = banners.map(banner => `

            <tr>

                <td>${banner.id}</td>

                <td>
                    <img
                        src="${banner.thumbnail}"
                        style="
                            width:120px;
                            height:60px;
                            object-fit:cover;
                            border-radius:10px;
                        "
                    >
                </td>

                <td class="fw-semibold">
                    ${banner.name}
                </td>

                <td>
                    ${banner.position || "-"}
                </td>

                <td>
                    ${banner.type || "-"}
                </td>

                <td>
                    ${banner.sortOrder}
                </td>

                <td>
                   ${banner.status === "ACTIVE"
            ? `<span class="badge bg-success">Đang hiển thị</span>`
            : `<span class="badge bg-secondary">Đã ẩn</span>`
        }
                </td>

                <td>

    <div class="d-flex gap-2 justify-content-center">

        <!-- SỬA -->
        <button
            class="btn btn-sm btn-light border"
            title="Chỉnh sửa banner"
            onclick="BannerController.openUpdateModal(${banner.id})"
        >
            <i class="fa fa-edit text-dark"></i>
        </button>

        <!-- XÓA -->
        <button
            class="btn btn-sm btn-light border"
            title="Xóa banner"
            onclick="BannerController.delete(${banner.id})"
        >
            <i class="fa fa-trash text-dark"></i>
        </button>

    </div>

</td>

            </tr>

        `).join("");
        console.log(banners);
    },

    // ================= PAGINATION =================
    renderPagination: (totalPages, currentPage) => {
        const container = document.getElementById("pagination");
        if (!container) return;

        let html = `<nav><ul class="pagination">`;

        html += `
            <li class="page-item ${currentPage === 0 ? "disabled" : ""}">
                <a class="page-link" href="#" data-page="${currentPage - 1}">Trước</a>
            </li>
        `;

        for (let i = 0; i < totalPages; i++) {
            html += `
                <li class="page-item ${i === currentPage ? "active" : ""}">
                    <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
                </li>
            `;
        }

        html += `
            <li class="page-item ${currentPage === totalPages - 1 ? "disabled" : ""}">
                <a class="page-link" href="#" data-page="${currentPage + 1}">Sau</a>
            </li>
        `;

        html += `</ul></nav>`;

        container.innerHTML = html;

        container
            .querySelectorAll(".page-link")
            .forEach(btn => {

                btn.addEventListener("click", (e) => {

                    e.preventDefault();

                    const page =
                        Number(btn.dataset.page);

                    if (page >= 0) {

                        BannerController.load(page);

                    }

                });

            });
    },

    fillForm: (banner) => {

        document.getElementById("name").value =
            banner.name || "";

        document.getElementById("link").value =
            banner.link || "";

        document.getElementById("position").value =
            banner.position || "";

        document.getElementById("type").value =
            banner.type || "";

        document.getElementById("sortOrder").value =
            banner.sortOrder || 0;

        document.getElementById("status").value =
            banner.status;

        document.getElementById("startDate").value =
            banner.startDate
                ? banner.startDate.slice(0,16)
                : "";

        document.getElementById("endDate").value =
            banner.endDate
                ? banner.endDate.slice(0,16)
                : "";

        document.getElementById("thumbnailPreview").src =
            banner.thumbnail || "https://placehold.co/1200x300";

    },

    resetForm: () => {

        document.getElementById("bannerForm").reset();

        document.getElementById("thumbnailPreview").src =
            "https://placehold.co/1200x300?text=Banner+Preview";

    },

    getFormData: () => {

        const data = {

            name:
            document.getElementById("name").value,

            link:
            document.getElementById("link").value,

            position:
            document.getElementById("position").value,

            type:
            document.getElementById("type").value,

            sortOrder: Number(
                document.getElementById("sortOrder").value
            ),

            status:
            document.getElementById("status").value,

            startDate:
                document.getElementById("startDate").value || null,

            endDate:
                document.getElementById("endDate").value || null

        };

        const file =
            document.getElementById("thumbnail").files[0];

        return {
            data,
            file
        };
    }

};