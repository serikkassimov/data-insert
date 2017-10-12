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
				<el-menu-item index="/cash-report" v-if="isAllowed('/cash-report')">Кассовый отчет</el-menu-item>
				<el-menu-item index="/request-report" v-if="isAllowed('/request-report')">Заявка</el-menu-item>
			</el-submenu>
		</el-menu>
	</div>
</template>
<!-- components/menu-component.vue :: middle -->
<script>
var templates = {
	ROLE_ADMIN: {
		requiresAuthorization: true,
		requiredRoles: ['ROLE_ADMIN']
	},
	ROLE_ADMIN_ROLE_FILIAL_USER: {
		requiresAuthorization: true,
		requiredRoles: ['ROLE_ADMIN', 'ROLE_FILIAL_USER']
	}
};

var componentName = 'menu-component';
Vue.component(componentName, {
	template: '#' + componentName,
	data: function() {
		return {
			collapsed: false,
			securityMeta: {
				'/admin': templates.ROLE_ADMIN,
				'/admin/dict': templates.ROLE_ADMIN,
				'/admin/dict/budget': templates.ROLE_ADMIN,
				'/admin/dict/budgetNextChangeTypes': templates.ROLE_ADMIN,
				'/admin/dict/budgetNextChangeStates': templates.ROLE_ADMIN,
				'/admin/dict/budgetStoreTypes': templates.ROLE_ADMIN,
				'/admin/dict/orgs': templates.ROLE_ADMIN,
				'/admin/dict/currency': templates.ROLE_ADMIN,
				'/admin/auth-manage': templates.ROLE_ADMIN,
				'/admin/auth-manage/users': templates.ROLE_ADMIN,

				'/reports': templates.ROLE_ADMIN_ROLE_FILIAL_USER,
				'/cash-report': templates.ROLE_ADMIN_ROLE_FILIAL_USER,
				'/request-report': templates.ROLE_ADMIN_ROLE_FILIAL_USER
			}
		}
	},
	computed: Vuex.mapState({
		account: state => state.account,
		activeIndex: function() {
			return this.$route.fullPath;
		}
	}),
	methods: {
		isForbidden: function(metaName) {
			return isForbidden({meta: this.securityMeta[metaName]}, this.account);
		},
		isAllowed: function(metaName) {
			return !isForbidden({meta: this.securityMeta[metaName]}, this.account);
		}
	}
});
</script>
<!-- components/menu-component.vue :: end -->