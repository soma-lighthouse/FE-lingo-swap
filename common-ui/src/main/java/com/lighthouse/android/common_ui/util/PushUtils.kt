package com.lighthouse.android.common_ui.util

import com.sendbird.android.handler.PushRequestCompleteHandler
import com.sendbird.android.push.SendbirdPushHandler
import com.sendbird.android.push.SendbirdPushHelper

object PushUtils {
    fun registerPushHandler(handler: SendbirdPushHandler) {
        SendbirdPushHelper.registerPushHandler(handler)
    }

    fun unRegisterPushHandler(listener: PushRequestCompleteHandler) {
        SendbirdPushHelper.unregisterPushHandler(listener)
    }
}