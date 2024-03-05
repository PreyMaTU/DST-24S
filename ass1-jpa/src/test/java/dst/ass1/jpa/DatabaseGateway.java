package dst.ass1.jpa;

import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Type;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

// DO NOT MODIFY THIS CLASS.

/**
 * Contains various methods for accessing the database underlying an EntityManager.
 * <p>
 * Note that the caller is responsible for dealing with possible exceptions as well as doing the connection handling. A
 * connection will not be closed even if a fatal error occurs. However, other SQL resources i.e.,
 * {@link Statement Statements} and {@link ResultSet ResultSets} created within the methods, which are not returned to
 * the caller, are closed before the method returns.
 */
public class DatabaseGateway {

    private final EntityManager em;

    public DatabaseGateway(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns a list of all table-names for the given database/connection.
     *
     * @return List of table names
     */
    public List<String> getTables() {
        return getSession().doReturningWork(new CollectionWork<>("show tables", rs -> rs.getString(1)));
    }

    /**
     * Returns a list of all column names in the given table.
     *
     * @param tableName the table
     * @return a list of column names
     */
    public List<String> getColumns(String tableName) {
        return getColumnsDefinitions(tableName).stream().map(m -> m.get("COLUMN_NAME")).collect(Collectors.toList());
    }

    public List<Map<String, String>> getColumnsDefinitions(String tableName) {
        String sql = String.format("SELECT * FROM information_schema.columns "
            + "WHERE table_name='%s'", tableName.toUpperCase());

        return getSession().doReturningWork(new QueryWork<List<Map<String, String>>>(sql) {
            @Override
            protected List<Map<String, String>> execute(ResultSet rs) throws SQLException {
                List<Map<String, String>> list = new ArrayList<>();
                while (rs.next()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    Map<String, String> map = new HashMap<>();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        String key = meta.getColumnName(i);
                        String value = rs.getString(key);
                        map.put(key, value);
                    }
                    list.add(map);
                }
                return list;
            }
        });
    }

    /**
     * Returns the java types of all managed entity types.
     *
     * @return a list of java types
     */
    public List<Class<?>> getManagedJavaTypes() {
        return em.getMetamodel()
            .getManagedTypes().stream()
            .map(Type::getJavaType)
            .collect(Collectors.toList());
    }

    /**
     * Checks if the named table can be accessed via the given EntityManager.
     *
     * @param tableName the name of the table to find
     * @return {@code true} if the database schema contains a table with the given name, {@code false} otherwise
     */
    public boolean isTable(final String tableName) {
        return getSession().doReturningWork(new QueryWork<Boolean>("show tables") {
            @Override
            public Boolean execute(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    String tbl = rs.getString(1);
                    if (tbl.equalsIgnoreCase(tableName)) {
                        return true;
                    }
                }
                return false;
            }

        });
    }

    /**
     * Checks whether a certain database table contains a column with the given
     * name.
     *
     * @param tableName the name of the table to check
     * @param column    the name of the column to find
     * @return {@code true} if the table contains the column, {@code false} otherwise
     */
    public boolean isColumnInTable(String tableName, String column) {
        String sql = String.format(
            "SELECT * FROM information_schema.columns WHERE table_name='%s' and column_name='%s'",
            tableName.toUpperCase(), column.toUpperCase()
        );

        return getSession().doReturningWork(new HasAtLeastOneEntry(sql));
    }

    /**
     * Checks whether a table contains a column of the given type and length.
     *
     * @param tableName the table to look for
     * @param column    the expected column name
     * @param type      the expected column type
     * @param length    the expected column length
     * @return true if the information schema has at least one such column
     */
    public boolean isColumnInTableWithType(String tableName, String column, String type, String length) {
        String sql = String.format("SELECT * FROM information_schema.columns "
                + "WHERE table_name='%s' and column_name='%s' and "
                + "data_type='%s' and character_maximum_length='%s'",
            tableName.toUpperCase(), column.toUpperCase(), type.toUpperCase(), length);

        return getSession().doReturningWork(new HasAtLeastOneEntry(sql));
    }

    /**
     * Checks whether a certain table contains an index for the given column
     * name.
     *
     * @param tableName the name of the table to check
     * @param indexName the name of the column the index is created for
     * @param nonUnique {@code true} if the index is non unique, {@code false} otherwise
     * @return {@code true} if the index exists, {@code false} otherwise
     */
    public boolean isIndex(String tableName, String indexName, boolean nonUnique) {

        String sql = String.format(
            "SELECT * FROM information_schema.index_columns WHERE table_name='%s' and column_name='%s' and is_unique=%s",
            tableName.toUpperCase(), indexName.toUpperCase(), nonUnique ? "false" : "true"
        );

        return getSession().doReturningWork(new HasAtLeastOneEntry(sql));
    }

    public boolean isComposedIndex(String tableName, String columnName1, String columnName2) {
        String indexName1 = getIndexName(tableName, columnName1);
        String indexName2 = getIndexName(tableName, columnName2);

        return Objects.nonNull(indexName1) && Objects.equals(indexName1, indexName2);
    }

    private String getIndexName(String tableName, String columnName) {
        String sql = String.format(
            "SELECT index_name FROM information_schema.index_columns WHERE table_name='%s' and column_name='%s'",
            tableName.toUpperCase(), columnName.toUpperCase()
        );

        return getSession().doReturningWork(new QueryWork<String>(sql) {
            @Override
            protected String execute(ResultSet rs) throws SQLException {
                return (rs.next()) ? rs.getString(1) : null;
            }
        });
    }

    /**
     * Checks whether the given column of a certain table can contain {@code NULL} values.
     *
     * @param tableName  the name of the table to check
     * @param columnName the name of the column to check
     * @return {@code true} if the column is nullable, {@code false} otherwise
     */
    public boolean isNullable(String tableName, String columnName) {
        String sql = String.format(
            "SELECT * FROM information_schema.columns " +
                "WHERE table_name='%s' and column_name='%s' and IS_NULLABLE='YES'",
            tableName.toUpperCase(), columnName.toUpperCase()
        );

        return getSession().doReturningWork(new HasAtLeastOneEntry(sql));
    }

    /**
     * Deletes all data from all tables that can be accessed via the given EntityManager.
     */
    public void truncateTables() {
        List<String> tables = getTables();
        tables.removeIf(t -> t.toLowerCase().startsWith("hibernate"));

        getSession().doWork(connection -> {
            try (Statement stmt = connection.createStatement()) {
                stmt.addBatch("SET REFERENTIAL_INTEGRITY FALSE");
                for (String table : tables) {
                    stmt.addBatch("TRUNCATE TABLE " + table);
                }
                stmt.addBatch("SET REFERENTIAL_INTEGRITY TRUE");
                stmt.executeBatch();
            }
        });
    }

    public Session getSession() {
        return em.unwrap(Session.class);
    }

    public interface StatementWork<T> extends ReturningWork<T> {

        default T execute(Connection connection) throws SQLException {
            try (Statement stmt = connection.createStatement()) {
                return execute(stmt);
            }
        }

        T execute(Statement stmt) throws SQLException;
    }

    @FunctionalInterface
    public interface CheckedFunction<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    public static abstract class QueryWork<T> implements StatementWork<T> {
        private final String sql;

        public QueryWork(String sql) {
            this.sql = sql;
        }

        @Override
        public T execute(Statement stmt) throws SQLException {
            try (ResultSet rs = stmt.executeQuery(sql)) {
                return execute(rs);
            }
        }

        protected abstract T execute(ResultSet rs) throws SQLException;
    }

    public static class HasAtLeastOneEntry extends QueryWork<Boolean> {

        public HasAtLeastOneEntry(String sql) {
            super(sql);
        }

        @Override
        protected Boolean execute(ResultSet rs) throws SQLException {
            return rs.next();
        }
    }

    public static class CollectionWork<T> extends QueryWork<List<T>> {

        private final CheckedFunction<ResultSet, T, SQLException> extractor;

        public CollectionWork(String sql, CheckedFunction<ResultSet, T, SQLException> extractor) {
            super(sql);
            this.extractor = extractor;
        }

        @Override
        protected List<T> execute(ResultSet rs) throws SQLException {
            List<T> list = new ArrayList<>();
            while (rs.next()) {
                list.add(extractor.apply(rs));
            }
            return list;
        }
    }
}
