<!-- components/page-views/c-page-1.vue :: start -->
<template id="components-page-views-c-page-1">
	<div>
		<div>{{ text }} :: {{id}}</div>
	</div>
</template>
<!-- components/page-views/c-page-1.vue :: middle -->
<script>
	(function($) {
		var componentName = 'components-page-views-c-page-1';
		Vue.component(componentName, {
			template: '#' + componentName,
			data: function () {
				return {
					text: 'C page 1'
				};
			},
			computed: Vuex.mapState({
				account: state => state.account,
				c: function() {
					return 'c';
				},
				b: state => 'b',
				id: function() {
					return this.$route.params.id;
				}
			}),
			methods: {}
		});

		var parentComponentName = 'components-page-views-p-page';
		WorldClassPlugins.plugins.push({
			parameters: ['store', 'router'],
			name: componentName,
			dependencies: ['components-menu-menu-module', parentComponentName],
			install: function(Vue, store, router) {
				router.addRoutes([
					{
						path: '/p',
						component: Vue.component(parentComponentName),
						children: [
							{
								path: 'c1',
								component: Vue.component(componentName)
							},
							{
								path: 'c1/:id',
								component: Vue.component(componentName)
							}
						]
					}
				]);

				var info = {
					treePath: ['P page', 'C page 1'],
					route: {path: '/p/c1'},
					requiresAuthorization: false,
					order: 0
				};
				store.commit('menu/add', info);
				info = {
					treePath: ['P page', 'C with ID', 'C page 1-1'],
					route: {path: '/p/c1/1'},
					requiresAuthorization: false,
					order: 1
				};
				store.commit('menu/add', info);
				info = {
					treePath: ['P page', 'C with ID', 'C page 1-2'],
					route: {path: '/p/c1/2'},
					requiresAuthorization: false,
					order: 2
				};
				store.commit('menu/add', info);
			}
		});
	})(jQuery);
</script>
<!-- components/page-views/c-page-1.vue :: end -->