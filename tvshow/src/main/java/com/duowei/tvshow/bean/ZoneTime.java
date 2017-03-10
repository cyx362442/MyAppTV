package com.duowei.tvshow.bean;

import java.util.List;

/**
 * Created by Administrator on 2017-03-09.
 */

public class ZoneTime {
    /**
     * zone_time : [{"zone":{"zone":"发发"},"one_data":[{"time":"15:50-16:00","ad":"","video_palce":"2"},{"time":"15:50-16:05","ad":"发情啊围清风","video_palce":"5"}]},{"zone":{"zone":"放弃而为"},"one_data":[{"time":"16:05-16:00","ad":"放弃维权网","video_palce":"6"}]}]
     * down_data : ai.wxdw.top/resource/attachment/light_box_manage/175/20/zip.zip
     * version : 10
     */

    private String down_data;
    private String version;
    private List<ZoneTimeBean> zone_time;

    public String getDown_data() {
        return down_data;
    }

    public void setDown_data(String down_data) {
        this.down_data = down_data;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<ZoneTimeBean> getZone_time() {
        return zone_time;
    }

    public void setZone_time(List<ZoneTimeBean> zone_time) {
        this.zone_time = zone_time;
    }

    public static class ZoneTimeBean {
        /**
         * zone : {"zone":"发发"}
         * one_data : [{"time":"15:50-16:00","ad":"","video_palce":"2"},{"time":"15:50-16:05","ad":"发情啊围清风","video_palce":"5"}]
         */

        private ZoneBean zone;
        private List<OneDataBean> one_data;

        public ZoneBean getZone() {
            return zone;
        }

        public void setZone(ZoneBean zone) {
            this.zone = zone;
        }

        public List<OneDataBean> getOne_data() {
            return one_data;
        }

        public void setOne_data(List<OneDataBean> one_data) {
            this.one_data = one_data;
        }

        public static class ZoneBean {
            /**
             * zone : 发发
             */

            private String zone;

            public String getZone() {
                return zone;
            }

            public void setZone(String zone) {
                this.zone = zone;
            }
        }

//        public static class OneDataBean extends DataSupport {
//            /**
//             * time : 15:50-16:00
//             * ad :
//             * video_palce : 2
//             */
//
//            private String time;
//            private String ad;
//            private String video_palce;
//
//            public String getTime() {
//                return time;
//            }
//
//            public void setTime(String time) {
//                this.time = time;
//            }
//
//            public String getAd() {
//                return ad;
//            }
//
//            public void setAd(String ad) {
//                this.ad = ad;
//            }
//
//            public String getVideo_palce() {
//                return video_palce;
//            }
//
//            public void setVideo_palce(String video_palce) {
//                this.video_palce = video_palce;
//            }
//        }
    }
}
