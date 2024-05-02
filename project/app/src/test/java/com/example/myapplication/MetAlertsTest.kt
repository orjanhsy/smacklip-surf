package com.example.myapplication

import com.example.myapplication.data.smackLip.SmackLipRepository
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.surfareas.SurfArea
import kotlinx.coroutines.runBlocking
import org.junit.Test

class MetAlertsTest {


    val smackLipRepository: SmackLipRepository = SmackLipRepositoryImpl()


    @Test
    fun testAlertsApiCall()= runBlocking{
        print(smackLipRepository.getRelevantAlertsFor(SurfArea.HODDEVIK))
    }
}