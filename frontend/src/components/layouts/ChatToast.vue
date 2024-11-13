<template>
    <div class="toast-container toast-chat d-flex gap-3 align-items-end">
        <div class="toast toast-chat show" role="alert" aria-live="assertive" aria-atomic="true" data-bs-autohide="false">
            <div class="toast-header">
                <img src="/icon/profile-default-icon.png" class="rounded-circle me-2" alt="Profile Image" width="24" height="24">
                <strong class="me-auto">{{ chat.nickname }}</strong>
                <small class="text-muted">Just now</small>
                <button type="button" class="btn-close ms-2 mb-1" @click="$emit('closeToast')"></button>
            </div>
            <div class="toast-body">
                <div class="chat-conversation-content custom-scrollbar h-200px">
                    <div v-for="message in chat.messages" :key="message.id" class="d-flex" :class="message.sender === 'me' ? 'justify-content-end text-end mb-1' : 'mb-1'">
                        <div class="w-100">
                            <div class="d-flex flex-column" :class="message.sender === 'me' ? 'align-items-end' : 'align-items-start'">
                                <div :class="message.sender === 'me' ? 'bg-primary text-white' : 'bg-light text-secondary'" class="p-2 px-3 rounded-2">{{ message.content }}</div>
                                <div class="small my-2">{{ message.timestamp }}</div>
                            </div>
                        </div>
                    </div>
                </div>
                <textarea class="form-control mb-sm-0 mb-3" placeholder="Type a message" rows="1" v-model="newMessage"></textarea>
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
    },
    data() {
        return {
            newMessage: '',
        };
    },
    methods: {
        sendMessage() {
            if (this.newMessage.trim()) {
                this.$emit('sendMessage', { content: this.newMessage });
                this.newMessage = '';
            }
        },
    },
};
</script>
