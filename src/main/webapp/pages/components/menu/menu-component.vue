<!-- components/menu/menu-component.vue :: start -->
<template id="components-menu-menu-element-component">
	<div>
		<template v-if="item.children && (item.children.length > 0)">
			<el-submenu :index="item.index">
				<template slot="title">
					<i class="el-icon-menu"></i>
					<span slot="title">{{item.label}}</span>
				</template>
				<template v-if="item.route">
					<el-menu-item :index="'--' + item.index" @click="elementClick">{{item.label}}</el-menu-item>
					<el-menu-item-group>
						<span slot="title">
							<hr>
						</span>
						<template v-for="(child, childIndex) in item.children">
							<components-menu-menu-element-component :item="child" v-bind:key="child.index" @click="childClick"></components-menu-menu-element-component>
						</template>
					</el-menu-item-group>
				</template>
				<template v-else>
					<template v-for="(child, childIndex) in item.children">
						<components-menu-menu-element-component :item="child" v-bind:key="child.index" @click="childClick"></components-menu-menu-element-component>
					</template>
				</template>
			</el-submenu>
		</template>
		<template v-else>
			<el-menu-item :index="'--' + item.index" @click="elementClick">{{item.label}}</el-menu-item>
		</template>
	</div>
</template>
<!-- -->
<template id="components-menu-menu-component">
	<span>
		<el-menu :unique-opened="true" :default-active="activeIndex">
			<template v-for="(item, index) in menu">
				<components-menu-menu-element-component :item="item" @click="click"></components-menu-menu-element-component>
			</template>
		</el-menu>
	</span>
</template>
<!-- components/menu/menu-component.vue :: middle -->
<script>
	(function() {
		Vue.component('components-menu-menu-element-component', {
			template: '#components-menu-menu-element-component',
			props: ['item'],
			data: function() {
				return {};
			},
			computed: Vuex.mapState({}),
			methods: {
				elementClick: function() {
					this.$emit('click', {
						item: this.item,
						component: this
					});
				},
				childClick: function(eventData) {
					this.$emit('click', eventData);
				}
			}
		});

		var listRouteItems = function(tree, resultList) {
			for (var index in tree) {
				var item = tree[index];
				if (isObject(item.route)) resultList[item.index] = item;
				if (isNonEmptyArray(item.children)) listRouteItems(item.children, resultList);
			}
		};

		Vue.component('components-menu-menu-component', {
			template: '#components-menu-menu-component',
			data: function () {
				return {};
			},
			computed: Vuex.mapState({
				menu: (state, getters) => {
					return getters['menu/treeSecured'];
				},
				activeIndex: function(state, getters) {
					var routeItems = {};
					listRouteItems(this.menu, routeItems);

					var currentRoute = this.$route;
					var bestMatchItem = undefined;
					var matchCount = 0;
					for (var index in routeItems) {
						var routeItem = routeItems[index];
						var match = this.$router.matcher.match(routeItem.route);
						if (currentRoute.fullPath === match.fullPath) {
							bestMatchItem = routeItem;
							matchCount = currentRoute.matched.length;
							break;
						}
						if ((currentRoute.matched.length > 0) && (match.matched.length > 0)) {
							var currentMatchCount = 0;
							for (var i = 0; i < Math.min(currentRoute.matched.length, match.matched.length); i++) {
								if (currentRoute.matched[i].path !== match.matched[i].path) break;
								currentMatchCount++;
							}
							if (currentMatchCount > matchCount) {
								bestMatchItem = routeItem;
								matchCount = currentMatchCount;
							}
						}
					}

					if (isObject(bestMatchItem)) return '--' + bestMatchItem.index;
					else return undefined;
				}
			}),
			methods: {
				click: function(eventData) {
					this.$router.push(eventData.item.route);
				}
			}
		});
	})();
</script>
<!-- components/menu/menu-component.vue :: end -->