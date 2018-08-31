package com.tianheng.client.model.bean;

/**
 * Created by huyg on 2018/8/31.
 */
public class AdBean {


    /**
     * id : 1034972760557752321
     * companyId : 933937839211622402
     * orderIndex : 1
     * display : true
     * imgUrl : http://yunma-image.oss-cn-shenzhen.aliyuncs.com/upload/image/xlxtemp/11874209a30e-60c9-44b3-86f6-bf1b77ab9f7a.png?x-oss-process=style/mobile
     * startDate : 1535591710000
     * endDate : 2082556500000
     * href : null
     * companyName : 天恒新能源
     */

    private String id;
    private String companyId;
    private String orderIndex;
    private boolean display;
    private String imgUrl;
    private long startDate;
    private long endDate;
    private Object href;
    private String companyName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(String orderIndex) {
        this.orderIndex = orderIndex;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public Object getHref() {
        return href;
    }

    public void setHref(Object href) {
        this.href = href;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
