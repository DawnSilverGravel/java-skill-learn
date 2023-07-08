package com.silvergravel.base.config;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;

/**
 * Description:
 *
 * @author DawnStar
 * Date: 2023/7/7
 */
public class MqttCallbackConfig implements MqttCallbackExtended {

    /**
     * @param reconnect If true, the connection was the result of automatic reconnect.
     * @param serverUri The server URI that the connection was made to.
     */
    @Override
    public void connectComplete(boolean reconnect, String serverUri) {
        System.out.println(serverUri);
        // reconnect进行自动重连
    }

    /**
     * 导致失去连接的原因
     *
     * @param cause the reason behind the loss of connection.
     */
    @Override
    public void connectionLost(Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // 这里处理接收的消息
        String prefix = "**************";
        String content = "\n普通Client：\n" + "topic: " + topic
                + "\nqos：" + message.getQos()
                + "\npayload：" + new String(message.getPayload())
                + "\nretained：" + message.isRetained() + "\n";
        System.out.println(prefix + content + prefix);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println(Arrays.toString(token.getTopics()));
    }
}
