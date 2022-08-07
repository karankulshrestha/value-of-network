package com.karankulx.von.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.karankulx.von.Fragments.ChatsFragment;
import com.karankulx.von.Fragments.GroupFragment;
import com.karankulx.von.Fragments.SheetsFragment;

public class FragmentsAdapter extends FragmentPagerAdapter {
    public FragmentsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position)
        {
            case 0: return new ChatsFragment();
            case 1: return new GroupFragment();
            case 2: return new SheetsFragment();
            default: return new ChatsFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if(position == 0){
            title = "Chats";
        }
        if(position == 1){
            title = "Groups";
        }
        if(position == 2){
            title = "Sheets";
        }
        return title;
    }
}
