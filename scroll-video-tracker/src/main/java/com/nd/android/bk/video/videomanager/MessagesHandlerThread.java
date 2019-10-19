package com.nd.android.bk.video.videomanager;

import com.nd.android.bk.video.utils.Logger;
import com.nd.android.bk.video.videomanager.message.Message;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 处理播放器消息的线程，通过一个单一线程的线程池来处理从消息队列中获取的消息
 * @author JiaoYun
 * @date 2019/10/14 22:11
 */
public class MessagesHandlerThread {
    private static final String TAG = MessagesHandlerThread.class.getSimpleName();

    private final Queue<Message> mPlayerMessagesQueue = new ConcurrentLinkedQueue<>();
    private final PlayerQueueLock mQueueLock = new PlayerQueueLock();
    private final Executor mQueueProcessingThread = Executors.newSingleThreadExecutor();
    private AtomicBoolean mTerminated = new AtomicBoolean(false);
    private Message mLastMessage;

    public MessagesHandlerThread() {
        mQueueProcessingThread.execute(new Runnable() {
            @Override
            public void run() {

                Logger.v(TAG, "start worker thread");
                do {

                    mQueueLock.lock(TAG);
                    Logger.v(TAG, "mPlayerMessagesQueue " + mPlayerMessagesQueue);

                    if (mPlayerMessagesQueue.isEmpty()) {
                        try {
                            Logger.v(TAG, "queue 为空，等待新的message");

                            mQueueLock.wait(TAG);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            throw new RuntimeException("InterruptedException");
                        }
                    }

                    mLastMessage = mPlayerMessagesQueue.poll();

                    if (mLastMessage == null) break;
                    mLastMessage.polledFromQueue();
                    Logger.v(TAG, "poll mLastMessage " + mLastMessage);
                    mQueueLock.unlock(TAG);

                    Logger.v(TAG, "run, mLastMessage " + mLastMessage);
                    mLastMessage.runMessage();

                    mQueueLock.lock(TAG);
                    mLastMessage.messageFinished();
                    mQueueLock.unlock(TAG);

                } while (!mTerminated.get());

            }
        });
    }

    /**
     * 添加单个消息
     */
    public void addMessage(Message message) {

        Logger.v(TAG, ">> addMessage, lock " + message);
        mQueueLock.lock(TAG);

        mPlayerMessagesQueue.add(message);
        mQueueLock.notify(TAG);

        Logger.v(TAG, "<< addMessage, unlock " + message);
        mQueueLock.unlock(TAG);
    }

    /**
     * 添加多个消息
     */
    public void addMessages(List<? extends Message> messages) {
        Logger.v(TAG, ">> addMessages, lock " + messages);
        mQueueLock.lock(TAG);

        mPlayerMessagesQueue.addAll(messages);
        mQueueLock.notify(TAG);

        Logger.v(TAG, "<< addMessages, unlock " + messages);
        mQueueLock.unlock(TAG);
    }

    public void pauseQueueProcessing(String outer) {
        Logger.v(TAG, "pauseQueueProcessing, lock " + mQueueLock);
        mQueueLock.lock(outer);
    }

    public void resumeQueueProcessing(String outer) {
        Logger.v(TAG, "resumeQueueProcessing, unlock " + mQueueLock);
        mQueueLock.unlock(outer);
    }

    public void clearAllPendingMessages(String outer) {
        Logger.v(TAG, ">> clearAllPendingMessages, mPlayerMessagesQueue " + mPlayerMessagesQueue);

        if (mQueueLock.isLocked(outer)) {
            mPlayerMessagesQueue.clear();
        } else {
            throw new RuntimeException("不能执行 action, 未持有lock");
        }
        Logger.v(TAG, "<< clearAllPendingMessages, mPlayerMessagesQueue " + mPlayerMessagesQueue);
    }

    public void terminate() {
        mTerminated.set(true);
    }
}
