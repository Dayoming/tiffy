<template>
    <div class="toast-container toast-chat d-flex gap-3 align-items-end">
        <div :class="['toast toast-chat show', isMobile ? 'mobile-toast-message' : '']" role="alert" aria-live="assertive" aria-atomic="true" data-bs-autohide="false"
        :style="isMobile ? '' : style">
            <div class="toast-header" :style="isMobile ? 'display: none' : ''">
                <img src="/icon/profile-default-icon.png" class="rounded-circle me-2" alt="Profile Image" width="24" height="24">
                <strong class="me-auto">{{ chat.nickname }}</strong>
                <small class="text-muted">Just now</small>
                <button v-if="!isMobile" type="button" class="btn-close ms-2 mb-1" @click="$emit('closeToast')"></button>
            </div>
            <div class="toast-body">
                <div :class="['chat-conversation-content custom-scrollbar h-200px',
                    isMobile ? 'mobile-toast-height' : '']" ref="chatContents">
                    <div v-for="message in chat.messages" :key="message.id" class="d-flex" :class="message.sender === 'me' ? 'justify-content-end text-end mb-1' : 'mb-1'">
                        <div class="w-100">
                            <div class="d-flex flex-column" :class="message.sender === 'me' ? 'align-items-end' : 'align-items-start'">
                                <div :class="message.sender === 'me' ? 'bg-primary text-white' : 'bg-light text-secondary'" class="p-2 px-3 rounded-2">{{ message.content }}</div>
                                <div class="small my-2">{{ message.timestamp }}</div>
                            </div>
                        </div>
                    </div>
                </div>
                <input class="form-control mb-sm-0 mb-3" placeholder="Type a message" rows="1" v-model="newMessage" @keyup.enter="sendMessage"/>
                <button class="btn btn-sm btn-primary ms-auto chat-send-btn" @click="sendMessage">Send</button>
            </div>
        </div>
    </div>
</template>

<script>

export default {
    props: {
        chat: Object,
        loginUserId: Number,
        style: Object,
    },
    data() {
        return {
            isMobile: false,
            newMessage: '',
        };
    },
    methods: {
        checkScreenSize() {
            this.isMobile = window.innerWidth <= 768;
        },
        sendMessage() {
            if (this.newMessage.trim()) {
                // 메시지 전송 후 DOM 업데이트를 기다린 다음 스크롤 이동
                this.$emit('sendMessage', { content: this.newMessage });
                this.newMessage = '';
                this.$nextTick(() => this.scrollToBottom());
            }
        },
        scrollToBottom() {
            const chatContents = this.$refs.chatContents;
            if (chatContents) {
                chatContents.scrollTop = chatContents.scrollHeight;
            }
        },
    },
    watch: {
        'chat.messages': {
            handler() {
                // messages 변경 시 DOM 업데이트 후 스크롤
                this.$nextTick(() => this.scrollToBottom());
            },
            deep: true, // messages 배열 내부의 변경을 감지
        },
    },
    mounted() {
        this.$nextTick(() => this.scrollToBottom());
        this.checkScreenSize();
    },
};
</script>

<style scoped>
    .chat-conversation-content {
        overflow-y: auto;
        height: 160px;
    }

    .toast-container {
        z-index: 1051;
    }

    .mobile-toast-message {
        border: unset;
        box-shadow: unset;
        bottom: 20px;
        right: 20px;
        width: 75%;
        left: 25%;
        top: 13%;
    }

    .mobile-toast-height {
        height: 400px;
    }
</style>