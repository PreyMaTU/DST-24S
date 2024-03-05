package dst.ass1.doc;

import com.mongodb.client.MongoDatabase;
import dst.ass1.jpa.util.Constants;
import org.bson.Document;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class DocumentTestData implements IDocumentTestData {

    private String documentResource;

    public DocumentTestData() {
        this("documents.json");
    }

    public DocumentTestData(String documentResource) {
        this.documentResource = documentResource;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void insertTestData(MongoDatabase db) throws IOException {
        URL resource = Objects.requireNonNull(getDocumentsResource());

        String testDataJson = readFully(resource);
        List<Document> documents = Document.parse(testDataJson).get("documents", List.class);
        db.getCollection(Constants.COLL_LOCATION_DATA).insertMany(documents);
    }

    private URL getDocumentsResource() {
        return getClass().getClassLoader().getResource(documentResource);
    }

    private String readFully(URL resource) throws IOException {
        try (Scanner scanner = new Scanner(resource.openStream())) {
            return scanner.useDelimiter("\\Z").next();
        }
    }

}
