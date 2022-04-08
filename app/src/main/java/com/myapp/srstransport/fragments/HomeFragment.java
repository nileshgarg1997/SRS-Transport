package com.myapp.srstransport.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.myapp.srstransport.R;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;


public class HomeFragment extends Fragment {

    private SliderLayout sliderLayout;
    private ImageView map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        sliderLayout=view.findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(IndicatorAnimations.FILL);
        sliderLayout.setSliderTransformAnimation(SliderAnimations.CUBEINDEPTHTRANSFORMATION);
        sliderLayout.setScrollTimeInSec(2);

        setSliderViews();

        map=view.findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap();
            }
        });

        return view;
    }

    private void openMap() {

        Uri uri=Uri.parse("geo:0, 0?q=SRS Senior Secondary Public School");
        Intent intent=new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private void setSliderViews() {

        for (int i=0;i<4;i++){

            DefaultSliderView sliderView= new DefaultSliderView(getContext());

            switch (i){

                case 0:
                    sliderView.setImageDrawable(R.drawable.campus_image2);
                    break;

                case 1:
                    sliderView.setDescription("Reception");
                    sliderView.setDescriptionTextSize(25);
                    sliderView.setImageDrawable(R.drawable.campus_image3);
                    break;

                case 2:
                    sliderView.setDescription("Garden");
                    sliderView.setDescriptionTextSize(25);
                    sliderView.setImageDrawable(R.drawable.campus_image1);
                    break;

                case 3:
                    sliderView.setDescription("Computer Lab");
                    sliderView.setDescriptionTextSize(25);

                    sliderView.setImageDrawable(R.drawable.campus_image4);
                    break;

            }

            sliderView.setImageScaleType(ImageView.ScaleType.FIT_XY);
            sliderLayout.addSliderView(sliderView);
        }
    }
}