package com.makro.mall.file.controller;

import com.jhlabs.image.ContrastFilter;
import com.jhlabs.image.GlowFilter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/11/30
 */
public class Test {

    public static final Integer COUNT = 2;

    public static void main(String[] args) throws IOException {
        //BufferedImage image = ImageIO.read(new File("C:\\Users\\admin\\Pictures\\pepsi\\pepsi.png"));
        BufferedImage image = ImageIO.read(new File("C:\\Users\\admin\\Pictures\\pepsi\\feng\\rgb.png"));
        // contrast
        ContrastFilter filt = new ContrastFilter();
        filt.setBrightness(0.92f);
        filt.setContrast(1.25f);
        filt.filter(image, image);

        // contrast again
        filt.filter(image, image);

        //ImageIO.write(image, "png", new File("C:\\Users\\admin\\Pictures\\pepsi\\pepsi-bak.png"));
        ImageIO.write(image, "png", new File("C:\\Users\\admin\\Pictures\\pepsi\\feng\\rgb-bak.png"));
    }

    /*public static void main(String[] args) throws Exception {
        BufferedImage image = ImageIO.read(new File("C:\\Users\\admin\\Pictures\\pepsi\\pepsi.png"));
        // 高度和宽度
        int height = image.getHeight();
        int width = image.getWidth();

        // 生产背景透明和内容透明的图片
        ImageIcon imageIcon = new ImageIcon(image);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        // 获取画笔
        Graphics2D g2D = (Graphics2D) bufferedImage.getGraphics();
        // 绘制Image的图片，使用了imageIcon.getImage()，目的就是得到image,直接使用image就可以的
        g2D.drawImage(imageIcon.getImage(), 0, 0, null);

        int alpha = 0; // 图片透明度
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
                System.out.println("argb:" + color.getAlpha() + "," + color.getRed() + "," + color.getGreen() + "," + color.getBlue());
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
        // 生成图片为PNG
        ImageIO.write(bufferedImage, "png", new File("C:\\Users\\admin\\Pictures\\pepsi\\pepsi-bak.png"));
    }*/

    public static Color darker(Color color, double factor) {
        return new Color(Math.max((int) (color.getRed() * factor), 0),
                Math.max((int) (color.getGreen() * factor), 0),
                Math.max((int) (color.getBlue() * factor), 0),
                color.getAlpha());
    }

    public static boolean rangeColorInRange(BufferedImage image, int x, int y) {
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
    public static boolean colorInRange(int color) {
        // 获取color(RGB)中R位
        int red = (color & 0xff0000) >> 16;
        // 获取color(RGB)中G位
        int green = (color & 0x00ff00) >> 8;
        // 获取color(RGB)中B位
        int blue = (color & 0x0000ff);
        // 通过RGB三分量来判断当前颜色是否在指定的颜色区间内
        /*if (red >= color_range && green >= color_range && blue >= color_range){
            return true;
        };*/
        if (red >= color_range && red == green && red == blue) {
            return true;
        }
        return false;
    }

    //色差范围0~255
    public static int color_range = 233;
}
