/**
 *  This file is part of spreadit-android. Copyright (C) 2020 eggze Technik GmbH
 *
 * spreadit-android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * spreadit-android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.eggze.spreadit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;

import com.eggze.spreadit.R;
import com.eggze.spreadit.databinding.ActivityTermsBinding;
import com.eggze.spreadit.util.Statics;

public class TermsActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityTermsBinding binding = null;
    private SharedPreferences sp = null;
    private boolean showAction = true;
    public final static String SHOW_ACTION = "show_action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        handleIntent();
        sp = getSharedPreferences(Statics.SHARED_PREFS, MODE_PRIVATE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_terms);
        binding.setOnClickListener(this);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (showAction) {
            binding.actionBtn.setVisibility(View.VISIBLE);
        } else {
            binding.actionBtn.setVisibility(View.GONE);
        }
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //binding.termsTxtv.setText(Html.fromHtml(getString(R.string.terms_text), Html.FROM_HTML_MODE_COMPACT));
        //binding.termsTxtv.setBackgroundColor(getResources().getColor(R.color.windowBackground, null));
        //} else {
            //binding.termsTxtv.setText(Html.fromHtml(getString(R.string.terms_text)));
        //    binding.termsTxtv.loadData(getString(R.string.terms_text), "text/html; charset=utf-8", "UTF-8");
        //}
        binding.termsTxtv.setBackgroundColor(Color.TRANSPARENT);
        String encodedHtml = Base64.encodeToString(getString(R.string.terms_text).getBytes(), Base64.NO_PADDING);
        binding.termsTxtv.loadData(encodedHtml, "text/html; charset=utf-8", "base64");

    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(SHOW_ACTION)) {
            showAction = intent.getBooleanExtra(SHOW_ACTION, true);
        }
    }

    private void goToMain() {
        final SharedPreferences sp = getSharedPreferences(Statics.SHARED_PREFS, MODE_PRIVATE);
        sp.edit().putBoolean(Statics.ONBOARDING_COMPLETED, true).apply();
        final Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_btn:
                sp.edit().putBoolean(Statics.ACCEPTED_TERMS, true).apply();
                goToMain();
                break;
        }
    }
}
