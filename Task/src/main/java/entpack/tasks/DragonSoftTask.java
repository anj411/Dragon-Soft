package entpack.tasks;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Redis;
import entpack.api.DragonSoftApi;
import entpack.bean.MemberGameConfig;
import entpack.bean.RedisTicket;
import entpack.service.BaseService;
import entpack.service.CacheService;
import entpack.service.DragonSoftApiService;
import entpack.utils.DateUtil;
import entpack.utils.RedisLock;
import entpack.utils.TaskUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class DragonSoftTask implements StatefulJob {

    private static Logger logger = LoggerFactory.getLogger("dragonSoft_task");

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (TaskUtil.getStopStatus("DragonSoftTask")) {
            logger.info("DragonSoftTask.execute stop! Time:{} ", DateUtil.formatDate(new Date()));
            return;
        }

        String runTimeKey = "api:Task:dragonSoft:runTime";
        Redis.use().set(runTimeKey, DateUtil.formatDate(new Date()));

        try {
            Date now = new Date();
            for (DragonSoftApi value : DragonSoftApi.getApiMap().values()) {

                value.queryMemberReport(now);
            }

            doItem();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void doItem() {

        List<Record> recordList = DragonSoftApiService.queryTicket();

        for (Record record : recordList) {
            String api = record.getStr("api");
            String betId = record.getStr("uuid");
            String gameName = record.getStr("GameName");
            Date betTime = record.getDate("CreateTime");

            double payout = record.getDouble("Win");
            double betAmount = record.getDouble("bet");

            //赢输金额
            double amountWL = payout - betAmount;

            double turnover = Math.abs(betAmount);

            String account = record.getStr("account");

            Record user = DragonSoftApiService.getUserByAccount(account);
            String memberId = user.getStr("memberId");

            String key = "lock:dragonSoft::push:" + betId;

            //所有注单时间，使用当前接收时间
            //betTime = now;
            if (RedisLock.lock(key, 60 * 60)) {

                try {
                    Date now = new Date();

                    RedisTicket ticket = BaseService.buildTicket(api, betId, memberId, "SLOT",
                            "dragonSoft",
                            turnover,
                            betAmount, amountWL, 1, now, null);

                    if (ticket == null) {
                        //更新为失败
                        DragonSoftApiService.updateTicket(betId, -2, "not found gameId");
                        continue;
                    }

                    MemberGameConfig config = CacheService.getMemberConfig(memberId, ticket.getGameId());

                    JSONObject recordConfig = JSONObject.parseObject(JSONObject.toJSONString(config));
                    ticket.initConfig(new Record().setColumns(recordConfig));

                    //游戏名
                    ticket.setGameName(gameName);
                    ticket.setBetTime(DateUtil.formatDate(betTime));

                    logger.info("ticket:{}", JSON.toJSONString(ticket));
                    BaseService.pushTicket(ticket);
                } catch (Exception ex) {
                    //更新为失败
                    DragonSoftApiService.updateTicket(betId, -3, ex.getMessage());
                    ex.printStackTrace();
                    logger.error("betId:" + betId + ".buildTicket.error:", ex);
                    continue;
                }

                //update success
                DragonSoftApiService.updateTicket(betId);
            } else {
                logger.info("key.locked key:{}", key);
            }
        }
    }
}
