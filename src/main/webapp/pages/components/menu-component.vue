<!-- components/menu-component.vue :: start -->
<template id="menu-component">
	<div>
		<el-menu theme="dark" :unique-opened="true" :router="true" :default-active="activeIndex" :collapse="collapsed">
			<el-submenu index="/admin" v-if="isAllowed('/admin')">
				<template slot="title">Администрирование</template>
				<el-submenu index="/admin/dict" v-if="isAllowed('/admin/dict')">
					<template slot="title">Справочники</template>
					<el-menu-item index="/admin/dict/budget" v-if="isAllowed('/admin/dict/budget')">Статьи изменений бюджета</el-menu-item>
					<el-menu-item index="/admin/dict/budgetNextChangeTypes" v-if="isAllowed('/admin/dict/budgetNextChangeTypes')">Типы изменений бюджета</el-menu-item>
					<el-menu-item index="/admin/dict/budgetNextChangeStates" v-if="isAllowed('/admin/dict/budgetNextChangeStates')">Статусы изменений бюджета</el-menu-item>
					<el-menu-item index="/admin/dict/budgetStoreTypes" v-if="isAllowed('/admin/dict/budgetStoreTypes')">Типы хранения бюджета</el-menu-item>
					<el-menu-item index="/admin/dict/orgs" v-if="isAllowed('/admin/dict/orgs')">Организации</el-menu-item>
					<el-menu-item index="/admin/dict/roles" v-if="isAllowed('/admin/dict/roles')">Роли</el-menu-item>
					<el-menu-item index="/admin/dict/currency" v-if="isAllowed('/admin/dict/currency')">Валюта</el-menu-item>
				</el-submenu>
				<el-submenu index="/admin/auth-manage" v-if="isAllowed('/admin/auth-manage')">
					<template slot="title">Авторизация</template>
					<el-menu-item index="/admin/auth-manage/users" v-if="isAllowed('/admin/auth-manage/users')">Пользователи</el-menu-item>
				</el-submenu>
			</el-submenu>
			<el-submenu index="/reports" v-if="isAllowed('/reports')">
				<template slot="title">Отчеты</template>
				<el-menu-item index="/cash-report" v-if="isAllowed('/reports/cash-report-hq')">Отчеты Excel</el-menu-item>
				<el-menu-item index="/reports/cash-report-affiliate" v-if="isAllowed('/reports/cash-report-affiliate')">Кассовый отчет филиала</el-menu-item>
				<el-menu-item index="/reports/cash-report-hq" v-if="isAllowed('/reports/cash-report-hq')">Кассовые отчеты филиалов</el-menu-item>

			</el-submenu>
			<el-submenu index="/requests" v-if="isAllowed('/requests')">
				<template slot="title">Заявки</template>
				<el-menu-item index="/requests/expenses-request-affiliate" v-if="isAllowed('/requests/expenses-request-affiliate')">Заявка на расходы</el-menu-item>
			</el-submenu>
		</el-menu>
	</div>
</template>
<!-- components/menu-component.vue :: middle -->
<script>
(function($){
	var createMeta = function(requiredRoles, componentName, additional) {
		var result;
		if (isObject(additional)) result = $.extend(true, {}, additional);
		else result = {};

		if (isNonEmptyArray(requiredRoles)) {
			for (var index in requiredRoles) {
				var role = requiredRoles[index];
				if (!isNonEmptyString(role)) throw new Error('required role is not non-empty string: ' + role + ' (type ' + (typeof role) + ')');
			}
			result.requiredRoles = requiredRoles;
			result.requiresAuthorization = true;
		} else result.requiresAuthorization = false;

		if (isNonEmptyString(componentName)) result.componentName = componentName;

		return result;
	};

	var meta = {
		'/admin': createMeta(['ROLE_ADMIN']),
		'/admin/dict': createMeta(['ROLE_ADMIN']),
		'/admin/dict/budget': createMeta(['ROLE_ADMIN'], 'page-views-admin-dict-budget'),
		'/admin/dict/budgetNextChangeTypes': createMeta(['ROLE_ADMIN'], 'page-views-admin-dict-base', {dict: {type: 'budgetNextChangeTypes', name: 'Типы изменений бюджета'}}),
		'/admin/dict/budgetNextChangeStates': createMeta(['ROLE_ADMIN'], 'page-views-admin-dict-base', {dict: {type: 'budgetNextChangeStates', name: 'Статусы изменений бюджета'}}),
		'/admin/dict/budgetStoreTypes': createMeta(['ROLE_ADMIN'], 'page-views-admin-dict-base', {dict: {type: 'budgetStoreTypes', name: 'Типы хранения бюджета'}}),
		'/admin/dict/orgs': createMeta(['ROLE_ADMIN'], 'page-views-admin-dict-base', {dict: {type: 'orgs', name: 'Организации'}}),
		'/admin/dict/roles': createMeta(['ROLE_ADMIN'], 'page-views-admin-dict-base', {dict: {type: 'roles', name: 'Роли'}}),
		'/admin/dict/currency': createMeta(['ROLE_ADMIN'], 'page-views-admin-dict-currency'),
		'/admin/auth-manage': createMeta(['ROLE_ADMIN']),
		'/admin/auth-manage/users': createMeta(['ROLE_ADMIN'], 'page-views-admin-auth-manage-users'),

		'/reports': createMeta(['ROLE_HQ', 'ROLE_FILIAL_USER']),
		'/cash-report': createMeta(['ROLE_ADMIN', 'ROLE_FILIAL_USER'], 'components-page-views-cash-report'),
		'/reports/cash-report-affiliate': createMeta(['ROLE_FILIAL_USER'], 'page-views-reports-cash-report-affiliate'),
		'/reports/cash-report-hq': createMeta(['ROLE_HQ'], 'page-views-reports-cash-report-hq'),
		'/requests': createMeta(['ROLE_HQ', 'ROLE_FILIAL_USER']),
		'/requests/expenses-request-affiliate': createMeta(['ROLE_FILIAL_USER'], 'page-views-requests-expenses-request-affiliate'),
		'/request-report': createMeta(['ROLE_ADMIN', 'ROLE_FILIAL_USER'], 'components-page-views-request-report')
	};

	var componentName = 'menu-component';
	Vue.component(componentName, {
		template: '#' + componentName,
		data: function() {
			return {
				collapsed: false,
				meta: meta
			}
		},
		computed: Vuex.mapState({
			account: state => state.account,
			activeIndex: function() {
				return this.$route.fullPath;
			}
		}),
		methods: {
			isAllowed: function(path) {
				return this.account.restrictRoutes && (!isForbidden({meta: this.meta[path]}, this.account));
			}
		}
	});

	WorldClassPlugins.plugins.push({
		name: componentName,
		dependencies: [],
		parameters: ['store', 'router'],
		install: function (Vue, store, router) {
			var routes = [];
			for (var index in meta) {
				if (isNonEmptyString(meta[index].componentName)) {
					var component = Vue.component(meta[index].componentName);
					if (!isFunction(component)) throw new Error('no component with name "' + meta[index].componentName + '"');
					routes.push({
						path: index,
						component: component,
						meta: meta[index]
					});
				}
			}
			router.addRoutes(routes);
		}
	});
})(jQuery);
</script>
<!-- components/menu-component.vue :: end -->