package note.ui.login;

import android.support.v4.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

public static class MyAdapter extends FragmentPagerAdapter {
    public MyAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        return ArrayListFragment.newInstance(position);
    }
}