package com.lefu.ppblutoothkit.device.usermanage

import com.lefu.ppbase.vo.PPUserGender
import com.lefu.ppbase.vo.PPUserModel

/**
 * 用于测试的预设用户，与 iOS Demo 的 createUsers() 保持一致
 * 设备用户管理（Torre）和成员管理（Borre/Dorre）共用：
 * 点击“同步用户”时，按顺序把第一个设备上还不存在的预设用户同步到设备
 */
object PresetTestUsers {

    data class TestUser(
        val memberId: String,
        val userId: String,
        val name: String,
        val height: Int,
        val age: Int,
        val pIndex: Int
    )

    val users = listOf(
        TestUser("1234567890", "abcdefghijklmn", "Tom", 180, 28, 1),
        TestUser("m0000501", "20000002", "Mong", 175, 12, 2),
        TestUser("l0000503", "30000003", "Long", 175, 12, 3),
        TestUser("n0000504", "40000004", "Nike", 175, 12, 4),
        TestUser("s0000505", "50000005", "Ste", 180, 28, 5),
        TestUser("j0000501", "10000001", "Jack", 175, 12, 6)
    )

    /**
     * Torre：设备列表只有 UserId，按 userID 判断是否已存在
     */
    fun nextNotOnDeviceByUserId(deviceUserIds: Collection<String>): TestUser? =
        users.firstOrNull { it.userId !in deviceUserIds }

    /**
     * Borre/Dorre：设备列表是完整成员，按 userID + memberID 判断是否已存在
     */
    fun nextNotOnDeviceByMember(deviceMembers: Collection<DeviceMemberItem>): TestUser? =
        users.firstOrNull { user -> deviceMembers.none { it.userId == user.userId && it.memberId == user.memberId } }

    /**
     * 根据 userID + memberID 查找预设用户的名字，找不到返回 null
     */
    fun findName(userId: String, memberId: String): String? =
        users.firstOrNull { it.userId == userId && it.memberId == memberId }?.name

    /**
     * 每次都新建对象，避免 SDK 修改传入的用户
     */
    fun buildUserModel(user: TestUser): PPUserModel = PPUserModel.Builder().build().apply {
        memberID = user.memberId
        userID = user.userId
        userName = user.name
        isAthleteMode = false
        isPregnantMode = false
        userHeight = user.height
        age = user.age
        sex = PPUserGender.PPUserGenderFemale
        targetWeight = 60.0
        ideaWeight = 50.0
        //仅支持生日的设备会下发
        birthday = "1998-01-20" //yyyy-MM-dd
        PIndex = user.pIndex
        //设置设备端名字的字体大小，秤端支持显示名字的生效，单位：px
        nameFontSize = 18
    }
}
