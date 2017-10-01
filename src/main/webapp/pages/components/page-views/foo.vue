<script type="text/x-template" id="components-page-views-foo">
    <div>
        {{ text }}
    </div>
</script>

<script>
	Vue.component('components-page-views-foo', {
		template: '#components-page-views-foo',
		data: function () {
			return {
				text: 'Foo page'
			};
		}
	});
</script>