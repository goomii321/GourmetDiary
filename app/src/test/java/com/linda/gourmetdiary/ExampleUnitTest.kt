package com.linda.gourmetdiary

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.profile.ProfileViewModel
import com.linda.gourmetdiary.util.TimeConverters
import org.junit.Assert
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @get:Rule
    var rule : TestRule = InstantTaskExecutorRule()
    @Mock
    lateinit var repository : DiaryRepository
    lateinit var pMVM: ProfileViewModel
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        pMVM = ProfileViewModel(repository)
    }
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun timeConverter() {
        val timeConverter = TimeConverters.timeStampToTime(1594946760000, Locale.TAIWAN)
        assertEquals(timeConverter,"2020/07/17 08:46")
    }
    @Test
    fun checkLocalTimeStart() {
        var startTime = pMVM.getTodayStartTime()
        assertEquals(startTime,1596384051428)
    }
    @Test
    fun checkLocalTimeEnd() {
        var endTime = pMVM.getTodayEndTime()
        assertEquals(endTime,1596988791428)
    }
    @Test
    fun checkDiaryResult() {
        var result = pMVM.getDiaryResult(pMVM.getTodayStartTime(),pMVM.getTodayEndTime())
        result
        assertTrue(pMVM.diaryList.value != null)
    }
    @Test
    fun calculateCost(){
        val cost = pMVM.getCost()
        cost
        assertTrue(pMVM.weeklyCost.value != null)
    }
}
