<template id="filials_template">
    <diV>
        <el-button type="primary" @click="addNew">Добавить</el-button>
        <el-table
                :data="filials"
                height="250"
                border
                style="width: 100%"
                highlight-current-row
                @row-dblclick="editData">
            <el-table-column
                    prop="id"
                    label="Номер"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="code"
                    label="Код"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="name"
                    label="Наименование"
                    width="200">
            </el-table-column>
        </el-table>
        <el-dialog title="Корректировка филиала" :visible.sync="dialogFormVisible">
            <el-form :model="form">
                <el-form-item label="Наименование">
                    <el-input v-model="form.name" auto-complete="off"></el-input>
                </el-form-item>
            </el-form>
  <span slot="footer" class="dialog-footer">
      <el-button @click="deleteData">Удалить</el-button>
    <el-button @click="cancelData">Отмена</el-button>
    <el-button type="primary" @click="saveData">Сохранить</el-button>
  </span>
        </el-dialog>
    </diV>
</template>


<script>

    Vue.component('filials', {
        template: '#filials_template',
        data: function () {
            return {
                filials: [],
                dialogFormVisible: false,
                form: {
                    id: 0,
                    code: "",
                    name: ""
                }
            }
        },
        props: []
        ,
        methods: {
            editData(row){
                console.log(row);
                this.form = row;
                this.dialogFormVisible = true;
            },
            addNew(){
                console.log("addNew")
                form = {
                    id: 0,
                    code: "",
                    name: "Новый"
                }
                this.dialogFormVisible = true;
            },
            saveData() {
                var dat = {
                    id: this.form.id,
                    name: this.form.name
                }
                this.$http.post("save-filial", dat).then(
                        function (response) {
                            this.loadFilials();
                            console.log("save data")
                        }, function (error) {
                            console.log("Error save data")
                        })

                this.dialogFormVisible = false;
            },
            cancelData(){
                console.log("cancel");
                this.dialogFormVisible = false;
            },
            deleteData(){
                console.log("delete");
                var dat = {
                    id: this.form.id,
                    name: this.form.name
                }
                this.$http.post("delete-filial", dat).then(
                        function (response) {
                            this.loadFilials();
                            console.log("delete data")
                        }, function (error) {
                            console.log("Error delete data")
                        })

                this.dialogFormVisible = false;
            },
            loadFilials() {
                this.$http.get("get-filials").then(
                        function (response) {
                            this.filials = JSON.parse(response.data);
                            console.log("laod filials")
                        }, function (error) {
                            console.log("Error load filials")
                        })
            },
        },
        beforeMount: function () {
            this.loadFilials();
        },
        mounted: function () {

        },
        components: {}

    });


</script>