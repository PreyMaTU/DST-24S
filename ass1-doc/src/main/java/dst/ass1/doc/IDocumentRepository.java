package dst.ass1.doc;

import dst.ass1.jpa.model.ILocation;

import java.util.Map;

public interface IDocumentRepository {

    void insert(ILocation location, Map<String, Object> locationProperties);

}
