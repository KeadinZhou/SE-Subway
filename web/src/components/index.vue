<template>
    <div id="main-box" class="center-box">
        <el-card class="box-card" shadow="hover">
            <div style="margin: 20px"></div>
            <img src="@/assets/logo.jpg" width="50px">
            <h1>北京地铁出行线路规划系统</h1>
            <div style="margin: 50px"></div>

            <template v-if="!resultPage">
                <transition name="el-fade-in">
                    <div>
                        <template v-if="!isAll">
                            <el-autocomplete
                                    v-model="st1"
                                    :fetch-suggestions="querySearch"
                                    placeholder="请输入起点站"
                                    :trigger-on-focus="false">
                            </el-autocomplete>
                            &nbsp;&nbsp;<b>至</b>&nbsp;&nbsp;
                            <el-autocomplete
                                    v-model="st2"
                                    :fetch-suggestions="querySearch"
                                    placeholder="请输入终点站"
                                    :trigger-on-focus="false">
                            </el-autocomplete>
                        </template>
                        <template v-else>
                            <el-select v-model="line" placeholder="请选择线路">
                                <el-option
                                        v-for="item in lines"
                                        :key="item"
                                        :label="item"
                                        :value="item">
                                </el-option>
                            </el-select>
                        </template>

                        <div style="margin: 50px"></div>
                        <template>
                            <el-radio v-model="isAll" :label="false">路径查询</el-radio>
                            <el-radio v-model="isAll" :label="true">线路查询</el-radio>
                        </template>

                        <div style="margin: 20px"></div>
                        <el-button @click="queryPath(isAll, isAll?line:[st1,st2])">立即查询</el-button>

                    </div>
                </transition>
            </template>
            <template v-else>
                <transition name="el-fade-in">
                    <div>
                        <h3>{{isAll ? line : st1+" 至 "+st2}} 途经站点</h3>
                        <el-table :data="tableData">
                            <el-table-column label=" " align="center">
                                <template slot-scope="scope">
                                    <b>{{ scope.row.name }}</b>&nbsp;
                                    <template v-if="scope.row.change">
                                        <i class="el-icon-refresh" style="font-size: 9px;color: gray">换乘{{scope.row.change}}</i>
                                    </template>
                                </template>
                            </el-table-column>
                        </el-table>
                        <div style="margin: 50px"></div>
                        <el-button @click="resultPage = false">重新查询</el-button>
                    </div>
                </transition>
            </template>

            <div style="margin: 20px"></div>
        </el-card>

        <div style="margin: 30px;font-size: 9px;color: gray">
            Copyright &copy; <a href="https://github.com/KeadinZhou/SE-Subway" style="color:#409EFF">31701030</a> All Rights Reserved.
        </div>
    </div>
</template>

<script>
    export default {
        name: "index",
        data () {
            return {
                lines: null,
                sites: null,
                st1: '',
                st2: '',
                resultPage: false,
                isAll: false,
                line: '',
                tableData: [{
                    'name': '天安门北',
                    'change': '1号线'
                }]
            }
        },
        methods: {
            querySearch(queryString, cb) {
                var sites = this.sites
                var results = queryString ? sites.filter(this.createFilter(queryString)) : sites
                var res = []
                for(var item of results){
                    res.push({value: item})
                }
                cb(res)
            },
            createFilter(queryString) {
                return (site) => {
                    return (site.toLowerCase().indexOf(queryString.toLowerCase()) !== -1)
                }
            },
            getData () {
                const that = this
                that.$http.get('http://127.0.0.1:5000/data')
                    .then(data => {
                        that.lines = data.data.lines
                        that.sites = data.data.sites
                    })
                    .catch(function (error) {
                        if (error.response) {
                            alert(error.response)
                        }
                    })
            },
            queryPath (isAll, data) {
                const that = this
                var sendData = {
                    isAll: isAll,
                    query: data
                }
                that.$http.post('http://127.0.0.1:5000/query', sendData)
                    .then(data => {
                        var res = data.data.res
                        console.log(res)
                        that.resultPage = true
                        that.isAll = isAll
                        that.tableData = []
                        for(var index in res){
                            if(index === "0") continue
                            console.log(typeof index)
                            if(that.lines.includes(res[index])){
                                that.tableData[that.tableData.length-1].change = res[index]
                            } else {
                                that.tableData.push({
                                    'name': res[index],
                                    'change': ''
                                })
                            }
                        }
                    })
                    .catch(function (error) {
                        if (error.response) {
                            alert(error.response)
                        }
                    })
            }
        },
        created () {
            this.getData()
        }
    }
</script>

<style scoped>
    #main-box {
        width: 600px;
        margin: 50px 0;
        text-align: center;
    }
</style>