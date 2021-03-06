/*
 * Copyright (c) 2014 Jakob Wenzel, Ramon Wirsch.
 *
 * This file is part of RallyeSoft.
 *
 * RallyeSoft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RallyeSoft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RallyeSoft. If not, see <http://www.gnu.org/licenses/>.
 */

package de.stadtrallye.rallyesoft.uimodel;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;

import com.android.internal.util.Predicate;

import java.util.HashMap;
import java.util.Map;

import de.stadtrallye.rallyesoft.common.Std;
import de.stadtrallye.rallyesoft.model.Server;

/**
 * Manages all fragments used as Tabs in an activity
 */
public abstract class TabManager {

	private static final String THIS = TabManager.class.getSimpleName();

	protected final Context context;
	private final FragmentManager fragmentManager;
	private final int replaceId;
	protected Integer currentTab = null;
	protected Tab<?> activeTab = null;
	protected Integer parentTab = null;

	protected final Map<Integer, Tab<?>> tabs = new HashMap<Integer, Tab<?>>();
	private int defaultTab = 0;

	public TabManager(Context context, FragmentManager fragmentManager, int replaceId) {
		this.context = context;
		this.fragmentManager = fragmentManager;
		this.replaceId = replaceId;
	}

	/**
	 * Helper Method to get the TabManager from a parent Activity
	 * @param tabActivity the parent Activity who must initialize the TabManager
	 * @return the current TabManager
	 */
	public static TabManager getTabManager(FragmentActivity tabActivity) {
		try {
			return ((ITabActivity) tabActivity).getTabManager();
		} catch (ClassCastException e) {
			Log.e(THIS, "The Activity " + tabActivity + " must implement ITabActivity", e);
			throw new IllegalArgumentException("The Activity must implement ITabActivity");
		}
	}


	public void setDefaultTab(int tab) {
		defaultTab = tab;
	}

	public void restoreState(Bundle state) {
		if (state != null) {
			currentTab = state.getInt(Std.TAB, defaultTab);
			activeTab = tabs.get(currentTab);
			parentTab = state.getInt(Std.SUB_TAB, -1);
			if (parentTab == -1) {
				parentTab = null;
			}
			setSubMode(parentTab != null);
		}
	}

	public void saveState(Bundle outState) {
		outState.putInt(Std.TAB, currentTab);
		if (parentTab != null)
			outState.putInt(Std.SUB_TAB, parentTab);
	}

	public void setArguments(int key, Bundle args) {
		tabs.get(key).args = args;
	}

	public boolean onAndroidHome(MenuItem item) {
		if (parentTab != null) {
			closeSubTab();
			return true;
		} else
			return false;
	}

	public boolean  onBackPressed() {
		if (parentTab != null) {
			closeSubTab();
			return true;
		} else
			return false;
	}

	public abstract void onConfigurationChanged(Configuration newConfig);

	public abstract void onPostCreate();

	public abstract boolean isMenuOpen();

	public int getCurrentTab() {
		return currentTab;
	}

	public Fragment getActiveFragment() {
		return activeTab.getFragment();
	}

	/**
	 * Envelops a Fragment, reuses a already existing Fragment otherwise instantiates a new one
	 * @author Ramon
	 *
	 * @param <C> Fragment Type to envelop
	 */
	public class Tab<C extends Fragment> {

		public final String tag;
		private final Class<C> clz;
		public final int titleId;
		private final Predicate<Server> showCondition;
		private Bundle args;

		public Tab(String tag, Class<C> clz, int titleId, Predicate<Server> showCondition) {
			this.tag = tag;
			this.clz = clz;
			this.titleId = titleId;
			this.showCondition = showCondition;
		}

		public Fragment getFragment() {
			Fragment f = fragmentManager.findFragmentByTag(tag);

//			if (f != null && !f.isDetached())

			if (f == null) {
				f = Fragment.instantiate(context, clz.getName());
				if (args != null)
					f.setArguments(args);
			} else if (args != null) {
				Bundle oldArgs = f.getArguments();
				if (!args.equals(oldArgs))
					oldArgs.putAll(args);
			}

			return f;
		}

		public boolean isAvailable(Server server) {
			if (showCondition == null)
				return true;

			return showCondition.apply(server);
		}
	}

	public void showTab() {
		if (tabs.get(currentTab) != activeTab) {
			if (parentTab == null)
				switchToTab(currentTab);
			else {
				int subTab = currentTab;
				currentTab = parentTab;
				openSubTab(subTab, null);
			}
		}
	}

	public boolean switchToTab(int key) {
		Tab<?> tab = tabs.get(key);

		if (tab == null) {
			switchFailedNotSupported();
			return false;
		} else if (tab == activeTab)
			return false;

		try {
			startTransaction(tab)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
			.commit();

			setActiveTab(key, tab);

			return true;
		} catch (Exception e) {
			Log.e(THIS, "Unknown Exception during switch tab", e);
			switchFailedCondition();
			return false;
		}
	}

	protected abstract void switchFailedNotSupported();

	protected abstract void switchFailedCondition();

	public boolean openSubTab(int key, Bundle args) {
		if (tabs.get(currentTab) != activeTab)
			throw new IllegalStateException("Cannot switch to a Sub Tab before showTab() was executed! (setNextTab was executed!)");

		Tab<?> tab = tabs.get(key);
		tab.args = args;

		try {
			startTransaction(tab)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack(null)
			.commit();

			parentTab = currentTab;
			setSubMode(true);
			setActiveTab(key, tab);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean closeSubTab() {
		Tab<?> tab = tabs.get(parentTab);

		try {
			fragmentManager.popBackStack();

			setActiveTab(parentTab, tab);
			parentTab = null;
			setSubMode(false);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	protected abstract void setSubMode(boolean subTab);

	private FragmentTransaction startTransaction(Tab<?> tab) throws Exception {
		if (!checkCondition(tab))
			throw new Exception();

		FragmentTransaction ft = fragmentManager.beginTransaction();

		ft.replace(replaceId, tab.getFragment(), tab.tag);

		return ft;
	}

	protected void setActiveTab(int key, Tab<?> tab) {
		currentTab = key;
		activeTab = tab;

		showSelectedTab(key, tab);
	}

	protected abstract void showSelectedTab(int key, Tab<?> tab);

	protected abstract boolean checkCondition(Tab<?> tab);
}
