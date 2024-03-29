package dst.ass1.doc.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import dst.ass1.doc.IDocumentRepository;
import dst.ass1.jpa.model.ILocation;
import dst.ass1.jpa.util.Constants;
import org.bson.Document;

import java.util.Map;

public class DocumentRepository implements IDocumentRepository {
    private final MongoDatabase mongoDatabase;

    DocumentRepository() {
        final var mongoClient= new MongoClient("127.0.0.1");
        this.mongoDatabase= mongoClient.getDatabase( Constants.MONGO_DB_NAME );
    }

    @Override
    public void insert(ILocation location, Map<String, Object> locationProperties) {
        final var document= new Document( locationProperties );
        document.put("location_id", location.getLocationId());
        document.put("name", location.getName());

        final var collection= mongoDatabase.getCollection( Constants.COLL_LOCATION_DATA );
        collection.insertOne( document );
        collection.createIndex(Indexes.ascending("location_id"));
        collection.createIndex(Indexes.geo2dsphere("geo"));
    }
}
