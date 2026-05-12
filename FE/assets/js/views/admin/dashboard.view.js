const DashboardView = {

    render(data) {

        const div =
            document.getElementById("dashboard");

        if (!div) return;

        div.innerHTML = `

            <div class="row">

                <div class="col-md-3 mb-3">

                    <div class="card shadow-sm p-3">

                        <h6>Total Orders</h6>

                        <h2>
                            ${data.totalOrders}
                        </h2>

                    </div>

                </div>

                <div class="col-md-3 mb-3">

                    <div class="card shadow-sm p-3">

                        <h6>Revenue</h6>

                        <h2>
                            ${Number(data.totalRevenue)
            .toLocaleString()}
                        </h2>

                    </div>

                </div>

                <div class="col-md-3 mb-3">

                    <div class="card shadow-sm p-3">

                        <h6>Pending</h6>

                        <h2>
                            ${data.pendingOrders}
                        </h2>

                    </div>

                </div>

                <div class="col-md-3 mb-3">

                    <div class="card shadow-sm p-3">

                        <h6>Completed</h6>

                        <h2>
                            ${data.completedOrders}
                        </h2>

                    </div>

                </div>

            </div>
        `;
    }
};