package info.blockchain.api.exchangerates;

import info.blockchain.api.APIException;
import info.blockchain.api.HttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This class reflects the functionality documented
 * at https://blockchain.info/api/exchange_rates_api. It allows users to fetch the latest
 * ticker data and convert amounts between BTC and fiat currencies.
 */
public class ExchangeRates
{
	/**
	 * Gets the price ticker from https://blockchain.info/ticker
	 * @return A map of currencies where the key is a 3-letter currency symbol and the
	 *  value is the `Currency` class
	 * @throws APIException If the server returns an error
	 * @throws IOException
	 */
	public static Map<String, Currency> getTicker()
			throws APIException, IOException
	{
		return getTicker(null);
	}
	
	/**
	 * Gets the price ticker from https://blockchain.info/ticker
	 * @param apiCode Blockchain.info API code (optional, nullable)
	 * @return A map of currencies where the key is a 3-letter currency symbol and the
	 *  value is the `Currency` class
	 * @throws APIException If the server returns an error
	 * @throws IOException
	 */
	public static Map<String, Currency> getTicker(String apiCode)
		throws APIException, IOException
	{
		Map<String, String> params = new HashMap<String, String>();
		if (apiCode != null)
			params.put("api_code", apiCode);
		
		String response = HttpClient.get("ticker", params);
		JsonObject ticker = new JsonParser().parse(response).getAsJsonObject();
		
		Map<String, Currency> resultMap = new HashMap<String, Currency>();
		for (Entry<String, JsonElement> ccyKVP : ticker.entrySet())
		{
			JsonObject ccy = ccyKVP.getValue().getAsJsonObject();
			Currency currency = new Currency(
					ccy.get("buy").getAsDouble(),
					ccy.get("sell").getAsDouble(),
					ccy.get("last").getAsDouble(),
					ccy.get("15m").getAsDouble(),
					ccy.get("symbol").getAsString()
					);
			
			resultMap.put(ccyKVP.getKey(), currency);
		}
		
		return resultMap;
	}
	
	/**
	 * Converts x value in the provided currency to BTC.
	 * @param currency Currency code
	 * @param value Value to convert
	 * @return Converted value in BTC
	 * @throws APIException If the server returns an error
	 * @throws IOException
	 */
	public static double toBTC(String currency, double value)
			throws APIException, IOException
	{
		return toBTC(currency, value, null);
	}
	
	/**
	 * Converts x value in the provided currency to BTC.
	 * @param currency Currency code
	 * @param value Value to convert
	 * @param apiCode Blockchain.info API code (optional, nullable)
	 * @return Converted value in BTC
	 * @throws APIException If the server returns an error
	 * @throws IOException
	 */
	public static double toBTC(String currency, double value, String apiCode)
			throws APIException, IOException
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("currency", currency);
		params.put("value", String.valueOf(value));
		if (apiCode != null)
			params.put("api_code", apiCode);
		
		String response = HttpClient.get("tobtc", params);
		return Double.parseDouble(response);
	}
}
