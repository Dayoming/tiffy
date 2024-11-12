<template>
    <div class="offcanvas offcanvas-end" data-bs-scroll="true" data-bs-backdrop="false" tabindex="-1" id="offcanvasChat">
        <div class="offcanvas-header d-flex justify-content-between">
            <h5 class="offcanvas-title">Message</h5>
            <button class="btn btn-secondary-soft-hover py-1 px-2" data-bs-dismiss="offcanvas" aria-label="Close">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-x-lg" viewBox="0 0 16 16">
                  <path d="M2.146 2.854a.5.5 0 1 1 .708-.708L8 7.293l5.146-5.147a.5.5 0 0 1 .708.708L8.707 8l5.147 5.146a.5.5 0 0 1-.708.708L8 8.707l-5.146 5.147a.5.5 0 0 1-.708-.708L7.293 8z"/>
                </svg>
            </button>
        </div>
        <div class="offcanvas-body pt-0 custom-scrollbar">
            <form class="rounded position-relative">
                <input class="form-control ps-5 bg-light" type="search" placeholder="Search..." aria-label="Search" v-model="searchKeyword" @input="searchUser()">
                <ul class="dropdown-menu" style="width: 100%">
                  <li v-for="user in users" :key="user.id" @click="goOrCreateChat(user.id)"><a class="dropdown-item" href="#">{{user.nickname}}</a></li>
                </ul>
                <button class="btn bg-transparent px-3 py-0 position-absolute top-50 start-0 translate-middle-y"
                        type="submit"><i class="bi bi-search fs-5"> </i></button>
            </form>
            <ul class="list-unstyled">
                <li v-for="(contact, index) in contacts" :key="index" class="mt-3 hstack gap-3 align-items-center position-relative" @click="$emit('openChat', contact)">
                    <div class="avatar status-online">
                        <img class="avatar-img rounded-circle" src="/icon/profile-default-icon.png" alt="Profile Image">
                    </div>
                    <div class="overflow-hidden">
                        <a class="h6 mb-0 stretched-link">{{ contact.nickname }}</a>
                        <div class="small text-secondary text-truncate">{{ contact.lastMessage }}</div>
                    </div>
                    <div class="small ms-auto text-nowrap"> Just now</div>
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
        }
    },
    props: {
        contacts: Array,
    },
    methods: {
        searchUser() {
            const dropDown = document.getElementsByClassName("dropdown-menu")[0];

            if (this.searchKeyword == '') {
                dropDown.style.display = 'none';
                return;
            }

            dropDown.style.display = 'none';
            this.$axios
                .get(`http://localhost:8081/api/user/findUserByIncludeUserNickname`, {
                  params: {
                    nickname: this.searchKeyword,
                  },
                })
                .then((response) => {
                    this.users = response.data;
                    dropDown.style.display = 'block';
        })
    },
    // 검색한 이름을 클릭하면 채팅방을 생성하거나 불러옴
    goOrCreateChat(id) {
        this.$axios
            .get(`http://localhost:8081/api/chatrooms/getOrCreate`, {
              params: {
                user2Id: id,
              },
            })
            .then((response) => {
                this.$axios.get(`http://localhost:8081/api/user/findUserById`, {
                    params: { id: response.data.user2Id },
                })
                .then((response) => {
                    console.log(response.data.nickname + "님과의 1:1 채팅방에 입장했습니다.");
                })
        })
        .catch((error) => {
            console.log(error);
        });
    },
  },
};
</script>
<style scope>
.stretched-link {
    font-family: 'NanumSquareRound';
}
</style>
