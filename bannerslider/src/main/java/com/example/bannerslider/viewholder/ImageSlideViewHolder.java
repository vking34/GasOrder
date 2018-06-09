package com.example.bannerslider.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.bannerslider.Slider;
import com.squareup.picasso.Picasso;



public class ImageSlideViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;

    public ImageSlideViewHolder(View itemView) {
        super(itemView);
        this.imageView = (ImageView) itemView;
    }

    public void bindImageSlide(String imageUrl) {
        if (imageUrl != null) {

            Picasso.get().load(imageUrl).into(imageView);
        }
    }

    public void bindImageSlideFromServer(String imageUrl) {
        Slider.getImageLoadingService().loadImage(imageUrl, imageView);
    }



}