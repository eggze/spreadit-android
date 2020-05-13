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
package com.eggze.spreadit.ui.results;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.eggze.spreadit.R;
import com.eggze.spreadit.Spreadit;
import com.eggze.spreadit.common.packet.server.results.SendResultsPacketIF;
import com.eggze.spreadit.data.database.entity.UserResult;
import com.eggze.spreadit.databinding.ITestBinding;

import java.util.Date;
import java.util.List;

import static com.eggze.spreadit.common.packet.server.impl.ServerErrorPacketIF.E_RESULT_INVALID;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public class UserResultsPageAdapter extends RecyclerView.Adapter<UserResultsPageAdapter.ViewHolder> {
    private List<UserResult> data;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ITestBinding binding;

        ViewHolder(ITestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public UserResultsPageAdapter(List<UserResult> data) {
        this.data = data;
    }

    @Override
    public UserResultsPageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ITestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserResult userResult = data.get(position);
        Context context = holder.binding.rootL.getContext();
        int bgColor = ContextCompat.getColor(context, R.color.cardBg);
        int textColor = ContextCompat.getColor(context, R.color.text);
        int textSecondaryColor = ContextCompat.getColor(context, R.color.textSecondary);
        String date = "";
        switch (userResult.getTestResult()) {
            case SendResultsPacketIF.RES_PENDING: {
                holder.binding.userStatusIv.setImageResource(R.drawable.ic_pending);
                holder.binding.userStatusTxtv.setText(String.format(context.getString(R.string.result), context.getString(R.string.result_pending)));
                date = String.format(context.getString(R.string.scan_date), Spreadit.dateFormat.format(new Date(userResult.getScanTimestamp())));
                break;
            }
            case SendResultsPacketIF.RES_NEGATIVE: {
                bgColor = ContextCompat.getColor(context, R.color.ok);
                textColor = ContextCompat.getColor(context, R.color.textWhite);
                textSecondaryColor = ContextCompat.getColor(context, R.color.textWhiteSecondary);
                holder.binding.userStatusIv.setImageResource(R.drawable.ic_ok);
                holder.binding.userStatusTxtv.setText(String.format(context.getString(R.string.result), context.getString(R.string.result_ok)));
                date = String.format(context.getString(R.string.result_date), Spreadit.dateFormat.format(new Date(userResult.getResultTimestamp())));
                break;
            }
            case SendResultsPacketIF.RES_POSITIVE: {
                bgColor = ContextCompat.getColor(context, R.color.positive);
                textColor = ContextCompat.getColor(context, R.color.textWhite);
                textSecondaryColor = ContextCompat.getColor(context, R.color.textWhiteSecondary);
                holder.binding.userStatusIv.setImageResource(R.drawable.ic_virus);
                holder.binding.userStatusTxtv.setText(String.format(context.getString(R.string.result), context.getString(R.string.result_positive)));
                date = String.format(context.getString(R.string.result_date), Spreadit.dateFormat.format(new Date(userResult.getResultTimestamp())));
                break;
            }
            case SendResultsPacketIF.RES_RETEST: {
                bgColor = ContextCompat.getColor(context, R.color.retest);
                textColor = ContextCompat.getColor(context, R.color.textWhite);
                textSecondaryColor = ContextCompat.getColor(context, R.color.textWhite);
                holder.binding.userStatusIv.setImageResource(R.drawable.ic_positive);
                holder.binding.userStatusTxtv.setText(String.format(context.getString(R.string.result), context.getString(R.string.result_retest)));
                date = String.format(context.getString(R.string.result_date), Spreadit.dateFormat.format(new Date(userResult.getResultTimestamp())));
                break;
            }
            case E_RESULT_INVALID: {
                holder.binding.userStatusIv.setImageResource(R.drawable.ic_error_outline);
                holder.binding.userStatusTxtv.setText(String.format(context.getString(R.string.result), context.getString(R.string.result_error)));
                date = String.format(context.getString(R.string.scan_date), Spreadit.dateFormat.format(new Date(userResult.getScanTimestamp())));
                break;
            }
        }
        holder.binding.rootL.setCardBackgroundColor(bgColor);
        holder.binding.userStatusTxtv.setTextColor(textColor);
        holder.binding.userStatusLastUpdateTxtv.setTextColor(textSecondaryColor);
        ImageViewCompat.setImageTintList(holder.binding.userStatusIv, ColorStateList.valueOf(textColor));
        holder.binding.userStatusLastUpdateTxtv.setText(date);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<UserResult> newData) {
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
        List<UserResult> oldData = null;
        List<UserResult> newData = null;

        DiffCallback(List<UserResult> oldData, List<UserResult> newData) {
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