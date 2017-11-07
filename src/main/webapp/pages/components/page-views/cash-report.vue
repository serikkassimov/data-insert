<!-- components/page-views/cash-report.vue :: start -->
<template id="components-page-views-cash-report">
    <div class="report">
        <el-date-picker
                v-model="month"
                type="month"
                placeholder="Pick a month">
        </el-date-picker>
        <p><a href="cash/report">Загрузить отчет 4</a>
           
        </p>
        <p><a @click="loadReport">Загрузить отчет 4</a>
            
        </p>
        <p>
            <a href="cash/report_zip">Загрузить архив отчета 4</a>
        </p>
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
        var ajaxRoot = WorldClassRestRoot + '/cash';
        var componentName = 'components-page-views-cash-report';
        Vue.component(componentName, {
            template: '#' + componentName,
            data: function () {
                return {
                    text: 'Кассовый отчет',
                    month: "2017-10-01T00:00:00.000Z",
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
                loadReport: function () {
                    var begin = this.month.getTime();
                    window.open("cash/report/"+begin+"/"+begin,"_self")
                }
            }
        });
    })(jQuery);
</script>
<!-- components/page-views/cash-report.vue :: end -->