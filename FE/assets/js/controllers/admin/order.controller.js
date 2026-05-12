const OrderController = {

    currentPage: 0,

    keyword: "",

    status: "",

    init() {

        this.bindPagination();

        this.bindFilter();

        this.load();
    },

    // ================= LOAD =================
    load: async () => {

        showLoading();

        try {

            const res =
                await AdminModel.getOrders({

                    page: OrderController.currentPage,

                    size: 10,

                    keyword: OrderController.keyword,

                    status: OrderController.status
                });

            const pageData =
                res.data.data;

            OrderView.renderTable(
                pageData.content || []
            );

            OrderView.renderPagination(
                pageData.totalPages,
                pageData.number
            );

        } finally {

            hideLoading();
        }
    },

    // ================= FILTER =================
    bindFilter() {

        let timeout;

        // SEARCH
        document
            .getElementById("searchInput")
            .addEventListener(
                "keyup",
                (e) => {

                    clearTimeout(timeout);

                    timeout = setTimeout(() => {

                        OrderController.keyword =
                            e.target.value;

                        OrderController.currentPage = 0;

                        OrderController.load();

                    }, 500);
                }
            );

        // STATUS
        document
            .getElementById("statusFilter")
            .addEventListener(
                "change",
                (e) => {

                    OrderController.status =
                        e.target.value;

                    OrderController.currentPage = 0;

                    OrderController.load();
                }
            );
    },

    resetFilter() {

        this.keyword = "";

        this.status = "";

        this.currentPage = 0;

        document.getElementById("searchInput").value = "";

        document.getElementById("statusFilter").value = "";

        this.load();
    },

    // ================= PAGINATION =================
    bindPagination() {

        document.addEventListener(
            "click",
            (e) => {

                const target =
                    e.target.closest("[data-page]");

                if (!target) return;

                e.preventDefault();

                OrderController.currentPage =
                    parseInt(target.dataset.page);

                OrderController.load();
            }
        );
    },

    // ================= DETAIL =================
    openDetail: async (id) => {

        showLoading();

        try {

            const res =
                await AdminModel.getOrderDetail(id);

            const order =
                res.data.data;

            OrderView.renderDetail(order);

            const modal =
                new bootstrap.Modal(
                    document.getElementById(
                        "orderDetailModal"
                    )
                );

            modal.show();

        } finally {

            hideLoading();
        }
    },

    // ================= ACTIONS =================

    confirm: async (id) => {

        await AdminModel.confirmOrder(id);

        showToast("Confirmed");

        await OrderController.load();

        await OrderController.openDetail(id);
    },

    reject: async (id) => {

        await AdminModel.rejectOrder(id);

        showToast("Rejected");

        await OrderController.load();

        await OrderController.openDetail(id);
    },

    shipping: async (id) => {

        await AdminModel.shippingOrder(id);

        showToast("Shipping");

        await OrderController.load();

        await OrderController.openDetail(id);
    },

    complete: async (id) => {

        await AdminModel.completeOrder(id);

        showToast("Completed");

        await OrderController.load();

        await OrderController.openDetail(id);
    },

    cancel: async (id) => {

        await AdminModel.cancelOrder(id);

        showToast("Cancelled");

        await OrderController.load();

        await OrderController.openDetail(id);
    },
};

document.addEventListener(
    "DOMContentLoaded",
    () => {

        OrderController.init();
    }
);