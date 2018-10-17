package com.tianheng.client.ui.adapter;

import android.support.annotation.Nullable;
import android.text.format.DateUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.tianheng.client.R;
import com.tianheng.client.model.bean.BatteryInfo;
import com.tianheng.client.util.DataUtils;
import com.tianheng.client.util.DateUtil;

import java.util.List;

/**
 * Created by huyg on 2018/10/16.
 */
public class LogAdapter extends BaseQuickAdapter<BatteryInfo, BaseViewHolder> {

    public LogAdapter(int layoutResId, @Nullable List<BatteryInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BatteryInfo item) {
        helper.setText(R.id.item_log_battery_info, DateUtil.getCurrentTime() + "\n" + new Gson().toJson(item));
    }
}
