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
package com.eggze.spreadit.ui.scans;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.eggze.spreadit.R;
import com.eggze.spreadit.Spreadit;
import com.eggze.spreadit.data.database.entity.ScanLocation;
import com.eggze.spreadit.databinding.ITestBinding;

import java.util.Date;
import java.util.List;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class UserScansPageAdapter extends RecyclerView.Adapter<UserScansPageAdapter.ViewHolder> {
    private List<ScanLocation> data;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ITestBinding binding;

        ViewHolder(ITestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public UserScansPageAdapter(List<ScanLocation> data) {
        this.data = data;
    }

    @Override
    public UserScansPageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ITestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScanLocation scanLocation = data.get(position);
        Context context = holder.binding.rootL.getContext();
        String date = "";
        switch (scanLocation.getScanStatus()) {
            case 0: {
                holder.binding.userStatusIv.setImageResource(R.drawable.ic_ok);
                holder.binding.userStatusTxtv.setText(String.format(context.getString(R.string.status), context.getString(R.string.scan_result_ok)));
                break;
            }
            case 1: {
                holder.binding.userStatusIv.setImageResource(R.drawable.ic_contact);
                holder.binding.userStatusTxtv.setText(String.format(context.getString(R.string.status), context.getString(R.string.scan_result_contacts)));
                break;
            }
        }
        date = String.format(context.getString(R.string.scan_date), Spreadit.dateTimeFormat.format(new Date(scanLocation.getScanTimestamp())));
        holder.binding.userStatusLastUpdateTxtv.setText(date);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<ScanLocation> newData) {
        if (newData != null) {
            DiffCallback postDiffCallback = new DiffCallback(this.data, newData);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);
            this.data.clear();
            this.data.addAll(newData);
            diffResult.dispatchUpdatesTo(this);
            notifyDataSetChanged();
        }
    }

    private static class DiffCallback extends DiffUtil.Callback {
        List<ScanLocation> oldData = null;
        List<ScanLocation> newData = null;

        DiffCallback(List<ScanLocation> oldData, List<ScanLocation> newData) {
            this.oldData = oldData;
            this.newData = newData;
        }

        @Override
        public int getOldListSize() {
            return oldData.size();
        }

        @Override
        public int getNewListSize() {
            return newData.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldData.get(oldItemPosition) == newData.get(newItemPosition);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldData.get(oldItemPosition).getId() == newData.get(newItemPosition).getId();
        }
    }
}