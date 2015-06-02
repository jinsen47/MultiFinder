package com.jinsen.multifinder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.List;

/**
 * Created by Jinsen on 15/6/1.
 */
public class DeviceAdapter extends RecyclerSwipeAdapter<DeviceAdapter.DeviceHolder> {
    List<DeviceBean> mDevices;
    List<Integer> mColors;

    public DeviceAdapter(List<DeviceBean> mDevices, List<Integer> mColors) {
        this.mDevices = mDevices;
        this.mColors = mColors;
    }

    @Override
    public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new DeviceHolder(view);
    }

    @Override
    public void onBindViewHolder(final DeviceHolder holder, final int position) {
        if (mDevices.size() == 0) {
            return;
        }

        holder.mTitle.setText(mDevices.get(position).getTitle());
        holder.mAddress.setText(mDevices.get(position).getAddress());
        holder.mRssi.setText(mDevices.get(position).getRssi() + "");
        holder.mColorband.setBackgroundColor(mColors.get(position));

        holder.mTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemManger.removeShownLayouts(holder.swipelayout);
                mDevices.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDevices.size());
                mItemManger.closeAllItems();
                Log.d(this.getClass().getSimpleName(), "Remove " + position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.swipelayout;
    }

    public static class DeviceHolder extends RecyclerView.ViewHolder{
        public TextView mTitle;
        public TextView mAddress;
        public TextView mRssi;
        public FrameLayout mColorband;
        public ImageView mTrash;
        public SwipeLayout swipelayout;
        public DeviceHolder(final View itemView) {
            super(itemView);
            mTitle = ((TextView) itemView.findViewById(R.id.item_title));
            mAddress = ((TextView) itemView.findViewById(R.id.item_address));
            mRssi = ((TextView) itemView.findViewById(R.id.item_rssi));
            mColorband = ((FrameLayout) itemView.findViewById(R.id.colorband));
            mTrash = ((ImageView) itemView.findViewById(R.id.star));
            swipelayout = ((SwipeLayout) itemView.findViewById(R.id.swipelayout));
        }
    }
}
