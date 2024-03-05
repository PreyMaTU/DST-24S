package dst.ass1.doc;

import com.mongodb.client.MongoDatabase;

public interface IDocumentServiceFactory {

    IDocumentQuery createDocumentQuery(MongoDatabase db);

    IDocumentRepository createDocumentRepository();

}
