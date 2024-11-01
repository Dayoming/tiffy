<template>
    <div>
        <!-- Page Header -->
        <div class="container-fluid page-header wow fadeIn" data-wow-delay="0.1s">
            <div class="container">
                <h1 class="display-3 mb-3 animated slideInDown">Exchange</h1>
                <nav aria-label="breadcrumb animated slideInDown"></nav>
            </div>
        </div>

        <!-- Table Section -->
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
                    <tr
                            v-for="item in itemList"
                            :key="item.id"
                            @click="goToDetail(item.id)"
                            style="cursor: pointer;"
                    >
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

                <!-- Pagination and Registration Button -->
                <div class="container text-center">
                    <div class="row align-items-start">
                        <div class="col"></div>
                        <div class="col">
                            <nav aria-label="...">
                                <ul class="pagination pagination-sm justify-content-center">
                                    <li class="page-item">
                                        <a
                                                class="page-link"
                                                href="#"
                                                @click.prevent="goToPage(1)"
                                                aria-label="Previous"
                                        >
                                            <span aria-hidden="true">&laquo;</span>
                                        </a>
                                    </li>
                                    <li
                                            v-for="pageNum in pageNumbers"
                                            :key="pageNum"
                                            class="page-item"
                                    >
                                        <a
                                                class="page-link"
                                                href="#"
                                                @click.prevent="goToPage(pageNum)"
                                        >{{ pageNum }}</a>
                                    </li>
                                    <li class="page-item">
                                        <a
                                                class="page-link"
                                                href="#"
                                                @click.prevent="goToPage(totalPages)"
                                                aria-label="Next"
                                        >
                                            <span aria-hidden="true">&raquo;</span>
                                        </a>
                                    </li>
                                </ul>
                            </nav>
                        </div>
                        <div class="col">
                            <button
                                    @click="goToNewExchange"
                                    class="btn btn-primary float-end"
                            >
                                등록
                            </button>
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
          itemList: [], // 서버에서 데이터를 받아오는 itemList
          startPage: 1,
          endPage: 5, // 페이지네이션 끝 페이지
          totalPages: 10, // 총 페이지 수
        };
      },
      computed: {
        pageNumbers() {
          // 페이지 번호 배열 생성
          return Array.from({ length: this.endPage - this.startPage + 1 }, (_, i) => i + this.startPage);
        },
      },
      methods: {
        goToDetail(id) {
          // 특정 항목의 상세 페이지로 이동하는 메서드
          this.$router.push({ path: `/exchange/${id}` });
        },
        goToPage(pageNum) {
          // 특정 페이지로 이동하는 메서드
          this.$router.push({ path: `/exchange`, query: { page: pageNum } });
        },
        goToNewExchange() {
          // 새 항목 등록 페이지로 이동
          this.$router.push({ path: `/exchange/new` });
        },
        fetchData() {
          this.$axios.get('http://localhost:8081/api/exchange/') // 백엔드 주소와 포트 포함
            .then(response => {
              this.itemList = response.data.itemList; // 서버 응답 구조에 맞게 데이터 접근
            })
            .catch(error => {
              console.error("데이터를 가져오는 중 오류 발생:", error);
            });
        },
      },
      mounted() {
        this.fetchData(); // 컴포넌트가 로드되면 데이터 가져오기
      },
    };
</script>

<style scoped>

</style>
