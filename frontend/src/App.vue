<!-- src/App.vue -->
<template>
    <Header @logout="handleLogout" />
    <router-view />
    <MainChat />
    <Footer />
</template>

<script>
import Header from "@/components/layouts/CommonHeader.vue";
import MainChat from "@/components/layouts/MainChat.vue";
import Footer from "@/components/layouts/CommonFooter.vue";

export default {
  name: "App",
  components: {
    Header,
    Footer,
    MainChat,
  },
  methods: {
    handleLogout() {
        this.$router.push("/login"); // 로그아웃 시 로그인 페이지로 이동
      }
  },
};
</script>