package org.star.jaising.jarmony.ws;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhengjiaxing
 * @description 客户端开启WebSocket监听告警的参数
 * @date 2020/11/12
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AlarmArgs {

    /**
     * 告警相似度
     */
    private Double similarity;

    /**
     * 布控任务id列表
     */
    private List<String> controlIds;

    public static AlarmArgs init(Double similarity, List<String> controlIds) {
        return new AlarmArgs(similarity, controlIds);
    }

    public static AlarmArgs empty() {
        return new AlarmArgs(0D, Lists.newArrayList());
    }
}
