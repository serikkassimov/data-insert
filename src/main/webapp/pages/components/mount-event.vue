<!-- components/mount-event.vue :: start -->
<template id="mount-event">
	<div style="display: none;"></div>
</template>
<!-- components/mount-event.vue :: middle -->
<script>
(function() {
	Vue.component('mount-event', {
		template: '#mount-event',
		mounted: function() {
			this.$emit('mounted');
		}
	});
})();
</script>
<!-- components/mount-event.vue :: end -->