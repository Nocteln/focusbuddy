package com.nocteln.app.data.buddy


const val MAX_BUDDY_HEALTH: Int = 100
const val DEFAULT_BUDDY_NAME = "no name"

const val GIVE_UP_PENALTY = 15
const val END_SESSION_HEALTH_BONUS = 10

data class Buddy(
    val name: String = DEFAULT_BUDDY_NAME,
    val health: Int = MAX_BUDDY_HEALTH,
    val skin: String = "plant",
) {
    val mood: BuddyMood
        get() = BuddyMood.getMoodFromHealth(health)
}