package dst.ass1.doc.impl;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import dst.ass1.doc.IDocumentQuery;
import dst.ass1.jpa.util.Constants;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class DocumentQuery implements IDocumentQuery {
    private final MongoDatabase mongoDatabase;

    DocumentQuery(MongoDatabase mongoDatabase) {
        this.mongoDatabase= mongoDatabase;
    }

    private void printResults( List<Document> documents ) {
        int i= 0;
        for( final var doc: documents ) {
            System.out.printf("Result (%d): %s\n", i++, doc.toJson());
        }
    }

    @Override
    public List<Document> getAverageOpeningHoursPerCategory() {
        final var results= mongoDatabase
                .getCollection( Constants.COLL_LOCATION_DATA )
                .aggregate( Arrays.asList(
                        Aggregates.match( Filters.eq("type", "place") ),
                        Aggregates.project(
                                Projections.fields(
                                        Projections.include("category"),
                                        Projections.computed( "hours",
                                                new Document("$subtract", Arrays.asList("$closingHour", "$openHour"))
                                        ),
                                        Projections.excludeId()
                                )
                        ),
                        Aggregates.group("$category", Accumulators.avg("value", "$hours"))
                )).into(new ArrayList<>());

        printResults( results );
        return results;
    }

    @Override
    public List<Document> findDocumentsByNameWithinPolygon(String name, List<List<Double>> polygon) {
        final var results= mongoDatabase
                .getCollection( Constants.COLL_LOCATION_DATA )
                .find(
                    Filters.and(
                            Filters.regex("name", Pattern.quote(name)),
                            Filters.geoWithinPolygon("geo", polygon)
                    )
                ).projection(
                        Projections.fields(
                                Projections.include("location_id"),
                                Projections.excludeId()
                        )
                ).into(new ArrayList<>());

        printResults( results );
        return results;
    }

    @Override
    public List<Document> findDocumentsByType(String type) {
        final var results= mongoDatabase
                .getCollection( Constants.COLL_LOCATION_DATA )
                .find(Filters.eq("type", type))
                .into(new ArrayList<>());

        printResults( results );
        return results;
    }
}
