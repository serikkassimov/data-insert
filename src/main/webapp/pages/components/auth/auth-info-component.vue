<!-- components/auth/auth-info-component.vue :: start -->
<template id="components-auth-auth-info">
	<span>
		<template v-if="account.anonymousUser">
			<el-button @click="showLoginDialog">Войти</el-button>
		</template>
		<template v-else>
			<el-tag>{{ account.username }} ({{ account.email }})</el-tag>
			<el-button @click="startLogout">Выйти</el-button>
		</template>
		<el-dialog title="Вход" size="tiny" :visible.sync="login.dialog.visible" v-loading.body="login.dialog.loading">
			<span slot="title" class="dialog-title">
				Вход
			</span>
			<el-form ref="loginForm" :model="login.form.data" :rules="login.form.rules" :label-position="'right'" :label-width="'100px'">
				<el-form-item label="Логин" prop="login" :required="true">
					<el-input v-model="login.form.data.login" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="Пароль" prop="password" :required="true">
					<el-input v-model="login.form.data.password" type="password" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="Запомнить меня" :required="false">
					<el-checkbox v-model="login.form.data.rememberMe"></el-checkbox>
				</el-form-item>
			</el-form>
			<span slot="footer" class="dialog-footer">
				<template v-if="login.dialog.error && (login.dialog.error.length > 0)">
					<div>{{ login.dialog.error }}</div>
				</template>
				<el-button type="primary" @click="startLogin">Войти</el-button>
				<el-button @click="login.dialog.visible = false">Отмена</el-button>
			</span>
		</el-dialog>
	</span>
</template>
<!-- components/auth/auth-info-component.vue :: middle -->
<script>
	(function($) {
		Vue.component('components-auth-auth-info', {
			template: '#components-auth-auth-info',
			data: function () {
				return {
					login: {
						dialog: {
							visible: false,
							loading: false,
							error: ''
						},
						form: {
							data: {
								login: '',
								password: '',
								rememberMe: false
							},
							rules: {
								login: [
									{required: true, message: 'Необходимо ввести логин (макс 50 символов)', trigger: 'blur'},
									{min: 1, max: 50, message: 'Необходимо ввести логин (макс 50 символов)', trigger: 'blur'}
								],
								password: [
									{required: true, message: 'Необходимо ввести пароль (макс 50 символов)', trigger: 'blur'},
									{min: 1, max: 50, message: 'Необходимо ввести пароль (макс 50 символов)', trigger: 'blur'}
								]
							}
						}
					}
				};
			},
			computed: Vuex.mapState({
				account: state => state.account,
				c: function() {
					return 'c';
				},
				b: state => 'b'
			}),
			methods: {
				showLoginDialog: function() {
					this.login.dialog.error = '';
					this.login.dialog.visible = true;
				},
				startLogin: function() {
					this.login.dialog.error = '';
					this.$refs.loginForm.validate(this.loginValidationCallback);
				},
				loginValidationCallback: function(valid) {
					if (valid) {
						var data = {
							'login': this.login.form.data.login,
							'password': this.login.form.data.password
						};
						if (this.login.form.data.rememberMe) data['remember-me'] = 'true';

						this.login.dialog.loading = true;

						$.ajax({
							url: '/data-insert/auth/login',
							method: 'POST',
							context: this,
							data: data,
							dataType: 'json',
							error: function(jqXHR, textStatus, errorThrown) {
								this.login.dialog.error = textStatus + ': ' + errorThrown;
							},
							success: function(data, textStatus, jqXHR) {
								var results = ['SUCCESS', 'EXCEPTION', 'BAD_CREDENTIALS'];
								switch (results.indexOf(data)) {
									case 0: // SUCCESS
										this.login.dialog.visible = false;
										var message = this.$message;
										this.$store.dispatch('account/update').then(function() {
											message.success('Вход выполнен');
										});
										break;
									case 1: // EXCEPTION
										this.login.dialog.error = 'Неизвестная ошибка входа';
										break;
									case 2: // BAD_CREDENTIALS
										this.login.dialog.error = 'Неправильный логин или пароль';
										break;
									default:
										this.login.dialog.error = 'Неизвестный ответ сервера: ' + data;
										break;
								}
							},
							complete: function(jqXHR, textStatus) {
								this.login.dialog.loading = false;
							}
						});
						return true;
					} else {
						return false;
					}
				},
				startLogout: function() {
					$.ajax({
						url: '/data-insert/auth/logout',
						method: 'GET',
						context: this,
						dataType: 'json',
						error: function(jqXHR, textStatus, errorThrown) {
							this.$message.error('Ошибка: ' + textStatus + ' - ' + errorThrown);
						},
						success: function(data, textStatus, jqXHR) {
							var results = ['SUCCESS'];
							switch (results.indexOf(data)) {
								case 0: // SUCCESS
									this.login.dialog.visible = false;
									this.$message.success('Выход выполнен');
									this.$store.dispatch('account/update');
									break;
								default:
									this.$message.error('Неизвестный ответ сервера: ' + data);
									break;
							};
						}
					});
				}
			}
		});
	})(jQuery);
</script>
<!-- components/auth/auth-info-component.vue :: end -->