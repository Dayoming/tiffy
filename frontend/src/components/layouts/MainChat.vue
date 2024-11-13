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
                .get(`http://localhost:8081/api/user/findByLoginUserName`)
                .then((response) => { this.loginUserId = response.data.id; });

            this.$axios
                .get(`http://localhost:8081/api/chatrooms/findChatRoomsByUserId`)
                .then((response) => {
                    const chatRooms = response.data;
                    // 각 채팅방의 상대방 사용자 정보를 가져오기 위해 Promise.all 사용
                    const contactPromises = chatRooms.map(room => {
                        // 만약 내 id가 user1Id면 user2Id를 가져와야 하고, 내 id가 user2Id면 user1Id를 가져와야 함.
                        if (this.loginUserId == room.user1Id) {
                            this.otherUserId = room.user2Id;
                        } else {
                            this.otherUserId = room.user1Id;
                        }

                        this.$axios.get(`http://localhost:8081/api/messages/getLastMessage`, {
                            params: { chatRoomId: room.id }
                        })
                        .then((lastMessageResponse) => {
                            this.lastMessage = lastMessageResponse.data.content;
                        });

                        // 상대방 사용자 정보를 가져오는 요청을 반환
                        return this.$axios.get(`http://localhost:8081/api/user/findUserById`, {
                            params: { id: this.otherUserId }
                        })
                        .then(userResponse => ({
                            id: userResponse.data.id,
                            chatRoomId: room.id,
                            nickname: userResponse.data.nickname,
                            lastMessage: this.lastMessage,
                        }));
                    });

                    // 모든 상대방 정보를 contacts 배열에 저장
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

            // 기존 WebSocket 연결 종료
            if (this.stompClient && this.stompClient.connected) {
                await this.disconnect();
            }

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

            // 새로운 WebSocket 연결 시작
            await this.connect(this.activeChat.chatRoomId);
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

                this.stompClient.send(`/app/user/messages/${this.activeChat.id}`, {}, JSON.stringify(message));
                this.activeChat.messages.push(
                    { ...message,
                      senderId: this.loginUserId,
                      sender: "me",
                      timestamp: message.timestamp });
            }
            this.setContacts();
        },
        connect(chatRoomId) {
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
                this.stompClient.subscribe(`/queue/user/${chatRoomId}`, (message) => {
                  const parsedMessage = JSON.parse(message.body);

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
        this.setContacts();
    },
    beforeUnmount() {
        this.disconnect();
    },
};
</script>
