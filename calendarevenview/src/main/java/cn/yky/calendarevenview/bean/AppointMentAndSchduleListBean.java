package cn.yky.calendarevenview.bean;

import java.util.List;

/**
 * Created by yukuoyuan on 2017/6/22.
 */

public class AppointMentAndSchduleListBean {

    public List<ItemsBean> Items;

    public static class ItemsBean {
        public String Aim;
        public List<String> AimString;
        public int Type;
        public String Id;
        public String StartTime;
        public String EndTime;
        public String DoctorName;
        public String DoctorCode;
        public int Status;
        public int PatlogType;
        public String Name;
        public String PatientCode;
        public PatientBean Patient;
        public List<WorkerBean> Worker;


        public static class WorkerBean {
            public String WorkerId;
            public String WorkerCode;
            public String WorkerName;
            public String HeadImageUrl;
            public int WorkerType;
        }

        public static class PatientBean {

            public String Id;
            public String PatientCode;
            public String PatientName;
            public int Sex;
            public String Age;
            public boolean IsImportant;
            public boolean IsArrearage;
            public String Image;

        }
    }
}
