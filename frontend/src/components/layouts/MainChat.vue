<template>
    <div>
        <!-- 사이드바 버튼 -->
        <a class="icon-md btn btn-primary position-fixed end-0 bottom-0 mb-5" data-bs-toggle="offcanvas"
            href="#offcanvasChat" role="button" aria-controls="offcanvasChat"
            style="border-radius: 10px; z-index: 1;">
            <i class="bi bi-arrow-bar-left"></i>
        </a>

        <!-- 사이드바 컴포넌트 -->
        <ChatSidebar v-if="contacts" :contacts="contacts" @openChat="openChat" @closeSidebar="closeSidebar" />

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
        };
    },
    methods: {
        goOrCreateChat(id) {
            this.$axios
                .get(`http://localhost:8081/api/chatrooms/getOrCreate`, {
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
                .get(`http://localhost:8081/api/chatrooms/findChatRoomsByUserId`)
                .then((response) => {
                    const chatRooms = response.data;

                    // 각 채팅방의 상대방 사용자 정보와 마지막 메시지를 동시에 가져오기 위해 Promise.all 사용
                    const contactPromises = chatRooms.map(room => {
                        // 상대방 사용자 ID 결정
                        const otherUserId = this.loginUserId === room.user1Id ? room.user2Id : room.user1Id;

                        // 두 개의 요청을 동시에 수행하여 결과를 반환
                        const lastMessageRequest = this.$axios.get(`http://localhost:8081/api/messages/getLastMessage`, {
                            params: { chatRoomId: room.id }
                        });

                        const userInfoRequest = this.$axios.get(`http://localhost:8081/api/user/findUserById`, {
                            params: { id: otherUserId }
                        });

                        // 두 요청이 완료된 후에 결과를 조합하여 반환
                        return Promise.all([lastMessageRequest, userInfoRequest]).then(([lastMessageResponse, userResponse]) => ({
                            id: userResponse.data.id,
                            chatRoomId: room.id,
                            nickname: userResponse.data.nickname,
                            lastMessage: lastMessageResponse.data.content || '', // 마지막 메시지가 없을 경우 빈 문자열로 설정
                        }));
                    });

                    // 모든 채팅방 정보를 contacts 배열에 저장
                    Promise.all(contactPromises).then(contacts => {
                        this.contacts = contacts;
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

            // 이전 대화 내용 불러오기
            try {
                const response = await this.$axios.get(`http://localhost:8081/api/messages/${this.activeChat.chatRoomId}`, {
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
            const socket = new SockJS("http://localhost:8081/ws/chat");
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

                  this.setContacts();

                  // senderId가 현재 로그인한 사용자의 ID와 다르고, 같은 채팅방일 때만 추가
                  if (parsedMessage.senderId !== this.loginUserId) {
                      if (this.activeChat && parsedMessage.chatRoomId === this.activeChat.chatRoomId) {
                          this.activeChat.messages.push({ ...parsedMessage, sender: parsedMessage.senderId === this.loginUserId ? "me" : "other" });
                      }
                  }
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
            .get(`http://localhost:8081/api/user/findByLoginUserName`)
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
