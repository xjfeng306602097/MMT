package com.makro.mall.stat;

import com.makro.mall.stat.pojo.entity.GoodClickRecord;
import com.makro.mall.stat.pojo.entity.PageViewRecord;
import org.apache.pulsar.client.impl.schema.AvroSchema;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/6/29
 */
public class AvroSchemaGenerateTest {

    public static void main(String[] args) {
        AvroSchema<GoodClickRecord> schema = AvroSchema.of(GoodClickRecord.class);
        System.out.println(schema.getAvroSchema().toString());
        AvroSchema<PageViewRecord> pageViewSchema = AvroSchema.of(PageViewRecord.class);
        System.out.println(pageViewSchema.getAvroSchema().toString());
    }
}
