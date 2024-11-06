<template>
  <header>
    <!-- Spinner Start -->
    <div id="spinner" class="show bg-white position-fixed translate-middle w-100 vh-100 top-50 start-50 d-flex align-items-center justify-content-center">
      <div class="spinner-border text-primary" role="status"></div>
    </div>
    <!-- Spinner End -->

    <!-- Navbar Start -->
    <div class="container-fluid fixed-top px-0 wow fadeIn" data-wow-delay="0.1s">
      <nav class="navbar navbar-expand-lg navbar-light py-lg-0 px-lg-5 wow fadeIn" data-wow-delay="0.1s">
        <a href="/" class="navbar-brand ms-4 ms-lg-0">
          <h1 class="fw-bold text-primary m-0">Tiffy</h1>
        </a>
        <button type="button" class="navbar-toggler me-4" data-bs-toggle="collapse" data-bs-target="#navbarCollapse">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarCollapse">
          <div class="navbar-nav ms-auto p-4 p-lg-0">
            <router-link to="/" class="nav-item nav-link active">Home</router-link>
            <router-link to="/exchange" class="nav-item nav-link">Exchange</router-link>
            <router-link to="/recipes" class="nav-item nav-link">Recipe</router-link>
            <button v-if="isLoggedIn" class="nav-item nav-link btn btn-link" @click="logout" style="text-align: left;">Logout</button>
            <router-link v-else to="/login" class="nav-item nav-link">Login</router-link>
          </div>
        </div>
      </nav>
    </div>
    <!-- Navbar End -->
  </header>
</template>

<script>
export default {
  name: "CommonHeader",
  computed: {
    isLoggedIn() {
      return this.$root.$globalState.isLoggedIn; // 전역 로그인 상태 사용
    },
  },
  methods: {
    logout() {
      // 로그아웃 로직
      localStorage.removeItem("authToken"); // 토큰 삭제
      this.$root.$globalState.isLoggedIn = false; // 전역 로그인 상태 변경
      this.$router.push("/"); // 메인 페이지로 리다이렉트
    },
  },
};
</script>

