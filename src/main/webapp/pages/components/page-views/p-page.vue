<!-- components/page-views/p-page.vue :: start -->
<template id="components-page-views-p-page">
	<div>
		<div>{{ text }}</div>
		<div>
			<router-view></router-view>
		</div>
	</div>
</template>
<!-- components/page-views/p-page.vue :: middle -->
<script>
	(function($) {
		var componentName = 'components-page-views-p-page';
		Vue.component(componentName, {
			template: '#' + componentName,
			data: function () {
				return {
					text: 'P page'
				};
			},
			computed: Vuex.mapState({
				account: state => state.account,
				c: function() {
					return 'c';
				},
				b: state => 'b'
			}),
			methods: {}
		});

		WorldClassPlugins.plugins.push({
			parameters: ['store', 'router'],
			name: componentName,
			dependencies: ['components-menu-menu-module'],
			install: function(Vue, store, router) {
				router.addRoutes([
					{
						path: '/p', component: Vue.component(componentName)
					}
				]);

				const info = {
					treePath: ['P page'],
					route: {path: '/p'},
					requiresAuthorization: false,
					order: 0
				};
				store.commit('menu/add', info);
			}
		});
	})(jQuery);
</script>
<!-- components/page-views/p-page.vue :: end -->