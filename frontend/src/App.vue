<!-- src/App.vue -->
<template>
    <Header />
    <router-view />
    <Footer />
</template>

<script>
import Header from "@/components/layouts/CommonHeader.vue";
import Footer from "@/components/layouts/CommonFooter.vue";

export default {
  name: "App",
  components: {
    Header,
    Footer,
  },
};
</script>