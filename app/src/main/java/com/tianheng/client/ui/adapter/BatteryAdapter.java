package com.tianheng.client.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rey.material.widget.Button;
import com.tianheng.client.R;
import com.tianheng.client.model.bean.BatteryBean;
import com.tianheng.client.model.event.OpenDoorNumEvent;
import com.tianheng.client.wedget.BatteryView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by huyg on 2017/12/26.
 */

public class BatteryAdapter extends RecyclerView.Adapter<BatteryAdapter.ViewHolder> {

    private List<BatteryBean> mList = new ArrayList<>();


    private boolean isTesting = false;

    public BatteryAdapter() {

    }

    public void setBatteryBean(BatteryBean batteryBean) {
        mList.set(batteryBean.getNum(), batteryBean);
        notifyDataSetChanged();
    }

    public void setList(List<BatteryBean> list) {
        if (list != null) {
            this.mList = list;
            notifyDataSetChanged();
        }
    }


    public boolean isTesting() {
        return isTesting;
    }

    public void setTesting(boolean testing) {
        isTesting = testing;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_battery, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BatteryBean batteryBean = mList.get(position);

        if (batteryBean.getStatus() == 0) {
            holder.mElectricity.setText("电量" + batteryBean.getElectricity() + "%");
            holder.mBattery.setPower(batteryBean.getElectricity());
        } else if (batteryBean.getStatus()==1){
            holder.mElectricity.setText("已充满");
            holder.mBattery.setPower(100);
        }
        else if (batteryBean.getStatus() == 2) {
            holder.mElectricity.setText("无电池");
            holder.mBattery.setPower(0);
        }

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_battery_electricity)
        TextView mElectricity;
        @BindView(R.id.item_battery)
        BatteryView mBattery;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
