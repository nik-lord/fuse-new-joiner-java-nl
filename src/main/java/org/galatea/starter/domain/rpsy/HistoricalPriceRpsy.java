package org.galatea.starter.domain.rpsy;

import org.galatea.starter.domain.IexHistoricalPrice;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface HistoricalPriceRpsy extends CassandraRepository<IexHistoricalPrice, String> {
}
