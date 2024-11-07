<template>
  <div class="container-xxl py-6">
    <div class="container py-6">
      <form @submit.prevent="submitForm" class="container">
        <div class="mb-3">
          <label class="form-label">제목</label>
          <input type="text" v-model="form.itemNm" class="form-control" />
        </div>
        <div class="mb-3">
          <label class="form-label">내용</label>
          <textarea class="form-control" v-model="form.itemDetail" rows="3"></textarea>
        </div>
        <div class="mb-3">
          <label class="form-label">카테고리</label><br />
          <div class="form-check form-check-inline" v-for="(category, index) in categories" :key="index">
            <input
              class="form-check-input"
              type="radio"
              v-model="form.itemCategory"
              :id="'inlineRadio' + (index + 1)"
              :value="category.value"
            />
            <label class="form-check-label" :for="'inlineRadio' + (index + 1)">
              {{ category.label }}
            </label>
          </div>
        </div>
        <div class="mb-3">
          <label class="form-label">가격</label>
          <input type="text" v-model="form.price" class="form-control" />
        </div>
        <div class="mb-3">
          <label class="form-label">수량</label>
          <input type="number" v-model="form.stockNumber" class="form-control" min="0" max="99" placeholder="1" />
        </div>
        <div class="mb-3">
          <label class="form-label">거래 희망 위치</label>
          <input type="text" v-model="form.place" class="form-control" />
        </div>
        <button type="submit" class="btn btn-primary">Submit</button>
        <button type="button" class="btn btn-light" @click="goBack">Back</button>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ExchangeEdit',
  data() {
    return {
      form: {
        itemNm: '',
        itemDetail: '',
        itemCategory: '',
        price: '',
        stockNumber: 1,
      },
      categories: [
        { label: '과일', value: 'FRUIT' },
        { label: '야채/채소', value: 'VEGETABLE' },
        { label: '고기', value: 'MEAT' },
        { label: '도구/기구', value: 'SUPPLIES' },
        { label: '그 외', value: 'ETC' },
      ],
    };
  },
  methods: {
    async submitForm() {
      try {
        const response = await this.$axios.put(`http://localhost:8081/api/exchange/edit/${this.$route.params.id}`, this.form);
        if (response != null) {
            alert('정상적으로 수정되었습니다.');
            // 성공적으로 저장한 후 다른 페이지로 이동
            this.$router.push('/exchange');
        }
      } catch (error) {
        console.error(error);
        alert('상품 수정에 오류가 발생했습니다.');
      }
    },
    fetchData() {
        this.$axios.get(`http://localhost:8081/api/exchange/${this.$route.params.id}`)
            .then(response => {
                this.item = response.data;
                this.form = { ...response.data };
            })
            .catch(error => {
                console.error("데이터를 가져오는 중 오류 발생:", error);
            });
    },
    goBack() {
      // 이전 페이지로 이동
      this.$router.push('/exchange');
    },
  },
  mounted() {
    this.fetchData();
  },
};
</script>

<style scoped>

</style>