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
      <div class="current-address-div">
        <p class="current-address-info">현재 설정된 위치: <span id="currentAddress">{{ this.place }}</span></p>
        <button class="btn btn-primary edit-place" @click="goToEditPlace">변경</button>
      </div>
        <table class="table table-hover table-bordered">
          <thead>
            <tr>
              <th scope="col">No</th>
              <th scope="col">제목</th>
              <th scope="col">카테고리</th>
              <th scope="col">작성자</th>
              <th scope="col">가격</th>
              <th scope="col">판매상태</th>
              <th scope="col">거리</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in itemList" :key="item.id" @click="goToDetail(item.id)" style="cursor: pointer;">
              <th scope="row">{{ item.id }}</th>
              <td>{{ item.itemNm }}</td>
              <td>{{ item.itemCategory }}</td>
              <td>{{ item.sellerNm }}</td>
              <td>{{ item.price }}</td>
              <td>{{ item.itemSellStatus }}</td>
              <td>{{ item.distance }}</td>
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
      place: null,
      latitude: 0,
      longitude: 0,
      distance: "X",
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
    goToEditPlace() {
        this.$router.push({ path: '/exchange/ask-for-place' });
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

          this.$axios
            .get(`http://localhost:8081/api/user/findByLoginUserName`)
            .then((response) => {
                if (response.data.place == null || response.data.place == '') { // 사용자 위치가 없으면 현재 위치 설정
                  this.$getLocation()
                  .then((coordinates) => {
                    const kakaoAxios = this.$axios.create();

                    this.latitude = coordinates.lat;
                    this.longitude = coordinates.lng;
                    kakaoAxios.get('https://dapi.kakao.com/v2/local/geo/coord2address.json', {
                      params: { x: this.longitude, y: this.latitude },
                      headers: {
                          Authorization: 'KakaoAK 1100c051ea6f907012629e48936f0504'
                      }
                  })
                  .then((response) => {
                    if (response.data.documents[0].address != null) {
                        this.place = response.data.documents[0].address.address_name;
                    } else {
                        this.place = response.data.documents[0].road_address.address_name;
                    }
                    this.$axios
                        .post(`http://localhost:8081/api/user/updatePlace`, { place: this.place })
                        })
                    this.calculateDistances();
                    });
                } else {
                    this.place = response.data.place;
                    const kakaoAxios = this.$axios.create();
                    kakaoAxios.get('https://dapi.kakao.com/v2/local/search/address.json', {
                        params: { query: this.place },
                        headers: {
                            Authorization: 'KakaoAK 1100c051ea6f907012629e48936f0504'
                        }
                    }).then((response) => {
                        this.longitude = response.data.documents[0].x;
                        this.latitude = response.data.documents[0].y;
                        this.calculateDistances();
                    });
                }
            })
        })
        .catch((error) => {
              if (error.response && error.response.status === 403) {
                alert("접근 권한이 없습니다. 로그인 후 다시 시도하세요.");
                this.$router.push({ path: `/` });
              }
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
    calculateDistances() {
      // 사용자 위치와 각 게시글의 위치 간의 거리 계산
      this.itemList.forEach((item) => {
        const itemCoords = { lat: item.lat, lng: item.lng }

        // 위도 또는 경도가 없는 경우 'X'로 표시
        if (!itemCoords.lat || !itemCoords.lng) {
            item.distance = "X";
            return;
        }

        const distanceInKm = this.getDistanceFromLatLonInKm(this.latitude, this.longitude, itemCoords.lat, itemCoords.lng);
        item.distance = `${distanceInKm.toFixed(1)}km`;
      });
    },
    getDistanceFromLatLonInKm(lat1, lon1, lat2, lon2) {
      // 두 좌표 간의 거리 계산 (단위: km)
      const R = 6371; // 지구 반지름 (단위: km)
      const dLat = this.deg2rad(lat2 - lat1);
      const dLon = this.deg2rad(lon2 - lon1);
      const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(this.deg2rad(lat1)) * Math.cos(this.deg2rad(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
      const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
      return R * c;
    },
    deg2rad(deg) {
      return deg * (Math.PI / 180);
    }
 },
  mounted() {
    this.fetchData();
  },
};
</script>

<style scoped>
    .current-address-info {
        display: inline-block;
        margin: 0;
    }

    .current-address-div {
        margin-bottom: 30px;
    }

    #currentAddress {
        font-weight: bold;
        color: #3CB815;
    }

    .edit-place {
        float: right;
    }
</style>
