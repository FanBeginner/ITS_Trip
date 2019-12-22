package com.example.fan.its_trip.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fan.its_trip.R;
import com.example.fan.its_trip.view.IndexViewPager;

import java.util.ArrayList;
import java.util.List;

import io.github.leibnik.gradualradiobar.GradualRadioButton;
import io.github.leibnik.gradualradiobar.GradualRadioGroup;

/**
 * Created by Fan on 2019/7/9.
 */

public class Fragment_Home extends Fragment {
    private IndexViewPager home_viewPager;
    private GradualRadioButton home;
    private GradualRadioButton nav;
    private GradualRadioGroup radiobar;
    private List<Fragment> fragmentList=new ArrayList<>();
    private FragmentPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_home, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        home_viewPager = (IndexViewPager) view.findViewById(R.id.home_viewPager);
        home = (GradualRadioButton) view.findViewById(R.id.home);
        nav = (GradualRadioButton) view.findViewById(R.id.nav);
        radiobar = (GradualRadioGroup) view.findViewById(R.id.radiobar);

        fragmentList.add(new Fragment_HomePage());
        fragmentList.add(new Fragment_Nav());
        pagerAdapter=new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        };
        home_viewPager.setCurrentItem(fragmentList.size());
        home_viewPager.setAdapter(pagerAdapter);
        //禁止滑动切换
        home_viewPager.setScanScroll(false);
        //导航栏切换效果
        radiobar.setViewPager(home_viewPager);
    }
}
