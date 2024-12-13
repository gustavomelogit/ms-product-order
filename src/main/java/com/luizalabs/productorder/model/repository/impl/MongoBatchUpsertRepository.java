package com.luizalabs.productorder.model.repository.impl;

import com.luizalabs.productorder.model.entity.EntityId;
import com.mongodb.BasicDBObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MongoBatchUpsertRepository {

    private final MongoTemplate mongoTemplate;
    private final String idField = "_id";
    private final String classField = "_class";

    public <T extends EntityId<String>> void upsertMany(List<T> documents, Class<T> clazz) {
        var bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, clazz);

        documents.forEach(document -> {
            var query = new Query(Criteria.where(idField).is(document.getId()));

            var dbDoc = new BasicDBObject();
            mongoTemplate.getConverter().write(document, dbDoc);
            dbDoc.remove(classField);

            Update update = new Update();
            update.set(idField, dbDoc.get(idField));
            dbDoc.forEach((key, value) -> {
                if (ObjectUtils.notEqual(idField, key)) {
                    update.set(key, value);
                }
            });
            bulkOperations.upsert(query, update);
        });

        bulkOperations.execute();

    }
}
