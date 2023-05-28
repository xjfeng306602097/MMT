package com.makro.mall.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.makro.mall.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Access;
import java.io.Serializable;
import java.util.Date;

/**
 * MM发布任务表
 *
 * @TableName MM_PUBLISH_JOB
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "MM_PUBLISH_JOB")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class MmPublishJob extends BaseEntity implements Serializable {

    public static final Long STATUS_INIT = 0L;

    public static final Long STATUS_APPROVE = 1L;

    public static final Long STATUS_REJECT = 2L;

    public static final String MEDIA_TYPE_H5 = "h5";

    public static final String MEDIA_TYPE_PDF = "pdf";

    public static final String MEDIA_TYPE_PDF_PRINTING = "pdf-printing";

    public static final String MEDIA_TYPE_APP = "app";


    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("关联的流程")
    private Long relatedFlow;

    @ApiModelProperty("媒体类型")
    private String mediaType;

    private Long sendByEmail;

    private Long sendByLine;

    private Long sendBySms;

    private String pdfSize;

    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("更新时会同步到MM主表")
    private String filePath;

    private String mmCode;

    @ApiModelProperty("0-初始，1-通过审核，2-审核拒绝")
    private Long status;

    @ApiModelProperty("0-初始化，1-生成成功,2-生成失败")
    private Long publishStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private Date gmtCreateBegin;

    @TableField(exist = false)
    private Date gmtCreateEnd;

    @ApiModelProperty("创建时间")
    private Date buildTime;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    @ApiModelProperty("更新时会同步到MM主表")
    private String appTitle;

    public MmPublishJob convertFilePath() {
        if (STATUS_REJECT.equals(this.status)) {
            this.filePath = null;
        }
        return this;
    }
}
