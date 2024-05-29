package com.example.sms;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragment) {
        super(fragment);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ContactsFragment();
            case 1:
                return new ResponsesFragment();
            case 2:
                return new ActionsFragment();
            default:
                return new ContactsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
