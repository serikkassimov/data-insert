<!-- components/page-views/c-page-1.vue :: start -->
<template id="components-page-views-c-page-1">
	<div>
		<div>{{ text }}</div>
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
				b: state => 'b'
			}),
			methods: {}
		});

		var parentComponentName = 'components-page-views-p-page';
		WorldClassPlugins.plugins.push({
			parameters: ['store', 'router'],
			name: componentName,
			dependencies: [parentComponentName],
			install: function(Vue, store, router) {
				router.addRoutes([
					{
						path: '/p',
						component: Vue.component(parentComponentName),
						children: [
							{
								path: 'c1',
								component: Vue.component(componentName)
							}
						]
					}
				]);
			}
		});
	})(jQuery);
</script>
<!-- components/page-views/c-page-1.vue :: end -->