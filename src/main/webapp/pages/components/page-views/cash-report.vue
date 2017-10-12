<!-- components/page-views/cash-report.vue :: start -->
<template id="components-page-views-cash-report">
    <div class="report">
        <div>
            <el-row>
                <h1 class="h1">{{ text }}</h1>
            </el-row>
            <el-row>
                <el-button type="success">Сохранить</el-button>
            </el-row>
            <el-row>
                <el-form>
                    <el-form-item label="Отчетная дата">
                        <el-date-picker type="date" placeholder="Отчетная дата" v-model="formData.date"
                                        :format="'dd.MM.yyyy'"></el-date-picker>
                    </el-form-item>
                </el-form>
            </el-row>
            <el-row>
                <el-col :span="8">
                    <el-form label-width="120px">
                        <h3 class="h4">{{formData.type1.text}}</h3>
                        <el-form-item label="Клубные карты" prop="val1">
                            <el-input-number v-model="formData.type1.val1"></el-input-number>
                        </el-form-item>
                        <el-form-item label="SPA" prop="val2">
                            <el-input-number v-model="formData.type1.val2"></el-input-number>
                        </el-form-item>
                        <el-form-item label="Кафе" prop="val3">
                            <el-input-number v-model="formData.type1.val3"></el-input-number>
                        </el-form-item>
                        <el-form-item label="Фитнес услуги" prop="val4">
                            <el-input-number v-model="formData.type1.val4"></el-input-number>
                        </el-form-item>
                        <el-form-item label="прочие" prop="val5">
                            <el-input-number v-model="formData.type1.val5"></el-input-number>
                        </el-form-item>
                        <el-form-item label="спортивное питание" prop="val6">
                            <el-input-number v-model="formData.type1.val6"></el-input-number>
                        </el-form-item>
                        <el-form-item label="магазин" prop="val7">
                            <el-input-number v-model="formData.type1.val7"></el-input-number>
                        </el-form-item>
                        <el-form-item label="Депозит" prop="val8">
                            <el-input-number v-model="formData.type1.val8"></el-input-number>
                        </el-form-item>
                    </el-form>
                </el-col>
                <el-col :span="8">
                    <el-form label-width="120px">
                        <h3 class="h4">{{formData.type2.text}}</h3>
                        <el-form-item label="Клубные карты" prop="val1">
                            <el-input-number v-model="formData.type2.val1"></el-input-number>
                        </el-form-item>
                        <el-form-item label="SPA" prop="val2">
                            <el-input-number v-model="formData.type2.val2"></el-input-number>
                        </el-form-item>
                        <el-form-item label="Кафе" prop="val3">
                            <el-input-number v-model="formData.type2.val3"></el-input-number>
                        </el-form-item>
                        <el-form-item label="Фитнес услуги" prop="val4">
                            <el-input-number v-model="formData.type2.val4"></el-input-number>
                        </el-form-item>
                        <el-form-item label="прочие" prop="val5">
                            <el-input-number v-model="formData.type2.val5"></el-input-number>
                        </el-form-item>
                        <el-form-item label="спортивное питание" prop="val6">
                            <el-input-number v-model="formData.type2.val6"></el-input-number>
                        </el-form-item>
                        <el-form-item label="магазин" prop="val7">
                            <el-input-number v-model="formData.type2.val7"></el-input-number>
                        </el-form-item>
                        <el-form-item label="Депозит" prop="val8">
                            <el-input-number v-model="formData.type2.val8"></el-input-number>
                        </el-form-item>
                    </el-form>
                </el-col>
                <el-col :span="8">
                    <el-form label-width="120px">
                        <h3 class="h4">{{formData.type3.text}}</h3>
                        <el-form-item label="Клубные карты" prop="val1">
                            <el-input-number v-model="formData.type3.val1"></el-input-number>
                        </el-form-item>
                        <el-form-item label="SPA" prop="val2">
                            <el-input-number v-model="formData.type3.val2"></el-input-number>
                        </el-form-item>
                        <el-form-item label="Кафе" prop="val3">
                            <el-input-number v-model="formData.type3.val3"></el-input-number>
                        </el-form-item>
                        <el-form-item label="Фитнес услуги" prop="val4">
                            <el-input-number v-model="formData.type3.val4"></el-input-number>
                        </el-form-item>
                        <el-form-item label="прочие" prop="val5">
                            <el-input-number v-model="formData.type3.val5"></el-input-number>
                        </el-form-item>
                        <el-form-item label="спортивное питание" prop="val6">
                            <el-input-number v-model="formData.type3.val6"></el-input-number>
                        </el-form-item>
                        <el-form-item label="магазин" prop="val7">
                            <el-input-number v-model="formData.type3.val7"></el-input-number>
                        </el-form-item>
                        <el-form-item label="Депозит" prop="val8">
                            <el-input-number v-model="formData.type3.val8"></el-input-number>
                        </el-form-item>
                    </el-form>
                </el-col>
            </el-row>
        </div>
        <div>
            <router-view></router-view>
        </div>
    </div>
</template>
<!-- components/page-views/cash-report.vue :: middle -->
<style>
    body {
        font-family: Verdana, Arial, sans-serif;
    }

    .el-input-number input {
        text-align: right;
    }

    .report {
        padding: 20px;
    }
</style>
<!-- components/page-views/cash-report.vue :: middle -->
<script>

    (function ($) {
        ELEMENT.locale(ELEMENT.lang.ruRU)
        var componentName = 'components-page-views-cash-report';
        Vue.component(componentName, {
            template: '#' + componentName,
            data: function () {
                return {
                    text: 'Кассовый отчет',
                    formData: {
                        date: "",
                        type1: {
                            text: "Наличный расчет",
                            val1: 0,
                            val2: 0,
                            val3: 0,
                            val4: 0,
                            val5: 0,
                            val6: 0,
                            val7: 0,
                            val8: 0
                        },
                        type2: {
                            text: "Безналичный расчет-терминал",
                            val1: 0,
                            val2: 0,
                            val3: 0,
                            val4: 0,
                            val5: 0,
                            val6: 0,
                            val7: 0,
                            val8: 0
                        },
                        type3: {
                            text: "Безналичный расчет банк",
                            val1: 0,
                            val2: 0,
                            val3: 0,
                            val4: 0,
                            val5: 0,
                            val6: 0,
                            val7: 0,
                            val8: 0
                        },
                    }
                };
            },
            computed: Vuex.mapState({
                account: state => state.account,
                c: function () {
                    return 'c';
                },
                b: state => 'b'
            }),
            methods: {}
        });
    })(jQuery);
</script>
<!-- components/page-views/cash-report.vue :: end -->