<template>
    <div>
        <!-- 사이드바 컴포넌트 -->
        <ChatSidebar
            v-if="contacts"
            :contacts="contacts"
            :loginUserId="loginUserId"
            :unreadNotificationCount="unreadNotificationCount"
            @openChat="openChat" @closeSidebar="closeSidebar" @closeAllChats="closeAllChats" />

        <!-- 채팅창 영역 -->
        <div v-for="(chat, index) in activeChats" :key="chat.chatRoomId">
            <ChatToast
                :chat="chat"
                :loginUserId="loginUserId"
                @sendMessage="sendMessage"
                @closeToast="closeToast(chat.chatRoomId)"
                :style="getChatToastStyle(index)"
            />
        </div>
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
            activeChats: [], // activeChat이 설정되면 ChatToast가 나타남
            maxChatCount: 10, // 최대 채팅창 개수
            chatToastWidth: 370, // 채팅창 너비
            chatToastHeight: 330, // 채팅창 높이
            screenPadding: 20, // 화면 경계 간격
            loginUserId: 0,
            otherUserId: 0,
            textMessage: '',
            lastMessage: '',
            messages: [],
            stompClient: null,
            headers: '',
            unreadNotificationCount: 0,
            isOpenSidebar: false,
            isMobile: false, // 모바일 여부
        };
    },
    methods: {
        checkScreenSize() {
            this.isMobile = window.innerWidth <= 768;
        },
        // 채팅창을 여러 개 열었을 때 디자인
        getChatToastStyle(index) {
            if (this.isMobile) {
                // 모바일 환경 스타일 적용
                return {
                    bottom: `${20 + index * 70}px`,
                    right: '20px',
                    width: '90%',
                    left: '5%',
                };
            } else {
                const { chatToastWidth, chatToastHeight, screenPadding } = this;
                const screenWidth = window.innerWidth - 380;
                 // 최대 배치 가능한 열의 개수
                const maxColumns = Math.floor((screenWidth - screenPadding) / chatToastWidth);
                 // 채팅창의 열 위치 (오른쪽에서부터)
                const column = index % maxColumns;
                 // 채팅창의 행 위치 (위에서부터)
                const row = Math.floor(index / maxColumns);
                 // 오른쪽 및 위쪽으로 배치
                return {
                    right: `${380 + screenPadding + column * chatToastWidth}px`,
                    bottom: `${20 + screenPadding + row * chatToastHeight}px`,
                };
            }
        },
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
        closeAllChats() {
            this.activeChats = [];
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
                    });
                })
                .catch((error) => {
                    console.log("Error fetching chatrooms:", error);
                });
        },
        async openChat(contact) {
            if (this.isMobile) {
                this.activeChats = []; // 기존 채팅 정보 제거
            } else if (this.activeChats.length >= this.maxChatCount) {
                // 데스크톱 환경에서 최대 채팅창 개수 초과 시 알림
                alert(`최대 ${this.maxChatCount}개의 채팅창만 열 수 있습니다.`);
                return;
            } else {
                const { chatToastWidth, chatToastHeight, screenPadding } = this;
                const screenWidth = window.innerWidth - 380; // 사이드바를 제외한 화면 너비
                const screenHeight = window.innerHeight; // 화면 높이

                 // 현재 열려 있는 채팅창으로 필요한 공간 계산
                const maxColumns = Math.floor((screenWidth - screenPadding) / chatToastWidth);
                const maxRows = Math.floor((screenHeight - screenPadding) / chatToastHeight);
                const maxChatsPossible = maxColumns * maxRows;

                 // 최대 가능한 채팅창 개수를 초과하면 새 채팅창을 열지 못하게 함
                if (this.activeChats.length >= maxChatsPossible) {
                    alert("화면 공간이 부족하여 더 이상 채팅창을 열 수 없습니다.");
                    return;
                }
            }

            // 채팅창 중복 확인
            const existingChat = this.activeChats.find(chat => chat.chatRoomId === contact.chatRoomId);
            if (existingChat) {
                alert(`${contact.nickname}님과의 채팅이 이미 열려 있습니다.`);
                return;
            }

            // 클릭한 연락처 정보를 activeChat에 설정하여 ChatToast가 열리게 함
            const newChat = {
                id: contact.id,
                chatRoomId: contact.chatRoomId,
                nickname: contact.nickname,
                messages: [],
            };


            // 이전 대화 내용 불러오기
            try {
                const response = await this.$axios.get(`/api/messages/${contact.chatRoomId}`);
                newChat.messages = response.data.map(message => ({
                    ...message,
                    sender: message.senderId === this.loginUserId ? "me" : "other",
                }));

                this.activeChats.push(newChat);
                // 해당 채팅방에서 유저가 받은 대화 모두 읽음 처리
                this.$axios.post(`/api/notifications/markAsRead/${contact.chatRoomId}/${this.loginUserId}`);
                this.setContacts();
            } catch (error) {
                console.error("Error fetching messages:", error);
            }
        },
        closeToast(chatRoomId) {
            // 채팅창 닫기
            this.activeChats = this.activeChats.filter(chat => chat.chatRoomId !== chatRoomId);
        },
        sendMessage(messageContent) {
            if (this.activeChats.length > 0) {
                const activeChat = this.activeChats.find(chat => chat.chatRoomId);
                if (activeChat) {
                    const message = {
                        chatRoomId: activeChat.chatRoomId,
                        senderId: this.loginUserId,
                        receiverId: activeChat.id,
                        content: messageContent.content,
                        timestamp: new Date().toLocaleTimeString(),
                    };

                    this.stompClient.send(`/app/user/messages/${message.receiverId}`, {}, JSON.stringify(message));
                    activeChat.messages.push(
                        { ...message,
                          senderId: this.loginUserId,
                          receiverId: message.receiverId,
                          sender: "me",
                          timestamp: message.timestamp });

                    this.setContacts();
                }
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
                  const activeChat = this.activeChats.find(chat => chat.chatRoomId);

                  if (parsedMessage.senderId !== this.loginUserId) {
                      if (activeChat && parsedMessage.chatRoomId === activeChat.chatRoomId) {
                          activeChat.messages.push({ ...parsedMessage, sender: parsedMessage.senderId === this.loginUserId ? "me" : "other" });
                          // 채팅방 메시지 읽음 처리
                          this.$axios
                            .post(`/api/notifications/markAsRead/${activeChat.chatRoomId}/${this.loginUserId}`);
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
        this.checkScreenSize();
        window.addEventListener('resize', this.checkScreenSize);
    },
    beforeUnmount() {
        this.disconnect();
        window.removeEventListener('resize', this.checkScreenSize);
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