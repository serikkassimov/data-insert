<!-- components/page-views/requests/expenses-request-affiliate.vue :: start -->
<template id="page-views-requests-expenses-request-affiliate">
	<div class="h-100">
		<div class="card h-100">
			<h5 class="card-header" v-loading.body="loading">
				Заявка на расходы
			</h5>
			<h5 class="card-header" v-loading.body="loading">
				<el-button @click="reloadAll">Обновить все</el-button>
				<el-button @click="reloadData">Обновить данные</el-button>
				<el-date-picker type="date" format="dd.MM.yyyy" :clearable="false" v-model="data.date" @change="reloadData"></el-date-picker>
				<span class="text-secondary">|</span>
				<el-button @click="addItem">Добавить</el-button>
				<span class="text-secondary">|</span>
				<el-button @click="getCashReport">Заявка Excel</el-button>
				<el-button @click="save" :disabled="!changed">Сохранить</el-button>
			</h5>
			<div class="card-body" style="overflow: auto;" v-loading.body="loading">
				<table class="table table-bordered table-hover table-sm table-responsive">
					<thead>
						<tr>
							<th rowspan="2">#</th>
							<th rowspan="2"></th>
							<th rowspan="2" style="min-width: 200px;">Статья</th>
							<th rowspan="2" style="min-width: 200px;">Описание</th>
							<th v-for="budgetStoreType in dict.budgetStoreType.items" :key="budgetStoreType.id" :colspan="dict.currency.items.length">{{budgetStoreType.name}}</th>
						</tr>
						<tr>
							<template v-for="budgetStoreType in dict.budgetStoreType.items">
								<template v-for="currency in dict.currency.items">
									<th :key="budgetStoreType.id + '-' + currency.id">{{currency.name}}</th>
								</template>
							</template>
						</tr>
					</thead>
					<tbody>
						<tr v-for="(item, index) in data.items" :key="index">
							<td>{{ index + 1 }}</td>
							<td>
								<el-dropdown @command="itemCommand">
									<el-button type="primary">
										Действия<i class="el-icon-caret-bottom el-icon--right"></i>
									</el-button>
									<el-dropdown-menu slot="dropdown">
										<el-dropdown-item :command="{'type':'delete', 'index': index}">Удалить</el-dropdown-item>
									</el-dropdown-menu>
								</el-dropdown>
							</td>
							<td>
								<el-select filterable v-model="item.budgetId">
									<el-option v-for="budget in dict.budget.items" :key="budget.id" :label="'[' + budget.code + '] ' + budget.name" :value="budget.id"></el-option>
								</el-select>
							</td>
							<td>
								<el-input placeholder="Описание" type="textarea" v-model="item.note"></el-input>
							</td>
							<template v-for="budgetStoreType in dict.budgetStoreType.items">
								<template v-for="currency in dict.currency.items">
									<td :key="index + '-' + budgetStoreType.id + '-' + currency.id">
										<el-input-number
											v-model="item.values[budgetStoreType.code][currency.code]"
											:min="0"
										></el-input-number>
									</td>
								</template>
							</template>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</template>
<!-- components/page-views/requests/expenses-request-affiliate.vue :: middle -->
<script>
(function($) {
	var componentName = 'page-views-requests-expenses-request-affiliate';
	var ajaxRoot = WorldClassRestRoot + '/expenses-request';
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
					budget: {
						items: [],
						loading: false
					},
					budgetStoreType: {
						items:[],
						loading: false
					},
					currency: {
						items: [],
						loading: false
					}
				},
				data: {
					date: date,
					items: [],
					original: [],
					loading: false,
					initialized: false
				}
			};
		},
		computed: Vuex.mapState({
			dictLoading: function() {
				return this.dict.budget.loading || this.dict.budgetStoreType.loading || this.dict.currency.loading;
			},
			loading: function() {
				return this.dictLoading || this.data.loading;
			},
			changed: function() {
				var items = $.extend(true, [], this.data.items);
				var original = $.extend(true, [], this.data.original);

				var result = (!equals(items, original));
				return result;
			}
		}),
		methods: {
			initData: function() {
				if ((this.dict.budget.items.length > 0) && (this.dict.budgetStoreType.items.length > 0) && (this.dict.currency.items.length > 0)) {
					var items = $.extend(true, [], this.data.items);
					for (var index in items) {
						var item = items[index];
						if (!isObject(item.values)) item.values = {};
						if (!isComparableNumber(item.budgetId)) item.budgetId = this.dict.budget.items[0].id;
						for (var budgetStoreTypeIndex in this.dict.budgetStoreType.items) {
							var budgetStoreType = this.dict.budgetStoreType.items[budgetStoreTypeIndex];
							if (!isObject(item.values[budgetStoreType.code])) item.values[budgetStoreType.code] = {};
							for (var currencyIndex in this.dict.currency.items) {
								var currency = this.dict.currency.items[currencyIndex];
								if (!isComparableNumber(item.values[budgetStoreType.code][currency.code])) item.values[budgetStoreType.code][currency.code] = 0;
							}
						}
					}
					this.data.items = items;
					this.data.initialized = true;
				}
			},
			reloadAll: function() {
				this.reloadBudgets();
				this.reloadBudgetStoreTypes();
				this.reloadCurrencies();
				this.reloadData();
			},
            getCashReport: function () {
                console.log("getCashReport");
                var firstDay = this.data.date;
                window.open("cash/request_report?start=" + firstDay.getTime(), "_top")
            },
			reloadBudgets: function() {
				if (this.dict.budget.loading) return;

				this.dict.budget.loading = true;
				this.dict.budget.items = [];

				$.ajax({
					url: ajaxRoot + '/dict/budgets',
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						console.error('error while loading budgets:', textStatus, ' - ', errorThrown);
						this.$notify.error({title: 'Ошибка', message: 'Не удалось загрузить статьи изменения бюджета'});
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
			reloadBudgetStoreTypes: function() {
				if (this.dict.budgetStoreType.loading) return;

				this.dict.budgetStoreType.loading = true;
				this.dict.budgetStoreType.items = [];

				$.ajax({
					url: ajaxRoot + '/dict/budget-store-types',
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						console.error('error while loading budget store types:', textStatus, ' - ', errorThrown);
						this.$notify.error({title: 'Ошибка', message: 'Не удалось загрузить типы хранения бюджета'});
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
			reloadCurrencies: function() {
				if (this.dict.currency.loading) return;

				this.dict.currency.loading = true;
				this.dict.currency.items = [];

				$.ajax({
					url: ajaxRoot + '/dict/currencies',
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						console.error('error while loading currencies:', textStatus, ' - ', errorThrown);
						this.$notify.error({title: 'Ошибка', message: 'Не удалось загрузить валюты'});
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
					this.$notify.error({title: 'Ошибка сохранения', message: 'Не удалось определить дату'});
					return;
				}

				this.data.loading = true;
				this.data.items = [];
				this.data.original = [];

				$.ajax({
					url: ajaxRoot + '/affiliate/data/get?date=' + this.data.date.getTime(),
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						console.error('error while loading data:', textStatus, ' - ', errorThrown);
						this.$notify.error({title: 'Ошибка', message: 'Не удалось загрузить данные'});
					},
					success: function(data, textStatus, jqXHR) {
						if (isObject(data)) {
							if (data.type === 'SUCCESS') {
								var items = [];
								if (isNonEmptyArray(data.items)) {
									for (var dataItemIndex in data.items) {
										var dataItem = data.items[dataItemIndex];
										//console.log(dataItem);
										var item = undefined;
										/*for (var index in items) {
											if (dataItem.budgetType.id === items[index].budgetId) {
												item = items[index];
												break;
											}
										}
*/
										if (!isObject(item)) {
											item = {
												id: dataItem.id,
												budgetId: dataItem.budgetType.id,
												values: {}
											};
											items.push(item);
										}

										if (isObject(dataItem.note) && (isNonEmptyString(dataItem.note.noteValue))) item.note = dataItem.note.noteValue;

										if (!isObject(item.values[dataItem.storeType.code])) item.values[dataItem.storeType.code] = {};

										item.values[dataItem.storeType.code][dataItem.currency.code] = dataItem.itemValue;
									}
								}
								//console.log(items);
								this.data.items = items;
							} else if (data.type === 'NO_DATE') {
								this.$notify.error({title: 'Ошибка загрузки данных', message: 'Не передана дата'});
							} else if (data.type === 'NO_LOGIN') {
								this.$notify.error({title: 'Ошибка загрузки данных', message: 'Не определен логин текущего пользователя'});
							} else if (data.type === 'USER_NOT_FOUND') {
								this.$notify.error({title: 'Ошибка загрузки данных', message: 'Текущий пользователь не найден'});
							} else if (data.type === 'ORG_NOT_FOUND') {
								this.$notify.error({title: 'Ошибка загрузки данных', message: 'Не найдена организация текущего пользователя'});
							} else if (data.type === 'CHANGE_TYPE_NOT_FOUND') {
								this.$notify.error({title: 'Ошибка загрузки данных', message: 'Тип изменения "Заявка на расходы" не найдена'});
							} else {
								console.error('unknown server response type', data.type);
								this.$notify.error({title: 'Ошибка загрузки данных', message: 'Неизвестный ответ сервера'});
							}
						} else {
							console.error('unknown server response', data);
							this.$notify.error({title: 'Ошибка загрузки данных', message: 'Неизвестный ответ сервера'});
						}
					},
					complete: function(jqXHR, textStatus) {
						this.data.loading = false;
						this.initData();
						this.data.original = $.extend(true, [], this.data.items);
					}
				});
			},
			addItem: function() {
				var newItem = {
					id: null,
					budgetId: null,
					noteId: null,
					note: null,
					values: {}
				};

				this.data.items.push(newItem);
				this.initData();
			},
			deleteItem: function(index) {
				if ((index >= 0) && (index < this.data.items.length)) this.data.items.splice(index, 1);
			},
			moveItemUp: function(index) {
				if ((index > 0) && (index < this.data.items.length)) {
					var item = $.extend(true, {}, this.data.items[index]);
					this.data.items.splice(index, 1);
					this.data.items.splice(index - 1, 0, item);
				}
			},
			moveItemDown: function(index) {
				if ((index >= 0) && (index < this.data.items.length - 1)) {
					var item = $.extend(true, {}, this.data.items[index]);
					this.data.items.splice(index, 1);
					this.data.items.splice(index + 1, 0, item);
				}
			},
			itemCommand: function(command) {
				if (command && (command.type === 'delete')) this.deleteItem(command.index);
			},
			save: function() {
				if (this.data.loading || (!this.data.initialized)) return;

				if (!isDate(this.data.date)) {
					this.$notify.error({title: 'Ошибка сохранения', message: 'Не удалось определить дату'});
					return;
				}

				var nextChange = {
					changeDate: this.data.date.getTime(),
					items: []
				};

				for (var index in this.data.items) {
					var item = this.data.items[index];
					for (var budgetStoreTypeIndex in this.dict.budgetStoreType.items) {
						var budgetStoreType = this.dict.budgetStoreType.items[budgetStoreTypeIndex];
						for (var currencyIndex in this.dict.currency.items) {
							var currency = this.dict.currency.items[currencyIndex];
							if (item.values[budgetStoreType.code][currency.code] > 0) {
								var nextChangeItem = {
									id: item.id,
									itemValue: item.values[budgetStoreType.code][currency.code],
									currency: {id: currency.id},
									storeType: {id: budgetStoreType.id},
									budgetType: {id: item.budgetId},
									note: {noteValue: item.note}
								};
								nextChange.items.push(nextChangeItem);
							}
						}
					}
				}

				this.data.loading = true;

				var data = JSON.stringify(nextChange);

				$.ajax({
					url: ajaxRoot + '/affiliate/data/save',
					method: 'POST',
					dataType: 'json',
					data: data,
					contentType: 'application/json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						console.error('error while saving data:', textStatus, ' - ', errorThrown);
						this.$notify.error({title: 'Ошибка', message: 'Не удалось сохранить данные'});
						this.data.loading = false;
					},
					success: function(data, textStatus, jqXHR) {
						if (data === 'SUCCESS') {
							this.data.loading = false;
							this.reloadData();
						} else {
							this.data.loading = false;
							if (data === 'NO_DATA') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Данные не переданы'});
							} else if (data === 'NO_LOGIN') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Не определен логин текущего пользователя'});
							} else if (data === 'NO_DATE') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Дата не передана'});
							} else if (data === 'NO_BUDGET') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Не передана статья бюджета'});
							} else if (data === 'NO_CURRENCY') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Не передана валюта'});
							} else if (data === 'NO_STORE_TYPE') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Не передан тип хранения'});
							} else if (data === 'NEGATIVE_VALUE') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Передано отрицательное значение'});
							} else if (data === 'BUDGET_NOT_FOUND') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Статья бюджета не найдена'});
							} else if (data === 'CURRENCY_NOT_FOUND') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Валюта не найдена'});
							} else if (data === 'STORE_TYPE_NOT_FOUND') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Тип хранения не найден'});
							} else if (data === 'USER_NOT_FOUND') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Текущий пользователь не найден'});
							} else if (data === 'ORG_NOT_FOUND') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Не найдена организация текущего пользователя'});
							} else if (data === 'CHANGE_TYPE_NOT_FOUND') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Тип изменения "Заявка на расходы" не найдена'});
							} else if (data === 'STATE_NOT_FOUND') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Статус "Новый" не найден'});
							} else {
								console.error('unknown server response', data);
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Неизвестный ответ сервера'});
							}
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
<!-- components/page-views/requests/expenses-request-affiliate.vue :: end -->