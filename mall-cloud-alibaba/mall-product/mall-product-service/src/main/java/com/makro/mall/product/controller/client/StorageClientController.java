package com.makro.mall.product.controller.client;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.product.pojo.dto.ItemCodeSegmentDelDTO;
import com.makro.mall.product.pojo.entity.ProdList;
import com.makro.mall.product.pojo.vo.ExcelDataFromSheetName;
import com.makro.mall.product.service.ImportV4Service;
import com.makro.mall.product.service.ProdListService;
import com.makro.mall.product.service.ProdStorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Ljj
 */
@Api(tags = "关联MM的商品列表", hidden = true)
@RestController
@RequestMapping("/api/v1/prod/storage/client")
@RequiredArgsConstructor
public class StorageClientController {


    private final ProdStorageService prodStorageService;



    @ApiOperation(value = "删除商品与segment关联",hidden = true)
    @DeleteMapping("/itemCodeSegment")
    public BaseResponse<Boolean> itemCodeSegment(@RequestBody ItemCodeSegmentDelDTO dto) {
        return BaseResponse.success(prodStorageService.delItemCodeSegment(dto));
    }
}
