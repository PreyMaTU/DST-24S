package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IMoney;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class Money implements IMoney {

    static Money fromIMoney( IMoney other ) {
        if( other == null ) {
            return null;
        }
        final var money= new Money();
        money.setCurrency( other.getCurrency() );
        money.setCurrencyValue( money.getCurrencyValue() );
        return money;
    }
}
