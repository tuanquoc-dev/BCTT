let avatarFile = null;

document.addEventListener("DOMContentLoaded", () => {

    loadProfile();

    // switch tab
    document.getElementById("menuProfile").onclick = () => {
        ProfileView.switchTab("profile");
    };

    document.getElementById("menuPassword").onclick = () => {
        ProfileView.switchTab("password");
    };

    // submit
    document.getElementById("profileForm").onsubmit = updateProfile;
    document.getElementById("passwordForm").onsubmit = changePassword;

    // upload avatar
    document.getElementById("avatarPreview").onclick = () => {
        document.getElementById("avatarInput").click();
    };

    document.getElementById("avatarInput").onchange = (e) => {
        avatarFile = e.target.files[0];

        if (avatarFile) {
            document.getElementById("avatarPreview").src =
                URL.createObjectURL(avatarFile);
        }
    };
});


// ================= LOAD =================
async function loadProfile() {
    showLoading();

    try {
        const res = await UserModel.getMe();
        ProfileView.renderUser(res.data.data);

    } finally {
        hideLoading();
    }
}


// ================= UPDATE =================
async function updateProfile(e) {
    e.preventDefault();

    showLoading();

    try {
        await UserModel.updateProfile(
            ProfileView.getProfileData(),
            avatarFile
        );

        showToast("Cập nhật thành công");

        // reload UI
        await loadProfile();
        Navbar.renderUser();
        Sidebar.renderUser();

    } catch (err) {
        // interceptor handle
    } finally {
        hideLoading();
    }
}


// ================= CHANGE PASSWORD =================
async function changePassword(e) {
    e.preventDefault();

    const data = ProfileView.getPasswordData();

    if (data.newPassword !== data.confirmPassword) {
        showToast("Mật khẩu không khớp", "danger");
        return;
    }

    showLoading();

    try {
        await UserModel.changePassword(data);

        showToast("Đổi mật khẩu thành công");

        document.getElementById("passwordForm").reset();

    } catch (err) {
    } finally {
        hideLoading();
    }
}