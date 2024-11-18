<template>
    <div>
        <!-- 사이드바 컴포넌트 -->
        <ChatSidebar v-if="contacts" :contacts="contacts" :loginUserId="loginUserId" :unreadNotificationCount="unreadNotificationCount" @openChat="openChat" @closeSidebar="closeSidebar" />

        <!-- 채팅창 컴포넌트: activeChat이 설정될 때만 보임 -->
        <ChatToast v-if="activeChat" :chat="activeChat" :loginUserId="loginUserId" @sendMessage="sendMessage" @closeToast="closeToast" />
    </div>
</template>

<script>
import ChatSidebar from "@/components/layouts/ChatSidebar.vue";
import ChatToast from "@/components/layouts/ChatToast.vue";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

export default {
    components: { ChatSidebar, ChatToast },
    data() {
        return {
            contacts: [],
            activeChat: null, // activeChat이 설정되면 ChatToast가 나타남
            loginUserId: 0,
            otherUserId: 0,
            textMessage: '',
            lastMessage: '',
            messages: [],
            stompClient: null,
            headers: '',
            unreadNotificationCount: 0,
            isOpenSidebar: false,
        };
    },
    methods: {
        openSidebar() {
            // 이미 열려 있는 경우
            if (this.isOpenSidebar) {
                this.isOpenSidebar = false;
            } else {
                this.isOpenSidebar = true;
            }
        },
        goOrCreateChat(id) {
            this.$axios
                .get(`/api/chatrooms/getOrCreate`, {
                  params: {
                    user2Id: id,
                  },
                })
                .then((response) => {
                    this.$emit('openChat', response.data);
                })
                .catch((error) => {
                    console.log(error);
                });
        },
        setContacts() {
            this.$axios
                .get(`/api/chatrooms/findChatRoomsByUserId`)
                .then((response) => {
                    const chatRooms = response.data;

                    // 각 채팅방의 상대방 사용자 정보와 마지막 메시지를 동시에 가져오기 위해 Promise.all 사용
                    const contactPromises = chatRooms.map(room => {
                        // 상대방 사용자 ID 결정 (로그인 한 유저의 상대)
                        const otherUserId = this.loginUserId === room.user1Id ? room.user2Id : room.user1Id;

                        // 채팅방의 마지막 메시지를 가져옴
                        const lastMessageRequest = this.$axios.get(`/api/messages/getLastMessage`, {
                            params: { chatRoomId: room.id }
                        });

                        // 상대방 사용자 닉네임을 가져옴
                        const userInfoRequest = this.$axios.get(`/api/user/findUserById`, {
                            params: { id: otherUserId }
                        });

                        const notificationCountRequest = this.$axios.get(`/api/notifications/unread/${room.id}/${this.loginUserId}`);
                        // 요청이 완료된 후에 결과를 조합하여 반환
                        return Promise.all([lastMessageRequest, userInfoRequest, notificationCountRequest]).then(([lastMessageResponse, userResponse, notificationCountRequest]) => ({
                            id: userResponse.data.id,
                            chatRoomId: room.id,
                            nickname: userResponse.data.nickname,
                            lastMessage: lastMessageResponse.data.content || '', // 마지막 메시지가 없을 경우 빈 문자열로 설정
                            notificationCount: notificationCountRequest.data,
                        }));
                    });

                    // 모든 채팅방 정보를 contacts 배열에 저장
                    Promise.all(contactPromises).then(contacts => {
                        this.contacts = contacts;
                        // 전체 읽지 않은 메시지 수 계산
                        // contacts 배열에 있는 각 채팅방의 읽지 않은 메시지 수를 모두 더해 unreadNotificationCount에 저장
                        this.unreadNotificationCount = contacts.reduce((total, contact) => total + contact.notificationCount, 0);
                        console.log(this.unreadNotificationCount);
                    });
                })
                .catch((error) => {
                    console.log("Error fetching chatrooms:", error);
                });
        },
        async openChat(contact) {
            // 클릭한 연락처 정보를 activeChat에 설정하여 ChatToast가 열리게 함
            this.activeChat = {
                id: contact.id,
                chatRoomId: contact.chatRoomId,
                nickname: contact.nickname,
                messages: [],
            };

            // 해당 채팅방에서 유저가 받은 대화 모두 읽음 처리
            this.$axios.post(`/api/notifications/markAsRead/${this.activeChat.chatRoomId}/${this.loginUserId}`);

            this.setContacts();

            // 이전 대화 내용 불러오기
            try {
                const response = await this.$axios.get(`/api/messages/${this.activeChat.chatRoomId}`, {
                    params: { chatRoomId: this.activeChat.chatRoomId }
                });

                // 불러온 메시지 데이터를 activeChat.messages에 추가
                this.activeChat.messages = response.data.map(message => ({
                    ...message,
                    sender: message.senderId === this.loginUserId ? "me" : "other" // 메시지의 보낸 사람 구분
                }));
            } catch (error) {
                console.error("Error fetching messages:", error);
            }
        },
        closeToast() {
            // 채팅창 닫기: activeChat을 null로 설정하여 ChatToast를 숨김
            this.activeChat = null;
        },
        sendMessage(messageContent) {
            if (this.activeChat) {
                const message = {
                    chatRoomId: this.activeChat.chatRoomId,
                    senderId: this.loginUserId,
                    receiverId: this.activeChat.id,
                    content: messageContent.content,
                    timestamp: new Date().toLocaleTimeString(),
                };

                this.stompClient.send(`/app/user/messages/${message.receiverId}`, {}, JSON.stringify(message));
                this.activeChat.messages.push(
                    { ...message,
                      senderId: this.loginUserId,
                      receiverId: message.receiverId,
                      sender: "me",
                      timestamp: message.timestamp });
                this.setContacts();

            }
        },
        connect(loginUserId) {
            const socket = new SockJS(
                `http://${process.env.VUE_APP_IP}:${process.env.VUE_APP_PORT}/ws/chat`);
            this.stompClient = Stomp.over(socket);
            const token = localStorage.getItem('authToken');

            this.headers = {
              Authorization: `Bearer ${token}`, // JWT 토큰
            };

            this.stompClient.connect(
              this.headers,
              (frame) => {
                console.log("Connected: " + frame);

                // 구독 및 메시지 처리
                this.stompClient.subscribe(`/queue/user/${loginUserId}`, (message) => {
                  const parsedMessage = JSON.parse(message.body);

                  // senderId가 현재 로그인한 사용자의 ID와 다르고, 같은 채팅방이 켜져 있을 때
                  if (parsedMessage.senderId !== this.loginUserId) {
                      if (this.activeChat && parsedMessage.chatRoomId === this.activeChat.chatRoomId) {
                          this.activeChat.messages.push({ ...parsedMessage, sender: parsedMessage.senderId === this.loginUserId ? "me" : "other" });
                          // 채팅방 메시지 읽음 처리
                          this.$axios
                            .post(`/api/notifications/markAsRead/${this.activeChat.chatRoomId}/${this.loginUserId}`);
                      }
                  }

                  this.setContacts();

                });
              },
              (error) => {
                console.error("WebSocket connection error:", error);
              }
            );
        },
        async disconnect() {
            if (this.stompClient && this.stompClient.connected) {
                this.stompClient.disconnect(() => {
                    console.log("WebSocket connection closed");
                });
            }
        },
    },
    mounted() {
        this.$axios
            .get(`/api/user/findByLoginUserName`)
            .then((response) => {
                this.loginUserId = response.data.id;
                this.connect(this.loginUserId);
                this.setContacts();
            });
    },
    beforeUnmount() {
        this.disconnect();
    },
};
</script>
<style scope>
    .notification-side-bar-shape {
        width: 10px;
        height: 10px;
        background-color: #F65005;
        border-radius: 10px;
        position: absolute;
        top: 30%;
    }

    .sidebar-btn {
        width: 50px;
        height: 100px;
        z-index: 2;
    }

    .sidebar-div {
        position: relative;
    }

    .sidebar-div i {
        position: absolute;
        top: 50%;
        transform: translateY(-50%);
        right: 30%;
    }

    .notification-side-bar-count {
        font-size: 7px;
    }
</style>