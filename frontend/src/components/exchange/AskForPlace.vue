<template>
    <div>
        <div class="container-xxl py-6">
            <div class="container-fluid wow fadeIn ask-for-place-select" data-wow-delay="0.1s">
                <h1 class="info-title">현재 위치를 변경합니다.</h1>
                <p>지도의 마커를 움직여 희망하시는 위치를 알려주세요.</p>
                <div class="map_wrap">
                    <div id="map" style="width:100%;height:100%;position:relative;overflow:hidden;"></div>
                    <div class="hAddr">
                        <span class="title">지도중심기준 행정동 주소정보</span>
                        <span id="centerAddr"></span>
                    </div>
                </div>
                <p class="select-address">{{ place }}</p>
                <div class="container btn-area">
                    <button type="button" class="btn btn-primary" @click="selectPlaceComplete">완료</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'AskForPlace',
    data() {
        return {
            place: null,
            map: null,
            geocoder: null,
            infowindow: null,
            latitude: 0,
            longitude: 0,
        };
    },
    methods: {
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
                                this.place = result[0].road_address.address_name;
                            } else {
                                this.place = result[0].address.address_name;
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
                                    this.place = result[0].road_address.address_name;
                                } else {
                                    this.place = result[0].address.address_name;
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

                    this.relayoutMap();
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

                for (let i = 0; i < result.length; i++) {
                    // 행정동의 region_type 값은 'H' 이므로
                    if (result[i].region_type === 'H') {
                        infoDiv.innerHTML = result[i].address_name;
                        break;
                    }
                }
            }
        },
        goToExchange() {
            this.$router.push("/exchange");
        },
        selectPlaceComplete() {
            this.$axios
                .post(`/api/user/updatePlace`, { place: this.place })
                .then((response) => {
                    alert(response.data.message);
                    this.$router.push("/exchange");
                })
                .catch((error) => {
                    alert(error);
                })
        }
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
.ask-for-place-select {
    text-align: center;
    margin: 100px auto;
}

.ask-for-place-info p {
    margin: 0;
}

.ask-for-place-info p span {
    font-weight: bold;
    color: #3CB815;
}

.btn-area {
    margin: 30px auto;
}

.btn-area button {
    padding: 15px 60px;
    border-radius: 10px;
}

.link-skip {
    display: block;
    text-align: center;
    text-decoration: underline;
    color: gray;
    margin-top: 20px;
}

.info-title {
    margin-bottom: 30px;
    font-family: 'NanumSquareRound';
    font-weight: bold;
    font-style: normal;
}

#map {
    width: 100%;
    height: 100%;
}

.map_wrap {
    position: relative;
    width: 80%;
    height: 500px;
    margin: auto auto 30px auto;
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

.select-address {
    font-weight: bold;
    color: #3CB815;
    font-size: 20px;
}
</style>