    const ProfileView = {

        renderUser: (user) => {
            document.getElementById("username").textContent = user.username;

            document.getElementById("email").value = user.email || "";
            document.getElementById("fullName").value = user.fullName || "";
            document.getElementById("phone").value = user.phone || "";
            document.getElementById("address").value = user.address || "";
            document.getElementById("age").value = user.age || "";

            document.getElementById("avatarPreview").src =
                user.avatar || "https://i.pravatar.cc/150";
        },

        getProfileData: () => ({
            fullName: document.getElementById("fullName").value.trim(),
            phone: document.getElementById("phone").value.trim(),
            address: document.getElementById("address").value.trim(),
            age: document.getElementById("age").value
        }),

        getPasswordData: () => ({
            oldPassword: document.getElementById("oldPassword").value,
            newPassword: document.getElementById("newPassword").value,
            confirmPassword: document.getElementById("confirmPassword").value
        }),

        previewAvatar: (file) => {
            document.getElementById("avatarPreview").src =
                URL.createObjectURL(file);
        },

        toggleProfile: () => {
            profileContent.classList.remove("d-none");
            passwordContent.classList.add("d-none");
        },

        togglePassword: () => {
            profileContent.classList.add("d-none");
            passwordContent.classList.remove("d-none");
        }

    };