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
</script>

<style scoped>

</style>
