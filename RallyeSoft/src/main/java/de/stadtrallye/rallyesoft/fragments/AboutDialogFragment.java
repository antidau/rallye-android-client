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
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RallyeSoft. If not, see <http://www.gnu.org/licenses/>.
 */

package de.stadtrallye.rallyesoft.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.stadtrallye.rallyesoft.R;

/**
 * Created by Ramon on 16.10.13.
 */
public class AboutDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();

		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.about)
				.setIcon(R.drawable.ic_launcher)
				.setView(inflateCustomView(inflater, null, savedInstanceState))
				.setPositiveButton(R.string.ok, null).create();
	}

	private View inflateCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.about_fragment, container, false);

		TextView tvGitHub = (TextView) v.findViewById(R.id.about_github);
		TextView tvLibs = (TextView) v.findViewById(R.id.about_libs);
		tvGitHub.setMovementMethod(LinkMovementMethod.getInstance());
		tvLibs.setMovementMethod(LinkMovementMethod.getInstance());

		return v;
	}

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		return inflateCustomView(inflater, container, savedInstanceState);
//	}
}
