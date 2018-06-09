package com.example.bannerslider.adapters;


import com.example.bannerslider.SlideType;
import com.example.bannerslider.viewholder.ImageSlideViewHolder;


public abstract class SliderAdapter {
    public abstract int getItemCount();

    public final SlideType getSlideType(int position) {
        return SlideType.IMAGE;
    }

    public abstract void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder);
}
