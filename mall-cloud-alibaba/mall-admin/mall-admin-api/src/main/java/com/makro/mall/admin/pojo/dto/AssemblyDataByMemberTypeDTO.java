package com.makro.mall.admin.pojo.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssemblyDataByMemberTypeDTO implements Serializable {
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
    private Long uv;
    /**
     *
     */
    private Date date;
}
