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
					var actualState = {
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
					}
					if (isObject(newState) && isBoolean(newState.anonymousUser) && (!newState.anonymousUser)) {
						actualState.anonymousUser = false;
						if (isObject(newState.principal)) newState = newState.principal;

						actualState.authorities = [];
						if (isArray(newState.authorities) && (newState.authorities.length > 0)) {
							for (var index in newState.authorities) {
								var authority = newState.authorities[index];
								if (isObject(authority) && isNonEmptyString(authority.name)) actualState.authorities.push({name: authority.name});
							}
						}
						if (actualState.authorities.length === 0) actualState.authorities = [{name: 'ROLE_ANONYMOUS'}];
						if (isString(newState.username)) actualState.username = newState.username;
						if (isString(newState.email)) actualState.email = newState.email;
						if (isString(newState.firstName)) actualState.firstName = newState.firstName;
						if (isString(newState.lastName)) actualState.lastName = newState.lastName;
						if (isBoolean(newState.accountNonExpired)) actualState.accountNonExpired = newState.accountNonExpired;
						if (isBoolean(newState.accountNonLocked)) actualState.accountNonLocked = newState.accountNonLocked;
						if (isBoolean(newState.credentialsNonExpired)) actualState.credentialsNonExpired = newState.credentialsNonExpired;
						if (isBoolean(newState.enabled)) actualState.enabled = newState.enabled;
					}
					if (!equals(state, actualState)) {
						state.anonymousUser = actualState.anonymousUser;
						state.authorities = actualState.authorities;
						state.username = actualState.username;
						state.email = actualState.email;
						state.firstName = actualState.firstName;
						state.lastName = actualState.lastName;
						state.accountNonExpired = actualState.accountNonExpired;
						state.accountNonLocked = actualState.accountNonLocked;
						state.credentialsNonExpired = actualState.credentialsNonExpired;
						state.enabled = actualState.enabled;
						console.log('vuex :: account :: updated from', newState, ', new state:', actualState);
					} else {
						//console.log(actualState, 'equals', $.extend(true, {}, state));
					}
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
			},
			getters: {
				authorityNames: (state, getters, rootState, rootGetters) => {
					var result = [];
					for (var authorityIndex in state.authorities) {
						if (isUndefined(state.authorities[authorityIndex])) {
							console.log('undefined', authorityIndex, $.extend(true, [], state.authorities));
						}
						result.push(state.authorities[authorityIndex].name);
					}
					return result;
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