package com.dungkk.gasorder.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dungkk.gasorder.adapter.MainSliderAdapter;
import com.dungkk.gasorder.passingObjects.location;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.dungkk.gasorder.R;
import com.example.bannerslider.Slider;

import java.io.IOException;


public class FragmentMain extends Fragment implements View.OnClickListener{

    private FragmentTransaction transaction;
    private ImageButton ibtn_order;
    private ImageButton ibtn_products;
    private ImageButton ibtn_tips;
    private LinearLayout layout_order, layout_product, layout_tips;
    private View view;
    private Slider slider;
    private String[] arrslides;
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_main, container, false);


        setSlider(view);

//        ibtn_order = (ImageButton) view.findViewById(R.id.ibtn_order);
//        ibtn_products = (ImageButton) view.findViewById(R.id.ibtn_products);
//        ibtn_tips = (ImageButton) view.findViewById(R.id.ibtn_tips);
//
//        ibtn_order.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                replaceFragment(new FragmentOrder());
//            }
//        });
//
//        ibtn_products.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                replaceFragment(new FragmentProducts());
//            }
//        });
//
//
//        ibtn_tips.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                replaceFragment(new FragmentTips());
//            }
//        });

        layout_order = view.findViewById(R.id.layout_order);
        layout_product = view.findViewById(R.id.layout_product);
        layout_tips = view.findViewById(R.id.layout_tips);

        layout_order.setOnClickListener(this);
        layout_product.setOnClickListener(this);
        layout_tips.setOnClickListener(this);

        return view;
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager manager = getFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.content_main, fragment, FragmentMain.class.getSimpleName())
                .addToBackStack(FragmentMain.class.getSimpleName())
                .commit();
    }

    private void setSlider(View view) {
        slider = view.findViewById(R.id.banner_slider1);

        slider.setInterval(2000);
        slider.setIndicatorSize(24);
        slider.setAnimateIndicators(true);
        slider.showIndicators();
        slider.setSelectedSlideIndicator(ContextCompat.getDrawable(getContext(), R.drawable.selected_slide_indicator));
        slider.setUnSelectedSlideIndicator(ContextCompat.getDrawable(getContext(), R.drawable.unselected_slide_indicator));
        arrslides = getDataFromAsset();
        slider.setAdapter(new MainSliderAdapter(arrslides));
        slider.setSelectedSlide(0);
        slider.setLoopSlides(true);

    }


    private String[] getDataFromAsset() {
        String[] data = null;
        try {
            data = getActivity().getAssets().list("slide");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_order:
                replaceFragment(new FragmentOrder());
                break;
            case R.id.layout_product:
                replaceFragment(new FragmentProducts());
                break;
            case R.id.layout_tips:
                replaceFragment(new FragmentTips());
                break;
        }
    }
}
