package com.groupbyinc.datahub.consumer.producer;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.transforms.DoFn;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class WriteToMongo extends DoFn<Document, Void> {

    private final String mongoUri;
    private final String databaseName;
    private transient MongoClient mongoClient;
    private transient MongoDatabase database;
    private transient Map<String, MongoCollection<Document>> collections;

    public WriteToMongo(String mongoUri, String databaseName) {
        this.mongoUri = mongoUri;
        this.databaseName = databaseName;
    }

    @Setup
    public void setup() {
        this.mongoClient = MongoClients.create(mongoUri);
        this.database = mongoClient.getDatabase(databaseName);
        this.collections = new HashMap<>();
    }

    @ProcessElement
    public void processElement(ProcessContext context) {
        var element = context.element();
        var collectionName = element.keySet().iterator().next();
        var mongoCollection = getCollection(collectionName);
        var document = (Document) element.get(collectionName);
        mongoCollection.insertOne(document);
    }

    @Teardown
    public void teardown() {
        collections.clear();
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    private MongoCollection<Document> getCollection(String collection) {
        if (collections.containsKey(collection)) {
            return collections.get(collection);
        }
        var mongoCollection = database.getCollection(collection);
        collections.put(collection, mongoCollection);
        return mongoCollection;
    }
}
