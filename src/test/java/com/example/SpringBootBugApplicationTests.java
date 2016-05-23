package com.example;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringBootBugApplication.class)
@IntegrationTest
@Slf4j
public class SpringBootBugApplicationTests {
    @Autowired
    private MongoTemplate template;

    private final DynamicPropertiesEntity hello = DynamicPropertiesEntity.builder().id("hello")
            .detail("akey", "hello")
            .genericDetail("akey", "hello")
            .build();
    private final DynamicPropertiesEntity world = DynamicPropertiesEntity.builder().id("world")
            .detail("akey", "world")
            .genericDetail("akey", "world")
            .build();

    @Test
    @DirtiesContext
    public void ensure_can_find_by_static_property() {
        template.save(hello);
        template.save(world);
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is("world"));

        DynamicPropertiesEntity entity = template.findOne(query, DynamicPropertiesEntity.class);

        assertThat(entity).isEqualTo(world);
    }

    @Test
    @DirtiesContext
    public void ensure_can_find_by_dynamic_property_with_spring_mongo_api() {
        template.save(hello);
        template.save(world);
        final Query query = createDynamicHelloQuery();

        DynamicPropertiesEntity entity = template.findOne(query, DynamicPropertiesEntity.class);

        assertThat(entity).isEqualTo(hello);
    }

    @Test
    @DirtiesContext
    public void ensure_can_find_by_dynamic_generic_property_with_spring_mongo_api() {
        template.save(hello);
        template.save(world);
        final Query query = createDynamicGenericHelloQuery();

        DynamicPropertiesEntity entity = template.findOne(query, DynamicPropertiesEntity.class);

        assertThat(entity).isEqualTo(hello);
    }

    @Test
    @DirtiesContext
    public void ensure_can_find_by_dynamic_property_with_raw_mongo_api() {
        template.save(hello);
        template.save(world);
        final Query query = createDynamicHelloQuery();
        final DBCollection collection = template.getCollection(template.getCollectionName(DynamicPropertiesEntity.class));

        final DBCursor cursor = collection.find(query.getQueryObject());

        assertThat(cursor.hasNext()).isTrue();
        final DBObject item = cursor.next();
        assertThat(item.get("_id")).isEqualTo("hello");
        assertThat((Map) item.get("details")).containsEntry("akey", "hello");
        assertThat(cursor.hasNext()).isFalse();
    }

    private Query createDynamicGenericHelloQuery() {
        Query query = new Query();
        query.addCriteria(Criteria.where("genericDetails.akey").is("hello"));
        return query;
    }
    private Query createDynamicHelloQuery() {
        Query query = new Query();
        query.addCriteria(Criteria.where("details.akey").is("hello"));
        return query;
    }

}
