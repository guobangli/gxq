<template>
  <Layout>
    <Content>
      <Breadcrumb>
        <!-- <BreadcrumbItem>管理员(大数据办)</BreadcrumbItem> -->
        <BreadcrumbItem>{{title}}</BreadcrumbItem>
      </Breadcrumb>
      <Card>
        <Form
          ref="formValidate"
          inline
          :label-width="100"
          :model="searchCondition">
          <FormItem label="申请单位：">
            <Input
              v-model="searchCondition.applyOrgname"
              type="text">
            </Input>
          </FormItem>
          <FormItem label="申请人：">
            <Input
              v-model="searchCondition.applyUname"
              type="text">
            </Input>
          </FormItem>
          <FormItem label="申请类别：">
            <Select clearable v-model="searchCondition.applyType" style="width:140px">
              <Option v-for="item in categoryList" :value="item.value" :key="item.value">{{item.label}}</Option>
            </Select>
          </FormItem>
          <FormItem label="申请时间：">
            <DatePicker
              format="yyyy-MM-dd"
              :editable="false"
              type="daterange"
              style="width: 200px"
              @on-change="changeTime">
            </DatePicker>
          </FormItem>
          <FormItem :label-width="20">
            <Button type="primary" @click="handleSubmit">查询</Button>
            <Button type="primary" @click="ref">刷新</Button>
          </FormItem>
        </Form>
        <hy-table
          highlight-row
          :columns="tableList.columns"
          :data="tableList.data"
          :total="pageOption.total"
          :pageSize="pageOption.pageSize"
          :current="pageOption.current"
          pageType="small"
          show-elevator
          show-sizer
          class="department-apply"
          @on-change="pageChange"
          @on-page-size-change="changePageSize" />
      </Card>
    </Content>
  </Layout>
</template>

<script>
import api from '@/api/axiosApi'
import softhardApiList from '@/api/softhardApiList'
import { mapState } from 'vuex'
export default {
  data () {
    return {
      title:this.$store.state.title,
      searchCondition: {
        applyOrgname: '',
        applyUname: '',
        applyType: '',
        applyStartTime: '',
        applyEneTime: '',
        // scode: 'GL',
      },
      pageOption: { //分页参数
        current: 1,
        total: 0,
        pageSize: 10
      },
      tableList: {
        columns: [
          {
            type: 'index',
            title: '序号',
            width: 60,
            align: 'center'
          },
          {
            title: '申请单号',
            key: 'applyNo'
          },
          {
            title: '申请单位',
            key: 'applyOrgname'
          },
          {
            title: '申请人',
            key: 'applyUname'
          },
          {
            title: '申请类别',
            key: 'applyType',
            render(h, params) {
							let text;
							(params.row.applyType=='1') ? (text = '新增') : (text = '变更');
							return h('div', [
								h('span', {}, text)
							])
						}
          },
          {
            title: '申请时间',
            width: 220,
            key: 'applyTime'
          },
          {
            title: '申请状态',
            key: 'sname'
          },
          {
            title: '处理环节',
            key: 'stepName'
          },
          {
            title: '操作',
            key: 'address',
            width: 230,
            render: (h, params) => {
              const vm = this
              const handleType = params.row.handleType
              const id = params.row.age
              const deal = h('Button', {
                props: {
                  type: 'primary',
                  size: 'small'
                },
                domProps: {
                  innerHTML: '审核'
                },
                style: {
									display:this.checkButton('hardware_admin_apply_audit')?'inline-block':'none'
								},
                on: {
                  click () {
                    vm.gotoCtrl(0,params);
                  }
                }
              });
              const audit = h('Button', {
                props: {
                  type: 'primary',
                  size: 'small'
                },
                domProps: {
                  innerHTML: '配置'
                },
                style: {
									display:this.checkButton('hardware_gxyy_apply_manage_audit')?'inline-block':'none'
								},
                on: {
                  click () {
                    vm.gotoCtrl(2,params);
                  }
                }
              });
              const detail = h('Button', {
                props: {
                  type: 'primary',
                  size: 'small'
                },
                domProps: {
                  innerHTML: '详情'
                },
                style: {
									display:this.checkButton('hardware_admin_apply_detail')?'inline-block':'none'
								},
                on: {
                  click () {
                    vm.gotoCtrl(1,params)
                  }
                }
              });
              const check = h('Button', {
								props: {
									type: 'primary',
									size: 'small'
								},
								style: {
									display:this.checkButton('hardware_gxyy_sqgl_cl')?'inline-block':'none'
								},
								on: {
									click: () => {
										let keyid = params.row.id;
										vm.$router.push({
											path: '/engineDeal/' + keyid
										});
									}
								}
							}, '处理');
							const create = h('Button', {
								props: {
									type: 'primary',
									size: 'small'
								},
								style: {
									display:this.checkButton('hardware_gxyy_sqgl_zjzj')?'inline-block':'none'
								},
								on: {
									click: () => {
										let keyid = params.row.id;
										vm.$router.push({
											path: '/createEngine/' + keyid
										});
									}
								}
							}, '新增主机');
              switch (handleType) {
                case "2"://判断状态显示
                  if(params.row.stepCode=="YIYUN_ADD_HOST"){
										return h('div', [create,deal,audit,detail]);
									}else{
										return h('div', [check,deal,audit,detail])
									};
                  // return h('div', [deal,audit,detail])
                default:
                  return h('div', [detail])
              }
            }
          }
        ],
        data: []
      },
      categoryList: [{
        value: 1,
        label: '新增'
      }, {
        value: 2,
        label: '变更'
      }],
      typeList: [{
        value: 2,
        label: '类型1'
      }],
      providerList: [{
        value: 3,
        label: '提供方1'
      }]
    }
  },
  mounted(){
    this.getApprApplyList();
  },
  methods: {
    // 点击查询按钮
    handleSubmit () {
      this.pageOption.current=1;
      this.getApprApplyList();
    },
    ref(){
      this.pageOptio = { //分页参数
        current: 1,
        total: 0,
        pageSize: 10
      };
      this.getApprApplyList();
    },
    changeTime(val) { //申请时间选择
      if(val[0] && val[1]) {
        var startTime = val[0].replace(/\//g, "-");
        var endTime = val[1].replace(/\//g, "-");
        this.searchCondition.applyStartTime = startTime;
        this.searchCondition.applyEneTime = endTime;
      } else {
        this.searchCondition.applyStartTime = "";
        this.searchCondition.applyEneTime = "";
      }
    },
    pageChange(num) { //页码改变的回调
      this.pageOption.current = num;
      this.getApprApplyList();
    },
    changePageSize(num) { //切换每页条数时的回调
      this.pageOption.pageSize = num;
      this.getApprApplyList();
    },
    getApprApplyList() { // 查询服务分配管理分页
      this.data = [];
      this.showFlow = false;
      console.log(this.searchCondition);
      let searchString = (JSON.stringify(this.searchCondition)).replace(/\ +/g, "");
      let searchJson = JSON.parse(searchString);
      var formData = {
        "data": searchJson, //Object.assign({}, this.formQueryData),
        "pageNo": this.pageOption.current,
        "pageSize": this.pageOption.pageSize
      };
      api(softhardApiList.getApprApplyList, formData).then((res) => {
        if(res.data.errcode == 0){
          this.tableList.data = res.data.data.list;
          if(this.tableList.data.length>0){
            this.pageOption.pageSize = res.data.data.pageSize;
            this.pageOption.total = res.data.data.total;
            this.pageOption.current = res.data.data.pageNum;
            this.tableList.data.forEach(item=>{
								item.applyTime = item.applyTime.replace("-","年");
                item.applyTime = item.applyTime.replace("-","月");
                item.applyTime = item.applyTime.replace(" ","日 ");
            });
            console.log(this.pageOption);
          };
        }else{
          this.$Message.error(res.data.errmsg);
        }
      }, (err) => {
        this.$Message.error(err.data.errmsg);
        //dong something...
      })
    },
    deleteDraft(data) { // 删除
      console.log(data);
      api(softhardApiList.deleteDraft,{id:data.id}).then((res) => {
        if(res.data.errcode == 0){
          this.getMyApplyList();
          console.log(res);
        }else{
          this.$Message.error(res.data.errmsg);
        }
      }, (err) => {
        //dong something...
      })
    },
    // 操作栏点击
    gotoCtrl (idx,data) {
      switch (idx) {
        case 0:
          this.$router.push({//审批申请服务器
            name: 'adminCreate',
            params: {id: 4,departData:data.row}
          })
          break
        case 1://查看详情
          console.log(data);
          this.$router.push({
            name: 'adminDetail',
            params:{id: 6,departData:data.row}
          })
          break
        case 2:
          this.$router.push({//处理申请服务器
            name: 'applyDetail',
            params: {id: 5,departData:data.row}
          })
          break
      }
    },
    checkButton(code){
      if(this.authButton.indexOf(code) >= 0){
        return true;
      }else{
        return false;
      }
    }
  },
  computed: {
    ...mapState([
    'authButton'
    ])
  },
}
</script>

<style lang='less' scoped>
.department-apply{
  span.handle{
    margin: 0 5px;
    display: inline-block;
    cursor: pointer;
    &:hover{
      color: #57a3f3;
    }
  }
}
</style>
