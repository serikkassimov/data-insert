<!-- components/page-views/admin/auth-manage/users.vue :: start -->
<template id="page-views-admin-auth-manage-users">
	<div>
		<div class="grid-content">
			<el-button v-loading.body="roles.loading" @click="reloadRoles">Обновить роли ({{roles.items.length}})</el-button>
			<el-button v-loading.body="orgs.loading" @click="reloadOrgs">Обновить организации ({{orgs.items.length}})</el-button>
		</div>
		<div class="grid-content" v-loading.body="users.loading">
			<p>
				<h3>Пользователи</h3>
			</p>
			<el-button @click="reloadUsers">Обновить</el-button>
			<el-button @click="newUser">Добавить</el-button>
			<el-table
				ref="usersTable" border style="width: 100%"
				:data="users.items" :row-class-name="usersTableClassName"
			>
				<el-table-column prop="login" label="Логин" width="200"></el-table-column>
				<el-table-column prop="org.name" label="Организация"></el-table-column>
				<el-table-column prop="lastname" label="Фамилия"></el-table-column>
				<el-table-column prop="firstname" label="Имя"></el-table-column>
				<el-table-column prop="patronymic" label="Отчество"></el-table-column>
				<el-table-column prop="email" label="E-Mail"></el-table-column>
				<el-table-column label="Действия">
					<template scope="scope">
						<el-button size="small" @click="editUser(scope.row, scope.$index)">Изменить</el-button>
						<el-button size="small" type="danger" @click="unlockUser(scope.row, scope.$index)" v-if="scope.row.locked">Разблокировать</el-button>
						<el-button size="small" type="danger" @click="lockUser(scope.row, scope.$index)" v-else>Заблокировать</el-button>
					</template>
				</el-table-column>
			</el-table>
		</div>
		<el-dialog
			ref="userDialog" title="Изменение пользователя" size="small"
			v-loading.body="user.saving || orgs.loading"
			:visible.sync="user.dialog.visible"
			@open="userDialogContentShown"
		>
			<mount-event @mounted="userDialogContentShown"></mount-event>
			<el-form
				ref="userForm" label-width="120px"
				:model="user.data" :rules="user.rules"
			>
				<el-form-item label="Организация" prop="org.id">
					<el-select v-model="user.data.org.id">
						<el-option
							v-for="org in orgs.items"
							:key="org.id"
							:label="org.name"
							:value="org.id"
						>
							{{ org.name }}
						</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="Логин" prop="login">
					<el-input v-model="user.data.login"></el-input>
				</el-form-item>
				<el-form-item label="Пароль" prop="password">
					<el-input v-model="user.data.password"></el-input>
				</el-form-item>
				<el-form-item label="Фамилия">
					<el-input v-model="user.data.lastname"></el-input>
				</el-form-item>
				<el-form-item label="Имя">
					<el-input v-model="user.data.firstname"></el-input>
				</el-form-item>
				<el-form-item label="Отчество">
					<el-input v-model="user.data.patronymic"></el-input>
				</el-form-item>
				<el-form-item label="E-Mail">
					<el-input v-model="user.data.email"></el-input>
				</el-form-item>
			</el-form>
			<el-table
				ref="userRolesTable" border style="width: 100%"
				:data="roles.items"
				@selection-change="userRolesSelectionChange"
			>
				<el-table-column type="selection" width="55"></el-table-column>
				<el-table-column label="Код">
					<template scope="scope">{{ scope.row.code }}</template>
				</el-table-column>
				<el-table-column label="Название">
					<template scope="scope">{{ scope.row.name }}</template>
				</el-table-column>
			</el-table>
			<span slot="footer" class="dialog-footer">
				<el-button type="primary" :disabled="!userChanged" @click="saveUser">Сохранить</el-button>
				<el-button @click="user.dialog.visible = false">Закрыть</el-button>
			</span>
		</el-dialog>
	</div>
</template>
<!-- components/page-views/admin/auth-manage/users.vue :: middle -->
<script>
(function($){
	var isEntity = function(object) {
		return isObject(object) && isNumber(object.id);
	};
	var entityComparator = function(role1, role2) {
		if (isEntity(role1)) {
			if (isEntity(role2)) return numberComparator(role1.id, role2.id);
			else return 1;
		} else {
			if (isEntity(role2)) return -1;
			else return 0;
		}
	};

	var componentName = 'page-views-admin-auth-manage-users';
	var ajaxRoot = WorldClassRestRoot + '/auth-manage';
	Vue.component(componentName, {
		template: '#' + componentName,
		data: function () {
			var component = this;
			var loginDuplicateCheck = function(rule, value, callback) {
				for (var index in component.users.items) {
					var user = component.users.items[index];
					if (user.id === component.user.data.id) continue;
					if (user.login === value) {
						callback(new Error('Found another user (ID: ' + user.id + ') with login "' + value + ""));
						return;
					}
				}
				callback();
			};
			var orgCheck = function(rule, value, callback) {
				for (var index in component.orgs.items) {
					var org = component.orgs.items[index];
					if (value === org.id) {
						callback();
						return;
					}
				}
				callback(new Error('No org with ID ' + value));
			};

			return {
				roles: {
					items: [],
					loading: false,
				},
				orgs: {
					items: [],
					loading: false
				},
				users: {
					items: [],
					loading: false
				},
				user: {
					data: {
						id: undefined,
						login: undefined,
						password: undefined,
						firstname: undefined,
						lastname: undefined,
						patronymic: undefined,
						email: undefined,
						locked: undefined,
						org: {id: undefined},
						roles: []
					},
					original: {
						id: undefined,
						login: undefined,
						password: undefined,
						firstname: undefined,
						lastname: undefined,
						patronymic: undefined,
						email: undefined,
						locked: undefined,
						org: {id: undefined},
						roles: []
					},
					rules: {
						login: [
							{required: true, message: 'Необходимо ввести логин (макс 50 символов)', trigger: ['change', 'blur']},
							{min: 1, max: 50, message: 'Необходимо ввести логин (макс 50 символов)', trigger: ['change', 'blur']},
							{validator: loginDuplicateCheck, message: 'Логин занят', trigger: ['change', 'blur']}
						],
						password: [
							{required: true, message: 'Необходимо ввести логин (макс 50 символов)', trigger: ['change', 'blur']},
							{min: 1, max: 50, message: 'Необходимо ввести логин (макс 50 символов)', trigger: ['change', 'blur']}
						],
						'org.id': [
							{validator: orgCheck, message: 'Необходимо выбрать организацию', trigger: ['change', 'blur']}
						]
					},
					saving: false,
					dialog: {
						visible: false
					}
				}
			};
		},
		computed: Vuex.mapState({
			userChanged: function() {
				return !equals(this.user.data, this.user.original);
			}
		}),
		methods: {
			reloadRoles: function() {
				if (this.roles.loading) return;

				this.roles.items = [];
				this.roles.loading = true;

				$.ajax({
					url: ajaxRoot + '/roles',
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						this.$notify.error({
							title: 'Ошибка',
							message: 'Ошибка при загрузке ролей: ' + textStatus
						});
					},
					success: function(data, textStatus, jqXHR) {
						this.roles.items = data;
					},
					complete: function(jqXHR, textStatus) {
						this.roles.loading = false;
					}
				});
			},
			reloadOrgs: function() {
				if (this.orgs.loading) return;

				this.orgs.items = [];
				this.orgs.loading = true;

				$.ajax({
					url: ajaxRoot + '/orgs',
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						this.$notify.error({
							title: 'Ошибка',
							message: 'Ошибка при загрузке организаций: ' + textStatus
						});
					},
					success: function(data, textStatus, jqXHR) {
						this.orgs.items = data;
					},
					complete: function(jqXHR, textStatus) {
						this.orgs.loading = false;
					}
				});
			},
			reloadUsers: function() {
				if (this.users.loading) return;

				this.user.dialog.visible = false;
				this.users.items = [];
				this.users.loading = true;

				$.ajax({
					url: ajaxRoot + '/users',
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						this.$notify.error({
							title: 'Ошибка',
							message: 'Ошибка при загрузке пользователей: ' + textStatus
						});
					},
					success: function(data, textStatus, jqXHR) {
						this.users.items = data;
					},
					complete: function(jqXHR, textStatus) {
						this.users.loading = false;
					}
				});
			},
			usersTableClassName: function(row, index) {
				if (row.locked) return 'danger';
				else return '';
			},
			userRolesSelectionChange: function(roles) {
				var newRoles = [];
				for (var index in roles) newRoles.push({id: roles[index].id});
				sort(entityComparator, newRoles);
				this.user.data.roles = newRoles;
			},
			newUser: function() {
				this.showUserEditDialog();
			},
			editUser: function(user, index) {
				if (isObject(user)) {
					this.showUserEditDialog(user);
				}
			},
			showUserEditDialog: function(user) {
				var actualUser = $.extend(true, {}, user);
				if (!isNumber(actualUser.id)) actualUser.id = null;
				if (!isString(actualUser.login)) actualUser.login = '';
				if (!isString(actualUser.password)) actualUser.password = '';
				if (!isString(actualUser.firstname)) actualUser.firstname = '';
				if (!isString(actualUser.lastname)) actualUser.lastname = '';
				if (!isString(actualUser.patronymic)) actualUser.patronymic = '';
				if (!isString(actualUser.email)) actualUser.email = '';
				if (!isBoolean(actualUser.locked)) actualUser.locked = false;
				if (!isObject(actualUser.org)) actualUser.org = {};
				if (!isNumber(actualUser.org.id)) actualUser.org.id = null;
				actualUser.org = {id: actualUser.org.id};
				if (!isArray(actualUser.roles)) actualUser.roles = [];
				var deletedIndices = [];
				for (var index in actualUser.roles) {
					var role = actualUser.roles[index];
					if ((!isObject) || (!isNumber(role.id))) deletedIndices.push[index];
					else actualUser.roles[index] = {id: role.id};
				}
				for (var index in deletedIndices) delete actualUser.roles[deletedIndices[index]];

				sort(entityComparator, actualUser.roles);

				this.user.original = $.extend(true, {}, actualUser);
				this.user.data = $.extend(true, {}, actualUser);
				this.user.dialog.visible = true;
			},
			userDialogContentShown: function() {
				if (this.$refs.userRolesTable) {
					var ids = [];
					for (var index in this.user.data.roles) ids.push(this.user.data.roles[index].id);
					for (var index in this.roles.items) {
						var role = this.roles.items[index];
						this.$refs.userRolesTable.toggleRowSelection(role, (ids.indexOf(role.id) !== -1));
					}
				}
			},
			saveUser: function() {
				if (this.user.saving) return;
				this.$refs.userForm.validate(this.saveUserValidationCallback);
			},
			saveUserValidationCallback: function(valid) {
				if (this.user.saving) return;
				if (valid) {
					this.user.saving = true;
					var data = JSON.stringify(this.user.data);
					$.ajax({
						url: ajaxRoot + '/save-user',
						method: 'POST',
						data: data,
						contentType: 'application/json',
						dataType: 'json',
						context: this,
						error: function(jqXHR, textStatus, errorThrown) {
							this.$notify.error({
								title: 'Ошибка',
								message: 'Ошибка при сохранении пользователя: ' + textStatus
							});
						},
						success: function(data, textStatus, jqXHR) {
							if (data === 'SUCCESS') {
								this.$message.success('Пользователь сохранен');
								this.user.dialog.visible = false;
								this.reloadUsers();
							} else if (data === 'INTERNAL_ERROR') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении пользователя: внутренняя ошибка'
								});
							} else if (data === 'NO_DATA') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении пользователя: ошибка при передаче данных'
								});
							} else if (data === 'NO_LOGIN') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении пользователя: логин не заполнен'
								});
							} else if (data === 'NO_PASSWORD') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении пользователя: пароль не заполнен'
								});
							} else if (data === 'NO_ORG') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении пользователя: организация не указана'
								});
							} else if (data === 'USER_NOT_FOUND') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении пользователя: пользователь не найден'
								});
							} else if (data === 'ORG_NOT_FOUND') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении пользователя: организация не найдена'
								});
							} else if (data === 'ROLE_NOT_FOUND') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении пользователя: роль не найдена'
								});
							} else if (data === 'LOGIN_BUSY') {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении пользователя: логин занят'
								});
							} else {
								this.$notify.error({
									title: 'Ошибка',
									message: 'Ошибка при сохранении пользователя: неизвестный ответ сервера "' + data + '"'
								});
							}
						},
						complete: function(jqXHR, textStatus) {
							this.user.saving = false;
						}
					});
				}
			},
			lockUser: function(user, index) {
				if (isObject(user) && isNumber(user.id) && isBoolean(user.locked) && (!user.locked)) {
					var app = this;
					this.$confirm('Заблокировать пользователя "' + user.login + '"?', 'Внимание', {
						confirmButtonText: 'Да',
						cancelButtonText: 'Нет',
						type: 'warning'
					}).then(function() {
						app.lockUserCallback(user.id);
					}).catch(function() {});
				}
			},
			lockUserCallback: function(id) {
				if (!isNumber(id)) return;

				this.users.loading = true;
				$.ajax({
					url: ajaxRoot + '/lock?id=' + id,
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						this.$notify.error({
							title: 'Ошибка',
							message: 'Ошибка при блокировке пользователя: ' + textStatus
						});
					},
					success: function(data, textStatus, jqXHR) {
						if (data === 'SUCCESS') {
							this.$message.success('Пользователь заблокирован');
						} else if (data === 'NO_ID') {
							this.$notify.error({
								title: 'Ошибка',
								message: 'Ошибка при блокировке пользователя: не передан ИД пользователя'
							});
						} else if (data === 'NOT_FOUND') {
							this.$notify.error({
								title: 'Ошибка',
								message: 'Ошибка при блокировке пользователя: пользователь не найден'
							});
						}
					},
					complete: function(jqXHR, textStatus) {
						this.users.loading = false;
						this.reloadUsers();
					}
				});
			},
			unlockUser: function(user, index) {
				if (isObject(user) && isNumber(user.id) && isBoolean(user.locked) && user.locked) {
					var app = this;
					this.$confirm('Разблокировать пользователя "' + user.login + '"?', 'Внимание', {
						confirmButtonText: 'Да',
						cancelButtonText: 'Нет',
						type: 'warning'
					}).then(function() {
						app.unlockUserCallback(user.id);
					}).catch(function() {});
				}
			},
			unlockUserCallback: function(id) {
				if (!isNumber(id)) return;

				this.users.loading = true;
				$.ajax({
					url: ajaxRoot + '/unlock?id=' + id,
					dataType: 'json',
					context: this,
					error: function(jqXHR, textStatus, errorThrown) {
						this.$notify.error({
							title: 'Ошибка',
							message: 'Ошибка при разблокировке пользователя: ' + textStatus
						});
					},
					success: function(data, textStatus, jqXHR) {
						if (data === 'SUCCESS') {
							this.$message.success('Пользователь разблокирован');
						} else if (data === 'NO_ID') {
							this.$notify.error({
								title: 'Ошибка',
								message: 'Ошибка при разблокировке пользователя: не передан ИД пользователя'
							});
						} else if (data === 'NOT_FOUND') {
							this.$notify.error({
								title: 'Ошибка',
								message: 'Ошибка при разблокировке пользователя: пользователь не найден'
							});
						}
					},
					complete: function(jqXHR, textStatus) {
						this.users.loading = false;
						this.reloadUsers();
					}
				});
			}
		},
		mounted: function() {
			this.reloadRoles();
			this.reloadOrgs();
			this.reloadUsers();
		}
	});
})(jQuery);
</script>
<!-- components/page-views/admin/auth-manage/users.vue :: end -->