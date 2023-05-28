package com.makro.mall.stat.pojo.constant;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 功能描述:
 * <p>
 * 1开头FR
 * 2开头NFR
 * 3开头HO
 * 4开头SV
 * 5开头DT
 * 6开头OT
 *
 * @Author: 卢嘉俊
 * @Date: 2022/8/22 数据分析
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum CustomerTypeEnum {
    FR("FR", "1"), NFR("NFR", "2"), HO("HO", "3"), SV("SV", "4"), DT("DT", "5"), OT("OT", "6");

    private String name;
    private String no;


    public static String getNoByMemberType(String memberType) {
        return StrUtil.subPre(memberType, 1);
    }

    public static String getNameByMemberType(String memberType) {
        return getNameByNo(getNoByMemberType(memberType));
    }

    public static List<String> getNames() {
        return Arrays.stream(CustomerTypeEnum.values()).map(CustomerTypeEnum::getName).collect(Collectors.toList());
    }

    public static String getNoByName(String name) {
        for (CustomerTypeEnum value : CustomerTypeEnum.values()) {
            if (name.equals(value.getName())) {
                return value.getNo();
            }
        }
        return "";
    }

    public static String getNameByNo(String no) {
        for (CustomerTypeEnum value : CustomerTypeEnum.values()) {
            if (no.equals(value.getNo())) {
                return value.getName();
            }
        }
        return "";
    }

    /**
     * @param no 101,102
     * @return 生成会员类型字符串 FR,NFR这样表示
     */
    public static String getCustomerType(String no) {
        if (StrUtil.isNotBlank(no)) {
            boolean fr = false;
            boolean nfr = false;
            boolean ho = false;
            boolean sv = false;
            boolean dt = false;
            boolean ot = false;
            for (String s : StrUtil.split(no, ",")) {
                switch (CustomerTypeEnum.getNameByMemberType(s)) {
                    case "FR":
                        fr = true;
                        break;
                    case "NFR":
                        nfr = true;
                        break;
                    case "HO":
                        ho = true;
                        break;
                    case "SV":
                        sv = true;
                        break;
                    case "DT":
                        dt = true;
                        break;
                    case "OT":
                        ot = true;
                        break;
                    default:
                        break;
                }
            }
            StringBuilder sb = new StringBuilder();
            if (fr) {
                if (!"".equals(sb.toString())) {
                    sb.append(",");
                }
                sb.append("FR");
            }
            if (nfr) {
                if (!"".equals(sb.toString())) {
                    sb.append(",");
                }
                sb.append("NFR");
            }
            if (ho) {
                if (!"".equals(sb.toString())) {
                    sb.append(",");
                }
                sb.append("HO");
            }
            if (sv) {
                if (!"".equals(sb.toString())) {
                    sb.append(",");
                }
                sb.append("SV");
            }
            if (dt) {
                if (!"".equals(sb.toString())) {
                    sb.append(",");
                }
                sb.append("DT");
            }
            if (ot) {
                if (!"".equals(sb.toString())) {
                    sb.append(",");
                }
                sb.append("OT");
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * @param no 101,102
     * @return 生成会员类型字符串 FR,NFR这样表示
     */
    public static String getCustomerType(List<String> no) {
        if (CollUtil.isNotEmpty(no)) {
            boolean fr = false;
            boolean nfr = false;
            boolean ho = false;
            boolean sv = false;
            boolean dt = false;
            boolean ot = false;
            for (String s : no) {
                switch (CustomerTypeEnum.getNameByMemberType(s)) {
                    case "FR":
                        fr = true;
                        break;
                    case "NFR":
                        nfr = true;
                        break;
                    case "HO":
                        ho = true;
                        break;
                    case "SV":
                        sv = true;
                        break;
                    case "DT":
                        dt = true;
                        break;
                    case "OT":
                        ot = true;
                        break;
                    default:
                        break;
                }
            }
            StringBuilder sb = new StringBuilder();
            if (fr) {
                if (!"".equals(sb.toString())) {
                    sb.append(",");
                }
                sb.append("FR");
            }
            if (nfr) {
                if (!"".equals(sb.toString())) {
                    sb.append(",");
                }
                sb.append("NFR");
            }
            if (ho) {
                if (!"".equals(sb.toString())) {
                    sb.append(",");
                }
                sb.append("HO");
            }
            if (sv) {
                if (!"".equals(sb.toString())) {
                    sb.append(",");
                }
                sb.append("SV");
            }
            if (dt) {
                if (!"".equals(sb.toString())) {
                    sb.append(",");
                }
                sb.append("DT");
            }
            if (ot) {
                if (!"".equals(sb.toString())) {
                    sb.append(",");
                }
                sb.append("OT");
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * @param memberType 101,102
     * @param str
     * @return
     */
    public static boolean judge(String memberType, String str) {
        for (String s : StrUtil.split(memberType, ",")) {
            if (StrUtil.equals(CustomerTypeEnum.getNameByMemberType(s), str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param memberType FR,NFR
     * @param str
     * @return
     */
    public static boolean judge2(String memberType, String str) {
        for (String s : StrUtil.split(memberType, ",")) {
            if (StrUtil.equals(s, str)) {
                return true;
            }
        }
        return false;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomerTypeDTO {
        private boolean fr;

        private boolean nfr;

        private boolean ho;

        private boolean sv;

        private boolean dt;

        private boolean ot;
    }
}
