package cn.yky.calendarevenview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;

import cn.yky.calendarevenview.R;
import cn.yky.calendarevenview.view.CheckedStartAndEndMonthView;

/**
 * Created by yukuoyuan on 2017/6/30.
 * 这是一个选择开始和结束时间的列表适配器
 */
public class CheckedStartTimeAndEndTimeAdapter extends RecyclerView.Adapter<CheckedStartTimeAndEndTimeAdapter.MyHolder> {
    private Context context;
    private List<DateTime> list;
    private OnItemClickListener onItemClickListener;
    private DateTime CheckedstartTime;
    private DateTime CheckedendTime;


    /**
     * 设置选择的开始和结束时间
     *
     * @param CheckedstartTime
     * @param CheckedendTime
     */
    public void setStartTimeAndEndTime(DateTime CheckedstartTime, DateTime CheckedendTime) {

        this.CheckedstartTime = CheckedstartTime;
        this.CheckedendTime = CheckedendTime;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(DateTime dateTime);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public CheckedStartTimeAndEndTimeAdapter(Context context, List<DateTime> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_checked_start_endtime, null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.csaemItemView.setmMonthStartTime(list.get(position));
        holder.csaemItemView.setSelectedStartTimeAndEndTime(CheckedstartTime, CheckedendTime);
        holder.tvItemDate.setText(list.get(position).getYear() + "年" + list.get(position).getMonthOfYear()+"月");
        holder.csaemItemView.setOnDateClickListener(new CheckedStartAndEndMonthView.OnDateClickListener() {
            @Override
            public void OnDateClick(DateTime dateTime) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(dateTime);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void initView() {

    }

    class MyHolder extends RecyclerView.ViewHolder {

        private TextView tvItemDate;
        private CheckedStartAndEndMonthView csaemItemView;

        public MyHolder(View itemView) {
            super(itemView);
            tvItemDate = (TextView) itemView.findViewById(R.id.tv_item_date);
            csaemItemView = (CheckedStartAndEndMonthView) itemView.findViewById(R.id.csaem_item_view);
        }
    }
}
