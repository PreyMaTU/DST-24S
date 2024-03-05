package dst.ass1.jpa.model;

public interface IPlatformUser {

    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    String getTel();

    void setTel(String tel);

    Double getAvgRating();

    void setAvgRating(Double avgRating);
}
