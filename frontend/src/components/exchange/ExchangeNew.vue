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
              <label class="form-label">위치</label>
              <input type="text" v-model="form.place" class="form-control place-input" />
              <button type="button" class="btn btn-primary" @click="loadMap">내 위치</button>
        </div>
        <div class="map_wrap">
            <div id="map" style="width:100%;height:100%;position:relative;overflow:hidden;"></div>
            <div class="hAddr">
                <span class="title">지도중심기준 행정동 주소정보</span>
                <span id="centerAddr"></span>
            </div>
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
      map: null,
      geocoder: null,
      infowindow: null,
      latitude: 0,
      longitude: 0,
      form: {
        itemNm: '',
        itemDetail: '',
        itemCategory: '',
        price: '',
        place: '',
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
    loadScript() {
        const script = document.createElement("script");
        script.src = "//dapi.kakao.com/v2/maps/sdk.js?appkey=1abf99a7eaabed5bf02b329ee8596ee0&autoload=false&libraries=services";
        script.onload = () => window.kakao.maps.load(this.loadMap);
        document.head.appendChild(script);
    },
    // 카카오 지도 생성
    loadMap() {
        const container = document.getElementById("map");
        // 위, 경도 가져오기
        this.$getLocation().
            then((coordinates) => {
                this.latitude = coordinates.lat;
                this.longitude = coordinates.lng;

                const options = {
                    center: new window.kakao.maps.LatLng(this.latitude, this.longitude), // 지도 중심 좌표
                    level: 3, // 지도의 확대, 축소 정도
                };

                this.map = new window.kakao.maps.Map(container, options);
                // 주소-좌표 변환 객체를 생성합니다
                this.geocoder = new window.kakao.maps.services.Geocoder();
                this.infowindow = new window.kakao.maps.InfoWindow({ zindex: 1 }); // 클릭한 위치에 대한 주소를 표시할 인포윈도우

                // 마커가 표시될 위치입니다
                const markerPosition = new window.kakao.maps.LatLng(this.latitude, this.longitude);
                // 마커를 생성합니다
                const marker = new window.kakao.maps.Marker({
                    position: markerPosition,
                });

                marker.setMap(this.map);

                // 초기 지도 중심 좌표의 주소 표시
                this.searchDetailAddrFromCoords(this.map.getCenter(), (result, status) => {
                    if (status === window.kakao.maps.services.Status.OK) {
                        const detailAddr = result[0].road_address
                            ? `<div>도로명주소 : ${result[0].road_address.address_name}</div>`
                            : "";
                        const content = `
                            <div class="bAddr">
                                <span class="title">법정동 주소정보</span>
                                  ${detailAddr}
                                <div>지번 주소 : ${result[0].address.address_name}</div>
                            </div>`;

                        if (detailAddr != "") {
                            this.form.place = result[0].road_address.address_name;
                        } else {
                            this.form.place = result[0].address.address_name;
                        }

                        // 인포윈도우에 초기 주소를 표시
                        this.infowindow.setContent(content);
                        this.infowindow.open(this.map, marker);
                    }
               });

                // 현재 지도 중심좌표로 주소를 검색해서 지도 좌측 상단에 표시
                this.searchAddrFromCoords(this.map.getCenter(), this.displayCenterInfo);

                // 지도를 클릭했을 때 클릭 위치 좌표에 대한 주소정보 표시 이벤트 등록
                window.kakao.maps.event.addListener(this.map, 'click', (mouseEvent) => {
                    this.searchDetailAddrFromCoords(mouseEvent.latLng, (result, status) => {
                        if (status === window.kakao.maps.services.Status.OK) {
                            const detailAddr = result[0].road_address
                                ? `<div>도로명주소 :  ${result[0].road_address.address_name}</div>` : "";
                            const content = `
                                <div class="bAddr">
                                    ${detailAddr}
                                    <div>지번 주소 : ${result[0].address.address_name}</div>
                                </div>`;

                            if (detailAddr != "") {
                                this.form.place = result[0].road_address.address_name;
                            } else {
                                this.form.place = result[0].address.address_name;
                            }

                            // 마커를 클릭한 위치에 표시
                            marker.setPosition(mouseEvent.latLng);
                            // 인포윈도우에 클릭한 위치에 대한 법정동 상세 주소정보를 표시합니다
                            this.infowindow.setContent(content);
                            this.infowindow.open(this.map, marker)
                        }
                    });
                });

                // 중심 좌표나 확대 수준이 변경됐을 때 지도 중심 좌표에 대한 주소 정보를 표시하도록 이벤트를 등록합니다
                window.kakao.maps.event.addListener(this.map, 'idle', () => {
                    this.searchAddrFromCoords(this.map.getCenter(), this.displayCenterInfo);
                });
            })
            .catch((error) => {
                console.log(error);
        });
    },
    searchAddrFromCoords(coords, callback) {
        // 좌표로 행정동 주소 정보를 요청합니다
        this.geocoder.coord2RegionCode(coords.getLng(), coords.getLat(), callback);
    },
    searchDetailAddrFromCoords(coords, callback) {
        // 좌표로 법정동 상세 주소 정보를 요청합니다
        this.geocoder.coord2Address(coords.getLng(), coords.getLat(), callback);
    },
    // 지도 좌측상단에 지도 중심좌표에 대한 주소정보를 표출하는 함수입니다
    displayCenterInfo(result, status) {
        if (status === window.kakao.maps.services.Status.OK) {
            const infoDiv = document.getElementById('centerAddr');

            for(let i = 0; i < result.length; i++) {
                // 행정동의 region_type 값은 'H' 이므로
                if (result[i].region_type === 'H') {
                    infoDiv.innerHTML = result[i].address_name;
                    break;
                }
            }
        }
    },
  },
  mounted() {
    if (window.kakao && window.kakao.maps) {
        this.loadMap();
    } else {
        this.loadScript();
    }
  },
};
</script>

<style scoped>
#map {
    width: 100%;
    height: 500px;
    margin-bottom: 20px;
}

.map_wrap {
  position: relative;
  width: 100%;
  height: 500px;
  margin-bottom: 20px;
}
.title {
  font-weight: bold;
  display: block;
}
.hAddr {
  position: absolute;
  left: 10px;
  top: 10px;
  font-size: small;
  border-radius: 2px;
  background: #fff;
  background: rgba(255, 255, 255, 0.8);
  z-index: 1;
  padding: 5px;
}
#centerAddr {
  display: block;
  margin-top: 2px;
  font-weight: normal;
}
.bAddr {
  padding: 5px;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  font-size: small;
}
.place-input {
  display: inline-block;
  width: 70%;
  margin: 10px;
}
</style>