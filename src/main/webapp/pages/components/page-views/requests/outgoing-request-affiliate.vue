<!-- components/page-views/requests/outgoing-request-affiliate.vue :: start -->
<template id="page-views-requests-outgoing-request-affiliate">
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
						<th v-for="budgetStoreType in dict.budgetStoreType.items" :key="budgetStoreType.id" :colspan="dict.orgPart.items.length">{{budgetStoreType.name}}</th>
					</tr>
					<tr>
						<template v-for="budgetStoreType in dict.budgetStoreType.items">
							<template v-for="orgPart in dict.orgPart.items">
								<th :key="budgetStoreType.id + '-' + orgPart.id">{{orgPart.name}}</th>
							</template>
						</template>
					</tr>
				</thead>
				<tbody>
					<tr v-for="(row, index) in data.rows" :key="index">
						<td>{{ index + 1 }}</td>
						<td>
							<el-dropdown @command="itemCommand">
								<el-button type="primary">
									Действия<i class="el-icon-caret-bottom el-icon--right"></i>
								</el-button>
								<el-dropdown-menu slot="dropdown">
									<el-dropdown-item :command="{'type':'delete', 'index': index}">Удалить</el-dropdown-item>
									<el-dropdown-item :command="{'type':'moveUp', 'index': index}" v-if="index > 0">Вверх</el-dropdown-item>
									<el-dropdown-item :command="{'type':'moveDown', 'index': index}" v-if="index < (data.rows.length - 1)">Вниз</el-dropdown-item>
								</el-dropdown-menu>
							</el-dropdown>
						</td>
						<td>
							<el-select filterable v-model="row.budget.id">
								<el-option v-for="budget in dict.budget.items" :key="budget.id" :label="'[' + budget.code + '] ' + budget.name" :value="budget.id"></el-option>
							</el-select>
						</td>
						<td>
							<el-input placeholder="Описание" type="textarea" v-model="row.note"></el-input>
						</td>
						<template v-for="budgetStoreType in dict.budgetStoreType.items">
							<template v-for="orgPart in dict.orgPart.items">
								<td :key="index + '-' + budgetStoreType.id + '-' + orgPart.id">
									<el-input-number
										v-model="row[budgetStoreType.code][orgPart.code].value"
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
<!-- components/page-views/requests/outgoing-request-affiliate.vue :: middle -->
<script>
(function($) {
	var componentName = 'page-views-requests-outgoing-request-affiliate';
	var ajaxRoot = WorldClassRestRoot + '/outgoing-request';
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
						items: [],
						loading: false
					},
					orgPart: {
						items: [],
						loading: false
					}
				},
				data: {
					id: undefined,
					date: date,
					rows: [],
					original: [],
					loading: false
				}
			};
		},
		computed: Vuex.mapState({
			dictLoading: function() {
				return this.dict.budget.loading || this.dict.budgetStoreType.loading || this.dict.orgPart.loading;
			},
			loading: function() {
				return this.data.loading || this.dictLoading;
			},
			changed: function() {
				return !equals($.extend(true, [], this.data.rows), $.extend(true, [], this.data.original));
			}
		}),
		methods: {
			initData: function() {
				if (this.data.rows.length > 0) {
					for (var rowIndex = 0; rowIndex < this.data.rows.length; rowIndex++) {
						var row = this.data.rows[rowIndex];
						if (!isObject(row.budget)) row.budget = {};
						if (!isComparableNumber(row.budget.id)) row.budget.id = undefined;
						if (!isString(row.note)) row.note = '';
						if (isNonEmptyArray(row.cells)) {
							for (var cellIndex = 0; cellIndex < row.cells.length; cellIndex++) {
								var cell = row.cells[cellIndex];
								if (isObject(cell) && isComparableNumber(cell.value) && (cell.value !== 0)) {
									var budgetStoreType = cell.storeType;
									if (isObject(budgetStoreType) && isNonEmptyString(budgetStoreType.code)) {
										if (!isObject(row[budgetStoreType.code])) row[budgetStoreType.code] = {};
										var orgPart = cell.orgPart;
										if (isObject(orgPart) && isNonEmptyString(orgPart.code)) {
											if (!isObject(row[budgetStoreType.code][orgPart.code])) row[budgetStoreType.code][orgPart.code] = {};
											row[budgetStoreType.code][orgPart.code].value = cell.value;
											row[budgetStoreType.code][orgPart.code].id = cell.id;
										}
									}
								}
							}
						}
					}

					if ((this.dict.budgetStoreType.items.length > 0) && (this.dict.orgPart.items.length > 0)) {
						for (var rowIndex = 0; rowIndex < this.data.rows.length; rowIndex++) {
							var row = this.data.rows[rowIndex];
							for (var budgetStoreTypeIndex = 0; budgetStoreTypeIndex < this.dict.budgetStoreType.items.length; budgetStoreTypeIndex++) {
								var budgetStoreType = this.dict.budgetStoreType.items[budgetStoreTypeIndex];
								if (!isObject(row[budgetStoreType.code])) row[budgetStoreType.code] = {};
								for (var orgPartIndex = 0; orgPartIndex < this.dict.orgPart.items.length; orgPartIndex++) {
									var orgPart = this.dict.orgPart.items[orgPartIndex];
									if (!isObject(row[budgetStoreType.code][orgPart.code])) row[budgetStoreType.code][orgPart.code] = {};
									if (!isComparableNumber(row[budgetStoreType.code][orgPart.code].value)) row[budgetStoreType.code][orgPart.code].value = 0;
								}
							}
						}
					}

					this.data.rows = $.extend(true, [], this.data.rows);
				}
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
			reloadOrgParts: function() {
				if (this.dict.orgPart.loading) return;

				this.dict.orgPart.loading = true;
				this.dict.orgPart.items = [];

				$.ajax({
					url: ajaxRoot + '/dict/org-parts',
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						console.error('error while loading org parts:', textStatus, ' - ', errorThrown);
						this.$notify.error({title: 'Ошибка', message: 'Не удалось загрузить части организаций'});
					},
					success: function(data, textStatus, jqXHR) {
						this.dict.orgPart.items = data;
					},
					complete: function(jqXHR, textStatus) {
						this.dict.orgPart.loading = false;
						this.initData();
					}
				});
			},
			reloadData: function() {
				if (this.data.loading) return;

				this.data.loading = true;
				this.data.rows = [];
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
								sort(function(row1, row2) {
									if (isObject(row1) && isComparableNumber(row1.orderNumber)) {
										if (isObject(row2) && isComparableNumber(row2.orderNumber)) return row1.orderNumber - row2.orderNumber;
										else return 1;
									} else {
										if (isObject(row2) && isComparableNumber(row2.orderNumber)) return -1;
										else return 0;
									}
								}, data.rows);
								this.data.original = $.extend(true, [], data.rows);
								this.data.rows = data.rows;
								this.data.id = data.id;
							}
							else if (data.type === 'NO_LOGIN') this.$notify.error({title: 'Ошибка загрузки данных', message: 'Не удалось определить текущего пользователя'});
							else if (data.type === 'NO_DATE') this.$notify.error({title: 'Ошибка загрузки данных', message: 'Дата не передана'});
							else if (data.type === 'NO_USER') this.$notify.error({title: 'Ошибка загрузки данных', message: 'Текущий пользователь не найден'});
							else if (data.type === 'NO_ORG') this.$notify.error({title: 'Ошибка загрузки данных', message: 'Не найдена организация текущего пользователя'});
							else {
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
					}
				});
			},
			reloadAll: function() {
				this.reloadBudgets();
				this.reloadBudgetStoreTypes();
				this.reloadOrgParts();
				this.reloadData();
			},
			itemCommand: function(command) {
				if (command) {
					if (command.type === 'delete') this.deleteItem(command.index);
					else if (command.type === 'moveUp') this.moveUp(command.index);
					else if (command.type === 'moveDown') this.moveDown(command.index);
				}
			},
			deleteItem: function(index) {
				if ((index > 0) && (index < this.data.rows.length)) this.data.rows.splice(index, 1);
			},
			addItem: function() {
				var row = {};
				this.data.rows.push(row);
				this.initData();
			},
			moveUp: function(index) {
				if ((index > 0) && (index < this.data.rows.length)) {
					var row = $.extend(true, {}, this.data.rows[index]);
					this.data.rows.splice(index, 1);
					this.data.rows.splice(index - 1, 0, row);
				}
			},
			moveDown: function(index) {
				if ((index >= 0) && (index < this.data.rows.length - 1)) {
					var row = $.extend(true, {}, this.data.rows[index]);
					this.data.rows.splice(index, 1);
					this.data.rows.splice(index + 1, 0, row);
				}
			},
			save: function() {
				if (this.loading) return;

				var request = {
					date: this.data.date.getTime(),
					rows: []
				};
				if (isComparableNumber(this.data.id)) request.id = this.data.id;

				for (var rowIndex = 0; rowIndex < this.data.rows.length; rowIndex++) {
					var row = this.data.rows[rowIndex];
					if (!isComparableNumber(row.budget.id)) {
						this.$notify.error({title: 'Не заполнены данные', message: 'Не заполнена статья бюджета в строке №' + (rowIndex + 1)});
						return;
					}

					var savedRow = {
						budget: {
							id: row.budget.id
						},
						note: row.note,
						orderNumber: rowIndex,
						cells: []
					};
					if (isComparableNumber(row.id)) savedRow.id = row.id;
					request.rows.push(savedRow);

					for (var budgetStoreTypeIndex = 0; budgetStoreTypeIndex < this.dict.budgetStoreType.items.length; budgetStoreTypeIndex++) {
						var budgetStoreType = this.dict.budgetStoreType.items[budgetStoreTypeIndex];
						if (isObject(row[budgetStoreType.code])) {
							for (var orgPartIndex = 0; orgPartIndex < this.dict.orgPart.items.length; orgPartIndex++) {
								var orgPart = this.dict.orgPart.items[orgPartIndex];
								if (isObject(row[budgetStoreType.code][orgPart.code]) && (row[budgetStoreType.code][orgPart.code].value > 0)) {
									var cell = {
										value: row[budgetStoreType.code][orgPart.code].value,
										storeType: {id: budgetStoreType.id},
										orgPart: {id: orgPart.id}
									};
									if (isComparableNumber(row[budgetStoreType.code][orgPart.code].id)) cell.id = row[budgetStoreType.code][orgPart.code].id;
									savedRow.cells.push(cell);
								}
							}
						}
					}
				}

				var data = JSON.stringify(request);

				this.data.loading = true;

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
							if (data === 'NO_LOGIN') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Не определен логин текущего пользователя'});
							} else if (data === 'NO_DATA') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Данные не переданы'});
							} else if (data === 'NO_DATE') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Не передана дата'});
							} else if (data === 'NO_USER') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Не определен текущий пользователь'});
							} else if (data === 'NO_ORG') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Не определена организация текущего пользователя'});
							} else if (data === 'NO_CURRENCY') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Не найдена валюта "тенге"'});
							} else if (data === 'NULL_ROW') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Встречена пустая строка'});
							} else if (data === 'NULL_BUDGET') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Не передана статья бюджета'});
							} else if (data === 'NULL_BUDGET_ID') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Не передана статья бюджета (ID)'});
							} else if (data === 'BUDGET_NOT_FOUND') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Статья бюджета не найдена'});
							} else if (data === 'NULL_CELL') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Встречена пустая ячейка'});
							} else if (data === 'NULL_ORG_PART') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Не передана часть организации'});
							} else if (data === 'NULL_ORG_PART_ID') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Не передана часть организации (ID)'});
							} else if (data === 'ORG_PART_NOT_FOUND') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Часть организации не найдена'});
							} else if (data === 'NULL_STORE_TYPE') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Не передан тип хранения'});
							} else if (data === 'NULL_STORE_TYPE_ID') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Не передан тип хранения (ID)'});
							} else if (data === 'STORE_TYPE_NOT_FOUND') {
								this.$notify.error({title: 'Ошибка сохранения данных', message: 'Тип хранения не найден'});
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
<!-- components/page-views/requests/outgoing-request-affiliate.vue :: end -->