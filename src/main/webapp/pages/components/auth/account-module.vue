<!-- components/auth/account-module.vue :: start -->
<script>
	(function($) {
		const module = {
			namespaced: true,
			state: {
				anonymousUser: true,
				authorities: [{name: 'ROLE_ANONYMOUS'}],
				username: '',
				email: '',
				firstName: '',
				lastName: '',
				accountNonExpired: true,
				accountNonLocked: true,
				credentialsNonExpired: true,
				enabled: true
				/*
				Example content if user is authorized

				email: 'admin@worldclass.kz',
				firstName: 'Admin',
				lastName: 'Admin',
				username: 'admin',
				accountNonExpired: true,
				accountNonLocked: true,
				credentialsNonExpired: true,
				enabled: true,
				authorities: [{name: 'ROLE_ADMIN'}]
				} */
			},
			mutations: {
				resetToAnonimous: function(state) {;
					state.anonymousUser = true;
					state.authorities = [{name: 'ROLE_ANONYMOUS'}],
					state.username = '';
					state.email = '';
					state.firstName = '';
					state.lastName = '';
					state.accountNonExpired = true;
					state.accountNonLocked = true;
					state.credentialsNonExpired = true;
					state.enabled = true;
					// console.log('resetted to anonimous, new state:', $.extend(true, {}, state));
				},
				updateState: function(state, newState) {
					state.anonymousUser = true;
					state.authorities = [{name: 'ROLE_ANONYMOUS'}],
					state.username = '';
					state.email = '';
					state.firstName = '';
					state.lastName = '';
					state.accountNonExpired = true;
					state.accountNonLocked = true;
					state.credentialsNonExpired = true;
					state.enabled = true;
					if (isObject(newState) && isBoolean(newState.anonymousUser) && (!newState.anonymousUser)) {
						state.anonymousUser = false;
						if (isObject(newState.principal)) newState = newState.principal;

						state.authorities = [];
						if (isArray(newState.authorities) && (newState.authorities.length > 0)) {
							for (var index in newState.authorities) {
								var authority = newState.authorities[index];
								if (isObject(authority) && isNonEmptyString(authority.name)) state.authorities.push({name: authority.name});
							}
						}
						if (state.authorities.length === 0) state.authorities = [{name: 'ROLE_ANONYMOUS'}];
						if (isString(newState.username)) state.username = newState.username;
						if (isString(newState.email)) state.email = newState.email;
						if (isString(newState.firstName)) state.firstName = newState.firstName;
						if (isString(newState.lastName)) state.lastName = newState.lastName;
						if (isBoolean(newState.accountNonExpired)) state.accountNonExpired = newState.accountNonExpired;
						if (isBoolean(newState.accountNonLocked)) state.accountNonLocked = newState.accountNonLocked;
						if (isBoolean(newState.credentialsNonExpired)) state.credentialsNonExpired = newState.credentialsNonExpired;
						if (isBoolean(newState.enabled)) state.enabled = newState.enabled;
					}
					// console.log('updated from', newState, ', new state:', $.extend(true, {}, state));
				}
			},
			actions: {
				update: function(context) {
					$.ajax({
						url: '/data-insert/auth/info',
						context: context,
						dataType: 'json',
						error: function(jqXHR, textStatus, errorThrown) {
							console.error('Error while updating account: ' + textStatus + ' - ' + errorThrown);
						},
						success: function(data, textStatus, jqXHR) {
							this.commit('updateState', data);
						},
						complete: function(jqXHR, textStatus) {}
					});
				}
			}
		};

		const pluginInfo = {
			name: 'components-auth-account-module',
			parameters: ['store', 'router'],
			install: function(Vue, store, router) {
				store.registerModule('account', module);

				setInterval(function(){
					store.dispatch('account/update');
				}, 5000);
				store.dispatch('account/update');
			}
		};

		WorldClassPlugins.plugins.push(pluginInfo);
	})(jQuery);
</script>
<!-- components/auth/account-module.vue :: end -->