const NotifyView = {

    render(list) {

        const container =
            document.getElementById(
                "adminNotificationList"
            );

        if (!container) return;

        if (!list.length) {

            container.innerHTML = `
                <li class="text-center py-3 text-muted">
                    Chưa có thông báo
                </li>
            `;

            return;
        }

        let unread = 0;

        container.innerHTML = `
            <li class="dropdown-header fw-bold">
                Thông báo quản trị
            </li>
        `;

        list.forEach(item => {

            if (!item.isRead) unread++;

            container.innerHTML += `

                <li>

                    <div
                        class="
                            notification-item
                            px-3
                            py-2
                            border-bottom
                            ${!item.isRead ? 'notification-unread' : ''}
                        "

                        style="cursor:pointer"

                        onclick="
                            NotifyController.readAndRedirect(
                                ${item.id},
                                '${item.redirectUrl}'
                            )
                        "
                    >

                        <div class="fw-semibold">
                            ${item.title}
                        </div>

                        <div class="small text-secondary">
                            ${item.content}
                        </div>

                        <div class="small text-muted mt-1">
                            ${formatTime(item.createdAt)}
                        </div>

                    </div>

                </li>
            `;
        });

        this.updateBadge(unread);
    },

    append(item) {

        const container =
            document.getElementById(
                "adminNotificationList"
            );

        if (!container) return;

        // remove empty
        if (
            container.innerText.includes(
                "Chưa có thông báo"
            )
        ) {

            container.innerHTML = `
                <li class="dropdown-header fw-bold">
                    Thông báo quản trị
                </li>
            `;
        }

        container.insertAdjacentHTML(
            "afterbegin",

            `
            <li>

                <div
                    class="
                        notification-item
                        notification-unread
                        px-3
                        py-2
                        border-bottom
                    "

                    style="cursor:pointer"

                    onclick="
                        NotifyController.readAndRedirect(
                            ${item.id},
                            '${item.redirectUrl}'
                        )
                    "
                >

                    <div class="fw-semibold">
                        ${item.title}
                    </div>

                    <div class="small text-secondary">
                        ${item.content}
                    </div>

                    <div class="small text-muted mt-1">
                        Vừa xong
                    </div>

                </div>

            </li>
            `
        );
    },

    updateBadge(count) {

        const badge =
            document.getElementById(
                "adminNotificationBadge"
            );

        if (!badge) return;

        if (count <= 0) {

            badge.classList.add("d-none");

            return;
        }

        badge.classList.remove("d-none");

        badge.innerText = count;
    },

    increaseBadge() {

        const badge =
            document.getElementById(
                "adminNotificationBadge"
            );

        if (!badge) return;

        let count =
            Number(badge.innerText || 0);

        count++;

        this.updateBadge(count);
    }
};