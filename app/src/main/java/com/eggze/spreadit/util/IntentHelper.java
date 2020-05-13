/**
 * This file is part of spreadit-android. Copyright (C) 2020 eggze Technik GmbH
 *
 * spreadit-android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * spreadit-android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.eggze.spreadit.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;

import com.eggze.spreadit.R;
import com.eggze.spreadit.activities.ForceUpdateActivity;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class IntentHelper {

    public static void openUrl(Context context, String url) {
        try {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(browserIntent);
            } else {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(context, Uri.parse(url));
                builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorAccent));
            }
        } catch (Exception e) {
        }
    }

    public static void sendEmail(Activity context, String title, String to, String subject, String body) {
        try {
            ShareCompat.IntentBuilder.from(context)
                    .setType("message/rfc822")
                    .addEmailTo(to)
                    .setSubject(subject)
                    .setText(body)
                    .setChooserTitle(title)
                    .startChooser();
        } catch (Exception e) {
        }
    }

    public static void forceUpdate(Context context) {
        try {
            Intent intent = new Intent(context, ForceUpdateActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }
}
