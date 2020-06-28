package com.example.datproject.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.datproject.record.RecordFragment;
import com.example.datproject.recordlist.RecordListFragment;


/**
 * Adapter lưu các fragment
 * Viewpager sử dụng để quản lý các fragment này
 */
public class TabAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;

    public TabAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                RecordFragment recordFragment = new RecordFragment();
                return recordFragment;
            case 1:
                RecordListFragment recordListFragment = new RecordListFragment();
                return recordListFragment;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
