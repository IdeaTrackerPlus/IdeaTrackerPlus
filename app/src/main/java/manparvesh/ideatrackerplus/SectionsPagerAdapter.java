package manparvesh.ideatrackerplus;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Fragment adapter creating the right fragment for the right tab
 */
class SectionsPagerAdapter extends FragmentPagerAdapter {
    private final Context context;
    private final int[] pageTitles = {R.string.first_tab, R.string.second_tab, R.string.third_tab};
    private final boolean darkTheme;
    private int tabCount = 3;

    SectionsPagerAdapter(Context context, FragmentManager fm, boolean darkTheme) {
        super(fm);
        this.context = context;
        this.darkTheme = darkTheme;
    }

    void setTabCount(int count) {
        tabCount = count;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        return ListFragment.newInstance(context.getResources().getString(pageTitles[position]), darkTheme);
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getString(pageTitles[position]);
    }
}
