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
        <button type="submit" class="btn btn-primary">Submit</button>
        <button type="button" class="btn btn-light" @click="goBack">Back</button>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ExchangeNew',
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
        const response = await this.$axios.post('http://localhost:8081/api/exchange/createItem', this.form);
        if (response.status === 201) { // 상태 코드 확인
            alert(response.data.message); // 성공 메시지를 응답에서 받아서 사용
            this.$router.push('/exchange'); // 성공적으로 저장한 후 다른 페이지로 이동
        }
      } catch (error) {
        console.error(error);
        alert('상품 등록에 오류가 발생했습니다.');
      }
    },
    goBack() {
      // 이전 페이지로 이동
      this.$router.push('/exchange');
    },
  },
};
</script>

<style scoped>

</style>