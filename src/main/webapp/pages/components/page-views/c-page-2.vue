<!-- components/page-views/c-page-2.vue :: start -->
<template id="components-page-views-c-page-2">
	<div>
		<div>{{ text }}</div>
	</div>
</template>
<!-- components/page-views/c-page-2.vue :: middle -->
<script>
	(function($) {
		var componentName = 'components-page-views-c-page-2';
		Vue.component(componentName, {
			template: '#' + componentName,
			data: function () {
				return {
					text: 'C page 2'
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

		var parentComponentName = 'components-page-views-p-page';
		WorldClassPlugins.plugins.push({
			parameters: ['store', 'router'],
			name: componentName,
			dependencies: ['components-menu-menu-module', parentComponentName],
			install: function(Vue, store, router) {
				// router.addRoutes([
				// 	{
				// 		path: '/p',
				// 		component: Vue.component(parentComponentName),
				// 		children: [
				// 			{
				// 				path: 'c2',
				// 				component: Vue.component(componentName)
				// 			}
				// 		]
				// 	}
				// ]);

				// const info = {
				// 	treePath: ['P page', 'C page 2'],
				// 	route: {path: '/p/c2'},
				// 	requiresAuthorization: false,
				// 	order: 4
				// };
				// store.commit('menu/add', info);
			}
		});
	})(jQuery);
</script>
<!-- components/page-views/c-page-2.vue :: end -->