package com.lighthouse.android.chats.uikit.channel

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.lighthouse.android.chats.databinding.CustomMessageInputBinding
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.message.BaseMessage
import com.sendbird.uikit.modules.components.MessageInputComponent
import com.sendbird.uikit.widgets.MessageInputView

class CustomMessageInputComponent : MessageInputComponent() {
    private lateinit var binding: CustomMessageInputBinding
    private var mode = MessageInputView.Mode.DEFAULT
    var cameraInput: View.OnClickListener? = null
    var voiceInput: View.OnClickListener? = null

    override fun onCreateView(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup,
        args: Bundle?,
    ): View {
        binding = CustomMessageInputBinding.inflate(inflater, parent, false)

        binding.ivSend.setOnClickListener {
            onInputRightButtonClicked(it)
        }

        binding.ivCamera.setOnClickListener {
            onInputLeftButtonClicked(it)
        }

        binding.ivMick.setOnClickListener {
            voiceInput?.onClick(it)
        }

        binding.ivCross.setOnClickListener {
            requestInputMode(MessageInputView.Mode.DEFAULT)
        }

        binding.ivQuestion.setOnClickListener {
            binding.rvQuestionPanel.apply {
                if (visibility == View.VISIBLE) {
                    setGone()
                    binding.ivQuestion.setColorFilter(
                        ContextCompat.getColor(
                            getContext(), com.lighthouse.android.common_ui.R.color.brown_grey
                        )
                    )
                } else {
                    setVisible()
                    binding.ivQuestion.setColorFilter(
                        ContextCompat.getColor(
                            getContext(), com.lighthouse.android.common_ui.R.color.main
                        )
                    )
                }
            }
        }

        binding.etMessageInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onInputTextChanged(s ?: " ", start, before, count)
            }

            override fun afterTextChanged(s: Editable?) {
                binding.etMessageInput.apply {
                    val lineCount = lineCount + 2
                    val maxLines = 5
                    val layoutParams = layoutParams
                    layoutParams.height = lineHeight * lineCount.coerceAtMost(maxLines) - 5
                    this.layoutParams = layoutParams

                }

                binding.ivSend.visibility = if (s?.isNotEmpty() != true) View.GONE else View.VISIBLE
            }
        })
        return binding.root
    }

    override fun getEditTextView(): EditText {
        return binding.etMessageInput
    }

    override fun getRootView(): View {
        return binding.root
    }


    override fun notifyDataChanged(
        message: BaseMessage?,
        channel: GroupChannel,
        defaultText: String,
    ) {
        super.notifyDataChanged(message, channel, defaultText)

        if (mode == MessageInputView.Mode.QUOTE_REPLY) {
            if (message != null) {
                binding.replyPanel.text = applyColor(message)
                binding.replyPanel.setVisible()
                binding.ivCross.setVisible()
            }
        } else if (mode == MessageInputView.Mode.EDIT) {
            if (message != null) {
                binding.etMessageInput.setText(message.message)

            }
        } else {
            binding.etMessageInput.setText(defaultText)
        }
    }

    override fun requestInputMode(mode: MessageInputView.Mode) {
        val before = this.mode
        this.mode = mode
        if (mode == MessageInputView.Mode.QUOTE_REPLY) {
            binding.ivSend.setOnClickListener(this::onInputRightButtonClicked)
        } else if (mode == MessageInputView.Mode.EDIT) {
            binding.ivSend.setOnClickListener(this::onEditModeSaveButtonClicked)
            binding.replyPanel.setGone()
            binding.ivCross.setGone()
        } else {
            binding.ivSend.setOnClickListener(this::onInputRightButtonClicked)
            binding.replyPanel.setGone()
            binding.replyPanel.setGone()
        }
        onInputModeChanged(before, mode)
    }


    private fun applyColor(message: BaseMessage): SpannableStringBuilder {
        val m = SpannableStringBuilder()

        val first = SpannableString("reply to ${message.sender?.nickname ?: ""}\n")
        first.setSpan(
            ForegroundColorSpan(Color.BLACK),
            0,
            first.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        m.append(first)

        val second = SpannableString(message.message)
        second.setSpan(
            ForegroundColorSpan(Color.parseColor("#939393")),
            0,
            second.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        m.append(second)
        return m
    }
}