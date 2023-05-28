package com.makro.mall.admin.common.constant;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/4/24
 */
public interface EmailConstants {

    String HTML_PASSWORD_TEMPLATE = "<body style=\"color: #666; font-size: 14px; font-family: 'Open Sans',Helvetica,Arial,sans-serif;\">\n" +
            "<div class=\"box-content\" style=\"width: 80%; margin: 20px auto; max-width: 800px; min-width: 600px;\">\n" +
            "    <div class=\"header-tip\" style=\"font-size: 12px;\n" +
            "                                   color: #aaa;\n" +
            "                                   text-align: right;\n" +
            "                                   padding-right: 25px;\n" +
            "                                   padding-bottom: 10px;\">\n" +
            "    </div>\n" +
            "    <div class=\"info-top\" style=\"padding: 15px 25px;\n" +
            "                                 border-top-left-radius: 10px;\n" +
            "                                 border-top-right-radius: 10px;\n" +
            "                                 background: {0};\n" +
            "                                 color: #fff;\n" +
            "                                 overflow: hidden;\n" +
            "                                 line-height: 32px;\n" +
            "\t\t\t\t\t\t\t\t background:lightgreen\">\n" +
            "        <img src=\"data:image/png;base64,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\" style=\"float: left; margin: 0 10px 0 0; width: 32px;\" /><div style=\"color:#010e07\"><strong>Reset your password</strong></div>\n" +
            "    </div>\n" +
            "    <div class=\"info-wrap\" style=\"border-bottom-left-radius: 10px;\n" +
            "                                  border-bottom-right-radius: 10px;\n" +
            "                                  border:1px solid #ddd;\n" +
            "                                  overflow: hidden;\n" +
            "                                  padding: 15px 15px 20px;\">\n" +
            "        <div class=\"tips\" style=\"padding:15px;\">\n" +
            "            <p style=\" list-style: 160%; margin: 10px 0;font-size:16px;\"><strong>Hi {0},</strong></p>\n" +
            "            <p style=\" list-style: 160%; margin: 10px 0;\">{1}</p>\n" +
            "\t\t\t<p style=\" list-style: 160%; margin: 10px 0;\">{2}</p>\n" +
            "        </div>\n" +
            "        <div class=\"time\" style=\"text-align: right; color: #999; padding: 0 15px 15px;\">{3}</div>\n" +
            "        <br>\n" +
            "    </div>\n" +
            "</div>\n" +
            "</body>";

    String HTML_BASE_TEMPLATE = "<body style=\"color: #666; font-size: 14px; font-family: 'Open Sans',Helvetica,Arial,sans-serif;\">\n" +
            "<div class=\"box-content\" style=\"width: 80%; margin: 20px auto; max-width: 800px; min-width: 600px;\">\n" +
            "    <div class=\"header-tip\" style=\"font-size: 12px;\n" +
            "                                   color: #aaa;\n" +
            "                                   text-align: right;\n" +
            "                                   padding-right: 25px;\n" +
            "                                   padding-bottom: 10px;\">\n" +
            "    </div>\n" +
            "    <div class=\"info-top\" style=\"padding: 15px 25px;\n" +
            "                                 border-top-left-radius: 10px;\n" +
            "                                 border-top-right-radius: 10px;\n" +
            "                                 background: {0};\n" +
            "                                 color: #fff;\n" +
            "                                 overflow: hidden;\n" +
            "                                 line-height: 32px;\n" +
            "\t\t\t\t\t\t\t\t background:lightyellow\">\n" +
            "        <div style=\"color:#010e07\"><strong>{0}</strong></div>\n" +
            "    </div>\n" +
            "    <div class=\"info-wrap\" style=\"border-bottom-left-radius: 10px;\n" +
            "                                  border-bottom-right-radius: 10px;\n" +
            "                                  border:1px solid #ddd;\n" +
            "                                  overflow: hidden;\n" +
            "                                  padding: 15px 15px 20px;\">\n" +
            "        <div class=\"tips\" style=\"padding:15px;\">\n" +
            "            <p style=\" list-style: 160%; margin: 10px 0;font-size:16px;\"><strong>{1}</strong></p>\n" +
            "            <p style=\" list-style: 160%; margin: 10px 0;\">{2}</p>\n" +
            "\t\t\t<p style=\" list-style: 160%; margin: 10px 0;\">{3}</p>\n" +
            "        </div>\n" +
            "        <div class=\"time\" style=\"text-align: right; color: #999; padding: 0 15px 15px;\">{4}</div>\n" +
            "        <br>\n" +
            "    </div>\n" +
            "</div>\n" +
            "</body>";

    String HTML_MM_PUBLISH_JOB_EMAIL_TEMPLATE = "<table width=\"100%\">\n" +
            "    <tbody>\n" +
            "    <tr>\n" +
            "        <td align=\"center\">\n" +
            "            <a target=\"_blank\" href=\"${publishUrl}\">\n" +
            "                <img\n" +
            "                    src=\"${img}\"\n" +
            "                    style=\"max-width: 100%;\"\n" +
            "                    alt=\"Click to view\"/>\n" +
            "            </a>\n" +
            "        </td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "        <td align=\"center\">\n" +
            "            <div class=\"fr-view\" style=\"display: block;\">\n" +
            "                <img class=\"img-resize-mobile-72\" height=\"auto\"\n" +
            "                     src=\"https://uat-file.makromail.atmakro.com/makro-images/bd50227ace7d4d9aae9ee528974124d2.png_rgb\"\n" +
            "                     style=\"border:0;display:block;outline:none;text-decoration:none;height:172px;width:172px;font-size:13px;\"\n" +
            "                     width=\"auto\">\n" +
            "                <span style=\"\">This message was delivered to </span>\n" +
            "                <span>\n" +
            "\t\t\t\t\t<a href=\"${email}\" target=\"_blank\" rel=\"noopener\">\n" +
            "\t\t\t\t\t\t<span>${email}</span>\n" +
            "\t\t\t\t\t</a>.\n" +
            "\t\t\t\t</span>\n" +
            "                <br>\n" +
            "                <span style=\"\">If you prefer not to receive these messages in the future, please </span>\n" +
            "                <span>\n" +
            "\t\t\t\t\t<a href=\"${unsubscribeUrl}\" target=\"_blank\" rel=\"noopener\">\n" +
            "\t\t\t\t\t\t<span>click here to unsubscribe</span>\n" +
            "\t\t\t\t\t</a>\n" +
            "\t\t\t\t</span>\n" +
            "                <br>\n" +
            "                <br>\n" +
            "                <span style=\"\">© 2022 All Right Reserved • </span>\n" +
            "                <span>\n" +
            "\t\t\t\t\t<a href=\"https://siammakro.mail.txm4.net/cb/c/96/1160/q00e0aq/6013431/F/F/F/F\" target=\"_blank\"\n" +
            "                       rel=\"noopener\">\n" +
            "\t\t\t\t\t\t<span>Terms</span>\n" +
            "\t\t\t\t\t</a>\n" +
            "\t\t\t\t</span>\n" +
            "                <span>&nbsp;and </span>\n" +
            "                <span>\n" +
            "\t\t\t\t\t<a href=\"https://siammakro.mail.txm4.net/cb/c/96/1160/q00e0aq/6013431/F/F/F/F\" target=\"_blank\"\n" +
            "                       rel=\"noopener\">\n" +
            "\t\t\t\t\t\t<span>Privacy</span>\n" +
            "\t\t\t\t\t</a>\n" +
            "\t\t\t\t</span>\n" +
            "                <span>\n" +
            "\t\t\t\t\t<br> 3498 2nd FL., Lardprao Road, Klongchan, Bangkapi, Bangkok 10240</span>\n" +
            "            </div>\n" +
            "        </td>\n" +
            "    </tr>\n" +
            "    </tbody>\n" +
            "</table>";
}
