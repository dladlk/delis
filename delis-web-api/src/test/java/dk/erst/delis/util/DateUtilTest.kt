package dk.erst.delis.util

import org.apache.commons.lang3.time.DateUtils

import org.junit.Test

import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

import org.junit.Assert.assertEquals
import org.junit.runner.RunWith

import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
class DateUtilTest {

    @Test
    fun test() {
        val now = Date()
        val oneHourLaterExpected = Date(now.time + TimeUnit.HOURS.toMillis(1))
        val oneDayLaterExpected = Date(now.time + TimeUnit.DAYS.toMillis(1))
        val oneHourLaterActual = DateUtil.addHour(now, 1)
        assertEquals(oneHourLaterExpected, oneHourLaterActual)

        val oneDayLaterActual = DateUtil.addDay(now, 1)
        assertEquals(oneDayLaterExpected, oneDayLaterActual)

        val minutes = DateUtil.getMinutesBetween(now, oneHourLaterExpected)
        assertEquals(minutes, 60)

        val expectedBeginOfDay = DateUtils.truncate(now, Calendar.DATE)
        val actualBeginOfDay = DateUtil.generateBeginningOfDay(now)
        assertEquals(expectedBeginOfDay, actualBeginOfDay)
    }

    @Test
    fun testTimeZone() {

        val zaporozhyeTimeZone = "Europe/Zaporozhye"

        var date = DateUtil.convertClientTimeToServerTime(zaporozhyeTimeZone, Date(), true)
        println("date Zaporozhye = $date")

        val copenhagenTimeZone = "Europe/Copenhagen"

        date = DateUtil.convertClientTimeToServerTime(copenhagenTimeZone, Date(), true)
        println("date Copenhagen = $date")

        val kualaLumpurTimeZone = "Asia/Kuala_Lumpur"

        date = DateUtil.convertClientTimeToServerTime(kualaLumpurTimeZone, Date(), true)
        println("date Kuala Lumpur = $date")

        val losAngelesTimeZone = "America/Los_Angeles"

        date = DateUtil.convertClientTimeToServerTime(losAngelesTimeZone, Date(), true)
        println("date Los Angeles = $date")
    }
}
