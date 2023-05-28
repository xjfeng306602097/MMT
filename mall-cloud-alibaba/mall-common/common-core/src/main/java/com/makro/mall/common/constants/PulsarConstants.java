package com.makro.mall.common.constants;

/**
 * @author xiaojunfeng
 * @description Pulsar常量
 * @date 2021/11/10
 */
public interface PulsarConstants {

    String TOPIC_MAIL_MESSAGE_H5 = "mail_message_H5";

    String TOPIC_MAIL_MESSAGE_TEXT = "mail_message_TEXT";

    String TOPIC_STAT_CLICK = "mail_stat_click";

    String TOPIC_STAT_GOODS_CLICK = "mail_stat_goods_click";

    String TOPIC_STAT_PAGE_VIEW = "mail_stat_page_view";

    String TOPIC_STAT_PAGE_STAY = "mail_stat_page_stay";
    String TOPIC_STAT_APP_UV = "mail_stat_app_uv";

    String TOPIC_STAT_VIEW = "stat_view";

    String TOPIC_PUBLISH_JOB_EMAIL_TASK = "publish_job_email_task";

    String TOPIC_PUBLISH_JOB_LINE_TASK = "publish_job_line_task";

    String BINDING_LINE_USER_ID = "binding_line_user_id";

    String TOPIC_MAIL_STAT_GOODS_CLICK_SINK = "mail_stat_goods_click_sink";

    String TOPIC_MAIL_STAT_PAGE_VIEW_SINK = "mail_stat_page_view_sink";

    String TOPIC_MAIL_STAT_PAGE_STAY_SINK = "mail_stat_page_stay_sink";
    String TOPIC_MAIL_STAT_APP_UV_SINK = "mail_stat_app_uv_sink";

    String TOPIC_SMS_GGG = "sms_ggg";

    String TOPIC_USER_ACTION = "user_action";

    String TOPIC_PUBLISH_JOB_SMS_TASK = "publish_job_sms_task";

    String TOPIC_LINE_MULTICAST_FLEX = "line_multicast_flexV4";
    String TOPIC_INVALID_SEGMENT = "invalid_segment";
    String TOPIC_SYSTEM_USER_LOG = "system_user_log";
    String TOPIC_SYSTEM_USER_LOG_JSON = "system_user_log_json";
    String TOPIC_MM_SAVE_ROLL_BACK = "mm_save_roll_back";
}
