package com.dungkk.gasorder.adapter;


import com.example.bannerslider.adapters.SliderAdapter;
import com.example.bannerslider.viewholder.ImageSlideViewHolder;

/**
 * Created by Hung Phan on 5/18/2018.
 */

public class MainSliderAdapter extends SliderAdapter {

    String [] lisImage;

    public MainSliderAdapter(String[] lisImage) {
        this.lisImage = lisImage;
    }

    @Override
    public int getItemCount() {
        return lisImage.length;
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        imageSlideViewHolder.bindImageSlide("file:///android_asset/slide/" +lisImage[position]);
    }
}
