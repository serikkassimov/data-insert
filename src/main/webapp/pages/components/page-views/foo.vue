<script type="text/x-template" id="components_page-views_foo">
    <div>
        {{ text }}
    </div>
</script>

<script>
	Vue.component('components_page-views_foo', {
		template: '#components_page-views_foo',
		data: function () {
			return {
				text: 'Foo page'
			};
		}
	});
</script>