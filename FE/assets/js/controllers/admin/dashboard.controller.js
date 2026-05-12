const DashboardController = {

    init() {

        this.load();
    },

    load: async () => {

        showLoading();

        try {

            const res =
                await AdminModel.dashboard();

            DashboardView.render(
                res.data.data
            );

        } finally {

            hideLoading();
        }
    }
};

document.addEventListener(
    "DOMContentLoaded",
    () => {

        DashboardController.init();
    }
);