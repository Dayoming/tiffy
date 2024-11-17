<template>
    <div class="container-xxl py-6">
        <div class="container py-6">
            <main class="form-signin w-100 m-auto" style="text-align: center;">
                <form @submit.prevent="loginForm">
                    <h1 class="h3 mb-3 fw-normal">Please sign in</h1>

                    <!-- 오류 메시지 표시 -->
                    <div v-if="errorMessage" class="alert alert-danger">
                        {{ errorMessage }}
                    </div>

                    <div class="form-floating mb-3">
                        <input type="text" v-model="username" class="form-control" id="username" placeholder="ID">
                        <label for="username">ID</label>
                    </div>
                    <div class="form-floating mb-3">
                        <input type="password" v-model="password" class="form-control" id="password" placeholder="Password">
                        <label for="password">Password</label>
                    </div>
                    <div class="login-form-signup">
                        <a href="#" @click="goSignup" style="margin-right: 10px;">회원가입</a>
                        <a href="#">아이디/비밀번호 찾기</a>
                    </div>
                    <button class="btn btn-primary w-100 py-2" type="submit">Login</button>
                </form>
            </main>
        </div>
    </div>
</template>

<script>
export default {
    name: "LoginForm",
    data() {
        return {
            username: '',
            password: '',
            errorMessage: ''  // 에러 메시지 저장
        };
    },
    methods: {
        goSignup() {
            this.$router.push('/signup');
        },
        async loginForm() {
            try {
                // 로그인 요청 보내기
                const response = await this.$axios.post('/api/user/login', {
                    username: this.username,
                    password: this.password
                });

                if (response.data.status === 'success') {
                    // 로그인 성공 시 토큰을 로컬 스토리지에 저장하고 새로고침 후 메인 페이지로 리다이렉트
                    const token = response.data.token;
                    localStorage.setItem("authToken", token);
                    this.$root.$globalState.isLoggedIn = true;
                    alert(response.data.message);
                    this.$router.go(0);
                } else if (response.data.status === 'error') {
                    this.errorMessage = response.data.message;
                    this.username = '';
                    this.password = '';
                }
            } catch (error) {
                this.errorMessage = "로그인 중 오류가 발생했습니다. 다시 시도해 주세요.";
            }
        },
    },
    mounted() {
        if (this.$root.$globalState.isLoggedIn === true) {
            this.$router.push("/");
        }
    }
};
</script>

<style scope>
html,
body {
  height: 100%;
}

.form-signin {
  max-width: 330px;
  padding: 1rem;
}

.form-signin .form-floating:focus-within {
  z-index: 2;
}

.form-signin input[type="email"] {
  margin-bottom: -1px;
  border-bottom-right-radius: 0;
  border-bottom-left-radius: 0;
}

.form-signin input[type="password"] {
  margin-bottom: 10px;
  border-top-left-radius: 0;
  border-top-right-radius: 0;
}

.bd-placeholder-img {
        font-size: 1.125rem;
        text-anchor: middle;
        -webkit-user-select: none;
        -moz-user-select: none;
        user-select: none;
      }

      @media (min-width: 768px) {
        .bd-placeholder-img-lg {
          font-size: 3.5rem;
        }
      }

      .b-example-divider {
        width: 100%;
        height: 3rem;
        background-color: rgba(0, 0, 0, .1);
        border: solid rgba(0, 0, 0, .15);
        border-width: 1px 0;
        box-shadow: inset 0 .5em 1.5em rgba(0, 0, 0, .1), inset 0 .125em .5em rgba(0, 0, 0, .15);
      }

      .b-example-vr {
        flex-shrink: 0;
        width: 1.5rem;
        height: 100vh;
      }

      .bi {
        vertical-align: -.125em;
        fill: currentColor;
      }

      .nav-scroller {
        position: relative;
        z-index: 2;
        height: 2.75rem;
        overflow-y: hidden;
      }

      .nav-scroller .nav {
        display: flex;
        flex-wrap: nowrap;
        padding-bottom: 1rem;
        margin-top: -1px;
        overflow-x: auto;
        text-align: center;
        white-space: nowrap;
        -webkit-overflow-scrolling: touch;
      }

      .btn-bd-primary {
        --bd-violet-bg: #712cf9;
        --bd-violet-rgb: 112.520718, 44.062154, 249.437846;

        --bs-btn-font-weight: 600;
        --bs-btn-color: var(--bs-white);
        --bs-btn-bg: var(--bd-violet-bg);
        --bs-btn-border-color: var(--bd-violet-bg);
        --bs-btn-hover-color: var(--bs-white);
        --bs-btn-hover-bg: #6528e0;
        --bs-btn-hover-border-color: #6528e0;
        --bs-btn-focus-shadow-rgb: var(--bd-violet-rgb);
        --bs-btn-active-color: var(--bs-btn-hover-color);
        --bs-btn-active-bg: #5a23c8;
        --bs-btn-active-border-color: #5a23c8;
      }

      .bd-mode-toggle {
        z-index: 1500;
      }

      .bd-mode-toggle .dropdown-menu .active .bi {
        display: block !important;
      }

      .login-form-signup {
        float: right;
        margin-bottom: 10px;
      }

      .login-form-signup a {
        font-size: small;
        text-decoration-line: underline;
        color: gray;
      }
</style>