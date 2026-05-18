const DetailCommentController = {

    stompClient: null,

    async init(productId) {

        this.productId = productId;

        await this.loadComments();

        this.bindCreateComment();

        this.bindActions();

        this.connectSocket();

    },

    // ======================================
    // LOAD COMMENTS
    // ======================================

    async loadComments() {

        try {

            const res =
                await CommentModel.getByProduct(
                    this.productId
                );

            DetailCommentView.renderComments(
                res.data.data
            );

        } catch (e) {

        }
    },

    // ======================================
    // CREATE COMMENT
    // ======================================

    bindCreateComment() {

        document
            .getElementById("sendCommentBtn")
            .addEventListener("click", async () => {

                try {

                    const content =
                        document
                            .getElementById("commentContent")
                            .value
                            .trim();

                    if (!content) {

                        Utils.showToast(
                            "Vui lòng nhập nội dung",
                            "error"
                        );

                        return;
                    }

                    await CommentModel.create({

                        content,
                        productId: this.productId
                    });

                    document.getElementById(
                        "commentContent"
                    ).value = "";

                } catch (e) {

                }
            });
    },

    // ======================================
    // ACTIONS
    // ======================================

    bindActions() {

        document.addEventListener("click", async (e) => {

            // DELETE
            if (e.target.classList.contains("delete-comment-btn")) {

                const id =
                    e.target.dataset.id;

                if (!confirm("Xóa bình luận?")) {
                    return;
                }

                try {

                    await CommentModel.delete(id);

                } catch (err) {

                }
            }

            // EDIT
            if (e.target.classList.contains("edit-comment-btn")) {

                const id =
                    e.target.dataset.id;

                const oldContent =
                    e.target.dataset.content;

                const content =
                    prompt(
                        "Sửa bình luận",
                        oldContent
                    );

                if (!content) return;

                try {

                    await CommentModel.update(
                        id,
                        { content }
                    );

                } catch (err) {

                }
            }

            // OPEN REPLY
            if (e.target.classList.contains("reply-btn")) {

                const id =
                    e.target.dataset.id;

                document
                    .getElementById(
                        `reply-box-${id}`
                    )
                    .classList
                    .toggle("d-none");
            }

            // SEND REPLY
            if (e.target.classList.contains("send-reply-btn")) {

                const id =
                    e.target.dataset.id;

                const box =
                    document.querySelector(
                        `#reply-box-${id} .reply-content`
                    );

                const content =
                    box.value.trim();

                if (!content) return;

                try {

                    await CommentModel.create({

                        content,
                        parentId: id,
                        productId: this.productId
                    });

                    box.value = "";

                } catch (err) {

                }
            }
        });
    },

    // ======================================
    // SOCKET
    // ======================================

    connectSocket() {

        const socket =
            new SockJS(
                SOCKET_URL + "/ws"
            );

        this.stompClient =
            Stomp.over(socket);

        this.stompClient.debug = () => {};

        this.stompClient.connect({}, () => {

            // CREATE
            this.stompClient.subscribe(

                `/topic/comments/product/${this.productId}`,

                () => {

                    this.loadComments();
                }
            );

            // UPDATE
            this.stompClient.subscribe(

                "/topic/comments/update",

                () => {

                    this.loadComments();
                }
            );

            // DELETE
            this.stompClient.subscribe(

                "/topic/comments/delete",

                () => {

                    this.loadComments();
                }
            );
        });
    }
};

