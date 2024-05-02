package com.example.myapplication

import com.example.myapplication.data.smackLip.Repository
import com.example.myapplication.data.smackLip.RepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Test

class StatefulRepoTests {

    val repo: Repository = RepositoryImpl()

    @Test
    fun testStatefulRepoOFLF() = runBlocking{
        repo.loadOFlF()
        val state = repo.ofLfNext7Days.value
        print(state)
    }

}