package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IMoney;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class Money implements IMoney {

    static Money fromIMoney( IMoney money ) {
        return (Money) money;
    }

    private String currency;
    private BigDecimal currencyValue;

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public void setCurrency(String currency) {
        this.currency= currency;
    }

    @Override
    public BigDecimal getCurrencyValue() {
        return currencyValue;
    }

    @Override
    public void setCurrencyValue(BigDecimal currencyValue) {
        this.currencyValue= currencyValue;
    }
}
