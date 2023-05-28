package com.makro.mall.file.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.util.StreamConvertUtil;
import com.makro.mall.file.component.minio.MinioFileComponent;
import com.makro.mall.file.config.MinioProperties;
import com.makro.mall.file.pojo.dto.PicUploadDTO;
import com.makro.mall.file.util.FastImageInfo;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * @author xiaojunfeng
 * @description 图片上传控制器
 * @date 2021/11/24
 */
@Api(tags = "图片上传接口")
@RestController
@RequestMapping("/api/v1/pictures")
@RequiredArgsConstructor
@Slf4j
public class PicController {

    private final MinioFileComponent minioFileComponent;
    private final MinioProperties minioProperties;

    @Value("${file.image.thumbnail.width.limit:500}")
    private BigDecimal maxLengthLimit;

    @PostMapping
    @ApiOperation(value = "图片上传")
    public BaseResponse<PicUploadDTO> upload(
            @ApiParam("文件") @RequestParam(value = "file") MultipartFile file,
            @ApiParam("桶名称")  @RequestParam(value = "bucketName", required = false) String bucketName,
            @ApiParam("object名称")  @RequestParam(value = "objectName", required = false) String objectName,
            @ApiParam("压缩比例，他前端给过来，这个limit就不管了，直接按照thumbRate给他压缩")  @RequestParam(value = "thumbRate", required = false) BigDecimal thumbRate,
            @ApiParam("压缩阈值，当图片超过这个阈值就会开启压缩")  @RequestParam(value = "limit", required = false) BigDecimal limit
    ) {
        try {
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
            if (suffix.contains("_")) {
                suffix = suffix.substring(0, suffix.lastIndexOf("_"));
            }
            String finalObjectName = Optional.ofNullable(objectName).orElse(IdUtil.simpleUUID() + "." + suffix);
            if (StrUtil.isEmpty(bucketName)) {
                bucketName = minioProperties.getBucketName(suffix);
            }
            PicUploadDTO dto = new PicUploadDTO();
            // 判断是否需要缩略图
            InputStream is = file.getInputStream();
            FastImageInfo info = new FastImageInfo(is, suffix);
            InputStream stream = file.getInputStream();
            String path = null;
            String transformName = null;
            if (info.isRgb()) {
                transformName = finalObjectName + "_cmyk";
                finalObjectName = finalObjectName + "_rgb";
                dto.setOriginPath(minioFileComponent.putObjectWithProxy(bucketName, finalObjectName, file.getInputStream(), file.getContentType()));
                dto.setTransformPath(minioFileComponent.putObjectWithProxy(bucketName, transformName, file.getInputStream(), file.getContentType()));
            } else {
                transformName = finalObjectName + "_rgb";
                finalObjectName = finalObjectName + "_cmyk";
                dto.setOriginPath(minioFileComponent.putObjectWithProxy(bucketName, finalObjectName, file.getInputStream(), file.getContentType()));
                dto.setTransformPath(minioFileComponent.putObjectWithProxy(bucketName, transformName, file.getInputStream(), file.getContentType()));
            }
            BigDecimal width = new BigDecimal(info.getWidth());
            BigDecimal height = new BigDecimal(info.getHeight());
            dto.setOriginWidth(width);
            dto.setOriginHeight(height);
            dto.setRgb(info.isRgb());
            BigDecimal lengthLimit = null;
            if (limit == null) {
                lengthLimit = maxLengthLimit;
            } else {
                lengthLimit = limit;
            }
            boolean isWidthMax = (width.compareTo(height) > 0);
            path = getThumbnailPath(file, bucketName, thumbRate, finalObjectName, dto, file.getInputStream(), width, height, lengthLimit, isWidthMax, null, suffix);
            dto.setThumbnailPath(path);
            dto.setTransformThumbnailPath(getThumbnailPath(file, bucketName, thumbRate, transformName, dto, info.isRgb() ? info.contrastFilter(0.92f, 1.25f) : stream, width, height, lengthLimit, isWidthMax, info.getDest(), suffix));
            return BaseResponse.success(dto);
        } catch (Exception e) {
            log.error("上传图片失败", e);
            return BaseResponse.error(e.getMessage());
        }
    }

    private String getThumbnailPath(MultipartFile file, String bucketName, BigDecimal thumbRate, String finalObjectName,
                                    PicUploadDTO dto, InputStream stream, BigDecimal width, BigDecimal height,
                                    BigDecimal lengthLimit, boolean isWidthMax, BufferedImage image, String suffix) throws Exception {
        String path;
        if (thumbRate != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BigDecimal thumbHeight = thumbRate.multiply(height);
            BigDecimal thumbWidth = thumbRate.multiply(width);
            dto.setThumbnailWidth(thumbWidth);
            dto.setThumbnailHeight(thumbHeight);
            if (image == null) {
                Thumbnails.of(stream).size(thumbWidth.intValue(), thumbHeight.intValue()).imageType(BufferedImage.TYPE_INT_ARGB)
                        .toOutputStream(outputStream);
            } else {
                Thumbnails.of(image).size(thumbWidth.intValue(), thumbHeight.intValue()).imageType(BufferedImage.TYPE_INT_ARGB).outputFormat(suffix)
                        .toOutputStream(outputStream);
            }
            path = minioFileComponent.putObjectWithProxy(bucketName, finalObjectName + "_thumb",
                    StreamConvertUtil.parse(outputStream), file.getContentType());
        } else if (lengthLimit.compareTo(width) < 0) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BigDecimal thumbWidth = null;
            BigDecimal thumbHeight = null;
            if (isWidthMax) {
                thumbWidth = lengthLimit;
                thumbHeight = lengthLimit.divide(width, 3, RoundingMode.HALF_UP).multiply(height);
            } else {
                thumbWidth = lengthLimit.divide(height, 3, RoundingMode.HALF_UP).multiply(height);
                thumbHeight = lengthLimit;
            }
            dto.setThumbnailWidth(thumbWidth);
            dto.setThumbnailHeight(thumbHeight);
            if (image == null) {
                Thumbnails.of(stream).size(thumbWidth.intValue(), thumbHeight.intValue()).imageType(BufferedImage.TYPE_INT_ARGB)
                        .toOutputStream(outputStream);
            } else {
                Thumbnails.of(image).size(thumbWidth.intValue(), thumbHeight.intValue()).imageType(BufferedImage.TYPE_INT_ARGB).outputFormat(suffix)
                        .toOutputStream(outputStream);
            }
            path = minioFileComponent.putObjectWithProxy(bucketName, finalObjectName + "_thumb",
                    StreamConvertUtil.parse(outputStream), file.getContentType());
        } else {
            dto.setThumbnailHeight(height);
            dto.setThumbnailWidth(width);
            path = minioFileComponent.putObjectWithProxy(bucketName, finalObjectName + "_thumb",
                    stream, file.getContentType());
        }
        return path;
    }


}
