<!-- components/page-views/reports/x-incoming.vue :: start -->
<template id="page-views-reports-x-incoming">
<div class="h-100">
	<div class="card h-100">
		<h5 class="card-header" v-loading.body="loading">
			Остатки и поступления
			<el-button @click="reloadAll">Обновить все</el-button>
			<el-button @click="reloadData">Обновить данные</el-button>
			<el-date-picker v-model="data.date" type="date" format="dd.MM.yyyy" :clearable="false" @change="reloadData"></el-date-picker>
		</h5>
		<h5 class="card-header" v-loading.body="loading">
			<el-button @click="addItem">Добавить</el-button>
			<el-button v-if="changed" @click="save">Сохранить</el-button>
			<el-button v-else disabled>Сохранить</el-button>
		</h5>
		<div class="card-body" style="overflow: auto;" v-loading.body="loading">
			<table class="table table-bordered table-hover table-sm table-responsive h-100">
				<thead class="thead-default">
					<tr>
						<th>#</th>
						<th>Назначение</th>
						<th>Сумма</th>
						<th></th>
					</tr>
				</thead>
				<tbody style="overflow: scroll;">
					<tr v-for="(item, index) in data.items" :key="index">
						<th>{{index + 1}}</th>
						<th>
							<el-input placeholder="Назначение" v-model="item.note"></el-input>
						</th>
						<th>
							<el-input-number v-model="item.value"></el-input-number>
						</th>
						<th>
							<el-dropdown
								@command="(command) => {
									if (command === 'delete') deleteItem(index);
									else if (command === 'moveUp') moveUp(index);
									else if (command === 'moveDown') moveDown(index);
								}"
							>
								<el-button type="primary">
									Действия<i class="el-icon-arrow-down el-icon--right"></i>
								</el-button>
								<el-dropdown-menu slot="dropdown">
									<el-dropdown-item v-if="index > 0" command="moveUp">Вверх</el-dropdown-item>
									<el-dropdown-item v-if="index < (data.items.length - 1)" command="moveDown">Вниз</el-dropdown-item>
									<el-dropdown-item command="delete">Удалить</el-dropdown-item>
								</el-dropdown-menu>
							</el-dropdown>
						</th>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>
</template>
<!-- components/page-views/reports/x-incoming.vue :: middle -->
<script>
(function($) {
	var componentName = 'page-views-reports-x-incoming';
	var ajaxRoot = WorldClassRestRoot + '/x-incoming';
	Vue.component(componentName, {
		template: '#' + componentName,
		data: function() {
			var date = new Date();
			date.setMilliseconds(0);
			date.setSeconds(0);
			date.setMinutes(0);
			date.setHours(0);

			return {
				data: {
					date: date,
					items: [],
					original: [],
					loading: false
				}
			};
		},
		computed: Vuex.mapState({
			loading: function() {
				return this.data.loading;
			},
			changed: function() {
				return !equals($.extend(true, [], this.data.items), $.extend(true, [], this.data.original));
			}
		}),
		methods: {
			reloadAll: function() {
				this.reloadData();
			},
			reloadData: function() {
				if (this.data.loading) return;

				this.data.loading = true;
				this.data.items = [];
				this.data.original = [];

				$.ajax({
					url: ajaxRoot + '/data?date=' + this.data.date.getTime(),
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						console.error('cannot load data,', textStatus, ',', errorThrown);
						this.$notify.error({title: 'Ошибка', message: 'Не удалось загрузить данные'});
					},
					success: function(data, textStatus, jqXHR) {
						if (isObject(data)) {
							if (data.type === 'SUCCESS') {
								for (var index in data.items) {
									var item = data.items[index];
									if (!isObject(item)) item = {};
									if (!isString(item.note)) item.note = '';
									if (!isComparableNumber(item.value)) item.value = 0;
									if (!isComparableNumber(item.orderNumber)) item.orderNumber = 0;
								}

								sort(function(item1, item2) {
									if (isObject(item1) && isComparableNumber(item1.orderNumber)) {
										if (isObject(item2) && isComparableNumber(item2.orderNumber)) return item1.orderNumber - item2.orderNumber;
										else return 1;
									} else {
										if (isObject(item2) && isComparableNumber(item2.orderNumber)) return -1;
										else return 0;
									}
								}, data.items);

								this.data.items = data.items;
								this.data.original = $.extend(true, [], data.items);
							} else if (data.type === 'NO_LOGIN') {
								this.$notify.error({title: 'Ошибка загрузки данных', message: 'Логин текущего пользователя не определен'});
							} else if (data.type === 'USER_NOT_FOUND') {
								this.$notify.error({title: 'Ошибка загрузки данных', message: 'Текущий пользователь не найден'});
							} else if (data.type === 'ORG_NOT_FOUND') {
								this.$notify.error({title: 'Ошибка загрузки данных', message: 'Организация текущего пользователя не найдена'});
							} else {
								console.error('cannot load data: unknown server responce type ', data.type);
								this.$notify.error({title: 'Ошибка', message: 'Неизвестный тип ответа сервера'});
							}
						} else {
							console.error('cannot load data: unknown server responce ', data);
							this.$notify.error({title: 'Ошибка', message: 'Неизвестный ответ сервера'});
						}
					},
					complete: function(jqXHR, textStatus) {
						this.data.loading = false;
					}
				});
			},
			deleteItem: function(index) {
				if ((index >= 0) && (index < this.data.items.length)) this.data.items.splice(index, 1);
			},
			moveUp: function(index) {
				if ((index > 0) && (index < this.data.items.length)) {
					var item = this.data.items[index];
					this.data.items.splice(index, 1);
					this.data.items.splice(index - 1, 0, item);
				}
			},
			moveDown: function(index) {
				if ((index >= 0) && (index < this.data.items.length - 1)) {
					var item = this.data.items[index];
					this.data.items.splice(index, 1);
					if (index + 1 === this.data.items.length) this.data.items.push(item);
					else this.data.items.splice(index + 1, 0, item);
				}
			},
			addItem: function() {
				var item = {
					note: '',
					value: 0
				};
				this.data.items.push(item);
			},
			save: function() {
				if (this.data.loading) return;

				this.data.loading = true;

				var items = [];
				for (var index in this.data.items) {
					var item = this.data.items[index];
					if (isComparableNumber(item.value)) {
						var savedItem = {
							value: item.value
						};
						if (isNonEmptyString(item.note)) savedItem.note = item.note;
						if (isComparableNumber(item.id)) savedItem.id = item.id;
						items.push(savedItem);
					}
				}
				for (var i = 0; i < items.length; i++) {
					items[i].orderNumber = i;
				}

				var data = JSON.stringify(items);

				$.ajax({
					url: ajaxRoot + '/save?date=' + this.data.date.getTime(),
					method: 'POST',
					dataType: 'json',
					data: data,
					contentType: 'application/json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
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
							this.$notify.error({title: 'Сохранение данных', message: 'Ошибка при сохранении данных: не найдена организация текущего пользователя'});
						} else if (data === 'DATA_NOT_FOUND') {
							this.$notify.error({title: 'Сохранение данных', message: 'Ошибка при сохранении данных: не найдена измененная строка данных'});
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
<!-- components/page-views/reports/x-incoming.vue :: end -->