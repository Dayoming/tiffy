<template>
    <div class="container-xxl py-6">
        <div class="container py-6">
            <h1>상품 상세보기</h1>
            <div>
                <p><strong>상품명:</strong> <span>{{item.itemNm}}</span></p>
                <p><strong>카테고리:</strong> <span>{{item.itemCategory}}</span></p>
                <p><strong>판매자 아이디:</strong> <span>{{item.sellerId}}</span></p>
                <p><strong>판매자 이름:</strong> <span>{{item.sellerNm}}</span></p>
                <p><strong>가격:</strong> <span>{{item.price}}</span>원</p>
                <p><strong>수량:</strong> <span>{{item.stockNumber}}</span></p>
                <p><strong>상품 설명:</strong> <span>{{item.itemDetail}}</span></p>
                <p><strong>판매 상태:</strong> <span>{{item.itemSellStatus}}</span></p>
                <p><strong>등록 시간:</strong> <span>{{item.regTime}}</span></p>
                <p><strong>수정 시간:</strong> <span>{{item.updateTime}}</span></p>
                <button type="button" class="btn btn-light" @click="goBack">Back</button>
                <button type="button" class="btn btn-primary" @click="goToEdit(item.id)">Edit</button>
                <button type="button" class="btn btn-danger" @click="goToDelete(item.id)">Delete</button>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "ExchangeDetail",
        data() {
            return {
                item: []
            };
        },
        methods: {
            goBack() {
                this.$router.push('/exchange');
            },
            goToEdit(id) {
                this.$router.push({ path: `/exchange/edit/${id} `});
            },
            goToDelete(id) {
                this.$axios.delete(`http://localhost:8081/api/exchange/delete/${id}`)
                    .then(response => {
                        alert(response.data.message);
                        this.$router.push({ path: `/exchange`});
                    })
                    .catch(error => {
                        alert(response.data.error);
                    });
            },
            fetchData() {
                this.$axios.get(`http://localhost:8081/api/exchange/${this.$route.params.id}`)
                    .then(response => {
                        this.item = response.data;
                    })
                    .catch(error => {
                        console.error("데이터를 가져오는 중 오류 발생:", error);
                    });
            },
        },
        mounted() {
            this.fetchData();
        },
    };
</script>

<style scope>

</style>