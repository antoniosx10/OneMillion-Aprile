package unisa.it.pc1.provacirclemenu;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import unisa.it.pc1.provacirclemenu.model.ChatMessage;

public class SectionsPageAdapter extends FragmentPagerAdapter {


    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){

            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 1:
                GroupFragment groupFragment = new GroupFragment();
                return groupFragment;

            case 2:
                TaskFragment taskFragment = new TaskFragment();
                return taskFragment;

                default:
                    return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:

                return "Chats";

            case 1:

                return "Groups";


            case 2:

                return "Tasks";

            default:
                return null;
        }
    }
}
