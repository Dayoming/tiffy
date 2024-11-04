<template>
  <div>
    <div class="container-fluid page-header wow fadeIn" data-wow-delay="0.1s">
      <div class="container">
        <h1 class="display-3 mb-3 animated slideInDown">Exchange</h1>
        <nav aria-label="breadcrumb animated slideInDown"></nav>
      </div>
    </div>

    <div class="container-xxl py-6">
      <div class="container">
        <table class="table table-hover table-bordered">
          <thead>
            <tr>
              <th scope="col">No</th>
              <th scope="col">제목</th>
              <th scope="col">카테고리</th>
              <th scope="col">작성자</th>
              <th scope="col">가격</th>
              <th scope="col">수량</th>
              <th scope="col">판매상태</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in itemList" :key="item.id" @click="goToDetail(item.id)" style="cursor: pointer;">
              <th scope="row">{{ item.id }}</th>
              <td>{{ item.itemNm }}</td>
              <td>{{ item.itemCategory }}</td>
              <td>{{ item.sellerNm }}</td>
              <td>{{ item.price }}</td>
              <td>{{ item.stockNumber }}</td>
              <td>{{ item.itemSellStatus }}</td>
            </tr>
          </tbody>
        </table>

        <!-- Pagination -->
        <div class="container text-center">
          <div class="row align-items-start">
            <div class="col"></div>
            <div class="col">
              <nav aria-label="...">
                <ul class="pagination pagination-sm justify-content-center">
                  <li class="page-item" :class="{ disabled: currentPage === 1 }">
                    <a class="page-link" href="#" @click.prevent="goToPreviousSet" aria-label="Previous">
                      <span aria-hidden="true">&laquo;</span>
                    </a>
                  </li>
                  <li v-for="pageNum in pageNumbers" :key="pageNum" class="page-item">
                    <a
                      class="page-link"
                      href="#"
                      :class="{ active: pageNum === currentPage }"
                      @click.prevent="goToPage(pageNum)"
                    >{{ pageNum }}</a>
                  </li>
                  <li class="page-item" :class="{ disabled: startPage + pageLimit > totalPages }">
                    <a class="page-link" href="#" @click.prevent="goToNextSet" aria-label="Next">
                      <span aria-hidden="true">&raquo;</span>
                    </a>
                  </li>
                </ul>
              </nav>
            </div>
            <div class="col">
              <button @click="goToNewExchange" class="btn btn-primary float-end">등록</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ExchangeList",
  data() {
    return {
      itemList: [],
      currentPage: 1,
      totalPages: 1,
      totalItems: 0,
      pageSize: 10, // 한 페이지당 10개
      pageLimit: 10, // 한 번에 보여줄 페이지 버튼 수
      startPage: 1, // 페이지 버튼 시작 번호
    };
  },
  computed: {
    pageNumbers() {
      // 현재 startPage부터 pageLimit 수만큼의 페이지 번호 배열 생성
      return Array.from(
            { length: Math.min(this.pageLimit, this.totalPages - this.startPage + 1) },
            (_, i) => i + this.startPage
          );
    },
  },
  methods: {
    goToDetail(id) {
      this.$router.push({ path: `/exchange/${id}` });
    },
    goToPage(pageNum) {
      this.currentPage = pageNum;
      this.fetchData();

    // 현재 페이지가 pageLimit의 배수일 때 startPage 조정
        if (pageNum % this.pageLimit === 0) {
          this.startPage = pageNum + 1;
        } else if (pageNum === this.startPage - 1) {
          this.startPage = Math.max(1, this.startPage - this.pageLimit);
        }
    },

    goToNewExchange() {
      this.$router.push({ path: `/exchange/new` });
    },
    fetchData() {
      this.$axios
        .get(`http://localhost:8081/api/exchange/`, {
          params: {
            page: this.currentPage,
            size: this.pageSize,
          },
        })
        .then((response) => {
          this.itemList = response.data.items;
          this.totalPages = response.data.totalPages;
          this.totalItems = response.data.totalItems;
        })
        .catch((error) => {
          console.error("데이터를 가져오는 중 오류 발생:", error);
        });
    },
    goToPreviousSet() {
        // 이전 페이지 묶음으로 이동
        if (this.startPage > 1) {
          this.startPage = Math.max(1, this.startPage - this.pageLimit);
          this.currentPage = this.startPage;
          this.fetchData();
        }
      },
    goToNextSet() {
        // 다음 페이지 묶음으로 이동
        if (this.startPage + this.pageLimit <= this.totalPages) {
          this.startPage += this.pageLimit;
          this.currentPage = this.startPage;
          this.fetchData();
      }
    },
  },
  mounted() {
    this.fetchData();
  },
};
</script>

<style scoped></style>
