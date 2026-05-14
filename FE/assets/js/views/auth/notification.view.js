const NotificationView = (() => {

    // =====================================================
    // RENDER LIST
    // =====================================================

    const render = (list) => {

        const container =
            document.getElementById("notificationList");

        if (!list.length) {

            container.innerHTML = `
                <li class="text-center py-3 text-muted">
                    Chưa có thông báo
                </li>
            `;

            updateBadge(0);

            return;
        }

        let unread = 0;

        container.innerHTML = `
            <li class="dropdown-header fw-bold">
                Thông báo
            </li>
        `;

        list.forEach(item => {

            if (!item.isRead) unread++;

            container.innerHTML += buildItem(item);
        });

        updateBadge(unread);
    };

    // =====================================================
    // APPEND
    // =====================================================

    const append = (item) => {

        const container =
            document.getElementById("notificationList");

        container.insertAdjacentHTML(
            "beforeend",
            buildItem(item, true)
        );
    };

    // =====================================================
    // BUILD ITEM
    // =====================================================

    const buildItem = (
        item,
        isRealtime = false
    ) => {

        return `
            <li>

                <div class="notification-item
                    ${!item.isRead ? 'notification-unread' : ''}"

                    onclick="
                        NotificationController.readAndRedirect(
                            ${item.id},
                            '${item.redirectUrl}'
                        )
                    ">

                    <div class="notification-title">
                        ${item.title}
                    </div>

                    <div class="notification-content">
                        ${item.content}
                    </div>

                    <div class="notification-time">
                        ${
            isRealtime
                ? 'Vừa xong'
                : formatTime(item.createdAt)
        }
                    </div>

                </div>

            </li>
        `;
    };

    // =====================================================
    // BADGE
    // =====================================================

    const updateBadge = (count) => {

        const badge =
            document.getElementById("notificationBadge");

        if (count <= 0) {

            badge.classList.add("d-none");

            return;
        }

        badge.classList.remove("d-none");

        badge.innerText = count;
    };

    // =====================================================
    // INCREASE BADGE
    // =====================================================

    const increaseBadge = () => {

        const badge =
            document.getElementById("notificationBadge");

        let count =
            Number(badge.innerText || 0);

        count++;

        updateBadge(count);
    };

    return {
        render,
        append,
        updateBadge,
        increaseBadge
    };

})();