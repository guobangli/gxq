<!-- 监控详情 -->
<template>
  <Layout>
    <Content>
      <Breadcrumb>
        <BreadcrumbItem>服务器ID：{{serverId}}</BreadcrumbItem>
      </Breadcrumb>
      <Card>
        <Row>
          <Button type="default" @click="quit" style="float: right;">返回</Button>
        </Row>
        <Row type="flex" justify="space-between">
          <Col style="width: 100%">
            <chart-card title="CPU监控信息">
              <Row style="margin-top: 15px;text-align: right;">
                <Col span="24">
                  <RadioGroup v-model="cpuTime" type="button" @on-change='findMonitorDetail(id,cpuTime,"1")'>
                    <Radio :label="thirtyMinutes">
                      <span>30分钟</span>
                    </Radio>
                    <Radio :label="sixHours">
                      <span>6小时</span>
                    </Radio>
                    <Radio :label="oneDays">
                      <span>1天</span>
                    </Radio>
                  </RadioGroup>
                </Col>
              </Row>
              <chartLine ref="cpuMtr" el="cpuMtr" :lineOption="cpuMtr">
                <div id="cpuMtr" style="height: 400px;"></div>
              </chartLine>
            </chart-card>
            <div style="padding:0 6rem"><span style="color:#16D9F0">cpu百分比（%）</span>  最大值：{{cpuData.max}}%  最小值：{{cpuData.min}}%  平均值：{{cpuData.avg}}%</div>
          </Col>
          <Col style="width: 100%">
            <chart-card title="内存使用率监控信息">
              <Row style="margin-top: 15px;text-align: right;">
                <Col span="24">
                  <RadioGroup v-model="memoryTime" type="button" @on-change='findMonitorDetail(id,memoryTime,"2")'>
                    <Radio :label="thirtyMinutes">
                      <span>30分钟</span>
                    </Radio>
                    <Radio :label="sixHours">
                      <span>6小时</span>
                    </Radio>
                    <Radio :label="oneDays">
                      <span>1天</span>
                    </Radio>
                  </RadioGroup>
                </Col>
              </Row>
              <chartLine ref="memoryMtr" el="memoryMtr" :lineOption="memoryMtr">
                <div id="memoryMtr" style="height: 400px;"></div>
              </chartLine>
            </chart-card>
            <div style="padding:0 6rem"><span style="color:#16D9F0">内存使用率（%）</span>  最大值：{{memoryData.max}}%  最小值：{{memoryData.min}}%  平均值：{{memoryData.avg}}%</div>
          </Col>
          <Col style="width: 100%">
            <chart-card title="磁盘使用率监控信息">
              <Row style="margin-top: 15px;text-align: right;">
                <Col span="24">
                  <RadioGroup v-model="disksTime" type="button" @on-change='findMonitorDetail(id,disksTime,"3")'>
                    <Radio :label="thirtyMinutes">
                      <span>30分钟</span>
                    </Radio>
                    <Radio :label="sixHours">
                      <span>6小时</span>
                    </Radio>
                    <Radio :label="oneDays">
                      <span>1天</span>
                    </Radio>
                  </RadioGroup>
                </Col>
              </Row>
              <chartLine ref="disksMtr" el="disksMtr" :lineOption="disksMtr">
                <div id="disksMtr" style="height: 400px;"></div>
              </chartLine>
            </chart-card>
            <div style="padding:0 6rem"><span style="color:#16D9F0">磁盘使用率（%）</span>  最大值：{{disksData.max}}%  最小值：{{disksData.min}}%  平均值：{{disksData.avg}}%</div>
          </Col>
          <Col style="width: 100%">
            <chart-card title="流量监控">
              <Row style="margin-top: 15px;text-align: right;">
                <Col span="24">
                  <RadioGroup v-model="trafficTime" type="button" @on-change='findMonitorDetail(id,trafficTime,"4")'>
                    <Radio :label="thirtyMinutes">
                      <span>30分钟</span>
                    </Radio>
                    <Radio :label="sixHours">
                      <span>6小时</span>
                    </Radio>
                    <Radio :label="oneDays">
                      <span>1天</span>
                    </Radio>
                  </RadioGroup>
                </Col>
              </Row>
              <chartLine ref="trafficMtr" el="trafficMtr" :lineOption="trafficMtr">
                <div id="trafficMtr" style="height: 400px;"></div>
              </chartLine>
            </chart-card>
            <div style="padding:0 6rem"><span style="color:#16D9F0">流量使用量（MB）</span>  最大值：{{trafficData.max}}MB  最小值：{{trafficData.min}}MB  平均值：{{trafficData.avg}}MB</div>
          </Col>
        </Row>
      </Card>
    </Content>
  </Layout>
</template>

<script>
import chartLine from './common/ChartLine.vue'
// echart图的一些共用的配置
import {areaLine} from '@/assets/js/echartOption'
import api from '@/api/axiosApi'
import softhardApiList from '@/api/softhardApiList'
export default {
  components: {
    chartLine
  },
  data () {
    return {
      cpuTime: '',
      memoryTime: '',
      disksTime: '',
      trafficTime:'',
      thirtyMinutes:new Date(new Date().getTime() - 0.5 * 60 * 60 * 1000),//三十分钟时间
      sixHours:new Date(new Date().getTime() - 6 * 60 * 60 * 1000),//6小时内
      oneDays:new Date(new Date().getTime() - 24 * 60 * 60 * 1000),//24小时内
      id:this.$route.params.id,
      serverId:this.$route.params.serverId,
      cpuMtr: areaLine(),
      cpuData:{
        max:0,
        min:0,
        avg:0,
      },
      memoryMtr: areaLine(),
      memoryData:{
        max:0,
        min:0,
        avg:0,
      },
      disksMtr: areaLine(),
      disksData:{
        max:0,
        min:0,
        avg:0,
      },
      trafficMtr: areaLine(),
      trafficData:{
        max:0,
        min:0,
        avg:0,
      },
    }
  },
  mounted(){
    //初始化所有图标类型为30分钟
    this.cpuTime = this.thirtyMinutes;
    this.memoryTime = this.thirtyMinutes;
    this.disksTime = this.thirtyMinutes;
    this.trafficTime = this.thirtyMinutes;
    this.init(this.id,this.thirtyMinutes);
  },
  methods: {
    init(id,time){//初始化方法
      this.findMonitorDetail(id,time,'1');
      this.findMonitorDetail(id,time,'2');
      this.findMonitorDetail(id,time,'3');
      this.findMonitorDetail(id,time,'4');
      this.findMonitorDetail(id,time,'5');
    },
    // 返回
    quit () {
      this.$router.go(-1)
    },
    findMonitorDetail(id,time,type) { // 查询
      this.data = [];
      var formData = {
        "id": id,
        // "id":595929887,
        "time": this.crtTimeFtt(time),
        // "time": "2018-09-06 10:45:00",
        "type":type
      };
      api(softhardApiList.findMonitorDetail, formData).then((res) => {
        if(res.status == 200 && res.data.data) {
          let arr = [];
          switch (type) {
            case '1':
              this.getCpuMtr(res.data.data.time,res.data.data.val);
              if(res.data.data.val.length!=0){
                this.cpuData.max = Math.max.apply(null,res.data.data.val);
                this.cpuData.min = Math.min.apply(null,res.data.data.val);
                this.cpuData.avg = this.avgNum(res.data.data.val).toFixed(2);
              }
              break;
            case '2':
              this.getMemoryMtr(res.data.data.time,res.data.data.val);
              if(res.data.data.val.length!=0){
                this.memoryData.max = Math.max.apply(null,res.data.data.val);
                this.memoryData.min = Math.min.apply(null,res.data.data.val);
                this.memoryData.avg = this.avgNum(res.data.data.val).toFixed(2);
              }
              break;
            case '3':
              this.getDisksMtr(res.data.data.time,res.data.data.val);
              if(res.data.data.val.length!=0){
                this.disksData.max = Math.max.apply(null,res.data.data.val);
                this.disksData.min = Math.min.apply(null,res.data.data.val);
                this.disksData.avg = this.avgNum(res.data.data.val).toFixed(2);
              }
              break;
            case '4':
              this.getTrafficMtr(res.data.data.time,res.data.data.val);
              if(res.data.data.val.length!=0){
                this.trafficData.max = (Math.max.apply(null,res.data.data.val)/1024).toFixed(2);
                this.trafficData.min = (Math.min.apply(null,res.data.data.val)/1024).toFixed(2);
                this.trafficData.avg = (this.avgNum(res.data.data.val)/1024).toFixed(2);
              }
              break;
            // case '5':
            //   this.getCpuMtr(res.data.data.time,res.data.data.val);
            //   break;
            default:
              break;
          }
          console.log(res);
        }
      }, (err) => {
        //dong something...
      })
    },
    getCpuMtr (xTime,dataVal) {
      this.cpuMtr.xAxis.data = xTime;
      this.cpuMtr.series[0].data = dataVal;
      this.cpuMtr.series[0].itemStyle.normal.color = '#16D9F0';
      // this.cpuMtr.series[0].itemStyle.normal.label.formatter = '{b}\n{c}%';
      this.cpuMtr.series[0].areaStyle.normal.color = '#8cf1fd';
      this.$refs.cpuMtr.refresh()
    },
    getMemoryMtr (xTime,dataVal) {
      this.memoryMtr.xAxis.data = xTime
      this.memoryMtr.series[0].data = dataVal
      this.memoryMtr.series[0].itemStyle.normal.color = '#16D9F0';
      this.memoryMtr.series[0].areaStyle.normal.color = '#8cf1fd'
      this.$refs.memoryMtr.refresh()
    },
    getDisksMtr (xTime,dataVal) {
      this.disksMtr.xAxis.data = xTime
      this.disksMtr.series[0].data = dataVal
      this.disksMtr.series[0].itemStyle.normal.color = '#16D9F0';
      this.disksMtr.series[0].areaStyle.normal.color = '#8cf1fd'
      this.$refs.disksMtr.refresh()
    },
    getTrafficMtr (xTime,dataVal) {
      this.trafficMtr.xAxis.data = xTime
      this.trafficMtr.series[0].data = dataVal
      this.trafficMtr.series[0].itemStyle.normal.color = '#16D9F0';
      this.trafficMtr.series[0].areaStyle.normal.color = '#8cf1fd'
      this.$refs.trafficMtr.refresh()
    },
    avgNum(arr){
      let num = 0, avg = 0,index = 0;
      arr.forEach((item,idx)=>{
        index = idx;
        num += item;
      });
      return avg = num/(index+1);
    },
    crtTimeFtt(value,row,index){
        var crtTime = new Date(value);
        return this.dateFtt("yyyy-MM-dd hh:mm:ss",crtTime);//直接调用公共JS里面的时间类处理的办法     
    },
    dateFtt(fmt,date) { //author: meizz   
      var o = {   
        "M+" : date.getMonth()+1,                 //月份   
        "d+" : date.getDate(),                    //日   
        "h+" : date.getHours(),                   //小时   
        "m+" : date.getMinutes(),                 //分   
        "s+" : date.getSeconds(),                 //秒   
        "q+" : Math.floor((date.getMonth()+3)/3), //季度   
        "S"  : date.getMilliseconds()             //毫秒   
      };   
      if(/(y+)/.test(fmt))   
        fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length));   
      for(var k in o)   
        if(new RegExp("("+ k +")").test(fmt))   
      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
      return fmt;   
    },
  }
}
</script>
