package dst.ass1.doc;

import org.bson.Document;

import java.util.List;

public interface IDocumentQuery {

    /**
     * Retrieves the average opening hours per category.
     *
     * @return a {@code List} of {@code Document} objects representing the average opening hours per category.
     */
    List<Document> getAverageOpeningHoursPerCategory();

    /**
     * Retrieves a list of documents with the given name that are located within the specified polygon.
     *
     * @param name    the name of the documents to search for
     * @param polygon a list of lists of Doubles representing the vertices of the polygon
     * @return a List of Document objects that match the specified criteria
     */
    List<Document> findDocumentsByNameWithinPolygon(String name, List<List<Double>> polygon);

    /**
     * Retrieves a list of documents with the specified type.
     *
     * @param type the type of the documents to search for
     * @return a List of Document objects that match the specified type
     */
    List<Document> findDocumentsByType(String type);
}
