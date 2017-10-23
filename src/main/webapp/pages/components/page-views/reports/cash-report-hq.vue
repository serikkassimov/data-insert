<!-- components/page-views/reports/cash-report-hq.vue :: start -->
<template id="page-views-reports-cash-report-hq">
	<div class="h-100">
		<div class="card h-100">
			<h5 class="card-header" v-loading.body="loading">
				Кассовые отчеты филиалов
				<el-button @click="reloadAll">Обновить все</el-button>
				<el-button @click="reloadData">Обновить данные</el-button>
				<el-select v-model="data.orgId" @change="reloadData">
					<el-option v-for="org in dict.org.items" :key="org.id" :label="org.name" :value="org.id"></el-option>
				</el-select>
				<el-date-picker v-model="data.date" type="date" format="dd.MM.yyyy" :clearable="false" @change="reloadData"></el-date-picker>
			</h5>
			<h5 class="card-header" v-loading.body="loading">
				<template v-if="data.state.code === 'READY_FOR_APPROVAL'">
					Статус: на утверждении
					<el-button @click="approve">Утвердить</el-button>
				</template>
				<template v-else-if="data.state.code === 'APPROVED'">Статус: утверждено</template>
				<template v-else>Статус: новый отчет</template>
			</h5>
			<div class="card-body" style="overflow: auto;" v-loading.body="loading">
				<table class="table table-bordered table-hover table-sm table-responsive h-100">
					<thead class="thead-default">
						<tr>
							<th>#</th>
							<th>Название</th>
							<th v-for="currency in dict.currency.items" :key="currency.id">{{currency.name}}</th>
						</tr>
					</thead>
					<tbody style="overflow: scroll;">
						<template v-for="budgetStoreType in dict.budgetStoreType.items">
							<tr :key="budgetStoreType.id">
								<th :colspan="2 + dict.currency.items.length">{{budgetStoreType.name}}</th>
							</tr>
							<tr v-for="(budget, index) in dict.budget.items" :key="budgetStoreType.id + '-' + budget.id">
								<td>{{index}}</td>
								<td>{{budget.name}}</td>
								<template v-if="data.initialized">
									<td
										v-for="currency in dict.currency.items" :key="currency.id"
										:class="{
											'table-secondary': (data.items['' + budgetStoreType.id]['' + budget.id]['' + currency.id] > 0)
										}"
									>
										{{data.items['' + budgetStoreType.id]['' + budget.id]['' + currency.id]}}
										{{currency.symbol}}
									</td>
								</template>
								<template v-else>
									<td v-for="currency in dict.currency.items" :key="currency.id">
										{{currency.symbol}}
									</td>
								</template>
							</tr>
							<tr :key="budgetStoreType.id">
								<th></th>
								<th>Итого</th>
								<th v-for="currency in dict.currency.items" :key="currency.id">{{sum.byTable['' + currency.id]['' + budgetStoreType.id]}} {{currency.symbol}}</th>
							</tr>
						</template>
					</tbody>
					<thead>
						<tr>
							<td></td>
							<td></td>
							<td v-for="currency in dict.currency.items" :key="currency.id"></td>
						</tr>
						<tr>
							<th colspan="2">Общий итог</th>
							<th v-for="currency in dict.currency.items" :key="currency.id">{{sum.total['' + currency.id]}} {{currency.symbol}}</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>
	</div>
</template>
<!-- components/page-views/reports/cash-report-hq.vue :: middle -->
<script>
(function($) {
	var componentName = 'page-views-reports-cash-report-hq';
	var ajaxRoot = WorldClassRestRoot + '/cash-report';
	Vue.component(componentName, {
		template: '#' + componentName,
		data: function() {
			var date = new Date();
			date.setMilliseconds(0);
			date.setSeconds(0);
			date.setMinutes(0);
			date.setHours(0);
			return {
				dict: {
					org: {
						items: [],
						loading: false
					},
					budgetStoreType: {
						items: [],
						loading: false
					},
					budget: {
						items: [],
						loading: false
					},
					currency: {
						items: [],
						loading: false
					}
				},
				data: {
					orgId: null,
					date: date,
					items: {},
					state: {
						code: null
					},
					loading: false,
					initialized: false
				}
			};
		},
		computed: Vuex.mapState({
			dictLoading: function() {
				return this.dict.org.loading || this.dict.budgetStoreType.loading || this.dict.budget.loading || this.dict.currency.loading;
			},
			loading: function() {
				return this.dictLoading || this.data.loading;
			},
			sum: function() {
				var result = {
					byTable: {},
					total: {}
				};

				for (var currencyIndex in this.dict.currency.items) {
					var currencyId = '' + this.dict.currency.items[currencyIndex].id;
					if (!isObject(result.byTable[currencyId])) result.byTable[currencyId] = {};
					if (!isComparableNumber(result.total[currencyId])) result.total[currencyId] = 0;
					for (var budgetStoreTypeIndex in this.dict.budgetStoreType.items) {
						var budgetStoreTypeId = this.dict.budgetStoreType.items[budgetStoreTypeIndex].id;
						if (!isComparableNumber(result.byTable[currencyId][budgetStoreTypeId])) result.byTable[currencyId][budgetStoreTypeId] = 0;
					}
				}

				for (var budgetStoreTypeIndex in this.dict.budgetStoreType.items) {
					var budgetStoreTypeId = '' + this.dict.budgetStoreType.items[budgetStoreTypeIndex].id;
					if (!isObject(this.data.items[budgetStoreTypeId])) continue;
					for (var budgetIndex in this.dict.budget.items) {
						var budgetId = '' + this.dict.budget.items[budgetIndex].id;
						if (!isObject(this.data.items[budgetStoreTypeId][budgetId])) continue;
						for (var currencyIndex in this.dict.currency.items) {
							var currencyId = '' + this.dict.currency.items[currencyIndex].id;
							var value = this.data.items[budgetStoreTypeId][budgetId][currencyId];
							if (isComparableNumber(value)) {
								result.total[currencyId] += value;
								result.byTable[currencyId][budgetStoreTypeId] += value;
							}
						}
					}
				}

				return result;
			}
		}),
		methods: {
			initData: function() {
				if (
					(!this.dict.budgetStoreType.loading) && (this.dict.budgetStoreType.items.length > 0)
					&& (!this.dict.budget.loading) && (this.dict.budget.items.length > 0)
					&& (!this.dict.currency.loading) && (this.dict.currency.items.length > 0)
					&& (!this.data.loading)
				) {
					var items = $.extend(true, {}, this.data.items);
					for (var budgetStoreTypeIndex in this.dict.budgetStoreType.items) {
						var budgetStoreTypeId = '' + this.dict.budgetStoreType.items[budgetStoreTypeIndex].id;
						if (!isObject(items[budgetStoreTypeId])) items[budgetStoreTypeId] = {};
						for (var budgetIndex in this.dict.budget.items) {
							var budgetId = '' + this.dict.budget.items[budgetIndex].id;
							if (!isObject(items[budgetStoreTypeId][budgetId])) items[budgetStoreTypeId][budgetId] = {};
							for (var currencyIndex in this.dict.currency.items) {
								var currencyId = '' + this.dict.currency.items[currencyIndex].id;
								if (!isComparableNumber(items[budgetStoreTypeId][budgetId][currencyId])) items[budgetStoreTypeId][budgetId][currencyId] = 0;
							}
						}
					}
					this.data.items = $.extend(true, {}, items);
					this.data.initialized = true;
				}
			},
			reloadAll: function() {
				this.reloadOrgs();
				this.reloadDictBudgetStoreTypes();
				this.reloadBudgets();
				this.reloadCurrencies();
			},
			reloadOrgs: function() {
				if (this.dict.org.loading) return;

				this.dict.org.loading = true;
				this.dict.org.items = [];

				$.ajax({
					url: ajaxRoot + '/dict-orgs',
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						console.error('cannot load orgs:', textStatus, ',', errorThrown);
						this.$notify.error({
							title: 'Ошибка',
							message: 'Не удалось загрузить организации'
						});
					},
					success: function(data, textStatus, jqXHR) {
						var newId = null;
						if (isNonEmptyArray(data)) {
							for (var index in data) {
								var item = data[index];
								if (isObject(item) && isComparableNumber(item.id)) {
									newId = item.id;
									break;
								}
							}
						}
						this.dict.org.items = data;
						this.data.orgId = newId;
						this.reloadData();
					},
					complete: function(jqXHR, textStatus) {
						this.dict.org.loading = false;
					}
				});
			},
			reloadDictBudgetStoreTypes: function() {
				if (this.dict.budgetStoreType.loading) return;

				this.dict.budgetStoreType.loading = true;
				this.dict.budgetStoreType.items = [];

				$.ajax({
					url: ajaxRoot + '/dict-budget-store-types',
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						console.error('cannot load budget store types:', textStatus, ',', errorThrown);
						this.$notify.error({
							title: 'Ошибка',
							message: 'Не удалось загрузить типы хранения бюджета'
						});
					},
					success: function(data, textStatus, jqXHR) {
						this.dict.budgetStoreType.items = data;
					},
					complete: function(jqXHR, textStatus) {
						this.dict.budgetStoreType.loading = false;
						this.initData();
					}
				});
			},
			reloadBudgets: function() {
				if (this.dict.budget.loading) return;

				this.dict.budget.loading = true;
				this.dict.budget.items = [];

				$.ajax({
					url: ajaxRoot + '/dict-budgets',
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						console.error('cannot load budget store types:', textStatus, ',', errorThrown);
						this.$notify.error({
							title: 'Ошибка',
							message: 'Не удалось загрузить статьи бюджета'
						});
					},
					success: function(data, textStatus, jqXHR) {
						this.dict.budget.items = data;
					},
					complete: function(jqXHR, textStatus) {
						this.dict.budget.loading = false;
						this.initData();
					}
				});
			},
			reloadCurrencies: function() {
				if (this.dict.currency.loading) return;

				this.dict.currency.loading = true;
				this.dict.currency.items = [];

				$.ajax({
					url: ajaxRoot + '/dict-currencies',
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						console.error('cannot load budget store types:', textStatus, ',', errorThrown);
						this.$notify.error({
							title: 'Ошибка',
							message: 'Не удалось загрузить валюты'
						});
					},
					success: function(data, textStatus, jqXHR) {
						this.dict.currency.items = data;
					},
					complete: function(jqXHR, textStatus) {
						this.dict.currency.loading = false;
						this.initData();
					}
				});
			},
			reloadData: function() {
				if (this.data.loading) return;

				if (!isDate(this.data.date)) {
					console.error(this.data.date, 'is not date');
					this.$notify.error({title: 'Ошибка', message: 'Не удалось загрузить данные: ошибка при определении даты'});
					return;
				}

				if (!isComparableNumber(this.data.orgId)) {
					console.error(this.data.orgId, 'is not comparable number');
					this.$notify.error({title: 'Ошибка', message: 'Не удалось загрузить данные: ошибка при определении ИД организации'});
					return;
				}

				this.data.loading = true;
				this.data.initialized = false;
				this.data.items = {};
				this.data.original = {};

				$.ajax({
					url: ajaxRoot + '/hq/data/get?date=' + this.data.date.getTime() + '&org-id=' + this.data.orgId,
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						console.error('cannot load data:', textStatus, ',', errorThrown);
						this.$notify.error({
							title: 'Ошибка',
							message: 'Не удалось загрузить данные'
						});
					},
					success: function(data, textStatus, jqXHR) {
						if (isObject(data)) {
							if (data.type === 'SUCCESS') {
								var prepared = {};
								if (isObject(data.data)) {
									if (isNonEmptyArray(data.data.items)) {
										for (var itemIndex in data.data.items) {
											var item = data.data.items[itemIndex];
											var budgetStoreTypeId = '' + item.storeType.id;
											var budgetId = '' + item.budgetType.id;
											var currencyId = '' + item.currency.id;
											if (!isObject(prepared[budgetStoreTypeId])) prepared[budgetStoreTypeId] = {};
											if (!isObject(prepared[budgetStoreTypeId][budgetId])) prepared[budgetStoreTypeId][budgetId] = {};
											prepared[budgetStoreTypeId][budgetId][currencyId] = item.itemValue;
										}
									}
									if (isObject(data.data.state) && isNonEmptyString(data.data.state.code)) {
										this.data.state.code = data.data.state.code;
									}
								}
								this.data.items = prepared;
							} else if (data.type === 'NO_ORG_ID') {
								this.$notify.error({title: 'Ошибка', message: 'Не удалось загрузить данные: не передан ИД организации'});
							} else if (data.type === 'ORG_NOT_FOUND') {
								this.$notify.error({title: 'Ошибка', message: 'Не удалось загрузить данные: организация не найдена'});
							} else if (data.type === 'CHANGE_TYPE_NOT_FOUND') {
								this.$notify.error({title: 'Ошибка', message: 'Не удалось загрузить данные: тип изменения не найден'});
							} else {
								console.error('unknown data type:', data.type);
								this.$notify.error({title: 'Ошибка', message: 'Не удалось загрузить данные: неизвестный ответ сервера'});
							}
						} else {
							console.error('unknown data:', data);
							this.$notify.error({
								title: 'Ошибка',
								message: 'Не удалось загрузить данные: неизвестный ответ сервера'
							});
						}
					},
					complete: function(jqXHR, textStatus) {
						this.data.loading = false;
						this.initData();
					}
				});
			},
			approve: function() {
				if (this.data.loading) return;

				if (!isDate(this.data.date)) {
					console.error(this.data.date, 'is not date');
					this.$notify.error({title: 'Утверждение данных', message: 'Не удалось утвердить данные: ошибка при определении даты'});
					return;
				}

				if (!isComparableNumber(this.data.orgId)) {
					console.error(this.data.orgId, 'is not comparable number');
					this.$notify.error({title: 'Утверждение данных', message: 'Не удалось утвердить данные: ошибка при определении ИД организации'});
					return;
				}

				this.data.loading = true;

				$.ajax({
					url: ajaxRoot + '/hq/approve?org-id=' + this.data.orgId + '&date=' + this.data.date.getTime(),
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						console.error('error while approving data:', textStatus, ' - ', errorThrown);
						this.$notify.error({title: 'Утверждение данных', message: 'Ошибка при утверждении данных'});
					},
					success: function(data, textStatus, jqXHR) {
						if (data === 'SUCCESS') {
							this.$notify.success({title: 'Утверждение данных', message: 'Данные утверждены'});
						} else if (data === 'NO_ORG_ID') {
							this.$notify.error({title: 'Утверждение данных', message: 'Ошибка при утверждении данных: ИД организации не передан'});
						} else if (data === 'NO_DATE') {
							this.$notify.error({title: 'Утверждение данных', message: 'Ошибка при утверждении данных: дата не передана'});
						} else if (data === 'ORG_NOT_FOUND') {
							this.$notify.error({title: 'Утверждение данных', message: 'Ошибка при утверждении данных: организация не найдена'});
						} else if (data === 'CHANGE_TYPE_NOT_FOUND') {
							this.$notify.error({title: 'Утверждение данных', message: 'Ошибка при утверждении данных: тип изменения "кассовый отчет" не найден'});
						} else if (data === 'STATE_NOT_FOUND') {
							this.$notify.error({title: 'Утверждение данных', message: 'Ошибка при утверждении данных: состояние "утвержден" не найдено'});
						} else if (data === 'CHANGE_NOT_FOUND') {
							this.$notify.error({title: 'Утверждение данных', message: 'Ошибка при утверждении данных: кассовый отчет не найден'});
						} else if (data === 'INCORRECT_STATE') {
							this.$notify.error({title: 'Утверждение данных', message: 'Ошибка при утверждении данных: некорректное состояние кассового отчета (не "отправлен на утверждение")'});
						} else {
							console.error('unknown server response', data);
							this.$notify.error({title: 'Утверждение данных', message: 'Ошибка при утверждении данных: неизвестный ответ сервера'});
						}
					},
					complete: function(jqXHR, textStatus) {
						this.data.loading = false;
						this.reloadData();
					}
				});
			}
		},
		mounted: function() {
			this.reloadAll();
		}
	});
})(jQuery);
</script>
<!-- components/page-views/reports/cash-report-hq.vue :: end -->