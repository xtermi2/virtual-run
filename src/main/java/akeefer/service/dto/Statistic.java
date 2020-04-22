package akeefer.service.dto;

import akeefer.model.AktivitaetsTyp;
import akeefer.model.mongo.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;

public class Statistic {
    private final User user;
    private final Map<AktivitaetsTyp, BigDecimal> aggregated = new HashMap<>();
    private String mailString = null;

    public Statistic(User user) {
        this.user = user;
    }

    public Statistic add(AktivitaetsTyp typ, BigDecimal distanzInKm) {
        this.mailString = null;
        BigDecimal aggregatedDistanz = this.aggregated.get(typ);
        if (null == aggregatedDistanz) {
            this.aggregated.put(typ, distanzInKm);
        } else {
            this.aggregated.put(typ, aggregatedDistanz.add(distanzInKm));
        }
        return this;
    }

    public User getUser() {
        return user;
    }

    public Map<AktivitaetsTyp, BigDecimal> getAggregated() {
        return aggregated;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Statistic rhs = (Statistic) obj;
        return new EqualsBuilder()
                .append(this.user, rhs.user)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(user)
                .toHashCode();
    }

    public String toMailString() {
        if (null != mailString) {
            return mailString;
        }
        StringBuilder mail = new StringBuilder();
        if (!aggregated.isEmpty()) {
            mail.append(user.getAnzeigename())
                    .append(" ist ...").append(System.lineSeparator());
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
            otherSymbols.setDecimalSeparator(',');
            otherSymbols.setGroupingSeparator('.');
            DecimalFormat df = new DecimalFormat("#0.###", otherSymbols);
            for (Map.Entry<AktivitaetsTyp, BigDecimal> entry : aggregated.entrySet()) {
                BigDecimal distance = entry.getValue().setScale(3, RoundingMode.HALF_UP);
                mail.append("... ").append(df.format(distance)).append("km ").append(entry.getKey().toVergangenheit()).append(System.lineSeparator());
            }
        }
        mailString = mail.toString();
        return mailString;
    }
}
