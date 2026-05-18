const CommentView = {

    renderTable(comments) {

        const tbody =
            document.getElementById(
                "commentTable"
            );

        tbody.innerHTML = "";

        if (!comments.length) {

            tbody.innerHTML = `

                <tr>

                    <td colspan="7"
                        class="text-center text-muted py-5">

                        Không có bình luận

                    </td>

                </tr>
            `;

            return;
        }

        comments.forEach(comment => {

            tbody.innerHTML += `

                <tr id="comment-row-${comment.id}">

                    <td>
                        ${comment.id}
                    </td>

                    <td>

                       

                                <div class="fw-semibold">
                                    ${comment.username}
                                </div>

                            </div>

                        </div>

                    </td>

                    <td style="max-width:300px">

                        <div class="text-truncate">

                            ${comment.content}

                        </div>

                    </td>

                    <td>

                        ${comment.productId
                ? `<span class="badge bg-primary">
                                    Sản phẩm #${comment.productId}
                               </span>`
                : `<span class="badge bg-dark">
                                    Bài viết #${comment.postId}
                               </span>`
            }

                    </td>

                    <td>

                        ${this.renderStatus(comment.status)}

                    </td>

                    <td>

                        ${formatDateTime(
                comment.createdAt
            )}

                    </td>

<td>

    <div class="d-flex gap-2 justify-content-center">

        <!-- CHI TIẾT -->
        <button
            class="btn btn-sm btn-light border"
            title="Xem chi tiết"
            onclick='CommentController.openDetail(${JSON.stringify(comment)})'
        >
            <i class="fa fa-eye text-dark"></i>
        </button>

        <!-- TRẢ LỜI -->
        <button
            class="btn btn-sm btn-light border"
            title="Trả lời"
            onclick='CommentController.openReply(${JSON.stringify(comment)})'
        >
            <i class="fa fa-reply text-dark"></i>
        </button>

        <!-- ẨN / HIỆN -->
        ${
                comment.status === "APPROVED"

                    ? `

                <button
                    class="btn btn-sm btn-light border"
                    title="Ẩn bình luận"
                    onclick="
                        CommentController.toggleStatus(
                            ${comment.id},
                            'APPROVED'
                        )
                    "
                >
                    <i class="fa fa-eye-slash text-dark"></i>
                </button>
            `

                    : `

                <button
                    class="btn btn-sm btn-light border"
                    title="Hiển thị bình luận"
                    onclick="
                        CommentController.toggleStatus(
                            ${comment.id},
                            'HIDDEN'
                        )
                    "
                >
                    <i class="fa fa-eye text-dark"></i>
                </button>
            `
            }

        <!-- XÓA -->
        <button
            class="btn btn-sm btn-light border"
            title="Xóa bình luận"
            onclick="CommentController.delete(${comment.id})"
        >
            <i class="fa fa-trash text-dark"></i>
        </button>

    </div>

</td>

                </tr>
            `;
        });
    },

    // =====================================
    // STATUS
    // =====================================

    renderStatus(status) {

        if (status === "APPROVED") {

            return `
            <span class="badge bg-success">
                Đã duyệt
            </span>
        `;
        }

        if (status === "HIDDEN") {

            return `
            <span class="badge bg-secondary">
                Đã ẩn
            </span>
        `;
        }

        return `
        <span class="badge bg-dark">
            ${status}
        </span>
    `;
    },

    // ================= PAGINATION =================
    renderPagination: (totalPages, currentPage) => {
        const container = document.getElementById("pagination");
        if (!container) return;

        let html = `<nav><ul class="pagination">`;

        html += `
    <li class="page-item ${currentPage === 0 ? "disabled" : ""}">
        <a class="page-link" href="#" data-page="${currentPage - 1}">
            Trước
        </a>
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
        <a class="page-link" href="#" data-page="${currentPage + 1}">
            Sau
        </a>
    </li>
`;

        html += `</ul></nav>`;

        container.innerHTML = html;
    },

    // =====================================
    // DETAIL
    // =====================================

    renderDetail(comment) {

        document.getElementById(
            "detailUsername"
        ).innerText =
            comment.username;

        document.getElementById(
            "detailContent"
        ).innerText =
            comment.content;

        document.getElementById(
            "detailProduct"
        ).innerText =
            comment.productId || "N/A";

        document.getElementById(
            "detailPost"
        ).innerText =
            comment.postId || "N/A";

        document.getElementById(
            "detailStatus"
        ).innerHTML =
            this.renderStatus(comment.status);
    },

    // =====================================
    // REMOVE ROW
    // =====================================

    removeRow(id) {

        const row =
            document.getElementById(
                `comment-row-${id}`
            );

        if (row) {
            row.remove();
        }
    },

    // =====================================
    // RESET FILTER
    // =====================================

    resetFilter() {

        document.getElementById(
            "searchInput"
        ).value = "";

        document.getElementById(
            "statusFilter"
        ).value = "";

        CommentController.load();
    }
};