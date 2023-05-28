package com.makro.mall.admin.pojo.vo;

import com.makro.mall.admin.pojo.entity.MmPublishJobEmailTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author jincheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MmPublishJobEmailTaskRepVO extends MmPublishJobEmailTask {
    private String mmSegment;
    private String mmStore;
    private Integer pushTotal;

}
