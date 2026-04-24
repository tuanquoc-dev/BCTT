let currentPage = 0;
const pageSize = 10;

document.addEventListener("DOMContentLoaded", () => {

    loadUsers();

    document.getElementById("searchBtn")?.addEventListener("click", () => {
        currentPage = 0;
        loadUsers();
    });

    document.getElementById("searchInput")?.addEventListener("keypress", (e) => {
        if (e.key === "Enter") {
            currentPage = 0;
            loadUsers();
        }
    });

    document.getElementById("saveUserBtn")?.addEventListener("click", handleSaveUser);

    document.getElementById("createStaffBtn")?.addEventListener("click", () => {
        UserListView.showCreateModal();
    });

    document.getElementById("saveStaffBtn")?.addEventListener("click", handleCreateStaff);
});


// ================= LOAD =================
async function loadUsers() {
    showLoading();

    try {
        const res = await AdminModel.getUsers({
            keyword: UserListView.getKeyword(),
            page: currentPage,
            size: pageSize
        });

        const data = res.data.data;

        UserListView.renderTable(data.content);
        UserListView.renderPagination(data.totalPages, currentPage);

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

            UserListView.fillEditForm(user);
            UserListView.showModal();
        };
    });
}


// ================= SAVE =================
async function handleSaveUser() {

    const {id, data, status} = UserListView.getEditData();

    showLoading();

    try {
        await AdminModel.updateUser(id, data);
        await AdminModel.updateStatus(id, status);

        showToast("Cập nhật thành công");

        UserListView.hideModal();
        loadUsers();

    } catch (err) {
        // ❌ không xử lý gì
        // interceptor sẽ show lỗi từ BE
    } finally {
        hideLoading();
    }
}

async function handleCreateStaff() {

    const data = UserListView.getCreateData();

    showLoading();

    try {
        await AdminModel.createStaff(data);

        showToast("Tạo nhân viên thành công");

        UserListView.hideCreateModal();

        loadUsers();

    } catch (err) {

    } finally {
        hideLoading();
    }
}