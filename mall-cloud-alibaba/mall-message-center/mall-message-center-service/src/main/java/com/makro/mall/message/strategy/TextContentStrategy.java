package com.makro.mall.message.strategy;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.action.*;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.imagemap.*;
import com.linecorp.bot.model.message.sender.Sender;
import com.linecorp.bot.model.message.template.*;
import com.makro.mall.message.pojo.supplier.ExampleFlexMessageSupplier;
import com.makro.mall.message.pojo.supplier.MessageWithQuickReplySupplier;
import com.makro.mall.message.sdk.line.LineSDK;
import com.makro.mall.message.service.LineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

import static java.util.Collections.singletonList;

/**
 * @author jincheng
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TextContentStrategy implements LineEvent<MessageEvent<TextMessageContent>> {
    private final LineService lineService;
    private final LineSDK lineSDK;


    @Override
    public LineEvent<MessageEvent<TextMessageContent>> handle(MessageEvent<TextMessageContent> event) {

        String replyToken = event.getReplyToken();
        TextMessageContent content = event.getMessage();
        final String text = content.getText();

        log.info("Got text message from replyToken:{}: text:{} emojis:{}", replyToken, text,
                content.getEmojis());
        switch (text) {
            case "userid":
                lineSDK.reply(replyToken, event.getSource().getUserId());
                break;
            case "confirm": {
                ConfirmTemplate confirmTemplate = new ConfirmTemplate(
                        "吃饭了吗?",
                        new MessageAction("吃了", "吃了!"),
                        new MessageAction("没吃", "没吃!")
                );
                TemplateMessage templateMessage = new TemplateMessage("Confirm alt text", confirmTemplate);
                lineSDK.reply(replyToken, templateMessage);
                break;
            }
            case "buttons": {
                URI imageUrl = lineService.createUri("https://cdn.shopify.com/s/files/1/0574/6376/7235/products/35f562b57bfc464caaafe7c5b7da9a28.jpg?v=1675430709");
                ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
                        imageUrl,
                        "button title",
                        "button text",
                        Arrays.asList(
                                new URIAction("去line",
                                        URI.create("https://line.me"), null),
                                new URIAction("去后台",
                                        URI.create("https://dev-mail.makrogo.com"), null)
                        ));
                TemplateMessage templateMessage = new TemplateMessage("Button alt text", buttonsTemplate);
                lineSDK.reply(replyToken, templateMessage);
                break;
            }
            case "carousel": {
                URI imageUrl = lineService.createUri("https://cdn.shopify.com/s/files/1/0574/6376/7235/products/35f562b57bfc464caaafe7c5b7da9a28.jpg?v=1675430709");
                CarouselTemplate carouselTemplate = new CarouselTemplate(
                        Arrays.asList(
                                new CarouselColumn(imageUrl, "title", "text", Arrays.asList(
                                        CameraAction.withLabel("CameraAction"),
                                        CameraRollAction.withLabel("CameraRollAction"),
                                        LocationAction.withLabel("LocationAction")
                                )),
                                new CarouselColumn(imageUrl, "Datetime Picker",
                                        "Please select a date, time or datetime", Arrays.asList(
                                        DatetimePickerAction.OfLocalDatetime
                                                .builder()
                                                .label("Datetime")
                                                .data("action=sel")
                                                .initial(LocalDateTime.parse("2017-06-18T06:15"))
                                                .min(LocalDateTime.parse("1900-01-01T00:00"))
                                                .max(LocalDateTime.parse("2100-12-31T23:59"))
                                                .build(),
                                        DatetimePickerAction.OfLocalDate
                                                .builder()
                                                .label("Date")
                                                .data("action=sel&only=date")
                                                .initial(LocalDate.parse("2017-06-18"))
                                                .min(LocalDate.parse("1900-01-01"))
                                                .max(LocalDate.parse("2100-12-31"))
                                                .build(),
                                        DatetimePickerAction.OfLocalTime
                                                .builder()
                                                .label("Time")
                                                .data("action=sel&only=time")
                                                .initial(LocalTime.parse("06:15"))
                                                .min(LocalTime.parse("00:00"))
                                                .max(LocalTime.parse("23:59"))
                                                .build()
                                ))
                        ));
                TemplateMessage templateMessage = new TemplateMessage("Carousel alt text", carouselTemplate);
                lineSDK.reply(replyToken, templateMessage);
                break;
            }
            case "image_carousel": {
                URI imageUrl = lineService.createUri("https://cdn.shopify.com/s/files/1/0574/6376/7235/products/35f562b57bfc464caaafe7c5b7da9a28.jpg?v=1675430709");
                ImageCarouselTemplate imageCarouselTemplate = new ImageCarouselTemplate(
                        Arrays.asList(
                                new ImageCarouselColumn(imageUrl,
                                        new URIAction("Goto line.me",
                                                URI.create("https://line.me"), null)
                                ),
                                new ImageCarouselColumn(imageUrl,
                                        new MessageAction("Say message",
                                                "Rice=米")
                                ),
                                new ImageCarouselColumn(imageUrl,
                                        new PostbackAction("言 hello2",
                                                "hello こんにちは",
                                                "hello こんにちは")
                                )
                        ));
                TemplateMessage templateMessage = new TemplateMessage("ImageCarousel alt text",
                        imageCarouselTemplate);
                lineSDK.reply(replyToken, templateMessage);
                break;
            }
            case "imagemap":
                lineSDK.reply(replyToken, ImagemapMessage
                        .builder()
                        .baseUrl(lineService.createUri("https://cdn.shopify.com/s/files/1/0574/6376/7235/products/35f562b57bfc464caaafe7c5b7da9a28.jpg?v=1675430709"))
                        .altText("This is alt text")
                        .baseSize(new ImagemapBaseSize(1040, 1040))
                        .actions(Arrays.asList(
                                URIImagemapAction.builder()
                                        .linkUri("https://store.line.me/family/manga/en")
                                        .area(new ImagemapArea(0, 0, 520, 520))
                                        .build(),
                                URIImagemapAction.builder()
                                        .linkUri("https://store.line.me/family/music/en")
                                        .area(new ImagemapArea(520, 0, 520, 520))
                                        .build(),
                                URIImagemapAction.builder()
                                        .linkUri("https://store.line.me/family/play/en")
                                        .area(new ImagemapArea(0, 520, 520, 520))
                                        .build(),
                                MessageImagemapAction.builder()
                                        .text("URANAI!")
                                        .area(new ImagemapArea(520, 520, 520, 520))
                                        .build()
                        ))
                        .build());
                break;
            case "imagemap_video":
                lineSDK.reply(replyToken, ImagemapMessage
                        .builder()
                        .baseUrl(lineService.createUri("/static/imagemap_video"))
                        .altText("This is an imagemap with video")
                        .baseSize(new ImagemapBaseSize(722, 1040))
                        .video(
                                ImagemapVideo.builder()
                                        .originalContentUrl(
                                                lineService.createUri("/static/imagemap_video/originalContent.mp4"))
                                        .previewImageUrl(
                                                lineService.createUri("/static/imagemap_video/previewImage.jpg"))
                                        .area(new ImagemapArea(40, 46, 952, 536))
                                        .externalLink(
                                                new ImagemapExternalLink(
                                                        URI.create("https://example.com/see_more.html"),
                                                        "See More")
                                        )
                                        .build()
                        )
                        .actions(singletonList(
                                MessageImagemapAction.builder()
                                        .text("NIXIE CLOCK")
                                        .area(new ImagemapArea(260, 600, 450, 86))
                                        .build()
                        ))
                        .build());
                break;
            case "flex":
                lineSDK.reply(replyToken, new ExampleFlexMessageSupplier().get());
                break;
            case "quickreply":
                lineSDK.reply(replyToken, new MessageWithQuickReplySupplier().get());
                break;
            case "no_notify":
                lineSDK.reply(replyToken,
                        singletonList(new TextMessage("This message is send without a push notification")),
                        true);
                break;
            case "redelivery":
                lineSDK.reply(replyToken,
                        singletonList(new TextMessage("webhookEventId=" + event.getWebhookEventId()
                                + " deliveryContext.isRedelivery=" + event.getDeliveryContext().getIsRedelivery())
                        ));
                break;
            case "icon":
                lineSDK.reply(replyToken,
                        TextMessage.builder()
                                .text("Hello, I'm cat! Meow~")
                                .sender(Sender.builder()
                                        .name("Cat")
                                        .iconUrl(lineService.createUri("/static/icon/cat.png"))
                                        .build())
                                .build());
                break;
            default:
                log.info("Returns echo message {}: {}", replyToken, text);
                lineSDK.reply(
                        replyToken,
                        text
                );
                break;
        }
        return this;
    }


}
