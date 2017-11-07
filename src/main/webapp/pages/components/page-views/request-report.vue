<!-- components/page-views/request-report.vue :: start -->
<template id="components-page-views-request-report">
    <div class="report">
        <div>
            <el-row>
                <h1 class="h1">{{ text }}</h1>
            </el-row>

            <el-row>
                <el-form>
                    <el-form-item label="Отчетная дата">
                        <el-date-picker type="date" placeholder="Дата платежа" v-model="formData.date"
                                        :format="'dd.MM.yyyy'"></el-date-picker>
                    </el-form-item>
                </el-form>
            </el-row>
            <el-row>
                <el-button type="success" @click="addData">Новая заявка</el-button>
            </el-row>
            <el-form>
                <table>
                    <thead>
                    <tr>
                        <th>Номер</th>
                        <th>Вид</th>
                        <th>Статья бюджета</th>
                        <th>Назначение</th>
                        <th>Сумма</th>
                        <th>Действие</th>
                    </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(item, index) in formData.values">
                            <td>{{index+1}}</td>
                            <td>
                                <el-select v-model="item.store_type" placeholder="Выберите тип">
                                    <el-option v-for="item in dict_fin_store_type" :label="item.name"
                                               :value="item.code"></el-option>
                                </el-select>
                            </td>
                            <td>
                                <el-select v-model="item.budget" placeholder="Выберите статью бюджета">
                                    <el-option v-for="item in dict_budget" :label="item.name" :value="item.code"></el-option>
                                </el-select>
                            </td>
                            <td>
                                <el-input v-model="item.note" placeholder="Назначение текст"></el-input>
                            </td>
                            <td>
                                <el-input-number v-model="item.sum" placeholder="Сумма"></el-input-number>
                            </td>
                            <td>
                                <el-button @click="deleteRow(index)">Удалить</el-button>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <!--
                <el-table
                        :data="formData.values"
                        border
                        style="width: 100%"
                        max-height="250">
                    <el-table-column
                            type="index"
                            label="Номер"
                            width="100">
                    </el-table-column>
                    <el-table-column
                            prop="store_type"
                            label="Вид"
                            width="150">
                    </el-table-column>
                    <el-table-column
                            prop="note"
                            label="Назначение">
                    </el-table-column>
                    <el-table-column
                            prop="budget"
                            label="Статья бюджета"
                            width="350">
                    </el-table-column>
                    <el-table-column
                            prop="sum"
                            label="Сумма"
                            width="150">
                    </el-table-column>
                    <el-table-column
                            label="Operations"
                            width="120">
                        <template scope="scope">
                            <el-button
                                    @click.native.prevent="editRow(scope.$index, formData.values)"
                                    type="text"
                                    size="small">
                                Изменить
                            </el-button>
                            <el-button
                                    @click.native.prevent="deleteRow(scope.$index, formData.values)"
                                    type="text"
                                    size="small">
                                Удалить
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
                -->
            </el-form>
        </div>
       <!--
        <el-dialog title="Ввод данных по одной заявке" :visible.sync="dialogFormVisible">
            <el-form :model="form" label-width="150px">
                <el-form-item label="Статья бюджета">
                    <el-select v-model="form.budget" placeholder="Выберите статью бюджета">
                        <el-option v-for="item in dict_budget" :label="item.name" :value="item.code"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="Вид">
                    <el-select v-model="form.store_type" placeholder="Выберите тип">
                        <el-option v-for="item in dict_fin_store_type" :label="item.name"
                                   :value="item.code"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="Назначение">
                    <el-input v-model="form.note" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="Сумма">
                    <el-input-number v-model="form.sum" auto-complete="off"></el-input-number>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
    <el-button @click="dialogFormVisible = false">Отмена</el-button>
    <el-button type="primary" @click="saveDialogForm">Сохранить</el-button>
  </span>
        </el-dialog>
-->
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
        var componentName = 'components-page-views-request-report';
        Vue.component(componentName, {
            template: '#' + componentName,
            data: function () {
                return {
                    isNewData: false,
                    dialogFormVisible: false,
                    form: {
                        budget: "",
                        store_type: "",
                        note: "",
                        sum: 0,
                    },
                    text: 'Заявка',
                    formData: {
                        date: "",
                        values: []
                    },
                    dict_budget: [
                        {
                            code: 1000,
                            name: "Аренда",
                        },
                        {
                            code: "2000",
                            name: "Содержание объектов"
                        }
                    ],
                    dict_fin_store_type: [
                        {
                            code: 1,
                            name: "Наличные"
                        }, {
                            code: 2,
                            name: "Безналичные"
                        }
                    ]
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
                addData: function () {
                   /* this.form = {};
                    this.isNewData = true;
                    this.dialogFormVisible = true;
                    */
                   var newRow = {
                       store_type: "",
                       budget: "",
                       note: "",
                       sum: 0,
                   }
                   this.formData.values.push(newRow);
                },
                deleteRow: function(rowId) {
                    console.log("data", this.formData.values)
                    this.formData.values.splice(rowId, 1)
                },
                loadBuget: function () {

                },
                saveDialogForm: function () {
                    if (this.isNewData) {
                        var nf = {};
                        var that = this;
                        for (var prop in that.form) {
                            if (that.form.hasOwnProperty(prop)) {
                                nf[prop] = that.form[prop];
                            }
                        }
                        this.formData.values.push(nf);
                    }
                    this.dialogFormVisible = false;
                },

                editRow(index, rows) {
                    this.isNewData = false;
                    this.form = rows[index];
                    this.dialogFormVisible = true;
                }
            }
        });
    })(jQuery);
</script>
<!-- components/page-views/request-report.vue :: end -->