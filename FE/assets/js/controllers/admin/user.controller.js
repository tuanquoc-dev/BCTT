let currentPage = 0;
const pageSize = 10;

document.addEventListener("DOMContentLoaded", () => {

    loadUsers();

    document.getElementById("searchInput")
        ?.addEventListener("keyup", () => {

            currentPage = 0;
            loadUsers();
        });

    document.getElementById("saveUserBtn")?.addEventListener("click", handleSaveUser);

    document.getElementById("createStaffBtn")?.addEventListener("click", () => {
        UserView.showCreateModal();
    });

    document.getElementById("saveStaffBtn")?.addEventListener("click", handleCreateStaff);
});


// ================= LOAD =================
async function loadUsers() {
    showLoading();

    try {
        const res = await AdminModel.getUsers({
            keyword: UserView.getKeyword(),
            page: currentPage,
            size: pageSize
        });

        const data = res.data.data;

        UserView.renderTable(data.content);
        UserView.renderPagination(data.totalPages, currentPage);

        bindEvents();

    } catch (err) {
        // ❌ KHÔNG xử lý ở đây
        // interceptor lo
    } finally {
        hideLoading();
    }
}


// ================= EVENTS =================
function bindEvents() {

    // PAGINATION
    document.querySelectorAll(".page-link").forEach(link => {
        link.onclick = (e) => {
            e.preventDefault();

            const page = Number(link.dataset.page);

            // tránh click vào disabled
            if (isNaN(page) || page < 0) return;

            currentPage = page;
            loadUsers();
        };
    });

    // EDIT
    document.querySelectorAll(".editBtn").forEach(btn => {
        btn.onclick = () => {
            const user = JSON.parse(btn.dataset.user);

            UserView.fillEditForm(user);
            UserView.showModal();
        };
    });
}


// ================= SAVE =================
async function handleSaveUser() {

    const {id, data, status} = UserView.getEditData();

    showLoading();

    try {
        await AdminModel.updateUser(id, data);
        await AdminModel.updateStatus(id, status);

        showToast("Cập nhật thành công");

        UserView.hideModal();
        loadUsers();

    } catch (err) {
        // ❌ không xử lý gì
        // interceptor sẽ show lỗi từ BE
    } finally {
        hideLoading();
    }
}

async function handleCreateStaff() {

    const data = UserView.getCreateData();

    showLoading();

    try {
        await AdminModel.createStaff(data);

        showToast("Tạo nhân viên thành công");

        UserView.hideCreateModal();

        loadUsers();

    } catch (err) {

    } finally {
        hideLoading();
    }
}