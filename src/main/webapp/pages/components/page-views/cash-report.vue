<!-- components/page-views/cash-report.vue :: start -->
<template id="components-page-views-cash-report">
	<div>
		<div>
            <el-row>
                <el-button type="success">Сохранить</el-button>
			<div class="h1">{{ text }}</div>
            </el-row>
            <div class="h1">Отчетная дата</div>
                <el-date-picker  type="date" placeholder="Отчетная дата" v-model="formData.date" :format="'dd.MM.yyyy'"
                ></el-date-picker>
            <el-row>
                <el-col :span="8">
			<el-form label-width="120px">
				<div class="h4">{{formData.type1.text}}</div>
				<el-form-item label="Клубные карты" prop="val1">
					<el-input-number  v-model="formData.type1.val1"
					></el-input-number>
				</el-form-item>
				<el-form-item label="SPA" prop="val2">
					<el-input-number  v-model="formData.type1.val2"
					></el-input-number>
				</el-form-item>
				<el-form-item label="Кафе" prop="val3">
					<el-input-number  v-model="formData.type1.val3"
					></el-input-number>
				</el-form-item>
				<el-form-item label="Фитнес услуги" prop="val4">
					<el-input-number  v-model="formData.type1.val4"
					></el-input-number>
				</el-form-item>
				<el-form-item label="прочие" prop="val5">
					<el-input-number  v-model="formData.type1.val5"
					></el-input-number>
				</el-form-item>
				<el-form-item label="спортивное питание" prop="val6">
					<el-input-number  v-model="formData.type1.val6"
					></el-input-number>
				</el-form-item>
				<el-form-item label="магазин" prop="val7">
					<el-input-number  v-model="formData.type1.val7"
					></el-input-number>
				</el-form-item>
				<el-form-item label="Депозит" prop="val8">
					<el-input-number  v-model="formData.type1.val8"
					></el-input-number>
				</el-form-item>
			</el-form>
                </el-col>
                <el-col :span="8">
                    <el-form label-width="120px">
                        <div class="h4">{{formData.type2.text}}</div>
                        <el-form-item label="Клубные карты" prop="val1">
                            <el-input-number  v-model="formData.type2.val1"
                            ></el-input-number>
                        </el-form-item>
                        <el-form-item label="SPA" prop="val2">
                            <el-input-number  v-model="formData.type2.val2"
                            ></el-input-number>
                        </el-form-item>
                        <el-form-item label="Кафе" prop="val3">
                            <el-input-number  v-model="formData.type2.val3"
                            ></el-input-number>
                        </el-form-item>
                        <el-form-item label="Фитнес услуги" prop="val4">
                            <el-input-number  v-model="formData.type2.val4"
                            ></el-input-number>
                        </el-form-item>
                        <el-form-item label="прочие" prop="val5">
                            <el-input-number  v-model="formData.type2.val5"
                            ></el-input-number>
                        </el-form-item>
                        <el-form-item label="спортивное питание" prop="val6">
                            <el-input-number  v-model="formData.type2.val6"
                            ></el-input-number>
                        </el-form-item>
                        <el-form-item label="магазин" prop="val7">
                            <el-input-number  v-model="formData.type2.val7"
                            ></el-input-number>
                        </el-form-item>
                        <el-form-item label="Депозит" prop="val8">
                            <el-input-number  v-model="formData.type2.val8"
                            ></el-input-number>
                        </el-form-item>
                    </el-form>
                </el-col>
                </el-col>
                <el-col :span="8">
                    <el-form label-width="120px">
                        <div class="h4">{{formData.type3.text}}</div>
                        <el-form-item label="Клубные карты" prop="val1">
                            <el-input-number  v-model="formData.type3.val1"
                            ></el-input-number>
                        </el-form-item>
                        <el-form-item label="SPA" prop="val2">
                            <el-input-number  v-model="formData.type3.val2"
                            ></el-input-number>
                        </el-form-item>
                        <el-form-item label="Кафе" prop="val3">
                            <el-input-number  v-model="formData.type3.val3"
                            ></el-input-number>
                        </el-form-item>
                        <el-form-item label="Фитнес услуги" prop="val4">
                            <el-input-number  v-model="formData.type3.val4"
                            ></el-input-number>
                        </el-form-item>
                        <el-form-item label="прочие" prop="val5">
                            <el-input-number  v-model="formData.type3.val5"
                            ></el-input-number>
                        </el-form-item>
                        <el-form-item label="спортивное питание" prop="val6">
                            <el-input-number  v-model="formData.type3.val6"
                            ></el-input-number>
                        </el-form-item>
                        <el-form-item label="магазин" prop="val7">
                            <el-input-number  v-model="formData.type3.val7"
                            ></el-input-number>
                        </el-form-item>
                        <el-form-item label="Депозит" prop="val8">
                            <el-input-number  v-model="formData.type3.val8"
                            ></el-input-number>
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
        font: normal 10px Verdana, Arial, sans-serif;
    }
    .el-input-number input {
        text-align: right;
    }
</style>
<!-- components/page-views/cash-report.vue :: middle -->
<script>

	(function($) {
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
				account: state=>state.account,
				c: function() {
					return 'c';
				},
				b: state => 'b'
			}),
			methods: {}
		});

		WorldClassPlugins.plugins.push({
			parameters: ['store', 'router'],
			name: componentName,
			dependencies: [],
			install: function(Vue, store, router) {
				router.addRoutes([
					{
						path: '/cash-report', component: Vue.component(componentName),
						meta: {
							requiresAuthorization: true,
							requiredRoles: ['ROLE_ADMIN', 'ROLE_FILIAL_USER']
						}
					}
				]);

				var info = {
					treePath: ['Отчеты', 'Кассовый отчет'],
					route: {path: '/cash-report'},
					requiresAuthorization: true,
					requiredRoles: ['ROLE_ADMIN', 'ROLE_FILIAL_USER'],
					order: 0
				};
				store.commit('menu/add', info);
			}
		});
	})(jQuery);
</script>
<!-- components/page-views/cash-report.vue :: end -->