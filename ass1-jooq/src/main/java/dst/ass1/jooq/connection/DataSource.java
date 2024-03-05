package dst.ass1.jooq.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

import java.sql.SQLException;

public class DataSource {
    private DataSource() {
    }

    private static final DSLContext dslContext;

    static {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:h2:/tmp/database/dst");
        var hikariDataSource = new HikariDataSource(hikariConfig);
        var dslConfig = new DefaultConfiguration();
        var setting = new Settings().withExecuteWithOptimisticLocking(true);
        dslConfig.set(hikariDataSource);
        dslConfig.set(SQLDialect.H2);
        dslConfig.set(setting);

        dslContext = DSL.using(dslConfig);

    }


    /**
     * This will return a dsl context which you can use to execute sql statements
     * via jooq
     *
     * @return a dsl context
     */
    public static DSLContext getConnection() throws SQLException {
        return dslContext;
    }
}
