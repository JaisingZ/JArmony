package org.star.jaising.jarmony.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.star.jaising.jarmony.util.JsonUtils;

import java.io.Serializable;

/**
 * @author zhengjiaxing
 * @description 告警信息返回结果
 * @date 2020/11/9
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AlarmResp implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 告警唯一标识
     */
    private String alarmId;

    /**
     * 任务id
     */
    private String controlId;

    /**
     * 任务名称
     */
    private String controlName;

    /**
     * 姓名
     */
    private String controlUser;

    /**
     * 相似度，精确到小数点后四位
     */
    private Double similarity;

    /**
     * 告警时间，yyyy-MM-dd HH:mm:ss
     */
    private String alarmTime;

    public AlarmResp(String alarmId) {
        this.alarmId = alarmId;
    }

    public static AlarmResp empty(String alarmId) {
        return new AlarmResp(alarmId);
    }

    public String serial() {
        return JsonUtils.serialize(this);
    }

}
