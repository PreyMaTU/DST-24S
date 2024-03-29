package dst.ass1.jooq.dao.impl;

import dst.ass1.jooq.connection.DataSource;
import dst.ass1.jooq.dao.IRiderPreferenceDAO;
import dst.ass1.jooq.model.IRiderPreference;
import dst.ass1.jooq.model.impl.RiderPreference;
import org.jooq.DSLContext;

import static dst.ass1.jooq.model.public_.Tables.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RiderPreferenceDAO implements IRiderPreferenceDAO {
    private DSLContext tryGetConnection() {
        try {
            return DataSource.getConnection();
        } catch ( SQLException e ) {
            return null;
        }
    }
    @Override
    public IRiderPreference findById(Long id) {
        final var connection= tryGetConnection();
        if( connection == null ) {
            return null;
        }

        final var riderPreferenceRecord= connection
                .select()
                .from(RIDER_PREFERENCE)
                .where(RIDER_PREFERENCE.RIDER_ID.eq(id))
                .fetchOne();

        if( riderPreferenceRecord == null ) {
            return null;
        }

        final var preferenceRecords= connection
                .select()
                .from( PREFERENCE )
                .where( PREFERENCE.RIDER_ID.eq(id) )
                .fetch();

        final var preferences= new HashMap<String, String>( preferenceRecords.size() );
        for( final var record : preferenceRecords ) {
            preferences.put( record.get(PREFERENCE.PREF_KEY), record.get(PREFERENCE.PREF_VALUE) );
        }

        RiderPreference riderPreference= new RiderPreference();
        riderPreference.setRiderId( id );
        riderPreference.setArea( riderPreferenceRecord.get(RIDER_PREFERENCE.AREA) );
        riderPreference.setVehicleClass( riderPreferenceRecord.get(RIDER_PREFERENCE.VEHICLE_CLASS) );
        riderPreference.setPreferences( preferences );
        return riderPreference;
    }

    @Override
    public List<IRiderPreference> findAll() {
        final var connection= tryGetConnection();
        if( connection == null ) {
            return null;
        }

        final var joinedRecords= connection
                .select()
                .from(
                        RIDER_PREFERENCE
                                .leftJoin(PREFERENCE)
                                .on(RIDER_PREFERENCE.RIDER_ID.eq(PREFERENCE.RIDER_ID))
                ).orderBy(RIDER_PREFERENCE.RIDER_ID.asc())
                .fetch();

        final var preferences= new ArrayList<IRiderPreference>();
        HashMap<String,String> collectedPreferences= null;

        for(final var record : joinedRecords) {
            final var id= record.get(PREFERENCE.RIDER_ID);
            if( preferences.isEmpty() || !preferences.get(preferences.size() - 1).getRiderId().equals(id) ) {
                if( !preferences.isEmpty() ) {
                    final var current= preferences.get(preferences.size() - 1);
                    current.setPreferences( collectedPreferences );
                }

                final var riderPreference= new RiderPreference();
                riderPreference.setRiderId( id );
                riderPreference.setArea( record.get(RIDER_PREFERENCE.AREA) );
                riderPreference.setVehicleClass( record.get(RIDER_PREFERENCE.VEHICLE_CLASS) );
                preferences.add( riderPreference );

                collectedPreferences= new HashMap<>();
            }

            collectedPreferences.put( record.get(PREFERENCE.PREF_KEY), record.get(PREFERENCE.PREF_VALUE) );
        }

        // Add the preferences to the last item
        if( !preferences.isEmpty() ) {
            final var current= preferences.get(preferences.size() - 1);
            current.setPreferences( collectedPreferences );
        }

        return preferences;
    }

    @Override
    public IRiderPreference insert(IRiderPreference model) {
        final var connection= tryGetConnection();
        if( connection == null ) {
            return null;
        }

        connection.transaction(trx -> {
            trx.dsl()
                    .insertInto( RIDER_PREFERENCE )
                    .set( RIDER_PREFERENCE.RIDER_ID, model.getRiderId() )
                    .set( RIDER_PREFERENCE.AREA, model.getArea() )
                    .set( RIDER_PREFERENCE.VEHICLE_CLASS, model.getVehicleClass() )
                    .execute();

            final var batchQuery=
                    trx.dsl().batch(
                            trx.dsl().insertInto( PREFERENCE, PREFERENCE.RIDER_ID, PREFERENCE.PREF_KEY, PREFERENCE.PREF_VALUE )
                            .values(                            (Long) null,        (String) null,          (String) null     )
                    );

            for(final var entry : model.getPreferences().entrySet()) {
                batchQuery.bind( model.getRiderId(), entry.getKey(), entry.getValue() );
            }

            batchQuery.execute();
        });

        return model;
    }

    @Override
    public void delete(Long id) {
        final var connection= tryGetConnection();
        if( connection == null ) {
            return;
        }

        connection.transaction(trx -> {
            trx.dsl()
                    .deleteFrom( PREFERENCE )
                    .where( PREFERENCE.RIDER_ID.eq(id) )
                    .execute();

            trx.dsl()
                    .deleteFrom( RIDER_PREFERENCE )
                    .where( RIDER_PREFERENCE.RIDER_ID.eq(id) )
                    .execute();
        });
    }

    @Override
    public void updatePreferences(IRiderPreference model) {
        final var connection= tryGetConnection();
        if( connection == null ) {
            return;
        }

        connection.batched(trx -> {
            for( final var entry : model.getPreferences().entrySet() ) {
                trx.dsl()
                        .mergeInto( PREFERENCE )
                        .using( trx.dsl().selectOne() )
                        .on( PREFERENCE.RIDER_ID.eq(model.getRiderId())
                                .and( PREFERENCE.PREF_KEY.eq(entry.getKey())) )
                        .whenMatchedThenUpdate()
                        .set( PREFERENCE.PREF_VALUE, entry.getValue() )
                        .whenNotMatchedThenInsert( PREFERENCE.RIDER_ID, PREFERENCE.PREF_KEY, PREFERENCE.PREF_VALUE )
                        .values( model.getRiderId(), entry.getKey(), entry.getValue() )
                        .execute();
            }
        });
    }
}
