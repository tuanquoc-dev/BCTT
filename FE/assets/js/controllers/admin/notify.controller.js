const NotifyController = (() => {

    let stompClient = null;

    // =========================================
    // INIT
    // =========================================

    const init = async () => {

        await loadNotifications();

        connectSocket();
    };

    // =========================================
    // LOAD
    // =========================================

    const loadNotifications = async () => {

        try {

            const res =
                await NotificationAdminModel.getAdminNotifications();

            NotifyView.render(
                res.data.data || []
            );

        } catch (e) {

            console.error(e);
        }
    };

    // =========================================
    // SOCKET
    // =========================================

    const connectSocket = () => {

        const socket =
            new SockJS(SOCKET_URL + "/ws");

        stompClient =
            Stomp.over(socket);

        stompClient.debug = null;

        stompClient.connect(

            {},

            () => {

                console.log(
                    "ADMIN WS CONNECTED"
                );

                stompClient.subscribe(

                    "/topic/admin/notifications",

                    (message) => {

                        const notification =
                            JSON.parse(
                                message.body
                            );

                        console.log(
                            "ADMIN REALTIME:",
                            notification
                        );

                        NotifyView.append(
                            notification
                        );

                        NotifyView
                            .increaseBadge();

                        showToast(
                            notification.title
                        );
                    }
                );
            },

            (err) => {

                console.error(
                    "ADMIN WS ERROR:",
                    err
                );
            }
        );
    };

    // =========================================
    // READ
    // =========================================

    const readAndRedirect = async (
        id,
        redirectUrl
    ) => {

        try {

            await NotificationAdminModel.read(id);

            let prefix = "/MobileHub/FE/pages";

            if (
                redirectUrl.startsWith("/admin")
                || redirectUrl.startsWith("/staff")
                || redirectUrl.startsWith("/user")
            ) {

                window.location.href =
                    "/MobileHub/FE/pages" + redirectUrl;
            }

            else {

                window.location.href =
                    prefix + redirectUrl;
            }

        } catch (e) {

            console.error(e);
        }
    };

    return {
        init,
        readAndRedirect
    };

})();