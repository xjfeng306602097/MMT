package com.makro.mall.stat.pojo.metadata;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xiaojunfeng
 * @TableName page_stay_log
 */
@TableName(value = "page_stay_log")
@Data
public class PageStayLog implements Serializable {

    /**
     * 会员账号
     */
    private String memberNo;

    /**
     * 会员类型
     */
    private String memberType;

    /**
     * MM活动编码
     */
    private String mmCode;

    /**
     * 店铺code
     */
    private String storeCode;

    /**
     * 渠道,
     * email/sms/line/facebook/app
     */
    private String channel;

    /**
     * 类型,
     * h5/pdf
     */
    private String publishType;

    /**
     * 页码
     */
    private String pageNo;

    /**
     * 是否是新的请求
     */
    private Integer isNew;

    /**
     * 停留时间，ms为单位
     */
    private Long stayTime;

    /**
     * 页面类型
     */
    private String pageType;

    /**
     * 商品类型
     */
    private String goodsType;

    /**
     * ip地址
     */
    protected String ip;

    /**
     * 产生时间戳
     */
    protected Date ts;

    /**
     * 日志事件类型
     */
    protected String event;

    /**
     * uuid,设备唯一标识，
     */
    protected String uuid;

    /**
     * 业务id
     */
    protected String bizId;

    /**
     * user-agent信息
     */
    protected String userAgent;

    /**
     * 是否为移动平台, 0-否，1-是
     */
    protected Integer mobile;
    /**
     * 浏览器类型
     */
    protected String browser;
    /**
     * 浏览器版本
     */
    protected String browserVersion;

    /**
     * 平台类型
     */
    protected String platform;

    /**
     * 系统类型
     */
    protected String os;
    /**
     * 系统版本
     */
    protected String osVersion;

    /**
     * 引擎类型
     */
    protected String engine;
    /**
     * 引擎版本
     */
    protected String engineVersion;

    /**
     * referer
     */
    protected String referer;

    /**
     * 累计点击
     */
    protected Long totalCount;

    /**
     * 事件时间
     */
    protected Date eventDate = new Date();

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        PageStayLog other = (PageStayLog) that;
        return (this.getIp() == null ? other.getIp() == null : this.getIp().equals(other.getIp()))
                && (this.getTs() == null ? other.getTs() == null : this.getTs().equals(other.getTs()))
                && (this.getEvent() == null ? other.getEvent() == null : this.getEvent().equals(other.getEvent()))
                && (this.getUuid() == null ? other.getUuid() == null : this.getUuid().equals(other.getUuid()))
                && (this.getBizId() == null ? other.getBizId() == null : this.getBizId().equals(other.getBizId()))
                && (this.getUserAgent() == null ? other.getUserAgent() == null : this.getUserAgent().equals(other.getUserAgent()))
                && (this.getMobile() == null ? other.getMobile() == null : this.getMobile().equals(other.getMobile()))
                && (this.getBrowser() == null ? other.getBrowser() == null : this.getBrowser().equals(other.getBrowser()))
                && (this.getBrowserVersion() == null ? other.getBrowserVersion() == null : this.getBrowserVersion().equals(other.getBrowserVersion()))
                && (this.getPlatform() == null ? other.getPlatform() == null : this.getPlatform().equals(other.getPlatform()))
                && (this.getOs() == null ? other.getOs() == null : this.getOs().equals(other.getOs()))
                && (this.getOsVersion() == null ? other.getOsVersion() == null : this.getOsVersion().equals(other.getOsVersion()))
                && (this.getEngine() == null ? other.getEngine() == null : this.getEngine().equals(other.getEngine()))
                && (this.getEngineVersion() == null ? other.getEngineVersion() == null : this.getEngineVersion().equals(other.getEngineVersion()))
                && (this.getReferer() == null ? other.getReferer() == null : this.getReferer().equals(other.getReferer()))
                && (this.getTotalCount() == null ? other.getTotalCount() == null : this.getTotalCount().equals(other.getTotalCount()))
                && (this.getEventDate() == null ? other.getEventDate() == null : this.getEventDate().equals(other.getEventDate()))
                && (this.getMemberNo() == null ? other.getMemberNo() == null : this.getMemberNo().equals(other.getMemberNo()))
                && (this.getMemberType() == null ? other.getMemberType() == null : this.getMemberType().equals(other.getMemberType()))
                && (this.getMmCode() == null ? other.getMmCode() == null : this.getMmCode().equals(other.getMmCode()))
                && (this.getStoreCode() == null ? other.getStoreCode() == null : this.getStoreCode().equals(other.getStoreCode()))
                && (this.getChannel() == null ? other.getChannel() == null : this.getChannel().equals(other.getChannel()))
                && (this.getPublishType() == null ? other.getPublishType() == null : this.getPublishType().equals(other.getPublishType()))
                && (this.getPageNo() == null ? other.getPageNo() == null : this.getPageNo().equals(other.getPageNo()))
                && (this.getIsNew() == null ? other.getIsNew() == null : this.getIsNew().equals(other.getIsNew()))
                && (this.getStayTime() == null ? other.getStayTime() == null : this.getStayTime().equals(other.getStayTime()))
                && (this.getPageType() == null ? other.getPageType() == null : this.getPageType().equals(other.getPageType()))
                && (this.getGoodsType() == null ? other.getGoodsType() == null : this.getGoodsType().equals(other.getGoodsType()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getIp() == null) ? 0 : getIp().hashCode());
        result = prime * result + ((getTs() == null) ? 0 : getTs().hashCode());
        result = prime * result + ((getEvent() == null) ? 0 : getEvent().hashCode());
        result = prime * result + ((getUuid() == null) ? 0 : getUuid().hashCode());
        result = prime * result + ((getBizId() == null) ? 0 : getBizId().hashCode());
        result = prime * result + ((getUserAgent() == null) ? 0 : getUserAgent().hashCode());
        result = prime * result + ((getMobile() == null) ? 0 : getMobile().hashCode());
        result = prime * result + ((getBrowser() == null) ? 0 : getBrowser().hashCode());
        result = prime * result + ((getBrowserVersion() == null) ? 0 : getBrowserVersion().hashCode());
        result = prime * result + ((getPlatform() == null) ? 0 : getPlatform().hashCode());
        result = prime * result + ((getOs() == null) ? 0 : getOs().hashCode());
        result = prime * result + ((getOsVersion() == null) ? 0 : getOsVersion().hashCode());
        result = prime * result + ((getEngine() == null) ? 0 : getEngine().hashCode());
        result = prime * result + ((getEngineVersion() == null) ? 0 : getEngineVersion().hashCode());
        result = prime * result + ((getReferer() == null) ? 0 : getReferer().hashCode());
        result = prime * result + ((getTotalCount() == null) ? 0 : getTotalCount().hashCode());
        result = prime * result + ((getEventDate() == null) ? 0 : getEventDate().hashCode());
        result = prime * result + ((getMemberNo() == null) ? 0 : getMemberNo().hashCode());
        result = prime * result + ((getMemberType() == null) ? 0 : getMemberType().hashCode());
        result = prime * result + ((getMmCode() == null) ? 0 : getMmCode().hashCode());
        result = prime * result + ((getStoreCode() == null) ? 0 : getStoreCode().hashCode());
        result = prime * result + ((getChannel() == null) ? 0 : getChannel().hashCode());
        result = prime * result + ((getPublishType() == null) ? 0 : getPublishType().hashCode());
        result = prime * result + ((getPageNo() == null) ? 0 : getPageNo().hashCode());
        result = prime * result + ((getIsNew() == null) ? 0 : getIsNew().hashCode());
        result = prime * result + ((getStayTime() == null) ? 0 : getStayTime().hashCode());
        result = prime * result + ((getPageType() == null) ? 0 : getPageType().hashCode());
        result = prime * result + ((getGoodsType() == null) ? 0 : getGoodsType().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", ip=").append(ip);
        sb.append(", ts=").append(ts);
        sb.append(", event=").append(event);
        sb.append(", uuid=").append(uuid);
        sb.append(", bizId=").append(bizId);
        sb.append(", userAgent=").append(userAgent);
        sb.append(", mobile=").append(mobile);
        sb.append(", browser=").append(browser);
        sb.append(", browserVersion=").append(browserVersion);
        sb.append(", platform=").append(platform);
        sb.append(", os=").append(os);
        sb.append(", osVersion=").append(osVersion);
        sb.append(", engine=").append(engine);
        sb.append(", engineVersion=").append(engineVersion);
        sb.append(", referer=").append(referer);
        sb.append(", totalCount=").append(totalCount);
        sb.append(", eventDate=").append(eventDate);
        sb.append(", memberNo=").append(memberNo);
        sb.append(", memberType=").append(memberType);
        sb.append(", mmCode=").append(mmCode);
        sb.append(", storeCode=").append(storeCode);
        sb.append(", channel=").append(channel);
        sb.append(", publishType=").append(publishType);
        sb.append(", pageNo=").append(pageNo);
        sb.append(", isNew=").append(isNew);
        sb.append(", stayTime=").append(stayTime);
        sb.append(", pageType=").append(pageType);
        sb.append(", goodsType=").append(goodsType);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}