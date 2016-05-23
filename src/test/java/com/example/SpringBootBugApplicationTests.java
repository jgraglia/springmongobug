package com.example;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
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

    private final DynamicPropertiesEntity a = DynamicPropertiesEntity.builder().id("a").detail("akey", "hello").build();
    private final DynamicPropertiesEntity b = DynamicPropertiesEntity.builder().id("b").detail("akey", "world").build();

    @Test
    @DirtiesContext
    public void ensure_can_find_by_static_property() {
        template.save(a);
        template.save(b);
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is("b"));

        DynamicPropertiesEntity entity = template.findOne(query, DynamicPropertiesEntity.class);

        assertThat(entity).isEqualTo(b);
    }

    @Test
    @DirtiesContext
    public void ensure_can_find_by_dynamic_property_with_spring_mongo_api() {
        template.save(a);
        template.save(b);
        final Query query = createDynamicQuery();

        DynamicPropertiesEntity entity = template.findOne(query, DynamicPropertiesEntity.class);

        assertThat(entity).isEqualTo(a);
    }

    @Test
    @DirtiesContext
    public void ensure_can_find_by_dynamic_property_with_raw_mongo_api() {
        template.save(a);
        template.save(b);
        final Query query = createDynamicQuery();
        final DBCollection collection = template.getCollection(template.getCollectionName(DynamicPropertiesEntity.class));

        final DBCursor cursor = collection.find(query.getQueryObject());

        assertThat(cursor.hasNext()).isTrue();
        final DBObject item = cursor.next();
        assertThat(item.get("_id")).isEqualTo("a");
        assertThat((Map) item.get("details")).containsEntry("akey", "hello");
        assertThat(cursor.hasNext()).isFalse();
    }

    private Query createDynamicQuery() {
        Query query = new Query();
        query.addCriteria(Criteria.where("details.akey").is("hello"));
        return query;
    }

}
