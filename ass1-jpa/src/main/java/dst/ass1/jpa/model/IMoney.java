package dst.ass1.jpa.model;

import java.math.BigDecimal;

public interface IMoney {

    String getCurrency();

    void setCurrency(String currency);

    BigDecimal getCurrencyValue();

    void setCurrencyValue(BigDecimal currencyValue);
}
