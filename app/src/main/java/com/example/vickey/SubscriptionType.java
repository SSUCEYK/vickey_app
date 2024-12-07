package com.example.vickey;

public enum SubscriptionType {
    BASIC("Basic",5500),
    STANDARD("Standard",11000),
    PREMIUM("Premium",17000);

    private final String name;
    private final int price;

    SubscriptionType(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}

