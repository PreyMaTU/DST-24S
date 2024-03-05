package dst.ass1.jpa.util;

import org.hibernate.dialect.H2Dialect;

/**
 * Temporarily needed because the default h2 dialect maps boolean values to "1" or "0".
 * See: https://groups.google.com/g/h2-database/c/AKjKqvGr9j8 for more infos
 */
public class H2DialectExtended extends H2Dialect {

    @Override
    public String toBooleanValueString(boolean bool) {
        return bool ? "TRUE" : "FALSE";
    }
}
