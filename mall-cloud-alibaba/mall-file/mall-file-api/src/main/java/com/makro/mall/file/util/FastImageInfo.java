package com.makro.mall.file.util;

import com.jhlabs.image.ContrastFilter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;

/**
 * @author xiaojunfeng
 * @description 图片工具类 快速获取图片的大小
 * @date 2021/11/24
 */
@Data
@Slf4j
public class FastImageInfo {
    private int height;
    private int width;
    private String mimeType;
    private boolean isRgb = true;
    private boolean filterCheckRgb = false;
    private BufferedImage image;
    private String suffix;
    private BufferedImage dest;

    public FastImageInfo(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            processStream(is);
        }
    }

    public FastImageInfo(InputStream is, String suffix) throws IOException {
        processStream(is);
        this.suffix = suffix;
    }

    public FastImageInfo(byte[] bytes) throws IOException {
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            processStream(is);
        }
    }

    private void processStream(InputStream is) throws IOException {
        // 检验是cmyk还是rgb
        BufferedInputStream bis = new BufferedInputStream(is);
        try {
            bis.mark(is.available() + 1);
            checkRgbOrCmyk(bis);
            //bis use for read
            bis.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.image = ImageIO.read(bis);
        int c1 = is.read();
        int c2 = is.read();
        int c3 = is.read();

        mimeType = null;
        width = height = -1;

        if (c1 == 'G' && c2 == 'I' && c3 == 'F') { // GIF
            is.skip(3);
            width = readInt(is, 2, false);
            height = readInt(is, 2, false);
            mimeType = "image/gif";
            filterCheckRgb = true;
        } else if (c1 == 0xFF && c2 == 0xD8) { // JPG
            while (c3 == 255) {
                int marker = is.read();
                int len = readInt(is, 2, true);
                if (marker == 192 || marker == 193 || marker == 194) {
                    is.skip(1);
                    height = readInt(is, 2, true);
                    width = readInt(is, 2, true);
                    mimeType = "image/jpeg";
                    break;
                }
                is.skip(len - 2);
                c3 = is.read();
            }
        } else if (c1 == 137 && c2 == 80 && c3 == 78) { // PNG
            is.skip(15);
            width = readInt(is, 2, true);
            is.skip(2);
            height = readInt(is, 2, true);
            mimeType = "image/png";
        } else if (c1 == 66 && c2 == 77) { // BMP
            is.skip(15);
            width = readInt(is, 2, false);
            is.skip(2);
            height = readInt(is, 2, false);
            mimeType = "image/bmp";
            filterCheckRgb = true;
        } else if (c1 == 'R' && c2 == 'I' && c3 == 'F') { // WEBP
            byte[] bytes = new byte[27];
            is.read(bytes);
            width = ((int) bytes[24] & 0xff) << 8 | ((int) bytes[23] & 0xff);
            height = ((int) bytes[26] & 0xff) << 8 | ((int) bytes[25] & 0xff);
            mimeType = "image/webp";
        } else {
            int c4 = is.read();
            if ((c1 == 'M' && c2 == 'M' && c3 == 0 && c4 == 42)
                    || (c1 == 'I' && c2 == 'I' && c3 == 42 && c4 == 0)) { //TIFF
                boolean bigEndian = c1 == 'M';
                int ifd = 0;
                int entries;
                ifd = readInt(is, 4, bigEndian);
                is.skip(ifd - 8);
                entries = readInt(is, 2, bigEndian);
                for (int i = 1; i <= entries; i++) {
                    int tag = readInt(is, 2, bigEndian);
                    int fieldType = readInt(is, 2, bigEndian);
                    int valOffset;
                    if ((fieldType == 3 || fieldType == 8)) {
                        valOffset = readInt(is, 2, bigEndian);
                        is.skip(2);
                    } else {
                        valOffset = readInt(is, 4, bigEndian);
                    }
                    if (tag == 256) {
                        width = valOffset;
                    } else if (tag == 257) {
                        height = valOffset;
                    }
                    if (width != -1 && height != -1) {
                        mimeType = "image/tiff";
                        break;
                    }
                }
            }
        }
        width = image.getWidth();
        height = image.getHeight();
    }


    private void checkRgbOrCmyk(InputStream is) throws IOException {
        if (!filterCheckRgb) {
            int len = 1;
            byte[] temp = new byte[len];
            StringWriter sw = new StringWriter();
            /*16进制转化模块*/
            int i = 0;
            for (; (is.read(temp, 0, len)) != -1 && i < 4; ) {
                if (temp[0] > 0xf && temp[0] <= 0xff) {
                    sw.write(Integer.toHexString(temp[0]));
                } else if (temp[0] >= 0x0 && temp[0] <= 0xf) {
                    //对于只有1位的16进制数前边补“0”
                    sw.write("0" + Integer.toHexString(temp[0]));
                } else {
                    //对于int<0的位转化为16进制的特殊处理，因为Java没有Unsigned int，所以这个int可能为负数
                    sw.write(Integer.toHexString(temp[0]).substring(6));
                }
                i++;
            }
            isRgb = !"ffd8ffe1".equalsIgnoreCase(sw.toString()) && !"ffd8ffed".equalsIgnoreCase(sw.toString());
        }
    }


    private int readInt(InputStream is, int noOfBytes, boolean bigEndian) throws IOException {
        int ret = 0;
        int sv = bigEndian ? ((noOfBytes - 1) * 8) : 0;
        int cnt = bigEndian ? -8 : 8;
        for (int i = 0; i < noOfBytes; i++) {
            ret |= is.read() << sv;
            sv += cnt;
        }
        return ret;
    }

    public InputStream convertToRgb() throws IOException {
        if (this.dest == null) {
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
            ColorConvertOp op = new ColorConvertOp(cs, null);
            this.dest = op.filter(this.image, null);
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(this.dest, suffix, os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    public InputStream contrastFilter(double brightness, double contrast) throws IOException {
        ColorModel colorModel = image.getColorModel();
        boolean isAlphaPremultiplied = image.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        BufferedImage newImage = new BufferedImage(colorModel, raster,
                isAlphaPremultiplied, null);
        // contrast
        ContrastFilter filt = new ContrastFilter();
        filt.setBrightness(0.92f);
        filt.setContrast(1.25f);
        filt.filter(newImage, newImage);

        // contrast again
        filt.filter(newImage, newImage);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(newImage, suffix, os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    public InputStream revertCmyk() throws IOException {
        ColorModel colorModel = image.getColorModel();
        boolean isAlphaPremultiplied = image.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        BufferedImage newImage = new BufferedImage(colorModel, raster,
                isAlphaPremultiplied, null);
        for (int y = image.getMinY(); y < image.getHeight(); y++) {
            // 内层遍历是X轴的像素
            for (int x = image.getMinX(); x < image.getWidth(); x++) {
                int r = image.getRGB(x, y);
                int alpha = r >> 24;
                int red = (r >> 16) & 0x0ff;
                int green = (r >> 8) & 0x0ff;
                int blue = r & 0x0ff;
                red = 255 - red;
                green = 255 - green;
                blue = 255 - blue;
                r = (alpha << 24) | (red << 16) | (green << 8) | blue;
                // #AARRGGBB 最前两位为透明度
                newImage.setRGB(x, y, r);
            }
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(newImage, suffix, os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    public InputStream darker() throws IOException {
        // 生产背景透明和内容透明的图片
        ImageIcon imageIcon = new ImageIcon(image);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        // 获取画笔
        Graphics2D g2D = (Graphics2D) bufferedImage.getGraphics();
        // 绘制Image的图片，使用了imageIcon.getImage()，目的就是得到image,直接使用image就可以的
        g2D.drawImage(imageIcon.getImage(), 0, 0, null);
        // 图片透明度
        int alpha = 0;
        // 外层遍历是Y轴的像素
        for (int y = bufferedImage.getMinY(); y < bufferedImage.getHeight(); y++) {
            // 内层遍历是X轴的像素
            for (int x = bufferedImage.getMinX(); x < bufferedImage.getWidth(); x++) {
                int rgb = bufferedImage.getRGB(x, y);
                // 对当前颜色判断是否在指定区间内
                if (rangeColorInRange(image, x, y)) {
                    alpha = 0;
                } else {
                    // 设置为不透明
                    alpha = 255;
                    rgb = darker(new Color(rgb), 0.85).getRGB();
                }
                Color color = new Color(rgb);
                // #AARRGGBB 最前两位为透明度
                rgb = (alpha << 24) | (rgb & 0x00ffffff);
                bufferedImage.setRGB(x, y, rgb);
            }
        }
        // 绘制设置了RGB的新图片,这一步感觉不用也可以只是透明地方的深浅有变化而已，就像蒙了两层的感觉
        g2D.drawImage(bufferedImage, 0, 0, null);
        for (int y = bufferedImage.getMinY(); y < bufferedImage.getHeight(); y++) {
            // 内层遍历是X轴的像素
            for (int x = bufferedImage.getMinX(); x < bufferedImage.getWidth(); x++) {
                int rgb = bufferedImage.getRGB(x, y);
                // 对当前颜色判断是否在指定区间内
                if ((rgb & 0xffffff) == 0) {
                    rgb = (0) | (rgb & 0x00ffffff);
                    bufferedImage.setRGB(x, y, rgb);
                }
            }
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, suffix, os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    public boolean rangeColorInRange(BufferedImage image, int x, int y) {
        // 循环校验
        int minX = Math.max(x - COUNT, 0);
        int minY = Math.max(y - COUNT, 0);
        int maxX = Math.min(x + COUNT, image.getWidth() - 1);
        int maxY = Math.min(y + COUNT, image.getHeight() - 1);
        for (int y1 = minY; y1 < maxY; y1++) {
            for (int x1 = minX; x1 < maxX; x1++) {
                int rgb = image.getRGB(x1, y1);
                if (!(colorInRange(rgb) || rgb >> 24 == 0)) {
                    // 是透明底，忽略
                    return false;
                }
            }
        }
        return true;
    }

    // 判断是背景还是内容
    public boolean colorInRange(int color) {
        // 获取color(RGB)中R位
        int red = (color & 0xff0000) >> 16;
        // 获取color(RGB)中G位
        int green = (color & 0x00ff00) >> 8;
        // 获取color(RGB)中B位
        int blue = (color & 0x0000ff);
        if (red >= COLOR_RANGE && red == green && red == blue) {
            return true;
        }
        return false;
    }

    public static Color darker(Color color, double factor) {
        return new Color(Math.max((int) (color.getRed() * factor), 0),
                Math.max((int) (color.getGreen() * factor), 0),
                Math.max((int) (color.getBlue() * factor), 0),
                color.getAlpha());
    }

    //色差范围0~255
    public static final int COLOR_RANGE = 233;

    public static final int COUNT = 2;


}