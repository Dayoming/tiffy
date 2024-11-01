<template>
  <div class="container-xxl py-6">
    <div class="container py-6">
      <h1 style="margin-bottom: 30px;">Recipe Search</h1>

      <form @submit.prevent="searchRecipes">
        <div class="input-group mb-3">
          <input type="text" v-model="ingredients" class="form-control" placeholder="재료를 입력하세요" required />
          <button class="btn btn-outline-secondary" type="submit">검색</button>
        </div>
      </form>

      <!-- 레시피 결과를 표시할 영역 -->
      <div id="recipe-results" class="mt-4">
        <div v-if="recipes.length === 0 && !loading">검색 결과가 없습니다.</div>
        <div v-else>
          <div
            v-for="(recipe, index) in recipes"
            :key="index"
            class="card mb-3"
          >
            <div class="row g-0">
              <div class="col-md-4">
                <img :src="recipe.attFileNoMain" class="img-fluid rounded-start" :alt="recipe.rcpNm" />
              </div>
              <div class="col-md-8">
                <div class="card-body">
                  <h5 class="card-title">{{ recipe.rcpNm }}</h5>
                  <p class="card-text"><strong>조리방법:</strong> {{ recipe.rcpWay2 }}</p>
                  <p class="card-text"><strong>요리종류:</strong> {{ recipe.rcpPat2 }}</p>
                  <p class="card-text"><strong>재료정보:</strong> {{ recipe.rcpPartsDtls }}</p>
                  <h6>만드는 법:</h6>
                  <ul v-html="generateManualSteps(recipe)"></ul>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="loading" class="mt-4">검색 중...</div>
    </div>
  </div>
</template>

<script>

export default {
  name: 'RecipeSearch',
  data() {
    return {
      ingredients: '', // 사용자 입력 재료
      recipes: [], // 검색 결과 레시피 목록
      loading: false, // 로딩 상태
    };
  },
  methods: {
    // 레시피 검색 메서드
    searchRecipes() {
      this.loading = true;
      this.$axios
        .post('http://localhost:8081/api/recipes/search', new URLSearchParams({ ingredients: this.ingredients }))
        .then(response => {
          this.recipes = response.data.recipes || [];
        })
        .catch(error => {
          console.error('Error:', error);
          this.recipes = [];
        })
        .finally(() => {
          this.loading = false;
        });
    },

    // 만드는 법 단계를 HTML로 생성하는 메서드
    generateManualSteps(recipe) {
      let steps = '';
      for (let i = 1; i <= 20; i++) {
        const manual = recipe[`manual${String(i).padStart(2, '0')}`];
        const manualImg = recipe[`manualImg${String(i).padStart(2, '0')}`];
        if (manual) {
          steps += `<li>${manual}`;
          if (manualImg) {
            steps += `<br><img src="${manualImg}" class="img-fluid mt-2" alt="Step ${i}">`;
          }
          steps += '</li>';
        }
      }
      return steps;
    },
  },
};
</script>

<style scoped>

</style>