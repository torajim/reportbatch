package com.skplanet.dpa.reportbatch.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Settings {
    @Value("${slack.webhook.dmfc}")
    public String SLACK_ENDPOINT_DMFC;

    @Value("${slack.webhook.batchdev}")
    public String SLACK_ENDPOINT_BATCHDEV;

    @Value("${slack.webhook.delivery_estimation}")
    public String SLACK_ENDPOINT_SDC;

    @Value("${slack.webhook.dmfcso}")
    public String SLACK_ENDPOINT_DMFCSO;

    @Value("${slack.bot.token}")
    public String SLACK_BOT_TOKEN;

    @Value("${slack.bot.dmfcso}")
    public String SLACK_BOT_DMFCSO;

    @Value("${splunk.send.enable}")
    public boolean SPLUNK_SEND_ENABLE;
}
