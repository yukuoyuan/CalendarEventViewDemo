package cn.yky.calendarevenview.utils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.yky.calendarevenview.bean.AppointMentAndSchduleListBean;

/**
 * Created by yukuoyuan on 2017/9/16.
 */

public class DataUtils {
    private static DataUtils sInstance;

    private DataUtils() {
    }

    public static DataUtils instance() {
        if (sInstance == null) {
            synchronized (DataUtils.class) {
                if (sInstance == null) {
                    sInstance = new DataUtils();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取数据
     *
     * @return
     */
    public List<AppointMentAndSchduleListBean.ItemsBean> getData() {
        Random r = new Random();
        List<AppointMentAndSchduleListBean.ItemsBean> list = new ArrayList<AppointMentAndSchduleListBean.ItemsBean>();
        for (int i = 0; i <= 10; i++) {
            DateTime dateTime = new DateTime().minusHours(r.nextInt(100));
            AppointMentAndSchduleListBean.ItemsBean itemsBean = new AppointMentAndSchduleListBean.ItemsBean();
            itemsBean.Type = 1;
            itemsBean.StartTime = dateTime.minusHours(2).toString();
            itemsBean.EndTime = dateTime.plusHours(2).toString();
            itemsBean.Patient = new AppointMentAndSchduleListBean.ItemsBean.PatientBean();
            itemsBean.Patient.PatientName = "测试" + i;
            list.add(itemsBean);
        }
        return list;
    }
}
