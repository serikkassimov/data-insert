<!-- components/menu/menu-module.vue :: start -->
<script>
(function($) {
	/*
	requiredRoles accepted only if it is non-empty array
	route - see "location" parameter for method "router.push" at https://router.vuejs.org/ru/essentials/navigation.html
	Example usage:
	const nodeInfo = {
		treePath: ['Root node', 'Parent node', 'This node'],
		route: {},
		requiresAuthorization: true,
		requiredRoles: ['ROLE_USER_1', 'ROLE_USER_2'],
		order: 0
	};
	...
	store.commit('menu/add', nodeInfo);
	...
	store.commit('menu/remove', nodeInfo);
	// or
	store.commit('menu/remove', {treePath: nodeInfo.treePath});
	*/
	var getId = function(treePath) {
		var result = '';
		for (var index in treePath) result += '/' + index + ':' + treePath[index];
		return result;
	};

	var checkTreePath = function(nodeInfo) {
		if (!isObject(nodeInfo)) throw new Error('nodeInfo is not object: type "' + $.type(nodeInfo) + '"');

		var result = nodeInfo.treePath;
		if (!isNonEmptyArray(result)) throw new Error(
			'nodeInfo.treePath is not non-empty array: "' + result + '" (type "' + $.type(result) + '")'
		);
		for (var treePathIndex in result) {
			var treePathElement = result[treePathIndex];
			if (!isNonEmptyString(treePathElement)) throw new Error(
				'nodeInfo.treePath[' + treePathIndex + '] is not non-empty string: "' + treePathElement + '" (type "' + $.type(treePathElement) + '")'
			);
		}
		return result;
	};

	var fillTree = function(tree, info, secured, account) {
		if (secured && info.requiresAuthorization) {
			if (account.anonymousUser || (!account.accountNonExpired) || (!account.accountNonLocked) || (!account.credentialsNonExpired) || (!account.enabled)) return;
			else if (info.requiredRoles.length > 0) {
				if (account.authorityNames.length === 0) return;
				else {
					var overlapExists = false;
					for (var requiredRoleIndex in info.requiredRoles) {
						if (account.authorityNames.indexOf(info.requiredRoles[requiredRoleIndex]) > -1) {
							overlapExists = true;
							break;
						}
					}
					if (!overlapExists) return;
				}
			}
		}

		var children = tree;
		var child = undefined;
		for (var labelIndex in info.treePath) {
			var label = info.treePath[labelIndex];

			child = undefined;
			for (var childIndex in children) {
				var testChild = children[childIndex];
				if (testChild.label === label) {
					child = testChild;
					break;
				}
			}

			if (isUndefined(child)) {
				var order = info.order;
				child = {
					label: label,
					children: [],
					requiresAuthorization: info.requiresAuthorization,
					requiredRoles: info.requiredRoles,
					order: order
				};

				if (children.length === 0) children.push(child);
				else {
					var insertIndex = 0;
					while (insertIndex < children.length) {
						var testChild = children[insertIndex];
						if (testChild.order > order) break;
						else if (testChild.order === order) {
							var testLabel = testChild.label.toLowerCase();
							var currentLabel = label.toLowerCase();

							if (testLabel > currentLabel) break;

							if ((testLabel === currentLabel) && (testChild.label < label)) break;
						}
						insertIndex++;
					}

					if (insertIndex === children.length) children.push(child);
					else children.splice(insertIndex, 0, child);
				}
			} else {
				if (!info.requiresAuthorization) child.requiresAuthorization = false;
				if (child.requiresAuthorization) {
					for (var roleIndex in info.requiredRoles) {
						var role = info.requiredRoles[roleIndex];
						if (child.requiredRoles.indexOf(role) === -1) child.requiredRoles.push(role);
					}
				} else child.requiredRoles = [];
			}

			children = child.children;
		}

		if (isObject(info.route)) child.route = $.extend(true, {}, info.route);
		else child.route = info.route;
		child.requiresAuthorization = info.requiresAuthorization;
		child.requiredRoles = $.extend(true, [], info.requiredRoles);
	};

	var createIndices = function(tree, parentIndex) {
		for (var index in tree) {
			var element = tree[index];
			var elementIndex = '' + index;
			if (isNonEmptyString(parentIndex)) elementIndex = parentIndex + '-' + elementIndex;
			element.index = elementIndex;
			if (isNonEmptyArray(element.children)) {
				for (var childIndex in element.children) createIndices(element.children, elementIndex);
			}
		}
	};

	const module = {
		namespaced: true,
		state: {
			menu: {
				/*
				'/0:Root node/1:Parent node/2:This node': {
					treePath: ['Root node', 'Parent node', 'This node'],
					route: {},
					requiresAuthorization: true,
					requiredRoles: ['ROLE_USER_1', 'ROLE_USER_2'],
					order: 0
				}
				*/
			}
		},
		mutations: {
			add: function(state, nodeInfo) {
				if (!isObject(nodeInfo)) throw new Error('nodeInfo is not object: type "' + $.type(nodeInfo) + '"');
				var treePath = checkTreePath(nodeInfo);

				var requiresAuthorization = nodeInfo.requiresAuthorization;
				if (!isBoolean(requiresAuthorization)) throw new Error(
					'nodeInfo.requiresAuthorization is not boolean: type "' + $.type(requiresAuthorization) + '"'
				);

				var requiredRoles;
				if (requiresAuthorization) {
					requiredRoles = nodeInfo.requiredRoles;
					if (isNonEmptyArray(requiredRoles)) {
						for (var requiredRoleIndex in requiredRoles) {
							var requiredRole = requiredRoles[requiredRoleIndex];
							if (!isNonEmptyString(requiredRole)) throw new Error(
								'nodeInfo.requiredRoles[' + requiredRoleIndex + '] is not non-empty string: "' + requiredRole + '" (type "' + $.type(requiredRole) + '")'
							);
						}
					} else requiredRoles = [];
				} else requiredRoles = [];

				var order = nodeInfo.order;
				if (!isComparableNumber(order)) throw new Error(
					'nodeInfo.order is not comparable number: ' + order + ' type "' + $.type(order) + '"'
				);

				var info = {
					treePath: $.extend(true, [], treePath),
					requiresAuthorization: requiresAuthorization,
					requiredRoles: requiredRoles,
					order: order
				};

				var route = nodeInfo.route;
				if (isNonEmptyString(route)) info.route = route;
				else if (isObject(route)) {
					var path = route.path;
					var name = route.name;
					if ((!isNonEmptyString(path)) && (!isNonEmptyString(name))) throw new Error(
						'nodeInfo.route.path is not non-empty string: "' + path + '" (type "' + $.type(path) + '"); '
							+ 'nodeInfo.route.name is not non-empty string: "' + name + '" (type "' + $.type(name) + '");'
					);

					info.route = {};
					
					if (isNonEmptyString(path)) {
						var query = nodeInfo.query;
						if (isDefined(query)) {
							if (!isObject(query)) throw new Error(
								'nodeInfo.query is defined and not object: type "' + $.type(query) + '"'
							);
							info.route.query = query;
						}
						info.route.path = path;
					}
					if (isNonEmptyString(name)) {
						var params = nodeInfo.params;
						if (isDefined(params)) {
							if (!isObject(params)) throw new Error(
								'nodeInfo.params is defined and not object: type "' + $.type(params) + '"'
							);
							info.route.params = params;
						}
						info.route.name = name;
					}
				} else throw new Error(
					'nodeInfo.route is not non-empty string or object: "' + route + '" (type "' + $.type(route) + '")'
				);

				var id = getId(treePath);

				Vue.set(state.menu, id, info);
			},
			remove: function(state, nodeInfo) {
				if (!isObject(nodeInfo)) throw new Error('nodeInfo is not object: type "' + $.type(nodeInfo) + '"');
				var treePath = checkTreePath(nodeInfo);
				var id = getId(treePath);
				Vue.delete(state.menu, id);
			}
		},
		actions: {},
		getters: {
			tree: (state, getters, rootState, rootGetters) => {
				var result = [];
				for (var index in state.menu) fillTree(
					result,
					state.menu[index],
					false
				);
				createIndices(result);
				return result;
			},
			treeSecured: (state, getters, rootState, rootGetters) => {
				var account = {
					anonymousUser: rootState.account.anonymousUser,
					accountNonExpired: rootState.account.accountNonExpired,
					accountNonLocked: rootState.account.accountNonLocked,
					credentialsNonExpired: rootState.account.credentialsNonExpired,
					enabled: rootState.account.enabled,
					authorityNames: rootGetters['account/authorityNames']
				};
				var result = [];
				for (var index in state.menu) fillTree(
					result,
					state.menu[index],
					true,
					account
				);
				createIndices(result);
				return result;
			}
		}
	};

	const pluginInfo = {
		name: 'components-menu-menu-module',
		parameters: ['store', 'router'],
		dependencies: [],
		install: function(Vue, store, router) {
			store.registerModule('menu', module);
		}
	};

	WorldClassPlugins.plugins.push(pluginInfo);
})(jQuery);
</script>
<!-- components/menu/menu-module.vue :: end -->