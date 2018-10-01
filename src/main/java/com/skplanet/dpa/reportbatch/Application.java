package com.skplanet.dpa.reportbatch;

import com.skplanet.dpa.reportbatch.common.util.BatchSender;
import com.skplanet.dpa.reportbatch.common.util.PidUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@PropertySources({
        @PropertySource("file:config/task-schedules-${spring.profiles.active}.properties"),
        @PropertySource("file:config/reportbatch-${spring.profiles.active}.properties")
})
public class Application {
    private static BatchSender batchSender;

    @Autowired
    public Application(BatchSender batchSender){
        this.batchSender = batchSender;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        PidUtil.savePid();

        if(args != null && args.length == 2){
            String svcName = args[0];
            int addVal = Integer.parseInt(args[1]);
            log.info("svcName:" + svcName + ", addVal:" + addVal);
            if("hotclkad".equals(svcName)){
                batchSender.sendHotClkAd(addVal);
            }else if("sellerpcs".equals(svcName)){
                batchSender.sendSellerPcs(addVal);
            }else if("sdcmy11mon".equals(svcName)){
                batchSender.sendSDCMy11Mon(addVal);
            }else if("sdcmy11week".equals(svcName)){
                batchSender.sendSDCMy11Week(addVal);
            }else if("dex".equals(svcName)){
                batchSender.sendDexQaLog(addVal);
            }
        }
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }
}