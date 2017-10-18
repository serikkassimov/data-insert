<!-- components/page-views/reports/cash-report-affiliate.vue :: start -->
<template id="page-views-reports-cash-report-affiliate">
	<div class="h-100">
		<div class="card h-100">
			<h5 class="card-header" v-loading.body="loading || (!data.initialized)">
				Кассовый отчет new
				<el-button @click="reloadAll">Обновить все</el-button>
				<el-button @click="reloadData">Обновить данные</el-button>
				<template v-if="hasChanges">
					<el-button type="primary" @click="save">Сохранить</el-button>
				</template>
				<template v-else>
					<el-button type="primary" disabled>Сохранить</el-button>
				</template>
			</h5>
			<div class="card-body" style="overflow: auto;" v-loading.body="loading || (!data.initialized)">
				<table class="table table-bordered table-hover table-sm table-responsive h-100">
					<colgroup>
						<col name="order-number"><col>
						<col name="budget-name"></col>
						<col v-for="currency in dict.currency.items" :key="currency.id" :name="'currency-' + currency.name"></col>
					</colgroup>
					<thead class="thead-default">
						<tr>
							<th>#</th>
							<th>Название</th>
							<th v-for="currency in dict.currency.items" :key="currency.id">{{currency.name}}</th>
						</tr>
					</thead>
					<tbody style="overflow: scroll;">
						<template v-for="budgetStoreType in dict.budgetStoreType.items">
							<!-- <thead :key="budgetStoreType.id"> -->
								<!-- <tr> -->
								<tr :key="budgetStoreType.id">
									<th :colspan="2 + dict.currency.items.length">{{budgetStoreType.name}}</th>
								</tr>
							<!-- </thead> -->
							<!-- <tbody :key="budgetStoreType.id"> -->
								<tr v-for="(budget, index) in dict.budget.items" :key="budgetStoreType.id + '-' + budget.id">
									<td>{{index}}</td>
									<td>{{budget.name}}</td>
									<template v-if="data.initialized">
										<td
											v-for="currency in dict.currency.items" :key="currency.id"
											:class="{
												'table-info': changed('' + budgetStoreType.id, '' + budget.id, '' + currency.id),
												'table-secondary': (!changed('' + budgetStoreType.id, '' + budget.id, '' + currency.id))
													&& (data.items['' + budgetStoreType.id]['' + budget.id]['' + currency.id] > 0)
											}"
										>
											<el-input-number size="small" v-model="data.items['' + budgetStoreType.id]['' + budget.id]['' + currency.id]" :min="0"></el-input-number>
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
							<!-- </tbody> -->
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
<!-- components/page-views/reports/cash-report-affiliate.vue :: middle -->
<script>
(function($){
	var componentName = 'page-views-reports-cash-report-affiliate';
	var ajaxRoot = WorldClassRestRoot + '/cash-report';
	Vue.component(componentName, {
		template: '#' + componentName,
		data: function() {
			return {
				dict: {
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
					items: {},
					original: {},
					loading: false,
					initialized: false
				}
			};
		},
		computed: Vuex.mapState({
			dictLoading: function() {
				return this.dict.budgetStoreType.loading || this.dict.budget.loading || this.dict.currency.loading;
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
			},
			hasChanges: function() {
				return this.data.initialized && (!equals(this.data.items, this.data.original));
			}
		}),
		methods: {
			changed: function(budgetStoreTypeId, budgetId, currencyId) {
				if (this.data.initialized) {
					return this.data.items[budgetStoreTypeId][budgetId][currencyId] !== this.data.original[budgetStoreTypeId][budgetId][currencyId];
				} else return false;
			},
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
					this.data.original = $.extend(true, {}, items);
					this.data.initialized = true;
				}
			},
			reloadAll: function() {
				this.reloadData();
				this.reloadDictBudgetStoreTypes();
				this.reloadBudgets();
				this.reloadCurrencies();
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

				this.data.loading = true;
				this.data.initialized = false;
				this.data.items = {};
				this.data.original = {};

				$.ajax({
					url: ajaxRoot + '/affiliate/data/get',
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
								if (isObject(data.data) && isNonEmptyArray(data.data.items)) {
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
								this.data.items = prepared;
							} else if (data.type === 'NO_LOGIN') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Не удалось загрузить данные: нет логина текущего пользователя'
								});
							} else if (data.type === 'USER_NOT_FOUND') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Не удалось загрузить данные: не найден текущий пользователь'
								});
							} else if (data.type === 'ORG_NOT_FOUND') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Не удалось загрузить данные: не найдена организация текущего пользователя'
								});
							} else if (data.type === 'CHANGE_TYPE_NOT_FOUND') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Не удалось загрузить данные: не найден тип изменения'
								});
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
			save: function() {
				if (this.data.loading) return;

				this.data.loading = true;

				var data = [];
				if (isObject(this.data.items)) {
					for (var budgetStoreTypeIndex in this.dict.budgetStoreType.items) {
						var budgetStoreTypeId = '' + this.dict.budgetStoreType.items[budgetStoreTypeIndex].id;
						if (isObject(this.data.items[budgetStoreTypeId])) {
							for (var budgetIndex in this.dict.budget.items) {
								var budgetId = '' + this.dict.budget.items[budgetIndex].id;
								if (isObject(this.data.items[budgetStoreTypeId][budgetId])) {
									for (var currencyIndex in this.dict.currency.items) {
										var currencyId = '' + this.dict.currency.items[currencyIndex].id;
										if (this.data.items[budgetStoreTypeId][budgetId][currencyId] > 0) {
											data.push({
												itemValue: this.data.items[budgetStoreTypeId][budgetId][currencyId],
												currency: {id: currencyId},
												storeType: {id: budgetStoreTypeId},
												budgetType: {id: budgetId}
											});
										}
									}
								}
							}
						}
					}
				}
				data = JSON.stringify({items: data});

				$.ajax({
					url: ajaxRoot + '/affiliate/data/save',
					method: 'POST',
					dataType: 'json',
					data: data,
					contentType: 'application/json',
					context: this,
					error: function() {
						this.data.loading = false;
						console.error('error while saving data:', textStatus, ' - ', errorThrown);
						this.$notify.error({
							title: 'Сохранение данных',
							message: 'Ошибка при сохранении данных'
						});
					},
					success: function(data, textStatus, jqXHR) {
						this.data.loading = false;
						if (data === 'SUCCESS') {
							this.$notify.success({title: 'Сохранение данных', message: 'Данные сохранены'});
							this.reloadData();
						} else if (data === 'NO_DATA') {
							this.$notify.error({title: 'Сохранение данных', message: 'Ошибка при сохранении данных: данные не переданы'});
						} else if (data === 'NO_LOGIN') {
							this.$notify.error({title: 'Сохранение данных', message: 'Ошибка при сохранении данных: не определен логин текущего пользователя'});
						} else if (data === 'USER_NOT_FOUND') {
							this.$notify.error({title: 'Сохранение данных', message: 'Ошибка при сохранении данных: текущий пользователь не найден'});
						} else if (data === 'ORG_NOT_FOUND') {
							this.$notify.error({title: 'Сохранение данных', message: 'Ошибка при сохранении данных: организация текущего пользователя не найдена'});
						} else if (data === 'CHANGE_TYPE_NOT_FOUND') {
							this.$notify.error({title: 'Сохранение данных', message: 'Ошибка при сохранении данных: не найден тип изменения бюджета'});
						} else if (data === 'NULL_ITEM') {
							this.$notify.error({title: 'Сохранение данных', message: 'Ошибка при сохранении данных: передан пустой элемент'});
						} else if (data === 'NO_VALUE') {
							this.$notify.error({title: 'Сохранение данных', message: 'Ошибка при сохранении данных: передан элемент без значения'});
						} else if (data === 'NEGATIVE_VALUE') {
							this.$notify.error({title: 'Сохранение данных', message: 'Ошибка при сохранении данных: передан элемент с отрицательным значением'});
						} else if (data === 'NO_BUDGET_TYPE') {
							this.$notify.error({title: 'Сохранение данных', message: 'Ошибка при сохранении данных: передан элемент без статьи бюджета'});
						} else if (data === 'BUDGET_TYPE_NOT_FOUND') {
							this.$notify.error({title: 'Сохранение данных', message: 'Ошибка при сохранении данных: статья бюджета не найдена'});
						} else if (data === 'NO_CURRENCY') {
							this.$notify.error({title: 'Сохранение данных', message: 'Ошибка при сохранении данных: передан элемент без валюты'});
						} else if (data === 'CURRENCY_NOT_FOUND') {
							this.$notify.error({title: 'Сохранение данных', message: 'Ошибка при сохранении данных: валюта не найдена'});
						} else if (data === 'NO_STORE_TYPE') {
							this.$notify.error({title: 'Сохранение данных', message: 'Ошибка при сохранении данных: передан элемент без типа хранения'});
						} else if (data === 'STORE_TYPE_NOT_FOUND') {
							this.$notify.error({title: 'Сохранение данных', message: 'Ошибка при сохранении данных: тип хранения не найден'});
						} else if (data === 'STATE_NOT_FOUND') {
							this.$notify.error({title: 'Сохранение данных', message: 'Ошибка при сохранении данных: состояние "новое изменение" не найдено'});
						} else {
							console.error('unknown save result', data);
						}
					},
					complete: function(jqXHR, textStatus) {}
				});
			}
		},
		mounted: function() {
			this.reloadAll();
		}
	});
})(jQuery);
</script>
<!-- components/page-views/reports/cash-report-affiliate.vue :: end -->