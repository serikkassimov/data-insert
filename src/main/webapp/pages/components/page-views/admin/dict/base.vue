<!-- components/page-views/admin/dict/base.vue :: start -->
<template id="page-views-admin-dict-base">
	<div>
		<div class="card">
			<h5 class="card-header">Справочник: {{dict.name}}</h5>
		</div>
		<div class="card" v-loading.body="items.loading">
			<div class="card-header">
				<el-button @click="reloadItems">Обновить</el-button>
				<el-button @click="newItem">Добавить</el-button>
			</div>
			<div class="card-body">
				<el-table
					border style="width: 100%"
					:data="items.items" :row-class-name="itemsTableClassName"
				>
					<el-table-column prop="code" label="Код"></el-table-column>
					<el-table-column prop="name" label="Название"></el-table-column>
					<el-table-column label="Действия">
						<template scope="scope">
							<el-button size="small" @click="editItem(scope.row)">Изменить</el-button>
							<el-button size="small" type="danger" v-if="scope.row.disabled" @click="enableItem(scope.row)">Включить</el-button>
							<el-button size="small" type="danger" v-else @click="disableItem(scope.row)">Выключить</el-button>
						</template>
					</el-table-column>
				</el-table>
			</div>
		</div>
		<el-dialog
			title="Изменение элемента" size="small"
			v-loading.body="item.saving"
			:visible.sync="item.dialog.visible"
		>
			<el-form
				ref="itemForm" label-width="120px"
				:model="item.data" :rules="item.rules"
			>
				<el-form-item label="Код" prop="code">
					<el-input v-model="item.data.code"></el-input>
				</el-form-item>
				<el-form-item label="Название" prop="name">
					<el-input v-model="item.data.name"></el-input>
				</el-form-item>
			</el-form>
			<span slot="footer" class="dialog-footer">
				<el-button type="primary" :disabled="!itemChanged" @click="saveItem">Сохранить</el-button>
				<el-button @click="item.dialog.visible = false">Закрыть</el-button>
			</span>
		</el-dialog>
	</div>
</template>
<!-- components/page-views/admin/dict/base.vue :: middle -->
<script>
(function($) {
	var componentName = 'page-views-admin-dict-base';
	var ajaxRoot = WorldClassRestRoot + '/dict';
	Vue.component(componentName, {
		template: '#' + componentName,
		data: function() {
			var component = this;
			var codeDuplicateCheck = function(rule, value, callback) {
				for (var index in component.items.items) {
					var item = component.items.items[index];
					if (item.id === component.item.data.id) continue;
					if (item.code === value) {
						callback(new Error('Found another item (ID: ' + item.id + ') with code "' + value + '"'));
						return;
					}
				}
				callback();
			};

			return {
				dictType: undefined,
				items: {
					items: [],
					loading: false
				},
				item: {
					data: {
						id: undefined,
						code: undefined,
						name: undefined,
						disabled: undefined
					},
					original: {
						id: undefined,
						code: undefined,
						name: undefined,
						disabled: undefined
					},
					rules: {
						code: [
							{required: true, message: 'Необходимо ввести код (макс 50 символов)', trigger: ['change', 'blur']},
							{min: 1, max: 50, message: 'Необходимо ввести код (макс 50 символов)', trigger: ['change', 'blur']},
							{validator: codeDuplicateCheck, message: 'Код занят', trigger: ['change', 'blur']}
						]
					},
					dialog: {
						visible: false
					},
					saving: false
				}
			};
		},
		computed: Vuex.mapState({
			itemChanged: function() {
				return !equals(this.item.data, this.item.original);
			},
			dict: function() {
				return this.$route.meta.dict;
			}
		}),
		methods: {
			reloadItems: function() {
				if (this.items.loading) return;

				this.items.loading = true;
				this.items.items = [];

				$.ajax({
					url: ajaxRoot + '/base/' + this.dict.type + '/list',
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						this.$notify.error({
							title: 'Ошибка',
							message: 'Ошибка при загрузке элементов: ' + textStatus + ' - ' + errorThrown
						});
					},
					success: function(data, textStatus, jqXHR) {
						this.items.items = data;
					},
					complete: function(jqXHR, textStatus) {
						this.items.loading = false;
					}
				});
			},
			itemsTableClassName: function(row, index) {
				if (row.disabled) return 'bg-danger';
				else return '';
			},
			newItem: function() {
				this.showItemEditDialog();
			},
			editItem: function(item) {
				if (isObject(item)) this.showItemEditDialog(item);
			},
			showItemEditDialog: function(item) {
				var actualItem = $.extend(true, {}, item);
				if (!isNumber(actualItem.id)) actualItem.id = null;
				if (!isString(actualItem.code)) actualItem.code = '';
				if (!isString(actualItem.name)) actualItem.name = '';
				if (!isBoolean(actualItem.disabled)) actualItem.disabled = false;

				this.item.original = $.extend(true, {}, actualItem);
				this.item.data = $.extend(true, {}, actualItem);
				this.item.dialog.visible = true;
			},
			saveItem: function() {
				if (this.item.saving) return;
				this.$refs.itemForm.validate(this.saveItemValidationCallback);
			},
			saveItemValidationCallback: function(valid) {
				if (this.item.saving) return;
				if (valid) {
					this.item.saving = true;
					var data = JSON.stringify(this.item.data);
					$.ajax({
						url: ajaxRoot + '/base/' + this.dict.type + '/save',
						method: 'POST',
						data: data,
						contentType: 'application/json',
						dataType: 'json',
						context: this,
						error: function(jqXHR, textStatus, errorThrown) {
							this.$notify.error({
								title: 'Ошибка',
								message: 'Ошибка при сохранении элемента: ' + textStatus + ' - ' + errorThrown
							});
						},
						success: function(data, textStatus, jqXHR) {
							if (data === 'SUCCESS') {
								this.$message.success('Элемент сохранен');
								this.item.dialog.visible = false;
								this.reloadItems();
							} else if (data === 'INTERNAL_ERROR') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении элемента: внутренняя ошибка'
								});
							} else if (data === 'NO_DATA') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении элемента: ошибка передачи данных'
								});
							} else if (data === 'NO_CODE') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении элемента: нет кода'
								});
							} else if (data === 'NO_DISABLED') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении элемента: нет признака "выключен"'
								});
							} else if (data === 'NOT_FOUND') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении элемента: элемент не найден'
								});
							} else {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении элемента: неизвестный ответ сервера "' + data + '"'
								});
							}
						},
						complete: function(jqXHR, textStatus) {
							this.item.saving = false;
						}
					});
				}
			},
			enableItem: function(item) {
				if (isObject(item) && isNumber(item.id) && isBoolean(item.disabled) && item.disabled) {
					var app = this;
					this.$confirm('Включить элемент "' + item.name + '"?', 'Внимание', {
						confirmButtonText: 'Да',
						cancelButtonText: 'Нет',
						type: 'warning'
					}).then(function() {
						app.enableItemCallback(item.id);
					}).catch(function() {});
				}
			},
			enableItemCallback: function(id) {
				if (!isNumber(id)) return;

				this.items.loading = true;
				$.ajax({
					url: ajaxRoot + '/base/' + this.dict.type + '/enable?id=' + id,
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						this.$notify.error({
							title: 'Ошибка',
							message: 'Ошибка при включении элемента: ' + textStatus
						});
					},
					success: function(data, textStatus, jqXHR) {
						if (data === 'SUCCESS') {
							this.$message.success('Элемент включен');
						} else if (data === 'NO_ID') {
							this.$notify.error({
								title: 'Ошибка',
								message: 'Ошибка при включении элемента: не передан ИД элемента'
							});
						} else if (data === 'NOT_FOUND') {
							this.$notify.error({
								title: 'Ошибка',
								message: 'Ошибка при включении элемента: элемент не найден'
							});
						}
					},
					complete: function(jqXHR, textStatus) {
						this.items.loading = false;
						this.reloadItems();
					}
				});
			},
			disableItem: function(item) {
				if (isObject(item) && isNumber(item.id) && isBoolean(item.disabled) && (!item.disabled)) {
					var app = this;
					this.$confirm('Выключить элемент "' + item.name + '"?', 'Внимание', {
						confirmButtonText: 'Да',
						cancelButtonText: 'Нет',
						type: 'warning'
					}).then(function() {
						app.disableItemCallback(item.id);
					}).catch(function() {});
				}
			},
			disableItemCallback: function(id) {
				if (!isNumber(id)) return;

				this.items.loading = true;
				$.ajax({
					url: ajaxRoot + '/base/' + this.dict.type + '/disable?id=' + id,
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						this.$notify.error({
							title: 'Ошибка',
							message: 'Ошибка при выключении элемента: ' + textStatus
						});
					},
					success: function(data, textStatus, jqXHR) {
						if (data === 'SUCCESS') {
							this.$message.success('Элемент выключен');
						} else if (data === 'NO_ID') {
							this.$notify.error({
								title: 'Ошибка',
								message: 'Ошибка при выключении элемента: не передан ИД элемента'
							});
						} else if (data === 'NOT_FOUND') {
							this.$notify.error({
								title: 'Ошибка',
								message: 'Ошибка при выключении элемента: элемент не найден'
							});
						}
					},
					complete: function(jqXHR, textStatus) {
						this.items.loading = false;
						this.reloadItems();
					}
				});
			}
		},
		mounted: function() {
			this.reloadItems();
		},
		updated: function() {
			if (this.dict) {
				if (this.dictType !== this.dict.type) {
					this.dictType = this.dict.type;
					this.reloadItems();
				}
			}
		}
	});

	WorldClassPlugins.plugins.push({
		name: componentName,
		dependencies: [],
		parameters: ['store', 'router'],
		install: function(Vue, store, router) {
			var pathPrefix = '/admin/dict/';
			var dicts = [
				{type: 'roles', name: 'Роли'},
				{type: 'orgs', name: 'Организации'},
				{type: 'budgetStoreTypes', name: 'Типы хранения бюджета'},
				{type: 'budgetNextChangeTypes', name: 'Типы изменений бюджета'},
				{type: 'budgetNextChangeStates', name: 'Статусы изменений бюджета'}
			];
			for (var index in dicts) {
				var dict = dicts[index];
				var path = pathPrefix + dict.type;

				router.addRoutes([{
					path: path,
					component: Vue.component(componentName),
					meta: {
						requiresAuthorization: true,
						requiredRoles: ['ROLE_ADMIN'],
						dict: dict
					}
				}]);
			}
		}
	});
})(jQuery);
</script>
<!-- components/page-views/admin/dict/base.vue :: end -->