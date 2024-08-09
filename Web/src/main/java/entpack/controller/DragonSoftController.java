package entpack.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Record;
import entpack.api.DragonSoftApi;
import entpack.bean.MemberInfo;
import entpack.service.DragonSoftApiService;
import entpack.utils.DateUtil;
import entpack.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


public class DragonSoftController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger("DragonSoft");

	/**
	 * 返回游戏账号
	 * @param userName
	 * @return
	 */
    public String getAccount(String userName) {

        Record record = DragonSoftApiService.getUser(userName);
        if (record == null) {
            return null;
        }
        return record.get("account");
    }

    /**
     * 添加用户接口
     */

    /**
     * Create member Endpoint
     *
     * @param currency currency type
     * @param account account
     * @param password password
     */
    public void createMember(String currency, String account, String password, String memberId) {
        if (currency == null) {
            currency = "MYR";
        }
        renderJson(DragonSoftApi.getInstance(currency).createMember(account, password, memberId));
    }

    /**
     * Login into Game Endpoint
     *
     * @param currency currency type
     * @param gameId platform memberId
     * @param lang language
     * @param isDemo isDemo
     * @param backurl backurl
     * @param btn btn
     * @param extra extra
     * @param maxBet maxBet
     * @param minBet minBet
     * @param level level
     */
    public void obtainToken(String gameId,String lang,String currency, String backurl, String memberId,
                            boolean isDemo, String btn, String extra, int maxBet, int minBet, String level){

        if(currency==null){
            currency="MYR";
        }
        renderJson(DragonSoftApi.getInstance(currency).obtainToken(gameId,lang,backurl,isDemo,
                btn,extra,maxBet,minBet,level, memberId));
    }

    /**
     * Inquire Member Balance Endpoint
     *
     * @param currency currency type
     * @param memberId memberId
     */
    public void searchMember(String currency, String memberId) {
        if (currency == null) {
            currency = "MYR";
        }
        renderJson(DragonSoftApi.getInstance(currency).searchMember(memberId));

    }

    /**
     * Transfer Endpoint
     *
     * @param currency currency type
     * @param amount amount to add or withdraw
     * @param memberId memberId
     * @param operType transaction type( deposit : 1 , withdrawl : 0)
     */
    public void withdrawOrDeposit(String currency, String amount, String memberId, int operType) {
        if (currency == null) {
            currency = "MYR";
        }
        renderJson(DragonSoftApi.getInstance(currency).withdrawOrDeposit(amount, memberId, operType));
    }
    /**
     * Search game details
     *
     * @param currency currency type
     * @param startTime start time (time range)
     * @param endTime end time (time range)
     * @param index start page default: 0
     * @param limit inquire limit default: 1000 maximum: 5000
     */
    public void searchGame(String currency, String startTime, String endTime,int index, int limit) {
        if(currency == null) {
            currency = "MYR";
        }
        renderJson(DragonSoftApi.getInstance(currency).searchGame(startTime,endTime, index, limit));
    }

    /**
     * 修改用户信息
     */
    public void editUser(String currency, String userName, String oldPassWd, String passWd) {
        if (currency == null) {
            currency = "MYR";
        }

        renderJson(DragonSoftApi.getInstance(currency).editUser(userName, oldPassWd, passWd));
    }

    /**
     * 充值上下分接口
     */
    public void setScore(String currency, String userName, String txCode, Integer type, double scoreNum) {
        if (currency == null) {
            currency = "MYR";
        }

        if (type == null) {
            type = 0;
        }

        renderJson(DragonSoftApi.getInstance(currency).setScore(userName, txCode, type, scoreNum));
    }

    /**
     * 查询游戏余额
     */
    public void getGameBalance(String currency, String userName) {
        if (currency == null) {
            currency = "MYR";
        }
        renderJson(DragonSoftApi.getInstance(currency).getGameBalance(userName));
    }

    /**
     * 查玩家游戏记录
     *
     * @param pageIndex
     * @param userName
     * @param sDate
     * @param eDate
     * @return
     */
    public void gameLog(String currency, Integer pageIndex, String userName, Date sDate, Date eDate) {
        if (currency == null) {
            currency = "MYR";
        }

        renderJson(DragonSoftApi.getInstance(currency).gameLog(pageIndex, 100, userName, sDate, eDate));
    }

    /**
     * 提现到余额
     *
     * @param currency
     * @param memberId
     */
    public void withdraw2Balance(String currency, String memberId) {
        if (currency == null) {
            currency = "MYR";
        }

        String txnId = StringUtil.shortUUID();
        MemberInfo memberInfo = DragonSoftApiService.getMemberInfo(memberId);
        Ret result = DragonSoftApi.getInstance(currency).withdraw2Balance(txnId, memberId, memberInfo.getUserName());
        renderText("result:" + result.toJson());
    }

    /**
     * 下属玩家或代理报表
     *
     * @param currency
     * @param userName
     * @param sDate
     * @param eDate
     * @param type
     */
    public void agentTotalReport(String currency, String userName, String sDate, String eDate, String type) {
        if (currency == null) {
            currency = "MYR";
        }

        Date startDate = DateUtil.parse(sDate, "yyyy-MM-dd");
        Date endDate = DateUtil.parse(eDate, "yyyy-MM-dd");

        JSONObject result = DragonSoftApi.getInstance(currency).agentTotalReport(userName, startDate, endDate, type);
        renderText("result:" + result.toJSONString());
    }


    /**
     * 查询代理或玩家列表
     *
     * @param currency
     * @param action
     * @param userName
     * @param pageIndex
     */
    public void accountList(String currency, String action, String userName, Integer pageIndex) {
        if (currency == null) {
            currency = "MYR";
        }

        JSONObject result = DragonSoftApi.getInstance(currency).accountList(action, userName, pageIndex);
        renderText("result:" + result.toJSONString());
    }

    public void disable(String currency, String userName) {
        if (currency == null) {
            currency = "MYR";
        }

        JSONObject result = DragonSoftApi.getInstance(currency).disable(userName);
        renderText("result:" + result.toJSONString());
    }

    public void queryTicket(String currency, String date) {
        if (currency == null) {
            currency = "MYR";
        }

        if (date == null) {
            date = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        }

        Date sDate = DateUtil.parse(date, "yyyy-MM-dd");

        DragonSoftApi.getInstance(currency).queryMemberReport(sDate);
        renderText("result:ok");
    }
    public void apiMap() {
        renderText(JSON.toJSONString(DragonSoftApi.getApiMap()));
    }

}
