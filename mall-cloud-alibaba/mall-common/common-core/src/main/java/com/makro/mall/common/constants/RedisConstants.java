package com.makro.mall.common.constants;

/**
 * @author xiaojunfeng
 */
public interface RedisConstants {

    String DELIMITER = ":";

    String LIKE = "*";

    String DEFAULT_PREFIX = "default:cache:";

    String BUSINESS_NO_PREFIX = "business_no:";

    String MM_MEMBER_TYPE_LIST = "mm:member:type:list";

    String SYS_USER_TOKEN = "sys:user:%s:token";

    String RESET_PASSWORD_TOKEN = "reset:password:token:";

    String TEXT_THAI_IMPORT_PREFIX = "text:thai:import:";

    String MM_PAGE_PREFIX = "mm-page:";

    String MM_APP_TOTAL = "mm:app:total:day:";

    String SHORT_LINK_CODE_PREFIX = "short:link:";

    String SETTING_PREFIX = "setting:1";
    String MESSAGE_PROPERTIES_PREFIX = "messageProperties:1";

    Long RESET_PASSWORD_TIME = 10800L;

}
