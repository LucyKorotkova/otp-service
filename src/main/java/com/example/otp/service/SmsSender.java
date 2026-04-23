package com.example.otp.service;

import org.jsmpp.bean.*;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Component
public class SmsSender {

    private static final Logger logger = LoggerFactory.getLogger(SmsSender.class);

    private final String host;
    private final int port;
    private final String systemId;
    private final String password;
    private final String systemType;
    private final String sourceAddress;

    public SmsSender() {
        try {
            Properties props = new Properties();
            InputStream input = getClass()
                    .getClassLoader()
                    .getResourceAsStream("sms.properties");

            props.load(input);

            host = props.getProperty("smpp.host");
            port = Integer.parseInt(props.getProperty("smpp.port"));
            systemId = props.getProperty("smpp.system_id");
            password = props.getProperty("smpp.password");
            systemType = props.getProperty("smpp.system_type");
            sourceAddress = props.getProperty("smpp.source_addr");

        } catch (Exception e) {
            throw new RuntimeException("Failed to load SMS config", e);
        }
    }

    public void send(String destination, String code) {

        SMPPSession session = new SMPPSession();

        try {

            BindParameter bindParameter = new BindParameter(
                    BindType.BIND_TX,
                    systemId,
                    password,
                    systemType,
                    TypeOfNumber.UNKNOWN,
                    NumberingPlanIndicator.UNKNOWN,
                    sourceAddress
            );

            session.connectAndBind(host, port, bindParameter);

            session.submitShortMessage(
                    systemType,
                    TypeOfNumber.UNKNOWN,
                    NumberingPlanIndicator.UNKNOWN,
                    sourceAddress,
                    TypeOfNumber.UNKNOWN,
                    NumberingPlanIndicator.UNKNOWN,
                    destination,
                    new ESMClass(),
                    (byte) 0,
                    (byte) 1,
                    null,
                    null,
                    new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
                    (byte) 0,
                    new GeneralDataCoding(Alphabet.ALPHA_DEFAULT),
                    (byte) 0,
                    ("Your OTP: " + code).getBytes(StandardCharsets.UTF_8)
            );

            logger.info("SMS sent successfully to {}", destination);

        } catch (Exception e) {
            logger.warn("SMPP server not available: {}", e.getMessage());
        } finally {
            try {
                session.unbindAndClose();
            } catch (Exception ignored) {
            }
        }
    }
}