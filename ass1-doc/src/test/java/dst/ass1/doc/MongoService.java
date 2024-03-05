package dst.ass1.doc;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dst.ass1.doc.impl.DocumentServiceFactory;
import dst.ass1.jpa.util.Constants;
import org.bson.Document;
import org.junit.rules.ExternalResource;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

// DO NOT MODIFY THIS CLASS.

/**
 * The MongoService class is used as a JUnit rule, that fulfills the same tasks as the ORMService in ass1-jpa, and works
 * very similarly.
 */
public class MongoService extends ExternalResource {

    private IDocumentTestData testData;
    private boolean insertTestData;
    private boolean clearTestData;

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private IDocumentServiceFactory documentServiceFactory;
    private IDocumentRepository documentRepository;
    private IDocumentQuery documentQuery;

    public MongoService() {
        this(null, false, true);
    }

    public MongoService(IDocumentTestData testData) {
        this(testData, true, true);
    }

    public MongoService(IDocumentTestData testData, boolean insertTestData, boolean clearTestData) {
        this.testData = testData;
        this.insertTestData = insertTestData;
        this.clearTestData = clearTestData;
    }

    public static Stream<Document> stream(MongoCollection<Document> collection) {
        return StreamSupport.stream(collection.find().spliterator(), false);
    }

    public static <T> Map<T, Document> idMap(MongoCollection<Document> collection, Function<Document, T> idFunction) {
        return stream(collection).collect(Collectors.toMap(idFunction, Function.identity()));
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public IDocumentServiceFactory getDocumentServiceFactory() {
        return documentServiceFactory;
    }

    public IDocumentRepository getDocumentRepository() {
        return documentRepository;
    }

    public IDocumentQuery getDocumentQuery() {
        return documentQuery;
    }

    @Override
    protected void before() throws Throwable {
        setUpMongo();

        if (insertTestData && testData != null) {
            insertData(testData);
        }
    }

    @Override
    protected void after() {
        try {
            if (clearTestData) {
                clearData();
            }
        } finally {
            tearDownMongo();
        }
    }

    private void setUpMongo() {
        mongoClient = new MongoClient("127.0.0.1");
        mongoDatabase = mongoClient.getDatabase(Constants.MONGO_DB_NAME);

        documentServiceFactory = new DocumentServiceFactory();
        documentRepository = documentServiceFactory.createDocumentRepository();

        if (documentRepository == null) {
            throw new IllegalStateException("DocumentRepository returned from factory is null");
        }

        documentQuery = documentServiceFactory.createDocumentQuery(mongoDatabase);
    }

    private void tearDownMongo() {
        mongoClient.close();
    }

    private void insertData(IDocumentTestData testData) throws Exception {
        testData.insertTestData(getMongoDatabase());
    }

    private void clearData() {
        getMongoDatabase().drop();
    }

}
