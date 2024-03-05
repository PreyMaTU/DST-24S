package dst.ass1.doc;

import com.mongodb.client.MongoDatabase;

// DO NOT MODIFY THIS CLASS.

/**
 * The IDocumentTestData interface works like the ITestData as introduced in ass1-jpa only for the {@link MongoService}.
 */
public interface IDocumentTestData {
    /**
     * Inserts the data into the given MongoDatabase instance.
     *
     * @param db the mongo database instance
     * @throws Exception if the insertion failed for some reason
     */
    void insertTestData(MongoDatabase db) throws Exception;
}
