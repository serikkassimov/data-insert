<!-- components/auth/account-module.vue :: start -->
<script>
	(function($) {
		const module = {
			namespaced: true,
			state: {
				restrictRoutes: false,
				anonymousUser: true,
				authorities: [{name: 'ROLE_ANONYMOUS'}],
				username: '',
				email: '',
				firstName: '',
				lastName: '',
				accountNonExpired: true,
				accountNonLocked: true,
				credentialsNonExpired: true,
				enabled: true,
				lastUpdate: new Date()
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
				resetToAnonimous: function(state) {
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
					if (isBoolean(newState.restrictRoutes)) actualState.restrictRoutes = newState.restrictRoutes;
					else actualState.restrictRoutes = false;
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

					var checkCopy = $.extend(true, {}, state);
					delete checkCopy.lastUpdate;

					if (!equals(checkCopy, actualState)) {
						actualState.lastUpdate = new Date();
						state.restrictRoutes = actualState.restrictRoutes;
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
						state.lastUpdate = actualState.lastUpdate;
						// console.log('vuex :: account :: updated from', newState, ', new state:', actualState);
					} else {
						// console.log(actualState, 'equals', $.extend(true, {}, state));
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
							data.restrictRoutes = true;
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
						result.push(state.authorities[authorityIndex].name);
					}
					return result;
				},
				state: (state) => {
					return state;
				}
			}
		};

		var isForbidden = function(route, account) {
			var result = false;

			if (account.restrictRoutes) {
				var meta = route.meta;
				if (isObject(meta) && meta.requiresAuthorization) {
					if (account.anonymousUser) result = true;
					else if (isNonEmptyArray(meta.requiredRoles)) {
						if (isNonEmptyArray(account.authorities)) {
							result = true;
							for (var index in account.authorities) {
								var authority = account.authorities[index];
								if (isObject(authority)) {
									var indexInRequired = meta.requiredRoles.indexOf(authority.name);
									if (indexInRequired !== -1) {
										result = false;
										break;
									}
								}
							}
						} else result = true;
					}
				}
			}

			return result;
		}

		var checkRoute = function(route, account) {
			var forbidden = isForbidden(route, account);

			if (forbidden) {
				if (isNonEmptyArray(route.matched)) {
					var choosenRoute = undefined;
					for (var index = route.matched.length - 1; (index >= 0) && isUndefined(choosenRoute); index --) {
						var testRoute = route.matched[index];
						if (isRegexp(testRoute.regex) && isNonEmptyArray(testRoute.regex.keys)) continue;
						if (!isForbidden(testRoute, account)) {
							choosenRoute = testRoute;
							break;
						}
					}

					var newPath;
					if (isUndefined(choosenRoute)) newPath = '/';
					else newPath = choosenRoute.path;
					return newPath;
				}
			}
			return undefined;
		}

		var checkRouter = function(router, account) {
			var newPath = checkRoute(router.currentRoute, account);
			if (isNonEmptyString(newPath)) router.push({path: newPath});
		};

		const pluginInfo = {
			name: 'components-auth-account-module',
			parameters: ['store', 'router'],
			install: function(Vue, store, router) {
				store.registerModule('account', module);

				var lastUpdate = new Date();
				store.subscribe(function(mutation, state) {
					if (state.account.lastUpdate != lastUpdate) {
						lastUpdate = state.account.lastUpdate;
						checkRouter(router, state.account);
					}
				});

				router.beforeEach(function(to, from, next) {
					var newPath = checkRoute(to, store.state.account);
					if (isNonEmptyString(newPath)) next(newPath);
					else next();
				});

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