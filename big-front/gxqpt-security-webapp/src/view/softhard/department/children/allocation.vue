<!-- 分配资源 -->
<!-- 暂时无法想象如何开发，等待UI -->
<template>
    <Content>
        <Form v-for="(item,idx) in applyData" :key="idx" :label-width="80" style="margin:80px 0;">
            <Row>
                <Col span="5">
                    <FormItem label="CPU核数">
                        <Input :value="item.cpuCount+'核'" disabled></Input>
                    </FormItem>
                </Col>
                <Col span="5">
                    <FormItem label="内存">
                        <Input :value="item.memorySize+'GB'" disabled></Input>
                    </FormItem>
                </Col>
                <Col span="5">
                    <FormItem label="所属网络">
                        <Input :value="item.netScopeName" disabled></Input>
                    </FormItem>
                </Col>
                <Col span="5">
                    <FormItem label="网络带宽">
                        <div class="ivu-input ivu-input-disabled" style="background:#f3f3f3;color: #797979;cursor: not-allowed;">{{ item.netWide }}M</div>
                        <!-- <Input :value="item.netWide" disabled>{{ item.netWide }}M</Input> -->
                    </FormItem>
                </Col>
            </Row>
            <Row>
                <Col span="5">
                    <FormItem label="操作系统">
                        <Input :value="item.systemName" disabled></Input>
                    </FormItem>
                </Col>
                <Col span="5">
                    <FormItem label="系统盘">
                        <Input :value="item.sysDisksize+'GB'" disabled></Input>
                    </FormItem>
                </Col>
                <Col span="5" v-if="disk.diskName!=''" v-for="(disk,idx2) in item.disks" :key="idx2">
                    <FormItem :label="disk.diskName">
                        <Input :value="disk.diskSize+'GB'" disabled></Input>
                    </FormItem>
                </Col>
                <Col span="5">
                    <FormItem label="数量">
                        <Input :value="item.applyCount" disabled></Input>
                    </FormItem>
                </Col>
            </Row>
            <!-- <Button type="primary" @click="tableCreate(item)">分配</Button> -->
            <Table class="tableList" :columns="tableList.columns" :data="item.data" disabled-hover></Table>
        </Form>
        <Row class="bottom-bar" style="text-align: center">
            <Col>
                <Button type="default" @click="quit(-2)">取消</Button>
                <Button type="primary" @click="goCreat">上一步</Button>
                <Button type="primary" @click="submit">提交</Button>
            </Col>
        </Row>
    </Content>
</template>

<script>
import api from '@/api/axiosApi'
import softhardApiList from '@/api/softhardApiList'

function getConfigInfo() {
  return {
    serverId:'',
    ip:'',
    disksID:[],
    disksMount:[],
    diskProperty:[],
  }
}
export default {
    data(){
        return{
            applyData:[],//获取传递的数据如内存、CPU等
            tableData:[],//表格数据
            subData:{
                dealServer:[],
            },//提交数据整理
            parentId:0,//父级index值
            tableIdx:0,//父级table的index值
            tableShow:false,//table显示判断
            disksID:{id:''},//磁盘id默认模块
            disksMount:{mount:''},//挂载点默认模块
            diskProperty:{property:'数据盘'},//磁盘属性默认模块
            disksIDColumns:[
                {
                    title:'磁盘ID',
                    key:'id',
                    render: (h, params) => {
                        return h('Input', {
                            class:'server-list',
                            props: {
                                clearable:true,
                            },
                            on: {
                                input: (value) => {

                                    let item = this.subData.dealServer;
                                    for(var i = 0;i<item.length;i++){
                                        this.dealServerApp(item[i],params);
                                        if(item[i].parentId == params.row.parentId){//判断有相同键值时候
                                            item[i].disks[params.index].diskId = value;
                                            break;
                                        }else if(!item[i].parentId){//判断没有键值时候注入数据
                                            item[i].parentId = params.row.parentId;
                                            item[i].disks[params.index].diskId = value;
                                            break;
                                        }else{//有键值但键值不同
                                        }
                                    }
                                }
                            }
                        })
                    }
                }
            ],
            disksMountColumns:[
                {
                    title:'挂载点',
                    key:'mount',
                    render: (h, params) => {
                        return h('Input', {
                            class:'server-list',
                            props: {
                                clearable:true,
                            },
                            on: {
                                input: (value) => {
                                    let item = this.subData.dealServer;
                                    for(var i = 0;i<item.length;i++){
                                        this.dealServerApp(item[i],params);
                                        if(item[i].parentId == params.row.parentId){//判断有相同键值时候
                                            item[i].disks[params.index].diskPath = value;
                                            break;
                                        }else if(!item[i].parentId){//判断没有键值时候注入数据
                                            item[i].parentId = params.row.parentId;
                                            item[i].disks[params.index].diskPath = value;
                                            break;
                                        }else{//有键值但键值不同
                                        }
                                    }
                                }
                            }
                        })
                    }
                }
            ],
            diskPropertyColumns:[
                {
                    title:'磁盘属性',
                    key:'property',
                    align: 'center',
                    render:(h,params) => {
                        const idx = params.index;
                        const property = params.row.property;//默认第一个为系统盘
                        return h('span', {
                            domProps: {
                                innerHTML: property
                            },
                        })
                    }
                    // render: (h, params) => {
                    //     return h('Input', {
                    //         props:{
                    //             value:'',
                    //             label:'',
                    //             disabled:true
                    //         },
                    //         on: {
                    //             'on-keyup':(event) => {
                    //                 console.log(params);
                    //                 console.log(event);
                    //                 this.dealServerApp(this.subData,params);
                    //                 this.subData.dealServer[this.tableIdx].disks[params.index].diskType = event;
                    //             }
                    //         }

                    //     },
                    //     [
                    //         h('Option',{
                    //             props: {
                    //                 value: '1'
                    //             }
                    //         },'数据盘'),
                    //         h('Option',{
                    //             props: {
                    //                 value: '2'
                    //             }
                    //         },'系统盘')
                    //     ])
                    // }
                }
            ],
            tableList: {
                columns: [
                    {
                        type: 'index',
                        title: '序号',
                        width: 80,
                        align: 'center'
                    },
                    {
                        title: '服务器ID',
                        key: 'serverId',
                        renderHeader: (h,params)=>{
                            return h('div',[
                                h('span',{
                                    style: {
                                        marginRight: '3px',
                                        color:'red'
                                    }
                                },'*'),
                                h('span','服务器ID')
                            ])
                        },
                        render: (h, params) => {
                            return h('Input', {
                                class:'server-list',
                                props: {
                                    clearable:true,
                                },
                                on: {
                                    input: (value) => {
                                        let item = this.subData.dealServer;
                                        for(var i = 0;i<item.length;i++){
                                            if(item[i].parentId == params.row.parentId){//判断有相同键值时候
                                                item[i].serverId = value;
                                                break;
                                            }else if(!item[i].parentId){//判断没有键值时候注入数据
                                                item[i].parentId = params.row.parentId;
                                                item[i].serverId = value;
                                                break;
                                            }else{//有键值但键值不同
                                            }
                                        }
                                    }
                                }
                            })
                        }
                    },
                    {
                        title: '服务器ip',
                        key: 'ip',
                        renderHeader: (h,params)=>{
                            return h('div',[
                                h('span',{
                                    style: {
                                        marginRight: '3px',
                                        color:'red'
                                    }
                                },'*'),
                                h('span','服务器ip')
                            ])
                        },
                        render: (h, params) => {
                            return h('Input', {
                                class:'server-list',
                                props: {
                                    clearable:true,
                                },
                                on: {
                                    input: (value) => {
                                        let item = this.subData.dealServer;
                                        for(var i = 0;i<item.length;i++){
                                            if(item[i].parentId == params.row.parentId){//判断有相同键值时候
                                                item[i].ip = value;
                                                break;
                                            }else if(!item[i].parentId){//判断没有键值时候注入数据
                                                item[i].parentId = params.row.parentId;
                                                item[i].ip = value;
                                                break;
                                            }else{//有键值但键值不同
                                            }
                                        }
                                    }
                                }
                            })
                        }
                    },
                    {
                        title: '磁盘ID',
                        key: 'disksID',
                        align: 'center',
                        renderHeader: (h,params)=>{
                            return h('div',[
                                h('span',{
                                    style: {
                                        marginRight: '3px',
                                        color:'red'
                                    }
                                },'*'),
                                h('span','磁盘ID')
                            ])
                        },
                        render: (h, params) => {
                            return h('Table', {
                                class:'server-list',
                                props: {
                                    columns: this.disksIDColumns,
                                    data:params.row.disksID,
                                    'show-header':false
                                },
                                on: {
                                    'on-row-click': (value) => {
                                        console.log(params);
                                        this.parentId = params.row.parentId;
                                        this.tableIdx = params.index;//获取父表格的index
                                    }
                                }
                            })
                        }
                    },
                    {
                        title: '挂载点',
                        key: 'disksMount',
                        align: 'center',
                        render: (h, params) => {
                        return h('Table', {
                            class:'server-list',
                            props: {
                                columns: this.disksMountColumns,
                                data:params.row.disksMount,
                                'show-header':false
                            },
                            on: {
                                'on-row-click': (value) => {
                                    this.parentId = params.row.parentId;
                                    this.tableIdx = params.index;//获取父表格的index
                                }
                            }
                        })
                        }
                    },
                    {
                        title: '磁盘属性',
                        key: 'diskProperty',
                        align: 'center',
                        render: (h, params) => {
                        return h('Table', {
                            class:'server-list',
                            props: {
                                columns: this.diskPropertyColumns,
                                data:params.row.diskProperty,
                                'show-header':false
                            },
                            on: {
                                'on-row-click': (value) => {
                                    this.parentId = params.row.parentId;
                                    this.tableIdx = params.index;//获取父表格的index
                                }
                            }
                        })
                        }
                    },
                ],
                // data: []
            },
        }
    },
    mounted(){
        if(this.$route.params.table){
            this.applyData = this.$route.params.table;
            let len = 0;
            this.applyData.forEach((item,index)=>{
                item.data = this.tableCreate(item,index);
                len += item.applyCount;
            });
            console.log(len);
            for(var i = 0;i<len;i++){
                this.subData.dealServer.push({applyKeyid:this.$route.params.departData.id});
            }
        }
        console.log(this.subData);
        console.log(this.$route.params.table);
        console.log(this.applyData);
    },
    methods:{
        quit (n) {// 取消
            this.$router.go(n)
        },
        goCreat(){
            this.$router.push({//审批申请服务器
                name: 'applyCreate',
                params: {id: 5,departData:this.$route.params.departData}
            })
        },
        tableCreate(item,index){//表格整理然后显示；
            let tableData = [];//表格数据初始化
            for(var i = 0;i<item.applyCount;i++){
                tableData.push(getConfigInfo());
                tableData[i].parentId = index + '-' + i;
                for(var j =0;j<=item.disks.length;j++){
                    tableData[i].disksID.push({id:'',parentId:index + '-' + i});//根据数量遍历磁盘ID
                    tableData[i].disksMount.push({mount:'',parentId: index + '-' + i});//根据数量遍历挂载点
                    tableData[i].diskProperty[j] = { property:''};//根据数量遍历并且初始化磁盘属性
                    if(j==0){
                        tableData[i].diskProperty[j].property = '系统盘('+item.sysDisksize+'GB)';
                    }else{
                        tableData[i].diskProperty[j].property = item.disks[j-1].diskName + '(' + item.disks[j-1].diskSize + 'GB)';
                    }
                }
            }
            return tableData
            // this.tableList.data = this.tableData;
            // this.tableShow = true;
        },
        submit(){//提交
            for(var i = 0;i<this.subData.dealServer.length;i++){
                console.log(this.subData.dealServer[i].parentId);
                if(!this.subData.dealServer[i].parentId){
                    this.$Message.error('请填写完成配置后再提交！');
                    return
                }
            }
            if(this.dataDeal(this.subData)){
                console.log(this.subData);
                api(softhardApiList.uYApproveSubmit,this.subData).then((res) => {
                    if(res.data.errcode == 0){
                        this.$Message.success('提交成功');
                        this.$router.push({
                            name: 'applyMgr',
                        })
                    }else{
                        this.$Message.error(res.data.errmsg);
                    }
                }, (err) => {
                    //dong something...
                })
            }else{
                this.$Message.error("请填写完整后提交！！");
            }
        },
        dataDeal(data){
            console.log(data);
            data.stepCode = 'YOUYI_HANDLER';//this.$route.params.departData.stepCode;假数据
            data.applyKeyid = this.$route.params.departData.id;
            for(var index = 0;index<data.dealServer.length;index++){
                let item = data.dealServer[index];
                let j = parseInt(item.parentId.substring(0,1));
                let len = this.applyData[j].data[index].diskProperty.length;//获取长度
                console.log(this.applyData[j]);
                if(item.disks.length != len){//判断长度是否正确
                    return false;
                }else{
                    for(var idx = 0;idx<item.disks.length;idx++){
                        let items = item.disks[idx];
                        if(!items.diskId || items.diskId == ''){//判断id是否有值
                            return false;
                        }
                        if(idx==0){
                            items.diskName = '系统盘';
                            items.diskSize = this.applyData[j].sysDisksize;
                            items.diskType = '2'
                        }else{
                            items.diskName = this.applyData[j].disks[idx-1].diskName;
                            items.diskSize = this.applyData[j].disks[idx-1].diskSize;
                            items.diskType = '1';
                        }
                    }
                }
                item.disks.forEach((items,idx) =>{
                });
                item.cpuCount = this.applyData[j].cpuCount;
                item.applyCount = this.applyData[j].applyCount;
                item.memorySize = this.applyData[j].memorySize;
                item.netScope = this.applyData[j].netScope;
                item.netWide = this.applyData[j].netWide;
                // if(this.applyData[j].netWide.indexOf("M") != -1){
                //     item.netWide = this.applyData[j].netWide.substring(0,this.applyData[j].netWide.length-1);
                // }
                item.system = this.applyData[j].system;
                item.applyKeyid = this.$route.params.departData.id;
            }
            return true
        },
        dealServerApp(data,params){
            if(!data.disks){
                data.disks =[];
            }
            if(!data.disks[params.index]){
                data.disks[params.index] = {}
            }
        }
    }
}
</script>

<style lang="less" scoped>
.server-list /deep/ thead{
    display: none;
}
.tableList{
    margin-top:30px;
}
.tableList input{
    border: 0px;outline:none;
}
.tableList /deep/.ivu-table-cell{
    overflow: inherit;
}
.tableList /deep/.ivu-table{
    overflow: inherit;
}

</style>
