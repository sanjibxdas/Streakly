package com.streakly.data.repository

import com.streakly.data.local.dao.WaterLogDao
import com.streakly.data.local.entity.WaterLogEntity
import com.streakly.domain.model.WaterIntake
import com.streakly.domain.repository.WaterRepository
import com.streakly.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WaterRepositoryImpl @Inject constructor(
    private val waterLogDao: WaterLogDao
) : WaterRepository {

    override fun getTodayWater(): Flow<WaterIntake> {
        val today = DateUtils.getTodayIso()
        return waterLogDao.getWaterLogForDate(today).map { entity ->
            if (entity != null) {
                WaterIntake(
                    date = entity.date,
                    glassesCount = entity.glassesCount,
                    targetGlasses = 8,
                    lastUpdated = entity.lastUpdated
                )
            } else {
                WaterIntake(
                    date = today,
                    glassesCount = 0,
                    targetGlasses = 8,
                    lastUpdated = System.currentTimeMillis()
                )
            }
        }
    }

    override suspend fun addGlass() {
        val today = DateUtils.getTodayIso()
        val existing = waterLogDao.getWaterLogForDate(today).firstOrNull()
        val now = System.currentTimeMillis()
        if (existing == null) {
            waterLogDao.insertWaterLog(
                WaterLogEntity(
                    date = today,
                    glassesCount = 1,
                    lastUpdated = now
                )
            )
        } else {
            waterLogDao.updateWaterCount(today, existing.glassesCount + 1, now)
        }
    }

    override suspend fun removeGlass() {
        val today = DateUtils.getTodayIso()
        val existing = waterLogDao.getWaterLogForDate(today).firstOrNull() ?: return
        val now = System.currentTimeMillis()
        val newCount = (existing.glassesCount - 1).coerceAtLeast(0)
        waterLogDao.updateWaterCount(today, newCount, now)
    }

    override suspend fun setGlasses(count: Int) {
        val today = DateUtils.getTodayIso()
        val existing = waterLogDao.getWaterLogForDate(today).firstOrNull()
        val now = System.currentTimeMillis()
        val validCount = count.coerceAtLeast(0)
        if (existing == null) {
            waterLogDao.insertWaterLog(
                WaterLogEntity(
                    date = today,
                    glassesCount = validCount,
                    lastUpdated = now
                )
            )
        } else {
            waterLogDao.updateWaterCount(today, validCount, now)
        }
    }
}
