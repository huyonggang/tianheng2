package com.tianheng.client.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.tianheng.client.R;
import com.tianheng.client.model.event.FrameEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by huyg on 2018/10/2.
 */
public class LogFragment extends DialogFragment {

    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.close)
    TextView mClose;


    @OnClick(R.id.close)
    public void onClick(){
        dismiss();
    }

    private StringBuilder builder = new StringBuilder();

    public static LogFragment newInstance() {
        LogFragment logFragment = new LogFragment();
        return logFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FrameEvent event) {
        builder.append(event.frame);
        builder.append("\n");
       // mContent.setText(builder);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        builder.delete(0, builder.length());
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
