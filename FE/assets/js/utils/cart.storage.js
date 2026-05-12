const CartStorage = {

    getKey() {

        const user = Auth.getUser();

        if (!user) {

            return "mobilehub_cart_guest";
        }

        return `mobilehub_cart_${user.username}`;
    },

    getCart() {

        return JSON.parse(
            localStorage.getItem(this.getKey())
        ) || [];
    },

    saveCart(cart) {

        localStorage.setItem(
            this.getKey(),
            JSON.stringify(cart)
        );
    },

    addItem(product, quantity = 1) {

        const cart = this.getCart();

        const exist = cart.find(item =>
            item.productId === product.id
        );

        // validate quantity
        quantity = Number(quantity);

        if (quantity <= 0) {
            quantity = 1;
        }

        if (exist) {

            const newQty =
                exist.quantity + quantity;

            // vượt stock
            if (newQty > product.stock) {

                toastError(
                    "Số lượng vượt tồn kho"
                );

                return;
            }

            exist.quantity = newQty;

        } else {

            cart.push({

                productId: product.id,

                slug: product.slug,

                name: product.name,

                thumbnail: product.thumbnail,

                price: product.finalPrice,

                color: product.color,

                ram: product.ram,

                stock: product.stock,

                quantity
            });
        }

        this.saveCart(cart);

        Header.updateCartCount();
    },

    removeItem(productId) {

        const cart = this.getCart()
            .filter(i => i.productId !== productId);

        this.saveCart(cart);

        Header.updateCartCount();
    },

    clear() {

        localStorage.removeItem(this.getKey());

        Header.updateCartCount();
    },

    updateQuantity(productId, quantity) {

        quantity = Number(quantity);

        const cart = this.getCart();

        const item = cart.find(i =>
            i.productId === productId
        );

        if (!item) return;

        // MIN = 1
        if (quantity < 1) {

            item.quantity = 1;

        }

        // MAX = stock
        else if (quantity > item.stock) {

            item.quantity = item.stock;

            toastError(
                "Số lượng vượt tồn kho"
            );

        }

        else {

            item.quantity = quantity;
        }

        this.saveCart(cart);

        Header.updateCartCount();
    },

    getCount() {

        return this.getCart()
            .reduce(
                (total, item) =>
                    total + item.quantity,
                0
            );
    }
};