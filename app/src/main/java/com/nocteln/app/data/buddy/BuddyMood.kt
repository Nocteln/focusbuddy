package com.nocteln.app.data.buddy


enum class BuddyMood(val minHealth: Int)
{
    THRIVING(80),
    HAPPY(60),
    NEUTRAL(40),
    SAD(20),
    NEGLECTED(0);

    companion object {
        fun getMoodFromHealth(health: Int): BuddyMood
        {
            return entries.first { it.minHealth <= health}
        }
    }
}
