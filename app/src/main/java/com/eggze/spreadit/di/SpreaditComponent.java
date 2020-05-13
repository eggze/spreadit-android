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
package com.eggze.spreadit.di;

import android.content.Context;

import com.eggze.spreadit.Spreadit;
import com.eggze.spreadit.activities.BaseActivity;
import com.eggze.spreadit.tcp.ResponseHandler;
import com.eggze.spreadit.tcp.requests.Request;
import com.eggze.spreadit.ui.main.BaseFragment;
import com.eggze.spreadit.viewmodel.BaseViewModel;
import com.eggze.spreadit.work.BaseWorker;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
@Singleton
@Component(modules = {DatabaseModule.class, ExecutorsModule.class, NetworkModule.class})
public interface SpreaditComponent {
    void inject(BaseViewModel viewModel);

    void inject(BaseActivity activity);

    void inject(BaseFragment baseFragment);

    void inject(BaseWorker worker);

    void inject(ResponseHandler responseHandler);

    void inject(Request request);


    @Component.Builder
    public interface Builder {
        SpreaditComponent build();

        @BindsInstance
        public SpreaditComponent.Builder application(Spreadit application);

        @BindsInstance
        public SpreaditComponent.Builder context(Context context);


        public SpreaditComponent.Builder databaseModule(DatabaseModule databaseModule);

        public SpreaditComponent.Builder executorsModule(ExecutorsModule executorsModule);

        public SpreaditComponent.Builder networkModule(NetworkModule networkModule);
    }
}
