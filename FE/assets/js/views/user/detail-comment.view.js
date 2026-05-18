const DetailCommentView = {

    renderComments(comments) {

        const container =
            document.getElementById("commentList");

        const count =
            document.getElementById("commentCount");

        count.innerText =
            `${comments.length} bình luận`;

        container.innerHTML =
            comments.map(c => DetailCommentView.renderComment(c)).join("");

        if (!comments.length) {

            container.innerHTML = `
                <div class="text-muted">
                    Chưa có bình luận nào
                </div>
            `;

            return;

        }

        container.innerHTML =
            comments.map(this.renderComment).join("");
    },

    renderComment(comment) {

        const replies =
            comment.replies || [];

        return `

            <div class="border-bottom pb-3"
                 id="comment-${comment.id}">

                <div class="d-flex gap-3">

                    <img src="${comment.avatar || '/assets/images/default-avatar.png'}"
                         class="rounded-circle"
                         width="48"
                         height="48"
                         style="object-fit:cover">

                    <div class="flex-grow-1">

                        <div class="d-flex justify-content-between">

                            <div>

                                <div class="fw-bold">
                                    ${comment.username}
                                </div>

                                <div class="text-muted small">
                        ${formatDateTime(comment.createdAt)}
                                </div>

                            </div>

                            <div class="dropdown">

                                <button class="btn btn-sm border-0"
                                        data-bs-toggle="dropdown">

                                    <i class="fa-solid fa-ellipsis"></i>

                                </button>

                                <ul class="dropdown-menu dropdown-menu-end">

                                    <li>

                                        <button class="dropdown-item edit-comment-btn"
                                                data-id="${comment.id}"
                                                data-content="${comment.content}">

                                            Sửa

                                        </button>

                                    </li>

                                    <li>

                                        <button class="dropdown-item text-danger delete-comment-btn"
                                                data-id="${comment.id}">

                                            Xóa

                                        </button>

                                    </li>

                                </ul>

                            </div>

                        </div>

                        <div class="mt-2"
                             id="comment-content-${comment.id}">

                            ${comment.content}

                        </div>

                        <!-- REPLY BUTTON -->
                        <button class="btn btn-sm btn-link px-0 reply-btn"
                                data-id="${comment.id}">

                            Phản hồi

                        </button>

                        <!-- REPLY BOX -->
                        <div class="reply-box mt-2 d-none"
                             id="reply-box-${comment.id}">

                            <textarea class="form-control mb-2 reply-content"
                                      rows="2"></textarea>

                            <button class="btn btn-sm btn-primary send-reply-btn"
                                    data-id="${comment.id}">

                                Gửi

                            </button>

                        </div>

                        <!-- REPLIES -->
                        <div class="mt-3 ms-4 d-flex flex-column gap-3">

                            ${replies.map(r => DetailCommentView.renderReply(r)).join("")}

                        </div>

                    </div>

                </div>

            </div>
        `;
    },

    renderReply(reply) {

        return `
        <div class="d-flex gap-2">

            <img src="${reply.avatar || '/assets/images/default-avatar.png'}"
                 class="rounded-circle"
                 width="36"
                 height="36">

            <div class="bg-light rounded-3 p-3 flex-grow-1">

               <div class="
    rounded-3
    p-3
    flex-grow-1
    ${reply.isAdmin ? 'bg-primary-subtle border border-primary' : 'bg-light'}
">

    <div class="fw-bold small">
        ${reply.username}
    </div>

    ${
            reply.isAdmin

                ? `
                <span class="badge bg-danger">
                    ADMIN
                </span>
              `

                : ""
        }

</div>

                <div class="small text-muted mb-1">
                    ${formatDateTime(reply.createdAt)}
                </div>

                <div>
                    ${reply.content}
                </div>

            </div>

        </div>
    `;
    }
};