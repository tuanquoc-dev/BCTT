package be.enums;

public enum OrderStatus {

    PENDING,       // chờ xác nhận

    CONFIRMED,     // admin xác nhận

    SHIPPING,      // đang giao

    COMPLETED,     // hoàn tất

    CANCELLED,     // user/admin hủy

    REJECTED       // admin từ chối
}