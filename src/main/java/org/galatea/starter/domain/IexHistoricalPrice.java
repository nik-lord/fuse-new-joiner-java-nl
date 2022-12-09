package org.galatea.starter.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Data
@Builder
@Table
public class IexHistoricalPrice {
  @PrimaryKey
  private String uniqueid;
  private String symbol;
  private LocalDate date;
  private BigDecimal open;
  private BigDecimal close;
  private BigDecimal high;
  private BigDecimal low;
  private BigDecimal volume;


  public String getUniqueid() {
    return symbol+date;
  }
}