<template>
    <div class="container-xxl py-6">
        <div class="container py-6">
            <h1>상품 상세보기</h1>
            <div>
                <p><strong>상품명:</strong> <span>{{ item.itemNm }}</span></p>
                <p><strong>카테고리:</strong> <span>{{ item.itemCategory }}</span></p>
                <p><strong>판매자 아이디:</strong> <span>{{ item.sellerId }}</span></p>
                <p><strong>판매자 이름:</strong> <span>{{ item.sellerNm }}</span></p>
                <p><strong>가격:</strong> <span>{{ item.price }}</span>원</p>
                <p><strong>수량:</strong> <span>{{ item.stockNumber }}</span></p>
                <p><strong>상품 설명:</strong> <span>{{ item.itemDetail }}</span></p>
                <p><strong>판매 상태:</strong> <span>{{ item.itemSellStatus }}</span></p>
                <p><strong>거래 희망 위치:</strong> <span>{{ item.place }}</span></p>
                <div id="map"></div>
                <p><strong>등록 시간:</strong> <span>{{ item.regTime }}</span></p>
                <p><strong>수정 시간:</strong> <span>{{ item.updateTime }}</span></p>
                <button type="button" class="btn btn-light" @click="goBack">Back</button>
                <!-- 현재 사용자만 수정/삭제 버튼 보이기 -->
                <button
                    v-if="isOwner"
                    type="button"
                    class="btn btn-primary"
                    @click="goToEdit(item.id)"
                >Edit</button>
                <button
                    v-if="isOwner"
                    type="button"
                    class="btn btn-danger"
                    @click="goToDelete(item.id)"
                >Delete</button>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "ExchangeDetail",
    data() {
        return {
            item: {}, // 게시글 데이터
            map: null, // 지도 객체
            geocoder: null, // 주소 검색 객체
            currentUserId: null, // 현재 로그인 사용자 ID
            isOwner: false, // 작성자인지 여부
        };
    },
    methods: {
        goBack() {
            this.$router.push('/exchange');
        },
        goToEdit(id) {
            this.$router.push({ path: `/exchange/edit/${id}` });
        },
        goToDelete(id) {
            this.$axios.delete(`/api/exchange/delete/${id}`)
                .then(response => {
                    alert(response.data.message);
                    this.$router.push('/exchange');
                })
                .catch(error => {
                    alert("상세 데이터 조회 중 오류가 발생했습니다.");
                    console.error("상세 데이터 조회 중 오류 발생:", error);
                });
        },
        async fetchData() {
            try {
                // 현재 사용자 정보 가져오기
                const userResponse = await this.$axios.get(`/api/user/findByLoginUserName`);
                this.currentUserId = userResponse.data.username;

                // 게시글 데이터 가져오기
                const itemResponse = await this.$axios.get(`/api/exchange/${this.$route.params.id}`);
                this.item = itemResponse.data;

                // 작성자인지 확인
                this.isOwner = this.currentUserId === this.item.sellerId;

                // 지도 초기화
                if (window.kakao && window.kakao.maps) {
                    this.initMap();
                }
            } catch (error) {
                console.error("데이터를 가져오는 중 오류 발생:", error);
            }
        },
        loadScript() {
            if (!window.kakao || !window.kakao.maps) {
                const script = document.createElement("script");
                script.src = "//dapi.kakao.com/v2/maps/sdk.js?appkey=1abf99a7eaabed5bf02b329ee8596ee0&autoload=false&libraries=services";
                script.onload = () => {
                    window.kakao.maps.load(() => {
                        this.initMap(); // SDK 로드 후 지도 초기화
                    });
                };
                document.head.appendChild(script);
            }
        },
        initMap() {
            if (!this.item.place) {
                console.error("지도 초기화 실패: 거래 위치 정보가 없습니다.");
                return;
            }

            const container = document.getElementById("map");
            const options = {
                center: new window.kakao.maps.LatLng(33.450701, 126.570667),
                level: 3,
            };

            this.map = new window.kakao.maps.Map(container, options);
            this.geocoder = new window.kakao.maps.services.Geocoder();

            // 주소 검색 및 마커 표시
            this.geocoder.addressSearch(this.item.place, (result, status) => {
                if (status === window.kakao.maps.services.Status.OK) {
                    const coords = new window.kakao.maps.LatLng(result[0].y, result[0].x);
                    const marker = new window.kakao.maps.Marker({
                        map: this.map,
                        position: coords,
                    });
                    const infowindow = new window.kakao.maps.InfoWindow({
                        content: `<div style="width:150px;text-align:center;padding:6px 0;">${this.item.place}</div>`,
                    });
                    infowindow.open(this.map, marker);
                    this.map.setCenter(coords);
                } else {
                    console.error("주소 검색 실패:", status);
                }
            });
        },
    },
    async mounted() {
        if (window.kakao && window.kakao.maps) {
            await this.fetchData(); // 데이터 가져오기 및 지도 초기화
        } else {
            this.loadScript(); // SDK 로드
            await this.fetchData(); // 데이터 가져오기 및 지도 초기화
        }
    },
};
</script>

<style scoped>
#map {
    width: 100%;
    height: 500px;
    margin: 20px;
}
</style>
