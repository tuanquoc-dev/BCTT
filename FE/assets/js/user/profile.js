const username = document.getElementById("username");
const email = document.getElementById("email");
const phone = document.getElementById("phone");
const address = document.getElementById("address");

const avatarPreview = document.getElementById("avatarPreview");
const avatarInput = document.getElementById("avatarInput");

const profileForm = document.getElementById("profileForm");
const passwordForm = document.getElementById("passwordForm");

const oldPassword = document.getElementById("oldPassword");
const newPassword = document.getElementById("newPassword");
const confirmPassword = document.getElementById("confirmPassword");

let currentUser = null;

// ================= INIT =================
document.addEventListener("DOMContentLoaded", async () => {
    initMenu();
    initAvatarClick();
    bindForms();
    await loadProfile();
});

// ================= MENU =================
function initMenu() {
    const menuProfile = document.getElementById("menuProfile");
    const menuPassword = document.getElementById("menuPassword");

    const profileContent = document.getElementById("profileContent");
    const passwordContent = document.getElementById("passwordContent");

    menuProfile.onclick = () => {
        profileContent.classList.remove("d-none");
        passwordContent.classList.add("d-none");

        menuProfile.classList.add("active");
        menuPassword.classList.remove("active");
    };

    menuPassword.onclick = () => {
        profileContent.classList.add("d-none");
        passwordContent.classList.remove("d-none");

        menuProfile.classList.remove("active");
        menuPassword.classList.add("active");
    };
}

// ================= LOAD PROFILE =================
async function loadProfile() {
    try {
        showLoading();

        const res = await api.get("/users/me");
        currentUser = res.data.data;

        username.textContent = currentUser.username;
        email.value = currentUser.email || "";
        phone.value = currentUser.phone || "";
        address.value = currentUser.address || "";

        avatarPreview.src = currentUser.avatar || "https://i.pravatar.cc/150";

    } catch (err) {
        showToast("Không tải được user", "danger");
    } finally {
        hideLoading();
    }
}

// ================= AVATAR CLICK =================
function initAvatarClick() {
    avatarPreview.onclick = () => avatarInput.click();

    avatarInput.onchange = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        // preview realtime
        avatarPreview.src = URL.createObjectURL(file);

        // upload luôn
        await uploadAvatar(file);
    };
}

// ================= UPLOAD =================
async function uploadAvatar(file) {

    const formData = new FormData();

    formData.append("file", file);
    formData.append("data", JSON.stringify({
        fullName: currentUser.fullName,
        phone: currentUser.phone,
        address: currentUser.address,
        age: currentUser.age
    }));

    try {
        showLoading();

        const res = await api.put("/users/profile", formData, {
            headers: { "Content-Type": "multipart/form-data" }
        });

        avatarPreview.src = res.data.data.avatar;

        showToast("Cập nhật avatar thành công");

    } catch (err) {
        showToast("Upload lỗi", "danger");
    } finally {
        hideLoading();
    }
}

// ================= PROFILE =================
function bindForms() {

    profileForm.onsubmit = async (e) => {
        e.preventDefault();

        if (!email.value.includes("@")) {
            return showToast("Email không hợp lệ", "danger");
        }

        try {
            showLoading();

            const formData = new FormData();

            formData.append("data", JSON.stringify({
                fullName: currentUser.fullName,
                email: email.value,
                phone: phone.value,
                address: address.value
            }));

            await api.put("/users/profile", formData, {
                headers: { "Content-Type": "multipart/form-data" }
            });

            showToast("Cập nhật thành công");

        } catch (err) {
            showToast("Lỗi update", "danger");
        } finally {
            hideLoading();
        }
    };

    passwordForm.onsubmit = async (e) => {
        e.preventDefault();

        if (!oldPassword.value) return showToast("Nhập mật khẩu cũ");
        if (newPassword.value.length < 6) return showToast(">= 6 ký tự");
        if (newPassword.value !== confirmPassword.value)
            return showToast("Không khớp");

        try {
            showLoading();

            await api.put("/users/change-password", {
                oldPassword: oldPassword.value,
                newPassword: newPassword.value,
                confirmPassword: confirmPassword.value
            });

            showToast("Đổi mật khẩu thành công");
            passwordForm.reset();

        } catch (err) {
            showToast(err.response?.data?.message || "Lỗi", "danger");
        } finally {
            hideLoading();
        }
    };
}