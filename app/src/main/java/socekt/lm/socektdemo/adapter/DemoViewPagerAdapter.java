package socekt.lm.socektdemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import socekt.lm.socektdemo.ui.fragment.ContactFragment;

/**
 *
 */
public class DemoViewPagerAdapter extends FragmentPagerAdapter {

	private Fragment currentFragment;
	private List<Fragment> fragments;

	public DemoViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);

		this.fragments=fragments;

	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		if (getCurrentFragment() != object) {
			currentFragment = ((Fragment) object);
		}
		super.setPrimaryItem(container, position, object);
	}

	/**
	 * Get the current fragment
	 */
	public Fragment getCurrentFragment() {
		return currentFragment;
	}
}