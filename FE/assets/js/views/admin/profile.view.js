const ProfileView = {

    renderUser: (user) => {

        document.getElementById("username").textContent = user.username;

        document.getElementById("avatarPreview").src =
            user.avatar || "https://i.pravatar.cc/120";

        document.getElementById("fullName").value = user.fullName || "";
        document.getElementById("age").value = user.age || "";
        document.getElementById("email").value = user.email || "";
        document.getElementById("phone").value = user.phone || "";
        document.getElementById("address").value = user.address || "";
    },

    getProfileData: () => ({
        fullName: document.getElementById("fullName").value,
        age: document.getElementById("age").value,
        phone: document.getElementById("phone").value,
        address: document.getElementById("address").value
    }),

    getPasswordData: () => ({
        oldPassword: document.getElementById("oldPassword").value,
        newPassword: document.getElementById("newPassword").value,
        confirmPassword: document.getElementById("confirmPassword").value
    }),

    switchTab: (type) => {

        document.getElementById("profileContent")
            .classList.toggle("d-none", type !== "profile");

        document.getElementById("passwordContent")
            .classList.toggle("d-none", type !== "password");

        document.getElementById("menuProfile")
            .classList.toggle("active", type === "profile");

        document.getElementById("menuPassword")
            .classList.toggle("active", type === "password");
    }
};