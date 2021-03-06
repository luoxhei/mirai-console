/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package net.mamoe.mirai.console.command

import net.mamoe.mirai.console.command.internal.isValidSubName
import net.mamoe.mirai.message.data.SingleMessage

/**
 * 指令
 * 通常情况下, 你的指令应继承 @see CompositeCommand/SimpleCommand
 * @see register 注册这个指令
 *
 * @see RawCommand
 * @see CompositeCommand
 */
interface Command {
    /**
     * 指令名. 需要至少有一个元素. 所有元素都不能带有空格
     */
    val names: Array<out String>

    val usage: String

    val description: String

    /**
     * 指令权限
     */
    val permission: CommandPermission

    /**
     * 为 `true` 时表示 [指令前缀][CommandPrefix] 可选
     */
    val prefixOptional: Boolean

    val owner: CommandOwner

    /**
     * @param args 指令参数. 可能是 [SingleMessage] 或 [String]. 且已经以 ' ' 分割.
     */ // TODO: 2020/6/28 Java-friendly bridges
    suspend fun CommandSender.onCommand(args: Array<out Any>)
}

/**
 * [Command] 的基础实现
 */
abstract class AbstractCommand @JvmOverloads constructor(
    final override val owner: CommandOwner,
    vararg names: String,
    description: String = "<no description available>",
    final override val permission: CommandPermission = CommandPermission.Default,
    final override val prefixOptional: Boolean = false
) : Command {
    final override val description = description.trimIndent()
    final override val names: Array<out String> =
        names.map(String::trim).filterNot(String::isEmpty).map(String::toLowerCase).also { list ->
            list.firstOrNull { !it.isValidSubName() }?.let { error("Invalid name: $it") }
        }.toTypedArray()

}

suspend inline fun Command.onCommand(sender: CommandSender, args: Array<out Any>) = sender.run { onCommand(args) }

/**
 * 主要指令名. 为 [Command.names] 的第一个元素.
 */
val Command.primaryName: String get() = names[0]