package com.makro.mall.message.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.linecorp.bot.model.message.Message;
import com.makro.mall.common.enums.MessageSendEnum;
import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author jincheng
 */
@Data
@Document("line_send_message")
@CompoundIndexes(
        {
                @CompoundIndex(name = "idx_to_for_customerId", def = "{'to.customerId': 1}"),
                @CompoundIndex(name = "idx_to_for_lineId", def = "{'to.lineId': 1}")
        }
)
public class LineSendMessage {


    private String id;
    private String type;
    private Message message;
    private String otherMessage;
    private String body;
    @Indexed
    private Set<LineCustomer> to;
    private MessageSendEnum status;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime now = LocalDateTime.now();
    @Indexed
    private String mmPublishJobLineTaskId;

    @Data
    @Document("line_customer")
    public static class LineCustomer {
        private Long customerId;
        private String lineId;
    }

}
