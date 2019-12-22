package com.example.fan.its_trip.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.fan.its_trip.R;
import com.example.fan.its_trip.activity.NearbyActivity;

/**
 * Created by Fan on 2019/10/22.
 */

public class Fragment_NearbyIntroduce extends Fragment implements View.OnClickListener {
    private Button btn_start_neardy;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_nearby_introduce, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        btn_start_neardy = (Button) view.findViewById(R.id.btn_start_neardy);

        btn_start_neardy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_neardy:
                //startActivity(new Intent(getContext(), NearbyActivity.class));
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_NearbySort()).commit();
                break;
        }
    }
}
