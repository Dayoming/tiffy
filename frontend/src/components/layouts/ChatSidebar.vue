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
    <div class="offcanvas-body pt-0">
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
      <ul :class="isMobile ? 'hidden' : 'list-unstyled'">
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
      <!-- 모바일 환경에서 프로필 사진으로 채팅 목록 추가 -->
      <div v-if="isMobile" class="mobile-profile-images">
        <div
          v-for="(contact, index) in contacts"
          :key="index"
          class="profile-image-wrapper"
          @click="$emit('openChat', contact)"
        >
          <img class="profile-image" :src="contact.profileImage || '/icon/profile-default-icon.png'" :alt="contact.nickname">
          <p class="profile-name">{{ contact.nickname }}</p>
        </div>
      </div>
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
            isMobile: false,
        }
    },
    props: {
        contacts: Array,
        loginUserId: Number,
        unreadNotificationCount: Number,
    },
    methods: {
        checkScreenSize() {
            this.isMobile = window.innerWidth <= 768;
        },
        toggleSidebar() {
            this.isOpenedSideBar = !this.isOpenedSideBar; // 열림 상태 토글
            if (!this.isOpenedSideBar) {
                this.$emit('closeAllChats'); // 부모 컴포넌트에 모든 채팅을 닫으라는 이벤트 전달
            }
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
    mounted() {
        this.checkScreenSize();
        window.addEventListener('resize', this.checkScreenSize);
    },
    beforeUnmount() {
        window.removeEventListener('resize', this.checkScreenSize);
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

.mobile-chat-list {
    width: 100px;
    height: 100%;
    background-color: white;
}

/* 모바일 환경에서 프로필 이미지 스타일 */
.mobile-profile-images {
    display: flex;
    width: calc(100% - 309px);
    height: 100%;
    overflow-y: auto;
    padding: 10px;
    background-color: #fff;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    z-index: 1050;
    flex-direction: column;
}

.profile-image-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    cursor: pointer;
}

.profile-image {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    object-fit: cover;
}

.profile-name {
    font-size: 12px;
    text-align: center;
    margin-top: 5px;
    color: #333;
}

.hidden {
    display: none;
}

.show {
    display: block;
}

.offcanvas-body {
    overflow: visible;
}

</style>
