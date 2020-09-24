package org.star.jaising.jarmony;

import org.apache.commons.codec.binary.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;
import org.star.jaising.jarmony.time.NextWorkingDayTemporal;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @author zhengjiaxing
 * @description
 * @date 2020/9/22
 */
public class TimeTests extends JArmonyApplicationTests {

    @Resource
    private NextWorkingDayTemporal nextWorkingDayTemporal;

    @Test
    public void testNextWorkingDay() {
        LocalDate now = LocalDate.now();
        LocalDate nextWorkingDayFromNow = now.with(nextWorkingDayTemporal);
        Assert.isTrue(StringUtils.equals(nextWorkingDayFromNow.toString(), "2020-09-23"), "nextWorkingDayFromNow wrong");

        LocalDate thisFriday = LocalDate.now().plusDays(3);
        LocalDate nextWorkingDayFromFriday = thisFriday.with(nextWorkingDayTemporal);
        Assert.isTrue(StringUtils.equals(nextWorkingDayFromFriday.toString(), "2020-09-28"), "nextWorkingDayFromFriday wrong");
    }
}
