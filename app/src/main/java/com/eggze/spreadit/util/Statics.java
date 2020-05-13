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

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class Statics {

    public static final String SHARED_PREFS = "sharedPrefs";

    public static final String ONBOARDING_COMPLETED = "ONBOARDING_COMPLETED";

    public static final String ACCEPTED_TERMS = "ACCEPTED_TERMS";

    public static final String ARG_ONBOARDING_INDEX = "ARG_ONBOARDING_INDEX";
    public static final String ARG_ONBOARDING_TITLE = "ARG_ONBOARDING_TITLE";
    public static final String ARG_ONBOARDING_INFO = "ARG_ONBOARDING_INFO";
    public static final String ARG_ONBOARDING_COLOR = "ARG_ONBOARDING_COLOR";
    public static final String ARG_ONBOARDING_TYPE = "ARG_ONBOARDING_TYPE";
    public static final String ARG_ONBOARDING_IMAGE = "ARG_ONBOARDING_IMAGE";

    public static final String INTENT_ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    public static final String INTENT_EXTRA_SCAN_RESULT = "SCAN_RESULT";

    public static final long EXPIRATION = 15 * 24 * 60 * 60 * 1000;


}
