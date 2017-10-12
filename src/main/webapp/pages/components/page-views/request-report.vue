<!-- components/page-views/request-report.vue :: start -->
<template id="components-page-views-request-report">
    <div class="report">
        <div>
            <el-row>
                <h1 class="h1">{{ text }}</h1>
            </el-row>
            <el-row>
                <el-button type="success" @click="saveData">Сохранить</el-button>
            </el-row>
            <el-row>
                <el-form>
                    <el-form-item label="Отчетная дата">
                        <el-date-picker type="date" placeholder="Отчетная дата" v-model="formData.date"
                                        :format="'dd.MM.yyyy'"></el-date-picker>
                    </el-form-item>
                </el-form>
            </el-row>

        </div>
        <div>
            <router-view></router-view>
        </div>
    </div>
</template>
<!-- components/page-views/request-report.vue :: middle -->
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
<!-- components/page-views/request-report.vue :: middle -->
<script>

    (function ($) {
        ELEMENT.locale(ELEMENT.lang.ruRU)
        var componentName = 'components-page-views-request-report';
        Vue.component(componentName, {
            template: '#' + componentName,
            data: function () {
                return {
                    text: 'Заявка',
                    formData: {
                        date: "",
                        values: []
                    }
                };
            },
            computed: Vuex.mapState({
                account: state => state.account,
                c: function () {
                    return 'c';
                },
                b: state => 'b'
            }),
            methods: {
                saveData : function () {
                    console.log("test")
                }
            }
        });

        WorldClassPlugins.plugins.push({
            parameters: ['store', 'router'],
            name: componentName,
            dependencies: [],
            install: function (Vue, store, router) {
                router.addRoutes([
                    {
                        path: '/request-report', component: Vue.component(componentName),
                        meta: {
                            requiresAuthorization: true,
                            requiredRoles: ['ROLE_ADMIN', 'ROLE_FILIAL_USER']
                        }
                    }
                ]);
            }
        });
    })(jQuery);
</script>
<!-- components/page-views/request-report.vue :: end -->