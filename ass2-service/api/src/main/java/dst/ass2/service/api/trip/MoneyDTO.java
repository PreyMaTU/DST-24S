package dst.ass2.service.api.trip;

import java.math.BigDecimal;
import java.util.Objects;

public class MoneyDTO {

    private String currency;
    private BigDecimal value;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MoneyDTO moneyDTO = (MoneyDTO) o;
        return Objects.equals(getCurrency(), moneyDTO.getCurrency()) &&
            Objects.equals(getValue(), moneyDTO.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCurrency(), getValue());
    }

    @Override
    public String toString() {
        return "MoneyDTO{" +
            "currency='" + currency + '\'' +
            ", value=" + value +
            '}';
    }
}
