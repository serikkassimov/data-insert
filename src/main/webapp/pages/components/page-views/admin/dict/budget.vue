<!-- components/page-views/admin/dict/budget.vue :: start -->
<template id="page-views-admin-dict-budget">
	<div>
		<div class="card">
			<h5 class="card-header">Справочник: Статьи изменений бюджета</h5>
		</div>
		<div class="card" v-loading.body="items.loading">
			<div class="card-header">
				<el-button @click="reloadItems">Обновить</el-button>
				<el-button @click="newItem">Добавить</el-button>
			</div>
			<div class="card-body">
				<el-table
					border style="width: 100%"
					:data="tableItems" :row-class-name="itemsTableClassName"
				>
					<el-table-column label="Код">
						<template scope="scope">
							<template v-for="level in scope.row.level">
								<i class="el-icon-more text-white"></i>
							</template>
							<template v-if="scope.row.children && (scope.row.children.length > 0)">
								<i class="el-icon-caret-right" @click="toggleExpanded(scope.row.id)" v-if="items.expanded.indexOf(scope.row.id) === -1"></i>
								<i class="el-icon-caret-bottom" @click="toggleExpanded(scope.row.id)" v-else></i>
							</template>
							<template v-else>
								<i class="el-icon-more text-white"></i>
							</template>
							<span>{{scope.row.code}}</span>
						</template>
					</el-table-column>
					<el-table-column label="Название" prop="name"></el-table-column>
					<el-table-column label="Расход">
						<template scope="scope">
							<template v-if="scope.row.outgo">
								<div class="float-right">Да</div>
							</template>
							<template v-else>
								<div class="float-left">Нет</div>
							</template>
						</template>
					</el-table-column>
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
				<el-form-item label="Данные">
					<pre>{{item.data}}</pre>
				</el-form-item>
				<el-form-item label="Код" prop="code">
					<el-input v-model="item.data.code"></el-input>
				</el-form-item>
				<el-form-item label="Название" prop="name">
					<el-input v-model="item.data.name"></el-input>
				</el-form-item>
				<el-form-item label="Расход" prop="outgo">
					<el-switch on-text="" off-text="" v-model="item.data.outgo"></el-switch>
					<template v-if="item.data.outgo">Да</template>
					<template v-else>Нет</template>
				</el-form-item>
				<el-form-item label="Родитель">
					<el-select filterable v-model="item.data.parentId">
						<el-option class="text-secondary" label="<Без родителя>" :value="null"></el-option>
						<el-option v-for="parent in possibleParents" :key="parent.id" :label="'[' + parent.code + '] ' + parent.name" :value="parent.id"></el-option>
					</el-select>
				</el-form-item>
			</el-form>
			<span slot="footer" class="dialog-footer">
				<el-button type="primary" :disabled="!itemChanged" @click="saveItem">Сохранить</el-button>
				<el-button @click="item.dialog.visible = false">Закрыть</el-button>
			</span>
		</el-dialog>
	</div>
</template>
<!-- components/page-views/admin/dict/budget.vue :: middle -->
<script>
(function($){
	var componentName = 'page-views-admin-dict-budget';
	var ajaxRoot = WorldClassRestRoot + '/dict';

	Vue.component(componentName, {
		template: '#' + componentName,
		data: function() {
			return {
				items: {
					items: [],
					loading: false,
					expanded: []
				},
				item: {
					data: {
						id: undefined,
						code: undefined,
						name: undefined,
						disabled: undefined,
						outgo: undefined,
						parentId: undefined
					},
					original: {
						id: undefined,
						code: undefined,
						name: undefined,
						disabled: undefined,
						outgo: undefined,
						parentId: undefined
					},
					rules: {
						code: [
							{required: true, message: 'Необходимо ввести код (макс 50 символов)', trigger: ['change', 'blur']},
							{min: 1, max: 50, message: 'Необходимо ввести код (макс 50 символов)', trigger: ['change', 'blur']},
							{validator: this.codeDuplicateCheck, message: 'Код занят', trigger: ['change', 'blur']}
						]
					},
					saving: false,
					dialog: {
						visible: false
					}
				}
			}
		},
		computed: Vuex.mapState({
			tableItems: function() {
				var result = [];
				var expanded = this.items.expanded;
				walkElementTree(this.items.items, function(walkData) {
					if (isObject(walkData.item)) {
						result.push(walkData.item);
						if (expanded.indexOf(walkData.item.id) === -1) return walkElementTree.SKIP_SUBTREE;
						else return walkElementTree.CONTINUE;
					} else return walkElementTree.CONTINUE;
				});
				return result;
			},
			possibleParents: function() {
				var result = [];
				var skippedId = this.item.data.id;
				walkElementTree(this.items.items, function(walkData) {
					if (isObject(walkData.item)) {
						if (walkData.item.id === skippedId) return walkElementTree.SKIP_SUBTREE;
						else result.push(walkData.item);
					}
					return walkElementTree.CONTINUE;
				});
				return result;
			},
			itemChanged: function() {
				return !equals(this.item.data, this.item.original);
			}
		}),
		methods: {
			reloadItems: function() {
				if (this.items.loading) return;
				this.items.loading = true;
				this.items.items = [];
				$.ajax({
					url: ajaxRoot + '/budget/tree',
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						this.$notify.error({
							title: 'Ошибка',
							message: 'Ошибка при загрузке элементов: ' + textStatus + ' - ' + errorThrown
						});
					},
					success: function(data, textStatus, jqXHR) {
						walkElementTree(data, function(walkData) {
							if (isObject(walkData.item)) {
								walkData.item.level = (walkData.level - 1) / 2;
								if (walkData.objectPath.length > 3) {
									var parent = walkData.objectPath[walkData.objectPath.length - 3];
									if (isObject(parent) && isComparableNumber(parent.id)) walkData.item.parentId = parent.id;
								}
							}
							return walkElementTree.CONTINUE;
						});
						this.items.items = data;
					},
					complete: function(jqXHR, textStatus) {
						this.items.loading = false;
					}
				});
			},
			toggleExpanded: function(id) {
				var index = this.items.expanded.indexOf(id);
				if (index === -1) this.items.expanded.push(id);
				else this.items.expanded.splice(index, 1);
			},
			codeDuplicateCheck: function(rule, value, callback) {
				var error;
				var currentId = this.item.data.id;
				walkElementTree(this.items.items, function(walkData) {
					if (isObject(walkData.item)) {
						if ((walkData.item.id !== currentId) && (walkData.item.code === value)) {
							error = new Error('Found another item (ID: ' + walkData.item.id + ') with code "' + value + '"');
							return walkElementTree.TERMINATE;
						}
					}
					return walkElementTree.CONTINUE;
				});
				if (isError(error)) callback(error);
				else callback();
			},
			newItem: function() {
				this.showItemDialog();
			},
			editItem: function(item) {
				this.showItemDialog(item);
			},
			showItemDialog: function(item) {
				var actualItem = $.extend(true, {}, item);
				if (!isComparableNumber(actualItem.id)) actualItem.id = null;
				if (!isNonEmptyString(actualItem.code)) actualItem.code = '';
				if (!isNonEmptyString(actualItem.name)) actualItem.name = '';
				if (!isComparableNumber(actualItem.parentId)) actualItem.parentId = null;
				if (!isBoolean(actualItem.outgo)) actualItem.outgo = false;
				if (!isBoolean(actualItem.disabled)) actualItem.disabled = false;
				delete actualItem.children;

				this.item.data = $.extend(true, {}, actualItem);
				this.item.original = $.extend(true, {}, actualItem);
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

					var data = $.extend(true, {}, this.item.data);
					if (isComparableNumber(data.parentId)) {
						data.parent = {
							id: data.parentId
						};
					}

					$.ajax({
						url: ajaxRoot + '/budget/save',
						method: 'POST',
						data: JSON.stringify(data),
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
							} else if (data === 'NO_OUTGO') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении элемента: нет признака "расход"'
								});
							} else if (data === 'NOT_FOUND') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении элемента: элемент не найден'
								});
							} else if (data === 'PARENT_NOT_FOUND') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении элемента: родитель не найден'
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
					url: ajaxRoot + '/base/budget/enable?id=' + id,
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
					url: ajaxRoot + '/base/budget/disable?id=' + id,
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
			},
			itemsTableClassName: function(row, index) {
				if (row.disabled) return 'bg-danger';
				else return '';
			}
		},
		mounted: function() {
			this.reloadItems();
		}
	});

	WorldClassPlugins.plugins.push({
		name: componentName,
		dependencies: ['components-menu-menu-module'],
		parameters: ['store', 'router'],
		install: function(Vue, store, router) {
			var path = '/admin/dict/budget';

			router.addRoutes([{
				path: path,
				component: Vue.component(componentName),
				meta: {
					requiresAuthorization: true,
					requiredRoles: ['ROLE_ADMIN']
				}
			}]);

			var info = {
				treePath: ['Администрирование', 'Справочники', 'Статьи изменений бюджета'],
				route: path,
				requiresAuthorization: true,
				requiredRoles: ['ROLE_ADMIN'],
				order: 0
			};
			store.commit('menu/add', info);
		}
	});
})(jQuery);
</script>
<!-- components/page-views/admin/dict/budget.vue :: end -->