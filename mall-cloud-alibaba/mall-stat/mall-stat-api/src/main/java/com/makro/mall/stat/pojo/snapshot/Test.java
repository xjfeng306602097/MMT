package com.makro.mall.stat.pojo.snapshot;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName test
 */
@TableName(value = "test")
@Data
public class Test implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    private String mmCode;
    /**
     * 店铺code
     */
    private String storeCode;
    /**
     * 会员类型
     */
    private String memberType;
    /**
     *
     */
    private String mmType;
    /**
     * 渠道,email/sms/line/facebook/app
     */
    private String channel;
    /**
     * 页面类型
     */
    private String pageType;
    /**
     *
     */
    private Long pv;
    /**
     *
     */
    private Long uv;
    /**
     *
     */
    private Date date;

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
        Test other = (Test) that;
        return (this.getMmCode() == null ? other.getMmCode() == null : this.getMmCode().equals(other.getMmCode()))
                && (this.getStoreCode() == null ? other.getStoreCode() == null : this.getStoreCode().equals(other.getStoreCode()))
                && (this.getMemberType() == null ? other.getMemberType() == null : this.getMemberType().equals(other.getMemberType()))
                && (this.getMmType() == null ? other.getMmType() == null : this.getMmType().equals(other.getMmType()))
                && (this.getChannel() == null ? other.getChannel() == null : this.getChannel().equals(other.getChannel()))
                && (this.getPageType() == null ? other.getPageType() == null : this.getPageType().equals(other.getPageType()))
                && (this.getPv() == null ? other.getPv() == null : this.getPv().equals(other.getPv()))
                && (this.getUv() == null ? other.getUv() == null : this.getUv().equals(other.getUv()))
                && (this.getDate() == null ? other.getDate() == null : this.getDate().equals(other.getDate()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getMmCode() == null) ? 0 : getMmCode().hashCode());
        result = prime * result + ((getStoreCode() == null) ? 0 : getStoreCode().hashCode());
        result = prime * result + ((getMemberType() == null) ? 0 : getMemberType().hashCode());
        result = prime * result + ((getMmType() == null) ? 0 : getMmType().hashCode());
        result = prime * result + ((getChannel() == null) ? 0 : getChannel().hashCode());
        result = prime * result + ((getPageType() == null) ? 0 : getPageType().hashCode());
        result = prime * result + ((getPv() == null) ? 0 : getPv().hashCode());
        result = prime * result + ((getUv() == null) ? 0 : getUv().hashCode());
        result = prime * result + ((getDate() == null) ? 0 : getDate().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", mmCode=").append(mmCode);
        sb.append(", storeCode=").append(storeCode);
        sb.append(", memberType=").append(memberType);
        sb.append(", mmType=").append(mmType);
        sb.append(", channel=").append(channel);
        sb.append(", pageType=").append(pageType);
        sb.append(", pv=").append(pv);
        sb.append(", uv=").append(uv);
        sb.append(", date=").append(date);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}