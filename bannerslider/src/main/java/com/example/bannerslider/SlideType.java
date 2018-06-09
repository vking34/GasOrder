package com.example.bannerslider;


public enum SlideType {
    IMAGE(0);
    private final int value;

    SlideType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}