package org.galatea.starter.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.galatea.starter.domain.IexHistoricalPrice;
import org.galatea.starter.domain.IexLastTradedPrice;
import org.galatea.starter.domain.IexSymbol;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A Feign Declarative REST Client to access endpoints from the Free and Open IEX API to get market
 * data. See https://iextrading.com/developer/docs/
 */
@FeignClient(name = "IEX", url = "${spring.rest.iexBasePath}")
public interface IexClient {
  String TOKEN = "${spring.rest.token}";
  /**
   * Get a list of all stocks supported by IEX. See https://iextrading.com/developer/docs/#symbols.
   * As of July 2019 this returns almost 9,000 symbols, so maybe don't call it in a loop.
   *
   * @return a list of all of the stock symbols supported by IEX.
   */
  @GetMapping("/ref-data/symbols?token=" + TOKEN)
  List<IexSymbol> getAllSymbols();

  /**
   * Get the last traded price for each stock symbol passed in. See https://iextrading.com/developer/docs/#last.
   *
   * @param symbols stock symbols to get last traded price for.
   * @return a list of the last traded price for each of the symbols passed in.
   */
  @GetMapping("/tops/last?symbols={symbols}&token="+TOKEN)
  List<IexLastTradedPrice> getLastTradedPriceForSymbols(@PathVariable String[] symbols);

//  @GetMapping(path = "/stock/{symbol}/chart?token="+TOKEN, params = {"symbol"})
//  IexHistoricalPrice getHistoricalPriceForSymbol(@PathVariable String symbol);

  @GetMapping(path = "/stock/{symbol}/chart/{range}?token="+TOKEN, params = {"symbol", "range"} )
  List<IexHistoricalPrice> getHistoricalPriceForRange(@PathVariable String symbol, @PathVariable String range);

  @GetMapping(path = "/stock/{symbol}/chart/date/{date}?chartByDay=true&token="+TOKEN, params = {"symbol", "date"} )
  List<IexHistoricalPrice> getHistoricalPriceForDate(@PathVariable String symbol, @PathVariable String date);

  default List<IexHistoricalPrice> getHistoricalPriceForDate(String symbol, LocalDate date){
    return getHistoricalPriceForDate(symbol, date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
  }
}
