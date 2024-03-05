package dst.ass1.jpa;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Matcher that finds items in string collections in a case-insensitive way.
 */
public class CaseInsensitiveStringCollectionMatcher extends TypeSafeDiagnosingMatcher<List<String>> {

    private final String[] items;

    public CaseInsensitiveStringCollectionMatcher(String... items) {
        this.items = items;
    }

    @Factory
    public static CaseInsensitiveStringCollectionMatcher hasItems(String... items) {
        return new CaseInsensitiveStringCollectionMatcher(items);
    }

    @Override
    protected boolean matchesSafely(List<String> collection, Description description) {
        List<String> missing = new ArrayList<>();

        for (String item : items) {
            if (collection.stream().noneMatch(i -> i.equalsIgnoreCase(item))) {
                missing.add(item);
            }
        }

        if (!missing.isEmpty()) {
            if (missing.size() == items.length) {
                description.appendValueList("was [", ", ", "]", collection);
            } else {
                description.appendValueList("missing [", ", ", "]", missing)
                    .appendValueList(" in [", ", ", "]", collection);
            }
        }

        return missing.isEmpty();
    }

    @Override
    public void describeTo(Description description) {
        description.appendValueList("collection containing ", " and ", "", items);
    }
}
