const CommentController = {

    currentPage: 0,
    currentEditId: null,

    stompClient: null,

    // =====================================
    // INIT
    // =====================================

    async init() {

        await this.load();

        this.connectSocket();

        this.bindPagination();
    },

    // =====================================
    // LOAD COMMENTS
    // =====================================

    async load() {

        try {

            showLoading();

            const keyword =
                document.getElementById("searchInput")?.value || "";

            const status =
                document.getElementById("statusFilter")?.value || "";

            const res =
                await AdminCommentModel.getComments({

                    keyword,
                    status,
                    page: this.currentPage

                });

            const pageData = res.data.data;

            CommentView.renderTable(
                pageData.content
            );

            CommentView.renderPagination(
                pageData.totalPages,
                pageData.number
            );

        } catch (err) {

            console.log(err);

            toastError(
                "Không tải được bình luận"
            );

        } finally {

            hideLoading();
        }
    },

    bindPagination() {
        const container = document.getElementById("pagination");
        if (!container) return;

        container.addEventListener("click", (e) => {
            e.preventDefault();

            const target = e.target.closest("[data-page]");
            if (!target) return;

            const page = parseInt(target.dataset.page);
            if (isNaN(page)) return;

            this.currentPage = page;
            this.load();
        });
    },

    // =====================================
    // DELETE
    // =====================================

    async delete(id) {

        if (!confirm("Xóa comment này?")) {
            return;
        }

        try {

            await AdminCommentModel.delete(id);

            CommentView.removeRow(id);

            toastSuccess(
                "Xóa comment thành công"
            );

        } catch (err) {

            console.log(err);
        }
    },

    async toggleStatus(id, currentStatus) {

        try {

            const newStatus =
                currentStatus === "APPROVED"
                    ? "HIDDEN"
                    : "APPROVED";

            await AdminCommentModel.updateStatus(
                id,
                newStatus
            );

            toastSuccess(
                "Cập nhật trạng thái thành công"
            );

        } catch (err) {

            console.log(err);
        }
    },

    // =====================================
    // DETAIL
    // =====================================

    openDetail(comment) {

        CommentView.renderDetail(comment);

        const modal =
            new bootstrap.Modal(
                document.getElementById(
                    "commentDetailModal"
                )
            );

        modal.show();
    },

    async openReply(comment) {

        const content =
            prompt("Nhập phản hồi");

        if (!content) return;

        try {

            await AdminCommentModel.reply({

                content,

                parentId: comment.id,

                productId: comment.productId,

                postId: comment.postId
            });

            toastSuccess(
                "Reply thành công"
            );

        } catch (err) {

            console.log(err);
        }
    },

    // =====================================
    // SOCKET
    // =====================================

    connectSocket() {

        const socket =
            new SockJS(SOCKET_URL + "/ws");

        this.stompClient =
            Stomp.over(socket);

        this.stompClient.debug = null;

        this.stompClient.connect(
            {},
            () => {

                // DELETE
                this.stompClient.subscribe(

                    "/topic/comments/delete",

                    (msg) => {

                        const id =
                            JSON.parse(msg.body);

                        CommentView.removeRow(id);
                    }
                );

                // STATUS
                this.stompClient.subscribe(

                    "/topic/admin/comments/status",

                    () => {

                        this.load();
                    }
                );

                // CREATE
                this.stompClient.subscribe(
                    "/topic/admin/comments/create",

                    () => {

                        this.load();
                    }
                );
            }
        );
    }
};

// AUTO INIT
document.addEventListener(

    "DOMContentLoaded",

    () => {



        CommentController.init();
    }
);