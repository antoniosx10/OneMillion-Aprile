package unisa.it.pc1.provacirclemenu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio on 24/03/2018.
 */

public class ViewPageAdapter extends FragmentPagerAdapter {

    private final List<Fragment> lastFragment = new ArrayList<>();
    private final List<String> lastTitles = new ArrayList<>();
    public ViewPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return lastFragment.get(position);
    }

    @Override
    public int getCount() {
        return lastTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return lastTitles.get(position);
    }



    public void addFragment(Fragment fragment, String title){
        lastFragment.add(fragment);
        lastTitles.add(title);
    }
}
