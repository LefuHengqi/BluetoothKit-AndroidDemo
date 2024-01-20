package com.lefu.ppblutoothkit.util.log

import android.content.Context
import com.lefu.ppblutoothkit.util.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

/**
 *    @author : whs
 *    e-mail : haisilen@163.com
 *    date   : 2023/7/29 8:46
 *    desc   : 用队列来处理日志的写入临时保存
 */
object MyQueueLinkedUtils {
    /**
     * LinkedBlockingQueue 是 Java 标准库中的一个线程安全的阻塞队列，
     * 它是基于链表实现的。性能方面，它的插入和删除操作具有 O(1) 的时间复杂度，相对较低，因此适用于高并发的生产者-消费者场景。
     */
    private val queue = LinkedBlockingQueue<LogVo>()

    private val parentJob = Job()

    data class LogVo(
        val messages: Array<out Any>,
        val type: String
    ) : java.io.Serializable

    /**
     * 启动队列查询
     */
    fun start(context: Context) {
        CoroutineScope(parentJob).launch(Dispatchers.IO) {
            while (true) {
                val value = MyQueueLinkedUtils.poll()
                if (parentJob.isCancelled) {
                    break
                }
                if (value != null) {
                    val path = "${context.applicationContext.filesDir}/Log/AppLog"
                    //创建所有日志最外层文件夹
                    LFLogFileUtils.creatFolder(path)
                    //创建指定类型日志的文件夹
                    LFLogFileUtils.creatFolder("${path}/${value.type}")
                    //日志文件
                    val file = LFLogFileUtils.creatLogFile("${path}/${value.type}", Date())
                    // 启动一个子协程，将日志写入文件中
                    try {
                        var raf: RandomAccessFile? = null
                        //以叠加的形式在日志文件中写入日志
                        raf = RandomAccessFile(file, "rw")
                        raf.seek(file.length())
                        val channel = raf.channel
                        channel.force(true)
                        val lock = channel.lock()
                        //日志写入时间,精确到秒
                        val date = Date()
                        val sdfLong = SimpleDateFormat(LogConstant.FORMAT_LONG, Locale.CHINA)
                        val curTime = sdfLong.format(date)
                        val byteBuffer =
                            ByteBuffer.wrap("${curTime} : ${LogUtils.getString(value.messages)}\n".toByteArray())
                        channel.write(byteBuffer)
                        lock.release()
                        channel.close()
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    fun stop() {
        parentJob.cancel()
    }

    //入队
    fun offer(buffer: LogVo) {
        queue.offer(buffer)
    }

    //出队
    fun poll(): LogVo? {
        return queue.take()
    }

    fun queueIsEmpty(): Boolean {
        return queue.isEmpty()
    }


}