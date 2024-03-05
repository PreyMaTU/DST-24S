package dst.ass1.jooq.entity;

public interface IPreference {

    Long getId();

    void setId(Long id);

    Long getRiderId();

    void setRiderId(Long id);

    String getPrefKey();

    void setPrefKey(String prefKey);

    String getPrefValue();

    void setPrefValue(String prefValue);
}
