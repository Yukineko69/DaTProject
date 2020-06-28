package com.example.datproject.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.datproject.R;
import com.example.datproject.databinding.ActivityMainBinding;
import com.example.datproject.record.RecordFragment;
import com.example.datproject.recordlist.RecordListFragment;
import com.example.datproject.room.database.AppDatabase;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity  {
    public static ActivityMainBinding binding;
    public static AppDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_PHONE_STATE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        } else {
            // do nothing
        }

        initDatabase();
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Ghi âm");

        setupTabLayout();
        setupAdapter();
        setIcon();
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
                binding.viewPager.getAdapter().notifyDataSetChanged();
                if (tab.getPosition() == 0) {
                    getSupportActionBar().setTitle("Ghi âm");
                    tab.getIcon().setAlpha(228);
                    binding.tabLayout.getTabAt(1).getIcon().setAlpha(100);
                }
                else {
                    getSupportActionBar().setTitle("Bản ghi");
                    tab.getIcon().setAlpha(228);
                    binding.tabLayout.getTabAt(0).getIcon().setAlpha(100);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Failed to grant permission!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void initDatabase() {
        database = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "record_database").allowMainThreadQueries().build();

        /*database = Room.inMemoryDatabaseBuilder(getApplicationContext(),
                AppDatabase.class).allowMainThreadQueries().build();
         */
    }


    private void setupAdapter() {
        TabAdapter adapter = new TabAdapter(this, getSupportFragmentManager(), binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(adapter);
    }

    private void setupTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab());
        binding.tabLayout.addTab(binding.tabLayout.newTab());
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    private void setIcon() {
        binding.tabLayout.getTabAt(0).setIcon(R.drawable.icon_mic);
        binding.tabLayout.getTabAt(1).setIcon(R.drawable.playlist_record);
        binding.tabLayout.getTabAt(1).getIcon().setAlpha(100);
    }
}
