<template>
<!-- 사이드바 버튼 -->
    <div class="sidebar-div">
        <a :class="['icon-md btn btn-primary position-fixed end-0 bottom-0 mb-5 sidebar-btn',
            isOpenedSideBar ? 'open-sidebar-btn' : '']"
            data-bs-toggle="offcanvas"
            href="#offcanvasChat" role="button" aria-controls="offcanvasChat"
            @click="toggleSidebar">
            <!-- 읽지 않은 메시지가 있을 때만 보이는 알림 아이콘 -->
            <div v-if="unreadNotificationCount > 0" class="notification-side-bar-shape">
                <p class="notification-side-bar-count">{{ unreadNotificationCount }}</p>
            </div>
            <i class="bi bi-arrow-bar-left"></i>
        </a>
   </div>
  <div class="offcanvas offcanvas-end sidebar-canvas" data-bs-scroll="true" data-bs-backdrop="false" tabindex="-1" id="offcanvasChat">
    <div class="offcanvas-header d-flex justify-content-between">
      <h5 class="offcanvas-title">Message</h5>
      <button class="btn btn-secondary-soft-hover py-1 px-2" data-bs-dismiss="offcanvas" aria-label="Close" @click="toggleSidebar">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-x-lg" viewBox="0 0 16 16">
          <path d="M2.146 2.854a.5.5 0 1 1 .708-.708L8 7.293l5.146-5.147a.5.5 0 0 1 .708.708L8.707 8l5.147 5.146a.5.5 0 0 1-.708.708L8 8.707l-5.146 5.147a.5.5 0 0 1-.708-.708L7.293 8z"/>
        </svg>
      </button>
    </div>
    <div class="offcanvas-body pt-0 custom-scrollbar">
      <form class="rounded position-relative">
        <input :value="searchKeyword" v-bind:disabled="loginUserId === 0" id="searchKeyword"
            class="form-control ps-5 bg-light" type="search"
            v-bind:placeholder="loginUserId === 0 ? '로그인 후 사용 가능한 기능입니다.' : '두 글자 이상 입력해주세요.'"
            aria-label="Search" @input="searchUser">
        <ul v-if="searchKeyword !== '' && users.length > 0" class="dropdown-menu" :class="searchKeyword !== '' && users.length > 0 ? 'show' : ''" style="width: 100%;">
          <li v-for="user in users" :key="user.id" @click="goOrCreateChat(user.id)">
            <a class="dropdown-item" href="#">{{user.nickname}}</a>
          </li>
        </ul>
        <button class="btn bg-transparent px-3 py-0 position-absolute top-50 start-0 translate-middle-y" type="submit">
          <i class="bi bi-search fs-5"></i>
        </button>
      </form>
      <ul class="list-unstyled">
        <li v-for="(contact, index) in contacts" :key="index" class="mt-3 hstack gap-3 align-items-center position-relative" @click="$emit('openChat', contact)">
          <div class="avatar status-online">
            <img class="avatar-img rounded-circle" src="/icon/profile-default-icon.png" alt="Profile Image">
          </div>
          <div class="overflow-hidden">
            <a class="h6 mb-0 stretched-link">{{ contact.nickname }}</a>
            <div v-if="contact.notificationCount !== 0" class="notification">
              <p class="notification-num">{{ contact.notificationCount }}</p>
            </div>
            <div class="small text-secondary text-truncate">{{ contact.lastMessage }}</div>
          </div>
          <div class="small ms-auto text-nowrap">{{ contact.timestamp }}</div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
    data() {
        return {
            searchKeyword: '',
            users: [],
            isOpenedSideBar: false,
        }
    },
    props: {
        contacts: Array,
        loginUserId: Number,
        unreadNotificationCount: Number,
        isMobile: Boolean,
    },
    methods: {
        toggleSidebar() {
            this.isOpenedSideBar = !this.isOpenedSideBar; // 열림 상태 토글
        },
        searchUser(e) {
            this.searchKeyword = e.target.value;

            // 두 글자 이상일 때만 검색 결과 반환
            if (this.searchKeyword.length >= 2) {
                this.$axios
                    .get(`/api/user/findUserByIncludeUserNickname`, {
                      params: {
                        nickname: this.searchKeyword,
                      },
                    })
                    .then((response) => {
                        this.users = response.data;
                });
            }
        },
        // 검색한 이름을 클릭하면 채팅방을 생성하거나 불러옴
        goOrCreateChat(id) {
            if (id === this.loginUserId) {
                alert("자기 자신과는 대화할 수 없습니다.");
                this.searchKeyword = '';
                return;
            }

            if (!this.loginUserId) {
                alert("로그인 후 사용 가능한 기능입니다.");
                return;
            }

            this.$axios
                .get(`/api/chatrooms/getOrCreate`, {
                  params: {
                    user2Id: id,
                  },
                })
                .then((response) => {
                    this.$axios.get(`/api/user/findUserById`, {
                        params: { id: response.data.user2Id },
                    })
                    .then((userResponse) => {
                        console.log(userResponse.data.nickname + "님과의 1:1 채팅방에 입장했습니다.");
                        const user = userResponse.data;
                        const chatRoom = response.data;

                        // 부모 컴포넌트로 선택된 유저와 채팅방 정보를 전달
                        this.$emit('openChat', {
                            id: user.id,
                            nickname: user.nickname,
                            chatRoomId: chatRoom.id,
                        });
                    })
            })
            .catch((error) => {
                console.log(error);
            });
            this.searchKeyword = '';
        },
    },
};
</script>
<style scope>

.stretched-link {
    font-family: 'NanumSquareRound';
}

.notification {
    display: inline-block;
    width: 15px;
    height: 15px;
    background-color: #F65005;
    border-radius: 10px;
    float: inline-end;
}

.notification-num {
    color: white;
    font-size: 0.6em;
    text-align: center;
    margin-top: 1px;
}

.open-sidebar-btn {
    transform: translateX(-309px);
    transition: transform 0.3s ease-in-out;
}

.sidebar-canvas {
    width: 310px;
}

</style>
