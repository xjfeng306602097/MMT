package com.makro.mall.admin.pojo.vo;

import com.makro.mall.admin.pojo.entity.MmPublishJobSmsTask;
import lombok.*;

/**
 * @author jincheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MmPublishJobSmsTaskRepVO extends MmPublishJobSmsTask {
    private String mmSegment;
    private String mmStore;
    private Integer pushTotal;

}
