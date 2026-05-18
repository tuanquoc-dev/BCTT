const NotificationController = (() => {

    let stompClient = null;

    // =====================================================
    // INIT
    // =====================================================

    const init = async () => {

        // 👇 CHECK LOGIN
        const token = Auth.getToken();

        if (!token) {
            return;
        }

        await loadNotifications();

        connectSocket();
    };

    // =====================================================
    // LOAD NOTIFICATIONS
    // =====================================================

    const loadNotifications = async () => {

        try {

            const res =
                await NotificationModel.getMine();

            NotificationView.render(
                res.data.data
            );

        } catch (err) {

            console.error(err);
        }
    };

    // =====================================================
    // CONNECT SOCKET
    // =====================================================

    const connectSocket = () => {

        const token = Auth.getToken();

        // 👇 CHECK TOKEN
        if (!token) {
            return;
        }

        const socket =
            new SockJS(SOCKET_URL + "/ws");

        stompClient =
            Stomp.over(socket);

        stompClient.debug = null;

        stompClient.connect(

            {},

            () => {


                const payload =
                    parseJwt(Auth.getToken());

                // 👇 SAFE CHECK
                if (!payload) {
                    return;
                }

                const username =
                    payload.sub;


                stompClient.subscribe(

                    "/topic/notifications/" + username,

                    (message) => {

                        const notification =
                            JSON.parse(message.body);

                        NotificationView.append(
                            notification
                        );

                        NotificationView.increaseBadge();

                        showToast(notification.title);
                    }
                );
            },

            (error) => {

                console.error(error);
            }
        );
    };

    // =====================================================
    // READ + REDIRECT
    // =====================================================

    const readAndRedirect = async (
        id,
        url
    ) => {

        try {

            await NotificationModel.read(id);

            if (url) {

                window.location.href =
                    "/MobileHub/FE/pages" + url;
            }

        } catch (err) {

            console.error(err);
        }
    };

    return {
        init,
        readAndRedirect
    };

})();

document.addEventListener(
    "DOMContentLoaded",
    () => {
        NotificationController.init();
    }
);