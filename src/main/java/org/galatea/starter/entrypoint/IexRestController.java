package org.galatea.starter.entrypoint;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.domain.IexHistoricalPrice;
import org.galatea.starter.domain.IexLastTradedPrice;
import org.galatea.starter.domain.IexSymbol;
import org.galatea.starter.domain.rpsy.HistoricalPriceRpsy;
import org.galatea.starter.service.IexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
@Validated
@RestController
@RequiredArgsConstructor
public class IexRestController {
  @Autowired
  private HistoricalPriceRpsy historicalPriceRpsy;

  @NonNull
  private IexService iexService;

  private Set<String> rangeVals = new HashSet<>(Arrays.asList("max", "5y", "2y", "1y", "ytd", "6m",
          "3m", "1m", "1mm", "5d", "2d", "5dm", "date", "dynamic"));
  private Set<String> validRange = ImmutableSet.copyOf(rangeVals);

  private Clock clock = Clock.systemDefaultZone();

  /**
   * Exposes an endpoint to get all of the symbols available on IEX.
   *
   * @return a list of all IexStockSymbols.
   */
  @GetMapping(value = "${mvc.iex.getAllSymbolsPath}", produces = {MediaType.APPLICATION_JSON_VALUE})
  public List<IexSymbol> getAllStockSymbols() {
    return iexService.getAllSymbols();
  }

  /**
   * Get the last traded price for each of the symbols passed in.
   *
   * @param symbols list of symbols to get last traded price for.
   * @return a List of IexLastTradedPrice objects for the given symbols.
   */
  @GetMapping(value = "${mvc.iex.getLastTradedPricePath}", produces = {
      MediaType.APPLICATION_JSON_VALUE})
  public List<IexLastTradedPrice> getLastTradedPrice(
      @RequestParam(value = "symbols") final List<String> symbols) {
    return iexService.getLastTradedPriceForSymbols(symbols);
  }

  @GetMapping(value = "${mvc.iex.getHistoricalPricePath}",
          produces = {MediaType.APPLICATION_JSON_VALUE})
  public List<IexHistoricalPrice> getHistoricalPrice(
      @RequestParam(value = "symbol") final String symbol,
      @RequestParam(value = "date", required = false)
      @DateTimeFormat(pattern = "yyyyMMdd") final LocalDate date,
      @RequestParam(value = "range", required = false) final String range) {
    if (date != null) {
      if (isDateValid(date)) {
        if(historicalPriceRpsy.existsById(symbol+date)){
          List<IexHistoricalPrice> list = new ArrayList<IexHistoricalPrice>();
          list.add(historicalPriceRpsy.findById(symbol+date).get());
          return list;
        }
        List<IexHistoricalPrice> list = iexService.getHistoricalPriceForDate(symbol, date);
        System.out.println(list.get(0).getUniqueid());
        for (IexHistoricalPrice i : list){
          historicalPriceRpsy.save(i);
        }
        return list;
      }
      throw new IllegalArgumentException("Date is not valid");
    }

    if (!isRangeValid(range)) {
      throw new IllegalArgumentException("Range value is not valid");
    }

    return iexService.getHistoricalPriceForRange(symbol, range);


  }

  private boolean isDateValid(final LocalDate date) {
    if (date.isAfter(LocalDate.now(clock))) {
      return false;
    }
    if (date.isBefore(LocalDate.now(clock).minusYears(5))) {
      return false;
    }
    return true;
  }

  private boolean isRangeValid(final String range) {
    if (range == null) {
      return true;
    }
    return validRange.contains(range);
  }

}
