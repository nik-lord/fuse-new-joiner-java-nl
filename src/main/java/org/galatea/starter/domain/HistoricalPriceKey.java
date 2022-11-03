package org.galatea.starter.domain;

import java.io.Serializable;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;

@PrimaryKeyClass
public class HistoricalPriceKey implements Serializable {
}
