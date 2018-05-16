package unisa.it.pc1.provacirclemenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import unisa.it.pc1.provacirclemenu.model.User;

public class HomeFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter viewPageAdapter;
    private ArrayList<User> lista;



    View v;

    public HomeFragment() {

    }

    @Nullable

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.home_fragment,container,false);
        tabLayout = v.findViewById(R.id.tabLayout);
        viewPager = v.findViewById(R.id.viewPager);

        viewPageAdapter = new ViewPageAdapter(getActivity().getSupportFragmentManager());

        viewPageAdapter.addFragment(new ContactFragment(),"Contatti");
        viewPageAdapter.addFragment(new TaskFragment(),"ToDo");



        viewPager.setAdapter(viewPageAdapter);



        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_group_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_save_black_24dp);


        return v;
    }
}
