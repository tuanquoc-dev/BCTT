let currentUser = null;

document.addEventListener("DOMContentLoaded", async () => {
    initMenu();
    initAvatar();
    bindForms();
    await loadProfile();
});

// ================= LOAD =================
async function loadProfile() {
    showLoading();

    try {
        const res = await UserModel.getMe();
        currentUser = res.data.data;

        ProfileView.renderUser(currentUser);

    } catch (err) {
        // interceptor xử lý
    } finally {
        hideLoading();
    }
}

// ================= MENU =================
function initMenu() {
    const menuProfile = document.getElementById("menuProfile");
    const menuPassword = document.getElementById("menuPassword");

    menuProfile.onclick = () => {
        ProfileView.toggleProfile();
        menuProfile.classList.add("active");
        menuPassword.classList.remove("active");
    };

    menuPassword.onclick = () => {
        ProfileView.togglePassword();
        menuProfile.classList.remove("active");
        menuPassword.classList.add("active");
    };
}

// ================= AVATAR =================
function initAvatar() {
    const avatarPreview = document.getElementById("avatarPreview");
    const avatarInput = document.getElementById("avatarInput");

    avatarPreview.onclick = () => avatarInput.click();

    avatarInput.onchange = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        ProfileView.previewAvatar(file);
        await uploadAvatar(file);
    };
}

async function uploadAvatar(file) {
    const formData = new FormData();
    const data = ProfileView.getProfileData();

    const blob = new Blob(
        [JSON.stringify(data)],
        { type: "application/json" }
    );

    formData.append("data", blob);
    formData.append("file", file);

    showLoading();

    try {
        const res = await UserModel.updateProfile(formData);

        document.getElementById("avatarPreview").src =
            res.data.data.avatar;

        showToast("Cập nhật avatar thành công");

    } catch (err) {
        // interceptor xử lý
    } finally {
        hideLoading();
    }
}

// ================= FORM =================
function bindForms() {

    // PROFILE
    document.getElementById("profileForm").onsubmit = async (e) => {
        e.preventDefault();

        const data = ProfileView.getProfileData();

        const formData = new FormData();

        const blob = new Blob(
            [JSON.stringify(data)],
            { type: "application/json" }
        );

        formData.append("data", blob);

        showLoading();

        try {
            await UserModel.updateProfile(formData);

            showToast("Cập nhật thành công");

        } catch (err) {
            // interceptor xử lý
        } finally {
            hideLoading();
        }
    };

    // PASSWORD
    document.getElementById("passwordForm").onsubmit = async (e) => {
        e.preventDefault();

        const data = ProfileView.getPasswordData();

        showLoading();

        try {
            await UserModel.changePassword(data);

            showToast("Đổi mật khẩu thành công");
            e.target.reset();

        } catch (err) {
            // interceptor xử lý
        } finally {
            hideLoading();
        }
    };
}