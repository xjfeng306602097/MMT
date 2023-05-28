package com.makro.mall.admin.pojo.vo;

import com.makro.mall.admin.pojo.entity.MmPublishJobLineTask;
import lombok.*;

/**
 * @author jincheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MmPublishJobLineTaskRepVO extends MmPublishJobLineTask {
    private String mmSegment;
    private String mmStore;
    private Integer pushTotal;
}
