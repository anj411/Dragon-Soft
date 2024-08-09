package entpack.api;

import entpack.Constant;

import java.util.HashMap;
import java.util.Map;

public class DragonSoftApi extends DragonSoftBaseApi {

    private static Map<String, DragonSoftApi> apiMap = new HashMap<>();

    private final static Map<String, String> agentIdMap = new HashMap<>();

    static {
        String def_Api = "dragonSoft";
        String open_currency = Constant.GameConfig.get(def_Api + ".open_currency");
        //初始化api
        for (String currency : open_currency.split(",")) {
            String currency_config = Constant.GameConfig.get(def_Api + ".currency." + currency);
            for (String config : currency_config.split("\\|")) {
                String[] configs = config.split(",");

                String agentId = configs[0];
                String channel = configs[1];
                String aes_key = configs[2];
                String sign_key = configs[3];
                apiMap.put(currency, new DragonSoftApi(currency, sign_key, aes_key, agentId, channel));

                if (currency.equals("HKD")) {
                    apiMap.put("HK", new DragonSoftApi(currency, sign_key, aes_key, agentId, channel));
                }
                agentIdMap.put(agentId, sign_key);
            }
        }
    }

    public DragonSoftApi(String currency, String sign_key, String aes_key, String agentId, String channel) {
        this.currency = currency;
        this.api_agent = agentId;
        this.sign_key = sign_key;
        this.aes_key = aes_key;
        this.channel = channel;
    }
    public static DragonSoftApi getInstance(String currency) {
        return apiMap.get(currency);
    }

    @Override
    public String getApi() {
        switch (currency) {
            case "HKD":
            case "THB":
            case "1MMK":
            case "1VND":
            case "SGD":
            case "JPY":
            case "1IDR":
            case "INR":
            case "PHP":
            default:
                return def_Api + currency.toLowerCase();
            case "MYR":
                return def_Api;
        }
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public String getSign_key() {
        return sign_key;
    }

    @Override
    public String getAes_key() {
        return aes_key;
    }

    @Override
    public String getApiAgent() {
        return api_agent;
    }

    public static Map<String, DragonSoftApi> getApiMap() {
        return apiMap;
    }
}
