<!-- components/page-views/cash-report.vue :: start -->
<template id="components-page-views-cash-report">
    <div class="report">
        <a href="cash/report">Загрузить отчет 4</a>
            <router-view></router-view>
    </div>
</template>
<!-- components/page-views/cash-report.vue :: middle -->
<style>
    body {
        font-family: Verdana, Arial, sans-serif;
    }

    .el-input-number input {
        text-align: right;
    }

    .report {
        padding: 20px;
    }
</style>
<!-- components/page-views/cash-report.vue :: middle -->
<script>

    (function ($) {
        ELEMENT.locale(ELEMENT.lang.ruRU);
        var ajaxRoot = WorldClassRestRoot + '/cash';
        var componentName = 'components-page-views-cash-report';
        Vue.component(componentName, {
            template: '#' + componentName,
            data: function () {
                return {
                    text: 'Кассовый отчет',
                }
            },
            computed: Vuex.mapState({
                account: state => state.account,
                c: function () {
                    return 'c';
                },
                b: state => 'b'
            }),
            methods: {

            }
        });
    })(jQuery);
</script>
<!-- components/page-views/cash-report.vue :: end -->