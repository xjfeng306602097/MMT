package com.makro.mall.stat.pojo.snapshot;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName assembly_data_by_member_type
 */
@TableName(value = "assembly_data_by_member_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssemblyDataByMemberType implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    private String mmCode;
    /**
     * 会员类型
     */
    private String memberType;
    /**
     * mm发送人群数
     */
    private Long total;

    /**
     * mm页面访问人数
     */
    private Long pv;
    /**
     * mm页面访问人数
     */
    private Long uv;
    /**
     *
     */
    private Date date;
}
