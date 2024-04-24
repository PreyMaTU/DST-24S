package dst.ass1.jpa.tests;

import dst.ass1.jpa.ITestData;
import dst.ass1.jpa.model.*;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class TestData implements ITestData {

    public static final String RIDER_1_NAME = "rider1";
    public static final String RIDER_2_NAME = "rider2";
    public static final String RIDER_3_NAME = "rider3";
    public static final String RIDER_4_NAME = "rider4";

    public static final String RIDER_1_EMAIL = "email1";
    public static final String RIDER_2_EMAIL = "email2";
    public static final String RIDER_3_EMAIL = "email3";
    public static final String RIDER_4_EMAIL = "email4";

    public static final String RIDER_1_PW = "pw1";
    public static final String RIDER_2_PW = "pw2";
    public static final String RIDER_3_PW = "pw3";
    public static final String RIDER_4_PW = "pw4";

    public static final String PAYMENT_INFO_1_NAME = "paymentInfo1";
    public static final String PAYMENT_INFO_2_NAME = "paymentInfo2";
    public static final String PAYMENT_INFO_3_NAME = "paymentInfo3";
    public static final String PAYMENT_INFO_4_NAME = "paymentInfo4";
    public static final String PAYMENT_INFO_5_NAME = "paymentInfo5";

    public static final Boolean PAYMENT_INFO_1_PREFERRED = true;
    public static final Boolean PAYMENT_INFO_2_PREFERRED = true;
    public static final Boolean PAYMENT_INFO_3_PREFERRED = true;
    public static final Boolean PAYMENT_INFO_4_PREFERRED = true;
    public static final Boolean PAYMENT_INFO_5_PREFERRED = false;

    public static final PaymentMethod PAYMENT_INFO_1_METHOD = PaymentMethod.CASH;
    public static final PaymentMethod PAYMENT_INFO_2_METHOD = PaymentMethod.PAYPAL;
    public static final PaymentMethod PAYMENT_INFO_3_METHOD = PaymentMethod.CREDIT_CARD;
    public static final PaymentMethod PAYMENT_INFO_4_METHOD = PaymentMethod.CREDIT_CARD;
    public static final PaymentMethod PAYMENT_INFO_5_METHOD = PaymentMethod.PAYPAL;


    public static final Double RIDER_1_AVG_RATING = 4.3;
    public static final Double RIDER_2_AVG_RATING = 5.0;
    public static final Double RIDER_3_AVG_RATING = 1.0;
    public static final Double RIDER_4_AVG_RATING = 2.1;

    public static final String RIDER_1_TEL = "+43000000001";
    public static final String RIDER_2_TEL = "+43000000002";
    public static final String RIDER_3_TEL = "+43000000003";
    public static final String RIDER_4_TEL = "+43000000004";

    public static final String DRIVER_1_NAME = "driver1";
    public static final String DRIVER_2_NAME = "driver2";
    public static final String DRIVER_3_NAME = "driver3";
    public static final String DRIVER_4_NAME = "driver4";
    public static final String DRIVER_5_NAME = "driver5";

    public static final String DRIVER_1_TEL = "+43000000011";
    public static final String DRIVER_2_TEL = "+43000000012";
    public static final String DRIVER_3_TEL = "+43000000013";
    public static final String DRIVER_4_TEL = "+43000000014";
    public static final String DRIVER_5_TEL = "+43000000015";

    public static final Double DRIVER_1_AVG_RATING = 2.3;
    public static final Double DRIVER_2_AVG_RATING = 2.0;
    public static final Double DRIVER_3_AVG_RATING = 3.9;
    public static final Double DRIVER_4_AVG_RATING = 4.09;
    public static final Double DRIVER_5_AVG_RATING = 1.5;

    public static final String LOCATION_1_NAME = "location1";
    public static final String LOCATION_2_NAME = "location2";
    public static final String LOCATION_3_NAME = "location3";
    public static final String LOCATION_4_NAME = "location4";
    public static final String LOCATION_5_NAME = "location5";

    public static final Long LOCATION_1_ID = 1L;
    public static final Long LOCATION_2_ID = 2L;
    public static final Long LOCATION_3_ID = 3L;
    public static final Long LOCATION_4_ID = 4L;
    public static final Long LOCATION_5_ID = 5L;

    public static final String ORGANIZATION_1_NAME = "organization1";
    public static final String ORGANIZATION_2_NAME = "organization2";
    public static final String ORGANIZATION_3_NAME = "organization3";
    public static final String ORGANIZATION_4_NAME = "organization4";
    public static final String ORGANIZATION_5_NAME = "organization5";
    public static final String ORGANIZATION_6_NAME = "organization";

    public static final String VEHICLE_1_LICENSE = "license1";
    public static final String VEHICLE_2_LICENSE = "license2";
    public static final String VEHICLE_3_LICENSE = "license3";
    public static final String VEHICLE_4_LICENSE = "license4";

    public static final String COLOR_1 = "yellow";
    public static final String COLOR_2 = "red";
    public static final String COLOR_3 = "green";

    public static final String TYPE_1 = "type1";
    public static final String TYPE_2 = "type2";

    public static final String CURRENCY_1 = "currency1";
    public static final String CURRENCY_2 = "currency2";
    public static final String CURRENCY_3 = "currency3";

    public static final BigDecimal TRIP_RECEIPT_1_TOTAL_MONEY_VALUE = new BigDecimal(330.0);
    public static final BigDecimal TRIP_RECEIPT_2_TOTAL_MONEY_VALUE = new BigDecimal(10.0);
    public static final BigDecimal TRIP_RECEIPT_3_TOTAL_MONEY_VALUE = new BigDecimal(70.0);
    public static final BigDecimal TRIP_RECEIPT_4_TOTAL_MONEY_VALUE = new BigDecimal(23.0);
    public static final BigDecimal TRIP_RECEIPT_5_TOTAL_MONEY_VALUE = new BigDecimal(25.0);
    public static final BigDecimal TRIP_RECEIPT_17_TOTAL_MONEY_VALUE = new BigDecimal(22.0);

    public static final BigDecimal TRIP_RECEIPT_TIP_1_MONEY_VALUE = new BigDecimal(20.0);
    public static final BigDecimal TRIP_RECEIPT_TIP_2_MONEY_VALUE = new BigDecimal(0.0);
    public static final BigDecimal TRIP_RECEIPT_TIP_3_MONEY_VALUE = new BigDecimal(1.0);
    public static final BigDecimal TRIP_RECEIPT_TIP_4_MONEY_VALUE = new BigDecimal(2.5);
    public static final BigDecimal TRIP_RECEIPT_TIP_5_MONEY_VALUE = new BigDecimal(5.0);
    public static final BigDecimal TRIP_RECEIPT_TIP_17_MONEY_VALUE = new BigDecimal(1.0);

    public static final Boolean TRIP_RECEIPT_PAID_1 = true;
    public static final Boolean TRIP_RECEIPT_PAID_2 = true;
    public static final Boolean TRIP_RECEIPT_PAID_3 = true;
    public static final Boolean TRIP_RECEIPT_PAID_4 = true;
    public static final Boolean TRIP_RECEIPT_PAID_5 = true;
    public static final Boolean TRIP_RECEIPT_PAID_17 = false;


    public static final BigDecimal MATCH_FARE_1 = new BigDecimal(200.0);
    public static final BigDecimal MATCH_FARE_2 = new BigDecimal(12.0);
    public static final BigDecimal MATCH_FARE_3 = new BigDecimal(70.0);
    public static final BigDecimal MATCH_FARE_4 = new BigDecimal(65.0);
    public static final BigDecimal MATCH_FARE_5 = new BigDecimal(23.0);
    public static final BigDecimal MATCH_FARE_6 = new BigDecimal(30.0);
    public static final BigDecimal MATCH_FARE_7 = new BigDecimal(22.0);
    public static final BigDecimal MATCH_FARE_17 = new BigDecimal(25.0);

    public static final Date MATCH_1_CREATED = createDate(2020, 1, 27, 2, 2);
    public static final Date MATCH_2_CREATED = createDate(2020, 1, 27, 2, 2);
    public static final Date MATCH_3_CREATED = createDate(2018, 10, 11, 11, 11);

    public static final double TRIP_1_DISTANCE = 33.2;
    public static final double TRIP_2_DISTANCE = 0.9;
    public static final double TRIP_3_DISTANCE = 2.12;
    public static final double TRIP_4_DISTANCE = 10.0;
    public static final double TRIP_5_DISTANCE = 8.3;
    public static final double TRIP_17_DISTANCE = 11.0;

    public static final Date TRIP_6_CREATED = createDate(2019, 1, 2, 2, 2);
    public static final Date TRIP_11_CREATED = createDate(2018, 4, 5, 5, 5);
    public static final Date TRIP_12_CREATED = createDate(2017, 0, 2, 2, 2);
    public static final Date TRIP_13_CREATED = createDate(2019, 1, 26, 3, 3);
    public static final Date TRIP_14_CREATED = createDate(2019, 1, 27, 2, 2);
    public static final Date TRIP_15_CREATED = createDate(2019, 1, 28, 8, 9);
    public static final Date TRIP_16_CREATED = createDate(2018, 10, 11, 11, 11);

    public Long rider1Id;
    public Long rider2Id;
    public Long rider3Id;
    public Long rider4Id;
    public Long organization1Id;
    public Long organization2Id;
    public Long organization3Id;
    public Long organization4Id;
    public Long organization5Id;
    public Long driver1Id;
    public Long driver2Id;
    public Long driver3Id;
    public Long driver4Id;
    public IEmploymentKey employmentId1;
    public IEmploymentKey employmentId2;
    public IEmploymentKey employmentId3;
    public IEmploymentKey employmentId4;
    public IEmploymentKey employmentId5;
    public Long vehicle1Id;
    public Long vehicle2Id;
    public Long vehicle3Id;
    public Long vehicle4Id;
    public Long match1Id;
    public Long match2Id;
    public Long match3Id;
    public Long match4Id;
    public Long match5Id;
    public Long match7Id;
    public Long match9Id;
    public Long match17Id;
    public Long location1Id;
    public Long location2Id;
    public Long location3Id;
    public Long location4Id;
    public Long location5Id;
    public Long trip1Id;
    public Long trip2Id;
    public Long trip3Id;
    public Long trip4Id;
    public Long trip5Id;
    public Long trip6Id;
    public Long trip7Id;
    public Long trip8Id;
    public Long trip9Id;
    public Long trip10Id;
    public Long trip11Id;
    public Long trip12Id;
    public Long trip13Id;
    public Long trip14Id;
    public Long trip15Id;
    public Long trip16Id;
    public Long trip17Id;
    public Long tripInfo1Id;
    public Long tripInfo2Id;
    public Long tripInfo3Id;
    public Long tripInfo4Id;
    public Long tripInfo5Id;
    public Long tripInfo17Id;

    public Long tripReceipt1Id;
    public Long tripReceipt2Id;
    public Long tripReceipt3Id;
    public Long tripReceipt4Id;
    public Long tripReceipt5Id;
    public Long tripReceipt17Id;

    public Long paymentInfo1Id;
    public Long paymentInfo2Id;
    public Long paymentInfo3Id;
    public Long paymentInfo4Id;
    public Long paymentInfo5Id;

    public static Date createDate(int year, int month, int day, int hours, int minutes) {
        GregorianCalendar cal = new GregorianCalendar(year, month, day, hours, minutes);
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        return cal.getTime();
    }

    public static Date localDateToDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public void insert(IModelFactory modelFactory, EntityManager em) {
        MessageDigest md = getMessageDigest();

        //Money
        IMoney total1 = modelFactory.createMoney();
        IMoney total2 = modelFactory.createMoney();
        IMoney total3 = modelFactory.createMoney();
        IMoney total4 = modelFactory.createMoney();
        IMoney total5 = modelFactory.createMoney();
        IMoney total17 = modelFactory.createMoney();

        total1.setCurrencyValue(TRIP_RECEIPT_1_TOTAL_MONEY_VALUE);
        total2.setCurrencyValue(TRIP_RECEIPT_2_TOTAL_MONEY_VALUE);
        total3.setCurrencyValue(TRIP_RECEIPT_3_TOTAL_MONEY_VALUE);
        total4.setCurrencyValue(TRIP_RECEIPT_4_TOTAL_MONEY_VALUE);
        total5.setCurrencyValue(TRIP_RECEIPT_5_TOTAL_MONEY_VALUE);
        total17.setCurrencyValue(TRIP_RECEIPT_17_TOTAL_MONEY_VALUE);

        total1.setCurrency(CURRENCY_1);
        total2.setCurrency(CURRENCY_2);
        total3.setCurrency(CURRENCY_3);
        total4.setCurrency(CURRENCY_1);
        total5.setCurrency(CURRENCY_2);
        total17.setCurrency(CURRENCY_2);

        IMoney tip1 = modelFactory.createMoney();
        IMoney tip2 = modelFactory.createMoney();
        IMoney tip3 = modelFactory.createMoney();
        IMoney tip4 = modelFactory.createMoney();
        IMoney tip5 = modelFactory.createMoney();
        IMoney tip17 = modelFactory.createMoney();

        tip1.setCurrency(CURRENCY_1);
        tip2.setCurrency(CURRENCY_2);
        tip3.setCurrency(CURRENCY_3);
        tip5.setCurrency(CURRENCY_2);
        tip4.setCurrency(CURRENCY_1);
        tip17.setCurrency(CURRENCY_2);

        tip1.setCurrencyValue(TRIP_RECEIPT_TIP_1_MONEY_VALUE);
        tip2.setCurrencyValue(TRIP_RECEIPT_TIP_2_MONEY_VALUE);
        tip3.setCurrencyValue(TRIP_RECEIPT_TIP_3_MONEY_VALUE);
        tip4.setCurrencyValue(TRIP_RECEIPT_TIP_4_MONEY_VALUE);
        tip5.setCurrencyValue(TRIP_RECEIPT_TIP_5_MONEY_VALUE);
        tip17.setCurrencyValue(TRIP_RECEIPT_TIP_17_MONEY_VALUE);

        total1.setCurrencyValue(TRIP_RECEIPT_1_TOTAL_MONEY_VALUE);
        total2.setCurrencyValue(TRIP_RECEIPT_2_TOTAL_MONEY_VALUE);
        total3.setCurrencyValue(TRIP_RECEIPT_3_TOTAL_MONEY_VALUE);
        total4.setCurrencyValue(TRIP_RECEIPT_4_TOTAL_MONEY_VALUE);
        total5.setCurrencyValue(TRIP_RECEIPT_5_TOTAL_MONEY_VALUE);
        total17.setCurrencyValue(TRIP_RECEIPT_17_TOTAL_MONEY_VALUE);

        total1.setCurrency(CURRENCY_1);
        total2.setCurrency(CURRENCY_2);
        total3.setCurrency(CURRENCY_3);
        total4.setCurrency(CURRENCY_1);
        total5.setCurrency(CURRENCY_2);
        total17.setCurrency(CURRENCY_2);

        IMoney fare1 = modelFactory.createMoney();
        IMoney fare2 = modelFactory.createMoney();
        IMoney fare3 = modelFactory.createMoney();
        IMoney fare4 = modelFactory.createMoney();
        IMoney fare5 = modelFactory.createMoney();
        IMoney fare6 = modelFactory.createMoney();
        IMoney fare7 = modelFactory.createMoney();
        IMoney fare17 = modelFactory.createMoney();

        fare1.setCurrency(CURRENCY_1);
        fare2.setCurrency(CURRENCY_2);
        fare3.setCurrency(CURRENCY_3);
        fare4.setCurrency(CURRENCY_1);
        fare5.setCurrency(CURRENCY_2);
        fare6.setCurrency(CURRENCY_1);
        fare7.setCurrency(CURRENCY_1);
        fare17.setCurrency(CURRENCY_2);

        fare1.setCurrencyValue(MATCH_FARE_1);
        fare2.setCurrencyValue(MATCH_FARE_2);
        fare3.setCurrencyValue(MATCH_FARE_3);
        fare4.setCurrencyValue(MATCH_FARE_4);
        fare5.setCurrencyValue(MATCH_FARE_5);
        fare6.setCurrencyValue(MATCH_FARE_6);
        fare7.setCurrencyValue(MATCH_FARE_7);
        fare17.setCurrencyValue(MATCH_FARE_17);


        // Payment Infos
        IPaymentInfo paymentInfo1 = modelFactory.createPaymentInfo();
        IPaymentInfo paymentInfo2 = modelFactory.createPaymentInfo();
        IPaymentInfo paymentInfo3 = modelFactory.createPaymentInfo();
        IPaymentInfo paymentInfo4 = modelFactory.createPaymentInfo();
        IPaymentInfo paymentInfo5 = modelFactory.createPaymentInfo();

        paymentInfo1.setName(PAYMENT_INFO_1_NAME);
        paymentInfo2.setName(PAYMENT_INFO_2_NAME);
        paymentInfo3.setName(PAYMENT_INFO_3_NAME);
        paymentInfo4.setName(PAYMENT_INFO_4_NAME);
        paymentInfo5.setName(PAYMENT_INFO_5_NAME);

        paymentInfo1.setPreferred(PAYMENT_INFO_1_PREFERRED);
        paymentInfo2.setPreferred(PAYMENT_INFO_2_PREFERRED);
        paymentInfo3.setPreferred(PAYMENT_INFO_3_PREFERRED);
        paymentInfo4.setPreferred(PAYMENT_INFO_4_PREFERRED);
        paymentInfo5.setPreferred(PAYMENT_INFO_5_PREFERRED);

        paymentInfo1.setPaymentMethod(PAYMENT_INFO_1_METHOD);
        paymentInfo2.setPaymentMethod(PAYMENT_INFO_2_METHOD);
        paymentInfo3.setPaymentMethod(PAYMENT_INFO_3_METHOD);
        paymentInfo4.setPaymentMethod(PAYMENT_INFO_4_METHOD);
        paymentInfo5.setPaymentMethod(PAYMENT_INFO_5_METHOD);


        //Riders
        IRider rider1 = modelFactory.createRider();
        IRider rider2 = modelFactory.createRider();
        IRider rider3 = modelFactory.createRider();
        IRider rider4 = modelFactory.createRider();

        rider1.setEmail(RIDER_1_EMAIL);
        rider2.setEmail(RIDER_2_EMAIL);
        rider3.setEmail(RIDER_3_EMAIL);
        rider4.setEmail(RIDER_4_EMAIL);

        rider1.setPassword(md.digest(RIDER_1_PW.getBytes()));
        rider2.setPassword(md.digest(RIDER_2_PW.getBytes()));
        rider3.setPassword(md.digest(RIDER_3_PW.getBytes()));
        rider4.setPassword(md.digest(RIDER_4_PW.getBytes()));

        rider1.setName(RIDER_1_NAME);
        rider2.setName(RIDER_2_NAME);
        rider3.setName(RIDER_3_NAME);
        rider4.setName(RIDER_4_NAME);

        rider1.setAvgRating(RIDER_1_AVG_RATING);
        rider2.setAvgRating(RIDER_2_AVG_RATING);
        rider3.setAvgRating(RIDER_3_AVG_RATING);
        rider4.setAvgRating(RIDER_4_AVG_RATING);

        rider1.setTel(RIDER_1_TEL);
        rider2.setTel(RIDER_2_TEL);
        rider3.setTel(RIDER_3_TEL);
        rider4.setTel(RIDER_4_TEL);

        List<IPaymentInfo> rider1PaymentInfos = new LinkedList<>();
        rider1PaymentInfos.add(paymentInfo1);

        List<IPaymentInfo> rider2PaymentInfos = new LinkedList<>();
        rider2PaymentInfos.add(paymentInfo2);

        List<IPaymentInfo> rider3PaymentInfos = new LinkedList<>();
        rider3PaymentInfos.add(paymentInfo3);

        List<IPaymentInfo> rider4PaymentInfos = new LinkedList<>();
        rider4PaymentInfos.add(paymentInfo4);
        rider4PaymentInfos.add(paymentInfo5);

        rider1.setPaymentInfos(rider1PaymentInfos);
        rider2.setPaymentInfos(rider2PaymentInfos);
        rider3.setPaymentInfos(rider3PaymentInfos);
        rider4.setPaymentInfos(rider4PaymentInfos);

        //Organizations
        IOrganization organization1 = modelFactory.createOrganization();
        IOrganization organization2 = modelFactory.createOrganization();
        IOrganization organization3 = modelFactory.createOrganization();
        IOrganization organization4 = modelFactory.createOrganization();
        IOrganization organization5 = modelFactory.createOrganization();

        organization1.setName(ORGANIZATION_1_NAME);
        organization2.setName(ORGANIZATION_2_NAME);
        organization3.setName(ORGANIZATION_3_NAME);
        organization4.setName(ORGANIZATION_4_NAME);
        organization5.setName(ORGANIZATION_5_NAME);

        organization1.addPart(organization4);
        organization1.addPart(organization5);

        organization4.addPartOf(organization1);
        organization5.addPartOf(organization1);

        organization4.addPart(organization2);
        organization2.addPartOf(organization4);

        organization2.addPart(organization1);
        organization1.addPartOf(organization2);


        //Drivers
        IDriver driver1 = modelFactory.createDriver();
        IDriver driver2 = modelFactory.createDriver();
        IDriver driver3 = modelFactory.createDriver();
        IDriver driver4 = modelFactory.createDriver();
        IDriver driver5 = modelFactory.createDriver();

        driver1.setName(DRIVER_1_NAME);
        driver2.setName(DRIVER_2_NAME);
        driver3.setName(DRIVER_3_NAME);
        driver4.setName(DRIVER_4_NAME);
        driver5.setName(DRIVER_5_NAME);

        driver1.setTel(DRIVER_1_TEL);
        driver2.setTel(DRIVER_2_TEL);
        driver3.setTel(DRIVER_3_TEL);
        driver4.setTel(DRIVER_4_TEL);
        driver5.setTel(DRIVER_5_TEL);

        driver1.setAvgRating(DRIVER_1_AVG_RATING);
        driver2.setAvgRating(DRIVER_2_AVG_RATING);
        driver3.setAvgRating(DRIVER_3_AVG_RATING);
        driver4.setAvgRating(DRIVER_4_AVG_RATING);
        driver5.setAvgRating(DRIVER_5_AVG_RATING);

        //EmploymentKeys
        IEmploymentKey employmentKey1 = modelFactory.createEmploymentKey();
        IEmploymentKey employmentKey2 = modelFactory.createEmploymentKey();
        IEmploymentKey employmentKey3 = modelFactory.createEmploymentKey();
        IEmploymentKey employmentKey4 = modelFactory.createEmploymentKey();
        IEmploymentKey employmentKey5 = modelFactory.createEmploymentKey();
        IEmploymentKey employmentKey6 = modelFactory.createEmploymentKey();
        IEmploymentKey employmentKey7 = modelFactory.createEmploymentKey();
        IEmploymentKey employmentKey8 = modelFactory.createEmploymentKey();
        IEmploymentKey employmentKey9 = modelFactory.createEmploymentKey();
        IEmploymentKey employmentKey10 = modelFactory.createEmploymentKey();
        IEmploymentKey employmentKey11 = modelFactory.createEmploymentKey();
        IEmploymentKey employmentKey12 = modelFactory.createEmploymentKey();
        IEmploymentKey employmentKey13 = modelFactory.createEmploymentKey();
        IEmploymentKey employmentKey14 = modelFactory.createEmploymentKey();

        // Driver 1
        employmentKey1.setDriver(driver1);
        employmentKey1.setOrganization(organization1);

        employmentKey6.setDriver(driver1);
        employmentKey6.setOrganization(organization3);


        // Driver 2
        employmentKey2.setDriver(driver2);
        employmentKey2.setOrganization(organization1);

        employmentKey9.setDriver(driver2);
        employmentKey9.setOrganization(organization2);

        // Driver 3
        employmentKey3.setDriver(driver3);
        employmentKey3.setOrganization(organization3);

        employmentKey13.setDriver(driver3);
        employmentKey13.setOrganization(organization4);

        // Driver 4
        employmentKey5.setDriver(driver4);
        employmentKey5.setOrganization(organization2);

        employmentKey4.setDriver(driver4);
        employmentKey4.setOrganization(organization4);

        employmentKey8.setDriver(driver4);
        employmentKey8.setOrganization(organization5);

        // Driver 5
        employmentKey10.setDriver(driver5);
        employmentKey10.setOrganization(organization1);

        employmentKey11.setDriver(driver5);
        employmentKey11.setOrganization(organization2);

        employmentKey12.setDriver(driver5);
        employmentKey12.setOrganization(organization3);

        employmentKey7.setDriver(driver5);
        employmentKey7.setOrganization(organization4);

        employmentKey14.setDriver(driver5);
        employmentKey14.setOrganization(organization5);


        //Employments
        IEmployment employment1 = modelFactory.createEmployment();
        IEmployment employment2 = modelFactory.createEmployment();
        IEmployment employment3 = modelFactory.createEmployment();
        IEmployment employment4 = modelFactory.createEmployment();
        IEmployment employment5 = modelFactory.createEmployment();
        IEmployment employment6 = modelFactory.createEmployment();
        IEmployment employment7 = modelFactory.createEmployment();
        IEmployment employment8 = modelFactory.createEmployment();
        IEmployment employment9 = modelFactory.createEmployment();
        IEmployment employment10 = modelFactory.createEmployment();
        IEmployment employment11 = modelFactory.createEmployment();
        IEmployment employment12 = modelFactory.createEmployment();
        IEmployment employment13 = modelFactory.createEmployment();
        IEmployment employment14 = modelFactory.createEmployment();

        employment1.setActive(true);
        employment1.setSince(createDate(2000, 1, 1, 1, 1));
        employment1.setId(employmentKey1);


        employment2.setActive(true);
        employment2.setSince(createDate(2017, 4, 3, 2, 2));
        employment2.setId(employmentKey2);

        employment3.setActive(false);
        employment3.setSince(createDate(2018, 8, 23, 4, 45));
        employment3.setId(employmentKey3);

        employment4.setActive(true);
        employment4.setSince(createDate(2014, 7, 19, 1, 1));
        employment4.setId(employmentKey4);

        employment5.setActive(false);
        employment5.setSince(createDate(2014, 7, 19, 1, 1));
        employment5.setId(employmentKey5);

        employment6.setActive(true);
        employment6.setSince(createDate(2018, 2, 2, 2, 2));
        employment6.setId(employmentKey6);

        employment7.setActive(true);
        employment7.setSince(createDate(2018, 1, 1, 1, 1));
        employment7.setId(employmentKey7);

        employment8.setActive(true);
        employment8.setSince(createDate(2014, 7, 19, 1, 1));
        employment8.setId(employmentKey8);

        employment9.setActive(true);
        employment9.setSince(createDate(2014, 7, 19, 1, 1));
        employment9.setId(employmentKey9);

        employment10.setActive(true);
        employment10.setSince(createDate(2014, 7, 19, 1, 1));
        employment10.setId(employmentKey10);

        employment11.setActive(true);
        employment11.setSince(createDate(2014, 7, 19, 1, 1));
        employment11.setId(employmentKey11);

        employment12.setActive(true);
        employment12.setSince(createDate(2014, 7, 19, 1, 1));
        employment12.setId(employmentKey12);

        employment13.setActive(true);
        employment13.setSince(createDate(2014, 7, 19, 1, 1));
        employment13.setId(employmentKey13);

        employment14.setActive(true);
        employment14.setSince(localDateToDate(LocalDate.now().minusDays(2)));
        employment14.setId(employmentKey14);

        driver1.addEmployment(employment1);
        driver1.addEmployment(employment5);
        driver1.addEmployment(employment6);
        driver1.addEmployment(employment7);
        driver1.addEmployment(employment8);

        driver2.addEmployment(employment2);
        driver2.addEmployment(employment9);

        driver3.addEmployment(employment3);

        driver4.addEmployment(employment4);

        driver5.addEmployment(employment10);
        driver5.addEmployment(employment11);
        driver5.addEmployment(employment12);
        driver5.addEmployment(employment13);
        driver5.addEmployment(employment14);


        organization1.addEmployment(employment1);
        organization1.addEmployment(employment2);
        organization1.addEmployment(employment10);

        organization2.addEmployment(employment5);
        organization2.addEmployment(employment9);
        organization2.addEmployment(employment11);

        organization3.addEmployment(employment3);
        organization3.addEmployment(employment6);
        organization3.addEmployment(employment12);

        organization4.addEmployment(employment4);
        organization4.addEmployment(employment7);
        organization4.addEmployment(employment13);

        organization5.addEmployment(employment8);
        organization5.addEmployment(employment14);

        //Vehicles
        IVehicle vehicle1 = modelFactory.createVehicle();
        IVehicle vehicle2 = modelFactory.createVehicle();
        IVehicle vehicle3 = modelFactory.createVehicle();
        IVehicle vehicle4 = modelFactory.createVehicle();

        vehicle1.setLicense(VEHICLE_1_LICENSE);
        vehicle2.setLicense(VEHICLE_2_LICENSE);
        vehicle3.setLicense(VEHICLE_3_LICENSE);
        vehicle4.setLicense(VEHICLE_4_LICENSE);

        vehicle1.setColor(COLOR_1);
        vehicle2.setColor(COLOR_2);
        vehicle3.setColor(COLOR_3);
        vehicle4.setColor(COLOR_1);

        vehicle1.setType(TYPE_1);
        vehicle2.setType(TYPE_1);
        vehicle3.setType(TYPE_2);
        vehicle4.setType(TYPE_1);

        driver1.setVehicle(vehicle1);
        driver2.setVehicle(vehicle2);
        driver3.setVehicle(vehicle3);
        driver4.setVehicle(vehicle4);
        driver5.setVehicle(vehicle1);

        List<IVehicle> vehiclesOrganization1 = new LinkedList<>();
        List<IVehicle> vehiclesOrganization2 = new LinkedList<>();

        vehiclesOrganization1.add(vehicle1);
        vehiclesOrganization1.add(vehicle2);
        vehiclesOrganization1.add(vehicle3);

        vehiclesOrganization2.add(vehicle4);

        organization1.setVehicles(vehiclesOrganization1);
        organization2.setVehicles(vehiclesOrganization2);

        //Locations
        ILocation location1 = modelFactory.createLocation();
        ILocation location2 = modelFactory.createLocation();
        ILocation location3 = modelFactory.createLocation();
        ILocation location4 = modelFactory.createLocation();
        ILocation location5 = modelFactory.createLocation();

        location1.setName(LOCATION_1_NAME);
        location2.setName(LOCATION_2_NAME);
        location3.setName(LOCATION_3_NAME);
        location4.setName(LOCATION_4_NAME);
        location5.setName(LOCATION_5_NAME);

        location1.setLocationId(LOCATION_1_ID);
        location2.setLocationId(LOCATION_2_ID);
        location3.setLocationId(LOCATION_3_ID);
        location4.setLocationId(LOCATION_4_ID);
        location5.setLocationId(LOCATION_5_ID);

        //Matches
        IMatch match1 = modelFactory.createMatch();
        IMatch match2 = modelFactory.createMatch();
        IMatch match3 = modelFactory.createMatch();
        IMatch match4 = modelFactory.createMatch();
        IMatch match5 = modelFactory.createMatch();
        IMatch match7 = modelFactory.createMatch();
        IMatch match9 = modelFactory.createMatch();
        IMatch match17 = modelFactory.createMatch();

        match1.setDate(MATCH_1_CREATED);
        match2.setDate(MATCH_2_CREATED);
        match3.setDate(MATCH_3_CREATED);
        match4.setDate(new Date());
        match5.setDate(new Date());
        match7.setDate(new Date());
        match9.setDate(new Date());
        match17.setDate(new Date());

        match1.setFare(fare1);
        match2.setFare(fare2);
        match3.setFare(fare3);
        match4.setFare(fare4);
        match5.setFare(fare5);
        match7.setFare(fare6);
        match9.setFare(fare7);
        match17.setFare(fare17);

        match1.setDriver(driver1);
        match2.setDriver(driver1);
        match3.setDriver(driver1);
        match4.setDriver(driver2);
        match5.setDriver(driver3);
        match7.setDriver(driver4);
        match9.setDriver(driver1);
        match17.setDriver(driver1);

        match1.setVehicle(vehicle1);
        match2.setVehicle(vehicle1);
        match3.setVehicle(vehicle1);
        match4.setVehicle(vehicle2);
        match5.setVehicle(vehicle3);
        match7.setVehicle(vehicle4);
        match9.setVehicle(vehicle1);
        match17.setVehicle(vehicle1);


        //Trips
        ITrip trip1 = modelFactory.createTrip();
        ITrip trip2 = modelFactory.createTrip();
        ITrip trip3 = modelFactory.createTrip();
        ITrip trip4 = modelFactory.createTrip();
        ITrip trip5 = modelFactory.createTrip();
        ITrip trip6 = modelFactory.createTrip();
        ITrip trip7 = modelFactory.createTrip();
        ITrip trip8 = modelFactory.createTrip();
        ITrip trip9 = modelFactory.createTrip();
        ITrip trip10 = modelFactory.createTrip();
        ITrip trip11 = modelFactory.createTrip();
        ITrip trip12 = modelFactory.createTrip();
        ITrip trip13 = modelFactory.createTrip();
        ITrip trip14 = modelFactory.createTrip();
        ITrip trip15 = modelFactory.createTrip();
        ITrip trip16 = modelFactory.createTrip();
        ITrip trip17 = modelFactory.createTrip();

        trip1.setCreated(new Date());
        trip1.setUpdated(new Date());
        trip2.setCreated(new Date());
        trip2.setUpdated(new Date());
        trip3.setCreated(new Date());
        trip3.setUpdated(new Date());
        trip4.setCreated(new Date());
        trip4.setUpdated(new Date());
        trip5.setCreated(new Date());
        trip5.setUpdated(new Date());
        trip6.setCreated(TRIP_6_CREATED);
        trip6.setUpdated(new Date());
        trip7.setCreated(new Date());
        trip7.setUpdated(new Date());
        trip8.setCreated(new Date());
        trip8.setUpdated(new Date());
        trip9.setCreated(new Date());
        trip9.setUpdated(new Date());
        trip10.setCreated(new Date());
        trip10.setUpdated(new Date());
        trip11.setCreated(TRIP_11_CREATED);
        trip11.setUpdated(new Date());
        trip12.setCreated(TRIP_12_CREATED);
        trip12.setUpdated(new Date());
        trip13.setCreated(TRIP_13_CREATED);
        trip13.setUpdated(new Date());
        trip14.setCreated(TRIP_14_CREATED);
        trip14.setUpdated(new Date());
        trip15.setCreated(TRIP_15_CREATED);
        trip15.setUpdated(new Date());
        trip16.setCreated(TRIP_16_CREATED);
        trip16.setUpdated(new Date());
        trip17.setCreated(new Date());
        trip17.setUpdated(new Date());


        trip1.setState(TripState.COMPLETED);
        trip2.setState(TripState.COMPLETED);
        trip3.setState(TripState.COMPLETED);
        trip4.setState(TripState.COMPLETED);
        trip5.setState(TripState.COMPLETED);
        trip6.setState(TripState.CREATED);
        trip7.setState(TripState.APPROACHING);
        trip8.setState(TripState.CANCELLED);
        trip9.setState(TripState.IN_PROGRESS);
        trip10.setState(TripState.QUEUED);
        trip11.setState(TripState.CANCELLED);
        trip12.setState(TripState.CANCELLED);
        trip13.setState(TripState.CANCELLED);
        trip14.setState(TripState.CANCELLED);
        trip15.setState(TripState.CANCELLED);
        trip16.setState(TripState.CANCELLED);
        trip17.setState(TripState.COMPLETED);

        trip1.setPickup(location1);
        trip2.setPickup(location1);
        trip3.setPickup(location1);
        trip4.setPickup(location3);
        trip5.setPickup(location4);
        trip6.setPickup(location5);
        trip7.setPickup(location1);
        trip8.setPickup(location1);
        trip9.setPickup(location2);
        trip10.setPickup(location3);
        trip11.setPickup(location3);
        trip12.setPickup(location3);
        trip13.setPickup(location3);
        trip14.setPickup(location3);
        trip15.setPickup(location3);
        trip16.setPickup(location3);
        trip17.setPickup(location3);


        trip1.setDestination(location2);
        trip2.setDestination(location2);
        trip3.setDestination(location2);
        trip4.setDestination(location1);
        trip5.setDestination(location4);
        trip6.setDestination(location1);
        trip7.setDestination(location3);
        trip8.setDestination(location4);
        trip9.setDestination(location5);
        trip10.setDestination(location1);
        trip11.setDestination(location1);
        trip12.setDestination(location1);
        trip13.setDestination(location1);
        trip14.setDestination(location1);
        trip15.setDestination(location1);
        trip16.setDestination(location1);
        trip17.setDestination(location1);

        List<ILocation> stopsTrip1 = new LinkedList<>();
        stopsTrip1.add(location3);
        stopsTrip1.add(location4);
        stopsTrip1.add(location5);
        trip1.setStops(stopsTrip1);

        List<ILocation> stopsTrip2 = new LinkedList<>();
        stopsTrip2.add(location3);
        trip2.setStops(stopsTrip2);

        List<ILocation> stopsTrip6 = new LinkedList<>();
        stopsTrip6.add(location2);
        stopsTrip6.add(location3);
        stopsTrip6.add(location4);
        trip6.setStops(stopsTrip6);

        List<ILocation> stopsTrip10 = new LinkedList<>();
        stopsTrip10.add(location4);
        trip10.setStops(stopsTrip10);

        trip1.setRider(rider1);
        trip2.setRider(rider2);
        trip3.setRider(rider3);
        trip4.setRider(rider2);
        trip5.setRider(rider1);
        trip6.setRider(rider4);
        trip7.setRider(rider3);
        trip8.setRider(rider2);
        trip9.setRider(rider1);
        trip10.setRider(rider2);
        trip11.setRider(rider4);
        trip12.setRider(rider4);
        trip13.setRider(rider4);
        trip14.setRider(rider4);
        trip15.setRider(rider2);
        trip17.setRider(rider3);


        List<ITrip> tripsRider1 = new LinkedList<>();
        tripsRider1.add(trip1);
        tripsRider1.add(trip5);
        tripsRider1.add(trip9);
        rider1.setTrips(tripsRider1);

        List<ITrip> tripsRider2 = new LinkedList<>();
        tripsRider2.add(trip2);
        tripsRider2.add(trip4);
        tripsRider2.add(trip8);
        tripsRider2.add(trip10);
        rider2.setTrips(tripsRider2);

        List<ITrip> tripsRider3 = new LinkedList<>();
        tripsRider3.add(trip3);
        tripsRider3.add(trip7);
        tripsRider3.add(trip17);
        rider3.setTrips(tripsRider3);

        List<ITrip> tripsRider4 = new LinkedList<>();
        tripsRider4.add(trip6);
        rider4.setTrips(tripsRider4);

        trip1.setMatch(match1);
        trip2.setMatch(match2);
        trip3.setMatch(match3);
        trip4.setMatch(match4);
        trip5.setMatch(match5);
        trip7.setMatch(match7);
        trip9.setMatch(match9);
        trip17.setMatch(match17);

        match1.setTrip(trip1);
        match2.setTrip(trip2);
        match3.setTrip(trip3);
        match4.setTrip(trip4);
        match5.setTrip(trip5);
        match7.setTrip(trip7);
        match9.setTrip(trip9);
        match17.setTrip(trip17);

        // TripReceipts
        ITripReceipt tripReceipt1 = modelFactory.createTripReceipt();
        ITripReceipt tripReceipt2 = modelFactory.createTripReceipt();
        ITripReceipt tripReceipt3 = modelFactory.createTripReceipt();
        ITripReceipt tripReceipt4 = modelFactory.createTripReceipt();
        ITripReceipt tripReceipt5 = modelFactory.createTripReceipt();
        ITripReceipt tripReceipt17 = modelFactory.createTripReceipt();

        tripReceipt1.setTotal(total1);
        tripReceipt2.setTotal(total2);
        tripReceipt3.setTotal(total3);
        tripReceipt4.setTotal(total4);
        tripReceipt5.setTotal(total5);
        tripReceipt17.setTotal(total17);

        tripReceipt1.setTip(tip1);
        tripReceipt2.setTip(tip2);
        tripReceipt3.setTip(tip3);
        tripReceipt4.setTip(tip4);
        tripReceipt5.setTip(tip5);
        tripReceipt17.setTip(tip17);

        tripReceipt1.setPaid(TRIP_RECEIPT_PAID_1);
        tripReceipt2.setPaid(TRIP_RECEIPT_PAID_2);
        tripReceipt3.setPaid(TRIP_RECEIPT_PAID_3);
        tripReceipt4.setPaid(TRIP_RECEIPT_PAID_4);
        tripReceipt5.setPaid(TRIP_RECEIPT_PAID_5);
        tripReceipt17.setPaid(TRIP_RECEIPT_PAID_17);

        tripReceipt1.setPaymentInfo(paymentInfo1);
        tripReceipt2.setPaymentInfo(paymentInfo2);
        tripReceipt3.setPaymentInfo(paymentInfo3);
        tripReceipt4.setPaymentInfo(paymentInfo4);
        tripReceipt5.setPaymentInfo(paymentInfo4);
        tripReceipt17.setPaymentInfo(paymentInfo5);

        //TripInfos
        ITripInfo tripInfo1 = modelFactory.createTripInfo();
        ITripInfo tripInfo2 = modelFactory.createTripInfo();
        ITripInfo tripInfo3 = modelFactory.createTripInfo();
        ITripInfo tripInfo4 = modelFactory.createTripInfo();
        ITripInfo tripInfo5 = modelFactory.createTripInfo();
        ITripInfo tripInfo17 = modelFactory.createTripInfo();

        tripReceipt1.setTripInfo(tripInfo1);
        tripReceipt2.setTripInfo(tripInfo2);
        tripReceipt3.setTripInfo(tripInfo3);
        tripReceipt4.setTripInfo(tripInfo4);
        tripReceipt5.setTripInfo(tripInfo5);
        tripReceipt17.setTripInfo(tripInfo17);

        LocalDate now = LocalDate.now();
        tripInfo1.setCompleted(localDateToDate(now.minusDays(10)));
        tripInfo2.setCompleted(localDateToDate(now.minusDays(1)));
        tripInfo3.setCompleted(localDateToDate(now.minusDays(20)));
        tripInfo4.setCompleted(localDateToDate(now.minusDays(29)));
        tripInfo5.setCompleted(localDateToDate(now.minusDays(30)));
        tripInfo17.setCompleted(localDateToDate(now.minusDays(31)));

        tripInfo1.setDistance(TRIP_1_DISTANCE);
        tripInfo2.setDistance(TRIP_2_DISTANCE);
        tripInfo3.setDistance(TRIP_3_DISTANCE);
        tripInfo4.setDistance(TRIP_4_DISTANCE);
        tripInfo5.setDistance(TRIP_5_DISTANCE);
        tripInfo17.setDistance(TRIP_17_DISTANCE);

        tripInfo1.setDriverRating(4);
        tripInfo2.setDriverRating(3);
        tripInfo3.setDriverRating(2);
        tripInfo4.setDriverRating(1);
        tripInfo5.setDriverRating(6);
        tripInfo17.setDriverRating(6);

        tripInfo1.setRiderRating(2);
        tripInfo2.setRiderRating(5);
        tripInfo3.setRiderRating(3);
        tripInfo4.setRiderRating(6);
        tripInfo5.setRiderRating(1);
        tripInfo17.setRiderRating(1);

        tripInfo1.setTrip(trip1);
        tripInfo2.setTrip(trip2);
        tripInfo3.setTrip(trip3);
        tripInfo4.setTrip(trip4);
        tripInfo5.setTrip(trip5);
        tripInfo17.setTrip(trip17);

        tripInfo1.setReceipt(tripReceipt1);
        tripInfo2.setReceipt(tripReceipt2);
        tripInfo3.setReceipt(tripReceipt3);
        tripInfo4.setReceipt(tripReceipt4);
        tripInfo5.setReceipt(tripReceipt5);
        tripInfo17.setReceipt(tripReceipt17);

        trip1.setTripInfo(tripInfo1);
        trip2.setTripInfo(tripInfo2);
        trip3.setTripInfo(tripInfo3);
        trip4.setTripInfo(tripInfo4);
        trip5.setTripInfo(tripInfo5);
        trip17.setTripInfo(tripInfo17);

        em.persist(location1);
        em.persist(location2);
        em.persist(location3);
        em.persist(location4);
        em.persist(location5);
        em.persist(rider1);
        em.persist(rider2);
        em.persist(rider3);
        em.persist(rider4);
        em.persist(paymentInfo1);
        em.persist(paymentInfo2);
        em.persist(paymentInfo3);
        em.persist(paymentInfo4);
        em.persist(paymentInfo5);
        em.persist(vehicle1);
        em.persist(vehicle2);
        em.persist(vehicle3);
        em.persist(vehicle4);
        em.persist(organization1);
        em.persist(organization2);
        em.persist(organization3);
        em.persist(organization4);
        em.persist(organization5);
        em.persist(driver1);
        em.persist(driver2);
        em.persist(driver3);
        em.persist(driver4);
        em.persist(driver5);
        em.persist(employment1);
        em.persist(employment2);
        em.persist(employment3);
        em.persist(employment4);
        em.persist(employment5);
        em.persist(employment6);
        em.persist(employment7);
        em.persist(employment8);
        em.persist(employment9);
        em.persist(employment10);
        em.persist(employment11);
        em.persist(employment12);
        em.persist(employment13);
        em.persist(employment14);
        em.persist(trip1);
        em.persist(trip2);
        em.persist(trip3);
        em.persist(trip4);
        em.persist(trip5);
        em.persist(trip6);
        em.persist(trip7);
        em.persist(trip8);
        em.persist(trip9);
        em.persist(trip10);
        em.persist(trip11);
        em.persist(trip12);
        em.persist(trip13);
        em.persist(trip14);
        em.persist(trip15);
        em.persist(trip16);
        em.persist(trip17);
        em.persist(match1);
        em.persist(match2);
        em.persist(match3);
        em.persist(match4);
        em.persist(match5);
        em.persist(match7);
        em.persist(match9);
        em.persist(match17);
        em.persist(tripReceipt1);
        em.persist(tripReceipt2);
        em.persist(tripReceipt3);
        em.persist(tripReceipt4);
        em.persist(tripReceipt5);
        em.persist(tripReceipt17);
        em.persist(tripInfo1);
        em.persist(tripInfo2);
        em.persist(tripInfo3);
        em.persist(tripInfo4);
        em.persist(tripInfo5);
        em.persist(tripInfo17);


        trip6.setCreated(TRIP_6_CREATED);
        trip11.setCreated(TRIP_11_CREATED);
        trip12.setCreated(TRIP_12_CREATED);
        trip13.setCreated(TRIP_13_CREATED);
        trip14.setCreated(TRIP_14_CREATED);
        trip15.setCreated(TRIP_15_CREATED);
        trip16.setCreated(TRIP_16_CREATED);

        rider1Id = rider1.getId();
        rider2Id = rider2.getId();
        rider3Id = rider3.getId();
        rider4Id = rider4.getId();
        paymentInfo1Id = paymentInfo1.getId();
        paymentInfo2Id = paymentInfo2.getId();
        paymentInfo3Id = paymentInfo3.getId();
        paymentInfo4Id = paymentInfo4.getId();
        paymentInfo5Id = paymentInfo5.getId();
        vehicle1Id = vehicle1.getId();
        vehicle2Id = vehicle2.getId();
        vehicle3Id = vehicle3.getId();
        vehicle4Id = vehicle4.getId();
        organization1Id = organization1.getId();
        organization2Id = organization2.getId();
        organization3Id = organization3.getId();
        organization4Id = organization4.getId();
        organization5Id = organization5.getId();
        driver1Id = driver1.getId();
        driver2Id = driver2.getId();
        driver3Id = driver3.getId();
        driver4Id = driver4.getId();
        employmentId1 = employment1.getId();
        employmentId2 = employment2.getId();
        employmentId3 = employment3.getId();
        employmentId4 = employment4.getId();
        employmentId5 = employment5.getId();
        match1Id = match1.getId();
        match2Id = match2.getId();
        match3Id = match3.getId();
        match4Id = match4.getId();
        match5Id = match5.getId();
        match7Id = match7.getId();
        match9Id = match9.getId();
        match17Id = match9.getId();
        location1Id = location1.getId();
        location2Id = location2.getId();
        location3Id = location3.getId();
        location4Id = location4.getId();
        location5Id = location5.getId();
        trip1Id = trip1.getId();
        trip2Id = trip2.getId();
        trip3Id = trip3.getId();
        trip4Id = trip4.getId();
        trip5Id = trip5.getId();
        trip6Id = trip6.getId();
        trip7Id = trip7.getId();
        trip8Id = trip8.getId();
        trip9Id = trip9.getId();
        trip10Id = trip10.getId();
        trip11Id = trip11.getId();
        trip12Id = trip12.getId();
        trip13Id = trip13.getId();
        trip14Id = trip14.getId();
        trip15Id = trip15.getId();
        trip16Id = trip16.getId();
        trip17Id = trip17.getId();
        tripInfo1Id = tripInfo1.getId();
        tripInfo2Id = tripInfo2.getId();
        tripInfo3Id = tripInfo3.getId();
        tripInfo4Id = tripInfo4.getId();
        tripInfo5Id = tripInfo5.getId();
        tripInfo17Id = tripInfo5.getId();
        tripReceipt1Id = tripReceipt1.getId();
        tripReceipt2Id = tripReceipt2.getId();
        tripReceipt3Id = tripReceipt3.getId();
        tripReceipt4Id = tripReceipt4.getId();
        tripReceipt5Id = tripReceipt5.getId();
        tripReceipt17Id = tripReceipt17.getId();

        em.flush();
    }

    private MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
