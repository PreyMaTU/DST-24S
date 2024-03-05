package dst.ass1.jpa.util;

public final class Constants {

    public static final String JPA_PERSISTENCE_UNIT = "dst_pu";

    /* TYPES (CLASSES) */
    public static final String T_DRIVER = "Driver";
    public static final String T_EMPLOYMENT = "Employment";
    public static final String T_LOCATION = "Location";
    public static final String T_MATCH = "Match";
    public static final String T_ORGANIZATION = "Organization";
    public static final String T_PREFERENCES = "Preferences";
    public static final String T_RIDER = "Rider";
    public static final String T_TRIP = "Trip";
    public static final String T_TRIP_INFO = "TripInfo";
    public static final String T_VEHICLE = "Vehicle";

    public static final String T_TRIP_RECEIPT = "TripReceipt";
    public static final String T_PAYMENT_INFO = "PaymentInfo";

    /* IDs (FOREIGN KEYS) */
    public static final String I_DRIVER = "driver_id";
    public static final String I_EMPLOYMENT = "employment_id";
    public static final String I_LOCATION = "location_id";
    public static final String I_MATCH = "match_id";
    public static final String I_ORGANIZATION = "organization_id";
    public static final String I_ORGANIZATION_PART_OF = "partOfOrganization_id";
    public static final String I_ORGANIZATION_PARTS = "partsOrganization_id";
    public static final String I_PREFERENCES = "preferences_id";
    public static final String I_RIDER = "rider_id";
    public static final String I_PAYMENT_INFOS = "paymentinfos_id";
    public static final String I_PAYMENT_INFO = "paymentinfo_id";
    public static final String I_TRIP_RECEIPT = "tripreceipt_id";
    public static final String I_TRIP = "trip_id";
    public static final String I_TRIP_INFO = "tripInfo_id";
    public static final String I_VEHICLE = "vehicle_id";
    public static final String I_VEHICLES = "vehicles_id";

    public static final String I_DESTINATION = "destination_id";
    public static final String I_PICKUP = "pickup_id";
    public static final String I_STOPS = "stops_id";


    /* MEMBER ATTRIBUTES */


    public static final String M_DRIVER_TEL = "tel";
    public static final String M_DRIVER_ORGANIZATIONS = "organizations";
    public static final String M_DRIVER_EMPLOYMENTS = "employments";
    public static final String M_DRIVER_VEHICLE = "vehicle";
    public static final String M_DRIVER_NAME = "name";
    public static final String M_DRIVER_AVG_RATING = "avgRating";
    public static final String M_EMPLOYMENT_SINCE = "since";
    public static final String M_EMPLOYMENT_ACTIVE = "active";
    public static final String M_LOCATION_LOCATION_ID = "locationId";
    public static final String M_LOCATION_GEO = "geo";
    public static final String M_LOCATION_NAME = "name";
    public static final String M_MATCH_DATE = "date";
    public static final String M_MATCH_FARE = "fare";
    public static final String M_MATCH_TRIP = "trip";
    public static final String M_MATCH_VEHICLE = "vehicle";
    public static final String M_MATCH_DRIVER = "driver";
    public static final String M_ORGANIZATION_NAME = "name";
    public static final String M_ORGANIZATION_EMPLOYMENTS = "employments";
    public static final String M_ORGANIZATION_PARTS = "parts";
    public static final String M_ORGANIZATION_DRIVERS = "drivers";
    public static final String M_PREFERENCES_DATA = "data";
    public static final String M_RIDER_EMAIL = "email";
    public static final String M_RIDER_PASSWORD = "password";
    public static final String M_RIDER_TEL = "tel";
    public static final String M_RIDER_PREFERENCES = "preferences";
    public static final String M_RIDER_TRIPS = "trips";
    public static final String M_RIDER_NAME = "name";
    public static final String M_RIDER_AVG_RATING = "avgRating";
    public static final String M_TRIP_STOPS = "stops";
    public static final String M_TRIP_STATE = "state";
    public static final String M_TRIP_CREATED = "created";
    public static final String M_TRIP_UPDATED = "updated";
    public static final String M_TRIP_PICKUP = "pickup";
    public static final String M_TRIP_DESTINATION = "destination";
    public static final String M_TRIP_TRIP_INFO = "tripInfo";
    public static final String M_TRIP_MATCH = "match";
    public static final String M_TRIP_RIDER = "rider";
    public static final String M_TRIP_PAYMENT_METHOD = "paymentMethod";
    public static final String M_TRIP_INFO_COMPLETED = "completed";
    public static final String M_TRIP_INFO_TOTAL = "total";
    public static final String M_TRIP_INFO_DISTANCE = "distance";
    public static final String M_TRIP_INFO_TRIP = "trip";
    public static final String M_TRIP_INFO_DRIVER_RATING = "driverRating";
    public static final String M_TRIP_INFO_RIDER_RATING = "riderRating";
    public static final String M_VEHICLE_LICENSE = "license";
    public static final String M_VEHICLE_COLOR = "color";
    public static final String M_VEHICLE_TYPE = "type";


    /* ASSOCIATION NAMES (FOR QUERIES) */
    public static final String A_TRIP_INFO = "tripInfo";
    public static final String A_TRIP = "trip";
    public static final String A_RIDER = "rider";

    /* NAMED QUERIES */
    public static final String Q_RIDER_BY_EMAIL = "riderByEmail";
    public static final String Q_ACTIVE_IN_MULITIPLE_ORGANIZATIONS_DRIVERS = "activeInMultipleOrganizationsDrivers";
    public static final String Q_REACHED_LOCATIONS = "reachedLocations";
    public static final String Q_SUM_DISTANCE_MOST_RECENT_TRIP = "sumDistanceOfRiderWithMostRecentTrip";
    public static final String Q_INACTIVE_RIDER = "findRidersWithoutTripsBetweenDates";
    public static final String Q_AVERAGE_RATING_RIDER = "findAverageRatingByRider";
    public static final String Q_TRIP_BY_STATE = "findTripsByState";

    /* JOIN TABLES */
    public static final String J_ORGANIZATION_VEHICLE = "organization_vehicle";
    public static final String J_RIDER_PAYMENT_INFO = "rider_paymentinfo";
    public static final String J_ORGANIZATION_PARTS = "organization_parts";
    public static final String J_DRIVER_ORGANIZATION = "driver_organization";
    public static final String J_PREFERENCES_DATA = "preferences_data";
    public static final String J_DRIVER_EMPLOYMENT = "driver_employment";
    public static final String J_ORGANIZATION_EMPLOYMENT = "organization_employment";
    public static final String J_TRIP_LOCATION = "trip_location";
    public static final String J_DRIVER_VEHICLE = "driver_vehicle";

    /* EMBEDDED ATTRIBUTE OVERRIDES */
    public static final String AO_NAME_TIP_CURRENCY = "currency";
    public static final String AO_NAME_TIP_CURRENCY_VALUE = "currencyValue";

    public static final String AO_COLUMN_NAME_TIP_CURRENCY = "tipCurrency";
    public static final String AO_COLUMN_NAME_TIP_CURRENCY_VALUE = "tipCurrencyValue";

    /* MONGODB */
    public static final String MONGO_DB_NAME = "dst";
    public static final String COLL_LOCATION_DATA = "LocationData";


    private Constants() {
        // final
    }
}
